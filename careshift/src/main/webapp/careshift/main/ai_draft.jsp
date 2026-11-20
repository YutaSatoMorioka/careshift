<%-- AI001 AI依頼下書き / 設計書: 画面設計書_AI依頼下書き --%>
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
        value="AI依頼下書き"/>
    <jsp:param
        name="pageClass"
        value="ai-draft-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/ai-draft.css?v=20261117-2">

<section class="ai-draft-page-header">

    <div class="ai-draft-page-heading">

        <p class="ai-draft-page-eyebrow">
            AI REQUEST DRAFT
        </p>

        <h1 class="ai-draft-page-title">
            AI依頼下書き
        </h1>

        <p class="ai-draft-page-description">
            相談したい内容を入力すると、家族へ送る依頼文の下書きを作成します。
            作成された文章はそのまま送信されません。内容を確認・修正してから利用できます。
        </p>

    </div>

    <a
        class="ai-draft-header-link"
        href="${pageContext.request.contextPath}/careshift/main/RequestList.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>依頼一覧へ戻る</span>
    </a>

</section>

<section
    class="ai-draft-notice-card"
    aria-labelledby="ai-draft-notice-title">

    <span
        class="ai-draft-notice-icon"
        aria-hidden="true">
        AI
    </span>

    <div class="ai-draft-notice-content">

        <h2
            class="ai-draft-notice-title"
            id="ai-draft-notice-title">
            入力前に確認してください
        </h2>

        <p>
            AIは依頼文の<strong>下書きのみ</strong>を作ります。
            医療的な助言や、家族の誰が担当するかの決定は行いません。
        </p>

        <ul class="ai-draft-notice-list">

            <li>
                病名、薬名、診療内容などの
                <strong>医療情報は入力しないでください。</strong>
            </li>

            <li>
                実名、住所、電話番号など、
                <strong>個人を特定できる情報は入力しないでください。</strong>
            </li>

            <li>
                入力した相談内容は外部のAIサービス
                <strong>Google Gemini</strong>
                へ送信され、このアプリには保存されません。
            </li>

            <li>
                無料利用枠では、送信した内容がサービス改善に利用される場合があります。
            </li>

        </ul>

    </div>

</section>

<section
    class="ai-draft-usage-card"
    aria-labelledby="ai-draft-usage-title">

    <div class="ai-draft-usage-heading">

        <span
            class="ai-draft-usage-icon"
            aria-hidden="true">
            月
        </span>

        <div>

            <span class="ai-draft-usage-label">
                今月の利用状況
            </span>

            <h2
                class="ai-draft-usage-title"
                id="ai-draft-usage-title">

                <c:out value="${paid ? '有料プラン' : '無料プラン'}"/>
            </h2>

        </div>

    </div>

    <div class="ai-draft-usage-value">

        <strong>
            <c:out value="${aiUsed}"/>
        </strong>

        <span>
            /
            <c:out value="${aiLimit}"/>
            回
        </span>

    </div>

    <div class="ai-draft-usage-progress">

        <div
            class="ai-draft-usage-progress-bar"
            style="width:${aiLimit > 0 ? (aiUsed * 100 / aiLimit) : 0}%;">
        </div>

    </div>

    <p class="ai-draft-usage-remaining">

        残り

        <strong>
            <c:out value="${aiRemaining}"/>
        </strong>

        回利用できます。

        <c:if test="${!paid}">

            <a href="${pageContext.request.contextPath}/careshift/main/Plan.action">
                プランを確認
            </a>

        </c:if>

    </p>

</section>

<section
    class="ai-draft-input-section"
    aria-labelledby="ai-draft-input-title">

    <header class="ai-draft-section-header">

        <div>

            <p class="ai-draft-section-eyebrow">
                CONSULTATION
            </p>

            <h2
                class="ai-draft-section-title"
                id="ai-draft-input-title">
                相談内容を入力
            </h2>

            <p class="ai-draft-section-description">
                家族へ何をお願いしたいか、希望する日時や所要時間を、
                個人情報を含めずに入力してください。
            </p>

        </div>

        <span class="ai-draft-section-label">
            1000文字以内
        </span>

    </header>

    <form
        class="ai-draft-generate-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/main/AiDraftGenerate.action">

        <div class="ai-draft-form-field">

            <div class="ai-draft-label-row">

                <label for="prompt">
                    相談内容

                    <span class="ai-draft-required">
                        必須
                    </span>
                </label>

                <span class="ai-draft-character-limit">
                    最大1000文字
                </span>

            </div>

            <textarea
                class="ai-draft-form-control ai-draft-textarea${not empty errors['prompt'] ? ' is-error' : ''}"
                id="prompt"
                name="prompt"
                rows="7"
                maxlength="1000"
                required
                aria-invalid="${not empty errors['prompt']}"
                aria-describedby="${not empty errors['prompt'] ? 'promptError' : 'promptHelp'}"
                placeholder="例：来週の平日に2時間ほど休みたいので、その間の買い物と見守りを家族にお願いしたいです。"><c:out value="${prompt}"/></textarea>

            <c:if test="${not empty errors['prompt']}">

                <span
                    class="ai-draft-field-error"
                    id="promptError"
                    role="alert">

                    <span aria-hidden="true">!</span>

                    <c:out value="${errors['prompt']}"/>
                </span>

            </c:if>

            <div
                class="ai-draft-input-example"
                id="promptHelp">

                <span
                    class="ai-draft-input-example-icon"
                    aria-hidden="true">
                    例
                </span>

                <div>

                    <strong>入力のポイント</strong>

                    <p>
                        「いつ」「どのくらい」「何をお願いしたいか」を入力すると、
                        内容が伝わりやすい下書きになります。
                    </p>

                </div>

            </div>

        </div>

        <div class="ai-draft-generate-actions">

            <a
                class="ai-draft-button ai-draft-button-secondary"
                href="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">
                AIを使わず手入力する
            </a>

            <button
                class="ai-draft-button ai-draft-button-primary"
                type="submit"
                name="generate"
                <c:if test="${limitReached}">
                    disabled
                </c:if>>

                <span
                    class="ai-draft-button-icon"
                    aria-hidden="true">
                    AI
                </span>

                <span>下書きを作る</span>
            </button>

        </div>

    </form>

