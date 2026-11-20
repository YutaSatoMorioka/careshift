<%-- S17 負担状況 / 設計書: 画面設計書_負担状況 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="負担状況"/>
    <jsp:param name="pageClass" value="load-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/load.css?v=20261117-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="load-page-header">

    <div class="load-page-heading">

        <p class="load-page-eyebrow">
            WORKLOAD BALANCE
        </p>

        <h1 class="load-page-title">
            負担状況
        </h1>

        <p class="load-page-description">
            タスクの件数と所要時間を担当者ごとに集計し、家族内の分担状況を確認します。
        </p>

    </div>

    <a
        class="load-header-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            ホームへ戻る
        </span>

    </a>

</section>

<%-- =======================================================
     対象期間
     ======================================================= --%>

<section
    class="load-period-card"
    aria-labelledby="load-period-title">

    <div class="load-period-heading">

        <span
            class="load-period-icon"
            aria-hidden="true">
            期
        </span>

        <div>

            <span class="load-period-label">
                現在の集計期間
            </span>

            <h2
                class="load-period-title"
                id="load-period-title">

                <c:out
                    value="${periodLabel}"
                    default="今週"/>

            </h2>

            <p class="load-period-range">
                <c:out value="${rangeLabel}"/>
            </p>

        </div>

    </div>

    <form
        class="load-period-form"
        method="get"
        action="${pageContext.request.contextPath}/careshift/main/Load.action">

        <div class="load-form-field">

            <label for="period">
                集計期間
            </label>

            <select
                class="load-form-control"
                id="period"
                name="period">

                <option
                    value="week"
                    ${period == 'week' ? 'selected' : ''}>
                    今週
                </option>

                <option
                    value="month"
                    ${period == 'month' ? 'selected' : ''}>
                    今月
                </option>

            </select>

        </div>

        <button
            class="load-button load-button-primary"
            type="submit"
            name="show">

            <span
                class="load-button-icon"
                aria-hidden="true">
                表
            </span>

            <span>
                表示を更新
            </span>

        </button>

    </form>

</section>

<div class="load-calculation-note">

    <span
        class="load-calculation-note-icon"
        aria-hidden="true">
        i
    </span>

    <p>
        期限が集計期間内にあるタスクを、担当者ごとに集計しています。
        完了済みのタスクも集計に含まれます。
        割合は、担当者が設定されているタスクの合計所要時間を基準に算出します。
    </p>

</div>

<%-- =======================================================
     負担集中の案内
     ======================================================= --%>

<c:if test="${not empty concentrated}">

    <section
        class="load-concentration-alert"
        id="concentration"
        aria-labelledby="load-concentration-title">

        <div class="load-concentration-main">

            <span
                class="load-concentration-icon"
                aria-hidden="true">
                !
            </span>

            <div class="load-concentration-content">

                <p class="load-concentration-eyebrow">
                    WORKLOAD ALERT
                </p>

                <h2
                    class="load-concentration-title"
                    id="load-concentration-title">

                    <c:out value="${concentrated.name}"/>
                    さんに負担が集中しています

                </h2>

                <p class="load-concentration-description">
                    担当者が設定されているタスクの所要時間のうち、
                    <strong>
                        <c:out value="${concentrated.percent}"/>%
                    </strong>
                    を担当しています。
                    対象は
                    <strong>
                        <c:out value="${concentrated.count}"/>件
                    </strong>
                    で、合計所要時間は
                    <strong>
                        <c:out value="${concentrated.minutesLabel}"/>
                    </strong>
                    です。
                </p>

            </div>

        </div>

        <div class="load-concentration-metrics">

            <div class="load-concentration-metric">

                <span>
                    負担割合
                </span>

                <strong>
                    <c:out value="${concentrated.percent}"/>
                    <small>%</small>
                </strong>

            </div>

            <div class="load-concentration-metric">

                <span>
                    担当件数
                </span>

                <strong>
                    <c:out value="${concentrated.count}"/>
                    <small>件</small>
                </strong>

            </div>

            <div class="load-concentration-metric">

                <span>
                    所要時間
                </span>

                <strong class="is-time">
                    <c:out value="${concentrated.minutesLabel}"/>
                </strong>

            </div>

        </div>

        <div class="load-concentration-actions">

            <a
                class="load-button load-button-primary"
                id="requestLink"
                href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">

                <span
                    class="load-button-icon"
                    aria-hidden="true">
                    依
                </span>

                <span>
                    家族に依頼する
                </span>

            </a>

            <a
                class="load-button load-button-secondary"
                href="${pageContext.request.contextPath}/careshift/main/TaskList.action?assignee=${concentrated.userId}">
                担当タスクを確認
            </a>

        </div>

    </section>

