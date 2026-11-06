<%-- S12 送信前プレビュー --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="依頼内容の確認"/></jsp:include>

<h1>依頼内容の確認</h1>
<p>次の内容で家族に送信します。よろしければ「送信」を押してください。</p>

<c:if test="${not empty sendError}">
  <p class="alert" id="sendError"><c:out value="${sendError}"/></p>
</c:if>

<dl>
  <dt>依頼種別</dt><dd>タスク</dd>
  <dt>依頼内容</dt><dd id="previewContent" style="white-space:pre-wrap;"><c:out value="${form.content}"/></dd>
  <dt>希望日時・期限</dt><dd><c:out value="${form.requestedAtLabel}"/></dd>
  <dt>所要時間</dt><dd><c:out value="${empty form.duration ? '指定なし' : form.duration.concat('分')}"/></dd>
  <dt>依頼先</dt>
  <dd><c:forEach var="n" items="${recipientNames}" varStatus="st"><c:out value="${n}"/><c:if test="${!st.last}">、</c:if></c:forEach></dd>
</dl>

<%-- 送信・修正のどちらも、入力した値を hidden で引き継ぐ（送信に失敗しても下書きとして残る） --%>
<form method="post" style="display:inline" action="${pageContext.request.contextPath}/careshift/main/RequestSend.action">
  <jsp:include page="/common/request_hidden.jsp"/>
  <button type="submit" name="send">送信</button>
</form>
<form method="post" style="display:inline" action="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">
  <jsp:include page="/common/request_hidden.jsp"/>
  <button type="submit" name="back">修正する</button>
</form>

<jsp:include page="/common/footer.jsp"/>
