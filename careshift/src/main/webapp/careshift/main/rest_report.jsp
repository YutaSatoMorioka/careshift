<%-- S15 休息レポート / 設計書: 画面設計書_休息レポート --%>
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
        value="休息レポート"/>
    <jsp:param
        name="pageClass"
        value="rest-report-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/rest-report.css?v=20261117-1">

<section class="rest-report-header">

    <div>

        <p class="rest-report-eyebrow">
            REST REPORT
        </p>

        <h1 class="rest-report-title">
            休息レポート
        </h1>

        <p class="rest-report-description">
            確定した休息の回数と合計時間を確認できます。
            調整中・取消の予定は実績と分けて表示します。
        </p>

    </div>

    <a
        class="rest-report-back-link noprint"
        href="${pageContext.request.contextPath}/careshift/main/RestRequest.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>休息管理へ戻る</span>
    </a>

</section>

<section
    class="rest-report-toolbar noprint"
    aria-label="対象月と出力操作">

    <div class="rest-report-month-navigation">

        <form
            method="get"
            action="${pageContext.request.contextPath}/careshift/main/RestReport.action">

            <input
                type="hidden"
                name="targetMonth"
                value="<c:out value='${prevMonth}'/>">

            <button
                class="rest-report-button rest-report-button-secondary"
                type="submit">

                <span aria-hidden="true">
                    &lsaquo;
                </span>

                前月
            </button>

        </form>

        <form
            class="rest-report-month-form"
            method="get"
            action="${pageContext.request.contextPath}/careshift/main/RestReport.action">

            <label for="targetMonth">
                対象年月
            </label>

            <input
                class="rest-report-month-input"
                type="month"
                id="targetMonth"
                name="targetMonth"
                value="<c:out value='${targetMonth}'/>">

            <button
                class="rest-report-button rest-report-button-primary"
                type="submit">
                表示
            </button>

        </form>

        <form
            method="get"
            action="${pageContext.request.contextPath}/careshift/main/RestReport.action">

            <input
                type="hidden"
                name="targetMonth"
                value="<c:out value='${nextMonth}'/>">

            <button
                class="rest-report-button rest-report-button-secondary"
                type="submit">

                翌月

                <span aria-hidden="true">
                    &rsaquo;
                </span>
            </button>

        </form>

    </div>

    <div class="rest-report-output-actions">

        <form
            method="get"
            action="${pageContext.request.contextPath}/careshift/main/RestReportPdf.action">

            <input
                type="hidden"
                name="targetMonth"
                value="<c:out value='${targetMonth}'/>">

            <button
                class="rest-report-button rest-report-button-secondary"
                type="submit"
                name="exportPdf">
                PDF出力
            </button>

        </form>

        <button
            class="rest-report-button rest-report-button-secondary"
            type="button"
            name="print"
            onclick="window.print()">
            印刷
        </button>

    </div>

</section>

<p class="rest-report-pdf-help noprint">
    PDFで日本語が正しく表示されない場合は、「印刷」を押し、
    ブラウザーの送信先から「PDFに保存」を選択してください。
</p>

<section
    class="rest-report-summary-section"
    aria-labelledby="rest-report-summary-title">

    <header class="rest-report-section-header">

        <div>

            <p class="rest-report-section-eyebrow">
                MONTHLY SUMMARY
            </p>

            <h2
                class="rest-report-section-title"
                id="rest-report-summary-title">

                <c:out value="${monthLabel}"/>の集計
            </h2>

        </div>

        <span class="rest-report-target-month">
            <c:out value="${monthLabel}"/>
        </span>

    </header>

    <div
        class="rest-report-summary-grid"
        id="restChart">

        <article class="rest-report-summary-card is-primary">

            <span class="rest-report-summary-label">
                確定した休息
            </span>

            <p class="rest-report-summary-value">

                <strong id="confirmedCount">
                    <c:out value="${confirmedCount}"/>
                </strong>

                <span>回</span>
            </p>

            <p class="rest-report-summary-help">
                家族の担当が決まり、確定した予定です。
            </p>

        </article>

        <article class="rest-report-summary-card is-time">

            <span class="rest-report-summary-label">
                合計休息時間
            </span>

            <p class="rest-report-summary-value">

                <strong id="totalMinutes">
                    <c:out value="${totalMinutesLabel}"/>
                </strong>
            </p>

            <p class="rest-report-summary-help">
                確定した予定の合計時間です。
            </p>

        </article>

        <article class="rest-report-summary-card is-adjusting">

            <span class="rest-report-summary-label">
                調整中
            </span>

            <p class="rest-report-summary-value">

                <strong>
                    <c:out value="${adjustingCount}"/>
                </strong>

                <span>件</span>
            </p>

            <p class="rest-report-summary-help">
                担当する家族がまだ決まっていません。
            </p>

        </article>

        <article class="rest-report-summary-card is-cancelled">

            <span class="rest-report-summary-label">
                取消
            </span>

            <p class="rest-report-summary-value">

                <strong>
                    <c:out value="${cancelledCount}"/>
                </strong>

                <span>件</span>
            </p>

            <p class="rest-report-summary-help">
                実績には含まれません。
            </p>

        </article>

    </div>

    <c:if test="${upcomingCount > 0}">

        <div class="rest-report-upcoming-notice">

            <span
                class="rest-report-notice-icon"
                aria-hidden="true">
                i
            </span>

            <p>
                確定した休息のうち、まだ開始前の予定が
                <strong>
                    <c:out value="${upcomingCount}"/>件
                </strong>
                含まれています。
            </p>

        </div>

    </c:if>

