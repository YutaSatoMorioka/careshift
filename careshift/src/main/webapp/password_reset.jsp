<%-- S03 パスワード再設定（メール送信依頼） / 設計書: 画面設計書_パスワード再設定 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="パスワード再設定"/></jsp:include>

<h1>パスワード再設定</h1>
<p>登録したメールアドレスを入力してください。再設定の手順をメールでお送りします。</p>

<form method="post" action="${pageContext.request.contextPath}/careshift/PasswordResetRequest.action">
  <div>
    <label for="email">メールアドレス</label>
    <input type="email" id="email" name="email" size="40" value="<c:out value='${email}'/>">
    <c:if test="${not empty errors['email']}">
      <span class="error"><c:out value="${errors['email']}"/></span>
    </c:if>
  </div>
  <button type="submit" name="sendReset">再設定メール送信</button>
</form>

<p><a href="${pageContext.request.contextPath}/login.jsp" id="login">ログインへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
