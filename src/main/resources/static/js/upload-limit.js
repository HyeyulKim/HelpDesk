(function () {
    var MAX_FILE = 10 * 1024 * 1024;                  // 파일 하나당 10MB
    var MAX_TOTAL = 30 * 1024 * 1024 - 100 * 1024;    // 한 번에 30MB (여유분 100KB)

    function toMB(bytes) {
        return (bytes / 1024 / 1024).toFixed(1) + 'MB';
    }

    function showError(input, message) {
        var box = document.getElementById('uploadError');
        if (!box) {
            box = document.createElement('div');
            box.id = 'uploadError';
            box.className = 'alert alert-error';
            box.style.whiteSpace = 'pre-line';
            var anchor = input.form || input;
            anchor.parentNode.insertBefore(box, anchor);
        }
        box.textContent = message;
        box.style.display = message ? '' : 'none';
    }

    document.querySelectorAll('input[type="file"][data-max-check]').forEach(function (input) {
        input.addEventListener('change', function () {
            var files = Array.prototype.slice.call(input.files);
            var total = 0;
            var problems = [];

            files.forEach(function (f) {
                total += f.size;
                if (f.size > MAX_FILE) {
                    problems.push('"' + f.name + '" (' + toMB(f.size) + '): 파일 하나당 10MB를 넘습니다.');
                }
            });
            if (problems.length === 0 && total > MAX_TOTAL) {
                problems.push('선택한 파일의 합계가 30MB를 넘습니다. (' + toMB(total) + ')');
            }

            showError(input, problems.join('\n'));
            if (problems.length > 0) {
                input.value = '';   // 선택 취소
            }
        });
    });
})();