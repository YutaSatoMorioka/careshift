<%-- S20 設定 / 設計書: S20_設定 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="設定"/>
    <jsp:param name="pageClass" value="settings-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/settings.css?v=20261117-2">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="settings-page-header">

    <div class="settings-page-heading">

        <p class="settings-page-eyebrow">
            SETTINGS
        </p>

        <h1 class="settings-page-title">
            設定
        </h1>

        <p class="settings-page-description">
            アカウント、家族グループ、通知、データ出力、利用プランを確認・管理します。
        </p>

    </div>

    <a
        class="settings-header-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            ホームへ戻る
        </span>

    </a>

</section>

<%-- =======================================================
     現在の設定概要
     ======================================================= --%>

<section
    class="settings-summary-card"
    aria-labelledby="settings-summary-title">

    <div class="settings-summary-group">

        <span
            class="settings-summary-icon"
            aria-hidden="true">
            G
        </span>

        <div>

            <span class="settings-summary-label">
                家族グループ
            </span>

            <h2
                class="settings-summary-name"
                id="settings-summary-title">

                <c:out
                    value="${group.name}"
                    default="家族グループ"/>

            </h2>

            <span class="settings-role-badge">

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

    <div class="settings-summary-plan">

        <span class="settings-summary-plan-label">
            現在のプラン
        </span>

        <strong>

            <c:out
                value="${plan.planLabel}"
                default="無料"/>

        </strong>

        <a
            href="${pageContext.request.contextPath}/careshift/main/Plan.action">

            プランを確認

            <span aria-hidden="true">
                &rsaquo;
            </span>

        </a>

    </div>

</section>

<%-- =======================================================
     アカウント情報
     ======================================================= --%>

<section
    class="settings-card"
    aria-labelledby="settings-account-title">

    <header class="settings-card-header">

        <span
            class="settings-card-icon"
            aria-hidden="true">
            A
        </span>

        <div>

            <h2
                class="settings-card-title"
                id="settings-account-title">
                アカウント
            </h2>

            <p class="settings-card-description">
                現在ログインしているアカウントの情報を確認します。
            </p>

        </div>

    </header>

    <div class="settings-card-body">

        <dl class="settings-account-information">

            <div class="settings-account-row">

                <dt>
                    表示名
                </dt>

                <dd>
                    <c:out
                        value="${sessionScope.user.displayName}"
                        default="ユーザー"/>
                </dd>

            </div>

            <div class="settings-account-row">

                <dt>
                    メールアドレス
                </dt>

                <dd>
                    <c:out
                        value="${sessionScope.user.email}"
                        default="未設定"/>
                </dd>

            </div>

            <div class="settings-account-row">

                <dt>
                    メール確認
                </dt>

                <dd>

                    <c:choose>

                        <c:when test="${sessionScope.user.emailVerified}">

                            <span class="settings-account-status is-verified">
                                確認済み
                            </span>

                        </c:when>

                        <c:otherwise>

                            <span class="settings-account-status">
                                未確認
                            </span>

                        </c:otherwise>

                    </c:choose>

                </dd>

            </div>

        </dl>

        <div class="settings-account-note">

            <span
                class="settings-account-note-icon"
                aria-hidden="true">
                i
            </span>

            <p>
                現在の実装には、表示名やメールアドレスを変更する機能はありません。
                パスワードを変更したい場合は、再設定メールを請求してください。
            </p>

        </div>

        <div class="settings-card-link-row">

            <a
                href="${pageContext.request.contextPath}/password_reset.jsp">

                パスワード再設定メールを請求

                <span aria-hidden="true">
                    &rsaquo;
                </span>

            </a>

        </div>

    </div>

</section>

<%-- =======================================================
     グループ・通知の保存フォーム
     ======================================================= --%>

