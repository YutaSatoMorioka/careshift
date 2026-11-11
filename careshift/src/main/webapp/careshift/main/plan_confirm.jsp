<%-- S21 有料へ切替：金額・条件の確認 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="有料プランの確認"/></jsp:include>

<h1>有料プランの確認</h1>

<p>金額：<strong id="price">月額 <c:out value="${price}"/>円</strong></p>
<ul>
  <li>お申し込みの日から1か月ごとに、自動で更新されます（次回更新日は申し込み後の画面に表示されます）。</li>
  <li>「無料へ戻す」手続きをしても、次回更新日までは有料機能を利用できます。</li>
  <li>お支払いは外部の決済サービスの画面で行います。<strong>カード情報は、このアプリでは入力・保存しません。</strong></li>
  <li>決済が成功すると有料機能が使えるようになります。失敗・キャンセルの場合は、無料プランのままです。</li>
</ul>

<table id="features">
  <tr><th>機能</th><th>無料</th><th>有料</th></tr>
  <c:forEach var="f" items="${features}">
    <tr><td><c:out value="${f[0]}"/></td><td><c:out value="${f[1]}"/></td><td><c:out value="${f[2]}"/></td></tr>
  </c:forEach>
</table>

<form method="post" action="${pageContext.request.contextPath}/careshift/main/PlanCheckout.action">
  <button type="submit" name="checkout">決済サービスで申し込む</button>
</form>
<p><a href="${pageContext.request.contextPath}/careshift/main/Plan.action">やめて戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