</section>

<section
    class="rest-report-detail-section"
    aria-labelledby="rest-report-detail-title">

    <header class="rest-report-section-header">

        <div>

            <p class="rest-report-section-eyebrow">
                CONFIRMED REST
            </p>

            <h2
                class="rest-report-section-title"
                id="rest-report-detail-title">
                確定した休息の内訳
            </h2>

        </div>

        <span class="rest-report-count">
            <strong>
                <c:out value="${confirmedCount}"/>
            </strong>
            件
        </span>

    </header>

    <c:choose>

        <c:when test="${empty confirmed}">

            <div class="rest-report-empty-state">

                <span
                    class="rest-report-empty-icon"
                    aria-hidden="true">
                    休
                </span>

                <h3>
                    この月に確定した休息はありません
                </h3>

                <p>
                    家族が休息リクエストを引き受けると、この一覧に表示されます。
                </p>

            </div>

        </c:when>

        <c:otherwise>

            <div class="rest-report-table-wrapper">

                <table class="rest-report-table">

                    <thead>
                        <tr>
                            <th scope="col">日付</th>
                            <th scope="col">時間</th>
                            <th scope="col">休息時間</th>
                            <th scope="col">担当</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach
                            var="p"
                            items="${confirmed}">

                            <tr>

                                <td>
                                    <span class="rest-report-date-cell">
                                        <span
                                            class="rest-report-date-icon"
                                            aria-hidden="true">
                                            日
                                        </span>

                                        <c:out value="${p.dateLabel}"/>
                                    </span>
                                </td>

                                <td>
                                    <c:out value="${p.timeRange}"/>
                                </td>

                                <td>
                                    <span class="rest-report-duration">
                                        <c:out value="${p.durationLabel}"/>
                                    </span>
                                </td>

                                <td>
                                    <span class="rest-report-person">
                                        <span
                                            class="rest-report-person-avatar"
                                            aria-hidden="true">
                                            人
                                        </span>

                                        <c:out
                                            value="${p.coveredName}"
                                            default="担当者"/>
                                    </span>
                                </td>

                            </tr>

                        </c:forEach>

                    </tbody>

                </table>

            </div>

        </c:otherwise>

    </c:choose>

</section>

<section
    class="rest-report-trend-section"
    aria-labelledby="rest-report-trend-title">

    <header class="rest-report-section-header">

        <div>

            <p class="rest-report-section-eyebrow">
                SIX MONTH TREND
            </p>

            <h2
                class="rest-report-section-title"
                id="rest-report-trend-title">
                直近6か月の推移
            </h2>

            <p class="rest-report-section-description">
                確定した休息の合計時間を月別に比較します。
            </p>

        </div>

    </header>

    <div
        class="rest-report-trend-list"
        id="trend">

        <c:forEach
            var="s"
            items="${stats}">

            <div class="rest-report-trend-row">

                <div class="rest-report-trend-label">
                    <c:out value="${s.label}"/>
                </div>

                <div class="rest-report-trend-track">

                    <div
                        class="rest-report-trend-bar"
                        style="width:${s.percent}%; min-width:${s.minutes > 0 ? '6px' : '0'};">
                    </div>

                </div>

                <div class="rest-report-trend-value">

                    <strong>
                        <c:out value="${s.count}"/>回
                    </strong>

                    <span>
                        <c:out value="${s.minutesLabel}"/>
                    </span>

                </div>

            </div>

        </c:forEach>

    </div>

</section>

<nav
    class="rest-report-footer-nav noprint"
    aria-label="ページ下部のナビゲーション">

    <a href="${pageContext.request.contextPath}/careshift/main/RestRequest.action">
        <span aria-hidden="true">&lsaquo;</span>
        休息管理へ戻る
    </a>

    <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">
        ホームへ戻る
    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>