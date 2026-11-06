<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="カレンダー"/></jsp:include><section class="page-content"><h1>タスクカレンダー</h1>
<form method="get"><label>月<input type="month" name="month" value="${month}"/></label><button>表示</button></form>
<table class="calendar"><thead><tr><th>日</th><th>月</th><th>火</th><th>水</th><th>木</th><th>金</th><th>土</th></tr></thead><tbody><c:forEach var="week" items="${weeks}"><tr><c:forEach var="day" items="${week}"><td style="vertical-align:top;height:100px;min-width:90px;background:${day.inMonth?'#fff':'#f2f2f2'}"><strong><c:out value="${day.day}"/></strong><c:forEach var="t" items="${day.tasks}"><div><a href="${pageContext.request.contextPath}/careshift/main/TaskForm.action?id=${t.id}"><c:out value="${t.title}"/></a><br><small><c:out value="${t.assignee_name}"/></small></div></c:forEach></td></c:forEach></tr></c:forEach></tbody></table>
<p><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">タスク一覧へ</a></p></section><jsp:include page="/common/footer.jsp"/>

