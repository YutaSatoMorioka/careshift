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
        href="${pageContext.request.contextPath}/css/style.css?v=20261111-2">
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

            <a
                class="header-link"
                href="${pageContext.request.contextPath}/careshift/main/Menu.action">
                ホーム
            </a>

            <a
                class="header-link"
                id="notifications"
                href="${pageContext.request.contextPath}/careshift/main/NotificationList.action">
                通知

                <c:if test="${unreadCount > 0}">
                    <span class="badge">
                        <c:out value="${unreadCount}"/>
                    </span>
                </c:if>
            </a>

            <span class="header-user-name">
                <c:out
                    value="${sessionScope.user.displayName}"
                    default="ログイン中"/>さん
            </span>

            <a
                class="logout-link"
                href="${pageContext.request.contextPath}/careshift/Logout.action"
                onclick="return confirm('ログアウトします。よろしいですか？');">
                ログアウト
            </a>

        </c:if>


    </div>

</header>

<main class="${param.pageClass}">

    <%-- 処理成功メッセージ --%>
    <c:if test="${not empty sessionScope.flash}">
        <p class="success" role="status">
            <c:out value="${sessionScope.flash}"/>
        </p>

        <c:remove
            var="flash"
            scope="session"/>
    </c:if>

    <%-- 処理失敗メッセージ --%>
    <c:if test="${not empty sessionScope.flashError}">
        <p class="page-alert" role="alert">
            <c:out value="${sessionScope.flashError}"/>
        </p>

        <c:remove
            var="flashError"
            scope="session"/>
    </c:if>