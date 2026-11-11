<%-- S21 プラン / 設計書: S21_プラン --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="プラン"/></jsp:include>

<h1>プラン</h1>

<%-- 決済の結果（成功／失敗） --%>
<c:if test="${not empty paymentResult}">
  <p class="${paymentOk ? 'success' : 'alert'}" id="paymentResult"><c:out value="${paymentResult}"/></p>
</c:if>

<p>現在のプラン：<strong id="currentPlan"><c:out value="${plan.planLabel}"/></strong></p>
<c:if test="${plan.paid}">
  <p id="planDate">
    <c:choose>
      <c:when test="${plan.cancelRequested}">無料へ戻す手続き済みです。<strong><c:out value="${plan.nextDate}"/></strong> まで、有料機能を利用できます。</c:when>
      <c:otherwise>次回更新日：<strong><c:out value="${plan.nextDate}"/></strong>（月額 <c:out value="${price}"/>円）</c:otherwise>
    </c:choose>
  </p>
</c:if>

<table id="features">
  <tr><th>機能</th><th>無料</th><th>有料</th></tr>
  <c:forEach var="f" items="${features}">
    <tr><td><c:out value="${f[0]}"/></td><td><c:out value="${f[1]}"/></td><td><c:out value="${f[2]}"/></td></tr>
  </c:forEach>
</table>
<p><small>金額・機能は試験用の仮の内容です。</small></p>

<%-- プランの切替は主介護者だけに表示する --%>
<c:choose>
  <c:when test="${member.primary}">
    <c:if test="${!plan.paid}">
      <form method="get" action="${pageContext.request.contextPath}/careshift/main/PlanConfirm.action">
        <button type="submit" name="upgrade">有料へ切替</button>
      </form>
    </c:if>
    <c:if test="${plan.paid and !plan.cancelRequested}">
      <form method="post" action="${pageContext.request.contextPath}/careshift/main/PlanDowngrade.action"
            onsubmit="return confirm('無料プランへ戻します。次回更新日までは、有料機能を引き続き利用できます。よろしいですか？');">
        <button type="submit" name="downgrade">無料へ戻す</button>
      </form>
    </c:if>
  </c:when>
  <c:otherwise><p><small>プランを切り替えられるのは主介護者のみです。</small></p></c:otherwise>
</c:choose>

<p><a href="${pageContext.request.contextPath}/careshift/main/Settings.action">設定へ戻る</a>
 ／ <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
