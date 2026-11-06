<%-- NOTI001 通知一覧 / 設計書: 画面設計書_通知一覧 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="通知一覧"/></jsp:include>

<h1>通知一覧</h1>

<div style="display:flex; gap:12px; align-items:center;">
  <form method="get" action="${pageContext.request.contextPath}/careshift/main/NotificationList.action">
    <label for="type">通知種別</label>
    <select id="type" name="type">
      <c:forEach var="t" items="${types}">
        <option value="<c:out value='${t.key}'/>" ${t.key == type ? 'selected' : ''}><c:out value="${t.value}"/></option>
      </c:forEach>
    </select>
    <button type="submit" name="filter">絞り込む</button>
  </form>

  <%-- すべて既読 --%>
  <form method="post" action="${pageContext.request.contextPath}/careshift/main/NotificationReadAll.action">
    <button type="submit" name="markAllRead">すべて既読にする</button>
  </form>
</div>

<c:choose>
  <%-- 読み込み失敗：再試行 --%>
  <c:when test="${loadError}">
    <div class="alert" id="loadError">
      <p>通知の読み込みに失敗しました。</p>
      <p><a href="${pageContext.request.contextPath}/careshift/main/NotificationList.action?type=${type}">再試行する</a></p>
    </div>
  </c:when>
  <c:when test="${empty notifications}">
    <div class="guide" id="empty"><p>通知はありません。</p></div>
  </c:when>
  <c:otherwise>
    <table id="notificationList">
      <tr><th>日時</th><th>種別</th><th>内容</th><th>状態</th><th>操作</th></tr>
      <c:forEach var="n" items="${notifications}">
        <tr style="${n.unread ? 'background:#fffbe6; font-weight:bold;' : ''}">
          <td><c:out value="${n.createdAtLabel}"/></td>
          <td><c:out value="${n.typeLabel}"/></td>
          <%-- 通知を選ぶと対象の画面へ移動し、既読になる --%>
          <td><a href="${pageContext.request.contextPath}/careshift/main/NotificationOpen.action?id=${n.id}"><c:out value="${n.message}"/></a></td>
          <td><c:choose><c:when test="${n.unread}"><span class="tag" style="background:#c77d00;">未読</span></c:when><c:otherwise>既読</c:otherwise></c:choose></td>
          <td>
            <c:if test="${n.unread}">
              <form method="post" style="display:inline" action="${pageContext.request.contextPath}/careshift/main/NotificationRead.action">
                <input type="hidden" name="id" value="${n.id}">
                <input type="hidden" name="type" value="<c:out value='${type}'/>">
                <button type="submit" name="markRead">既読にする</button>
              </form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </table>
    <p><small>最新の100件まで表示します。</small></p>
  </c:otherwise>
</c:choose>

<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
