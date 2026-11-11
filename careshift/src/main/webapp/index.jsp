<%-- 入口。ログイン中ならホームへ、そうでなければログイン画面へ --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:choose>
  <c:when test="${not empty sessionScope.user}">
    <c:redirect url="/careshift/main/Menu.action"/>
  </c:when>
  <c:otherwise>
    <c:redirect url="/login.jsp"/>
  </c:otherwise>
</c:choose>