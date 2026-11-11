<%-- NOTI001 通知一覧 / 設計書: 画面設計書_通知一覧 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="通知一覧"/>
    <jsp:param name="pageClass" value="notification-list-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/notification-list.css?v=20261111-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="notification-page-header">

    <div class="notification-page-heading">

        <p class="notification-page-eyebrow">
            NOTIFICATIONS
        </p>

        <h1 class="notification-page-title">
            通知一覧
        </h1>

        <p class="notification-page-description">
            家族からの依頼、回答、休息予定、タスク期限などのお知らせを確認できます。
        </p>

    </div>

    <div class="notification-header-summary">

        <span
            class="notification-header-icon"
            aria-hidden="true">
            N
        </span>

        <span class="notification-header-summary-content">

            <span>
                未読通知
            </span>

            <strong>
                <c:out
                    value="${unreadCount}"
                    default="0"/>
                <small>件</small>
            </strong>

        </span>

    </div>

</section>

<%-- =======================================================
     絞り込みと一括操作
     ======================================================= --%>

<section
    class="notification-toolbar"
    aria-label="通知の絞り込みと操作">

    <form
        class="notification-filter-form"
        method="get"
        action="${pageContext.request.contextPath}/careshift/main/NotificationList.action">

        <div class="notification-filter-field">

            <label for="type">
                通知種別
            </label>

            <select
                class="notification-filter-select"
                id="type"
                name="type">

                <c:forEach
                    var="t"
                    items="${types}">

                    <option
                        value="<c:out value='${t.key}'/>"
                        ${t.key == type ? 'selected' : ''}>
                        <c:out value="${t.value}"/>
                    </option>

                </c:forEach>

            </select>

        </div>

        <button
            class="notification-button notification-button-filter"
            type="submit"
            name="filter">

            <span
                class="notification-button-icon"
                aria-hidden="true">
                絞
            </span>

            <span>
                絞り込む
            </span>

        </button>

        <c:if test="${not empty type}">

            <a
                class="notification-clear-filter"
                href="${pageContext.request.contextPath}/careshift/main/NotificationList.action">
                絞り込みを解除
            </a>

        </c:if>

    </form>

    <form
        class="notification-read-all-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/main/NotificationReadAll.action"
        onsubmit="return confirm('未読の通知をすべて既読にします。よろしいですか？');">

        <button
            class="notification-button notification-button-secondary"
            type="submit"
            name="markAllRead"
            ${unreadCount == 0 ? 'disabled' : ''}>

            <span
                class="notification-button-icon"
                aria-hidden="true">
                ✓
            </span>

            <span>
                すべて既読にする
            </span>

        </button>

    </form>

</section>

<%-- =======================================================
     現在の絞り込み
     ======================================================= --%>

<c:if test="${not empty type}">

    <div class="notification-active-filter">

        <span
            class="notification-active-filter-icon"
            aria-hidden="true">
            絞
        </span>

        <span>
            表示中の種別：
        </span>

        <strong>
            <c:out value="${types[type]}"/>
        </strong>

    </div>

</c:if>

<%-- =======================================================
     通知一覧
     ======================================================= --%>

