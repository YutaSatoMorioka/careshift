<%-- S17 負担状況 / 設計書: 画面設計書_負担状況 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="負担状況"/></jsp:include>

<h1>負担状況：<c:out value="${periodLabel}"/>（<c:out value="${rangeLabel}"/>）</h1>

<form method="get" action="${pageContext.request.contextPath}/careshift/main/Load.action">
  <label for="period">集計期間</label>
  <select id="period" name="period">
    <option value="week" ${period == 'week' ? 'selected' : ''}>今週</option>
    <option value="month" ${period == 'month' ? 'selected' : ''}>今月</option>
  </select>
  <button type="submit" name="show">表示</button>
</form>
<p><small>期限が期間内のタスク（完了したものも含む）を、担当者ごとに集計しています。</small></p>

<%-- 一人に集中している場合は、依頼の導線を出す --%>
<c:if test="${not empty concentrated}">
  <div class="alert" id="concentration">
    <p><strong><c:out value="${concentrated.name}"/> さんに負担が集中しています</strong>
       （担当あり合計の <c:out value="${concentrated.percent}"/>%・<c:out value="${concentrated.count}"/>件・<c:out value="${concentrated.minutesLabel}"/>）。</p>
    <p>
      <a href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action" id="requestLink">家族に依頼する</a>
      ／ <a href="${pageContext.request.contextPath}/careshift/main/TaskList.action?assignee=${concentrated.userId}">担当タスクを確認する</a>
    </p>
  </div>
</c:if>

<c:choose>
  <c:when test="${totalTasks == 0}">
    <div class="guide" id="empty">
      <p>この期間に期限があるタスクはありません。</p>
      <p><a href="${pageContext.request.contextPath}/careshift/main/TaskCreate.action">タスクを登録する</a></p>
    </div>
  </c:when>
  <c:otherwise>
    <h2>負担グラフ</h2>
    <table id="loadChart" style="width:100%;">
      <tr><th style="width:9em;">家族</th><th>件数</th><th>所要時間</th></tr>
      <c:forEach var="s" items="${stats}">
        <tr>
          <td><c:out value="${s.name}"/></td>
          <td>
            <div class="bar" style="width:${s.countBar}%; min-width:${s.count > 0 ? '4px' : '0'};"></div>
            <small><c:out value="${s.count}"/>件</small>
          </td>
          <td>
            <div class="bar" style="width:${s.minutesBar}%; min-width:${s.minutes > 0 ? '4px' : '0'};"></div>
            <small><c:out value="${s.minutesLabel}"/></small>
          </td>
        </tr>
      </c:forEach>
      <%-- 担当なしは別に表示 --%>
      <tr id="unassignedRow" style="background:#fff4e0;">
        <td><strong>担当なし</strong></td>
        <td>
          <div class="bar" style="background:#f0c070; width:${unassigned.countBar}%; min-width:${unassigned.count > 0 ? '4px' : '0'};"></div>
          <small><c:out value="${unassigned.count}"/>件</small>
        </td>
        <td>
          <div class="bar" style="background:#f0c070; width:${unassigned.minutesBar}%; min-width:${unassigned.minutes > 0 ? '4px' : '0'};"></div>
          <small><c:out value="${unassigned.minutesLabel}"/></small>
        </td>
      </tr>
    </table>

    <h2>詳細</h2>
    <table id="loadList">
      <tr><th>家族</th><th>件数</th><th>所要時間</th><th>割合（担当あり内）</th></tr>
      <c:forEach var="s" items="${stats}">
        <tr>
          <td><c:out value="${s.name}"/></td>
          <td><c:out value="${s.count}"/>件</td>
          <td><c:out value="${s.minutesLabel}"/></td>
          <td><c:out value="${s.percent}"/>%</td>
        </tr>
      </c:forEach>
      <tr style="background:#fff4e0;">
        <td>担当なし</td>
        <td><c:out value="${unassigned.count}"/>件</td>
        <td><c:out value="${unassigned.minutesLabel}"/></td>
        <td>―</td>
      </tr>
    </table>
    <c:if test="${unassigned.count > 0}">
      <p><a href="${pageContext.request.contextPath}/careshift/main/TaskList.action?assignee=none">担当なしのタスクを見る</a></p>
    </c:if>
  </c:otherwise>
</c:choose>

<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
