<%-- S08 タスク登録 / 設計書: 画面設計書_タスク登録 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="タスク登録"/></jsp:include>

<h1>タスク登録</h1>

<c:if test="${not empty errors['save']}">
  <p class="alert"><c:out value="${errors['save']}"/></p>
</c:if>

<%-- 過去の期限・医療情報らしい語：確認してから登録する --%>
<c:if test="${not empty warnings}">
  <div class="guide" id="warnings">
    <strong>登録前にご確認ください</strong>
    <ul>
      <c:forEach var="w" items="${warnings}"><li><c:out value="${w}"/></li></c:forEach>
    </ul>
    <p>問題がなければ「確認して登録」を押してください。</p>
  </div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/careshift/main/TaskCreateExecute.action">
  <c:if test="${not empty warnings}"><input type="hidden" name="confirm" value="1"></c:if>
  <c:set var="editMode" value="${false}" scope="request"/>
  <jsp:include page="/common/task_fields.jsp"/>
  <button type="submit" name="save">${empty warnings ? '登録' : '確認して登録'}</button>
</form>

<p><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">タスク一覧へ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
