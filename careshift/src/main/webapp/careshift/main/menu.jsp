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

<ul>
  <li><a href="${pageContext.request.contextPath}/careshift/main/Group.action" id="family">家族管理（グループ）</a></li>
  <c:if test="${not empty member}">
    <li><a href="${pageContext.request.contextPath}/careshift/main/MemberList.action">家族メンバー</a></li>
  </c:if>
  <%-- 画面を作るたびにリンクを足す。例:
  <li><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">タスク一覧</a></li>
  --%>
</ul>

<jsp:include page="/common/footer.jsp"/>
