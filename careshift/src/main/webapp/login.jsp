<%-- S01 ログイン / 設計書: 画面設計書_ログイン --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="ログイン"/>
</jsp:include>

<main class="login-main">
    <section class="login-card">

        <div class="login-heading">
            <p class="login-eyebrow">WELCOME BACK</p>

            <h1>ログイン</h1>

            <p class="login-description">
                メールアドレスとパスワードを入力してください。
            </p>
        </div>

        <%-- 認証失敗、アカウントロック、メール未確認時のメッセージ --%>
        <c:if test="${not empty formError}">
            <div class="alert alert-error" role="alert">
                <c:out value="${formError}"/>
            </div>
        </c:if>

        <%-- 招待リンク経由の案内 --%>
        <c:if test="${not empty param.invite}">
            <div class="alert alert-info">
                <p>
                    家族グループへの招待リンクから開きました。
                    ログインすると、招待の確認画面に進みます。
                </p>

                <p>
                    まだ登録していない場合は、新規登録とメール確認を
                    済ませてから、もう一度招待リンクを開いてください。
                </p>
            </div>
        </c:if>

        <form
            class="login-form"
            method="post"
            action="${pageContext.request.contextPath}/careshift/LoginExecute.action">

            <%-- 招待リンク経由の場合、ログイン後に招待画面へ戻す --%>
            <input
                type="hidden"
                name="invite"
                value="<c:out value='${param.invite}'/>">

            <div class="form-group">
                <label for="email">
                    メールアドレス
                </label>

                <input
                    class="form-input${not empty errors['email'] ? ' input-error' : ''}"
                    type="email"
                    id="email"
                    name="email"
                    value="<c:out value='${email}'/>"
                    placeholder="example@careshift.jp"
                    autocomplete="email"
                    inputmode="email"
                    required
                    <c:if test="${not empty errors['email']}">
                        aria-invalid="true"
                        aria-describedby="email-error"
                    </c:if>>

                <c:if test="${not empty errors['email']}">
                    <span
                        class="field-error"
                        id="email-error"
                        role="alert">
                        <c:out value="${errors['email']}"/>
                    </span>
                </c:if>
            </div>

            <div class="form-group">
                <div class="form-label-row">
                    <label for="password">
                        パスワード
                    </label>

                    <a
                        class="sub-link"
                        id="reset"
                        href="${pageContext.request.contextPath}/password_reset.jsp">
                        パスワードを忘れた方
                    </a>
                </div>

                <input
                    class="form-input${not empty errors['password'] ? ' input-error' : ''}"
                    type="password"
                    id="password"
                    name="password"
                    placeholder="パスワードを入力"
                    autocomplete="current-password"
                    required
                    <c:if test="${not empty errors['password']}">
                        aria-invalid="true"
                        aria-describedby="password-error"
                    </c:if>>

                <c:if test="${not empty errors['password']}">
                    <span
                        class="field-error"
                        id="password-error"
                        role="alert">
                        <c:out value="${errors['password']}"/>
                    </span>
                </c:if>
            </div>

            <button
                class="login-button"
                type="submit"
                name="login">
                ログイン
            </button>
        </form>

        <div class="signup-area">
            <span>アカウントをお持ちでない方</span>

            <a href="${pageContext.request.contextPath}/register.jsp">
                新規登録はこちら
            </a>
        </div>

    </section>
</main>

<jsp:include page="/common/footer.jsp"/>