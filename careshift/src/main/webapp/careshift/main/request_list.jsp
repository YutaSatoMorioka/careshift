<%-- S11 依頼一覧 / 設計書: S11_依頼一覧 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="依頼一覧"/>
    <jsp:param name="pageClass" value="request-list-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/request-list.css?v=20261111-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="request-page-header">

    <div class="request-page-heading">

        <p class="request-page-eyebrow">
            REQUEST MANAGEMENT
        </p>

        <h1 class="request-page-title">
            依頼一覧
        </h1>

        <p class="request-page-description">
            家族から受け取った依頼と、自分が送った依頼の回答状況を確認できます。
        </p>

    </div>

    <a
        class="request-button request-button-primary"
        id="createRequest"
        href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">

        <span
            class="request-button-icon"
            aria-hidden="true">
            ＋
        </span>

        <span>家族に依頼する</span>
    </a>

</section>

<%-- =======================================================
     依頼区分タブ
     ======================================================= --%>

<nav
    class="request-tabs"
    id="requestTab"
    aria-label="依頼区分">

    <a
        class="request-tab${tab == 'received' ? ' is-active' : ''}"
        href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=received"
        <c:if test="${tab == 'received'}">
            aria-current="page"
        </c:if>>

        <span
            class="request-tab-icon"
            aria-hidden="true">
            受
        </span>

        <span class="request-tab-content">

            <strong>
                受け取った依頼
            </strong>

            <small>
                家族から届いた依頼
            </small>

        </span>

    </a>

    <a
        class="request-tab${tab == 'sent' ? ' is-active' : ''}"
        href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=sent"
        <c:if test="${tab == 'sent'}">
            aria-current="page"
        </c:if>>

        <span
            class="request-tab-icon"
            aria-hidden="true">
            送
        </span>

        <span class="request-tab-content">

            <strong>
                送った依頼
            </strong>

            <small>
                回答状況を確認
            </small>

        </span>

    </a>

</nav>

<%-- =======================================================
     一覧カード
     ======================================================= --%>

