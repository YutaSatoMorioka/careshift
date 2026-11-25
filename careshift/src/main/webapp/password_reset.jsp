<%-- S03 パスワード再設定（メール送信依頼） --%>
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
        value="パスワード再設定"/>

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
    aria-labelledby="password-reset-title">

    <div class="auth-heading">

        <h1
            class="auth-title"
            id="password-reset-title">
            パスワード再設定
        </h1>

        <p class="auth-lead">
            登録したメールアドレスを入力してください。
            パスワードを再設定するための手順をメールでお送りします。
        </p>

    </div>

    <div class="auth-note">

        <p>
            再設定メールが届かない場合は、
            迷惑メールフォルダーも確認してください。
        </p>

        <p>
            登録されていないメールアドレスを入力した場合も、
            アカウントの有無が分からないよう同じ完了案内を表示します。
        </p>

    </div>

    <form
        class="auth-form"
        id="password-reset-request-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/PasswordResetRequest.action"
        onsubmit="return passwordResetRequestStart(this);">

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
                Care Shiftの新規登録で使用したメールアドレスを入力してください。
            </p>

        </div>

        <button
            class="auth-button"
            id="password-reset-request-submit"
            type="submit"
            name="sendReset">

            <span class="auth-button-label">
                再設定メールを送信
            </span>

        </button>

    </form>

    <div class="auth-security-note">

        <p>
            メール内の再設定リンクは1時間有効で、一度だけ使用できます。
            パスワードや認証コードをメールで返信する必要はありません。
        </p>

    </div>

    <div class="auth-switch">

        <a
            class="auth-link"
            id="login"
            href="${pageContext.request.contextPath}/login.jsp">
            ログインへ戻る
        </a>

    </div>

</section>

<script>
(function () {
    "use strict";

    window.passwordResetRequestStart = function (form) {
        if (!form.checkValidity()) {
            return true;
        }

        var button =
            document.getElementById(
                "password-reset-request-submit"
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
                "送信しています...";
        }

        return true;
    };
})();
</script>

<jsp:include page="/common/footer.jsp"/>