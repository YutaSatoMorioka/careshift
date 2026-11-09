<%-- S04 招待から参加 / 設計書: S04_招待参加 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="家族グループへの招待"/></jsp:include>

<h1>家族グループへの招待</h1>
<p>次のグループに参加します。内容を確認して「参加する」を押してください。</p>

<dl>
  <dt>グループ名</dt><dd id="groupName"><c:out value="${invitation.groupName}"/></dd>
  <dt>招待者</dt><dd id="inviter"><c:out value="${invitation.inviterName}"/></dd>
  <dt>続柄・役割</dt><dd id="relationshipRole"><c:out value="${invitation.relationshipRole}"/></dd>
</dl>

<form method="post" action="${pageContext.request.contextPath}/careshift/InviteJoin.action">
  <input type="hidden" name="token" value="<c:out value='${token}'/>">
  <button type="submit" name="join">参加する</button>
</form>

<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
