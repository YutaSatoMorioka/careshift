<%-- S14 休息リクエスト / 主介護者専用 --%>
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
        value="休息リクエスト"/>
    <jsp:param
        name="pageClass"
        value="rest-request-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/rest-request.css?v=20261117-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="rest-page-header">

    <div class="rest-page-heading">

        <p class="rest-page-eyebrow">
            REST SUPPORT
        </p>

        <h1 class="rest-page-title">
            休息リクエスト
        </h1>

        <p class="rest-page-description">
            休みたい日時を登録し、家族へ協力を依頼できます。
            家族からの回答状況や確定した予定も、この画面で確認できます。
        </p>

    </div>

    <div class="rest-page-header-actions">

        <a
            class="rest-button rest-button-secondary"
            href="${pageContext.request.contextPath}/careshift/main/RestReport.action">

            <span
                class="rest-button-icon"
                aria-hidden="true">
                集
            </span>

            <span>休息レポート</span>
        </a>

        <a
            class="rest-header-back-link"
            href="${pageContext.request.contextPath}/careshift/main/Menu.action">

            <span aria-hidden="true">
                &lsaquo;
            </span>

            <span>ホームへ戻る</span>
        </a>

    </div>

</section>

<%-- =======================================================
     機能説明
     ======================================================= --%>

<section
    class="rest-information-card"
    aria-labelledby="rest-information-title">

    <span
        class="rest-information-icon"
        aria-hidden="true">
        休
    </span>

    <div class="rest-information-content">

        <h2
            class="rest-information-title"
            id="rest-information-title">
            家族との調整を始めるための予定です
        </h2>

        <p>
            この機能は、休息そのものを保証するものではありません。
            希望日時を家族へ共有し、協力できる人を確認するために利用します。
        </p>

        <ul class="rest-information-list">
            <li>
                家族の誰かが引き受けると、休息予定が
                <strong>「確定」</strong>
                になります。
            </li>
            <li>
                全員が対応できない場合は
                <strong>「調整中」</strong>
                のまま、依頼先を選び直して再依頼できます。
            </li>
            <li>
                休息開始前であれば、予定を取り消せます。
            </li>
        </ul>

    </div>

</section>

<%-- =======================================================
     送信エラー
     ======================================================= --%>

<c:if test="${not empty errors['send']}">

    <div
        class="rest-alert rest-alert-error"
        role="alert">

        <span
            class="rest-alert-icon"
            aria-hidden="true">
            !
        </span>

        <div>
            <strong>休息リクエストを送信できませんでした</strong>

            <p>
                <c:out value="${errors['send']}"/>
            </p>
        </div>

    </div>

</c:if>

<%-- =======================================================
     登録フォーム
     ======================================================= --%>

