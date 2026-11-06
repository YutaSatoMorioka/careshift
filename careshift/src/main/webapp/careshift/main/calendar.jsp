<%-- S10 カレンダー / 設計書: 画面設計書_カレンダー --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="カレンダー"/></jsp:include>

<h1>カレンダー：<c:out value="${monthLabel}"/></h1>

<div style="display:flex; gap:8px; align-items:center;">
  <form method="get" action="${pageContext.request.contextPath}/careshift/main/Calendar.action">
    <input type="hidden" name="month" value="<c:out value='${prevMonth}'/>">
    <button type="submit" name="prev">前月</button>
  </form>
  <form method="get" action="${pageContext.request.contextPath}/careshift/main/Calendar.action">
    <label for="month">表示月</label>
    <input type="month" id="month" name="month" value="<c:out value='${month}'/>">
    <button type="submit">表示</button>
  </form>
  <form method="get" action="${pageContext.request.contextPath}/careshift/main/Calendar.action">
    <input type="hidden" name="month" value="<c:out value='${nextMonth}'/>">
    <button type="submit" name="next">翌月</button>
  </form>
</div>

<table id="calendar" style="width:100%; table-layout:fixed; margin-top:12px;">
  <tr>
    <c:forEach var="d" items="${weekdays}"><th><c:out value="${d}"/></th></c:forEach>
  </tr>
  <c:forEach var="week" items="${weeks}">
    <tr>
      <c:forEach var="cell" items="${week}">
        <td style="height:90px; ${cell.inMonth ? '' : 'background:#f4f4f4; color:#999;'} ${cell.today ? 'outline:2px solid #2f6f5e;' : ''}">
          <div><strong><c:out value="${cell.day}"/></strong></div>
          <c:forEach var="t" items="${cell.tasks}">
            <div style="font-size:0.85em; ${t.done ? 'text-decoration:line-through; color:#888;' : ''}">
              <a href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}"><c:out value="${t.title}"/></a>
              <br>
              <c:choose>
                <c:when test="${t.unassigned}"><span class="error">担当なし</span></c:when>
                <c:otherwise>担当: <c:out value="${t.assigneeName}"/></c:otherwise>
              </c:choose>
            </div>
          </c:forEach>
          <%-- 休息予定：調整中／確定／取消 --%>
          <c:forEach var="rp" items="${cell.rests}">
            <div style="font-size:0.85em; margin-top:4px; padding:2px 4px; border-left:4px solid ${rp.confirmed ? '#2f6f5e' : (rp.cancelled ? '#999' : '#c77d00')}; ${rp.cancelled ? 'color:#888; text-decoration:line-through;' : ''}">
              休息［<c:out value="${rp.statusLabel}"/>］<c:out value="${rp.timeRange}"/>
              <c:if test="${rp.confirmed}"><br>担当: <c:out value="${rp.coveredName}"/></c:if>
              <c:if test="${isPrimary and not rp.cancelled}">
                <form method="post" style="display:inline" action="${pageContext.request.contextPath}/careshift/main/RestCancel.action"
                      onsubmit="return confirm('この休息予定を取り消します。担当の家族がいれば通知されます。よろしいですか？');">
                  <input type="hidden" name="id" value="${rp.id}">
                  <input type="hidden" name="back" value="calendar">
                  <button type="submit" name="cancelRest" style="font-size:0.85em;">取消</button>
                </form>
              </c:if>
            </div>
          </c:forEach>
        </td>
      </c:forEach>
    </tr>
  </c:forEach>
</table>

<p>
  <a href="${pageContext.request.contextPath}/careshift/main/TaskList.action">タスク一覧へ</a>
  ／ <a href="${pageContext.request.contextPath}/careshift/main/TaskCreate.action">タスクを登録する</a>
</p>

<jsp:include page="/common/footer.jsp"/>
