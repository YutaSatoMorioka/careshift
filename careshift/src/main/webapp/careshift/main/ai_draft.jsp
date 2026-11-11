<%-- AI001 AI依頼下書き / 設計書: 画面設計書_AI依頼下書き --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="AI依頼下書き"/></jsp:include>

<h1>AI依頼下書き</h1>
<div class="guide">
  <p>相談したい内容を入力すると、家族に送る依頼文の<strong>下書き</strong>を作ります。
     AIは下書きを作るだけで、<strong>医療的なアドバイスや、誰が担当するかの決定はしません</strong>。
     病名・薬名などの医療情報は入力しないでください。入力した相談内容は、外部のAIサービスに送られます（保存はされません）。</p>
</div>
<p><small>今月の利用回数：<c:out value="${aiUsed}"/> / <c:out value="${aiLimit}"/> 回（<c:out value="${paid ? '有料プラン' : '無料プラン'}"/>）</small></p>

<form method="post" action="${pageContext.request.contextPath}/careshift/main/AiDraftGenerate.action">
  <div>
    <label for="prompt">相談内容（1000文字以内・必須）</label><br>
    <textarea id="prompt" name="prompt" rows="6" cols="70" maxlength="1000"><c:out value="${prompt}"/></textarea>
    <c:if test="${not empty errors['prompt']}"><br><span class="error" id="promptError"><c:out value="${errors['prompt']}"/></span></c:if>
  </div>
  <button type="submit" name="generate">下書きを作る</button>
</form>

<%-- 生成できなかったとき：手入力を案内する --%>
<c:if test="${unavailable}">
  <div class="alert" id="unavailable">
    <p>下書きを作れませんでした（回数は消費していません）。お手数ですが、依頼の内容を手で入力してください。</p>
    <form method="post" action="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">
      <input type="hidden" name="content" value="<c:out value='${prompt.length() > 500 ? prompt.substring(0, 500) : prompt}'/>">
      <button type="submit" name="manual">入力した内容を使って、手入力で依頼を作る</button>
    </form>
  </div>
</c:if>
<c:if test="${limitReached}">
  <div class="guide" id="limitReached">
    <form method="post" style="display:inline" action="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">
      <input type="hidden" name="content" value="<c:out value='${prompt.length() > 500 ? prompt.substring(0, 500) : prompt}'/>">
      <button type="submit" name="manual">手入力で依頼を作る</button>
    </form>
    <c:if test="${!paid}"> ／ <a href="${pageContext.request.contextPath}/careshift/main/Plan.action">プランを確認する</a></c:if>
  </div>
</c:if>

<%-- 下書き（確認・修正して、保存または依頼作成へ） --%>
<c:if test="${hasDraft}">
  <hr>
  <h2>下書き<c:if test="${generated}">（AIが作りました。内容を確認して、必要なら直してください）</c:if></h2>
  <form method="post" action="${pageContext.request.contextPath}/careshift/main/AiDraftSave.action">
    <div>
      <label for="content">依頼内容（500文字以内）</label><br>
      <textarea id="content" name="content" rows="6" cols="70" maxlength="500"><c:out value="${draftContent}"/></textarea>
      <c:if test="${not empty errors['content']}"><br><span class="error"><c:out value="${errors['content']}"/></span></c:if>
    </div>
    <div>
      <label for="requestedAt">希望日時（未定なら空のまま）</label>
      <input type="datetime-local" id="requestedAt" name="requestedAt" value="<c:out value='${draftRequestedAt}'/>">
      <c:if test="${not empty errors['requestedAt']}"><span class="error"><c:out value="${errors['requestedAt']}"/></span></c:if>
    </div>
    <div>
      <label for="duration">所要時間（分）</label>
      <input type="number" id="duration" name="duration" min="5" max="1440" value="<c:out value='${draftDuration}'/>">
      <c:if test="${not empty errors['duration']}"><span class="error"><c:out value="${errors['duration']}"/></span></c:if>
    </div>
    <button type="submit" name="saveDraft">修正して保存</button>
    <%-- 同じ入力欄の値を、そのまま依頼作成（S12）へ渡す --%>
    <button type="submit" name="createRequest" formaction="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">依頼作成へ</button>
  </form>
</c:if>

<p><a href="${pageContext.request.contextPath}/careshift/main/RequestList.action">依頼一覧へ</a>
 ／ <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
