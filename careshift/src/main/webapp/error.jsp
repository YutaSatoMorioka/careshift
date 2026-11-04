<%-- エラー画面（FrontController で例外が起きたときの転送先）。詳細は画面に出さずコンソールに出力する --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="エラー"/></jsp:include>

<h1>エラーが発生しました</h1>
<p>処理中に問題が発生しました。時間をおいて、もう一度お試しください。</p>
<p><a href="${pageContext.request.contextPath}/login.jsp">ログイン画面へ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
