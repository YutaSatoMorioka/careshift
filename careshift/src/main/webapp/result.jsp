<%-- 結果メッセージ共通画面。Action.showResult(...) から表示する --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="${resultTitle}"/></jsp:include>

<h1><c:out value="${resultTitle}"/></h1>
<p><c:out value="${resultMessage}"/></p>
<c:if test="${not empty resultLinkPath}">
  <p><a href="${pageContext.request.contextPath}${resultLinkPath}"><c:out value="${resultLinkLabel}"/></a></p>
</c:if>

<jsp:include page="/common/footer.jsp"/>
