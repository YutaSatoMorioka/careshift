<%-- S19 タスク分担表 / 設計書: 画面設計書_タスク分担表出力 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="タスク分担表"/></jsp:include>

<h1>タスク分担表</h1>

<form method="get" class="noprint" action="${pageContext.request.contextPath}/careshift/main/TaskSheet.action">
  <label for="period">対象期間</label>
  <select id="period" name="period">
    <option value="week" ${sheet.period == 'week' ? 'selected' : ''}>今週</option>
    <option value="month" ${sheet.period == 'month' ? 'selected' : ''}>今月</option>
    <option value="all" ${sheet.period == 'all' ? 'selected' : ''}>全て</option>
  </select>
  <label for="assignee">担当者</label>
  <select id="assignee" name="assignee">
    <option value="">全員</option>
    <option value="none" ${sheet.assignee == 'none' ? 'selected' : ''}>担当なし</option>
    <c:forEach var="m" items="${members}">
      <option value="${m.userId}" ${sheet.assignee == m.userId.toString() ? 'selected' : ''}><c:out value="${m.displayName}"/></option>
    </c:forEach>
  </select>
  <label for="order">並び順</label>
  <select id="order" name="order">
    <option value="assignee" ${sheet.order == 'assignee' ? 'selected' : ''}>担当者別</option>
    <option value="due" ${sheet.order == 'due' ? 'selected' : ''}>期限順</option>
  </select>
  <button type="submit" name="preview">プレビュー</button>
</form>

<c:if test="${sheet.blocked}">
  <p class="alert" id="blockedReason"><c:out value="${sheet.blockedReason}"/></p>
</c:if>

<%-- 出力ボタン（出力できないときは押せない） --%>
<div class="noprint" style="display:flex; gap:8px; margin:8px 0;">
  <form method="get" action="${pageContext.request.contextPath}/careshift/main/TaskSheetPdf.action">
    <input type="hidden" name="period" value="<c:out value='${sheet.period}'/>">
    <input type="hidden" name="assignee" value="<c:out value='${sheet.assignee}'/>">
    <input type="hidden" name="order" value="<c:out value='${sheet.order}'/>">
    <button type="submit" name="exportPdf" ${sheet.blocked ? 'disabled' : ''}>PDF出力</button>
  </form>
  <button type="button" name="print" onclick="window.print()" ${sheet.blocked ? 'disabled' : ''}>印刷</button>
</div>

<%-- 印刷にも使うプレビュー --%>
<h2><c:out value="${group.name}"/>　タスク分担表（<c:out value="${sheet.periodLabel}"/>・<c:out value="${sheet.orderLabel}"/>）</h2>
<c:if test="${not empty sheet.rows}">
  <table id="preview">
    <tr><th>タスク</th><th>担当者</th><th>期限</th><th>所要時間</th><th>状態</th></tr>
    <c:forEach var="r" items="${sheet.rows}">
      <tr>
        <td><c:out value="${r.title}"/></td>
        <td><c:out value="${r.assignee}"/></td>
        <td><c:out value="${r.due}"/></td>
        <td><c:out value="${r.duration}"/></td>
        <td><c:out value="${r.status}"/></td>
      </tr>
    </c:forEach>
  </table>
  <p><small>担当者がいないタスクは「担当なし」と表示しています。
    <c:if test="${sheet.maskedCount > 0}">医療情報を含む可能性のある内容 <c:out value="${sheet.maskedCount}"/>件は、内容を表示していません。</c:if></small></p>
</c:if>

<p class="noprint"><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
