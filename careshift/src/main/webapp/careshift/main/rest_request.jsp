<%-- S14 休息リクエスト / 設計書: 画面設計書_休息リクエスト（主介護者専用） --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="休息リクエスト"/></jsp:include>

<h1>休息リクエスト</h1>
<div class="guide">
  <p>休みたい日時を登録して、家族に協力を依頼します。<strong>休息を保証する機能ではなく、家族との調整を始めるための予定</strong>です。
     家族の誰かが引き受けると「確定」、全員が難しい場合は「調整中」のまま再依頼できます。</p>
</div>

<c:if test="${not empty errors['send']}"><p class="alert"><c:out value="${errors['send']}"/></p></c:if>

<c:choose>
  <c:when test="${empty others}">
    <p class="guide">協力を依頼できる家族がまだいません。<a href="${pageContext.request.contextPath}/careshift/main/Group.action">家族を招待する</a></p>
  </c:when>
  <c:otherwise>
    <form method="post" action="${pageContext.request.contextPath}/careshift/main/RestRequestSend.action">
      <div>
        <label for="restDate">希望日（必須・未来）</label>
        <input type="date" id="restDate" name="restDate" value="<c:out value='${form.restDate}'/>">
        <c:if test="${not empty errors['restDate']}"><span class="error"><c:out value="${errors['restDate']}"/></span></c:if>
      </div>
      <div>
        <label for="startTime">開始時刻（必須）</label>
        <input type="time" id="startTime" name="startTime" value="<c:out value='${form.startTime}'/>">
        <c:if test="${not empty errors['startTime']}"><span class="error"><c:out value="${errors['startTime']}"/></span></c:if>
        <label for="endTime">終了時刻（必須・開始より後）</label>
        <input type="time" id="endTime" name="endTime" value="<c:out value='${form.endTime}'/>">
        <c:if test="${not empty errors['endTime']}"><span class="error"><c:out value="${errors['endTime']}"/></span></c:if>
      </div>
      <div>
        <label for="restContent">希望内容（300文字以内・必須）</label><br>
        <textarea id="restContent" name="restContent" rows="4" cols="60" maxlength="300"><c:out value="${form.restContent}"/></textarea>
        <c:if test="${not empty errors['restContent']}"><br><span class="error"><c:out value="${errors['restContent']}"/></span></c:if>
        <br><small>文章が思い浮かばないときは、そのまま手で入力できます（<a href="${pageContext.request.contextPath}/careshift/main/AiDraft.action">AIで下書きを作る</a>こともできます）。</small>
      </div>
      <fieldset>
        <legend>協力依頼先（必須・複数選べます）</legend>
        <c:forEach var="m" items="${others}">
          <label>
            <input type="checkbox" name="recipients" value="${m.userId}" ${form.recipientIds.contains(m.userId) ? 'checked' : ''}>
            <c:out value="${m.displayName}"/>
          </label>
        </c:forEach>
        <c:if test="${not empty errors['recipients']}"><br><span class="error"><c:out value="${errors['recipients']}"/></span></c:if>
      </fieldset>
      <button type="submit" name="send">依頼送信</button>
    </form>
  </c:otherwise>
</c:choose>

<h2>休息予定の一覧</h2>
<c:choose>
  <c:when test="${empty rows}"><p id="empty">休息予定はまだありません。</p></c:when>
  <c:otherwise>
    <table id="restList">
      <tr><th>日時</th><th>内容</th><th>状態</th><th>回答状況</th><th>操作</th></tr>
      <c:forEach var="row" items="${rows}">
        <tr>
          <td><c:out value="${row.plan.dateLabel}"/><br><c:out value="${row.plan.timeRange}"/>（<c:out value="${row.plan.durationLabel}"/>）</td>
          <td style="white-space:pre-wrap;"><c:out value="${row.plan.content}"/></td>
          <td>
            <c:out value="${row.plan.statusLabel}"/>
            <c:if test="${row.plan.confirmed}"><br><small>担当: <c:out value="${row.plan.coveredName}"/></small></c:if>
          </td>
          <td>
            <c:choose>
              <c:when test="${empty row.latest}">依頼なし</c:when>
              <c:otherwise>
                <c:forEach var="p" items="${row.latest.recipients}" varStatus="st">
                  <c:out value="${p.name}"/>（<c:out value="${p.responseLabel}"/>）<c:if test="${!st.last}"><br></c:if>
                </c:forEach>
                <br><small>依頼: <c:out value="${row.latest.statusLabel}"/></small>
              </c:otherwise>
            </c:choose>
            <%-- 全員が難しい場合：調整中のまま、相談先の案内 --%>
            <c:if test="${row.showGuidance}">
              <div class="guide" id="guidance">
                <small>全員が難しいとの回答でした。予定は「調整中」のままです。再依頼のほか、ケアマネジャーや地域包括支援センター、
                  ショートステイの利用など、外部の相談先も検討してみてください。</small>
              </div>
            </c:if>
          </td>
          <td>
            <c:if test="${row.canRerequest}">
              <form method="post" action="${pageContext.request.contextPath}/careshift/main/RestReRequest.action">
                <input type="hidden" name="restId" value="${row.plan.id}">
                <c:forEach var="m" items="${others}">
                  <label><input type="checkbox" name="recipients" value="${m.userId}"> <c:out value="${m.displayName}"/></label>
                </c:forEach>
                <button type="submit" name="rerequest">再依頼</button>
              </form>
            </c:if>
            <c:if test="${row.canCancel}">
              <form method="post" action="${pageContext.request.contextPath}/careshift/main/RestCancel.action"
                    onsubmit="return confirm('この休息予定を取り消します。担当の家族がいれば通知されます。よろしいですか？');">
                <input type="hidden" name="id" value="${row.plan.id}">
                <button type="submit" name="cancel">取消</button>
              </form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>

<p><a href="${pageContext.request.contextPath}/careshift/main/RestReport.action">休息レポートを見る</a>
 ／ <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