</section>

<%-- AI生成失敗 --%>
<c:if test="${unavailable}">

    <section
        class="ai-draft-result-message is-error"
        id="unavailable"
        role="alert">

        <span
            class="ai-draft-result-message-icon"
            aria-hidden="true">
            !
        </span>

        <div class="ai-draft-result-message-content">

            <h2>
                下書きを作成できませんでした
            </h2>

            <p>
                AIサービスの設定や通信状態などにより、
                下書きを作成できませんでした。
                今回の利用回数は消費していません。
            </p>

            <c:if test="${not empty unavailableReason}">

                <p class="ai-draft-error-detail">
                    <strong>原因：</strong>
                    <c:out value="${unavailableReason}"/>
                </p>

            </c:if>

            <form
                method="post"
                action="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">

                <input
                    type="hidden"
                    name="content"
                    value="<c:out value='${prompt.length() > 500 ? prompt.substring(0, 500) : prompt}'/>">

                <button
                    class="ai-draft-button ai-draft-button-primary"
                    type="submit"
                    name="manual">
                    入力した内容を使って依頼を作る
                </button>

            </form>

        </div>

    </section>

</c:if>

<%-- AI利用上限 --%>
<c:if test="${limitReached}">

    <section
        class="ai-draft-result-message is-warning"
        id="limitReached"
        role="alert">

        <span
            class="ai-draft-result-message-icon"
            aria-hidden="true">
            上
        </span>

        <div class="ai-draft-result-message-content">

            <h2>
                今月の利用上限に達しました
            </h2>

            <p>
                AIを使わず、入力した相談内容をもとに依頼を作成できます。
            </p>

            <div class="ai-draft-limit-actions">

                <form
                    method="post"
                    action="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">

                    <input
                        type="hidden"
                        name="content"
                        value="<c:out value='${prompt.length() > 500 ? prompt.substring(0, 500) : prompt}'/>">

                    <button
                        class="ai-draft-button ai-draft-button-primary"
                        type="submit"
                        name="manual">
                        手入力で依頼を作る
                    </button>

                </form>

                <c:if test="${!paid}">

                    <a
                        class="ai-draft-button ai-draft-button-secondary"
                        href="${pageContext.request.contextPath}/careshift/main/Plan.action">
                        プランを確認する
                    </a>

                </c:if>

            </div>

        </div>

    </section>

</c:if>