<form
    class="settings-save-form"
    method="post"
    action="${pageContext.request.contextPath}/careshift/main/SettingsSave.action">

    <%-- ===================================================
         グループ設定
         =================================================== --%>

    <section
        class="settings-card"
        aria-labelledby="settings-group-title">

        <header class="settings-card-header">

            <span
                class="settings-card-icon"
                aria-hidden="true">
                G
            </span>

            <div>

                <h2
                    class="settings-card-title"
                    id="settings-group-title">
                    家族グループ
                </h2>

                <p class="settings-card-description">

                    <c:choose>

                        <c:when test="${member.primary}">
                            家族グループの表示名を変更できます。
                        </c:when>

                        <c:otherwise>
                            現在所属している家族グループを確認できます。
                        </c:otherwise>

                    </c:choose>

                </p>

            </div>

        </header>

        <div class="settings-card-body">

            <c:choose>

                <c:when test="${member.primary}">

                    <div class="settings-form-field">

                        <div class="settings-label-row">

                            <label for="groupName">

                                グループ名

                                <span class="settings-required">
                                    必須
                                </span>

                            </label>

                            <span class="settings-character-limit">
                                1〜30文字
                            </span>

                        </div>

                        <input
                            class="settings-form-control${not empty errors['groupName'] ? ' is-error' : ''}"
                            type="text"
                            id="groupName"
                            name="groupName"
                            maxlength="30"
                            value="<c:out value='${groupName}'/>"
                            aria-invalid="${not empty errors['groupName']}"
                            autocomplete="off">

                        <c:if test="${not empty errors['groupName']}">

                            <span
                                class="settings-field-error"
                                id="groupName-error"
                                role="alert">
                                <c:out value="${errors['groupName']}"/>
                            </span>

                        </c:if>

                        <p class="settings-field-help">
                            変更すると、すべてのメンバーの画面に新しいグループ名が表示されます。
                        </p>

                    </div>

                </c:when>

                <c:otherwise>

                    <div class="settings-readonly-group">

                        <span
                            class="settings-readonly-group-icon"
                            aria-hidden="true">
                            鍵
                        </span>

                        <div>

                            <span class="settings-readonly-label">
                                現在のグループ名
                            </span>

                            <strong>
                                <c:out
                                    value="${group.name}"
                                    default="家族グループ"/>
                            </strong>

                            <p>
                                グループ名を変更できるのは主介護者のみです。
                            </p>

                        </div>

                    </div>

                </c:otherwise>

            </c:choose>

            <div class="settings-card-link-row">

                <a
                    href="${pageContext.request.contextPath}/careshift/main/Group.action">

                    グループ設定を開く

                    <span aria-hidden="true">
                        &rsaquo;
                    </span>

                </a>

                <a
                    href="${pageContext.request.contextPath}/careshift/main/MemberList.action">

                    メンバー管理を開く

                    <span aria-hidden="true">
                        &rsaquo;
                    </span>

                </a>

            </div>

        </div>

    </section>

    <%-- ===================================================
         通知設定
         =================================================== --%>

    <section
        class="settings-card"
        aria-labelledby="settings-notification-title">

        <header class="settings-card-header">

            <span
                class="settings-card-icon"
                aria-hidden="true">
                N
            </span>

            <div>

                <h2
                    class="settings-card-title"
                    id="settings-notification-title">
                    通知
                </h2>

                <p class="settings-card-description">
                    依頼、回答、タスク期限などの通知方法を設定します。
                </p>

            </div>

        </header>

        <div class="settings-card-body">

            <div class="settings-option-list">

                <%-- アプリ内通知は常にオン --%>

                <div class="settings-option is-locked">

                    <div class="settings-option-main">

                        <span
                            class="settings-option-icon"
                            aria-hidden="true">
                            A
                        </span>

                        <div class="settings-option-content">

                            <label for="appNotice">
                                アプリ内通知
                            </label>

                            <p>
                                家族からの依頼、回答、タスクなどをアプリ内の通知一覧へ表示します。
                            </p>

                        </div>

                    </div>

                    <div class="settings-toggle-wrapper">

                        <input
                            class="settings-toggle-input"
                            type="checkbox"
                            id="appNotice"
                            name="appNotice"
                            checked
                            disabled>

                        <span
                            class="settings-toggle is-on is-disabled"
                            aria-hidden="true">
                            <span></span>
                        </span>

                        <strong class="settings-toggle-label">
                            常にオン
                        </strong>

                    </div>

                </div>

                <%-- メール通知 --%>

                <label
                    class="settings-option settings-option-clickable"
                    for="emailNotice">

                    <span class="settings-option-main">

                        <span
                            class="settings-option-icon"
                            aria-hidden="true">
                            M
                        </span>

                        <span class="settings-option-content">

                            <strong>
                                メール通知
                            </strong>

                            <span>
                                アプリ内通知と同じ内容を、登録メールアドレスにも通知します。
                            </span>

                        </span>

                    </span>

                    <span class="settings-toggle-wrapper">

                        <input
                            class="settings-toggle-input"
                            type="checkbox"
                            id="emailNotice"
                            name="emailNotice"
                            ${emailNotice ? 'checked' : ''}>

                        <span
                            class="settings-toggle"
                            aria-hidden="true">
                            <span></span>
                        </span>

                    </span>

                </label>

                <%-- 期限リマインド --%>

                <label
                    class="settings-option settings-option-clickable"
                    for="reminder">

                    <span class="settings-option-main">

                        <span
                            class="settings-option-icon"
                            aria-hidden="true">
                            期
                        </span>

                        <span class="settings-option-content">

                            <strong>
                                期限リマインド
                            </strong>

                            <span>
                                自分が担当するタスクの期限が近づいたときに通知を作成します。
                            </span>

                        </span>

                    </span>

                    <span class="settings-toggle-wrapper">

                        <input
                            class="settings-toggle-input"
                            type="checkbox"
                            id="reminder"
                            name="reminder"
                            ${reminder ? 'checked' : ''}>

                        <span
                            class="settings-toggle"
                            aria-hidden="true">
                            <span></span>
                        </span>

                    </span>

                </label>

            </div>

            <div class="settings-notification-guide">

                <span
                    class="settings-notification-guide-icon"
                    aria-hidden="true">
                    i
                </span>

                <div>

                    <strong>
                        現在のメール通知は開発用です
                    </strong>
                </div>

            </div>

        </div>

    </section>

    <div class="settings-save-actions">

        <a
            class="settings-button settings-button-secondary"
            href="${pageContext.request.contextPath}/careshift/main/Menu.action">
            キャンセル
        </a>

        <button
            class="settings-button settings-button-primary"
            type="submit"
            name="save">

            <span
                class="settings-button-icon"
                aria-hidden="true">
                ✓
            </span>

            <span>
                設定を保存
            </span>

        </button>

    </div>

