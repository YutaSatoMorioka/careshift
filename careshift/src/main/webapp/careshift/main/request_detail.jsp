<%-- S13 依頼詳細・回答 / 設計書: 画面設計書_依頼回答 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="依頼詳細・回答"/></jsp:include>

<h1>依頼詳細・回答</h1>

<dl>
  <dt>種別</dt><dd><c:out value="${card.typeLabel}"/></dd>
  <dt>依頼内容</dt><dd id="requestContent" style="white-space:pre-wrap;"><c:out value="${card.content}"/></dd>
  <dt>依頼者</dt><dd><c:out value="${card.senderName}"/></dd>
  <dt>希望日時・期限</dt><dd><c:out value="${card.requestedAtLabel}"/></dd>
  <c:if test="${not empty card.durationMin}"><dt>所要時間</dt><dd><c:out value="${card.durationMin}"/>分</dd></c:if>
  <dt>状態</dt><dd id="requestStatus"><c:out value="${card.statusLabel}"/></dd>
  <c:if test="${not empty card.taskId}">
    <dt>関連タスク</dt>
    <dd><a href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${card.taskId}">タスクを見る</a></dd>
  </c:if>
</dl>

<c:choose>
  <%-- 送信者：各宛先の回答状況と取り下げ --%>
  <c:when test="${isSender}">
    <h2>宛先と回答</h2>
    <table id="recipientList">
      <tr><th>宛先</th><th>回答</th><th>代替案</th><th>回答日時</th></tr>
      <c:forEach var="p" items="${card.recipients}">
        <tr>
          <td><c:out value="${p.name}"/></td>
          <td><c:out value="${p.responseLabel}"/></td>
          <td style="white-space:pre-wrap;"><c:out value="${p.alternative}"/></td>
          <td><c:out value="${p.respondedAtLabel}"/></td>
        </tr>
      </c:forEach>
    </table>
    <c:choose>
      <c:when test="${card.withdrawable}">
        <form method="post" action="${pageContext.request.contextPath}/careshift/main/RequestWithdraw.action"
              onsubmit="return confirm('この依頼を取り下げます。宛先の家族にも通知されます。よろしいですか？');">
          <input type="hidden" name="id" value="${card.id}">
          <button type="submit" name="withdraw">依頼を取り下げる</button>
        </form>
      </c:when>
      <c:otherwise><p class="error"><c:out value="${card.withdrawBlockedReason}"/></p></c:otherwise>
    </c:choose>
  </c:when>

  <%-- 宛先：回答フォーム。回答できない依頼は理由を表示する --%>
  <c:otherwise>
    <c:choose>
      <c:when test="${not canAnswer}">
        <p class="alert" id="blockedReason"><c:out value="${blockedReason}"/></p>
      </c:when>
      <c:otherwise>
        <h2>回答</h2>
        <%-- 「担当する」は確認してから（担当設定と依頼者への通知が行われる） --%>
        <c:if test="${confirmAccept}">
          <div class="guide" id="confirmAccept">
            <p>「担当する」を選ぶと、あなたが担当として設定され、依頼者に通知されます。よろしいですか？</p>
          </div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/careshift/main/RequestRespond.action">
          <input type="hidden" name="id" value="${card.id}">
          <c:if test="${confirmAccept}"><input type="hidden" name="confirm" value="1"></c:if>
          <div>
            <c:forEach var="r" items="${responses}">
              <label><input type="radio" name="response" value="<c:out value='${r.key}'/>" ${r.key == selected ? 'checked' : ''}> <c:out value="${r.value}"/></label>
            </c:forEach>
            <c:if test="${not empty errors['response']}"><br><span class="error"><c:out value="${errors['response']}"/></span></c:if>
          </div>
          <div>
            <label for="alternative">代替案（300文字以内。「別タスクなら可能」の場合は必須）</label><br>
            <textarea id="alternative" name="alternative" rows="3" cols="60" maxlength="300"><c:out value="${alternative}"/></textarea>
            <c:if test="${not empty errors['alternative']}"><br><span class="error"><c:out value="${errors['alternative']}"/></span></c:if>
          </div>
          <button type="submit" name="submit">${confirmAccept ? 'はい、引き受けて回答する' : '回答を送信'}</button>
        </form>
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>

<p><a href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=${isSender ? 'sent' : 'received'}">依頼一覧へ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
