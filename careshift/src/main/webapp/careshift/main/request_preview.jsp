<%-- S12 送信前プレビュー / 設計書: 画面設計書_依頼カード作成 --%>
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
        value="依頼内容の確認"/>
    <jsp:param
        name="pageClass"
        value="request-flow-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/request-flow.css?v=20261124-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="request-flow-header">

    <div>

        <p class="request-flow-eyebrow">
            REQUEST PREVIEW
        </p>

        <h1 class="request-flow-title">
            依頼内容の確認
        </h1>

        <p class="request-flow-description">
            家族へ送る依頼内容を確認してください。
            「送信」を押すまでは、家族へ通知されません。
        </p>

    </div>

    <a
        class="request-flow-header-link"
        href="${pageContext.request.contextPath}/careshift/main/RequestList.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            依頼一覧へ戻る
        </span>
    </a>

</section>

<%-- =======================================================
     ステップ表示
     ======================================================= --%>

<nav
    class="request-stepper"
    aria-label="依頼作成の進行状況">

    <div class="request-step is-complete">

        <span class="request-step-number">
            ✓
        </span>

        <span class="request-step-text">

            <strong>
                入力
            </strong>

            <small>
                入力済み
            </small>

        </span>

    </div>

    <span
        class="request-step-line is-complete"
        aria-hidden="true">
    </span>

    <div
        class="request-step is-current"
        aria-current="step">

        <span class="request-step-number">
            2
        </span>

        <span class="request-step-text">

            <strong>
                確認
            </strong>

            <small>
                送信内容を確認
            </small>

        </span>

    </div>

    <span
        class="request-step-line"
        aria-hidden="true">
    </span>

    <div class="request-step">

        <span class="request-step-number">
            3
        </span>

        <span class="request-step-text">

            <strong>
                送信
            </strong>

            <small>
                家族へ通知
            </small>

        </span>

    </div>

</nav>

<%-- =======================================================
     送信失敗
     ======================================================= --%>

<c:if test="${not empty sendError}">

    <div
        class="request-flow-alert is-error"
        id="sendError"
        role="alert">

        <span
            class="request-flow-alert-icon"
            aria-hidden="true">
            !
        </span>

        <div>

            <strong>
                依頼を送信できませんでした
            </strong>

            <p>
                <c:out value="${sendError}"/>
            </p>

        </div>

    </div>

</c:if>

<%-- =======================================================
     確認案内
     ======================================================= --%>

<section
    class="request-preview-guide"
    aria-labelledby="request-preview-guide-title">

    <span
        class="request-preview-guide-icon"
        aria-hidden="true">
        確
    </span>

    <div>

        <h2 id="request-preview-guide-title">
            送信前の最終確認
        </h2>

        <p>
            依頼内容、希望日時、所要時間、依頼先に間違いがないか確認してください。
            修正が必要な場合は「入力内容を修正する」を押してください。
        </p>

    </div>

</section>

<%-- =======================================================
     依頼内容
     ======================================================= --%>

<section
    class="request-preview-card"
    aria-labelledby="request-preview-title">

    <header class="request-preview-card-header">

        <div>

            <p class="request-flow-eyebrow">
                REQUEST DETAILS
            </p>

            <h2
                class="request-preview-card-title"
                id="request-preview-title">
                送信する依頼
            </h2>

        </div>

        <span class="request-preview-status">
            未送信
        </span>

    </header>

    <div class="request-preview-content">

        <section class="request-preview-message-section">

            <h3>
                依頼内容
            </h3>

            <p
                class="request-preview-message"
                id="previewContent">
                <c:out value="${form.content}"/>
            </p>

        </section>

        <dl class="request-preview-information">

            <div>

                <dt>
                    依頼種別
                </dt>

                <dd>

                    <span class="request-preview-type">

                        <span
                            class="request-preview-type-icon"
                            aria-hidden="true">
                            依
                        </span>

                        <c:choose>

                            <c:when test="${form.requestType == 'TASK'}">
                                タスク依頼
                            </c:when>

                            <c:otherwise>
                                <c:out value="${form.requestType}"/>
                            </c:otherwise>

                        </c:choose>

                    </span>

                </dd>

            </div>

            <div>

                <dt>
                    希望日時・期限
                </dt>

                <dd>

                    <span class="request-preview-date">

                        <span
                            class="request-preview-date-icon"
                            aria-hidden="true">
                            日
                        </span>

                        <c:out
                            value="${form.requestedAtLabel}"
                            default="未設定"/>

                    </span>

                </dd>

            </div>

            <div>

                <dt>
                    所要時間
                </dt>

                <dd>

                    <c:choose>

                        <c:when test="${empty form.duration}">

                            <span class="request-preview-empty-value">
                                指定なし
                            </span>

                        </c:when>

                        <c:otherwise>

                            <span class="request-preview-duration">
                                <c:out value="${form.duration}"/>分
                            </span>

                        </c:otherwise>

                    </c:choose>

                </dd>

            </div>

            <c:if test="${not empty form.taskId}">

                <div>

                    <dt>
                        関連タスク
                    </dt>

                    <dd>

                        <span class="request-preview-related-task">
                            タスクから作成した依頼です
                        </span>

                    </dd>

                </div>

            </c:if>

        </dl>

    </div>

