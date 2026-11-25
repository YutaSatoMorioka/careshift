<%--
    ERROR01 共通エラー画面。

    次の2経路から表示される。

    1. Action.showError(...)
       request属性errorMessageを使用する。

    2. web.xmlのerror-page
       jakarta.servlet.error.status_codeなどを使用する。

    画面へ表示しない情報:
    ・例外クラス名
    ・スタックトレース
    ・SQL
    ・パスワード
    ・APIキー
    ・認証トークン
    ・利用者の入力本文
--%>
<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isErrorPage="true"
%>
<%@ taglib
    prefix="c"
    uri="jakarta.tags.core"
%>

<%--
    web.xmlのerror-pageから転送された場合のHTTP状態コード。

    Action.showError(...)から通常forwardされた場合はnullになる。
--%>
<c:set
    var="httpStatus"
    value="${requestScope['jakarta.servlet.error.status_code']}"/>

<%-- HTTP状態に応じた画面タイトル --%>
<c:choose>

    <c:when test="${httpStatus == 404}">
        <c:set
            var="safeErrorTitle"
            value="ページが見つかりません"/>
    </c:when>

    <c:when test="${httpStatus == 403}">
        <c:set
            var="safeErrorTitle"
            value="このページを利用できません"/>
    </c:when>

    <c:when test="${httpStatus == 405}">
        <c:set
            var="safeErrorTitle"
            value="この操作方法は利用できません"/>
    </c:when>

    <c:otherwise>
        <c:set
            var="safeErrorTitle"
            value="エラーが発生しました"/>
    </c:otherwise>

</c:choose>

<%-- HTTP状態に応じた安全な説明文 --%>
<c:choose>

    <c:when test="${not empty errorMessage}">
        <c:set
            var="safeErrorMessage"
            value="${errorMessage}"/>
    </c:when>

    <c:when test="${httpStatus == 404}">
        <c:set
            var="safeErrorMessage"
            value="指定されたページは存在しないか、移動または削除された可能性があります。URLを確認してください。"/>
    </c:when>

    <c:when test="${httpStatus == 403}">
        <c:set
            var="safeErrorMessage"
            value="このページを表示する権限がありません。ログイン中のアカウントと所属グループを確認してください。"/>
    </c:when>

    <c:when test="${httpStatus == 405}">
        <c:set
            var="safeErrorMessage"
            value="このURLでは、実行された操作方法を受け付けていません。画面上のボタンやリンクから操作してください。"/>
    </c:when>

    <c:when test="${httpStatus == 500}">
        <c:set
            var="safeErrorMessage"
            value="処理中に問題が発生しました。入力内容はそのまま保存されていない可能性があります。時間をおいて、もう一度お試しください。"/>
    </c:when>

    <c:otherwise>
        <c:set
            var="safeErrorMessage"
            value="処理中に問題が発生しました。時間をおいて、もう一度お試しください。"/>
    </c:otherwise>

</c:choose>

<jsp:include page="/common/header.jsp">

    <jsp:param
        name="title"
        value="${safeErrorTitle}"/>

    <jsp:param
        name="pageClass"
        value="error-main"/>

</jsp:include>

<link
    rel="stylesheet"
    type="text/css"
    href="${pageContext.request.contextPath}/css/error.css?v=20261125-2">

