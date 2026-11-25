<%-- S01 ログイン / 設計書: 画面設計書_ログイン --%>
<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
%>
<%@ taglib
    prefix="c"
    uri="jakarta.tags.core"
%>

<%--
    初回表示ではURLパラメーターを使用する。

    LoginExecuteActionから入力エラーで戻った場合は、
    request属性inviteを使用する。
--%>
<c:set
    var="loginInvite"
    value="${not empty invite ? invite : param.invite}"/>

<jsp:include page="/common/header.jsp">

    <jsp:param
        name="title"
        value="ログイン"/>

    <jsp:param
        name="pageClass"
        value="auth-main"/>

</jsp:include>

<link
    rel="stylesheet"
    type="text/css"
    href="${pageContext.request.contextPath}/css/auth.css?v=20261125-3">

<section
    class="auth-card"
    aria-labelledby="login-title">

    <div class="auth-heading">

        <h1
            class="auth-title"
            id="login-title">
            ログイン
        </h1>

        <p class="auth-lead">
            メールアドレスとパスワードを入力してください。
        </p>

    </div>

    <%-- 招待リンクから開いた場合 --%>

    <c:if test="${not empty loginInvite}">

        <div
            class="auth-note"
            role="status">

            <p>
                家族グループへの招待リンクから開きました。
                ログインすると、招待内容の確認画面に進みます。
            </p>

            <p>
                まだ登録していない場合は、新規登録とメール確認を済ませてから、
                もう一度招待リンクを開いてください。
            </p>

        </div>

    </c:if>

    <%-- 認証失敗、制限中、メール未確認 --%>

    <c:if test="${not empty formError}">

        <div
            class="auth-alert"
            id="login-error"
            role="alert">

            <span
                class="auth-alert-icon"
                aria-hidden="true">
                !
            </span>

            <p>
                <c:out value="${formError}"/>
            </p>

        </div>

    </c:if>

    <form
        class="auth-form"
        id="login-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/LoginExecute.action"
        onsubmit="return loginStartSubmit(this);">

        <%-- 招待確認画面へ戻るためのトークン --%>

        <input
            type="hidden"
            name="invite"
            value="<c:out value='${loginInvite}'/>">

        <div class="auth-field">

            <label for="email">
                メールアドレス
            </label>

            <input
                class="auth-input${not empty errors['email'] ? ' is-error' : ''}"
                type="email"
                id="email"
                name="email"
                maxlength="254"
                placeholder="example@careshift.jp"
                autocomplete="email"
                inputmode="email"
                autofocus
                required
                aria-invalid="${not empty errors['email']}"
                aria-describedby="${not empty errors['email'] ? 'email-error' : 'email-help'}"
                value="<c:out value='${email}'/>">

            <c:if test="${not empty errors['email']}">

                <span
                    class="field-error"
                    id="email-error"
                    role="alert">

                    <span aria-hidden="true">
                        !
                    </span>

                    <c:out value="${errors['email']}"/>
                </span>

            </c:if>

            <p
                class="auth-field-help"
                id="email-help">
                Care Shiftへ登録したメールアドレスを入力してください。
            </p>

        </div>

        <div class="auth-field">

            <div class="auth-label-row">

                <label for="password">
                    パスワード
                </label>

                <a
                    class="auth-link"
                    id="reset"
                    href="${pageContext.request.contextPath}/password_reset.jsp">
                    パスワードを忘れた方
                </a>

            </div>

            <input
                class="auth-input${not empty errors['password'] ? ' is-error' : ''}"
                type="password"
                id="password"
                name="password"
                maxlength="128"
                placeholder="パスワードを入力"
                autocomplete="current-password"
                required
                aria-invalid="${not empty errors['password']}"
                aria-describedby="${not empty errors['password'] ? 'password-error' : 'password-help'}">

            <c:if test="${not empty errors['password']}">

                <span
                    class="field-error"
                    id="password-error"
                    role="alert">

                    <span aria-hidden="true">
                        !
                    </span>

                    <c:out value="${errors['password']}"/>
                </span>

            </c:if>

            <p
                class="auth-field-help"
                id="password-help">
                パスワードは画面やログへ保存されません。
            </p>

        </div>

        <button
            class="auth-button"
            id="login-button"
            type="submit"
            name="login">

            <span class="auth-button-label">
                ログイン
            </span>

        </button>

    </form>

    <div class="auth-switch">

        <span>
            アカウントをお持ちでない方
        </span>

        <a
            class="auth-link"
            href="${pageContext.request.contextPath}/register.jsp">
            新規登録はこちら
        </a>

    </div>

</section>

<script>
(function () {
    "use strict";

    window.loginStartSubmit = function (form) {
        if (!form.checkValidity()) {
            return true;
        }

        var button =
            document.getElementById(
                "login-button"
            );

        if (!button) {
            return true;
        }

        button.disabled = true;

        var label =
            button.querySelector(
                ".auth-button-label"
            );

        if (label) {
            label.textContent =
                "ログインしています...";
        }

        return true;
    };
})();
</script>

<jsp:include page="/common/footer.jsp"/>