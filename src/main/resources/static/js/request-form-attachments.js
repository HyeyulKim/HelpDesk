(function () {
    var form = document.getElementById('requestForm');
    var input = document.getElementById('files');
    var pickBtn = document.getElementById('pickFilesBtn');
    var list = document.getElementById('attachList');
    var empty = document.getElementById('attachEmpty');
    if (!form || !input || !pickBtn || !list || !empty) {
        return;
    }

    // 지금까지 고른 파일 (여러 번에 나눠서 골라도 여기에 계속 쌓임)
    var selected = [];

    function sizeText(bytes) {
        return (bytes / 1024).toFixed(1) + ' KB';   // 상세 화면과 같은 표기
    }

    // 화면에 쌓아둔 목록을 실제 전송용 input에 반영
    function sync() {
        var dt = new DataTransfer();
        selected.forEach(function (f) { dt.items.add(f); });
        input.files = dt.files;
    }

    function render() {
        list.innerHTML = '';
        selected.forEach(function (file) {
            var li = document.createElement('li');
            li.className = 'attachment-item';

            var name = document.createElement('span');
            name.className = 'attachment-name';
            name.textContent = file.name;

            var size = document.createElement('span');
            size.className = 'attachment-size';
            size.textContent = sizeText(file.size);

            var del = document.createElement('button');
            del.type = 'button';
            del.className = 'comment-action-btn comment-action-danger';
            del.textContent = '삭제';
            del.addEventListener('click', function () {
                var pos = selected.indexOf(file);   // 클릭하는 시점의 위치를 다시 찾음
                if (pos > -1) {
                    selected.splice(pos, 1);
                }
                sync();
                render();
            });

            li.appendChild(name);
            li.appendChild(size);
            li.appendChild(del);
            list.appendChild(li);
        });

        var hasFiles = selected.length > 0;
        list.style.display = hasFiles ? '' : 'none';
        empty.style.display = hasFiles ? 'none' : '';
    }

    pickBtn.addEventListener('click', function () {
        input.click();
    });

    input.addEventListener('change', function () {
        Array.prototype.forEach.call(input.files, function (f) {
            var duplicated = selected.some(function (s) {
                return s.name === f.name && s.size === f.size && s.lastModified === f.lastModified;
            });
            if (!duplicated) {
                selected.push(f);
            }
        });
        sync();
        render();
    });

    // 파일 선택창을 열었다가 취소하면 브라우저가 선택값을 비워버리는 경우가 있어서, 전송 직전에 한 번 더 맞춰둠
    form.addEventListener('submit', sync);

    render();
})();