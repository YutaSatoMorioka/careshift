<%-- S02 確認メール送信の案内 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="確認メールを送信しました"/></jsp:include>

<c:choose>
  <c:when test="${mailFailed}">
    <h1>確認メールの送信に失敗しました</h1>
    <p>登録は受け付けましたが、確認メールを送信できませんでした。
       時間をおいてログイン画面からログインすると、確認メールを再送します。</p>
  </c:when>
  <c:otherwise>
    <h1>確認メールを送信しました</h1>
    <p><c:out value="${email}"/> 宛てに確認メールを送信しました。</p>
    <p>メール内のリンクを24時間以内に開くと、本登録が完了します。</p>
    <p>届かない場合は迷惑メールフォルダをご確認ください。期限が切れた場合は、ログインすると確認メールを再送します。</p>
  </c:otherwise>
</c:choose>
<form method="post" action="${pageContext.request.contextPath}/careshift/ResendVerification.action">
  <input type="hidden" name="email" value="<c:out value='${email}'/>">
  <button type="submit">確認メールを再送する</button>
</form>
<p><a href="${pageContext.request.contextPath}/login.jsp">ログインへ</a></p>

<jsp:include page="/common/footer.jsp"/>

