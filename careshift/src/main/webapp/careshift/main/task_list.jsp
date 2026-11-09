<%-- S07 タスク一覧 / 設計書: 画面設計書_タスク一覧 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="タスク一覧"/></jsp:include>

<h1>タスク一覧</h1>

<form method="get" action="${pageContext.request.contextPath}/careshift/main/TaskList.action">
  <label for="keyword">キーワード</label>
  <input type="text" id="keyword" name="keyword" size="30" maxlength="60" value="<c:out value='${keyword}'/>">

  <label for="assignee">担当者</label>
  <select id="assignee" name="assignee">
    <option value="">すべて</option>
    <option value="none" ${assignee == 'none' ? 'selected' : ''}>担当なし</option>
    <c:forEach var="m" items="${members}">
      <option value="${m.userId}" ${assigneeId == m.userId ? 'selected' : ''}><c:out value="${m.displayName}"/></option>
    </c:forEach>
  </select>

  <label for="dueDate">期限（この日まで）</label>
  <input type="date" id="dueDate" name="dueDate" value="<c:out value='${dueDate}'/>">
  <c:if test="${not empty errors['dueDate']}"><span class="error"><c:out value="${errors['dueDate']}"/></span></c:if>

  <label for="status">状態</label>
  <select id="status" name="status">
    <option value="">すべて</option>
    <c:forEach var="s" items="${statuses}">
      <option value="<c:out value='${s.key}'/>" ${s.key == status ? 'selected' : ''}><c:out value="${s.value}"/></option>
    </c:forEach>
  </select>

  <button type="submit" name="search">検索</button>
</form>

<p>
  <a href="${pageContext.request.contextPath}/careshift/main/TaskCreate.action" id="create">新規登録</a>
  ／ <a href="${pageContext.request.contextPath}/careshift/main/Calendar.action">カレンダーで見る</a>
</p>

<c:choose>
  <c:when test="${empty tasks}">
    <%-- 0件：空状態と追加の導線 --%>
    <div class="guide" id="empty">
      <p>条件に合うタスクはありません。</p>
      <p><a href="${pageContext.request.contextPath}/careshift/main/TaskCreate.action">タスクを登録する</a></p>
    </div>
  </c:when>
  <c:otherwise>
    <table id="taskList">
      <tr><th>内容</th><th>担当者</th><th>期限</th><th>所要時間</th><th>状態</th></tr>
      <c:forEach var="t" items="${tasks}">
        <tr>
          <td><a href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}"><c:out value="${t.title}"/></a></td>
          <td>
            <c:choose>
              <c:when test="${t.unassigned}"><span class="tag" style="background:#c77d00;">担当なし</span></c:when>
              <c:otherwise><c:out value="${t.assigneeName}"/></c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:out value="${t.dueDate}"/>
            <c:if test="${t.overdue}"><span class="error">（期限超過）</span></c:if>
          </td>
          <td><c:out value="${t.durationLabel}"/></td>
          <td><c:out value="${t.statusLabel}"/></td>
        </tr>
      </c:forEach>
    </table>
    <p><c:out value="${tasks.size()}"/> 件</p>
  </c:otherwise>
</c:choose>

<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
