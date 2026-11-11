<%-- S12 依頼カード作成 / 設計書: 画面設計書_依頼カード作成 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="依頼カード作成"/>
    <jsp:param name="pageClass" value="request-flow-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/request-flow.css?v=20261111-2">

<section class="request-flow-header">

    <div>

        <p class="request-flow-eyebrow">
            NEW REQUEST
        </p>

        <h1 class="request-flow-title">
            依頼カード作成
        </h1>

        <p class="request-flow-description">
            家族にお願いしたい内容、希望日時、依頼先を入力してください。
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

<nav
    class="request-stepper"
    aria-label="依頼作成の進行状況">

    <div class="request-step is-current">

        <span class="request-step-number">
            1
        </span>

        <span class="request-step-text">

            <strong>
                入力
            </strong>

            <small>
                依頼内容を入力
            </small>

        </span>

    </div>

    <span
        class="request-step-line"
        aria-hidden="true">
    </span>

    <div class="request-step">

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

<c:choose>

    <%-- 依頼を送れる家族がいない場合 --%>
    <c:when test="${empty others}">

        <section class="request-no-recipient-card">

            <span
                class="request-no-recipient-icon"
                aria-hidden="true">
                人
            </span>

            <h2 class="request-no-recipient-title">
                依頼を送れる家族がいません
            </h2>

            <p class="request-no-recipient-description">
                依頼の宛先には、自分以外の家族グループメンバーを指定します。
                家族を招待してから、もう一度依頼を作成してください。
            </p>

            <div class="request-no-recipient-actions">

                <a
                    class="request-flow-button request-flow-button-primary"
                    href="${pageContext.request.contextPath}/careshift/main/Group.action">
                    家族を招待する
                </a>

                <a
                    class="request-flow-button request-flow-button-secondary"
                    href="${pageContext.request.contextPath}/careshift/main/RequestList.action">
                    依頼一覧へ戻る
                </a>

            </div>

        </section>

    </c:when>

    <%-- 依頼を送れる家族がいる場合 --%>
    <c:otherwise>

        <form
            class="request-create-form"
            method="post"
            action="${pageContext.request.contextPath}/careshift/main/RequestPreview.action">

            <input
                type="hidden"
                name="taskId"
                value="<c:out value='${form.taskId}'/>">

            <%-- =============================================
                 依頼内容
                 ============================================= --%>

            <section class="request-form-card">

                <header class="request-form-card-header">

                    <span
                        class="request-form-card-icon"
                        aria-hidden="true">
                        1
                    </span>

                    <div>

                        <h2 class="request-form-card-title">
                            依頼内容
                        </h2>

                        <p class="request-form-card-description">
                            依頼の種類と、家族に伝える内容を入力します。
                        </p>

                    </div>

                </header>

                <div class="request-form-card-body">

                    <div class="request-form-field">

                        <label for="requestType">

                            依頼種別

                            <span class="request-required">
                                必須
                            </span>

                        </label>

                        <select
                            class="request-form-control${not empty errors['requestType'] ? ' is-error' : ''}"
                            id="requestType"
                            name="requestType"
                            aria-invalid="${not empty errors['requestType']}">

                            <c:forEach
                                var="t"
                                items="${types}">

                                <option
                                    value="<c:out value='${t.key}'/>"
                                    ${t.key == form.requestType ? 'selected' : ''}>
                                    <c:out value="${t.value}"/>
                                </option>

                            </c:forEach>

                        </select>

                        <c:if test="${not empty errors['requestType']}">

                            <span
                                class="request-field-error"
                                id="requestType-error"
                                role="alert">
                                <c:out value="${errors['requestType']}"/>
                            </span>

                        </c:if>

                        <p class="request-field-help">
                            休息の依頼は、主介護者が「休息リクエスト」画面から送信します。
                        </p>

                    </div>

                    <div class="request-form-field">

                        <div class="request-label-row">

                            <label for="content">

                                依頼内容

                                <span class="request-required">
                                    必須
                                </span>

                            </label>

                            <span class="request-character-limit">
                                500文字以内
                            </span>

                        </div>

                        <textarea
                            class="request-form-control request-form-textarea${not empty errors['content'] ? ' is-error' : ''}"
                            id="content"
                            name="content"
                            rows="6"
                            maxlength="500"
                            aria-invalid="${not empty errors['content']}"
                            placeholder="例：11月15日の通院に付き添ってもらえますか？"><c:out value="${form.content}"/></textarea>

                        <c:if test="${not empty errors['content']}">

                            <span
                                class="request-field-error"
                                id="content-error"
                                role="alert">
                                <c:out value="${errors['content']}"/>
                            </span>

                        </c:if>

                    </div>

                </div>

            </section>

            <%-- =============================================
                 日時と所要時間
                 ============================================= --%>

            <section class="request-form-card">

                <header class="request-form-card-header">

                    <span
                        class="request-form-card-icon"
                        aria-hidden="true">
                        2
                    </span>

                    <div>

                        <h2 class="request-form-card-title">
                            日時と所要時間
                        </h2>

                        <p class="request-form-card-description">
                            希望日時・期限と、想定する所要時間を入力します。
                        </p>

                    </div>

                </header>

                <div class="request-form-card-body">

                    <div class="request-form-grid">

                        <div class="request-form-field">

                            <label for="requestedAt">

                                希望日時・期限

                                <span class="request-required">
                                    必須
                                </span>

                            </label>

                            <input
                                class="request-form-control${not empty errors['requestedAt'] ? ' is-error' : ''}"
                                type="datetime-local"
                                id="requestedAt"
                                name="requestedAt"
                                value="<c:out value='${form.requestedAt}'/>"
                                aria-invalid="${not empty errors['requestedAt']}">

                            <c:if test="${not empty errors['requestedAt']}">

                                <span
                                    class="request-field-error"
                                    id="requestedAt-error"
                                    role="alert">
                                    <c:out value="${errors['requestedAt']}"/>
                                </span>

                            </c:if>

                            <p class="request-field-help">
                                現在より後の日時を指定してください。
                            </p>

                        </div>

                        <div class="request-form-field">

                            <label for="duration">

                                所要時間

                                <span class="request-optional">
                                    任意
                                </span>

                            </label>

                            <div class="request-number-wrapper">

                                <input
                                    class="request-form-control${not empty errors['duration'] ? ' is-error' : ''}"
                                    type="number"
                                    id="duration"
                                    name="duration"
                                    min="5"
                                    max="1440"
                                    step="5"
                                    value="<c:out value='${form.duration}'/>"
                                    aria-invalid="${not empty errors['duration']}"
                                    placeholder="60">

                                <span class="request-number-suffix">
                                    分
                                </span>

                            </div>

                            <c:if test="${not empty errors['duration']}">

                                <span
                                    class="request-field-error"
                                    id="duration-error"
                                    role="alert">
                                    <c:out value="${errors['duration']}"/>
                                </span>

                            </c:if>

                            <p class="request-field-help">
                                5分から1440分まで指定できます。
                            </p>

                        </div>

                    </div>

                </div>

            </section>

            <%-- =============================================
                 依頼先
                 ============================================= --%>

            <section class="request-form-card">

                <header class="request-form-card-header">

                    <span
                        class="request-form-card-icon"
                        aria-hidden="true">
                        3
                    </span>

                    <div>

                        <h2 class="request-form-card-title">
                            依頼先
                        </h2>

                        <p class="request-form-card-description">
                            依頼を送る家族を1人以上選択してください。
                        </p>

                    </div>

                </header>

                <div class="request-form-card-body">

                    <fieldset class="request-recipient-fieldset">

                        <legend class="request-visually-hidden">
                            依頼先の家族
                        </legend>

                        <div class="request-recipient-grid">

                            <c:forEach
                                var="m"
                                items="${others}">

                                <label class="request-recipient-option">

                                    <input
                                        class="request-recipient-checkbox"
                                        type="checkbox"
                                        name="recipients"
                                        value="${m.userId}"
                                        ${form.recipientIds.contains(m.userId) ? 'checked' : ''}>

                                    <span
                                        class="request-recipient-avatar"
                                        aria-hidden="true">
                                        人
                                    </span>

                                    <span class="request-recipient-content">

                                        <strong>
                                            <c:out value="${m.displayName}"/>
                                        </strong>

                                        <small>

                                            <c:out
                                                value="${m.roleLabel}"
                                                default="家族等"/>

                                            <c:if test="${not empty m.relationship}">
                                                ・
                                                <c:out value="${m.relationship}"/>
                                            </c:if>

                                        </small>

                                    </span>

                                    <span
                                        class="request-recipient-check"
                                        aria-hidden="true">
                                        ✓
                                    </span>

                                </label>

                            </c:forEach>

                        </div>

                        <c:if test="${not empty errors['recipients']}">

                            <span
                                class="request-field-error"
                                id="recipients-error"
                                role="alert">
                                <c:out value="${errors['recipients']}"/>
                            </span>

                        </c:if>

                    </fieldset>

                </div>

            </section>

            <div class="request-form-actions">

                <a
                    class="request-flow-button request-flow-button-secondary"
                    href="${pageContext.request.contextPath}/careshift/main/RequestList.action">
                    キャンセル
                </a>

                <button
                    class="request-flow-button request-flow-button-primary"
                    type="submit"
                    name="preview">

                    内容を確認する

                    <span aria-hidden="true">
                        &rsaquo;
                    </span>

                </button>

            </div>

        </form>

    </c:otherwise>

</c:choose>

<jsp:include page="/common/footer.jsp"/>