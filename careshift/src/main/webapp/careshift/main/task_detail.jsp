<%-- S09 タスク詳細 / 設計書: S09_タスク詳細 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="タスク詳細"/>
    <jsp:param name="pageClass" value="task-detail-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/task-detail.css?v=20261111-1">

<section class="task-detail-page-header">

    <div class="task-detail-page-heading">

        <p class="task-detail-page-eyebrow">
            TASK DETAIL
        </p>

        <h1 class="task-detail-page-title">
            タスク詳細
        </h1>

        <p class="task-detail-page-description">
            タスクの期限、担当者、進捗状況を確認できます。
        </p>

    </div>

    <div class="task-detail-header-actions">

        <a
            class="task-detail-header-button task-detail-header-button-secondary"
            href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

            <span aria-hidden="true">
                &lsaquo;
            </span>

            <span>
                タスク一覧
            </span>

        </a>

        <c:if test="${canEdit}">

            <a
                class="task-detail-header-button task-detail-header-button-primary"
                id="edit"
                href="${pageContext.request.contextPath}/careshift/main/TaskEdit.action?id=${task.id}">

                <span
                    class="task-detail-button-icon"
                    aria-hidden="true">
                    編
                </span>

                <span>
                    編集する
                </span>

            </a>

        </c:if>

    </div>

</section>

<%-- =======================================================
     タスク概要
     ======================================================= --%>

<section
    class="task-detail-card${task.overdue ? ' is-overdue' : ''}"
    aria-labelledby="task-detail-title">

    <header class="task-detail-card-header">

        <div class="task-detail-labels">

            <span class="task-detail-status-badge">

                <c:out
                    value="${task.statusLabel}"
                    default="未対応"/>

            </span>

            <c:choose>

                <c:when test="${task.unassigned}">

                    <span class="task-detail-unassigned-badge">
                        担当なし
                    </span>

                </c:when>

                <c:otherwise>

                    <span class="task-detail-assigned-badge">
                        担当者設定済み
                    </span>

                </c:otherwise>

            </c:choose>

            <c:if test="${task.overdue}">

                <span
                    class="task-detail-overdue-badge"
                    role="status">
                    期限超過
                </span>

            </c:if>

        </div>

        <span class="task-detail-id">
            タスク ID：
            <c:out value="${task.id}"/>
        </span>

    </header>

    <div class="task-detail-title-area">

        <span
            class="task-detail-title-icon"
            aria-hidden="true">
            T
        </span>

        <div>

            <span class="task-detail-title-label">
                タスク内容
            </span>

            <h2
                class="task-detail-title"
                id="task-detail-title">
                <c:out value="${task.title}"/>
            </h2>

        </div>

    </div>

    <dl class="task-detail-information">

        <div class="task-detail-information-item">

            <dt>

                <span
                    class="task-detail-information-icon"
                    aria-hidden="true">
                    日
                </span>

                <span>
                    期限
                </span>

            </dt>

            <dd>

                <time datetime="<c:out value='${task.dueDate}'/>">
                    <c:out
                        value="${task.dueDate}"
                        default="未設定"/>
                </time>

                <c:if test="${task.overdue}">

                    <span class="task-detail-overdue-text">
                        期限を過ぎています
                    </span>

                </c:if>

            </dd>

        </div>

        <div class="task-detail-information-item">

            <dt>

                <span
                    class="task-detail-information-icon"
                    aria-hidden="true">
                    時
                </span>

                <span>
                    所要時間
                </span>

            </dt>

            <dd>
                <c:out
                    value="${task.durationLabel}"
                    default="未設定"/>
            </dd>

        </div>

        <div class="task-detail-information-item">

            <dt>

                <span
                    class="task-detail-information-icon"
                    aria-hidden="true">
                    頻
                </span>

                <span>
                    頻度
                </span>

            </dt>

            <dd>
                <c:out
                    value="${task.frequencyLabel}"
                    default="未設定"/>
            </dd>

        </div>

        <div class="task-detail-information-item">

            <dt>

                <span
                    class="task-detail-information-icon"
                    aria-hidden="true">
                    人
                </span>

                <span>
                    担当者
                </span>

            </dt>

            <dd>

                <c:choose>

                    <c:when test="${task.unassigned}">

                        <span class="task-detail-unassigned">
                            担当なし
                        </span>

                    </c:when>

                    <c:otherwise>

                        <span class="task-detail-person">

                            <span
                                class="task-detail-person-avatar"
                                aria-hidden="true">
                                人
                            </span>

                            <span>
                                <c:out
                                    value="${task.assigneeName}"
                                    default="担当者"/>
                            </span>

                        </span>

                    </c:otherwise>

                </c:choose>

            </dd>

        </div>

        <div class="task-detail-information-item">

            <dt>

                <span
                    class="task-detail-information-icon"
                    aria-hidden="true">
                    登
                </span>

                <span>
                    登録者
                </span>

            </dt>

            <dd>

                <span class="task-detail-person">

                    <span
                        class="task-detail-person-avatar"
                        aria-hidden="true">
                        人
                    </span>

                    <span>
                        <c:out
                            value="${task.creatorName}"
                            default="登録者"/>
                    </span>

                </span>

            </dd>

        </div>

        <div class="task-detail-information-item">

            <dt>

                <span
                    class="task-detail-information-icon"
                    aria-hidden="true">
                    状
                </span>

                <span>
                    現在の状態
                </span>

            </dt>

            <dd>
                <span class="task-detail-current-status">
                    <c:out
                        value="${task.statusLabel}"
                        default="未対応"/>
                </span>
            </dd>

        </div>

    </dl>

