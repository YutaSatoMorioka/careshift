<%-- S15 休息レポート / 設計書: 画面設計書_休息レポート --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="休息レポート"/></jsp:include>

<h1>休息レポート：<c:out value="${monthLabel}"/></h1>

<div class="noprint" style="display:flex; gap:8px; align-items:center;">
  <form method="get" action="${pageContext.request.contextPath}/careshift/main/RestReport.action">
    <input type="hidden" name="targetMonth" value="<c:out value='${prevMonth}'/>"><button type="submit">前月</button>
  </form>
  <form method="get" action="${pageContext.request.contextPath}/careshift/main/RestReport.action">
    <label for="targetMonth">対象年月</label>
    <input type="month" id="targetMonth" name="targetMonth" value="<c:out value='${targetMonth}'/>">
    <button type="submit">表示</button>
  </form>
  <form method="get" action="${pageContext.request.contextPath}/careshift/main/RestReport.action">
    <input type="hidden" name="targetMonth" value="<c:out value='${nextMonth}'/>"><button type="submit">翌月</button>
  </form>
  <button type="button" name="exportPdf" onclick="window.print()">PDF出力（印刷）</button>
</div>
<p class="noprint"><small>「PDF出力」を押すと印刷画面が開きます。送信先を「PDFに保存」にすると、PDFファイルとして保存できます。</small></p>

<h2>集計（確定した休息）</h2>
<div id="restChart">
  <p>取得回数：<strong id="confirmedCount"><c:out value="${confirmedCount}"/></strong> 回　／　合計時間：<strong id="totalMinutes"><c:out value="${totalMinutesLabel}"/></strong></p>
  <c:if test="${upcomingCount > 0}"><p><small>うち、まだ開始前の確定予定が <c:out value="${upcomingCount}"/> 件含まれます。</small></p></c:if>
  <p><small>実績に含めないもの：調整中 <c:out value="${adjustingCount}"/> 件／取消 <c:out value="${cancelledCount}"/> 件</small></p>

  <c:if test="${not empty confirmed}">
    <table>
      <tr><th>日付</th><th>時間</th><th>休息時間</th><th>担当</th></tr>
      <c:forEach var="p" items="${confirmed}">
        <tr>
          <td><c:out value="${p.dateLabel}"/></td><td><c:out value="${p.timeRange}"/></td>
          <td><c:out value="${p.durationLabel}"/></td><td><c:out value="${p.coveredName}"/></td>
        </tr>
      </c:forEach>
    </table>
  </c:if>
  <c:if test="${empty confirmed}"><p class="guide">この月に確定した休息はありません。</p></c:if>
</div>

<h2>直近6か月の推移（合計時間）</h2>
<table id="trend" style="width:100%;">
  <c:forEach var="s" items="${stats}">
    <tr>
      <td style="width:9em;"><c:out value="${s.label}"/></td>
      <td><div class="bar" style="width:${s.percent}%; min-width:${s.minutes > 0 ? '4px' : '0'};"></div></td>
      <td style="width:14em;"><c:out value="${s.count}"/>回／<c:out value="${s.minutesLabel}"/></td>
    </tr>
  </c:forEach>
</table>

<p class="noprint"><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
