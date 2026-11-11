<%-- S06 ホーム / 設計書: 画面設計書_メインメニュー --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param
        name="title"
        value="ホーム"/>

    <jsp:param
        name="pageClass"
        value="dashboard-main"/>
</jsp:include>

<%-- =======================================================
     ページ上部
     ======================================================= --%>

<section class="dashboard-heading">

    <div>
        <p class="page-eyebrow">
            DASHBOARD
        </p>

        <h1 class="page-title">
            ホーム
        </h1>

        <p class="dashboard-welcome">
            ようこそ、

            <strong>
                <c:out
                    value="${sessionScope.user.displayName}"
                    default="ユーザー"/>
                さん
            </strong>
        </p>
    </div>

    <div class="dashboard-week">
        <span
            class="dashboard-week-icon"
            aria-hidden="true">
            週
        </span>

        <span>
            <c:choose>
                <c:when test="${not empty weekLabel}">
                    <c:out value="${weekLabel}"/>
                </c:when>

                <c:otherwise>
                    今週
                </c:otherwise>
            </c:choose>
        </span>
    </div>

</section>

<%-- =======================================================
     初回利用ガイド
     ======================================================= --%>

<c:if test="${sessionScope.guide}">

    <section
        class="dashboard-guide"
        aria-labelledby="first-guide-title">

        <div
            class="dashboard-guide-icon"
            aria-hidden="true">
            i
        </div>

        <div class="dashboard-guide-content">

            <h2
                class="dashboard-guide-title"
                id="first-guide-title">
                はじめてのガイド
            </h2>

            <ol class="dashboard-guide-list">
                <li>
                    「家族管理」で、メンバーと主介護者を確認しましょう。
                </li>

                <li>
                    「タスク一覧」で、担当を決めましょう。
                </li>

                <li>
                    困ったときは、「依頼する」で家族に相談できます。
                </li>
            </ol>

        </div>

    </section>

    <c:remove
        var="guide"
        scope="session"/>

</c:if>

<%-- =======================================================
     グループ未所属
     ======================================================= --%>

<c:if test="${empty member}">

    <section
        class="group-empty-card"
        aria-labelledby="group-empty-title">

        <div
            class="group-empty-icon"
            aria-hidden="true">
            G
        </div>

        <div class="group-empty-content">

            <h2
                class="group-empty-title"
                id="group-empty-title">
                家族グループに参加しましょう
            </h2>

            <p class="group-empty-description">
                まだ家族グループに所属していません。
                グループを作成するか、受け取った招待リンクから参加してください。
            </p>

            <div class="group-empty-actions">
                <a
                    class="button-primary"
                    id="family"
                    href="${pageContext.request.contextPath}/careshift/main/Group.action">
                    グループを作成する
                </a>
            </div>

        </div>

    </section>

</c:if>

<%-- =======================================================
     概要カード
     ======================================================= --%>

<section
    class="summary-grid"
    aria-label="ホーム概要">

    <a
        class="summary-card"
        href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

        <span
            class="summary-icon"
            aria-hidden="true">
            T
        </span>

        <span class="summary-content">

            <span class="summary-label">
                今週のタスク
            </span>

            <strong
                class="summary-value"
                id="weekTotal">

                <c:choose>
                    <c:when test="${weekError}">
                        -
                    </c:when>

                    <c:otherwise>
                        <c:out
                            value="${weekTotal}"
                            default="0"/>
                    </c:otherwise>
                </c:choose>

                <small>件</small>
            </strong>

        </span>

        <span
            class="summary-arrow"
            aria-hidden="true">
            &rsaquo;
        </span>

    </a>

    <a
        class="summary-card"
        href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

        <span
            class="summary-icon"
            aria-hidden="true">
            未
        </span>

        <span class="summary-content">

            <span class="summary-label">
                未対応のタスク
            </span>

            <strong
                class="summary-value"
                id="todoCount">

                <c:choose>
                    <c:when test="${weekError}">
                        -
                    </c:when>

                    <c:otherwise>
                        <c:out
                            value="${todoCount}"
                            default="0"/>
                    </c:otherwise>
                </c:choose>

                <small>件</small>
            </strong>

        </span>

        <span
            class="summary-arrow"
            aria-hidden="true">
            &rsaquo;
        </span>

    </a>

    <a
        class="summary-card"
        href="${pageContext.request.contextPath}/careshift/main/NotificationList.action">

        <span
            class="summary-icon"
            aria-hidden="true">
            N
        </span>

        <span class="summary-content">

            <span class="summary-label">
                未読の通知
            </span>

            <strong class="summary-value">

                <c:choose>
                    <c:when test="${notificationError}">
                        -
                    </c:when>

                    <c:otherwise>
                        <c:out
                            value="${unreadCount}"
                            default="0"/>
                    </c:otherwise>
                </c:choose>

                <small>件</small>
            </strong>

        </span>

        <span
            class="summary-arrow"
            aria-hidden="true">
            &rsaquo;
        </span>

    </a>

    <a
        class="summary-card"
        href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

        <span
            class="summary-icon"
            aria-hidden="true">
            完
        </span>

        <span class="summary-content">

            <span class="summary-label">
                今週の完了
            </span>

            <strong
                class="summary-value"
                id="doneCount">

                <c:choose>
                    <c:when test="${weekError}">
                        -
                    </c:when>

                    <c:otherwise>
                        <c:out
                            value="${doneCount}"
                            default="0"/>
                    </c:otherwise>
                </c:choose>

                <small>件</small>
            </strong>

        </span>

        <span
            class="summary-arrow"
            aria-hidden="true">
            &rsaquo;
        </span>

    </a>

