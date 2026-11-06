<%-- S09 タスク詳細 / 設計書: S09_タスク詳細 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="タスク詳細"/></jsp:include>

<h1>タスク詳細</h1>

<dl>
  <dt>タスク内容</dt><dd id="taskTitle"><c:out value="${task.title}"/></dd>
  <dt>期限・所要時間・頻度</dt>
  <dd id="taskSchedule">
    <c:out value="${task.dueDate}"/>
    <c:if test="${task.overdue}"><span class="error">（期限超過）</span></c:if>
    ／ <c:out value="${task.durationLabel}"/> ／ <c:out value="${task.frequencyLabel}"/>
  </dd>
  <dt>登録者</dt><dd><c:out value="${task.creatorName}"/></dd>
</dl>

<c:choose>
  <%-- 権限あり：担当者・進捗を更新できる --%>
  <c:when test="${canEdit}">
    <form method="post" action="${pageContext.request.contextPath}/careshift/main/TaskQuickUpdate.action">
      <input type="hidden" name="id" value="${task.id}">
      <input type="hidden" name="version" value="${task.version}">
      <div>
        <label for="assignee">担当者</label>
        <select id="assignee" name="assignee">
          <option value="">担当なし</option>
          <c:forEach var="m" items="${members}">
            <option value="${m.userId}" ${task.assigneeId == m.userId ? 'selected' : ''}><c:out value="${m.displayName}"/></option>
          </c:forEach>
        </select>
      </div>
      <div>
        <label for="status">進捗</label>
        <select id="status" name="status">
          <c:forEach var="s" items="${statuses}">
            <option value="<c:out value='${s.key}'/>" ${s.key == task.status ? 'selected' : ''}><c:out value="${s.value}"/></option>
          </c:forEach>
        </select>
      </div>
      <button type="submit" name="update">担当・進捗を更新</button>
    </form>

    <p>
      <a href="${pageContext.request.contextPath}/careshift/main/TaskEdit.action?id=${task.id}" id="edit">編集</a>
    </p>
    <form method="post" action="${pageContext.request.contextPath}/careshift/main/TaskDelete.action"
          onsubmit="return confirm('このタスクを削除します。担当者にも通知されます。よろしいですか？');">
      <input type="hidden" name="id" value="${task.id}">
      <button type="submit" name="delete">削除</button>
    </form>
  </c:when>

  <%-- 権限なし：表示のみ（編集操作は出さない。直接送られても Action で拒否する） --%>
  <c:otherwise>
    <dl>
      <dt>担当者</dt>
      <dd><c:choose>
        <c:when test="${task.unassigned}">担当なし</c:when>
        <c:otherwise><c:out value="${task.assigneeName}"/></c:otherwise>
      </c:choose></dd>
      <dt>進捗</dt><dd><c:out value="${task.statusLabel}"/></dd>
    </dl>
    <p>このタスクを編集できるのは、登録者・担当者・主介護者です。</p>
  </c:otherwise>
</c:choose>

<%-- 依頼カード作成（S12）へ。このタスクの内容・所要時間・期限を初期値にする --%>
<c:if test="${members.size() > 1}">
  <p><a href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action?taskId=${task.id}" id="request">このタスクを家族に依頼する</a></p>
</c:if>

<p><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">タスク一覧へ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