</form>

<%-- =======================================================
     データ出力
     ======================================================= --%>

<c:if test="${member.primary}">

    <section
        class="settings-card settings-export-card"
        aria-labelledby="settings-export-title">

        <header class="settings-card-header">

            <span
                class="settings-card-icon"
                aria-hidden="true">
                CSV
            </span>

            <div>

                <h2
                    class="settings-card-title"
                    id="settings-export-title">
                    データの出力
                </h2>

                <p class="settings-card-description">
                    家族グループとタスクの情報をCSVファイルで保存します。
                </p>

            </div>

        </header>

        <div class="settings-card-body">

            <div class="settings-export-content">

                <div class="settings-export-information">

                    <h3>
                        CSVデータ
                    </h3>

                    <p>
                        家族とタスクの情報を、表計算ソフトで確認できる形式で出力します。
                    </p>

                    <ul>

                        <li>
                            出力できるのは主介護者のみです。
                        </li>

                        <li>
                            家族の表示名、続柄、連絡先などが含まれる場合があります。
                        </li>

                        <li>
                            公共の端末や共有パソコンには保存しないでください。
                        </li>

                    </ul>

                </div>

                <form
                    class="settings-export-form"
                    method="post"
                    action="${pageContext.request.contextPath}/careshift/main/ExportCsv.action">

                    <button
                        class="settings-button settings-button-secondary"
                        type="submit"
                        name="exportCsv">

                        <span
                            class="settings-button-icon"
                            aria-hidden="true">
                            ↓
                        </span>

                        <span>
                            データを出力（CSV）
                        </span>

                    </button>

                </form>

            </div>

            <div class="settings-security-warning">

                <span
                    class="settings-security-warning-icon"
                    aria-hidden="true">
                    !
                </span>

                <p>
                    出力したファイルには個人情報が含まれる可能性があります。
                    アクセス制限のある場所に保存し、共有先を十分に確認してください。
                </p>

            </div>

        </div>

    </section>

