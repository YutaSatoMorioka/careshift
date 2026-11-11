<%-- S13 依頼詳細・回答 / 設計書: 画面設計書_依頼回答 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="依頼詳細・回答"/>
    <jsp:param name="pageClass" value="request-flow-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/request-flow.css?v=20261111-1">

<section class="request-flow-header">

    <div>
        <p class="request-flow-eyebrow">
            REQUEST DETAIL
        </p>

        <h1 class="request-flow-title">
            依頼詳細・回答
        </h1>

        <p class="request-flow-description">
            依頼内容と回答状況を確認できます。
        </p>
    </div>

    <a
        class="request-flow-header-link"
        href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=${isSender ? 'sent' : 'received'}">
        <span aria-hidden="true">&lsaquo;</span>
        <span>依頼一覧へ戻る</span>
    </a>

</section>

<section
    class="request-detail-card"
    aria-labelledby="request-detail-title">

    <header class="request-detail-card-header">

        <div class="request-detail-labels">

            <span class="request-detail-type">
                <c:out
                    value="${card.typeLabel}"
                    default="依頼"/>
            </span>

            <span class="request-detail-status
                ${card.status == 'ACCEPTED' ? ' is-accepted' : ''}
                ${card.status == 'WITHDRAWN' ? ' is-withdrawn' : ''}
                ${card.expired ? ' is-expired' : ''}">

                <c:out
                    value="${card.statusLabel}"
                    default="回答待ち"/>
            </span>

        </div>

        <c:if test="${not empty card.taskId}">
            <a
                class="request-related-task-link"
                href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${card.taskId}">
                関連タスクを見る
                <span aria-hidden="true">&rsaquo;</span>
            </a>
        </c:if>

    </header>

    <div class="request-detail-content">

        <h2
            class="request-detail-content-title"
            id="request-detail-title">
            依頼内容
        </h2>

        <p
            class="request-detail-message"
            id="requestContent">
            <c:out value="${card.content}"/>
        </p>

    </div>

    <dl class="request-detail-information">

        <div>
            <dt>依頼者</dt>

            <dd>
                <span class="request-detail-person">

                    <span
                        class="request-detail-person-avatar"
                        aria-hidden="true">
                        人
                    </span>

                    <span>
                        <c:out
                            value="${card.senderName}"
                            default="依頼者"/>
                    </span>

                </span>
            </dd>
        </div>

        <div>
            <dt>希望日時・期限</dt>

            <dd>
                <span class="request-detail-date">
                    <span aria-hidden="true">日</span>

                    <c:out
                        value="${card.requestedAtLabel}"
                        default="未設定"/>
                </span>
            </dd>
        </div>

        <c:if test="${not empty card.durationMin}">
            <div>
                <dt>所要時間</dt>

                <dd>
                    <c:out value="${card.durationMin}"/>分
                </dd>
            </div>
        </c:if>

        <div>
            <dt>状態</dt>

            <dd id="requestStatus">
                <c:out
                    value="${card.statusLabel}"
                    default="回答待ち"/>
            </dd>
        </div>

    </dl>

</section>

