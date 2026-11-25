<%-- S02 新規登録 / 設計書: S02_新規登録 --%>
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
        value="新規登録"/>

    <jsp:param
        name="pageClass"
        value="auth-main"/>

</jsp:include>

<link
    rel="stylesheet"
    type="text/css"
    href="${pageContext.request.contextPath}/css/auth.css?v=20261125-4">

<section
    class="auth-card auth-register-card"
    aria-labelledby="register-title">

    <div class="auth-heading">

        <h1
            class="auth-title"
            id="register-title">
            新規登録
        </h1>

        <p class="auth-lead">
            表示名、メールアドレス、パスワードを入力してください。
            確認メールのリンクを開くと本登録が完了します。
        </p>

    </div>

    <c:if test="${alreadyRegistered}">

        <div
            class="auth-alert"
            role="alert">

            <span
                class="auth-alert-icon"
                aria-hidden="true">
                !
            </span>

            <div class="auth-alert-content">

                <strong>
                    このメールアドレスは登録済みです
                </strong>

                <p>
                    ログインするか、パスワードの再設定をお試しください。
                </p>

                <div class="auth-alert-links">

                    <a
                        class="auth-link"
                        href="${pageContext.request.contextPath}/login.jsp">
                        ログインする
                    </a>

                    <a
                        class="auth-link"
                        href="${pageContext.request.contextPath}/password_reset.jsp">
                        パスワードを再設定する
                    </a>

                </div>

            </div>

        </div>

    </c:if>

    <form
        class="auth-form"
        id="register-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/RegisterExecute.action">

        <div class="auth-field">

            <label for="displayName">
                表示名
            </label>

            <input
                class="auth-input${not empty errors['displayName'] ? ' is-error' : ''}"
                type="text"
                id="displayName"
                name="displayName"
                minlength="1"
                maxlength="30"
                placeholder="例：大原 太郎"
                autocomplete="name"
                autofocus
                required
                aria-invalid="${not empty errors['displayName']}"
                aria-describedby="${not empty errors['displayName'] ? 'displayName-error' : 'displayName-help'}"
                value="<c:out value='${displayName}'/>">

            <c:if test="${not empty errors['displayName']}">

                <span
                    class="field-error"
                    id="displayName-error"
                    role="alert">

                    <span aria-hidden="true">
                        !
                    </span>

                    <c:out value="${errors['displayName']}"/>
                </span>

            </c:if>

            <p
                class="auth-field-help"
                id="displayName-help">
                家族グループ内で表示される名前です。30文字以内で入力してください。
            </p>

        </div>

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
                本人確認メールとパスワード再設定に使用します。
            </p>

        </div>

        <div class="auth-field">

            <label for="password">
                パスワード
            </label>

            <input
                class="auth-input${not empty errors['password'] ? ' is-error' : ''}"
                type="password"
                id="password"
                name="password"
                minlength="8"
                maxlength="128"
                placeholder="8文字以上で入力"
                autocomplete="new-password"
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
                8文字以上で、他サービスと異なるパスワードを設定してください。
            </p>

        </div>

        <div class="auth-field">

            <label for="passwordConfirm">
                パスワード（確認）
            </label>

            <input
                class="auth-input${not empty errors['passwordConfirm'] ? ' is-error' : ''}"
                type="password"
                id="passwordConfirm"
                name="passwordConfirm"
                minlength="8"
                maxlength="128"
                placeholder="同じパスワードをもう一度入力"
                autocomplete="new-password"
                required
                aria-invalid="${not empty errors['passwordConfirm']}"
                aria-describedby="${not empty errors['passwordConfirm'] ? 'passwordConfirm-error' : 'passwordConfirm-help'}">

            <c:if test="${not empty errors['passwordConfirm']}">

                <span
                    class="field-error"
                    id="passwordConfirm-error"
                    role="alert">

                    <span aria-hidden="true">
                        !
                    </span>

                    <c:out value="${errors['passwordConfirm']}"/>
                </span>

            </c:if>

            <p
                class="auth-field-help"
                id="passwordConfirm-help">
                確認のため、同じパスワードを入力してください。
            </p>

        </div>

        <button
            class="auth-button"
            id="register-button"
            type="submit"
            name="register">

            <span class="auth-button-label">
                アカウントを登録
            </span>

        </button>

    </form>

    <div class="auth-security-note">

        <p>
            登録後、入力したメールアドレスへ確認メールを送信します。
            メール内のリンクは24時間有効です。
        </p>

    </div>

    <div class="auth-switch">

        <span>
            すでにアカウントをお持ちの方
        </span>

        <a
            class="auth-link"
            id="login"
            href="${pageContext.request.contextPath}/login.jsp">
            ログインへ
        </a>

    </div>

</section>

<script>
(function () {
    "use strict";

    var form =
        document.getElementById(
            "register-form"
        );

    var password =
        document.getElementById(
            "password"
        );

    var passwordConfirm =
        document.getElementById(
            "passwordConfirm"
        );

    var button =
        document.getElementById(
            "register-button"
        );

    if (
        !form
        || !password
        || !passwordConfirm
        || !button
    ) {
        return;
    }

    function validatePasswordMatch() {
        if (
            passwordConfirm.value !== ""
            && password.value !== passwordConfirm.value
        ) {
            passwordConfirm.setCustomValidity(
                "パスワードが一致しません"
            );
        } else {
            passwordConfirm.setCustomValidity("");
        }
    }

    password.addEventListener(
        "input",
        validatePasswordMatch
    );

    passwordConfirm.addEventListener(
        "input",
        validatePasswordMatch
    );

    form.addEventListener(
        "submit",
        function (event) {
            validatePasswordMatch();

            if (!form.checkValidity()) {
                event.preventDefault();
                form.reportValidity();
                return;
            }

            button.disabled = true;

            var label =
                button.querySelector(
                    ".auth-button-label"
                );

            if (label) {
                label.textContent =
                    "登録しています...";
            }
        }
    );
})();
</script>

<jsp:include page="/common/footer.jsp"/>