<%-- S21 プラン / 設計書: S21_プラン --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="プラン"/>
    <jsp:param name="pageClass" value="plan-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/plan.css?v=20261117-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="plan-page-header">

    <div class="plan-page-heading">

        <p class="plan-page-eyebrow">
            SUBSCRIPTION PLAN
        </p>

        <h1 class="plan-page-title">
            利用プラン
        </h1>

        <p class="plan-page-description">
            現在のプラン、利用できる機能、料金、次回更新日を確認できます。
        </p>

    </div>

    <a
        class="plan-header-link"
        href="${pageContext.request.contextPath}/careshift/main/Settings.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            設定へ戻る
        </span>

    </a>

</section>

<%-- =======================================================
     決済結果
     ======================================================= --%>

<c:if test="${not empty paymentResult}">

    <div
        class="plan-payment-result${paymentOk ? ' is-success' : ' is-error'}"
        id="paymentResult"
        role="${paymentOk ? 'status' : 'alert'}">

        <span
            class="plan-payment-result-icon"
            aria-hidden="true">

            <c:choose>
                <c:when test="${paymentOk}">
                    ✓
                </c:when>

                <c:otherwise>
                    !
                </c:otherwise>
            </c:choose>

        </span>

        <div>

            <strong>

                <c:choose>
                    <c:when test="${paymentOk}">
                        決済が完了しました
                    </c:when>

                    <c:otherwise>
                        決済を完了できませんでした
                    </c:otherwise>
                </c:choose>

            </strong>

            <p>
                <c:out value="${paymentResult}"/>
            </p>

        </div>

    </div>

</c:if>

<%-- =======================================================
     現在のプラン
     ======================================================= --%>

<section
    class="plan-current-card${plan.paid ? ' is-paid' : ' is-free'}"
    aria-labelledby="current-plan-title">

    <div class="plan-current-main">

        <span
            class="plan-current-icon"
            aria-hidden="true">

            <c:choose>
                <c:when test="${plan.paid}">
                    P
                </c:when>

                <c:otherwise>
                    F
                </c:otherwise>
            </c:choose>

        </span>

        <div class="plan-current-content">

            <span class="plan-current-label">
                現在のプラン
            </span>

            <h2
                class="plan-current-name"
                id="current-plan-title">

                <span id="currentPlan">
                    <c:out
                        value="${plan.planLabel}"
                        default="無料"/>
                </span>
                プラン

            </h2>

            <c:choose>

                <c:when test="${plan.paid and plan.cancelRequested}">

                    <span class="plan-current-status is-cancelled">
                        無料プランへの変更予約済み
                    </span>

                </c:when>

                <c:when test="${plan.paid}">

                    <span class="plan-current-status">
                        有料機能を利用できます
                    </span>

                </c:when>

                <c:otherwise>

                    <span class="plan-current-status is-free">
                        基本機能を利用できます
                    </span>

                </c:otherwise>

            </c:choose>

        </div>

    </div>

    <div class="plan-current-detail">

        <c:choose>

            <c:when test="${plan.paid and plan.cancelRequested}">

                <span class="plan-current-detail-label">
                    有料機能の利用期限
                </span>

                <strong id="planDate">
                    <c:out
                        value="${plan.nextDate}"
                        default="未設定"/>
                </strong>

                <p>
                    利用期限までは、有料機能を引き続き利用できます。
                </p>

            </c:when>

            <c:when test="${plan.paid}">

                <span class="plan-current-detail-label">
                    次回更新日
                </span>

                <strong id="planDate">
                    <c:out
                        value="${plan.nextDate}"
                        default="未設定"/>
                </strong>

                <p>
                    月額
                    <c:out value="${price}"/>
                    円で自動更新されます。
                </p>

            </c:when>

            <c:otherwise>

                <span class="plan-current-detail-label">
                    月額料金
                </span>

                <strong>
                    0円
                </strong>

                <p>
                    有料プランへの変更は、主介護者が行えます。
                </p>

            </c:otherwise>

        </c:choose>

    </div>

</section>

<%-- =======================================================
     プラン比較
     ======================================================= --%>

<section
    class="plan-comparison-card"
    aria-labelledby="plan-comparison-title">

    <header class="plan-card-header">

        <span
            class="plan-card-header-icon"
            aria-hidden="true">
            比
        </span>

        <div>

            <h2
                class="plan-card-title"
                id="plan-comparison-title">
                プランを比較
            </h2>

            <p class="plan-card-description">
                無料プランと有料プランで利用できる機能を比較できます。
            </p>

        </div>

    </header>

    <div class="plan-comparison-body">

        <div class="plan-price-grid">

            <article class="plan-price-card${not plan.paid ? ' is-current' : ''}">

                <div class="plan-price-card-header">

                    <div>

                        <span class="plan-price-name">
                            無料プラン
                        </span>

                        <span class="plan-price-current-badge">

                            <c:if test="${not plan.paid}">
                                現在のプラン
                            </c:if>

                        </span>

                    </div>

                    <strong class="plan-price">
                        0
                        <small>円／月</small>
                    </strong>

                </div>

                <p class="plan-price-description">
                    Care Shiftの基本機能を利用できます。
                </p>

            </article>

            <article class="plan-price-card is-premium${plan.paid ? ' is-current' : ''}">

                <div class="plan-price-card-header">

                    <div>

                        <span class="plan-price-name">
                            有料プラン
                        </span>

                        <span class="plan-price-current-badge">

                            <c:if test="${plan.paid}">
                                現在のプラン
                            </c:if>

                        </span>

                    </div>

                    <strong class="plan-price">
                        <c:out value="${price}"/>
                        <small>円／月</small>
                    </strong>

                </div>

                <p class="plan-price-description">
                    すべての対象機能を利用できるプランです。
                </p>

            </article>

        </div>

        <div class="plan-table-wrapper">

            <table
                class="plan-comparison-table"
                id="features">

                <thead>

                    <tr>

                        <th scope="col">
                            機能
                        </th>

                        <th scope="col">
                            無料
                        </th>

                        <th
                            class="plan-paid-column"
                            scope="col">
                            有料
                        </th>

                    </tr>

                </thead>

                <tbody>

                    <c:forEach
                        var="f"
                        items="${features}">

                        <tr>

                            <th scope="row">
                                <c:out value="${f[0]}"/>
                            </th>

                            <td>
                                <c:out value="${f[1]}"/>
                            </td>

                            <td class="plan-paid-column">
                                <c:out value="${f[2]}"/>
                            </td>

                        </tr>

                    </c:forEach>

                </tbody>

            </table>

        </div>

        <div class="plan-trial-note">

            <span
                class="plan-trial-note-icon"
                aria-hidden="true">
                i
            </span>

            <p>
                金額・機能は試験用の仮の内容です。
                本番運用前に、料金、提供機能、決済条件を確定してください。
            </p>

        </div>

    </div>

