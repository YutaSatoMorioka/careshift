<%-- S05 家族グループ作成・管理 / 設計書: 画面設計書_家族グループ管理 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="家族グループ"/>
    <jsp:param name="pageClass" value="group-settings-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/group-settings.css?v=20261111-1">

<section class="group-page-header">

    <div class="group-page-heading">

        <p class="group-page-eyebrow">
            FAMILY GROUP
        </p>

        <h1 class="group-page-title">

            <c:choose>

                <c:when test="${empty member}">
                    家族グループを作成
                </c:when>

                <c:otherwise>
                    家族グループ設定
                </c:otherwise>

            </c:choose>

        </h1>

        <p class="group-page-description">

            <c:choose>

                <c:when test="${empty member}">
                    家族とタスクや依頼を共有するためのグループを作成します。
                </c:when>

                <c:otherwise>
                    グループ名、参加メンバー、家族への招待を管理します。
                </c:otherwise>

            </c:choose>

        </p>

    </div>

    <c:if test="${not empty member}">

        <a
            class="group-button group-button-secondary"
            href="${pageContext.request.contextPath}/careshift/main/MemberList.action">

            <span
                class="group-button-icon"
                aria-hidden="true">
                人
            </span>

            <span>
                メンバー管理
            </span>

        </a>

    </c:if>

</section>

