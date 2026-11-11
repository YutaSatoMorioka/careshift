<%-- S01 ログイン / 設計書: 画面設計書_ログイン --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param
        name="title"
        value="ログイン"/>

    <jsp:param
        name="pageClass"
        value="auth-main"/>
</jsp:include>

<section class="auth-card">

    <div class="auth-heading">
        <h1 class="auth-title">
            ログイン
        </h1>

        <p class="auth-lead">
            メールアドレスとパスワードを入力してください。
        </p>
    </div>

    <%-- 招待リンクから開いた場合 --%>
    <c:if test="${not empty param.invite}">
        <div class="auth-note">
            <p>
                家族グループへの招待リンクから開きました。
                ログインすると、招待の確認画面に進みます。
            </p>

            <p>
                まだ登録していない場合は、新規登録とメール確認を済ませてから、
                もう一度招待リンクを開いてください。
            </p>
        </div>
    </c:if>

    <%-- 認証失敗、ロック、メール未確認のメッセージ --%>
    <c:if test="${not empty formError}">
        <div
            class="auth-alert"
            role="alert">
            <c:out value="${formError}"/>
        </div>
    </c:if>

    <form
        class="auth-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/LoginExecute.action">

        <%-- 招待画面へ戻るための値 --%>
        <input
            type="hidden"
            name="invite"
            value="<c:out value='${param.invite}'/>">

        <div class="auth-field">
            <label for="email">
                メールアドレス
            </label>

            <input
                class="auth-input${not empty errors['email'] ? ' is-error' : ''}"
                type="email"
                id="email"
                name="email"
                placeholder="example@careshift.jp"
                autocomplete="email"
                inputmode="email"
                autofocus
                required
                value="<c:out value='${email}'/>">

            <c:if test="${not empty errors['email']}">
                <span
                    class="field-error"
                    role="alert">
                    <c:out value="${errors['email']}"/>
                </span>
            </c:if>
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
                placeholder="パスワードを入力"
                autocomplete="current-password"
                required>

            <c:if test="${not empty errors['password']}">
                <span
                    class="field-error"
                    role="alert">
                    <c:out value="${errors['password']}"/>
                </span>
            </c:if>
        </div>

        <button
            class="auth-button"
            type="submit"
            name="login">
            ログイン
        </button>

    </form>

    <div class="auth-switch">
        <span>アカウントをお持ちでない方</span>
        <p>  </p>

        <a
            class="auth-link"
            href="${pageContext.request.contextPath}/register.jsp">
            新規登録はこちら
        </a>
    </div>

</section>

<jsp:include page="/common/footer.jsp"/>