</section>

<%-- =======================================================
     担当者・進捗の更新
     ======================================================= --%>

<c:choose>

    <c:when test="${canEdit}">

        <section
            class="task-quick-update-card"
            aria-labelledby="task-quick-update-title">

            <header class="task-quick-update-header">

                <span
                    class="task-quick-update-icon"
                    aria-hidden="true">
                    更
                </span>

                <div>

                    <h2
                        class="task-quick-update-title"
                        id="task-quick-update-title">
                        担当者・進捗を更新
                    </h2>

                    <p class="task-quick-update-description">
                        タスクの担当者と現在の進捗だけを素早く変更できます。
                    </p>

                </div>

            </header>

            <form
                class="task-quick-update-form"
                method="post"
                action="${pageContext.request.contextPath}/careshift/main/TaskQuickUpdate.action">

                <input
                    type="hidden"
                    name="id"
                    value="${task.id}">

                <input
                    type="hidden"
                    name="version"
                    value="${task.version}">

                <div class="task-quick-update-grid">

                    <div class="task-detail-form-field">

                        <label for="assignee">
                            担当者
                        </label>

                        <select
                            class="task-detail-form-control"
                            id="assignee"
                            name="assignee">

                            <option value="">
                                担当なし
                            </option>

                            <c:forEach
                                var="m"
                                items="${members}">

                                <option
                                    value="${m.userId}"
                                    ${task.assigneeId == m.userId ? 'selected' : ''}>

                                    <c:out value="${m.displayName}"/>

                                    <c:if test="${m.primary}">
                                        （主介護者）
                                    </c:if>

                                </option>

                            </c:forEach>

                        </select>

                        <p class="task-detail-field-help">
                            担当者を変更すると、新しい担当者へ通知されます。
                        </p>

                    </div>

                    <div class="task-detail-form-field">

                        <label for="status">
                            進捗
                        </label>

                        <select
                            class="task-detail-form-control"
                            id="status"
                            name="status">

                            <c:forEach
                                var="s"
                                items="${statuses}">

                                <option
                                    value="<c:out value='${s.key}'/>"
                                    ${s.key == task.status ? 'selected' : ''}>
                                    <c:out value="${s.value}"/>
                                </option>

                            </c:forEach>

                        </select>

                        <p class="task-detail-field-help">
                            タスクの現在の対応状況を選択してください。
                        </p>

                    </div>

                </div>

                <div class="task-quick-update-actions">

                    <button
                        class="task-detail-action-button task-detail-action-button-primary"
                        type="submit"
                        name="update">

                        <span
                            class="task-detail-button-icon"
                            aria-hidden="true">
                            ✓
                        </span>

                        <span>
                            担当・進捗を更新
                        </span>

                    </button>

                </div>

            </form>

        </section>

        <%-- =================================================
             編集・依頼
             ================================================= --%>

        <section class="task-detail-action-panel">

            <div class="task-detail-action-panel-content">

                <span
                    class="task-detail-action-panel-icon"
                    aria-hidden="true">
                    編
                </span>

                <div>

                    <h2 class="task-detail-action-panel-title">
                        タスク内容を編集
                    </h2>

                    <p class="task-detail-action-panel-description">
                        タスク内容、期限、所要時間、頻度などを変更できます。
                    </p>

                </div>

            </div>

            <a
                class="task-detail-action-button task-detail-action-button-secondary"
                href="${pageContext.request.contextPath}/careshift/main/TaskEdit.action?id=${task.id}">
                編集画面を開く
                <span aria-hidden="true">
                    &rsaquo;
                </span>
            </a>

        </section>

    </c:when>

    <c:otherwise>

        <section class="task-detail-readonly-card">

            <span
                class="task-detail-readonly-icon"
                aria-hidden="true">
                鍵
            </span>

            <div>

                <h2 class="task-detail-readonly-title">
                    このタスクは閲覧のみです
                </h2>

                <p class="task-detail-readonly-description">
                    このタスクを編集できるのは、登録者、担当者、主介護者です。
                </p>

            </div>

        </section>

    </c:otherwise>

