<%-- S08-2 タスク編集 / 設計書: 画面設計書_タスク編集 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="タスク編集"/></jsp:include>

<h1>タスク編集</h1>

<c:if test="${not empty errors['save']}">
  <p class="alert"><c:out value="${errors['save']}"/></p>
</c:if>

<c:if test="${not empty warnings}">
  <div class="guide" id="warnings">
    <strong>保存前にご確認ください</strong>
    <ul>
      <c:forEach var="w" items="${warnings}"><li><c:out value="${w}"/></li></c:forEach>
    </ul>
    <p>問題がなければ「確認して保存」を押してください。</p>
  </div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/careshift/main/TaskEditExecute.action">
  <input type="hidden" name="id" value="${task.id}">
  <%-- 開いた時点の版。保存時に、先に誰かが更新していないか確かめるために使う --%>
  <input type="hidden" name="version" value="<c:out value='${form.version}'/>">
  <c:if test="${not empty warnings}"><input type="hidden" name="confirm" value="1"></c:if>
  <c:set var="editMode" value="${true}" scope="request"/>
  <jsp:include page="/common/task_fields.jsp"/>
  <button type="submit" name="save">${empty warnings ? '保存' : '確認して保存'}</button>
</form>

<form method="post" action="${pageContext.request.contextPath}/careshift/main/TaskDelete.action"
      onsubmit="return confirm('このタスクを削除します。担当者にも通知されます。よろしいですか？');">
  <input type="hidden" name="id" value="${task.id}">
  <button type="submit" name="delete">削除</button>
</form>

<p><a href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${task.id}">詳細へ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
