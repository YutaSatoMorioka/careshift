<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="タスク登録"/></jsp:include><section class="page-content"><h1><c:choose><c:when test="${not empty task}">タスク編集</c:when><c:otherwise>タスク登録</c:otherwise></c:choose></h1>
<c:if test="${confirmPastWarning}"><p class="alert">期限が過去の日時です。このまま登録しますか？</p></c:if>
<form method="post" action="${pageContext.request.contextPath}/careshift/main/TaskSave.action"><input type="hidden" name="id" value="${empty param.id ? task.id : param.id}"><c:if test="${confirmPastWarning}"><input type="hidden" name="confirmPast" value="true"></c:if>
<label>内容（100文字以内）<input name="title" maxlength="100" required value="<c:out value='${empty param.title ? task.title : param.title}'/>"/></label>
<label>詳細<textarea name="details" maxlength="1000"><c:out value="${empty param.details ? task.details : param.details}"/></textarea></label>
<label>期限<input type="datetime-local" name="dueAt" required value="<c:out value='${empty param.dueAt ? dueAtValue : param.dueAt}'/>"/></label>
<label>所要時間（分・5〜1440）<input type="number" name="durationMinutes" min="5" max="1440" required value="<c:out value='${empty param.durationMinutes ? task.duration_minutes : param.durationMinutes}'/>"/></label>
<label>頻度<input name="frequency" maxlength="30" value="<c:out value='${empty param.frequency ? task.frequency : param.frequency}'/>" placeholder="例：毎週"/></label>
<label>担当者<select name="assigneeUserId"><option value="">未設定</option><c:forEach var="m" items="${members}"><option value="${m.user_id}" ${task.assignee_user_id eq m.user_id?'selected':''}><c:out value="${m.display_name}"/></option></c:forEach></select></label>
<button type="submit"><c:choose><c:when test="${confirmPastWarning}">このまま登録する</c:when><c:otherwise>保存</c:otherwise></c:choose></button></form><p><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">一覧へ戻る</a></p></section><jsp:include page="/common/footer.jsp"/>

