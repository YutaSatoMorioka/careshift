<%-- S10 カレンダー / 設計書: 画面設計書_カレンダー --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="カレンダー"/>
    <jsp:param name="pageClass" value="calendar-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/calendar.css?v=20261111-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="calendar-page-header">

    <div class="calendar-page-heading">

        <p class="calendar-page-eyebrow">
            CALENDAR
        </p>

        <h1 class="calendar-page-title">
            カレンダー
        </h1>

        <p class="calendar-page-description">
            タスクの期限と休息予定を月単位で確認できます。
        </p>

    </div>

    <div class="calendar-page-actions">

        <a
            class="calendar-button calendar-button-secondary"
            href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

            <span
                class="calendar-button-icon"
                aria-hidden="true">
                T
            </span>

            <span>タスク一覧</span>
        </a>

        <a
            class="calendar-button calendar-button-primary"
            href="${pageContext.request.contextPath}/careshift/main/TaskCreate.action">

            <span
                class="calendar-button-icon"
                aria-hidden="true">
                ＋
            </span>

            <span>タスクを登録</span>
        </a>

    </div>

</section>

<%-- =======================================================
     月の移動
     ======================================================= --%>

<section
    class="calendar-toolbar"
    aria-labelledby="calendar-month-title">

    <form
        class="calendar-month-move-form"
        method="get"
        action="${pageContext.request.contextPath}/careshift/main/Calendar.action">

        <input
            type="hidden"
            name="month"
            value="<c:out value='${prevMonth}'/>">

        <button
            class="calendar-month-button"
            type="submit"
            name="prev"
            aria-label="前月を表示">

            <span aria-hidden="true">
                &lsaquo;
            </span>

            <span class="calendar-month-button-label">
                前月
            </span>
        </button>

    </form>

    <div class="calendar-current-month">

        <span
            class="calendar-current-month-icon"
            aria-hidden="true">
            月
        </span>

        <div>
            <span class="calendar-current-month-label">
                表示中
            </span>

            <h2
                class="calendar-current-month-title"
                id="calendar-month-title">
                <c:out value="${monthLabel}"/>
            </h2>
        </div>

    </div>

    <form
        class="calendar-month-select-form"
        method="get"
        action="${pageContext.request.contextPath}/careshift/main/Calendar.action">

        <label
            class="calendar-month-select-label"
            for="month">
            表示月
        </label>

        <input
            class="calendar-month-input"
            type="month"
            id="month"
            name="month"
            value="<c:out value='${month}'/>">

        <button
            class="calendar-display-button"
            type="submit">
            表示
        </button>

    </form>

    <form
        class="calendar-month-move-form"
        method="get"
        action="${pageContext.request.contextPath}/careshift/main/Calendar.action">

        <input
            type="hidden"
            name="month"
            value="<c:out value='${nextMonth}'/>">

        <button
            class="calendar-month-button"
            type="submit"
            name="next"
            aria-label="翌月を表示">

            <span class="calendar-month-button-label">
                翌月
            </span>

            <span aria-hidden="true">
                &rsaquo;
            </span>
        </button>

    </form>

</section>

<%-- =======================================================
     凡例
     ======================================================= --%>

<section
    class="calendar-legend"
    aria-label="カレンダーの凡例">

    <div class="calendar-legend-item">
        <span
            class="calendar-legend-mark calendar-legend-task"
            aria-hidden="true">
        </span>

        <span>タスク</span>
    </div>

    <div class="calendar-legend-item">
        <span
            class="calendar-legend-mark calendar-legend-rest-adjusting"
            aria-hidden="true">
        </span>

        <span>休息・調整中</span>
    </div>

    <div class="calendar-legend-item">
        <span
            class="calendar-legend-mark calendar-legend-rest-confirmed"
            aria-hidden="true">
        </span>

        <span>休息・確定</span>
    </div>

    <div class="calendar-legend-item">
        <span
            class="calendar-legend-mark calendar-legend-rest-cancelled"
            aria-hidden="true">
        </span>

        <span>休息・取消</span>
    </div>

    <div class="calendar-legend-item">
        <span
            class="calendar-legend-mark calendar-legend-today"
            aria-hidden="true">
        </span>

        <span>今日</span>
    </div>

</section>

<%-- =======================================================
     月間カレンダー
     ======================================================= --%>

