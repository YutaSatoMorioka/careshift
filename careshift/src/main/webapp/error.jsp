<%-- ERROR01 エラー画面。原因と回復方法を示し、戻る／ホームへの導線を用意する。認証情報・相談文などは表示しない --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="エラー"/></jsp:include>

<h1>エラーが発生しました</h1>
<p id="errorMessage">
  <c:choose>
    <c:when test="${not empty errorMessage}"><c:out value="${errorMessage}"/></c:when>
    <c:otherwise>処理中に問題が発生しました。時間をおいて、もう一度お試しください。</c:otherwise>
  </c:choose>
</p>
<p>
  <a href="javascript:history.back()" id="back">前の画面に戻る</a>
  ／
  <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ</a>
</p>

<jsp:include page="/common/footer.jsp"/>