<c:choose>

    <%-- 協力を依頼できる家族がいない場合 --%>
    <c:when test="${empty others}">

        <section
            class="rest-empty-member-card"
            aria-labelledby="rest-empty-member-title">

            <span
                class="rest-empty-member-icon"
                aria-hidden="true">
                人
            </span>

            <h2
                class="rest-empty-member-title"
                id="rest-empty-member-title">
                協力を依頼できる家族がいません
            </h2>

            <p class="rest-empty-member-description">
                休息リクエストの宛先には、自分以外の家族グループメンバーを指定します。
                家族を招待してから、もう一度この画面を開いてください。
            </p>

            <div class="rest-empty-member-actions">

                <a
                    class="rest-button rest-button-primary"
                    href="${pageContext.request.contextPath}/careshift/main/Group.action">
                    家族を招待する
                </a>

                <a
                    class="rest-button rest-button-secondary"
                    href="${pageContext.request.contextPath}/careshift/main/Menu.action">
                    ホームへ戻る
                </a>

            </div>

        </section>

    </c:when>

    <%-- 協力を依頼できる家族がいる場合 --%>
    <c:otherwise>

        <section
            class="rest-create-section"
            aria-labelledby="rest-create-title">

            <header class="rest-section-header">

                <div>

                    <p class="rest-section-eyebrow">
                        NEW REST REQUEST
                    </p>

                    <h2
                        class="rest-section-title"
                        id="rest-create-title">
                        新しい休息を依頼する
                    </h2>

                    <p class="rest-section-description">
                        希望日時、家族へ伝える内容、協力をお願いする相手を入力してください。
                    </p>

                </div>

                <span class="rest-section-step">
                    入力
                </span>

            </header>

            <form
                class="rest-create-form"
                method="post"
                action="${pageContext.request.contextPath}/careshift/main/RestRequestSend.action">

                <%-- 希望日時 --%>
                <section class="rest-form-card">

                    <header class="rest-form-card-header">

                        <span
                            class="rest-form-card-number"
                            aria-hidden="true">
                            1
                        </span>

                        <div>

                            <h3 class="rest-form-card-title">
                                希望日時
                            </h3>

                            <p class="rest-form-card-description">
                                現在より後の日時を指定してください。
                            </p>

                        </div>

                    </header>

                    <div class="rest-form-card-body">

                        <div class="rest-date-time-grid">

                            <div class="rest-form-field">

                                <label for="restDate">
                                    希望日

                                    <span class="rest-required">
                                        必須
                                    </span>
                                </label>

                                <input
                                    class="rest-form-control${not empty errors['restDate'] ? ' is-error' : ''}"
                                    type="date"
                                    id="restDate"
                                    name="restDate"
                                    value="<c:out value='${form.restDate}'/>"
                                    required
                                    aria-invalid="${not empty errors['restDate']}"
                                    <c:if test="${not empty errors['restDate']}">
                                        aria-describedby="restDate-error"
                                    </c:if>>

                                <c:if test="${not empty errors['restDate']}">

                                    <span
                                        class="rest-field-error"
                                        id="restDate-error"
                                        role="alert">

                                        <span aria-hidden="true">!</span>

                                        <c:out value="${errors['restDate']}"/>
                                    </span>

                                </c:if>

                                <p class="rest-field-help">
                                    今日の日時より後を指定してください。
                                </p>

                            </div>

                            <div class="rest-form-field">

                                <label for="startTime">
                                    開始時刻

                                    <span class="rest-required">
                                        必須
                                    </span>
                                </label>

                                <input
                                    class="rest-form-control${not empty errors['startTime'] ? ' is-error' : ''}"
                                    type="time"
                                    id="startTime"
                                    name="startTime"
                                    value="<c:out value='${form.startTime}'/>"
                                    required
                                    aria-invalid="${not empty errors['startTime']}"
                                    <c:if test="${not empty errors['startTime']}">
                                        aria-describedby="startTime-error"
                                    </c:if>>

                                <c:if test="${not empty errors['startTime']}">

                                    <span
                                        class="rest-field-error"
                                        id="startTime-error"
                                        role="alert">

                                        <span aria-hidden="true">!</span>

                                        <c:out value="${errors['startTime']}"/>
                                    </span>

                                </c:if>

                            </div>

                            <div class="rest-form-field">

                                <label for="endTime">
                                    終了時刻

                                    <span class="rest-required">
                                        必須
                                    </span>
                                </label>

                                <input
                                    class="rest-form-control${not empty errors['endTime'] ? ' is-error' : ''}"
                                    type="time"
                                    id="endTime"
                                    name="endTime"
                                    value="<c:out value='${form.endTime}'/>"
                                    required
                                    aria-invalid="${not empty errors['endTime']}"
                                    <c:if test="${not empty errors['endTime']}">
                                        aria-describedby="endTime-error"
                                    </c:if>>

                                <c:if test="${not empty errors['endTime']}">

                                    <span
                                        class="rest-field-error"
                                        id="endTime-error"
                                        role="alert">

                                        <span aria-hidden="true">!</span>

                                        <c:out value="${errors['endTime']}"/>
                                    </span>

                                </c:if>

                                <p class="rest-field-help">
                                    開始時刻より後を指定してください。
                                </p>

                            </div>

                        </div>

                    </div>

                </section>

                <%-- 希望内容 --%>
                <section class="rest-form-card">

                    <header class="rest-form-card-header">

                        <span
                            class="rest-form-card-number"
                            aria-hidden="true">
                            2
                        </span>

                        <div>

                            <h3 class="rest-form-card-title">
                                家族へ伝える内容
                            </h3>

                            <p class="rest-form-card-description">
                                休息中にお願いしたいことや、共有しておきたい情報を入力します。
                            </p>

                        </div>

                    </header>

                    <div class="rest-form-card-body">

                        <div class="rest-form-field">

                            <div class="rest-label-row">

                                <label for="restContent">
                                    希望内容

                                    <span class="rest-required">
                                        必須
                                    </span>
                                </label>

                                <span class="rest-character-limit">
                                    300文字以内
                                </span>

                            </div>

                            <textarea
                                class="rest-form-control rest-form-textarea${not empty errors['restContent'] ? ' is-error' : ''}"
                                id="restContent"
                                name="restContent"
                                rows="5"
                                maxlength="300"
                                required
                                aria-invalid="${not empty errors['restContent']}"
                                <c:if test="${not empty errors['restContent']}">
                                    aria-describedby="restContent-error"
                                </c:if>
                                placeholder="例：通院や買い物をお願いして、午後は自宅で休みたいです。"><c:out value="${form.restContent}"/></textarea>

                            <c:if test="${not empty errors['restContent']}">

                                <span
                                    class="rest-field-error"
                                    id="restContent-error"
                                    role="alert">

                                    <span aria-hidden="true">!</span>

                                    <c:out value="${errors['restContent']}"/>
                                </span>

                            </c:if>

                            <div class="rest-ai-help">

                                <span
                                    class="rest-ai-help-icon"
                                    aria-hidden="true">
                                    AI
                                </span>

                                <p>
                                    文章が思い浮かばない場合は、
                                    <a href="${pageContext.request.contextPath}/careshift/main/AiDraft.action">
                                        AIで下書きを作る
                                    </a>
                                    こともできます。
                                </p>

                            </div>

                        </div>

                    </div>

                </section>

                <%-- 依頼先 --%>
                <section class="rest-form-card">

                    <header class="rest-form-card-header">

                        <span
                            class="rest-form-card-number"
                            aria-hidden="true">
                            3
                        </span>

                        <div>

                            <h3 class="rest-form-card-title">
                                協力依頼先
                            </h3>

                            <p class="rest-form-card-description">
                                協力をお願いする家族を1人以上選択してください。
                            </p>

                        </div>

                    </header>

                    <div class="rest-form-card-body">

                        <fieldset
                            class="rest-recipient-fieldset"
                            <c:if test="${not empty errors['recipients']}">
                                aria-describedby="recipients-error"
                            </c:if>>

                            <legend class="rest-visually-hidden">
                                協力を依頼する家族
                            </legend>

                            <div class="rest-recipient-grid">

                                <c:forEach
                                    var="m"
                                    items="${others}">

                                    <label class="rest-recipient-option">

                                        <input
                                            class="rest-recipient-checkbox"
                                            type="checkbox"
                                            name="recipients"
                                            value="${m.userId}"
                                            ${form.recipientIds.contains(m.userId) ? 'checked' : ''}>

                                        <span
                                            class="rest-recipient-avatar"
                                            aria-hidden="true">
                                            人
                                        </span>

                                        <span class="rest-recipient-content">

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
                                            class="rest-recipient-selected"
                                            aria-hidden="true">
                                            ✓
                                        </span>

                                    </label>

                                </c:forEach>

                            </div>

                            <c:if test="${not empty errors['recipients']}">

                                <span
                                    class="rest-field-error rest-recipient-error"
                                    id="recipients-error"
                                    role="alert">

                                    <span aria-hidden="true">!</span>

                                    <c:out value="${errors['recipients']}"/>
                                </span>

                            </c:if>

                        </fieldset>

                    </div>

                </section>

                <div class="rest-form-actions">

                    <a
                        class="rest-button rest-button-secondary"
                        href="${pageContext.request.contextPath}/careshift/main/Menu.action">
                        キャンセル
                    </a>

                    <button
                        class="rest-button rest-button-primary"
                        type="submit"
                        name="send">

                        <span
                            class="rest-button-icon"
                            aria-hidden="true">
                            送
                        </span>

                        <span>休息リクエストを送信</span>
                    </button>

                </div>

            </form>

        </section>

    </c:otherwise>