<section
    class="calendar-card"
    aria-label="<c:out value='${monthLabel}'/>のカレンダー">

    <div class="calendar-scroll">

        <table
            class="calendar-table"
            id="calendar">

            <thead>

                <tr>

                    <c:forEach
                        var="d"
                        items="${weekdays}"
                        varStatus="weekdayStatus">

                        <th
                            class="${weekdayStatus.first ? 'calendar-sunday' : ''}${weekdayStatus.last ? ' calendar-saturday' : ''}"
                            scope="col">
                            <c:out value="${d}"/>
                        </th>

                    </c:forEach>

                </tr>

            </thead>

            <tbody>

                <c:forEach
                    var="week"
                    items="${weeks}">

                    <tr>

                        <c:forEach
                            var="cell"
                            items="${week}"
                            varStatus="cellStatus">

                            <td class="calendar-cell
                                ${cell.inMonth ? '' : ' calendar-cell-outside'}
                                ${cell.today ? ' calendar-cell-today' : ''}
                                ${cellStatus.first ? ' calendar-cell-sunday' : ''}
                                ${cellStatus.last ? ' calendar-cell-saturday' : ''}">

                                <div class="calendar-cell-header">

                                    <time
                                        class="calendar-day-number"
                                        datetime="<c:out value='${cell.date}'/>">

                                        <c:out value="${cell.day}"/>

                                    </time>

                                    <c:if test="${cell.today}">

                                        <span class="calendar-today-label">
                                            今日
                                        </span>

                                    </c:if>

                                </div>

                                <div class="calendar-cell-events">

                                    <%-- タスク --%>
                                    <c:forEach
                                        var="t"
                                        items="${cell.tasks}">

                                        <a
                                            class="calendar-task
                                                ${t.done ? ' calendar-task-done' : ''}
                                                ${t.overdue ? ' calendar-task-overdue' : ''}
                                                ${t.unassigned ? ' calendar-task-unassigned' : ''}"
                                            href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}">

                                            <span class="calendar-task-title">
                                                <c:out value="${t.title}"/>
                                            </span>

                                            <span class="calendar-task-meta">

                                                <c:choose>

                                                    <c:when test="${t.unassigned}">
                                                        担当なし
                                                    </c:when>

                                                    <c:otherwise>
                                                        <c:out
                                                            value="${t.assigneeName}"
                                                            default="担当者"/>
                                                    </c:otherwise>

                                                </c:choose>

                                                <span aria-hidden="true">
                                                    ・
                                                </span>

                                                <c:out
                                                    value="${t.statusLabel}"
                                                    default="未対応"/>

                                            </span>

                                        </a>

                                    </c:forEach>

                                    <%-- 休息予定 --%>
                                    <c:forEach
                                        var="rp"
                                        items="${cell.rests}">

                                        <article class="calendar-rest
                                            ${rp.confirmed ? ' calendar-rest-confirmed' : ''}
                                            ${rp.cancelled ? ' calendar-rest-cancelled' : ''}
                                            ${rp.adjusting ? ' calendar-rest-adjusting' : ''}">

                                            <div class="calendar-rest-header">

                                                <span class="calendar-rest-title">
                                                    休息
                                                </span>

                                                <span class="calendar-rest-status">
                                                    <c:out value="${rp.statusLabel}"/>
                                                </span>

                                            </div>

                                            <time class="calendar-rest-time">
                                                <c:out value="${rp.timeRange}"/>
                                            </time>

                                            <c:if test="${not empty rp.content}">

                                                <span class="calendar-rest-content">
                                                    <c:out value="${rp.content}"/>
                                                </span>

                                            </c:if>

                                            <c:if test="${rp.confirmed}">

                                                <span class="calendar-rest-assignee">

                                                    担当：

                                                    <c:out
                                                        value="${rp.coveredName}"
                                                        default="未設定"/>

                                                </span>

                                            </c:if>

                                            <c:if test="${isPrimary and not rp.cancelled}">

                                                <form
                                                    class="calendar-rest-cancel-form"
                                                    method="post"
                                                    action="${pageContext.request.contextPath}/careshift/main/RestCancel.action"
                                                    onsubmit="return confirm('この休息予定を取り消します。担当の家族がいれば通知されます。よろしいですか？');">

                                                    <input
                                                        type="hidden"
                                                        name="id"
                                                        value="${rp.id}">

                                                    <input
                                                        type="hidden"
                                                        name="back"
                                                        value="calendar">

                                                    <button
                                                        class="calendar-rest-cancel-button"
                                                        type="submit"
                                                        name="cancelRest">
                                                        取消
                                                    </button>

                                                </form>

                                            </c:if>

                                        </article>

                                    </c:forEach>

                                </div>

                            </td>

                        </c:forEach>

                    </tr>

                </c:forEach>

            </tbody>

        </table>

    </div>

</section>

<%-- =======================================================
     スマートフォン向け月間リスト
     ======================================================= --%>