<section
    class="notification-result-card"
    aria-labelledby="notification-result-title">

    <header class="notification-result-header">

        <div>

            <p class="notification-section-eyebrow">
                NOTIFICATION LIST
            </p>

            <h2
                class="notification-result-title"
                id="notification-result-title">
                お知らせ
            </h2>

        </div>

        <div class="notification-result-count">

            <strong>
                <c:choose>
                    <c:when test="${empty notifications}">
                        0
                    </c:when>

                    <c:otherwise>
                        <c:out value="${notifications.size()}"/>
                    </c:otherwise>
                </c:choose>
            </strong>

            <span>
                件
            </span>

        </div>

    </header>

    <c:choose>

        <%-- =================================================
             読み込み失敗
             ================================================= --%>

        <c:when test="${loadError}">

            <div
                class="notification-error-state"
                id="loadError"
                role="alert">

                <span
                    class="notification-error-icon"
                    aria-hidden="true">
                    !
                </span>

                <h3 class="notification-error-title">
                    通知を読み込めませんでした
                </h3>

                <p class="notification-error-description">
                    通信状態を確認してから、もう一度お試しください。
                </p>

                <a
                    class="notification-button notification-button-secondary"
                    href="${pageContext.request.contextPath}/careshift/main/NotificationList.action?type=${type}">
                    再試行する
                </a>

            </div>

        </c:when>

        <%-- =================================================
             空状態
             ================================================= --%>

        <c:when test="${empty notifications}">

            <div
                class="notification-empty-state"
                id="empty">

                <span
                    class="notification-empty-icon"
                    aria-hidden="true">
                    ✓
                </span>

                <h3 class="notification-empty-title">
                    通知はありません
                </h3>

                <p class="notification-empty-description">

                    <c:choose>

                        <c:when test="${not empty type}">
                            選択した通知種別に該当する通知はありません。
                            絞り込みを解除すると、すべての通知を確認できます。
                        </c:when>

                        <c:otherwise>
                            新しい通知が届くと、ここに表示されます。
                        </c:otherwise>

                    </c:choose>

                </p>

                <c:if test="${not empty type}">

                    <a
                        class="notification-button notification-button-secondary"
                        href="${pageContext.request.contextPath}/careshift/main/NotificationList.action">
                        すべての通知を表示
                    </a>

                </c:if>

            </div>

        </c:when>

        <%-- =================================================
             通知あり
             ================================================= --%>

        <c:otherwise>

            <div
                class="notification-card-list"
                id="notificationList">

                <c:forEach
                    var="n"
                    items="${notifications}">

                    <article class="notification-item-card${n.unread ? ' is-unread' : ' is-read'}">

                        <div class="notification-item-marker">

                            <span
                                class="notification-type-icon notification-type-${n.type}"
                                aria-hidden="true">

                                <c:choose>

                                    <c:when test="${n.type == 'REQUEST'}">
                                        依
                                    </c:when>

                                    <c:when test="${n.type == 'ANSWER'}">
                                        回
                                    </c:when>

                                    <c:when test="${n.type == 'REST'}">
                                        休
                                    </c:when>

                                    <c:when test="${n.type == 'DEADLINE'}">
                                        期
                                    </c:when>

                                    <c:when test="${n.type == 'TASK'}">
                                        T
                                    </c:when>

                                    <c:when test="${n.type == 'GROUP'}">
                                        家
                                    </c:when>

                                    <c:otherwise>
                                        N
                                    </c:otherwise>

                                </c:choose>

                            </span>

                        </div>

                        <div class="notification-item-content">

                            <div class="notification-item-top">

                                <div class="notification-item-labels">

                                    <span class="notification-type-badge">
                                        <c:out
                                            value="${n.typeLabel}"
                                            default="その他"/>
                                    </span>

                                    <c:choose>

                                        <c:when test="${n.unread}">

                                            <span class="notification-unread-badge">
                                                未読
                                            </span>

                                        </c:when>

                                        <c:otherwise>

                                            <span class="notification-read-badge">
                                                既読
                                            </span>

                                        </c:otherwise>

                                    </c:choose>

                                </div>

                                <time class="notification-item-date">

                                    <span
                                        class="notification-date-icon"
                                        aria-hidden="true">
                                        日
                                    </span>

                                    <c:out
                                        value="${n.createdAtLabel}"
                                        default="日時不明"/>

                                </time>

                            </div>

                            <a
                                class="notification-message-link"
                                href="${pageContext.request.contextPath}/careshift/main/NotificationOpen.action?id=${n.id}">

                                <span class="notification-message">
                                    <c:out value="${n.message}"/>
                                </span>

                                <span
                                    class="notification-open-arrow"
                                    aria-hidden="true">
                                    &rsaquo;
                                </span>

                            </a>

                            <p class="notification-item-help">

                                <c:choose>

                                    <c:when test="${n.unread}">
                                        通知を開くと既読になり、関連する画面へ移動します。
                                    </c:when>

                                    <c:otherwise>
                                        この通知は確認済みです。選択すると関連する画面へ移動します。
                                    </c:otherwise>

                                </c:choose>

                            </p>

                        </div>

                        <div class="notification-item-actions">

                            <a
                                class="notification-open-button"
                                href="${pageContext.request.contextPath}/careshift/main/NotificationOpen.action?id=${n.id}">

                                <span>
                                    開く
                                </span>

                                <span aria-hidden="true">
                                    &rsaquo;
                                </span>

                            </a>

                            <c:if test="${n.unread}">

                                <form
                                    class="notification-read-form"
                                    method="post"
                                    action="${pageContext.request.contextPath}/careshift/main/NotificationRead.action">

                                    <input
                                        type="hidden"
                                        name="id"
                                        value="${n.id}">

                                    <input
                                        type="hidden"
                                        name="type"
                                        value="<c:out value='${type}'/>">

                                    <button
                                        class="notification-read-button"
                                        type="submit"
                                        name="markRead">
                                        既読にする
                                    </button>

                                </form>

                            </c:if>

                        </div>

                    </article>

                </c:forEach>

            </div>

            <footer class="notification-result-footer">

                <span
                    class="notification-result-footer-icon"
                    aria-hidden="true">
                    i
                </span>

                <p>
                    最新の100件まで表示します。
                    古い通知は一覧に表示されない場合があります。
                </p>

            </footer>

        </c:otherwise>

    </c:choose>

</section>

<%-- =======================================================
     下部ナビゲーション
     ======================================================= --%>

<nav
    class="notification-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="notification-back-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            ホームへ戻る
        </span>

    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>