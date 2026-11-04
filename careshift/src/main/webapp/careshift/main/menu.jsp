<%-- S06 ホーム（メニュー）。今後、各画面への導線をここに追加していく --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="ホーム"/></jsp:include>

<h1>ホーム</h1>
<p>ようこそ、<c:out value="${sessionScope.user.displayName}"/> さん</p>

<%-- 画面を作るたびにリンクを足す。例:
<ul>
  <li><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">タスク一覧</a></li>
</ul>
--%>

<jsp:include page="/common/footer.jsp"/>
