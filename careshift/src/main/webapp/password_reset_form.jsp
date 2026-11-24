<%-- S03 新しいパスワードの入力（メール内リンクから表示） --%>
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
        value="新しいパスワードの設定"/>

    <jsp:param
        name="pageClass"
        value="auth-main"/>

</jsp:include>

<section
    class="auth-card"
    aria-labelledby="password-reset-form-title">

    <div class="auth-heading">

        <h1
            class="auth-title"
            id="password-reset-form-title">
            新しいパスワードの設定
        </h1>

        <p class="auth-lead">
            Care Shiftで今後使用する新しいパスワードを入力してください。
        </p>

    </div>

    <div class="auth-note">

        <p>
            パスワードは8文字以上で設定してください。
        </p>

        <p>
            ほかのサービスで使用しているパスワードの使い回しは避けてください。
        </p>

    </div>

    <form
        class="auth-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/PasswordResetExecute.action">

        <%--
            再設定メールに含まれていたトークン。

            トークンはPasswordResetExecuteActionで再検証される。
            パスワード検証エラー後も同じトークンを引き継ぐ。
        --%>
        <input
            type="hidden"
            name="token"
            value="<c:out value='${token}'/>">

        <div class="auth-field">

            <label for="password">
                新しいパスワード
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
                autofocus
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
                8文字以上で、第三者に推測されにくいパスワードを設定してください。
            </p>

        </div>

        <div class="auth-field">

            <label for="passwordConfirm">
                新しいパスワード（確認）
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
                aria-describedby="${not empty errors['passwordConfirm'] ? 'password-confirm-error' : 'password-confirm-help'}">

            <c:if test="${not empty errors['passwordConfirm']}">

                <span
                    class="field-error"
                    id="password-confirm-error"
                    role="alert">

                    <span aria-hidden="true">
                        !
                    </span>

                    <c:out value="${errors['passwordConfirm']}"/>
                </span>

            </c:if>

            <p
                class="auth-field-help"
                id="password-confirm-help">
                確認のため、新しいパスワードをもう一度入力してください。
            </p>

        </div>

        <div class="auth-password-guidance">

            <strong>
                安全なパスワードの例
            </strong>

            <ul>
                <li>
                    8文字以上にする
                </li>

                <li>
                    名前、誕生日、電話番号などをそのまま使用しない
                </li>

                <li>
                    他サービスと同じパスワードを使用しない
                </li>
            </ul>

        </div>

        <button
            class="auth-button"
            type="submit"
            name="save"
            id="password-reset-submit">

            パスワードを変更
        </button>

    </form>

    <div class="auth-security-note">

        <p>
            再設定リンクは1時間有効で、一度だけ使用できます。
            パスワード変更が完了すると、このリンクは使用済みになります。
        </p>

    </div>

    <div class="auth-switch">

        <span>
            再設定を中止する場合
        </span>

        <a
            class="auth-link"
            href="${pageContext.request.contextPath}/login.jsp">
            ログインへ戻る
        </a>

    </div>

</section>

<script>
(function () {
    "use strict";

    var form = document.querySelector(".auth-form");
    var password = document.getElementById("password");
    var passwordConfirm = document.getElementById("passwordConfirm");
    var submitButton = document.getElementById("password-reset-submit");

    if (!form || !password || !passwordConfirm || !submitButton) {
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
        function () {
            validatePasswordMatch();

            if (!form.checkValidity()) {
                return;
            }

            submitButton.disabled = true;
            submitButton.textContent = "変更しています...";
        }
    );
})();
</script>

<jsp:include page="/common/footer.jsp"/>