</section>

<%-- =======================================================
     エラー表示
     ======================================================= --%>

<c:if test="${weekError}">

    <div
        class="dashboard-error"
        role="alert">

        <span>
            今週のタスク状況を読み込めませんでした。
        </span>

        <a
            href="${pageContext.request.contextPath}/careshift/main/Menu.action">
            再読み込み
        </a>

    </div>

</c:if>

<c:if test="${notificationError}">

    <div
        class="dashboard-error"
        role="alert">

        <span>
            通知を読み込めませんでした。
        </span>

        <a
            href="${pageContext.request.contextPath}/careshift/main/Menu.action">
            再読み込み
        </a>

    </div>

</c:if>

<%-- =======================================================
     メインダッシュボード
     ======================================================= --%>

<section class="dashboard-layout">

    <%-- 今週の担当タスク --%>
    <article class="dashboard-panel task-panel">

        <header class="panel-header">

            <div class="panel-title-group">

                <span
                    class="panel-icon"
                    aria-hidden="true">
                    T
                </span>

                <div>
                    <h2 class="panel-title">
                        今週の担当タスク
                    </h2>

                    <p class="panel-subtitle">
                        <c:choose>
                            <c:when test="${not empty weekLabel}">
                                <c:out value="${weekLabel}"/>
                            </c:when>

                            <c:otherwise>
                                今週
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>

            </div>

            <a
                class="panel-link"
                id="tasks"
                href="${pageContext.request.contextPath}/careshift/main/TaskList.action">
                すべて見る
                <span aria-hidden="true">
                    &rsaquo;
                </span>
            </a>

        </header>

        <c:if test="${overdueCount > 0}">

            <div
                class="overdue-alert"
                id="overdue"
                role="alert">

                <span
                    class="overdue-alert-icon"
                    aria-hidden="true">
                    !
                </span>

                <span>
                    期限を過ぎた未完了のタスクが
                    <strong>
                        <c:out value="${overdueCount}"/>
                        件
                    </strong>
                    あります。
                </span>

            </div>

        </c:if>

        <div
            class="task-dashboard-list"
            id="myTasks">

            <c:choose>

                <c:when test="${empty member}">

                    <div class="empty-state">

                        <span
                            class="empty-state-icon"
                            aria-hidden="true">
                            G
                        </span>

                        <p>
                            家族グループへ参加すると、担当タスクを確認できます。
                        </p>

                        <a
                            class="button-secondary"
                            href="${pageContext.request.contextPath}/careshift/main/Group.action">
                            家族グループを確認
                        </a>

                    </div>

                </c:when>

                <c:when test="${weekError}">

                    <div class="empty-state">

                        <span
                            class="empty-state-icon"
                            aria-hidden="true">
                            !
                        </span>

                        <p>
                            タスクを読み込めませんでした。
                        </p>

                        <a
                            class="button-secondary"
                            href="${pageContext.request.contextPath}/careshift/main/Menu.action">
                            再読み込み
                        </a>

                    </div>

                </c:when>

                <c:when test="${empty myTasks}">

                    <div class="empty-state">

                        <span
                            class="empty-state-icon"
                            aria-hidden="true">
                            ✓
                        </span>

                        <p>
                            今週期限の担当タスクはありません。
                        </p>

                        <a
                            class="button-secondary"
                            href="${pageContext.request.contextPath}/careshift/main/TaskList.action">
                            タスク一覧を見る
                        </a>

                    </div>

                </c:when>

                <c:otherwise>

                    <c:forEach
                        var="t"
                        items="${myTasks}">

                        <a
                            class="task-dashboard-item"
                            href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}">

                            <span
                                class="task-status-dot"
                                aria-hidden="true">
                            </span>

                            <span class="task-dashboard-content">

                                <strong class="task-dashboard-title">
                                    <c:out value="${t.title}"/>
                                </strong>

                                <span class="task-dashboard-meta">
                                    期限：
                                    <c:out
                                        value="${t.dueDate}"
                                        default="未設定"/>
                                </span>

                            </span>

                            <span class="task-status-label">
                                <c:out
                                    value="${t.statusLabel}"
                                    default="未対応"/>
                            </span>

                            <span
                                class="task-dashboard-arrow"
                                aria-hidden="true">
                                &rsaquo;
                            </span>

                        </a>

                    </c:forEach>

                </c:otherwise>

            </c:choose>

        </div>

        <footer class="task-summary-footer">

            <div class="task-summary-item">
                <span>未対応</span>

                <strong>
                    <c:out
                        value="${todoCount}"
                        default="0"/>
                </strong>
            </div>

            <div class="task-summary-item">
                <span>対応中</span>

                <strong>
                    <c:out
                        value="${doingCount}"
                        default="0"/>
                </strong>
            </div>

            <div class="task-summary-item">
                <span>完了</span>

                <strong>
                    <c:out
                        value="${doneCount}"
                        default="0"/>
                </strong>
            </div>

        </footer>

    </article>

    <%-- 未読通知 --%>
    <article class="dashboard-panel notification-panel">

        <header class="panel-header">

            <div class="panel-title-group">

                <span
                    class="panel-icon"
                    aria-hidden="true">
                    N
                </span>

                <div>
                    <h2 class="panel-title">
                        未読の通知
                    </h2>

                    <p class="panel-subtitle">
                        <c:out
                            value="${unreadCount}"
                            default="0"/>
                        件
                    </p>
                </div>

            </div>

            <a
                class="panel-link"
                id="notifications2"
                href="${pageContext.request.contextPath}/careshift/main/NotificationList.action">
                すべて見る
                <span aria-hidden="true">
                    &rsaquo;
                </span>
            </a>

        </header>

        <div
            class="notification-list"
            id="unreadList">

            <c:choose>

                <c:when test="${notificationError}">

                    <div class="empty-state compact">

                        <span
                            class="empty-state-icon"
                            aria-hidden="true">
                            !
                        </span>

                        <p>
                            通知を読み込めませんでした。
                        </p>

                        <a
                            class="button-secondary"
                            href="${pageContext.request.contextPath}/careshift/main/Menu.action">
                            再読み込み
                        </a>

                    </div>

                </c:when>

                <c:when test="${empty unreadList}">

                    <div
                        class="empty-state compact"
                        id="noUnread">

                        <span
                            class="empty-state-icon"
                            aria-hidden="true">
                            ✓
                        </span>

                        <p>
                            未読の通知はありません。
                        </p>

                    </div>

                </c:when>

                <c:otherwise>

                    <c:forEach
                        var="n"
                        items="${unreadList}">

                        <a
                            class="notification-item"
                            href="${pageContext.request.contextPath}/careshift/main/NotificationOpen.action?id=${n.id}">

                            <span
                                class="notification-dot"
                                aria-hidden="true">
                            </span>

                            <span class="notification-content">

                                <span class="notification-type">
                                    <c:out
                                        value="${n.typeLabel}"
                                        default="通知"/>
                                </span>

                                <strong class="notification-text">
                                    <c:out value="${n.message}"/>
                                </strong>

                                <small class="notification-time">
                                    <c:out value="${n.createdAtLabel}"/>
                                </small>

                            </span>

                            <span
                                class="notification-arrow"
                                aria-hidden="true">
                                &rsaquo;
                            </span>

                        </a>

                    </c:forEach>

                </c:otherwise>

            </c:choose>

        </div>

    </article>

    <%-- クイック操作 --%>
    <article class="dashboard-panel quick-panel">

        <header class="panel-header">

            <div class="panel-title-group">

                <span
                    class="panel-icon"
                    aria-hidden="true">
                    Q
                </span>

                <h2 class="panel-title">
                    クイック操作
                </h2>

            </div>

        </header>

        <div class="quick-action-grid">

            <c:if test="${not empty member}">

                <a
                    class="quick-action"
                    href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

                    <span
                        class="quick-action-icon"
                        aria-hidden="true">
                        T
                    </span>

                    <span>タスク一覧</span>
                </a>

                <a
                    class="quick-action"
                    id="calendar"
                    href="${pageContext.request.contextPath}/careshift/main/Calendar.action">

                    <span
                        class="quick-action-icon"
                        aria-hidden="true">
                        C
                    </span>

                    <span>カレンダー</span>
                </a>

                <a
                    class="quick-action"
                    href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">

                    <span
                        class="quick-action-icon"
                        aria-hidden="true">
                        R
                    </span>

                    <span>依頼する</span>
                </a>

                <a
                    class="quick-action"
                    href="${pageContext.request.contextPath}/careshift/main/Group.action">

                    <span
                        class="quick-action-icon"
                        aria-hidden="true">
                        G
                    </span>

                    <span>家族管理</span>
                </a>

            </c:if>

            <c:if test="${empty member}">

                <a
                    class="quick-action"
                    href="${pageContext.request.contextPath}/careshift/main/Group.action">

                    <span
                        class="quick-action-icon"
                        aria-hidden="true">
                        G
                    </span>

                    <span>グループを作成</span>
                </a>

                <a
                    class="quick-action"
                    href="${pageContext.request.contextPath}/careshift/main/NotificationList.action">

                    <span
                        class="quick-action-icon"
                        aria-hidden="true">
                        N
                    </span>

                    <span>通知一覧</span>
                </a>

            </c:if>

        </div>

    </article>

    <%-- おすすめコラム --%>
    <article
        class="featured-column-card"
        id="columnGuide">

        <div class="featured-column-content">

            <span class="featured-column-label">
                おすすめコラム
            </span>

            <c:choose>

                <c:when test="${not empty featuredArticle}">

                    <h2 class="featured-column-title">
                        <c:out value="${featuredArticle.title}"/>
                    </h2>

                    <p class="featured-column-summary">
                        <c:out value="${featuredArticle.summary}"/>
                    </p>

                    <a
                        class="featured-column-link"
                        href="${pageContext.request.contextPath}/careshift/main/Column.action?id=${featuredArticle.id}">
                        コラムを読む
                        <span aria-hidden="true">
                            &rsaquo;
                        </span>
                    </a>

                </c:when>

                <c:otherwise>

                    <h2 class="featured-column-title">
                        介護に役立つ情報を確認できます
                    </h2>

                    <p class="featured-column-summary">
                        Care Shiftのコラムとチェックリストをご覧ください。
                    </p>

                    <a
                        class="featured-column-link"
                        id="column"
                        href="${pageContext.request.contextPath}/careshift/main/Column.action">
                        コラム一覧を見る
                        <span aria-hidden="true">
                            &rsaquo;
                        </span>
                    </a>

                </c:otherwise>

            </c:choose>

        </div>

        <div
            class="featured-column-decoration"
            aria-hidden="true">

            <span>C</span>
        </div>

    </article>

</section>

<%-- =======================================================
     管理者専用リンク
     ======================================================= --%>

<c:if test="${isAdmin}">

    <section class="admin-shortcut">

        <div>

            <span class="admin-shortcut-label">
                ADMIN
            </span>

            <h2 class="admin-shortcut-title">
                管理者メニュー
            </h2>

            <p class="admin-shortcut-description">
                システム管理、バックアップ、障害ログなどを確認できます。
            </p>

        </div>

        <a
            class="button-secondary"
            id="admin"
            href="${pageContext.request.contextPath}/careshift/main/Admin.action">
            管理者画面を開く
        </a>

    </section>

</c:if>

<jsp:include page="/common/footer.jsp"/>