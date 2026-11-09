<%-- S03 新しいパスワードの入力（メール内リンクから表示） --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="新しいパスワードの設定"/></jsp:include>

<h1>新しいパスワードの設定</h1>

<form method="post" action="${pageContext.request.contextPath}/careshift/PasswordResetExecute.action">
  <input type="hidden" name="token" value="<c:out value='${token}'/>">
  <div>
    <label for="password">新しいパスワード（8文字以上）</label>
    <input type="password" id="password" name="password" size="40">
    <c:if test="${not empty errors['password']}">
      <span class="error"><c:out value="${errors['password']}"/></span>
    </c:if>
  </div>
  <div>
    <label for="passwordConfirm">新しいパスワード（確認）</label>
    <input type="password" id="passwordConfirm" name="passwordConfirm" size="40">
    <c:if test="${not empty errors['passwordConfirm']}">
      <span class="error"><c:out value="${errors['passwordConfirm']}"/></span>
    </c:if>
  </div>
  <button type="submit" name="save">パスワードを変更</button>
</form>

<jsp:include page="/common/footer.jsp"/>