</c:choose>

<%-- =======================================================
     休息予定一覧
     ======================================================= --%>

<section
    class="rest-list-section"
    aria-labelledby="rest-list-title">

    <header class="rest-section-header rest-list-header">

        <div>

            <p class="rest-section-eyebrow">
                REST SCHEDULE
            </p>

            <h2
                class="rest-section-title"
                id="rest-list-title">
                休息予定の一覧
            </h2>

            <p class="rest-section-description">
                最近登録した休息予定を最大30件表示します。
            </p>

        </div>

        <div class="rest-result-count">

            <strong>
                <c:choose>
                    <c:when test="${empty rows}">
                        0
                    </c:when>

                    <c:otherwise>
                        <c:out value="${rows.size()}"/>
                    </c:otherwise>
                </c:choose>
            </strong>

            <span>件</span>

        </div>

    </header>

    <c:choose>

        <%-- 空状態 --%>
        <c:when test="${empty rows}">

            <div
                class="rest-empty-state"
                id="empty">

                <span
                    class="rest-empty-state-icon"
                    aria-hidden="true">
                    休
                </span>

                <h3>
                    休息予定はまだありません
                </h3>

                <p>
                    上のフォームから休みたい日時を登録し、家族へ協力を依頼できます。
                </p>

            </div>

        </c:when>

        <%-- 一覧 --%>
        <c:otherwise>

            <div
                class="rest-plan-list"
                id="restList">

                <c:forEach
                    var="row"
                    items="${rows}">

                    <article class="rest-plan-card
                        ${row.plan.confirmed ? ' is-confirmed' : ''}
                        ${row.plan.cancelled ? ' is-cancelled' : ''}
                        ${row.plan.adjusting ? ' is-adjusting' : ''}">

                        <header class="rest-plan-card-header">

                            <div class="rest-plan-labels">

                                <span class="rest-plan-status
                                    ${row.plan.confirmed ? ' is-confirmed' : ''}
                                    ${row.plan.cancelled ? ' is-cancelled' : ''}
                                    ${row.plan.adjusting ? ' is-adjusting' : ''}">

                                    <c:out value="${row.plan.statusLabel}"/>
                                </span>

                                <c:if test="${row.awaiting}">
                                    <span class="rest-plan-response-status">
                                        回答待ち
                                    </span>
                                </c:if>

                            </div>

                            <div class="rest-plan-date">

                                <span
                                    class="rest-plan-date-icon"
                                    aria-hidden="true">
                                    日
                                </span>

                                <time>
                                    <c:out value="${row.plan.dateLabel}"/>
                                </time>

                            </div>

                        </header>

                        <div class="rest-plan-card-body">

                            <div class="rest-plan-main">

                                <div class="rest-plan-time">

                                    <strong>
                                        <c:out value="${row.plan.timeRange}"/>
                                    </strong>

                                    <span>
                                        <c:out value="${row.plan.durationLabel}"/>
                                    </span>

                                </div>

                                <p class="rest-plan-content">
                                    <c:out value="${row.plan.content}"/>
                                </p>

                                <dl class="rest-plan-detail">

                                    <div>

                                        <dt>登録者</dt>

                                        <dd>
                                            <c:out
                                                value="${row.plan.creatorName}"
                                                default="主介護者"/>
                                        </dd>

                                    </div>

                                    <div>

                                        <dt>現在の状態</dt>

                                        <dd>
                                            <c:out value="${row.plan.statusLabel}"/>
                                        </dd>

                                    </div>

                                    <c:if test="${row.plan.confirmed}">

                                        <div>

                                            <dt>協力担当</dt>

                                            <dd class="rest-covered-person">

                                                <span
                                                    class="rest-covered-avatar"
                                                    aria-hidden="true">
                                                    人
                                                </span>

                                                <c:out
                                                    value="${row.plan.coveredName}"
                                                    default="担当者"/>
                                            </dd>

                                        </div>

                                    </c:if>

                                </dl>

                            </div>

                            <aside class="rest-plan-response-area">

                                <h3 class="rest-plan-response-title">
                                    家族の回答状況
                                </h3>

                                <c:choose>

                                    <c:when test="${empty row.latest}">

                                        <p class="rest-no-request">
                                            依頼情報がありません。
                                        </p>

                                    </c:when>

                                    <c:otherwise>

                                        <div class="rest-response-list">

                                            <c:forEach
                                                var="p"
                                                items="${row.latest.recipients}">

                                                <div class="rest-response-person">

                                                    <span
                                                        class="rest-response-avatar"
                                                        aria-hidden="true">
                                                        人
                                                    </span>

                                                    <span class="rest-response-person-info">

                                                        <strong>
                                                            <c:out value="${p.name}"/>
                                                        </strong>

                                                        <small>
                                                            <c:out
                                                                value="${p.responseLabel}"
                                                                default="未回答"/>
                                                        </small>

                                                    </span>

                                                </div>

                                            </c:forEach>

                                        </div>

                                        <p class="rest-request-status">

                                            依頼状態：

                                            <strong>
                                                <c:out value="${row.latest.statusLabel}"/>
                                            </strong>

                                        </p>

                                    </c:otherwise>

                                </c:choose>

                                <%-- 全員対応不可の場合 --%>
                                <c:if test="${row.showGuidance}">

                                    <div
                                        class="rest-guidance"
                                        id="guidance">

                                        <span
                                            class="rest-guidance-icon"
                                            aria-hidden="true">
                                            i
                                        </span>

                                        <div>

                                            <strong>
                                                家族全員が対応できない状況です
                                            </strong>

                                            <p>
                                                予定は「調整中」のままです。
                                                再依頼のほか、ケアマネジャー、地域包括支援センター、
                                                ショートステイなどへの相談も検討してください。
                                            </p>

                                        </div>

                                    </div>

                                </c:if>

                            </aside>

                        </div>

                        <c:if test="${row.canRerequest or row.canCancel}">

                            <footer class="rest-plan-card-actions">

                                <%-- 再依頼 --%>
                                <c:if test="${row.canRerequest}">

                                    <form
                                        class="rest-rerequest-form"
                                        method="post"
                                        action="${pageContext.request.contextPath}/careshift/main/RestReRequest.action">

                                        <input
                                            type="hidden"
                                            name="restId"
                                            value="${row.plan.id}">

                                        <fieldset class="rest-rerequest-fieldset">

                                            <legend>
                                                再依頼する家族を選択
                                            </legend>

                                            <div class="rest-rerequest-members">

                                                <c:forEach
                                                    var="m"
                                                    items="${others}">

                                                    <label class="rest-rerequest-member">

                                                        <input
                                                            type="checkbox"
                                                            name="recipients"
                                                            value="${m.userId}">

                                                        <span>
                                                            <c:out value="${m.displayName}"/>
                                                        </span>

                                                    </label>

                                                </c:forEach>

                                            </div>

                                        </fieldset>

                                        <button
                                            class="rest-button rest-button-secondary"
                                            type="submit"
                                            name="rerequest">
                                            選択した家族へ再依頼
                                        </button>

                                    </form>

                                </c:if>

                                <%-- 取消 --%>
                                <c:if test="${row.canCancel}">

                                    <form
                                        class="rest-cancel-form"
                                        method="post"
                                        action="${pageContext.request.contextPath}/careshift/main/RestCancel.action"
                                        onsubmit="return confirm('この休息予定を取り消します。担当の家族がいれば通知されます。よろしいですか？');">

                                        <input
                                            type="hidden"
                                            name="id"
                                            value="${row.plan.id}">

                                        <button
                                            class="rest-button rest-button-danger"
                                            type="submit"
                                            name="cancel">
                                            休息予定を取り消す
                                        </button>

                                    </form>

                                </c:if>

                            </footer>

                        </c:if>

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
    class="rest-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a href="${pageContext.request.contextPath}/careshift/main/RestReport.action">
        休息レポートを見る
        <span aria-hidden="true">&rsaquo;</span>
    </a>

    <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">
        <span aria-hidden="true">&lsaquo;</span>
        ホームへ戻る
    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>