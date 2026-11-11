<%-- S06 ホーム / 設計書: 画面設計書_メインメニュー --%>
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
      <li>「タスク一覧」で、担当を決めましょう。</li>
      <li>困ったときは、「依頼する」で家族に相談できます。</li>
    </ol>
  </div>
  <c:remove var="guide" scope="session"/>
</c:if>

<%-- グループ未所属の案内 --%>
<c:if test="${empty member}">
  <p class="guide">まだ家族グループに所属していません。
    <a href="${pageContext.request.contextPath}/careshift/main/Group.action" id="family">グループを作成する</a>
    か、招待リンクを開いて参加してください。</p>
</c:if>

<c:if test="${not empty member}">
  <%-- 今週のタスク状況 --%>
  <h2>今週のタスク状況（<c:out value="${weekLabel}"/>）</h2>
  <c:choose>
    <c:when test="${weekError}">
      <p class="alert">今週のタスク状況を読み込めませんでした。<a href="${pageContext.request.contextPath}/careshift/main/Menu.action">再読み込み</a></p>
    </c:when>
    <c:otherwise>
      <div id="weekStatus">
        <p>
          期限が今週のタスク <strong id="weekTotal"><c:out value="${weekTotal}"/></strong> 件：
          未対応 <strong id="todoCount"><c:out value="${todoCount}"/></strong> ／
          対応中 <strong id="doingCount"><c:out value="${doingCount}"/></strong> ／
          完了 <strong id="doneCount"><c:out value="${doneCount}"/></strong>
        </p>
        <c:if test="${overdueCount > 0}">
          <p class="error" id="overdue">期限を過ぎた未完了のタスクが <c:out value="${overdueCount}"/> 件あります。</p>
        </c:if>
        <c:choose>
          <c:when test="${empty myTasks}"><p>今週期限のあなたの担当タスク（未完了）はありません。</p></c:when>
          <c:otherwise>
            <p>あなたの担当（未完了）</p>
            <ul id="myTasks">
              <c:forEach var="t" items="${myTasks}">
                <li><a href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}"><c:out value="${t.title}"/></a>
                  （<c:out value="${t.dueDate}"/>・<c:out value="${t.statusLabel}"/>）</li>
              </c:forEach>
            </ul>
          </c:otherwise>
        </c:choose>
      </div>
    </c:otherwise>
  </c:choose>
</c:if>

<%-- 未読通知 --%>
<h2>未読の通知<c:if test="${unreadCount > 0}">（<c:out value="${unreadCount}"/>件）</c:if></h2>
<c:choose>
  <c:when test="${notificationError}">
    <p class="alert">通知を読み込めませんでした。<a href="${pageContext.request.contextPath}/careshift/main/Menu.action">再読み込み</a></p>
  </c:when>
  <c:when test="${empty unreadList}"><p id="noUnread">未読の通知はありません。</p></c:when>
  <c:otherwise>
    <ul id="unreadList">
      <c:forEach var="n" items="${unreadList}">
        <li>
          <span class="tag"><c:out value="${n.typeLabel}"/></span>
          <a href="${pageContext.request.contextPath}/careshift/main/NotificationOpen.action?id=${n.id}"><c:out value="${n.message}"/></a>
          <small>（<c:out value="${n.createdAtLabel}"/>）</small>
        </li>
      </c:forEach>
    </ul>
  </c:otherwise>
</c:choose>
<p><a href="${pageContext.request.contextPath}/careshift/main/NotificationList.action">通知一覧へ</a></p>

<%-- 主要導線 --%>
<h2>メニュー</h2>
<ul id="menu">
  <c:if test="${not empty member}">
    <li><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action" id="tasks">タスク一覧</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Calendar.action" id="calendar">カレンダー</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Group.action" id="family">家族管理</a>
        （<a href="${pageContext.request.contextPath}/careshift/main/MemberList.action">メンバー</a>）</li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/RequestList.action" id="requests">依頼一覧</a>
        ／ <a href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">依頼する</a></li>
    <c:if test="${member.primary}">
      <li><a href="${pageContext.request.contextPath}/careshift/main/RestRequest.action" id="rest">休息リクエスト</a></li>
    </c:if>
    <li><a href="${pageContext.request.contextPath}/careshift/main/RestReport.action">休息レポート</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Load.action" id="load">負担状況</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/TaskSheet.action">タスク分担表（PDF・印刷）</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/RemoteSuggest.action">遠方からできるタスクの提案</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/AiDraft.action" id="aiDraft">AIで依頼文の下書きを作る</a></li>
    <li><a href="${pageContext.request.contextPath}/careshift/main/Settings.action" id="settings">設定</a>
        （<a href="${pageContext.request.contextPath}/careshift/main/Plan.action" id="plan">プラン</a>）</li>
  </c:if>
  <li><a href="${pageContext.request.contextPath}/careshift/main/NotificationList.action" id="notifications2">通知一覧</a></li>
  <c:if test="${isAdmin}">
    <li><a href="${pageContext.request.contextPath}/careshift/main/Admin.action" id="admin">管理者</a></li>
  </c:if>
</ul>

<%-- おすすめコラム（日替わり）と、コラム一覧への導線 --%>
<div class="guide" id="columnGuide">
  <strong>おすすめコラム</strong>
  <p><a href="${pageContext.request.contextPath}/careshift/main/Column.action?id=${featuredArticle.id}"><c:out value="${featuredArticle.title}"/></a><br>
     <small><c:out value="${featuredArticle.summary}"/></small></p>
  <p><a href="${pageContext.request.contextPath}/careshift/main/Column.action" id="column">コラム・チェックリストの一覧へ</a></p>
</div>

<jsp:include page="/common/footer.jsp"/>
