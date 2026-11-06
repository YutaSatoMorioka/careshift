<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="運用管理"/></jsp:include>
<h1>Care Shift 運用管理</h1><p><a href="${pageContext.request.contextPath}/careshift/AdminLogout.action">管理者ログアウト</a></p>
<form method="post" action="${pageContext.request.contextPath}/careshift/admin/AdminBackup.action"><button>今すぐバックアップ</button></form>
<h2>バックアップ（最大7世代）</h2><ul><c:forEach var="b" items="${backups}"><li><c:out value="${b.fileName}"/></li></c:forEach></ul>
<h2>復元</h2><p class="alert">復元すると現在のDBを上書きします。実行前にTomcatを停止し、必要なバックアップを別の場所へコピーしてください。復元機能はH2ローカルDB専用です。</p>
<form method="post" action="${pageContext.request.contextPath}/careshift/admin/AdminRestore.action"><label>ファイル名<input name="file" required/></label><label>管理者パスワード<input type="password" name="adminPassword" required/></label><label><input type="checkbox" required/>現在のDBを上書きすることを理解しました</label><button>選択したバックアップを復元</button></form>
<h2>障害ログ（直近500件）</h2><table><thead><tr><th>日時</th><th>レベル</th><th>画面</th><th>内容</th></tr></thead><tbody><c:forEach var="l" items="${logs}"><tr><td><c:out value="${l.created_at}"/></td><td><c:out value="${l.level}"/></td><td><c:out value="${l.screen}"/></td><td><c:out value="${l.message}"/></td></tr></c:forEach></tbody></table>
<jsp:include page="/common/footer.jsp"/>

