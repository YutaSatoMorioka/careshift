<%-- 依頼フォームの値を hidden で引き継ぐ部品（プレビューの送信・修正ボタンで共用） --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<input type="hidden" name="requestType" value="<c:out value='${form.requestType}'/>">
<input type="hidden" name="content" value="<c:out value='${form.content}'/>">
<input type="hidden" name="requestedAt" value="<c:out value='${form.requestedAt}'/>">
<input type="hidden" name="duration" value="<c:out value='${form.duration}'/>">
<input type="hidden" name="taskId" value="<c:out value='${form.taskId}'/>">
<c:forEach var="r" items="${form.recipients}">
  <input type="hidden" name="recipients" value="<c:out value='${r}'/>">
</c:forEach>
