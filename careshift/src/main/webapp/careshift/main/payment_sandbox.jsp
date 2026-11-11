<%-- 【試験用】決済サービスの代わりの画面 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="決済サービス（試験用）"/></jsp:include>

<h1>決済サービス（試験用の画面）</h1>
<div class="guide">
  <p>これは、決済サービスの代わりに置いた<strong>試験用の画面</strong>です。実際のお支払いは発生せず、カード情報の入力もありません。
     本番では、外部の決済サービスの画面に置き換えます。</p>
</div>
<p>お支払い金額：<strong><c:out value="${amount}"/>円（月額）</strong></p>

<form method="post" action="${pageContext.request.contextPath}/careshift/main/PaymentSandboxComplete.action">
  <input type="hidden" name="token" value="<c:out value='${token}'/>">
  <button type="submit" name="result" value="success">決済を成功させる</button>
  <button type="submit" name="result" value="failure">決済を失敗させる</button>
  <button type="submit" name="result" value="cancel">キャンセルする</button>
</form>

<jsp:include page="/common/footer.jsp"/>
