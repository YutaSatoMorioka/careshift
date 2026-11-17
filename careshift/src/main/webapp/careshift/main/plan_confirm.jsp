<%-- S21 有料へ切替：金額・条件の確認 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="有料プランの確認"/>
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
            CONFIRM SUBSCRIPTION
        </p>

        <h1 class="plan-page-title">
            有料プランの確認
        </h1>

        <p class="plan-page-description">
            料金、利用条件、利用できる機能を確認してからお申し込みください。
        </p>

    </div>

    <a
        class="plan-header-link"
        href="${pageContext.request.contextPath}/careshift/main/Plan.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            プラン画面へ戻る
        </span>

    </a>

</section>

<%-- =======================================================
     申込ステップ
     ======================================================= --%>

<nav
    class="plan-confirm-stepper"
    aria-label="有料プラン申込の進行状況">

    <div class="plan-confirm-step is-current">

        <span class="plan-confirm-step-number">
            1
        </span>

        <span class="plan-confirm-step-content">

            <strong>
                内容確認
            </strong>

            <small>
                料金と条件を確認
            </small>

        </span>

    </div>

    <span
        class="plan-confirm-step-line"
        aria-hidden="true">
    </span>

    <div class="plan-confirm-step">

        <span class="plan-confirm-step-number">
            2
        </span>

        <span class="plan-confirm-step-content">

            <strong>
                お支払い
            </strong>

            <small>
                決済サービスへ移動
            </small>

        </span>

    </div>

    <span
        class="plan-confirm-step-line"
        aria-hidden="true">
    </span>

    <div class="plan-confirm-step">

        <span class="plan-confirm-step-number">
            3
        </span>

        <span class="plan-confirm-step-content">

            <strong>
                完了
            </strong>

            <small>
                有料機能を開始
            </small>

        </span>

    </div>

</nav>

<%-- =======================================================
     料金
     ======================================================= --%>

<section
    class="plan-confirm-price-card"
    aria-labelledby="plan-confirm-price-title">

    <div class="plan-confirm-price-main">

        <span
            class="plan-confirm-price-icon"
            aria-hidden="true">
            P
        </span>

        <div>

            <span class="plan-confirm-price-label">
                お申し込み内容
            </span>

            <h2
                class="plan-confirm-price-name"
                id="plan-confirm-price-title">
                Care Shift 有料プラン
            </h2>

            <p class="plan-confirm-price-description">
                お申し込み日から1か月ごとに自動更新されます。
            </p>

        </div>

    </div>

    <div class="plan-confirm-price">

        <span>
            月額
        </span>

        <strong id="price">
            <c:out value="${price}"/>
            <small>円</small>
        </strong>

    </div>

</section>

<%-- =======================================================
     契約条件
     ======================================================= --%>

<section
    class="plan-confirm-card"
    aria-labelledby="plan-confirm-conditions-title">

    <header class="plan-card-header">

        <span
            class="plan-card-header-icon"
            aria-hidden="true">
            条
        </span>

        <div>

            <h2
                class="plan-card-title"
                id="plan-confirm-conditions-title">
                お申し込み条件
            </h2>

            <p class="plan-card-description">
                自動更新、解約、決済方法についてご確認ください。
            </p>

        </div>

    </header>

    <div class="plan-confirm-card-body">

        <ul class="plan-condition-list">

            <li>

                <span
                    class="plan-condition-number"
                    aria-hidden="true">
                    1
                </span>

                <div>

                    <strong>
                        1か月ごとの自動更新
                    </strong>

                    <p>
                        お申し込みの日から1か月ごとに自動で更新されます。
                        次回更新日は、決済完了後のプラン画面に表示されます。
                    </p>

                </div>

            </li>

            <li>

                <span
                    class="plan-condition-number"
                    aria-hidden="true">
                    2
                </span>

                <div>

                    <strong>
                        無料プランへ戻す場合
                    </strong>

                    <p>
                        「無料へ戻す」手続きをしても、次回更新日までは有料機能を利用できます。
                    </p>

                </div>

            </li>

            <li>

                <span
                    class="plan-condition-number"
                    aria-hidden="true">
                    3
                </span>

                <div>

                    <strong>
                        外部の決済サービスを使用
                    </strong>

                    <p>
                        お支払いは外部の決済サービス画面で行います。
                        カード情報はCare Shiftでは入力・保存しません。
                    </p>

                </div>

            </li>

            <li>

                <span
                    class="plan-condition-number"
                    aria-hidden="true">
                    4
                </span>

                <div>

                    <strong>
                        決済成功後に有料機能を開始
                    </strong>

                    <p>
                        決済が成功すると有料機能が利用可能になります。
                        決済の失敗またはキャンセル時は無料プランのままです。
                    </p>

                </div>

            </li>

        </ul>

    </div>

</section>

<%-- =======================================================
     機能比較
     ======================================================= --%>

<section
    class="plan-confirm-card"
    aria-labelledby="plan-confirm-features-title">

    <header class="plan-card-header">

        <span
            class="plan-card-header-icon"
            aria-hidden="true">
            比
        </span>

        <div>

            <h2
                class="plan-card-title"
                id="plan-confirm-features-title">
                利用できる機能
            </h2>

            <p class="plan-card-description">
                申し込み後に利用できる機能をご確認ください。
            </p>

        </div>

    </header>

    <div class="plan-confirm-card-body">

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

    </div>

</section>

<%-- =======================================================
     決済に関する重要事項
     ======================================================= --%>

<section class="plan-confirm-security">

    <span
        class="plan-confirm-security-icon"
        aria-hidden="true">
        鍵
    </span>

    <div>

        <h2 class="plan-confirm-security-title">
            カード情報はCare Shiftに保存されません
        </h2>

        <p class="plan-confirm-security-description">
            次の画面から外部の決済サービスへ移動します。
            Care Shiftがカード番号、セキュリティコード、有効期限を取得することはありません。
            現在は開発・試験用の決済画面を使用しています。
        </p>

    </div>

</section>

<%-- =======================================================
     申込操作
     ======================================================= --%>

<div class="plan-confirm-actions">

    <a
        class="plan-button plan-button-secondary"
        href="${pageContext.request.contextPath}/careshift/main/Plan.action">
        やめて戻る
    </a>

    <form
        class="plan-checkout-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/main/PlanCheckout.action"
        onsubmit="return confirm('月額<c:out value="${price}"/>円の有料プランを申し込みます。決済サービスへ移動してよろしいですか？');">

        <button
            class="plan-button plan-button-primary plan-checkout-button"
            type="submit"
            name="checkout">

            <span
                class="plan-button-icon"
                aria-hidden="true">
                &rsaquo;
            </span>

            <span>
                決済サービスへ進む
            </span>

        </button>

    </form>

</div>

<div class="plan-confirm-final-note">

    <span
        class="plan-confirm-final-note-icon"
        aria-hidden="true">
        i
    </span>

    <p>
        このボタンを押した時点では申込完了ではありません。
        決済サービスで決済が成功した後に、有料プランへ切り替わります。
    </p>

</div>

<nav
    class="plan-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="plan-back-link"
        href="${pageContext.request.contextPath}/careshift/main/Plan.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            プラン画面へ戻る
        </span>

    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>