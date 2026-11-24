<%--
    共通結果メッセージ画面。

    tool.Action.showResult(...)から次の属性を受け取る。

    resultTitle
    resultMessage
    resultLinkPath
    resultLinkLabel
--%>
<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
%>
<%@ taglib
    prefix="c"
    uri="jakarta.tags.core"
%>

<jsp:include page="/common/header.jsp">

    <jsp:param
        name="title"
        value="${resultTitle}"/>

    <jsp:param
        name="pageClass"
        value="result-main"/>

</jsp:include>

<link
    rel="stylesheet"
    type="text/css"
    href="${pageContext.request.contextPath}/css/result.css?v=20261124-2">

<%--
    結果タイトルから画面の表示種別を判断する。
--%>
<c:set
    var="resultSuccess"
    value="${resultTitle.contains('完了')
        or resultTitle.contains('送信しました')
        or resultTitle.contains('再設定しました')
        or resultTitle.contains('参加しました')}"/>

<c:set
    var="resultWarning"
    value="${resultTitle.contains('期限')
        or resultTitle.contains('使用済み')
        or resultTitle.contains('使用できません')
        or resultTitle.contains('正しくありません')
        or resultTitle.contains('参加できません')}"/>

<section
    class="result-card
        ${resultSuccess ? 'is-success' : ''}
        ${resultWarning ? 'is-warning' : ''}
        ${not resultSuccess and not resultWarning ? 'is-information' : ''}"
    aria-labelledby="result-title">

    <div class="result-content">
        <h1
            class="result-title"
            id="result-title">
            <c:out value="${resultTitle}"/>
        </h1>

        <p class="result-message">
            <c:out value="${resultMessage}"/>
        </p>

        <c:if test="${resultWarning}">

            <div class="result-help">

                <p>
                    メール内のリンクを開いた場合は、URLが途中で切れていないか確認してください。
                    新しいメールを受け取った場合は、最も新しいメールに記載されたリンクを使用してください。
                </p>

            </div>

        </c:if>

        <div class="result-actions">

            <c:choose>

                <c:when test="${not empty resultLinkPath}">

                    <a
                        class="result-button result-button-primary"
                        href="${pageContext.request.contextPath}${resultLinkPath}">

                        <span>
                            <c:out
                                value="${resultLinkLabel}"
                                default="次へ"/>
                        </span>

                        <span aria-hidden="true">
                            &rsaquo;
                        </span>

                    </a>

                </c:when>

                <c:otherwise>

                    <a
                        class="result-button result-button-primary"
                        href="${pageContext.request.contextPath}/index.jsp">

                        <span>
                            トップへ戻る
                        </span>

                        <span aria-hidden="true">
                            &rsaquo;
                        </span>

                    </a>

                </c:otherwise>

            </c:choose>

        </div>

    </div>

</section>



<nav
    class="result-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="result-footer-link"
        href="${pageContext.request.contextPath}/index.jsp">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            Care Shiftトップへ
        </span>

    </a>

    <c:choose>

        <c:when test="${empty sessionScope.user}">

            <a
                class="result-footer-link"
                href="${pageContext.request.contextPath}/login.jsp">
                ログイン
            </a>

        </c:when>

        <c:otherwise>

            <a
                class="result-footer-link"
                href="${pageContext.request.contextPath}/careshift/main/Menu.action">
                ホーム
            </a>

        </c:otherwise>

    </c:choose>

</nav>

<jsp:include page="/common/footer.jsp"/>