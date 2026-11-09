<%-- S16 家族メンバー / 設計書: 画面設計書_メンバー管理 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="家族メンバー"/></jsp:include>

<h1>家族メンバー：<c:out value="${group.name}"/></h1>
<p>メンバー <c:out value="${members.size()}"/> / <c:out value="${maxMembers}"/> 名</p>
<c:if test="${!member.primary}">
  <p>自分の「続柄」と「連絡先」だけ変更できます。その他の変更は主介護者が行います。</p>
</c:if>

<%-- 行ごとのフォームは、セルをまたげないため form 属性で入力欄と結び付ける --%>
<table id="members">
  <tr>
    <th>表示名</th><th>続柄</th><th>役割</th><th>連絡先</th><th>操作</th>
  </tr>
  <c:forEach var="m" items="${members}">
    <c:set var="canEdit" value="${member.primary or m.userId == member.userId}"/>
    <tr>
      <td>
        <c:out value="${m.displayName}"/>
        <c:if test="${m.userId == member.userId}">（あなた）</c:if>
      </td>
      <td>
        <select name="relationship" form="save-${m.userId}" ${canEdit ? '' : 'disabled'}>
          <option value="">未設定</option>
          <c:forEach var="r" items="${relationships}">
            <option value="<c:out value='${r}'/>" ${r == m.relationship ? 'selected' : ''}><c:out value="${r}"/></option>
          </c:forEach>
        </select>
      </td>
      <td>
        <c:choose>
          <c:when test="${member.primary}">
            <select name="role" form="save-${m.userId}">
              <option value="PRIMARY" ${m.primary ? 'selected' : ''}>主介護者</option>
              <option value="FAMILY" ${m.primary ? '' : 'selected'}>家族等</option>
            </select>
          </c:when>
          <c:otherwise>
            <c:if test="${m.primary}"><span class="tag">主介護者</span></c:if>
            <c:if test="${!m.primary}">家族等</c:if>
          </c:otherwise>
        </c:choose>
      </td>
      <td>
        <input type="text" name="contact" size="30" maxlength="100" form="save-${m.userId}"
               value="<c:out value='${m.contact}'/>" ${canEdit ? '' : 'disabled'}>
      </td>
      <td>
        <c:if test="${canEdit}">
          <form id="save-${m.userId}" method="post" action="${pageContext.request.contextPath}/careshift/main/MemberSave.action">
            <input type="hidden" name="userId" value="${m.userId}">
            <button type="submit" name="save">保存</button>
          </form>
        </c:if>
        <c:if test="${member.primary and !m.primary}">
          <form method="post" action="${pageContext.request.contextPath}/careshift/main/MemberSetPrimary.action"
                onsubmit="return confirm('この人を主介護者に設定しますか？（あなたは家族等になります）');">
            <input type="hidden" name="userId" value="${m.userId}">
            <button type="submit" name="setPrimary">主介護者に設定</button>
          </form>
        </c:if>
      </td>
    </tr>
  </c:forEach>
</table>

<p><a href="${pageContext.request.contextPath}/careshift/main/Group.action">招待リンクの発行・グループ名の変更</a></p>
<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