<section
    class="error-card"
    aria-labelledby="error-title">

    <div class="error-visual">

        <span
            class="error-icon"
            aria-hidden="true">
            !
        </span>

    </div>

    <div class="error-content">

        <p class="error-eyebrow">

            <c:choose>

                <c:when test="${httpStatus == 404}">
                    PAGE NOT FOUND
                </c:when>

                <c:when test="${httpStatus == 403}">
                    ACCESS DENIED
                </c:when>

                <c:when test="${httpStatus == 405}">
                    METHOD NOT ALLOWED
                </c:when>

                <c:otherwise>
                    SOMETHING WENT WRONG
                </c:otherwise>

            </c:choose>

        </p>

        <h1
            class="error-title"
            id="error-title">
            <c:out value="${safeErrorTitle}"/>
        </h1>

        <p
            class="error-message"
            id="errorMessage">
            <c:out value="${safeErrorMessage}"/>
        </p>

        <c:if test="${not empty httpStatus}">

            <p class="error-status">
                エラーコード：
                <c:out value="${httpStatus}"/>
            </p>

        </c:if>

        <div class="error-guidance">

            <span
                class="error-guidance-icon"
                aria-hidden="true">
                i
            </span>

            <div>

                <h2>
                    次の操作をお試しください
                </h2>

                <ul>

                    <c:choose>

                        <c:when test="${httpStatus == 404}">

                            <li>
                                URLの入力内容を確認する
                            </li>

                            <li>
                                ホームまたはメニューから目的の画面を開き直す
                            </li>

                            <li>
                                古いブックマークの場合は新しい画面を登録し直す
                            </li>

                        </c:when>

                        <c:when test="${httpStatus == 403}">

                            <li>
                                正しいCare Shiftアカウントでログインしているか確認する
                            </li>

                            <li>
                                家族グループへの所属と役割を確認する
                            </li>

                            <li>
                                管理者画面の場合は管理者メール設定を確認する
                            </li>

                        </c:when>

                        <c:when test="${httpStatus == 405}">

                            <li>
                                ブラウザーのURL欄から直接操作しない
                            </li>

                            <li>
                                前の画面へ戻り、画面上のボタンを使用する
                            </li>

                            <li>
                                画面を再読み込みしてから操作し直す
                            </li>

                        </c:when>

                        <c:otherwise>

                            <li>
                                前の画面へ戻り、入力内容を確認してからもう一度実行する
                            </li>

                            <li>
                                ブラウザーを再読み込みする
                            </li>

                            <li>
                                改善しない場合は少し時間をおいて再度開く
                            </li>

                        </c:otherwise>

                    </c:choose>

                </ul>

            </div>

        </div>

        <div class="error-actions">

            <button
                class="error-button error-button-secondary"
                type="button"
                id="back"
                onclick="errorGoBack();">

                <span aria-hidden="true">
                    &lsaquo;
                </span>

                <span>
                    前の画面に戻る
                </span>

            </button>

            <c:choose>

                <c:when test="${not empty sessionScope.user}">

                    <a
                        class="error-button error-button-primary"
                        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

                        <span
                            class="error-button-icon"
                            aria-hidden="true">
                            H
                        </span>

                        <span>
                            ホームへ戻る
                        </span>

                    </a>

                </c:when>

                <c:otherwise>

                    <a
                        class="error-button error-button-primary"
                        href="${pageContext.request.contextPath}/login.jsp">

                        <span
                            class="error-button-icon"
                            aria-hidden="true">
                            L
                        </span>

                        <span>
                            ログインへ
                        </span>

                    </a>

                </c:otherwise>

            </c:choose>

        </div>

    </div>

</section>

<section
    class="error-support-note"
    aria-labelledby="error-support-title">

    <span
        class="error-support-icon"
        aria-hidden="true">
        安
    </span>

    <div>

        <h2 id="error-support-title">
            安全のため、エラーの詳細は画面に表示していません
        </h2>

        <p>
            パスワード、APIキー、認証トークン、相談内容、SQL、例外内容などの
            機密情報は表示されません。問題が続く場合は、発生日時と操作していた
            画面名を管理者へ伝えてください。
        </p>

    </div>

</section>

<nav
    class="error-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a href="${pageContext.request.contextPath}/index.jsp">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        Care Shiftトップへ
    </a>

    <c:choose>

        <c:when test="${not empty sessionScope.user}">

            <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">
                ホーム
            </a>

        </c:when>

        <c:otherwise>

            <a href="${pageContext.request.contextPath}/login.jsp">
                ログイン
            </a>

        </c:otherwise>

    </c:choose>

</nav>

<script>
(function () {
    "use strict";

    window.errorGoBack = function () {
        if (window.history.length > 1) {
            window.history.back();
            return;
        }

        <c:choose>

            <c:when test="${not empty sessionScope.user}">

                window.location.href =
                    "${pageContext.request.contextPath}/careshift/main/Menu.action";

            </c:when>

            <c:otherwise>

                window.location.href =
                    "${pageContext.request.contextPath}/login.jsp";

            </c:otherwise>

        </c:choose>
    };
})();
</script>

<jsp:include page="/common/footer.jsp"/>