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
        <c:out value="${param.title}" default="Care Shift"/> | Care Shift
    </title>

	<link
	    rel="stylesheet"
	    href="${pageContext.request.contextPath}/css/style.css?v=20261111-11">
</head>

<body>

<header class="site-header">

    <a
        class="site-logo"
        href="${pageContext.request.contextPath}/index.jsp"
        aria-label="Care Shift トップページ">

        <span
            class="logo-mark"
            aria-hidden="true">
            C
        </span>

        <span>Care Shift</span>
    </a>

    <div class="header-actions">

        <c:choose>

            <c:when test="${not empty sessionScope.user}">

                <button
                    class="menu-toggle"
                    type="button"
                    aria-label="メニューを開く"
                    aria-expanded="false"
                    aria-controls="global-navigation">

                    <span class="menu-toggle-line"></span>
                    <span class="menu-toggle-line"></span>
                    <span class="menu-toggle-line"></span>
                </button>

                <nav
                    class="global-nav"
                    id="global-navigation"
                    aria-label="メインメニュー"
                    aria-hidden="true">

                    <div class="global-nav-header">

                        <div>
                            <span class="global-nav-label">
                                ログインユーザー
                            </span>

                            <strong class="global-nav-user">
                                <c:out
                                    value="${sessionScope.user.displayName}"
                                    default="ログイン中"/>
                                さん
                            </strong>
                        </div>

                        <button
                            class="menu-close"
                            type="button"
                            aria-label="メニューを閉じる">
                            &times;
                        </button>

                    </div>

                    <div
                        class="global-nav-divider"
                        aria-hidden="true">
                    </div>

                    <a
                        class="global-nav-link"
                        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

                        <span class="nav-icon" aria-hidden="true">H</span>
                        <span>ホーム</span>
                    </a>

                    <a
                        class="global-nav-link"
                        href="${pageContext.request.contextPath}/careshift/main/Calendar.action">

                        <span class="nav-icon" aria-hidden="true">C</span>
                        <span>カレンダー</span>
                    </a>

                    <a
                        class="global-nav-link"
                        href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

                        <span class="nav-icon" aria-hidden="true">T</span>
                        <span>タスク</span>
                    </a>

                    <a
                        class="global-nav-link"
                        href="${pageContext.request.contextPath}/careshift/main/RequestList.action">

                        <span class="nav-icon" aria-hidden="true">R</span>
                        <span>依頼</span>
                    </a>

                    <a
                        class="global-nav-link"
                        id="notifications"
                        href="${pageContext.request.contextPath}/careshift/main/NotificationList.action">

                        <span class="nav-icon" aria-hidden="true">N</span>
                        <span>通知</span>

                        <c:if test="${not empty unreadCount and unreadCount > 0}">
                            <span
                                class="badge"
                                aria-label="未読通知件数">
                                <c:out value="${unreadCount}"/>
                            </span>
                        </c:if>
                    </a>

                    <a
                        class="global-nav-link"
                        href="${pageContext.request.contextPath}/careshift/main/MemberList.action">

                        <span class="nav-icon" aria-hidden="true">M</span>
                        <span>メンバー</span>
                    </a>

                    <div
                        class="global-nav-divider"
                        aria-hidden="true">
                    </div>

                    <a
                        class="global-nav-link"
                        href="${pageContext.request.contextPath}/careshift/main/Burden.action">

                        <span class="nav-icon" aria-hidden="true">B</span>
                        <span>負担状況</span>
                    </a>

                    <a
                        class="global-nav-link"
                        href="${pageContext.request.contextPath}/careshift/main/Rest.action">

                        <span class="nav-icon" aria-hidden="true">休</span>
                        <span>休息管理</span>
                    </a>

                    <a
                        class="global-nav-link"
                        href="${pageContext.request.contextPath}/careshift/main/Columns.action">

                        <span class="nav-icon" aria-hidden="true">記</span>
                        <span>コラム</span>
                    </a>

                    <a
                        class="global-nav-link"
                        href="${pageContext.request.contextPath}/careshift/main/Settings.action">

                        <span class="nav-icon" aria-hidden="true">S</span>
                        <span>設定</span>
                    </a>

                    <div
                        class="global-nav-divider"
                        aria-hidden="true">
                    </div>

                    <a
                        class="global-nav-link global-nav-logout"
                        href="${pageContext.request.contextPath}/careshift/Logout.action"
                        onclick="return confirm('ログアウトします。よろしいですか？');">

                        <span class="nav-icon" aria-hidden="true">L</span>
                        <span>ログアウト</span>
                    </a>

                </nav>

                <div
                    class="menu-overlay"
                    aria-hidden="true">
                </div>

            </c:when>

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

<main class="<c:out value='${param.pageClass}' default='app-main'/>">

    <c:if test="${not empty sessionScope.flash}">

        <p class="success" role="status">
            <c:out value="${sessionScope.flash}"/>
        </p>

        <c:remove
            var="flash"
            scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.flashError}">

        <p class="page-alert" role="alert">
            <c:out value="${sessionScope.flashError}"/>
        </p>

        <c:remove
            var="flashError"
            scope="session"/>
    </c:if>