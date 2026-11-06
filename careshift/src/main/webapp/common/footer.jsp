<%-- 共通フッタ（header.jsp で開いた main / body / html を閉じる） --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
</main>
<footer>&copy; Care Shift</footer>
<script>
  (function () {
    const fontControl = document.getElementById('fontSizeControl');
    const savedSize = localStorage.getItem('careshift-font-size') || '16';
    document.body.style.fontSize = savedSize + 'px';
    if (fontControl) {
      fontControl.value = savedSize;
      fontControl.addEventListener('change', function () {
        document.body.style.fontSize = fontControl.value + 'px';
        localStorage.setItem('careshift-font-size', fontControl.value);
      });
    }
    const token = document.body.dataset.csrfToken;
    document.querySelectorAll('form[method="post"], form[method="POST"]').forEach(function (form) {
      if (!form.querySelector('input[name="_csrf"]')) {
        const input = document.createElement('input');
        input.type = 'hidden'; input.name = '_csrf'; input.value = token;
        form.appendChild(input);
      }
    });
  })();
</script>
</body>
</html>
