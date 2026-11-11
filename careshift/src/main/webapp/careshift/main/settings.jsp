<%-- S20 設定 / 設計書: S20_設定 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="設定"/></jsp:include>

<h1>設定</h1>

<form method="post" action="${pageContext.request.contextPath}/careshift/main/SettingsSave.action">

  <%-- グループ名：主介護者だけに表示する --%>
  <c:if test="${member.primary}">
    <h2>グループ</h2>
    <div>
      <label for="groupName">グループ名（1〜30文字）</label>
      <input type="text" id="groupName" name="groupName" size="30" value="<c:out value='${groupName}'/>">
      <c:if test="${not empty errors['groupName']}"><span class="error"><c:out value="${errors['groupName']}"/></span></c:if>
    </div>
  </c:if>
  <c:if test="${!member.primary}">
    <h2>グループ</h2>
    <p>グループ名：<c:out value="${group.name}"/>（変更できるのは主介護者のみです）</p>
  </c:if>

  <h2>通知</h2>
  <p><label><input type="checkbox" id="appNotice" name="appNotice" checked disabled> アプリ内の依頼通知（常にオンです。変更できません）</label></p>
  <p><label><input type="checkbox" id="emailNotice" name="emailNotice" ${emailNotice ? 'checked' : ''}> メール通知</label></p>
  <p><label><input type="checkbox" id="reminder" name="reminder" ${reminder ? 'checked' : ''}> 期限リマインド（担当タスクの期限が近づいたら通知）</label></p>

  <button type="submit" name="save">設定を保存</button>
</form>

<%-- データ出力：主介護者だけに表示する --%>
<c:if test="${member.primary}">
  <h2>データの出力</h2>
  <p>家族とタスクの情報を、CSVファイルで出力します。個人情報が含まれるので、保管と共有に注意してください。</p>
  <form method="post" action="${pageContext.request.contextPath}/careshift/main/ExportCsv.action">
    <button type="submit" name="exportCsv">データを出力（CSV）</button>
  </form>
</c:if>

<h2>プラン</h2>
<p>現在のプラン：<strong><c:out value="${plan.planLabel}"/></strong>
   ／ <a href="${pageContext.request.contextPath}/careshift/main/Plan.action" id="planLink">プランの確認・変更</a></p>

<h2>ログアウト</h2>
<form method="post" action="${pageContext.request.contextPath}/careshift/Logout.action"
      onsubmit="return confirm('ログアウトします。よろしいですか？');">
  <button type="submit" name="logout">ログアウト</button>
</form>

<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
