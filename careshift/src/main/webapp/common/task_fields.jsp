<%-- タスク登録(S08)・編集(S08-2)で共通の入力欄。使う側が form / errors / members / frequencies / statuses / editMode を用意する --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div>
  <label for="taskTitle">タスク内容（100文字以内・必須）</label>
  <input type="text" id="taskTitle" name="taskTitle" size="60" maxlength="100" value="<c:out value='${form.taskTitle}'/>">
  <c:if test="${not empty errors['taskTitle']}"><span class="error"><c:out value="${errors['taskTitle']}"/></span></c:if>
</div>
<div>
  <label for="dueDate">期限（必須）</label>
  <input type="date" id="dueDate" name="dueDate" value="<c:out value='${form.dueDate}'/>">
  <c:if test="${not empty errors['dueDate']}"><span class="error"><c:out value="${errors['dueDate']}"/></span></c:if>
</div>
<div>
  <label for="duration">所要時間（分・5〜1440・必須）</label>
  <input type="number" id="duration" name="duration" size="5" min="5" max="1440" value="<c:out value='${form.duration}'/>">
  <c:if test="${not empty errors['duration']}"><span class="error"><c:out value="${errors['duration']}"/></span></c:if>
</div>
<div>
  <label for="frequency">頻度</label>
  <select id="frequency" name="frequency">
    <c:forEach var="f" items="${frequencies}">
      <option value="<c:out value='${f.key}'/>" ${f.key == form.frequency ? 'selected' : ''}><c:out value="${f.value}"/></option>
    </c:forEach>
  </select>
  <c:if test="${not empty errors['frequency']}"><span class="error"><c:out value="${errors['frequency']}"/></span></c:if>
</div>
<div>
  <label for="assignee">担当者</label>
  <select id="assignee" name="assignee">
    <option value="">担当なし</option>
    <c:forEach var="m" items="${members}">
      <option value="${m.userId}" ${form.assigneeId == m.userId ? 'selected' : ''}><c:out value="${m.displayName}"/></option>
    </c:forEach>
  </select>
  <c:if test="${not empty errors['assignee']}"><span class="error"><c:out value="${errors['assignee']}"/></span></c:if>
</div>
<c:if test="${editMode}">
  <div>
    <label for="status">状態（必須）</label>
    <select id="status" name="status">
      <c:forEach var="s" items="${statuses}">
        <option value="<c:out value='${s.key}'/>" ${s.key == form.status ? 'selected' : ''}><c:out value="${s.value}"/></option>
      </c:forEach>
    </select>
    <c:if test="${not empty errors['status']}"><span class="error"><c:out value="${errors['status']}"/></span></c:if>
  </div>
</c:if>