</c:if>

<%-- =======================================================
     集計結果
     ======================================================= --%>

<section
    class="load-result-card"
    aria-labelledby="load-result-title">

    <header class="load-result-header">

        <div>

            <p class="load-section-eyebrow">
                WORKLOAD SUMMARY
            </p>

            <h2
                class="load-result-title"
                id="load-result-title">
                集計結果
            </h2>

        </div>

        <div class="load-result-count">

            <strong>
                <c:out
                    value="${totalTasks}"
                    default="0"/>
            </strong>

            <span>
                タスク
            </span>

        </div>

    </header>

    <c:choose>

        <%-- =================================================
             タスクなし
             ================================================= --%>

        <c:when test="${totalTasks == 0}">

            <div
                class="load-empty-state"
                id="empty">

                <span
                    class="load-empty-icon"
                    aria-hidden="true">
                    T
                </span>

                <h3 class="load-empty-title">
                    この期間のタスクはありません
                </h3>

                <p class="load-empty-description">
                    選択した期間内に期限が設定されているタスクはありません。
                    別の期間を選択するか、新しいタスクを登録してください。
                </p>

                <div class="load-empty-actions">

                    <c:choose>

                        <c:when test="${period == 'week'}">

                            <a
                                class="load-button load-button-secondary"
                                href="${pageContext.request.contextPath}/careshift/main/Load.action?period=month">
                                今月を表示
                            </a>

                        </c:when>

                        <c:otherwise>

                            <a
                                class="load-button load-button-secondary"
                                href="${pageContext.request.contextPath}/careshift/main/Load.action?period=week">
                                今週を表示
                            </a>

                        </c:otherwise>

                    </c:choose>

                    <a
                        class="load-button load-button-primary"
                        href="${pageContext.request.contextPath}/careshift/main/TaskCreate.action">
                        タスクを登録する
                    </a>

                </div>

            </div>

        </c:when>

        <%-- =================================================
             タスクあり
             ================================================= --%>

        <c:otherwise>

            <%-- =============================================
                 グラフ
                 ============================================= --%>

            <section
                class="load-chart-section"
                aria-labelledby="load-chart-title">

                <div class="load-subsection-header">

                    <div>

                        <p class="load-section-eyebrow">
                            CHART
                        </p>

                        <h3
                            class="load-subsection-title"
                            id="load-chart-title">
                            家族ごとの負担
                        </h3>

                    </div>

                    <div class="load-chart-legend">

                        <span class="load-legend-item">

                            <span
                                class="load-legend-color is-count"
                                aria-hidden="true">
                            </span>

                            件数

                        </span>

                        <span class="load-legend-item">

                            <span
                                class="load-legend-color is-minutes"
                                aria-hidden="true">
                            </span>

                            所要時間

                        </span>

                    </div>

                </div>

                <div
                    class="load-chart-list"
                    id="loadChart">

                    <c:forEach
                        var="s"
                        items="${stats}">

                        <article class="load-chart-row">

                            <div class="load-chart-person">

                                <span
                                    class="load-chart-avatar"
                                    aria-hidden="true">

                                    <c:choose>

                                        <c:when test="${not empty s.name}">
                                            <c:out value="${s.name.substring(0, 1)}"/>
                                        </c:when>

                                        <c:otherwise>
                                            人
                                        </c:otherwise>

                                    </c:choose>

                                </span>

                                <div>

                                    <h4 class="load-chart-name">
                                        <c:out value="${s.name}"/>
                                    </h4>

                                    <span class="load-chart-percent">
                                        担当あり合計の
                                        <strong>
                                            <c:out value="${s.percent}"/>%
                                        </strong>
                                    </span>

                                </div>

                            </div>

                            <div class="load-chart-bars">

                                <div class="load-chart-bar-item">

                                    <div class="load-chart-bar-heading">

                                        <span>
                                            件数
                                        </span>

                                        <strong>
                                            <c:out value="${s.count}"/>
                                            件
                                        </strong>

                                    </div>

                                    <div class="load-chart-track">

                                        <span
                                            class="load-chart-fill is-count"
                                            style="width: ${s.countBar}%; min-width: ${s.count > 0 ? '4px' : '0'};">
                                        </span>

                                    </div>

                                </div>

                                <div class="load-chart-bar-item">

                                    <div class="load-chart-bar-heading">

                                        <span>
                                            所要時間
                                        </span>

                                        <strong>
                                            <c:out value="${s.minutesLabel}"/>
                                        </strong>

                                    </div>

                                    <div class="load-chart-track">

                                        <span
                                            class="load-chart-fill is-minutes"
                                            style="width: ${s.minutesBar}%; min-width: ${s.minutes > 0 ? '4px' : '0'};">
                                        </span>

                                    </div>

                                </div>

                            </div>

                            <a
                                class="load-chart-detail-link"
                                href="${pageContext.request.contextPath}/careshift/main/TaskList.action?assignee=${s.userId}">

                                担当タスク

                                <span aria-hidden="true">
                                    &rsaquo;
                                </span>

                            </a>

                        </article>

                    </c:forEach>

                    <%-- 担当なし --%>

                    <article
                        class="load-chart-row is-unassigned"
                        id="unassignedRow">

                        <div class="load-chart-person">

                            <span
                                class="load-chart-avatar is-unassigned"
                                aria-hidden="true">
                                ？
                            </span>

                            <div>

                                <h4 class="load-chart-name">
                                    担当なし
                                </h4>

                                <span class="load-chart-percent">
                                    担当者が設定されていません
                                </span>

                            </div>

                        </div>

                        <div class="load-chart-bars">

                            <div class="load-chart-bar-item">

                                <div class="load-chart-bar-heading">

                                    <span>
                                        件数
                                    </span>

                                    <strong>
                                        <c:out value="${unassigned.count}"/>
                                        件
                                    </strong>

                                </div>

                                <div class="load-chart-track">

                                    <span
                                        class="load-chart-fill is-unassigned"
                                        style="width: ${unassigned.countBar}%; min-width: ${unassigned.count > 0 ? '4px' : '0'};">
                                    </span>

                                </div>

                            </div>

                            <div class="load-chart-bar-item">

                                <div class="load-chart-bar-heading">

                                    <span>
                                        所要時間
                                    </span>

                                    <strong>
                                        <c:out value="${unassigned.minutesLabel}"/>
                                    </strong>

                                </div>

                                <div class="load-chart-track">

                                    <span
                                        class="load-chart-fill is-unassigned"
                                        style="width: ${unassigned.minutesBar}%; min-width: ${unassigned.minutes > 0 ? '4px' : '0'};">
                                    </span>

                                </div>

                            </div>

                        </div>

                        <a
                            class="load-chart-detail-link is-unassigned"
                            href="${pageContext.request.contextPath}/careshift/main/TaskList.action?assignee=none">

                            タスクを確認

                            <span aria-hidden="true">
                                &rsaquo;
                            </span>

                        </a>

                    </article>

                </div>

            </section>

            <%-- =============================================
                 詳細表
                 ============================================= --%>

            <section
                class="load-detail-section"
                aria-labelledby="load-detail-title">

                <div class="load-subsection-header">

                    <div>

                        <p class="load-section-eyebrow">
                            DETAILS
                        </p>

                        <h3
                            class="load-subsection-title"
                            id="load-detail-title">
                            集計の詳細
                        </h3>

                    </div>

                </div>

                <div class="load-table-wrapper">

                    <table
                        class="load-detail-table"
                        id="loadList">

                        <thead>

                            <tr>

                                <th scope="col">
                                    家族
                                </th>

                                <th scope="col">
                                    件数
                                </th>

                                <th scope="col">
                                    所要時間
                                </th>

                                <th scope="col">
                                    割合
                                </th>

                                <th
                                    class="load-detail-action-heading"
                                    scope="col">
                                    タスク
                                </th>

                            </tr>

                        </thead>

                        <tbody>

                            <c:forEach
                                var="s"
                                items="${stats}">

                                <tr>

                                    <th scope="row">

                                        <span class="load-table-person">

                                            <span
                                                class="load-table-avatar"
                                                aria-hidden="true">

                                                <c:choose>

                                                    <c:when test="${not empty s.name}">
                                                        <c:out value="${s.name.substring(0, 1)}"/>
                                                    </c:when>

                                                    <c:otherwise>
                                                        人
                                                    </c:otherwise>

                                                </c:choose>

                                            </span>

                                            <span>
                                                <c:out value="${s.name}"/>
                                            </span>

                                        </span>

                                    </th>

                                    <td>
                                        <strong>
                                            <c:out value="${s.count}"/>
                                        </strong>
                                        件
                                    </td>

                                    <td>
                                        <c:out value="${s.minutesLabel}"/>
                                    </td>

                                    <td>

                                        <span class="load-percent-badge">
                                            <c:out value="${s.percent}"/>%
                                        </span>

                                    </td>

                                    <td class="load-detail-action-cell">

                                        <a
                                            class="load-detail-button"
                                            href="${pageContext.request.contextPath}/careshift/main/TaskList.action?assignee=${s.userId}"
                                            aria-label="<c:out value='${s.name}'/>さんの担当タスクを確認">
                                            &rsaquo;
                                        </a>

                                    </td>

                                </tr>

                            </c:forEach>

                            <tr class="load-unassigned-table-row">

                                <th scope="row">

                                    <span class="load-table-person">

                                        <span
                                            class="load-table-avatar is-unassigned"
                                            aria-hidden="true">
                                            ？
                                        </span>

                                        <span>
                                            担当なし
                                        </span>

                                    </span>

                                </th>

                                <td>
                                    <strong>
                                        <c:out value="${unassigned.count}"/>
                                    </strong>
                                    件
                                </td>

                                <td>
                                    <c:out value="${unassigned.minutesLabel}"/>
                                </td>

                                <td>
                                    <span class="load-no-percent">
                                        対象外
                                    </span>
                                </td>

                                <td class="load-detail-action-cell">

                                    <a
                                        class="load-detail-button is-unassigned"
                                        href="${pageContext.request.contextPath}/careshift/main/TaskList.action?assignee=none"
                                        aria-label="担当なしのタスクを確認">
                                        &rsaquo;
                                    </a>

                                </td>

                            </tr>

                        </tbody>

                    </table>

                </div>

            </section>

            <%-- =============================================
                 担当なしの案内
                 ============================================= --%>

            <c:if test="${unassigned.count > 0}">

                <section class="load-unassigned-notice">

                    <span
                        class="load-unassigned-notice-icon"
                        aria-hidden="true">
                        !
                    </span>

                    <div>

                        <h3 class="load-unassigned-notice-title">

                            担当者が決まっていないタスクが
                            <c:out value="${unassigned.count}"/>
                            件あります

                        </h3>

                        <p class="load-unassigned-notice-description">
                            担当者を設定すると、家族ごとの負担状況へ反映されます。
                            タスクの詳細画面から担当者を設定してください。
                        </p>

                    </div>

                    <a
                        class="load-button load-button-warning"
                        href="${pageContext.request.contextPath}/careshift/main/TaskList.action?assignee=none">
                        担当なしを確認
                    </a>

                </section>

            </c:if>

        </c:otherwise>

    </c:choose>

</section>

<%-- =======================================================
     画面説明
     ======================================================= --%>

<section class="load-guidance-card">

    <span
        class="load-guidance-icon"
        aria-hidden="true">
        i
    </span>

    <div>

        <h2 class="load-guidance-title">
            集計結果の見方
        </h2>

        <p class="load-guidance-description">
            棒グラフは、画面に表示されている家族と「担当なし」の中で、
            最も大きい値を100%として相対的に表示しています。
            家族名の下に表示される割合は、担当者が設定されたタスクの合計所要時間に対する割合です。
        </p>

    </div>

</section>

<nav
    class="load-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="load-back-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            ホームへ戻る
        </span>

    </a>

    <a
        class="load-task-list-link"
        href="${pageContext.request.contextPath}/careshift/main/TaskList.action">
        タスク一覧を開く
    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>