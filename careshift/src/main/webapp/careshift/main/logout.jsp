<%-- ログアウト完了 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="ログアウト"/></jsp:include>

<h1>ログアウトしました</h1>
<p><a href="${pageContext.request.contextPath}/login.jsp">ログイン画面へ</a></p>

<jsp:include page="/common/footer.jsp"/>

