<%-- S01 ログイン / 設計書: 画面設計書_ログイン --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="ログイン"/></jsp:include>

<h1>ログイン</h1>

<%-- 認証失敗・ロック・メール未確認時のメッセージ（<c:out> でHTMLエスケープ） --%>
<c:if test="${not empty formError}">
  <p class="error"><c:out value="${formError}"/></p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/careshift/LoginExecute.action">
  <div>
    <label for="email">メールアドレス</label>
    <input type="text" id="email" name="email" size="40" value="<c:out value='${email}'/>">
    <c:if test="${not empty errors['email']}">
      <span class="error"><c:out value="${errors['email']}"/></span>
    </c:if>
  </div>
  <div>
    <label for="password">パスワード</label>
    <input type="password" id="password" name="password" size="40">
    <c:if test="${not empty errors['password']}">
      <span class="error"><c:out value="${errors['password']}"/></span>
    </c:if>
  </div>
  <button type="submit" name="login">ログイン</button>
</form>

<p><a href="${pageContext.request.contextPath}/password_reset.jsp" id="reset">パスワードを忘れた方</a></p>
<p><a href="${pageContext.request.contextPath}/register.jsp">新規登録はこちら</a></p>

<jsp:include page="/common/footer.jsp"/>
