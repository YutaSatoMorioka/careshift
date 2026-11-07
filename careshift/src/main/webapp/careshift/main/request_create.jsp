<%-- S12 依頼カード作成 / 設計書: 画面設計書_依頼カード作成 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="依頼カード作成"/></jsp:include>

<h1>依頼カード作成</h1>

<c:choose>
  <c:when test="${empty others}">
    <div class="guide">
      <p>依頼を送れる家族がまだいません（宛先は自分以外の家族です）。</p>
      <p><a href="${pageContext.request.contextPath}/careshift/main/Group.action">家族を招待する</a></p>
    </div>
  </c:when>
  <c:otherwise>
    <form method="post" action="${pageContext.request.contextPath}/careshift/main/RequestPreview.action">
      <input type="hidden" name="taskId" value="<c:out value='${form.taskId}'/>">
      <div>
        <label for="requestType">依頼種別</label>
        <select id="requestType" name="requestType">
          <c:forEach var="t" items="${types}">
            <option value="<c:out value='${t.key}'/>" ${t.key == form.requestType ? 'selected' : ''}><c:out value="${t.value}"/></option>
          </c:forEach>
        </select>
        <c:if test="${not empty errors['requestType']}"><span class="error"><c:out value="${errors['requestType']}"/></span></c:if>
        <small>（休息の依頼は、主介護者が「休息リクエスト」画面から送ります）</small>
      </div>
      <div>
        <label for="content">依頼内容（500文字以内・必須）</label><br>
        <textarea id="content" name="content" rows="5" cols="60" maxlength="500"><c:out value="${form.content}"/></textarea>
        <c:if test="${not empty errors['content']}"><br><span class="error"><c:out value="${errors['content']}"/></span></c:if>
      </div>
      <div>
        <label for="requestedAt">希望日時・期限（必須・現在より後）</label>
        <input type="datetime-local" id="requestedAt" name="requestedAt" value="<c:out value='${form.requestedAt}'/>">
        <c:if test="${not empty errors['requestedAt']}"><span class="error"><c:out value="${errors['requestedAt']}"/></span></c:if>
      </div>
      <div>
        <label for="duration">所要時間（分・任意）</label>
        <input type="number" id="duration" name="duration" min="5" max="1440" size="5" value="<c:out value='${form.duration}'/>">
        <c:if test="${not empty errors['duration']}"><span class="error"><c:out value="${errors['duration']}"/></span></c:if>
      </div>
      <fieldset>
        <legend>依頼先の家族（必須・複数選べます）</legend>
        <c:forEach var="m" items="${others}">
          <label>
            <input type="checkbox" name="recipients" value="${m.userId}" ${form.recipientIds.contains(m.userId) ? 'checked' : ''}>
            <c:out value="${m.displayName}"/>
          </label>
        </c:forEach>
        <c:if test="${not empty errors['recipients']}"><br><span class="error"><c:out value="${errors['recipients']}"/></span></c:if>
      </fieldset>
      <button type="submit" name="preview">内容を確認する</button>
    </form>
  </c:otherwise>
</c:choose>

<p><a href="${pageContext.request.contextPath}/careshift/main/RequestList.action">依頼一覧へ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