<section
    class="calendar-mobile-list"
    aria-label="<c:out value='${monthLabel}'/>の予定一覧">

    <c:forEach
        var="week"
        items="${weeks}">

        <c:forEach
            var="cell"
            items="${week}">

            <c:if test="${cell.inMonth and (not empty cell.tasks or not empty cell.rests)}">

                <article class="calendar-mobile-day${cell.today ? ' calendar-mobile-day-today' : ''}">

                    <header class="calendar-mobile-day-header">

                        <time
                            class="calendar-mobile-date"
                            datetime="<c:out value='${cell.date}'/>">

                            <span class="calendar-mobile-day-number">
                                <c:out value="${cell.day}"/>
                            </span>

                            <span class="calendar-mobile-date-label">
                                <c:out value="${monthLabel}"/>
                            </span>

                        </time>

                        <c:if test="${cell.today}">
                            <span class="calendar-today-label">
                                今日
                            </span>
                        </c:if>

                    </header>

                    <div class="calendar-mobile-events">

                        <c:forEach
                            var="t"
                            items="${cell.tasks}">

                            <a
                                class="calendar-mobile-task
                                    ${t.done ? ' calendar-mobile-task-done' : ''}
                                    ${t.overdue ? ' calendar-mobile-task-overdue' : ''}"
                                href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}">

                                <div class="calendar-mobile-event-header">

                                    <span class="calendar-mobile-event-type">
                                        タスク
                                    </span>

                                    <span class="calendar-mobile-event-status">
                                        <c:out
                                            value="${t.statusLabel}"
                                            default="未対応"/>
                                    </span>

                                </div>

                                <strong class="calendar-mobile-event-title">
                                    <c:out value="${t.title}"/>
                                </strong>

                                <span class="calendar-mobile-event-meta">

                                    <c:choose>
                                        <c:when test="${t.unassigned}">
                                            担当なし
                                        </c:when>

                                        <c:otherwise>
                                            担当：
                                            <c:out
                                                value="${t.assigneeName}"
                                                default="担当者"/>
                                        </c:otherwise>
                                    </c:choose>

                                </span>

                            </a>

                        </c:forEach>

                        <c:forEach
                            var="rp"
                            items="${cell.rests}">

                            <article class="calendar-mobile-rest
                                ${rp.confirmed ? ' calendar-mobile-rest-confirmed' : ''}
                                ${rp.cancelled ? ' calendar-mobile-rest-cancelled' : ''}
                                ${rp.adjusting ? ' calendar-mobile-rest-adjusting' : ''}">

                                <div class="calendar-mobile-event-header">

                                    <span class="calendar-mobile-event-type">
                                        休息
                                    </span>

                                    <span class="calendar-mobile-event-status">
                                        <c:out value="${rp.statusLabel}"/>
                                    </span>

                                </div>

                                <strong class="calendar-mobile-event-title">
                                    <c:out value="${rp.timeRange}"/>
                                </strong>

                                <c:if test="${not empty rp.content}">
                                    <span class="calendar-mobile-event-meta">
                                        <c:out value="${rp.content}"/>
                                    </span>
                                </c:if>

                                <c:if test="${rp.confirmed}">
                                    <span class="calendar-mobile-event-meta">
                                        担当：
                                        <c:out
                                            value="${rp.coveredName}"
                                            default="未設定"/>
                                    </span>
                                </c:if>

                                <c:if test="${isPrimary and not rp.cancelled}">

                                    <form
                                        class="calendar-mobile-cancel-form"
                                        method="post"
                                        action="${pageContext.request.contextPath}/careshift/main/RestCancel.action"
                                        onsubmit="return confirm('この休息予定を取り消します。担当の家族がいれば通知されます。よろしいですか？');">

                                        <input
                                            type="hidden"
                                            name="id"
                                            value="${rp.id}">

                                        <input
                                            type="hidden"
                                            name="back"
                                            value="calendar">

                                        <button
                                            class="calendar-rest-cancel-button"
                                            type="submit"
                                            name="cancelRest">
                                            この予定を取り消す
                                        </button>

                                    </form>

                                </c:if>

                            </article>

                        </c:forEach>

                    </div>

                </article>

            </c:if>

        </c:forEach>

    </c:forEach>

    <div class="calendar-mobile-empty">
        予定が登録されていない日は表示していません。
    </div>

</section>

<%-- =======================================================
     下部ナビゲーション
     ======================================================= --%>

<nav
    class="calendar-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="calendar-back-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>ホームへ戻る</span>
    </a>

    <div class="calendar-footer-actions">

        <a
            class="calendar-footer-link"
            href="${pageContext.request.contextPath}/careshift/main/TaskList.action">
            タスク一覧
        </a>

        <a
            class="calendar-footer-link"
            href="${pageContext.request.contextPath}/careshift/main/TaskCreate.action">
            タスクを登録
        </a>

    </div>

</nav>

<jsp:include page="/common/footer.jsp"/>