<%-- S05 家族グループ作成・管理 / 設計書: 画面設計書_家族グループ管理 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="家族グループ"/></jsp:include>

<c:choose>
  <%-- ① グループ未所属：作成フォーム --%>
  <c:when test="${empty member}">
    <h1>家族グループを作成</h1>
    <p>あなたが主介護者として登録されます。作成後、家族を招待できます（上限6名）。</p>
    <form method="post" action="${pageContext.request.contextPath}/careshift/main/GroupSave.action">
      <div>
        <label for="groupName">グループ名（1〜30文字）</label>
        <input type="text" id="groupName" name="groupName" size="30" value="<c:out value='${groupName}'/>">
        <c:if test="${not empty errors['groupName']}">
          <span class="error"><c:out value="${errors['groupName']}"/></span>
        </c:if>
      </div>
      <button type="submit" name="save">作成・保存</button>
    </form>
  </c:when>

  <%-- ② 所属済み：名称変更（主介護者）と招待リンク発行 --%>
  <c:otherwise>
    <h1>家族グループ：<c:out value="${group.name}"/></h1>
    <p>メンバー <c:out value="${memberCount}"/> / <c:out value="${maxMembers}"/> 名
       ／ <a href="${pageContext.request.contextPath}/careshift/main/MemberList.action">メンバー管理へ</a></p>

    <c:if test="${member.primary}">
      <h2>グループ名の変更</h2>
      <form method="post" action="${pageContext.request.contextPath}/careshift/main/GroupSave.action">
        <div>
          <label for="groupName">グループ名（1〜30文字）</label>
          <input type="text" id="groupName" name="groupName" size="30" value="<c:out value='${groupName}'/>">
          <c:if test="${not empty errors['groupName']}">
            <span class="error"><c:out value="${errors['groupName']}"/></span>
          </c:if>
        </div>
        <button type="submit" name="save">作成・保存</button>
      </form>
    </c:if>

    <h2>家族を招待する</h2>
    <p>招待リンクは、<strong>1名分・7日間</strong>有効です。続柄を選んでリンクを発行し、家族に送ってください。
       参加すると、あなたと主介護者に通知されます。</p>
    <form method="post" action="${pageContext.request.contextPath}/careshift/main/InviteIssue.action">
      <label for="relationship">続柄</label>
      <select id="relationship" name="relationship">
        <option value="">指定しない</option>
        <c:forEach var="r" items="${relationships}">
          <option value="<c:out value='${r}'/>"><c:out value="${r}"/></option>
        </c:forEach>
      </select>
      <span>役割：家族等</span>
      <button type="submit" name="invite">招待リンク発行</button>
    </form>

    <c:if test="${not empty inviteLink}">
      <div class="guide">
        <p>招待リンク（この画面を閉じると再表示できません。コピーして家族に送ってください）</p>
        <input type="text" id="inviteLink" size="80" readonly value="<c:out value='${inviteLink}'/>" onclick="this.select()">
      </div>
    </c:if>
  </c:otherwise>
</c:choose>

<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>

