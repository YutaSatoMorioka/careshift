<%-- AI002 遠方家族向け提案 / 設計書: 画面設計書_遠方家族向け提案 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="遠方からできるタスクの提案"/></jsp:include>

<h1>遠方からできるタスクの提案</h1>
<p>離れていても担当できそうなタスクの候補です。選んで「タスクに追加」を押すと、内容が入った状態でタスク登録画面が開きます（押すまで登録されません）。</p>

<form method="get" action="${pageContext.request.contextPath}/careshift/main/RemoteSuggest.action">
  <label for="conditions">実施可能条件</label>
  <select id="conditions" name="conditions">
    <c:forEach var="c" items="${conditionList}">
      <option value="<c:out value='${c.key}'/>" ${c.key == conditions ? 'selected' : ''}><c:out value="${c.value}"/></option>
    </c:forEach>
  </select>
  <button type="submit" name="show">候補を表示</button>
</form>

<table id="suggestions">
  <tr><th>候補</th><th>内容</th><th>所要時間の目安</th><th>操作</th></tr>
  <c:forEach var="s" items="${suggestions}">
    <tr>
      <td><c:out value="${s.title}"/></td>
      <td><c:out value="${s.description}"/></td>
      <td><c:out value="${s.durationMin}"/>分</td>
      <td>
        <%-- 選んだ候補だけを、登録画面の初期値として渡す（この時点では登録しない） --%>
        <form method="get" action="${pageContext.request.contextPath}/careshift/main/TaskCreate.action">
          <input type="hidden" name="taskTitle" value="<c:out value='${s.title}'/>">
          <input type="hidden" name="duration" value="<c:out value='${s.durationMin}'/>">
          <input type="hidden" name="dueDate" value="<c:out value='${s.dueDate}'/>">
          <button type="submit" name="addTask">タスクに追加</button>
        </form>
      </td>
    </tr>
  </c:forEach>
</table>
<p><small>これは固定の候補です。内容は、登録画面で自由に直せます。</small></p>

<p><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">タスク一覧へ</a>
 ／ <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