</c:if>

<%-- =======================================================
     プラン
     ======================================================= --%>

<section
    class="settings-card settings-plan-card"
    aria-labelledby="settings-plan-title">

    <header class="settings-card-header">

        <span
            class="settings-card-icon"
            aria-hidden="true">
            P
        </span>

        <div>

            <h2
                class="settings-card-title"
                id="settings-plan-title">
                利用プラン
            </h2>

            <p class="settings-card-description">
                家族グループに適用されているプランを確認します。
            </p>

        </div>

    </header>

    <div class="settings-card-body">

        <div class="settings-plan-content">

            <div class="settings-plan-current">

                <span class="settings-plan-label">
                    現在のプラン
                </span>

                <strong class="settings-plan-name">

                    <c:out
                        value="${plan.planLabel}"
                        default="無料"/>

                    プラン

                </strong>

                <c:if test="${plan.paid}">

                    <c:choose>

                        <c:when test="${plan.cancelRequested}">

                            <span class="settings-plan-status is-cancelled">
                                無料プランへの変更予約済み
                            </span>

                            <p class="settings-plan-date">

                                有料機能の利用期限：

                                <strong>
                                    <c:out value="${plan.nextDate}"/>
                                </strong>

                            </p>

                        </c:when>

                        <c:otherwise>

                            <span class="settings-plan-status">
                                有料機能を利用できます
                            </span>

                            <p class="settings-plan-date">

                                次回更新日：

                                <strong>
                                    <c:out value="${plan.nextDate}"/>
                                </strong>

                            </p>

                        </c:otherwise>

                    </c:choose>

                </c:if>

                <c:if test="${not member.primary}">

                    <p class="settings-plan-permission">
                        プランを変更できるのは主介護者のみです。
                    </p>

                </c:if>

            </div>

            <a
                class="settings-button settings-button-primary"
                id="planLink"
                href="${pageContext.request.contextPath}/careshift/main/Plan.action">

                プランの確認・変更

                <span aria-hidden="true">
                    &rsaquo;
                </span>

            </a>

        </div>

    </div>

</section>

<%-- =======================================================
     ログアウト
     ======================================================= --%>

<section
    class="settings-logout-card"
    aria-labelledby="settings-logout-title">

    <div class="settings-logout-content">

        <span
            class="settings-logout-icon"
            aria-hidden="true">
            出
        </span>

        <div>

            <h2
                class="settings-logout-title"
                id="settings-logout-title">
                ログアウト
            </h2>

            <p class="settings-logout-description">
                この端末での利用を終了し、ログイン画面へ戻ります。
            </p>

        </div>

    </div>

    <form
        class="settings-logout-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/Logout.action"
        onsubmit="return confirm('ログアウトします。よろしいですか？');">

        <button
            class="settings-logout-button"
            type="submit"
            name="logout">
            ログアウト
        </button>

    </form>

</section>

<nav
    class="settings-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="settings-back-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            ホームへ戻る
        </span>

    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>