<c:choose>

    <%-- =====================================================
         送信者
         ===================================================== --%>

    <c:when test="${isSender}">

        <section
            class="request-response-card"
            aria-labelledby="recipient-list-title">

            <header class="request-response-card-header">

                <div>
                    <p class="request-flow-eyebrow">
                        RESPONSES
                    </p>

                    <h2
                        class="request-response-card-title"
                        id="recipient-list-title">
                        宛先と回答
                    </h2>
                </div>

                <span class="request-response-count">
                    <c:out value="${card.recipients.size()}"/>
                    名
                </span>

            </header>

            <div class="request-recipient-response-list">

                <c:forEach
                    var="p"
                    items="${card.recipients}">

                    <article class="request-recipient-response">

                        <div class="request-recipient-response-main">

                            <span
                                class="request-response-avatar"
                                aria-hidden="true">
                                人
                            </span>

                            <div>
                                <h3 class="request-response-name">
                                    <c:out
                                        value="${p.name}"
                                        default="家族"/>
                                </h3>

                                <span class="request-response-label">
                                    <c:out
                                        value="${p.responseLabel}"
                                        default="未回答"/>
                                </span>
                            </div>

                        </div>

                        <div class="request-recipient-response-detail">

                            <c:if test="${not empty p.alternative}">
                                <div class="request-alternative">

                                    <strong>
                                        代替案
                                    </strong>

                                    <p>
                                        <c:out value="${p.alternative}"/>
                                    </p>

                                </div>
                            </c:if>

                            <c:if test="${not empty p.respondedAtLabel}">
                                <time class="request-responded-at">
                                    回答日時：
                                    <c:out value="${p.respondedAtLabel}"/>
                                </time>
                            </c:if>

                        </div>

                    </article>

                </c:forEach>

            </div>

        </section>

        <section class="request-withdraw-section">

            <c:choose>

                <c:when test="${card.withdrawable}">

                    <div>

                        <h2 class="request-withdraw-title">
                            依頼の取り下げ
                        </h2>

                        <p class="request-withdraw-description">
                            取り下げると、宛先の家族へ通知されます。
                        </p>

                    </div>

                    <form
                        method="post"
                        action="${pageContext.request.contextPath}/careshift/main/RequestWithdraw.action"
                        onsubmit="return confirm('この依頼を取り下げます。宛先の家族にも通知されます。よろしいですか？');">

                        <input
                            type="hidden"
                            name="id"
                            value="${card.id}">

                        <button
                            class="request-flow-button request-flow-button-danger"
                            type="submit"
                            name="withdraw">
                            依頼を取り下げる
                        </button>

                    </form>

                </c:when>

                <c:otherwise>

                    <div class="request-blocked-message">

                        <span
                            class="request-blocked-icon"
                            aria-hidden="true">
                            i
                        </span>

                        <p>
                            <c:out value="${card.withdrawBlockedReason}"/>
                        </p>

                    </div>

                </c:otherwise>

            </c:choose>

        </section>

    </c:when>

    <%-- =====================================================
         受信者
         ===================================================== --%>

    <c:otherwise>

        <c:choose>

            <c:when test="${not canAnswer}">

                <div
                    class="request-blocked-message"
                    id="blockedReason"
                    role="alert">

                    <span
                        class="request-blocked-icon"
                        aria-hidden="true">
                        !
                    </span>

                    <div>
                        <strong>この依頼には回答できません</strong>

                        <p>
                            <c:out value="${blockedReason}"/>
                        </p>
                    </div>

                </div>

            </c:when>

            <c:otherwise>

                <section
                    class="request-answer-card"
                    aria-labelledby="request-answer-title">

                    <header class="request-answer-card-header">

                        <span
                            class="request-answer-icon"
                            aria-hidden="true">
                            回
                        </span>

                        <div>
                            <h2
                                class="request-answer-title"
                                id="request-answer-title">
                                依頼に回答する
                            </h2>

                            <p class="request-answer-description">
                                現在の状況に合う回答を選択してください。
                            </p>
                        </div>

                    </header>

                    <c:if test="${confirmAccept}">

                        <div
                            class="request-confirm-accept"
                            id="confirmAccept"
                            role="alert">

                            <span
                                class="request-confirm-icon"
                                aria-hidden="true">
                                ✓
                            </span>

                            <div>
                                <strong>
                                    担当として引き受けますか？
                                </strong>

                                <p>
                                    「担当する」を確定すると、担当者として設定され、
                                    依頼者へ通知されます。
                                </p>
                            </div>

                        </div>

                    </c:if>

                    <form
                        class="request-answer-form"
                        method="post"
                        action="${pageContext.request.contextPath}/careshift/main/RequestRespond.action">

                        <input
                            type="hidden"
                            name="id"
                            value="${card.id}">

                        <c:if test="${confirmAccept}">
                            <input
                                type="hidden"
                                name="confirm"
                                value="1">
                        </c:if>

                        <fieldset
                            class="request-answer-fieldset"
                            <c:if test="${not empty errors['response']}">
                                aria-describedby="response-error"
                            </c:if>>

                            <legend>
                                回答
                                <span class="request-required">必須</span>
                            </legend>

                            <div class="request-answer-options">

                                <c:forEach
                                    var="r"
                                    items="${responses}">

                                    <label class="request-answer-option">

                                        <input
                                            type="radio"
                                            name="response"
                                            value="<c:out value='${r.key}'/>"
                                            ${r.key == selected ? 'checked' : ''}>

                                        <span
                                            class="request-answer-option-mark"
                                            aria-hidden="true">
                                        </span>

                                        <span class="request-answer-option-content">
                                            <strong>
                                                <c:out value="${r.value}"/>
                                            </strong>

                                            <small>
                                                <c:choose>
                                                    <c:when test="${r.key == 'ACCEPT'}">
                                                        自分が担当として引き受けます。
                                                    </c:when>

                                                    <c:when test="${r.key == 'DECLINE'}">
                                                        今回は対応が難しいことを伝えます。
                                                    </c:when>

                                                    <c:otherwise>
                                                        別の方法やタスクを提案します。
                                                    </c:otherwise>
                                                </c:choose>
                                            </small>
                                        </span>

                                    </label>

                                </c:forEach>

                            </div>

                            <c:if test="${not empty errors['response']}">
                                <span
                                    class="request-field-error"
                                    id="response-error"
                                    role="alert">
                                    <c:out value="${errors['response']}"/>
                                </span>
                            </c:if>

                        </fieldset>

                        <div class="request-form-field">

                            <div class="request-label-row">

                                <label for="alternative">
                                    代替案
                                </label>

                                <span class="request-character-limit">
                                    300文字以内
                                </span>

                            </div>

                            <textarea
                                class="request-form-control request-form-textarea request-alternative-textarea${not empty errors['alternative'] ? ' is-error' : ''}"
                                id="alternative"
                                name="alternative"
                                rows="4"
                                maxlength="300"
                                placeholder="「別タスクなら可能」を選択した場合は、対応できる内容を入力してください。"
                                <c:if test="${not empty errors['alternative']}">
                                    aria-invalid="true"
                                    aria-describedby="alternative-error"
                                </c:if>><c:out value="${alternative}"/></textarea>

                            <c:if test="${not empty errors['alternative']}">
                                <span
                                    class="request-field-error"
                                    id="alternative-error"
                                    role="alert">
                                    <c:out value="${errors['alternative']}"/>
                                </span>
                            </c:if>

                        </div>

                        <div class="request-answer-actions">

                            <a
                                class="request-flow-button request-flow-button-secondary"
                                href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=received">
                                キャンセル
                            </a>

                            <button
                                class="request-flow-button request-flow-button-primary"
                                type="submit"
                                name="submit">

                                <c:choose>
                                    <c:when test="${confirmAccept}">
                                        はい、引き受けて回答する
                                    </c:when>

                                    <c:otherwise>
                                        回答を送信
                                    </c:otherwise>
                                </c:choose>

                            </button>

                        </div>

                    </form>

                </section>

            </c:otherwise>

        </c:choose>

    </c:otherwise>

</c:choose>

<nav
    class="request-flow-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        href="${pageContext.request.contextPath}/careshift/main/RequestList.action?tab=${isSender ? 'sent' : 'received'}">
        <span aria-hidden="true">&lsaquo;</span>
        依頼一覧へ戻る
    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>