</section>

<%-- =======================================================
     依頼先
     ======================================================= --%>

<section
    class="request-preview-recipient-card"
    aria-labelledby="request-preview-recipient-title">

    <header class="request-preview-recipient-header">

        <div>

            <p class="request-flow-eyebrow">
                RECIPIENTS
            </p>

            <h2
                class="request-preview-recipient-title"
                id="request-preview-recipient-title">
                依頼先
            </h2>

            <p>
                送信すると、次の家族へ通知されます。
            </p>

        </div>

        <span class="request-preview-recipient-count">

            <strong>
                <c:out value="${recipientNames.size()}"/>
            </strong>

            名
        </span>

    </header>

    <div class="request-preview-recipient-list">

        <c:forEach
            var="name"
            items="${recipientNames}">

            <div class="request-preview-recipient">

                <span
                    class="request-preview-recipient-avatar"
                    aria-hidden="true">
                    人
                </span>

                <div class="request-preview-recipient-content">

                    <strong>
                        <c:out value="${name}"/>
                    </strong>

                    <small>
                        依頼通知を送信
                    </small>

                </div>

                <span
                    class="request-preview-recipient-check"
                    aria-hidden="true">
                    ✓
                </span>

            </div>

        </c:forEach>

    </div>

</section>

<%-- =======================================================
     送信に関する案内
     ======================================================= --%>

<div class="request-preview-notice">

    <span
        class="request-preview-notice-icon"
        aria-hidden="true">
        i
    </span>

    <div>

        <strong>
            送信後について
        </strong>

        <p>
            家族が「担当する」と回答すると依頼は引受済みになります。
            回答待ちで期限前の依頼は、送信後に依頼一覧から取り下げることもできます。
        </p>

    </div>

</div>

<%-- =======================================================
     操作ボタン
     ======================================================= --%>

<div class="request-preview-actions">

    <form
        class="request-preview-back-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">

        <jsp:include page="/common/request_hidden.jsp"/>

        <button
            class="request-flow-button request-flow-button-secondary"
            type="submit"
            name="back">

            <span aria-hidden="true">
                &lsaquo;
            </span>

            <span>
                入力内容を修正する
            </span>

        </button>

    </form>

    <form
        class="request-preview-send-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/main/RequestSend.action"
        onsubmit="return requestPreviewConfirmSend(this);">

        <jsp:include page="/common/request_hidden.jsp"/>

        <button
            class="request-flow-button request-flow-button-primary request-preview-send-button"
            type="submit"
            name="send">

            <span
                class="request-flow-button-icon"
                aria-hidden="true">
                送
            </span>

            <span>
                この内容で送信する
            </span>

        </button>

    </form>

</div>

<nav
    class="request-flow-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a href="${pageContext.request.contextPath}/careshift/main/RequestList.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        依頼一覧へ戻る
    </a>

</nav>

<script>
(function () {
    "use strict";

    window.requestPreviewConfirmSend = function (form) {
        if (!window.confirm("この内容で家族へ依頼を送信します。よろしいですか？")) {
            return false;
        }

        var button = form.querySelector("button[type='submit']");

        if (button) {
            button.disabled = true;
            button.classList.add("is-sending");

            var label = button.querySelector("span:last-child");

            if (label) {
                label.textContent = "送信しています...";
            }
        }

        return true;
    };
})();
</script>

<jsp:include page="/common/footer.jsp"/>