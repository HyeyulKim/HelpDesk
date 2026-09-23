(function () {
    var bell = document.getElementById('notif-bell');
    var badge = document.getElementById('notif-badge');
    var dropdown = document.getElementById('notif-dropdown');
    var list = document.getElementById('notif-list');
    if (!bell) {
        return; // 이 화면엔 알림 아이콘이 없음 (로그인/회원가입 화면 등)
    }

    var currentUserId = bell.getAttribute('data-user-id');

    var csrfMeta = document.querySelector('meta[name="_csrf"]');
    var csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    var csrfToken = csrfMeta ? csrfMeta.content : null;
    var csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.content : null;

    function refreshUnreadCount() {
        fetch('/notifications/unread-count')
            .then(function (res) { return res.json(); })
            .then(function (data) {
                if (data.count > 0) {
                    badge.style.display = 'inline-flex';
                    badge.textContent = data.count > 99 ? '99+' : String(data.count);
                } else {
                    badge.style.display = 'none';
                }
            });
    }

    function formatDate(iso) {
        return iso.replace('T', ' ').substring(0, 16);
    }

    function loadList() {
        fetch('/notifications')
            .then(function (res) {
                if (!res.ok) {
                    throw new Error('서버 오류: ' + res.status);
                }
                return res.json();
            })
            .then(function (items) {
                list.innerHTML = '';
                if (items.length === 0) {
                    list.innerHTML = '<div class="notif-empty">알림이 없습니다.</div>';
                    return;
                }
                items.forEach(function (n) {
                    var item = document.createElement('a');
                    item.href = '/requests/' + n.requestId;
                    item.className = 'notif-item' + (n.read ? '' : ' notif-item-unread');
                    item.innerHTML =
                        '<div class="notif-message">' + n.message + '</div>' +
                        '<div class="notif-time">' + formatDate(n.createdAt) + '</div>';
                    list.appendChild(item);
                });
            });
    }

    bell.addEventListener('click', function () {
        var willOpen = !dropdown.classList.contains('open');
        dropdown.classList.toggle('open');
        if (willOpen) {
            loadList();
            var headers = {};
            if (csrfHeader && csrfToken) {
                headers[csrfHeader] = csrfToken;
            }
            fetch('/notifications/read-all', { method: 'POST', headers: headers })
                .then(refreshUnreadCount);
        }
    });

    document.addEventListener('click', function (e) {
        if (dropdown.classList.contains('open') && !dropdown.contains(e.target) && !bell.contains(e.target)) {
            dropdown.classList.remove('open');
        }
    });

    refreshUnreadCount();

    // ===== 웹소켓(STOMP) 연결 =====
    if (currentUserId && window.SockJS && window.Stomp) {
        var socket = new SockJS('/ws');
        var stompClient = Stomp.over(socket);
        stompClient.debug = null; // 콘솔에 프레임 로그 안 찍히게

        stompClient.connect({}, function () {
            stompClient.subscribe('/topic/notifications-' + currentUserId, function () {
                refreshUnreadCount(); // 뱃지 숫자 즉시 갱신
                if (dropdown.classList.contains('open')) {
                    loadList(); // 드롭다운을 이미 열어둔 상태라면 목록 내용도 바로 갱신
                }
            });
        });
    }
})();