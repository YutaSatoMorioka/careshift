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
        <c:choose>
            <c:when test="${not empty param.title}">
                <c:out value="${param.title}"/> | Care Shift
            </c:when>
            <c:otherwise>
                Care Shift
            </c:otherwise>
        </c:choose>
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

        <span class="logo-mark" aria-hidden="true">
            C
        </span>

        <span>Care Shift</span>
    </a>

    <div class="header-user">

        <%-- userがセッションに存在する場合はログイン済み --%>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <span class="header-user-name">
                    ログイン中
                </span>

                <a
                    class="logout-link"
                    href="${pageContext.request.contextPath}/careshift/Logout.action">
                    ログアウト
                </a>
            </c:when>

            <%-- loginUserという名前で保存されている場合にも対応 --%>
            <c:when test="${not empty sessionScope.loginUser}">
                <span class="header-user-name">
                    ログイン中
                </span>

                <a
                    class="logout-link"
                    href="${pageContext.request.contextPath}/careshift/Logout.action">
                    ログアウト
                </a>
            </c:when>

            <%-- 未ログインの場合 --%>
            <c:otherwise>
                <a
                    class="header-login-link"
                    href="${pageContext.request.contextPath}/login.jsp">
                    ログイン
                </a>
            </c:otherwise>
        </c:choose>

    </div>
</header>