<%-- S02 確認メール送信の案内 --%>
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
        value="${mailFailed ? '確認メールを送信できませんでした' : '確認メールを送信しました'}"/>

    <jsp:param
        name="pageClass"
        value="auth-main"/>

</jsp:include>

<link
    rel="stylesheet"
    type="text/css"
    href="${pageContext.request.contextPath}/css/auth.css?v=20261125-4">

<section
    class="auth-card auth-register-done-card"
    aria-labelledby="register-done-title">

    <div class="auth-heading">

        <p class="auth-result-mark">

            <c:choose>

                <c:when test="${mailFailed}">
                    !
                </c:when>

                <c:otherwise>
                    ✓
                </c:otherwise>

            </c:choose>

        </p>

        <h1
            class="auth-title"
            id="register-done-title">

            <c:choose>

                <c:when test="${mailFailed}">
                    確認メールを送信できませんでした
                </c:when>

                <c:otherwise>
                    確認メールを送信しました
                </c:otherwise>

            </c:choose>

        </h1>

    </div>

    <c:choose>

        <c:when test="${mailFailed}">

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
                        アカウント登録は受け付けています
                    </strong>

                    <p>
                        確認メールを送信できませんでした。
                        時間をおいてログインすると、確認メールを再送します。
                    </p>

                </div>

            </div>

        </c:when>

        <c:otherwise>

            <div class="auth-done-message">

                <p>
                    次のメールアドレスへ確認メールを送信しました。
                </p>

                <strong class="auth-done-email">
                    <c:out value="${email}"/>
                </strong>

                <p>
                    メール内のリンクを24時間以内に開くと、本登録が完了します。
                </p>

            </div>

            <div class="auth-note">

                <p>
                    メールが届かない場合は、迷惑メールフォルダーを確認してください。
                </p>

                <p>
                    リンクの期限が切れた場合は、ログイン画面からログインすると確認メールを再送します。
                </p>

            </div>

        </c:otherwise>

    </c:choose>

    <a
        class="auth-button auth-button-link"
        href="${pageContext.request.contextPath}/login.jsp">
        ログインへ
    </a>

    <div class="auth-security-note">

        <p>
            心当たりのないメールを受け取った場合は、
            メール内のリンクを開かず、そのまま破棄してください。
        </p>

    </div>

</section>

<jsp:include page="/common/footer.jsp"/>