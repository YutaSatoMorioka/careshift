<%-- S11 依頼一覧 / 設計書: S11_依頼一覧 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="依頼一覧"/></jsp:include>

<h1>依頼一覧</h1>

<%-- 依頼区分（requestTab）：受け取った依頼／送った依頼 --%>
<p id="requestTab">
  <c:choose>
    <c:when test="${tab == 'received'}"><strong>受け取った依頼</strong></c:when>
    <c:otherwise><a href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=received">受け取った依頼</a></c:otherwise>
  </c:choose>
  ｜
  <c:choose>
    <c:when test="${tab == 'sent'}"><strong>送った依頼</strong></c:when>
    <c:otherwise><a href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=sent">送った依頼</a></c:otherwise>
  </c:choose>
  ｜
  <a href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action" id="createRequest">依頼する</a>
</p>

<c:choose>
  <%-- 読み込み失敗：再試行 --%>
  <c:when test="${loadError}">
    <div class="alert" id="loadError">
      <p>依頼の読み込みに失敗しました。</p>
      <p><a href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=${tab}">再試行する</a></p>
    </div>
  </c:when>

  <%-- 0件：空状態と作成導線 --%>
  <c:when test="${empty requests}">
    <div class="guide" id="empty">
      <p>
        <c:choose>
          <c:when test="${tab == 'sent'}">送った依頼はまだありません。</c:when>
          <c:otherwise>受け取った依頼はまだありません。</c:otherwise>
        </c:choose>
      </p>
      <p><a href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">家族に依頼する</a></p>
    </div>
  </c:when>

  <c:otherwise>
    <table id="requestList">
      <tr>
        <th>種別</th><th>内容</th>
        <th><c:choose><c:when test="${tab == 'sent'}">宛先（回答）</c:when><c:otherwise>依頼者</c:otherwise></c:choose></th>
        <th>希望日時</th><th>状態</th><th>操作</th>
      </tr>
      <c:forEach var="r" items="${requests}">
        <tr>
          <td><c:out value="${r.typeLabel}"/></td>
          <td><a href="${pageContext.request.contextPath}/careshift/main/RequestDetail.action?id=${r.id}"><c:out value="${r.content.length() > 40 ? r.content.substring(0, 40).concat('…') : r.content}"/></a></td>
          <td>
            <c:choose>
              <c:when test="${tab == 'sent'}">
                <c:forEach var="p" items="${r.recipients}" varStatus="st">
                  <c:out value="${p.name}"/>（<c:out value="${p.responseLabel}"/>）<c:if test="${!st.last}">、</c:if>
                </c:forEach>
              </c:when>
              <c:otherwise><c:out value="${r.senderName}"/></c:otherwise>
            </c:choose>
          </td>
          <td><c:out value="${r.requestedAtLabel}"/></td>
          <td>
            <c:out value="${r.statusLabel}"/>
            <c:if test="${tab == 'received'}">
              <c:forEach var="p" items="${r.recipients}">
                <c:if test="${p.userId == sessionScope.user.id}"><br><small>あなたの回答：<c:out value="${p.responseLabel}"/></small></c:if>
              </c:forEach>
            </c:if>
          </td>
          <td>
            <a href="${pageContext.request.contextPath}/careshift/main/RequestDetail.action?id=${r.id}">${tab == 'sent' ? '詳細' : '詳細・回答'}</a>
            <c:if test="${tab == 'sent'}">
              <c:choose>
                <c:when test="${r.withdrawable}">
                  <form method="post" style="display:inline" action="${pageContext.request.contextPath}/careshift/main/RequestWithdraw.action"
                        onsubmit="return confirm('この依頼を取り下げます。宛先の家族にも通知されます。よろしいですか？');">
                    <input type="hidden" name="id" value="${r.id}">
                    <button type="submit" name="withdraw">取り下げ</button>
                  </form>
                </c:when>
                <c:otherwise><br><small class="error"><c:out value="${r.withdrawBlockedReason}"/></small></c:otherwise>
              </c:choose>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>

<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