<c:choose>

    <%-- =====================================================
         グループ未所属
         ===================================================== --%>

    <c:when test="${empty member}">

        <section class="group-welcome-card">

            <div class="group-welcome-visual">

                <span
                    class="group-welcome-icon"
                    aria-hidden="true">
                    G
                </span>

            </div>

            <div class="group-welcome-content">

                <p class="group-section-eyebrow">
                    CREATE GROUP
                </p>

                <h2 class="group-welcome-title">
                    最初の家族グループを作成しましょう
                </h2>

                <p class="group-welcome-description">
                    作成した人が最初の主介護者として登録されます。
                    作成後は招待リンクを発行し、家族を含めて最大6名まで参加できます。
                </p>

                <ul class="group-feature-list">

                    <li>
                        家族でタスクと担当状況を共有
                    </li>

                    <li>
                        依頼、回答、通知をグループ内で連携
                    </li>

                    <li>
                        カレンダーで期限と休息予定を確認
                    </li>

                </ul>

            </div>

        </section>

        <section
            class="group-form-card"
            aria-labelledby="group-create-title">

            <header class="group-form-card-header">

                <span
                    class="group-form-card-icon"
                    aria-hidden="true">
                    1
                </span>

                <div>

                    <h2
                        class="group-form-card-title"
                        id="group-create-title">
                        グループ名を入力
                    </h2>

                    <p class="group-form-card-description">
                        家族が識別しやすい名前を設定してください。
                    </p>

                </div>

            </header>

            <form
                class="group-name-form"
                method="post"
                action="${pageContext.request.contextPath}/careshift/main/GroupSave.action">

                <div class="group-form-field">

                    <div class="group-form-label-row">

                        <label for="groupName">
                            グループ名

                            <span class="group-required">
                                必須
                            </span>
                        </label>

                        <span class="group-character-limit">
                            1〜30文字
                        </span>

                    </div>

                    <input
                        class="group-form-control${not empty errors['groupName'] ? ' is-error' : ''}"
                        type="text"
                        id="groupName"
                        name="groupName"
                        maxlength="30"
                        value="<c:out value='${groupName}'/>"
                        aria-invalid="${not empty errors['groupName']}"
                        placeholder="例：佐藤家の介護グループ"
                        autocomplete="off">

                    <c:if test="${not empty errors['groupName']}">

                        <span
                            class="group-field-error"
                            id="groupName-error"
                            role="alert">
                            <c:out value="${errors['groupName']}"/>
                        </span>

                    </c:if>

                    <p class="group-field-help">
                        個人の病名や詳しい医療情報はグループ名に入力しないでください。
                    </p>

                </div>

                <div class="group-form-actions">

                    <a
                        class="group-button group-button-secondary"
                        href="${pageContext.request.contextPath}/careshift/main/Menu.action">
                        キャンセル
                    </a>

                    <button
                        class="group-button group-button-primary"
                        type="submit"
                        name="save">

                        <span
                            class="group-button-icon"
                            aria-hidden="true">
                            ＋
                        </span>

                        <span>
                            グループを作成
                        </span>

                    </button>

                </div>

            </form>

        </section>

        <section class="group-join-guide">

            <span
                class="group-join-guide-icon"
                aria-hidden="true">
                i
            </span>

            <div>

                <h2 class="group-join-guide-title">
                    招待リンクを受け取っている場合
                </h2>

                <p class="group-join-guide-description">
                    新しいグループを作成せず、家族から届いた招待リンクをそのまま開いて参加してください。
                    1人が複数の家族グループへ同時に所属することはできません。
                </p>

            </div>

        </section>

    </c:when>

    <%-- =====================================================
         グループ所属済み
         ===================================================== --%>

    <c:otherwise>

        <section
            class="group-summary-card"
            aria-labelledby="current-group-title">

            <div class="group-summary-main">

                <span
                    class="group-summary-icon"
                    aria-hidden="true">
                    G
                </span>

                <div class="group-summary-content">

                    <span class="group-summary-label">
                        現在の家族グループ
                    </span>

                    <h2
                        class="group-summary-name"
                        id="current-group-title">
                        <c:out
                            value="${group.name}"
                            default="家族グループ"/>
                    </h2>

                    <div class="group-summary-badges">

                        <span class="group-role-badge">

                            <c:choose>
                                <c:when test="${member.primary}">
                                    主介護者
                                </c:when>

                                <c:otherwise>
                                    家族等
                                </c:otherwise>
                            </c:choose>

                        </span>

                    </div>

                </div>

            </div>

            <div class="group-member-summary">

                <div class="group-member-count">

                    <span>
                        メンバー
                    </span>

                    <strong>
                        <c:out
                            value="${memberCount}"
                            default="0"/>

                        <small>
                            /
                            <c:out
                                value="${maxMembers}"
                                default="6"/>
                            名
                        </small>

                    </strong>

                </div>

                <div
                    class="group-member-progress"
                    aria-label="グループの参加人数">

                    <span
                        class="group-member-progress-fill"
                        style="width: ${(memberCount * 100) / maxMembers}%;">
                    </span>

                </div>

                <a
                    class="group-member-link"
                    href="${pageContext.request.contextPath}/careshift/main/MemberList.action">
                    メンバーを確認
                    <span aria-hidden="true">
                        &rsaquo;
                    </span>
                </a>

            </div>

        </section>

        <%-- =================================================
             グループ名変更
             ================================================= --%>

        <c:choose>

            <c:when test="${member.primary}">

                <section
                    class="group-form-card"
                    aria-labelledby="group-rename-title">

                    <header class="group-form-card-header">

                        <span
                            class="group-form-card-icon"
                            aria-hidden="true">
                            編
                        </span>

                        <div>

                            <h2
                                class="group-form-card-title"
                                id="group-rename-title">
                                グループ名の変更
                            </h2>

                            <p class="group-form-card-description">
                                グループ名は、主介護者だけが変更できます。
                            </p>

                        </div>

                    </header>

                    <form
                        class="group-name-form"
                        method="post"
                        action="${pageContext.request.contextPath}/careshift/main/GroupSave.action">

                        <div class="group-form-field">

                            <div class="group-form-label-row">

                                <label for="groupName">
                                    グループ名

                                    <span class="group-required">
                                        必須
                                    </span>
                                </label>

                                <span class="group-character-limit">
                                    1〜30文字
                                </span>

                            </div>

                            <input
                                class="group-form-control${not empty errors['groupName'] ? ' is-error' : ''}"
                                type="text"
                                id="groupName"
                                name="groupName"
                                maxlength="30"
                                value="<c:out value='${groupName}'/>"
                                aria-invalid="${not empty errors['groupName']}"
                                autocomplete="off">

                            <c:if test="${not empty errors['groupName']}">

                                <span
                                    class="group-field-error"
                                    id="groupName-error"
                                    role="alert">
                                    <c:out value="${errors['groupName']}"/>
                                </span>

                            </c:if>

                        </div>

                        <div class="group-form-actions">

                            <button
                                class="group-button group-button-primary"
                                type="submit"
                                name="save">

                                <span
                                    class="group-button-icon"
                                    aria-hidden="true">
                                    ✓
                                </span>

                                <span>
                                    グループ名を保存
                                </span>

                            </button>

                        </div>

                    </form>

                </section>

            </c:when>

            <c:otherwise>

                <section class="group-readonly-card">

                    <span
                        class="group-readonly-icon"
                        aria-hidden="true">
                        鍵
                    </span>

                    <div>

                        <h2 class="group-readonly-title">
                            グループ名は主介護者が管理します
                        </h2>

                        <p class="group-readonly-description">
                            グループ名の変更が必要な場合は、現在の主介護者へ依頼してください。
                        </p>

                    </div>

                </section>

            </c:otherwise>

        </c:choose>

        <%-- =================================================
             招待リンク
             ================================================= --%>

        <section
            class="group-invite-card"
            aria-labelledby="group-invite-title">

            <header class="group-invite-header">

                <span
                    class="group-invite-header-icon"
                    aria-hidden="true">
                    招
                </span>

                <div>

                    <h2
                        class="group-invite-title"
                        id="group-invite-title">
                        家族を招待する
                    </h2>

                    <p class="group-invite-description">
                        続柄を選択し、1名分の招待リンクを発行します。
                    </p>

                </div>

            </header>

            <div class="group-invite-rules">

                <div class="group-invite-rule">

                    <span
                        class="group-invite-rule-icon"
                        aria-hidden="true">
                        1
                    </span>

                    <div>
                        <strong>
                            1名分
                        </strong>

                        <small>
                            1回だけ使用できます
                        </small>
                    </div>

                </div>

                <div class="group-invite-rule">

                    <span
                        class="group-invite-rule-icon"
                        aria-hidden="true">
                        7
                    </span>

                    <div>
                        <strong>
                            7日間有効
                        </strong>

                        <small>
                            期限後は再発行が必要です
                        </small>
                    </div>

                </div>

                <div class="group-invite-rule">

                    <span
                        class="group-invite-rule-icon"
                        aria-hidden="true">
                        家
                    </span>

                    <div>
                        <strong>
                            家族等として参加
                        </strong>

                        <small>
                            主介護者は参加後に変更できます
                        </small>
                    </div>

                </div>

            </div>

            <c:choose>

                <c:when test="${memberCount >= maxMembers}">

                    <div
                        class="group-limit-warning"
                        role="alert">

                        <span
                            class="group-limit-warning-icon"
                            aria-hidden="true">
                            !
                        </span>

                        <div>

                            <strong>
                                メンバー数が上限に達しています
                            </strong>

                            <p>
                                このグループには最大
                                <c:out value="${maxMembers}"/>
                                名まで参加できます。新しい招待リンクは発行できません。
                            </p>

                        </div>

                    </div>

                </c:when>

                <c:otherwise>

                    <form
                        class="group-invite-form"
                        method="post"
                        action="${pageContext.request.contextPath}/careshift/main/InviteIssue.action">

                        <div class="group-form-field">

                            <label for="relationship">
                                招待する家族の続柄

                                <span class="group-optional">
                                    任意
                                </span>
                            </label>

                            <select
                                class="group-form-control"
                                id="relationship"
                                name="relationship">

                                <option value="">
                                    指定しない
                                </option>

                                <c:forEach
                                    var="r"
                                    items="${relationships}">

                                    <option value="<c:out value='${r}'/>">
                                        <c:out value="${r}"/>
                                    </option>

                                </c:forEach>

                            </select>

                            <p class="group-field-help">
                                指定した続柄は、招待を受けた人が参加したときに初期設定されます。
                            </p>

                        </div>

                        <div class="group-invite-role">

                            <span>
                                参加時の役割
                            </span>

                            <strong>
                                家族等
                            </strong>

                        </div>

                        <button
                            class="group-button group-button-primary"
                            type="submit"
                            name="invite">

                            <span
                                class="group-button-icon"
                                aria-hidden="true">
                                ＋
                            </span>

                            <span>
                                招待リンクを発行
                            </span>

                        </button>

                    </form>

                </c:otherwise>

            </c:choose>

            <c:if test="${not empty inviteLink}">

                <section
                    class="group-invite-result"
                    aria-labelledby="invite-result-title">

                    <div class="group-invite-result-header">

                        <span
                            class="group-invite-result-icon"
                            aria-hidden="true">
                            ✓
                        </span>

                        <div>

                            <h3
                                class="group-invite-result-title"
                                id="invite-result-title">
                                招待リンクを発行しました
                            </h3>

                            <p class="group-invite-result-description">
                                この画面を閉じると再表示できません。リンクをコピーして家族へ送ってください。
                            </p>

                        </div>

                    </div>

                    <div class="group-invite-link-row">

                        <input
                            class="group-invite-link-input"
                            type="text"
                            id="inviteLink"
                            readonly
                            value="<c:out value='${inviteLink}'/>"
                            aria-label="発行された招待リンク"
                            onclick="this.select()">

                        <button
                            class="group-copy-button"
                            type="button"
                            id="copyInviteLink"
                            onclick="copyGroupInviteLink()">
                            コピー
                        </button>

                    </div>

                    <p
                        class="group-copy-result"
                        id="copyResult"
                        aria-live="polite">
                    </p>

                    <div class="group-invite-security-note">

                        <span
                            class="group-invite-security-icon"
                            aria-hidden="true">
                            !
                        </span>

                        <p>
                            招待リンクを知っている人は参加手続きを開始できます。
                            招待する家族本人にだけ送ってください。
                        </p>

                    </div>

                </section>

            </c:if>

        </section>

        <%-- =================================================
             メンバー管理導線
             ================================================= --%>

        <section class="group-member-management">

            <div class="group-member-management-content">

                <span
                    class="group-member-management-icon"
                    aria-hidden="true">
                    人
                </span>

                <div>

                    <h2 class="group-member-management-title">
                        メンバー情報を管理
                    </h2>

                    <p class="group-member-management-description">
                        続柄、連絡先、役割、主介護者の設定を確認・変更できます。
                    </p>

                </div>

            </div>

            <a
                class="group-button group-button-secondary"
                href="${pageContext.request.contextPath}/careshift/main/MemberList.action">
                メンバー管理を開く
                <span aria-hidden="true">
                    &rsaquo;
                </span>
            </a>

        </section>

    </c:otherwise>

</c:choose>

<nav
    class="group-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="group-back-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            ホームへ戻る
        </span>

    </a>

</nav>

<script>
function copyGroupInviteLink() {
    var input = document.getElementById("inviteLink");
    var result = document.getElementById("copyResult");

    if (!input) {
        return;
    }

    input.focus();
    input.select();
    input.setSelectionRange(0, input.value.length);

    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(input.value)
            .then(function () {
                if (result) {
                    result.textContent = "招待リンクをコピーしました。";
                }
            })
            .catch(function () {
                copyGroupInviteLinkFallback(input, result);
            });

        return;
    }

    copyGroupInviteLinkFallback(input, result);
}

function copyGroupInviteLinkFallback(input, result) {
    try {
        var copied = document.execCommand("copy");

        if (result) {
            result.textContent = copied
                ? "招待リンクをコピーしました。"
                : "リンクを選択しました。手動でコピーしてください。";
        }
    } catch (error) {
        if (result) {
            result.textContent =
                "リンクを選択しました。手動でコピーしてください。";
        }
    }
}
</script>

<jsp:include page="/common/footer.jsp"/>