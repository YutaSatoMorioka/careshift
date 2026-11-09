<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        <c:if test="${not empty param.title}">
            <c:out value="${param.title}"/> |
        </c:if>
        Care Shift
    </title>

    <link
        rel="stylesheet"
        href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>

<header class="site-header">

    <a
        class="site-logo"
        href="${pageContext.request.contextPath}/index.jsp"
        aria-label="Care Shift トップページ">

        <span
            class="logo-mark"
            aria-hidden="true">C</span>

        <span>Care Shift</span>
    </a>

    <div class="header-user">

        <%-- ログイン済みの場合 --%>
        <c:if test="${not empty sessionScope.user}">

            <span class="header-user-name">
                <c:out
                    value="${sessionScope.user.displayName}"
                    default="ログイン中"/>
                さん
            </span>

            <a
                class="logout-link"
                href="${pageContext.request.contextPath}/careshift/Logout.action">
                ログアウト
            </a>

        </c:if>

        <%-- 未ログインの場合 --%>
        <c:if test="${empty sessionScope.user}">

            <a
                class="header-login-link"
                href="${pageContext.request.contextPath}/login.jsp">
                ログイン
            </a>

        </c:if>

    </div>

</header>