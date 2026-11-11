<%-- S90 管理者 / 設計書: S90_管理者 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="管理者"/></jsp:include>

<h1>管理者</h1>

<h2>バックアップ</h2>
<c:if test="${!supported}">
  <p class="alert" id="unsupported">このデータベースでは、画面からのバックアップ・復元に対応していません（PostgreSQL などは、pg_dump などの方法で行ってください）。</p>
</c:if>
<p>自動バックアップは、毎日行われます（直近7世代を保存し、古いものから削除します）。</p>

<form method="post" action="${pageContext.request.contextPath}/careshift/main/AdminBackup.action">
  <button type="submit" name="backupNow" ${supported ? '' : 'disabled'}>今すぐバックアップ</button>
</form>

<table id="backupList">
  <tr><th>日時</th><th>種類</th><th>結果</th><th>サイズ</th></tr>
  <c:forEach var="h" items="${history}">
    <tr>
      <td><c:out value="${h.atLabel}"/></td>
      <td><c:out value="${h.kindLabel}"/></td>
      <td><c:choose>
        <c:when test="${h.ok}">成功</c:when>
        <c:otherwise><span class="error">失敗</span></c:otherwise>
      </c:choose></td>
      <td><c:out value="${h.sizeLabel}"/></td>
    </tr>
  </c:forEach>
  <c:if test="${empty history}"><tr><td colspan="4">バックアップの履歴はまだありません。</td></tr></c:if>
</table>

<h2>復元</h2>
<div class="alert">
  <p><strong>復元すると、現在のデータはすべて、選んだバックアップの内容に置き換わります。</strong>
     復元の直前の状態は、自動で退避します。復元が終わると、ログインし直してもらいます。</p>
</div>
<c:choose>
  <c:when test="${empty restorable}"><p id="noRestorable">復元できるバックアップがありません。</p></c:when>
  <c:otherwise>
    <form method="post" action="${pageContext.request.contextPath}/careshift/main/AdminRestore.action"
          onsubmit="return confirm('現在のデータを、選んだバックアップで上書きします。よろしいですか？');">
      <div>
        <label for="restoreGeneration">復元する世代</label>
        <select id="restoreGeneration" name="restoreGeneration">
          <c:forEach var="r" items="${restorable}">
            <option value="<c:out value='${r.fileName}'/>"><c:out value="${r.atLabel}"/>（<c:out value="${r.kindLabel}"/>・<c:out value="${r.sizeLabel}"/>）</option>
          </c:forEach>
        </select>
      </div>
      <div>
        <label for="password">管理者のパスワード（再認証）</label>
        <input type="password" id="password" name="password" size="30" autocomplete="off">
      </div>
      <div><label><input type="checkbox" name="confirm" value="1"> 現在のデータを上書きすることを確認しました</label></div>
      <button type="submit" name="restore" ${supported ? '' : 'disabled'}>復元する</button>
    </form>
  </c:otherwise>
</c:choose>

<h2>障害ログ</h2>
<p><small>90日分を保存しています。パスワードや相談文の本文は記録していません。</small></p>
<form method="get" action="${pageContext.request.contextPath}/careshift/main/Admin.action">
  <label for="logFrom">期間</label>
  <input type="date" id="logFrom" name="logFrom" value="<c:out value='${logFrom}'/>">
  〜 <input type="date" id="logTo" name="logTo" value="<c:out value='${logTo}'/>">
  <label for="logType">種類</label>
  <select id="logType" name="logType">
    <option value="">すべて</option>
    <c:forEach var="t" items="${logTypes}">
      <option value="<c:out value='${t}'/>" ${t == logType ? 'selected' : ''}><c:out value="${t}"/></option>
    </c:forEach>
  </select>
  <button type="submit" name="filter">絞り込む</button>
  <c:forEach var="e" items="${errors}"><span class="error"><c:out value="${e.value}"/></span></c:forEach>
</form>
<table id="logList">
  <tr><th>日時</th><th>種類</th><th>内容</th></tr>
  <c:forEach var="l" items="${logs}">
    <tr><td><c:out value="${l.atLabel}"/></td><td><c:out value="${l.type}"/></td><td><c:out value="${l.message}"/></td></tr>
  </c:forEach>
  <c:if test="${empty logs}"><tr><td colspan="3">該当するログはありません。</td></tr></c:if>
</table>

<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