</section>

<%-- =======================================================
     プラン操作
     ======================================================= --%>

<section
    class="plan-action-card"
    aria-labelledby="plan-action-title">

    <header class="plan-card-header">

        <span
            class="plan-card-header-icon"
            aria-hidden="true">
            設
        </span>

        <div>

            <h2
                class="plan-card-title"
                id="plan-action-title">
                プランの変更
            </h2>

            <p class="plan-card-description">
                家族グループに適用するプランを変更します。
            </p>

        </div>

    </header>

    <div class="plan-action-body">

        <c:choose>

            <%-- 主介護者 --%>
            <c:when test="${member.primary}">

                <c:choose>

                    <%-- 無料プラン --%>
                    <c:when test="${not plan.paid}">

                        <div class="plan-action-content">

                            <div>

                                <h3 class="plan-action-title">
                                    有料プランへ変更
                                </h3>

                                <p class="plan-action-description">
                                    金額と条件を確認してから、外部の決済サービスへ移動します。
                                    カード情報をCare Shiftで入力・保存することはありません。
                                </p>

                            </div>

                            <form
                                class="plan-action-form"
                                method="get"
                                action="${pageContext.request.contextPath}/careshift/main/PlanConfirm.action">

                                <button
                                    class="plan-button plan-button-primary"
                                    type="submit"
                                    name="upgrade">

                                    <span
                                        class="plan-button-icon"
                                        aria-hidden="true">
                                        P
                                    </span>

                                    <span>
                                        有料プランを確認
                                    </span>

                                </button>

                            </form>

                        </div>

                    </c:when>

                    <%-- 有料、解約未予約 --%>
                    <c:when test="${not plan.cancelRequested}">

                        <div class="plan-action-content">

                            <div>

                                <h3 class="plan-action-title">
                                    無料プランへ戻す
                                </h3>

                                <p class="plan-action-description">
                                    手続き後も、次回更新日までは有料機能を利用できます。
                                    次回更新日以降に無料プランへ切り替わります。
                                </p>

                            </div>

                            <form
                                class="plan-action-form"
                                method="post"
                                action="${pageContext.request.contextPath}/careshift/main/PlanDowngrade.action"
                                onsubmit="return confirm('無料プランへ戻します。次回更新日までは、有料機能を引き続き利用できます。よろしいですか？');">

                                <button
                                    class="plan-button plan-button-danger-outline"
                                    type="submit"
                                    name="downgrade">
                                    無料プランへ戻す
                                </button>

                            </form>

                        </div>

                    </c:when>

                    <%-- 解約予約済み --%>
                    <c:otherwise>

                        <div class="plan-cancelled-notice">

                            <span
                                class="plan-cancelled-notice-icon"
                                aria-hidden="true">
                                ✓
                            </span>

                            <div>

                                <strong>
                                    無料プランへの変更手続きが完了しています
                                </strong>

                                <p>
                                    <c:out value="${plan.nextDate}"/>
                                    までは有料機能を利用できます。
                                </p>

                            </div>

                        </div>

                    </c:otherwise>

                </c:choose>

            </c:when>

            <%-- 一般メンバー --%>
            <c:otherwise>

                <div class="plan-permission-notice">

                    <span
                        class="plan-permission-notice-icon"
                        aria-hidden="true">
                        鍵
                    </span>

                    <div>

                        <strong>
                            プランの変更は主介護者が行います
                        </strong>

                        <p>
                            この画面でプランの内容は確認できますが、
                            有料プランへの変更や無料プランへ戻す操作はできません。
                        </p>

                    </div>

                </div>

            </c:otherwise>

        </c:choose>

    </div>

</section>

<%-- =======================================================
     決済に関する案内
     ======================================================= --%>

<section class="plan-security-card">

    <span
        class="plan-security-icon"
        aria-hidden="true">
        鍵
    </span>

    <div>

        <h2 class="plan-security-title">
            お支払い情報について
        </h2>

        <p class="plan-security-description">
            有料プランのお支払いは外部の決済サービスで行います。
            Care Shiftではカード番号、セキュリティコード、有効期限を入力・保存しません。
            現在は開発・試験用の決済画面を使用しています。
        </p>

    </div>

</section>

<nav
    class="plan-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="plan-back-link"
        href="${pageContext.request.contextPath}/careshift/main/Settings.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            設定へ戻る
        </span>

    </a>

    <a
        class="plan-home-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">
        ホームへ戻る
    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>