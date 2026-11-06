<%-- S02 新規登録 / 設計書: S02_新規登録 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="新規登録"/></jsp:include>

<h1>新規登録</h1>
<p>表示名・メールアドレス・パスワードを入力してください。確認メールのリンクを開くと本登録が完了します。</p>

<form method="post" action="${pageContext.request.contextPath}/careshift/RegisterExecute.action">
  <div>
    <label for="displayName">表示名（30文字以内）</label>
    <input type="text" id="displayName" name="displayName" size="30" value="<c:out value='${displayName}'/>">
    <c:if test="${not empty errors['displayName']}">
      <span class="error"><c:out value="${errors['displayName']}"/></span>
    </c:if>
  </div>
  <div>
    <label for="email">メールアドレス</label>
    <input type="email" id="email" name="email" size="40" value="<c:out value='${email}'/>">
    <c:if test="${not empty errors['email']}">
      <span class="error"><c:out value="${errors['email']}"/></span>
    </c:if>
    <%-- 登録済みの場合は、ログイン／再設定への案内を表示（設計書の代替フロー） --%>
    <c:if test="${alreadyRegistered}">
      <div>
        <a href="${pageContext.request.contextPath}/login.jsp">ログインする</a>
        ／
        <a href="${pageContext.request.contextPath}/password_reset.jsp">パスワードを再設定する</a>
      </div>
    </c:if>
  </div>
  <div>
    <label for="password">パスワード（8文字以上）</label>
    <input type="password" id="password" name="password" size="40">
    <c:if test="${not empty errors['password']}">
      <span class="error"><c:out value="${errors['password']}"/></span>
    </c:if>
  </div>
  <div>
    <label for="passwordConfirm">パスワード（確認）</label>
    <input type="password" id="passwordConfirm" name="passwordConfirm" size="40">
    <c:if test="${not empty errors['passwordConfirm']}">
      <span class="error"><c:out value="${errors['passwordConfirm']}"/></span>
    </c:if>
  </div>
  <button type="submit" name="register">登録</button>
</form>

<p><a href="${pageContext.request.contextPath}/login.jsp" id="login">ログインへ</a></p>

<jsp:include page="/common/footer.jsp"/>