</c:choose>

<%-- =======================================================
     家族への依頼
     ======================================================= --%>

<c:if test="${members.size() > 1}">

    <section class="task-request-card">

        <div class="task-request-card-content">

            <span
                class="task-request-card-icon"
                aria-hidden="true">
                依
            </span>

            <div>

                <h2 class="task-request-card-title">
                    このタスクを家族に依頼
                </h2>

                <p class="task-request-card-description">
                    タスクの内容、所要時間、期限を初期値として依頼カードを作成します。
                </p>

            </div>

        </div>

        <a
            class="task-detail-action-button task-detail-action-button-primary"
            id="request"
            href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action?taskId=${task.id}">
            家族に依頼する
            <span aria-hidden="true">
                &rsaquo;
            </span>
        </a>

    </section>

</c:if>

<%-- =======================================================
     削除
     ======================================================= --%>

<c:if test="${canEdit}">

    <section class="task-danger-zone">

        <div>

            <h2 class="task-danger-zone-title">
                タスクの削除
            </h2>

            <p class="task-danger-zone-description">
                削除したタスクは元に戻せません。担当者がいる場合は、削除されたことが通知されます。
            </p>

        </div>

        <form
            class="task-delete-form"
            method="post"
            action="${pageContext.request.contextPath}/careshift/main/TaskDelete.action"
            onsubmit="return confirm('このタスクを削除します。担当者にも通知されます。よろしいですか？');">

            <input
                type="hidden"
                name="id"
                value="${task.id}">

            <button
                class="task-detail-action-button task-detail-action-button-danger"
                type="submit"
                name="delete">

                <span
                    class="task-detail-button-icon"
                    aria-hidden="true">
                    ×
                </span>

                <span>
                    このタスクを削除
                </span>

            </button>

        </form>

    </section>

</c:if>

<nav
    class="task-detail-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="task-detail-back-link"
        href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            タスク一覧へ戻る
        </span>

    </a>

    <a
        class="task-detail-calendar-link"
        href="${pageContext.request.contextPath}/careshift/main/Calendar.action">
        カレンダーで見る
    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>