<%-- 作成または保存された下書き --%>
<c:if test="${hasDraft}">

    <section
        class="ai-draft-output-section"
        aria-labelledby="ai-draft-output-title">

        <header class="ai-draft-section-header">

            <div>

                <p class="ai-draft-section-eyebrow">
                    REQUEST DRAFT
                </p>

                <h2
                    class="ai-draft-section-title"
                    id="ai-draft-output-title">
                    依頼の下書き
                </h2>

                <p class="ai-draft-section-description">

                    <c:choose>

                        <c:when test="${generated}">
                            AIが作成した内容です。必ず確認し、必要に応じて修正してください。
                        </c:when>

                        <c:otherwise>
                            保存されている下書きです。修正して保存するか、依頼作成へ進めます。
                        </c:otherwise>

                    </c:choose>

                </p>

            </div>

            <c:choose>

                <c:when test="${generated}">

                    <span class="ai-draft-status-badge is-generated">
                        AI作成
                    </span>

                </c:when>

                <c:otherwise>

                    <span class="ai-draft-status-badge is-saved">
                        保存済み
                    </span>

                </c:otherwise>

            </c:choose>

        </header>

        <div class="ai-draft-review-notice">

            <span
                class="ai-draft-review-notice-icon"
                aria-hidden="true">
                ✓
            </span>

            <p>
                送信前に、日時、依頼内容、所要時間が正しいか確認してください。
                この画面ではまだ家族へ送信されません。
            </p>

        </div>

        <form
            class="ai-draft-save-form"
            method="post"
            action="${pageContext.request.contextPath}/careshift/main/AiDraftSave.action">

            <section class="ai-draft-form-card">

                <header class="ai-draft-form-card-header">

                    <span
                        class="ai-draft-form-card-number"
                        aria-hidden="true">
                        1
                    </span>

                    <div>

                        <h3>
                            依頼内容
                        </h3>

                        <p>
                            家族に伝わりやすい文章になっているか確認してください。
                        </p>

                    </div>

                </header>

                <div class="ai-draft-form-card-body">

                    <div class="ai-draft-form-field">

                        <div class="ai-draft-label-row">

                            <label for="content">
                                依頼内容

                                <span class="ai-draft-required">
                                    必須
                                </span>
                            </label>

                            <span class="ai-draft-character-limit">
                                最大500文字
                            </span>

                        </div>

                        <textarea
                            class="ai-draft-form-control ai-draft-textarea${not empty errors['content'] ? ' is-error' : ''}"
                            id="content"
                            name="content"
                            rows="7"
                            maxlength="500"
                            required
                            aria-invalid="${not empty errors['content']}"
                            aria-describedby="${not empty errors['content'] ? 'content-error' : ''}"><c:out value="${draftContent}"/></textarea>

                        <c:if test="${not empty errors['content']}">

                            <span
                                class="ai-draft-field-error"
                                id="content-error"
                                role="alert">

                                <span aria-hidden="true">!</span>

                                <c:out value="${errors['content']}"/>
                            </span>

                        </c:if>

                    </div>

                </div>

            </section>

            <section class="ai-draft-form-card">

                <header class="ai-draft-form-card-header">

                    <span
                        class="ai-draft-form-card-number"
                        aria-hidden="true">
                        2
                    </span>

                    <div>

                        <h3>
                            日時と所要時間
                        </h3>

                        <p>
                            未定の場合は空欄のまま保存できます。
                        </p>

                    </div>

                </header>

                <div class="ai-draft-form-card-body">

                    <div class="ai-draft-details-grid">

                        <div class="ai-draft-form-field">

                            <label for="requestedAt">
                                希望日時

                                <span class="ai-draft-optional">
                                    任意
                                </span>
                            </label>

                            <input
                                class="ai-draft-form-control${not empty errors['requestedAt'] ? ' is-error' : ''}"
                                type="datetime-local"
                                id="requestedAt"
                                name="requestedAt"
                                value="<c:out value='${draftRequestedAt}'/>"
                                aria-invalid="${not empty errors['requestedAt']}"
                                aria-describedby="${not empty errors['requestedAt'] ? 'requestedAt-error' : ''}">

                            <c:if test="${not empty errors['requestedAt']}">

                                <span
                                    class="ai-draft-field-error"
                                    id="requestedAt-error"
                                    role="alert">

                                    <span aria-hidden="true">!</span>

                                    <c:out value="${errors['requestedAt']}"/>
                                </span>

                            </c:if>

                            <p class="ai-draft-field-help">
                                日時が決まっていない場合は空欄にできます。
                            </p>

                        </div>

                        <div class="ai-draft-form-field">

                            <label for="duration">
                                所要時間

                                <span class="ai-draft-optional">
                                    任意
                                </span>
                            </label>

                            <div class="ai-draft-number-wrapper">

                                <input
                                    class="ai-draft-form-control${not empty errors['duration'] ? ' is-error' : ''}"
                                    type="number"
                                    id="duration"
                                    name="duration"
                                    min="5"
                                    max="1440"
                                    step="5"
                                    value="<c:out value='${draftDuration}'/>"
                                    aria-invalid="${not empty errors['duration']}"
                                    aria-describedby="${not empty errors['duration'] ? 'duration-error' : ''}"
                                    placeholder="60">

                                <span class="ai-draft-number-suffix">
                                    分
                                </span>

                            </div>

                            <c:if test="${not empty errors['duration']}">

                                <span
                                    class="ai-draft-field-error"
                                    id="duration-error"
                                    role="alert">

                                    <span aria-hidden="true">!</span>

                                    <c:out value="${errors['duration']}"/>
                                </span>

                            </c:if>

                            <p class="ai-draft-field-help">
                                5分から1440分まで指定できます。
                            </p>

                        </div>

                    </div>

                </div>

            </section>

            <div class="ai-draft-save-actions">

                <button
                    class="ai-draft-button ai-draft-button-secondary"
                    type="submit"
                    name="saveDraft">
                    修正して保存
                </button>

                <button
                    class="ai-draft-button ai-draft-button-primary"
                    type="submit"
                    name="createRequest"
                    formaction="${pageContext.request.contextPath}/careshift/main/RequestCreate.action">

                    <span
                        class="ai-draft-button-icon"
                        aria-hidden="true">
                        送
                    </span>

                    <span>依頼作成へ進む</span>
                </button>

            </div>

        </form>

    </section>

</c:if>

<nav
    class="ai-draft-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a href="${pageContext.request.contextPath}/careshift/main/RequestList.action">
        <span aria-hidden="true">&lsaquo;</span>
        依頼一覧へ
    </a>

    <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">
        ホームへ戻る
    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>