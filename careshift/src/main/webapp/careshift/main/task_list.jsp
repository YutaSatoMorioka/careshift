<<<<<<< HEAD
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="タスク一覧"/></jsp:include>
<section class="page-content"><h1>家族のタスク</h1>
<nav><a href="${pageContext.request.contextPath}/careshift/main/TaskForm.action">タスクを登録</a> | <a href="${pageContext.request.contextPath}/careshift/main/Calendar.action">カレンダー</a> | <a href="${pageContext.request.contextPath}/careshift/main/Request.action">依頼</a> | <a href="${pageContext.request.contextPath}/careshift/main/Rest.action">休息</a> | <a href="${pageContext.request.contextPath}/careshift/main/Burden.action">負担状況</a> | <a href="${pageContext.request.contextPath}/careshift/main/Column.action">コラム</a> | <a href="${pageContext.request.contextPath}/careshift/main/Notification.action">通知</a> | <a href="${pageContext.request.contextPath}/careshift/main/Plan.action">プラン</a></nav>
<p><a href="${pageContext.request.contextPath}/careshift/main/TaskCsv.action">タスクCSV出力</a> | <a href="${pageContext.request.contextPath}/careshift/main/DataCsv.action">家族・タスクCSV出力</a> | <a href="${pageContext.request.contextPath}/careshift/main/PrintTasks.action">印刷・PDF保存</a></p>
<c:choose><c:when test="${empty tasks}"><p>タスクはまだありません。</p></c:when><c:otherwise><table><thead><tr><th>内容</th><th>期限</th><th>担当</th><th>時間</th><th>進捗</th><th>操作</th></tr></thead><tbody>
<c:forEach var="t" items="${tasks}"><tr><td><c:out value="${t.title}"/><c:if test="${not empty t.details}"><br><small><c:out value="${t.details}"/></small></c:if></td><td><c:out value="${t.due_at}"/></td><td><c:out value="${t.assignee_name}"/></td><td><c:out value="${t.duration_minutes}"/>分</td><td><form method="post" action="${pageContext.request.contextPath}/careshift/main/TaskStatus.action"><input type="hidden" name="id" value="${t.id}"><select name="status"><option value="TODO" ${t.status eq 'TODO'?'selected':''}>未対応</option><option value="DOING" ${t.status eq 'DOING'?'selected':''}>対応中</option><option value="DONE" ${t.status eq 'DONE'?'selected':''}>完了</option></select><button>更新</button></form></td><td><a href="${pageContext.request.contextPath}/careshift/main/TaskForm.action?id=${t.id}">編集</a><form method="post" action="${pageContext.request.contextPath}/careshift/main/TaskDelete.action" onsubmit="return confirm('このタスクを削除しますか？');"><input type="hidden" name="id" value="${t.id}"><button>削除</button></form></td></tr></c:forEach>
</tbody></table></c:otherwise></c:choose></section><jsp:include page="/common/footer.jsp"/>

=======
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
>>>>>>> branch 'master' of https://github.com/YutaSatoMorioka/careshift.git
