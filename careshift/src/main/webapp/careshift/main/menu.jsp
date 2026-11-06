<%-- S06 ホーム（メニュー）。今後、各画面への導線をここに追加していく --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="ホーム"/></jsp:include>

<h1>ホーム</h1>
<p>ようこそ、<c:out value="${sessionScope.user.displayName}"/> さん</p>

<%-- S04 参加直後の初回ガイド --%>
<c:if test="${sessionScope.guide}">
  <div class="guide">
    <strong>はじめてのガイド</strong>
    <ol>
      <li>「家族管理」で、メンバーと主介護者を確認しましょう。</li>
      <li>タスクを登録して、担当を決めましょう（順次追加予定の機能です）。</li>
      <li>困ったときは、依頼カードで家族に相談できます（順次追加予定の機能です）。</li>
    </ol>
  </div>
  <c:remove var="guide" scope="session"/>
</c:if>

<%-- グループ未所属の案内 --%>
<c:if test="${empty member}">
  <p class="guide">まだ家族グループに所属していません。
    <a href="${pageContext.request.contextPath}/careshift/main/Group.action">グループを作成する</a>
    か、招待リンクを開いて参加してください。</p>
</c:if>

<c:if test="${not empty member}">
  <section class="card"><h2>今日のタスク</h2>
    <c:choose><c:when test="${empty todayTasks}"><p>今日が期限のタスクはありません。</p></c:when><c:otherwise><ul><c:forEach var="t" items="${todayTasks}"><li><c:out value="${t.title}"/> — <c:out value="${t.assignee_name}"/> (<c:out value="${t.status}"/>)</li></c:forEach></ul></c:otherwise></c:choose>
  </section>
  <section class="card"><h2>家族の依頼</h2><p><c:out value="${openRequests.size()}"/> 件</p><a href="${pageContext.request.contextPath}/careshift/main/Request.action">依頼を確認</a></section>
  <section class="card"><h2>休息予定</h2><p><c:out value="${rests.size()}"/> 件</p><a href="${pageContext.request.contextPath}/careshift/main/Rest.action">休息を確認</a></section>
</c:if>

<ul>
  <li><a href="${pageContext.request.contextPath}/careshift/main/Group.action" id="family">家族管理（グループ）</a></li>
  <c:if test="${not empty member}">
    <li><a href="${pageContext.request.contextPath}/careshift/main/MemberList.action">家族メンバー</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">タスク一覧</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Calendar.action">カレンダー</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Request.action">依頼カード</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Rest.action">休息支援</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Burden.action">負担状況</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/AiDraft.action">依頼下書き・タスク提案</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Column.action">コラム</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Notification.action">通知</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Plan.action">プラン</a></li>
  </c:if>
  <%-- 画面を作るたびにリンクを足す。例:
  <li><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">タスク一覧</a></li>
  --%>
</ul>

<jsp:include page="/common/footer.jsp"/>