<section
    class="request-result-card"
    aria-labelledby="request-result-title">

    <header class="request-result-header">

        <div>

            <p class="request-section-eyebrow">
                <c:choose>
                    <c:when test="${tab == 'sent'}">
                        SENT REQUESTS
                    </c:when>

                    <c:otherwise>
                        RECEIVED REQUESTS
                    </c:otherwise>
                </c:choose>
            </p>

            <h2
                class="request-result-title"
                id="request-result-title">

                <c:choose>
                    <c:when test="${tab == 'sent'}">
                        送った依頼
                    </c:when>

                    <c:otherwise>
                        受け取った依頼
                    </c:otherwise>
                </c:choose>

            </h2>

        </div>

        <div class="request-result-count">

            <strong>
                <c:choose>
                    <c:when test="${empty requests}">
                        0
                    </c:when>

                    <c:otherwise>
                        <c:out value="${requests.size()}"/>
                    </c:otherwise>
                </c:choose>
            </strong>

            <span>件</span>

        </div>

    </header>

    <c:choose>

        <%-- =================================================
             読み込み失敗
             ================================================= --%>

        <c:when test="${loadError}">

            <div
                class="request-error-state"
                id="loadError"
                role="alert">

                <span
                    class="request-error-icon"
                    aria-hidden="true">
                    !
                </span>

                <h3 class="request-error-title">
                    依頼を読み込めませんでした
                </h3>

                <p class="request-error-description">
                    通信状態を確認してから、もう一度お試しください。
                </p>

                <a
                    class="request-button request-button-secondary"
                    href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=${tab}">
                    再試行する
                </a>

            </div>

        </c:when>

        <%-- =================================================
             空状態
             ================================================= --%>

        <c:when test="${empty requests}">

            <div
                class="request-empty-state"
                id="empty">

                <span
                    class="request-empty-icon"
                    aria-hidden="true">

                    <c:choose>
                        <c:when test="${tab == 'sent'}">
                            送
                        </c:when>

                        <c:otherwise>
                            受
                        </c:otherwise>
                    </c:choose>

                </span>

                <h3 class="request-empty-title">

                    <c:choose>
                        <c:when test="${tab == 'sent'}">
                            送った依頼はまだありません
                        </c:when>

                        <c:otherwise>
                            受け取った依頼はまだありません
                        </c:otherwise>
                    </c:choose>

                </h3>

                <p class="request-empty-description">

                    <c:choose>
                        <c:when test="${tab == 'sent'}">
                            家族にお願いしたいことがある場合は、新しい依頼を作成してください。
                        </c:when>

                        <c:otherwise>
                            家族から依頼が届くと、この画面に表示されます。
                        </c:otherwise>
                    </c:choose>

                </p>

                <a
                    class="request-button request-button-primary"
                    href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">
                    家族に依頼する
                </a>

            </div>

        </c:when>

        <%-- =================================================
             依頼一覧
             ================================================= --%>

        <c:otherwise>

            <div
                class="request-card-list"
                id="requestList">

                <c:forEach
                    var="r"
                    items="${requests}">

                    <article class="request-item-card
                        ${r.withdrawable ? ' request-item-open' : ''}
                        ${r.expired ? ' request-item-expired' : ''}
                        ${r.status == 'ACCEPTED' ? ' request-item-accepted' : ''}
                        ${r.status == 'WITHDRAWN' ? ' request-item-withdrawn' : ''}">

                        <header class="request-item-header">

                            <div class="request-item-labels">

                                <span class="request-type-badge">
                                    <c:out
                                        value="${r.typeLabel}"
                                        default="依頼"/>
                                </span>

                                <span class="request-status-badge">

                                    <c:choose>

                                        <c:when test="${r.status == 'ACCEPTED'}">
                                            引受済み
                                        </c:when>

                                        <c:when test="${r.status == 'WITHDRAWN'}">
                                            取り下げ済み
                                        </c:when>

                                        <c:when test="${r.expired}">
                                            期限切れ
                                        </c:when>

                                        <c:otherwise>
                                            回答待ち
                                        </c:otherwise>

                                    </c:choose>

                                </span>

                            </div>

                            <time class="request-item-date">
                                <span
                                    class="request-item-date-icon"
                                    aria-hidden="true">
                                    日
                                </span>

                                <c:out
                                    value="${r.requestedAtLabel}"
                                    default="未設定"/>
                            </time>

                        </header>

                        <div class="request-item-body">

                            <div class="request-item-main">

                                <a
                                    class="request-item-content"
                                    href="${pageContext.request.contextPath}/careshift/main/RequestDetail.action?id=${r.id}">

                                    <c:out value="${r.content}"/>

                                </a>

                                <dl class="request-item-meta">

                                    <c:choose>

                                        <c:when test="${tab == 'sent'}">

                                            <div class="request-meta-row">

                                                <dt>
                                                    宛先と回答
                                                </dt>

                                                <dd>

                                                    <c:choose>

                                                        <c:when test="${empty r.recipients}">
                                                            宛先情報なし
                                                        </c:when>

                                                        <c:otherwise>

                                                            <div class="request-recipient-list">

                                                                <c:forEach
                                                                    var="p"
                                                                    items="${r.recipients}">

                                                                    <span class="request-recipient">

                                                                        <span
                                                                            class="request-recipient-avatar"
                                                                            aria-hidden="true">
                                                                            人
                                                                        </span>

                                                                        <span class="request-recipient-info">

                                                                            <strong>
                                                                                <c:out
                                                                                    value="${p.name}"
                                                                                    default="家族"/>
                                                                            </strong>

                                                                            <small>
                                                                                <c:out
                                                                                    value="${p.responseLabel}"
                                                                                    default="未回答"/>
                                                                            </small>

                                                                        </span>

                                                                    </span>

                                                                </c:forEach>

                                                            </div>

                                                        </c:otherwise>

                                                    </c:choose>

                                                </dd>

                                            </div>

                                        </c:when>

                                        <c:otherwise>

                                            <div class="request-meta-row">

                                                <dt>
                                                    依頼者
                                                </dt>

                                                <dd>

                                                    <span class="request-sender">

                                                        <span
                                                            class="request-sender-avatar"
                                                            aria-hidden="true">
                                                            人
                                                        </span>

                                                        <span>
                                                            <c:out
                                                                value="${r.senderName}"
                                                                default="依頼者"/>
                                                        </span>

                                                    </span>

                                                </dd>

                                            </div>

                                            <div class="request-meta-row">

                                                <dt>
                                                    あなたの回答
                                                </dt>

                                                <dd>

                                                    <c:set
                                                        var="myResponseFound"
                                                        value="false"/>

                                                    <c:forEach
                                                        var="p"
                                                        items="${r.recipients}">

                                                        <c:if test="${p.userId == sessionScope.user.id}">

                                                            <span class="request-my-response">
                                                                <c:out
                                                                    value="${p.responseLabel}"
                                                                    default="未回答"/>
                                                            </span>

                                                            <c:set
                                                                var="myResponseFound"
                                                                value="true"/>

                                                        </c:if>

                                                    </c:forEach>

                                                    <c:if test="${not myResponseFound}">

                                                        <span class="request-my-response">
                                                            未回答
                                                        </span>

                                                    </c:if>

                                                </dd>

                                            </div>

                                        </c:otherwise>

                                    </c:choose>

                                    <div class="request-meta-row">

                                        <dt>
                                            現在の状態
                                        </dt>

                                        <dd>
                                            <c:out
                                                value="${r.statusLabel}"
                                                default="回答待ち"/>
                                        </dd>

                                    </div>

                                </dl>

                            </div>

                            <aside class="request-item-actions">

                                <a
                                    class="request-detail-link"
                                    href="${pageContext.request.contextPath}/careshift/main/RequestDetail.action?id=${r.id}">

                                    <span>
                                        <c:choose>
                                            <c:when test="${tab == 'sent'}">
                                                詳細を見る
                                            </c:when>

                                            <c:otherwise>
                                                詳細・回答
                                            </c:otherwise>
                                        </c:choose>
                                    </span>

                                    <span aria-hidden="true">
                                        &rsaquo;
                                    </span>

                                </a>

                                <c:if test="${tab == 'sent'}">

                                    <c:choose>

                                        <c:when test="${r.withdrawable}">

                                            <form
                                                class="request-withdraw-form"
                                                method="post"
                                                action="${pageContext.request.contextPath}/careshift/main/RequestWithdraw.action"
                                                onsubmit="return confirm('この依頼を取り下げます。宛先の家族にも通知されます。よろしいですか？');">

                                                <input
                                                    type="hidden"
                                                    name="id"
                                                    value="${r.id}">

                                                <button
                                                    class="request-withdraw-button"
                                                    type="submit"
                                                    name="withdraw">
                                                    依頼を取り下げる
                                                </button>

                                            </form>

                                        </c:when>

                                        <c:otherwise>

                                            <p class="request-withdraw-reason">
                                                <c:out value="${r.withdrawBlockedReason}"/>
                                            </p>

                                        </c:otherwise>

                                    </c:choose>

                                </c:if>

                            </aside>

                        </div>

                    </article>

                </c:forEach>

            </div>

        </c:otherwise>

    </c:choose>

</section>

<%-- =======================================================
     下部ナビゲーション
     ======================================================= --%>

<nav
    class="request-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="request-back-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>ホームへ戻る</span>

    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>