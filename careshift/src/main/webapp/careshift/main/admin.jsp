<%-- S90 管理者 / 設計書: S90_管理者 --%>
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
        value="管理者"/>

    <jsp:param
        name="pageClass"
        value="admin-main"/>

</jsp:include>

<link
    rel="stylesheet"
    type="text/css"
    href="${pageContext.request.contextPath}/css/admin.css?v=20261124-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="admin-page-header">

    <div class="admin-page-heading">

        <p class="admin-page-eyebrow">
            SYSTEM ADMINISTRATION
        </p>

        <h1 class="admin-page-title">
            管理者
        </h1>

        <p class="admin-page-description">
            データベースのバックアップと復元、システムの操作ログを確認できます。
            復元は現在のデータを上書きするため、内容を確認してから実行してください。
        </p>

    </div>

    <div class="admin-page-actions">

        <span class="admin-access-badge">

            <span
                class="admin-access-badge-icon"
                aria-hidden="true">
                管
            </span>

            管理者専用
        </span>

        <a
            class="admin-header-link"
            href="${pageContext.request.contextPath}/careshift/main/Menu.action">

            <span aria-hidden="true">
                &lsaquo;
            </span>

            <span>
                ホームへ戻る
            </span>

        </a>

    </div>

</section>

<%-- =======================================================
     管理者向け注意
     ======================================================= --%>

<section
    class="admin-security-notice"
    aria-labelledby="admin-security-title">

    <span
        class="admin-security-icon"
        aria-hidden="true">
        鍵
    </span>

    <div>

        <h2 id="admin-security-title">
            管理操作について
        </h2>

        <p>
            バックアップファイルには、利用者情報やパスワードのハッシュなどが含まれます。
            ファイルをメールや共有ストレージへ無断で置かず、アクセスできる人を管理してください。
        </p>

    </div>

</section>

<%-- =======================================================
     バックアップ
     ======================================================= --%>

<section
    class="admin-section admin-backup-section"
    aria-labelledby="admin-backup-title">

    <header class="admin-section-header">

        <div class="admin-section-heading">

            <span
                class="admin-section-icon"
                aria-hidden="true">
                B
            </span>

            <div>

                <p class="admin-section-eyebrow">
                    BACKUP
                </p>

                <h2
                    class="admin-section-title"
                    id="admin-backup-title">
                    バックアップ
                </h2>

                <p class="admin-section-description">
                    データベースの内容を圧縮SQLファイルとして保存します。
                    自動・手動バックアップは直近7世代まで保持されます。
                </p>

            </div>

        </div>

        <c:choose>

            <c:when test="${supported}">

                <span class="admin-support-badge is-supported">
                    利用可能
                </span>

            </c:when>

            <c:otherwise>

                <span class="admin-support-badge is-unsupported">
                    利用対象外
                </span>

            </c:otherwise>

        </c:choose>

    </header>

    <c:if test="${!supported}">

        <div
            class="admin-alert is-warning"
            id="unsupported"
            role="alert">

            <span
                class="admin-alert-icon"
                aria-hidden="true">
                !
            </span>

            <div>

                <strong>
                    画面からバックアップ・復元を実行できません
                </strong>

                <p>
                    この機能はH2ローカルデータベース専用です。
                    PostgreSQLを使用している場合は、PostgreSQL公式の
                    pg_dumpとpg_restoreを使用してください。
                </p>

            </div>

        </div>

    </c:if>

    <div class="admin-backup-summary">

        <div class="admin-backup-explanation">

            <h3>
                手動バックアップ
            </h3>

            <p>
                重要な設定変更やデータ更新の前に、手動バックアップを作成してください。
                直近24時間以内に手動バックアップを作成した場合、次の日次自動バックアップは省略される場合があります。
            </p>

        </div>

        <form
            class="admin-backup-form"
            method="post"
            action="${pageContext.request.contextPath}/careshift/main/AdminBackup.action"
            onsubmit="return adminStartBackup(this);">

            <button
                class="admin-button admin-button-primary"
                id="backupButton"
                type="submit"
                name="backupNow"
                <c:if test="${!supported}">
                    disabled
                </c:if>>

                <span
                    class="admin-button-icon"
                    aria-hidden="true">
                    保
                </span>

                <span class="admin-button-label">
                    今すぐバックアップ
                </span>

            </button>

        </form>

    </div>

    <%-- =================================================
         バックアップ履歴
         ================================================= --%>

    <section
        class="admin-history"
        aria-labelledby="admin-history-title">

        <header class="admin-subsection-header">

            <div>

                <h3
                    class="admin-subsection-title"
                    id="admin-history-title">
                    バックアップ履歴
                </h3>

                <p>
                    自動・手動バックアップの直近7世代を表示します。
                </p>

            </div>

            <span class="admin-result-count">

                <strong>
                    <c:choose>

                        <c:when test="${empty history}">
                            0
                        </c:when>

                        <c:otherwise>
                            <c:out value="${history.size()}"/>
                        </c:otherwise>

                    </c:choose>
                </strong>

                件
            </span>

        </header>

        <c:choose>

            <c:when test="${empty history}">

                <div class="admin-empty-state">

                    <span
                        class="admin-empty-icon"
                        aria-hidden="true">
                        B
                    </span>

                    <h4>
                        バックアップ履歴はまだありません
                    </h4>

                    <p>
                        「今すぐバックアップ」を押すと、最初の手動バックアップを作成できます。
                    </p>

                </div>

            </c:when>

            <c:otherwise>

                <div class="admin-table-wrapper">

                    <table
                        class="admin-table"
                        id="backupList">

                        <thead>

                            <tr>

                                <th scope="col">
                                    日時
                                </th>

                                <th scope="col">
                                    種類
                                </th>

                                <th scope="col">
                                    結果
                                </th>

                                <th scope="col">
                                    サイズ
                                </th>

                            </tr>

                        </thead>

                        <tbody>

                            <c:forEach
                                var="h"
                                items="${history}">

                                <tr>

                                    <td>

                                        <time class="admin-date-time">
                                            <c:out value="${h.atLabel}"/>
                                        </time>

                                    </td>

                                    <td>

                                        <span class="admin-backup-kind
                                            ${h.kind == 'AUTO' ? ' is-auto' : ''}
                                            ${h.kind == 'MANUAL' ? ' is-manual' : ''}">

                                            <c:out value="${h.kindLabel}"/>
                                        </span>

                                    </td>

                                    <td>

                                        <c:choose>

                                            <c:when test="${h.ok}">

                                                <span class="admin-result-badge is-success">

                                                    <span aria-hidden="true">
                                                        ✓
                                                    </span>

                                                    成功
                                                </span>

                                            </c:when>

                                            <c:otherwise>

                                                <span class="admin-result-badge is-failure">

                                                    <span aria-hidden="true">
                                                        !
                                                    </span>

                                                    失敗
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <td>
                                        <c:out value="${h.sizeLabel}"/>
                                    </td>

                                </tr>

                            </c:forEach>

                        </tbody>

                    </table>

                </div>

            </c:otherwise>

        </c:choose>

    </section>

</section>

<%-- =======================================================
     復元
     ======================================================= --%>

<section
    class="admin-section admin-restore-section"
    aria-labelledby="admin-restore-title">

    <header class="admin-section-header is-danger">

        <div class="admin-section-heading">

            <span
                class="admin-section-icon is-danger"
                aria-hidden="true">
                復
            </span>

            <div>

                <p class="admin-section-eyebrow is-danger">
                    DATABASE RESTORE
                </p>

                <h2
                    class="admin-section-title"
                    id="admin-restore-title">
                    データベースの復元
                </h2>

                <p class="admin-section-description">
                    選択したバックアップの内容で現在のデータベースを置き換えます。
                </p>

            </div>

        </div>

        <span class="admin-danger-badge">
            危険な操作
        </span>

    </header>

    <div
        class="admin-alert is-danger"
        role="alert">

        <span
            class="admin-alert-icon"
            aria-hidden="true">
            !
        </span>

        <div>

            <strong>
                復元すると現在のデータが上書きされます
            </strong>

            <p>
                復元前に現在の状態が自動退避されます。
                復元に失敗した場合は退避したデータへの巻き戻しを試みます。
                復元成功後はセッションが無効になり、再ログインが必要です。
            </p>

        </div>

    </div>

    <ol class="admin-restore-steps">

        <li>

            <span>1</span>

            <div>

                <strong>
                    復元する世代を選択
                </strong>

                <small>
                    成功した自動または手動バックアップから選択します。
                </small>

            </div>

        </li>

        <li>

            <span>2</span>

            <div>

                <strong>
                    ログインパスワードで再認証
                </strong>

                <small>
                    現在ログイン中のCare Shiftアカウントのパスワードを入力します。
                </small>

            </div>

        </li>

        <li>

            <span>3</span>

            <div>

                <strong>
                    上書き確認後に復元
                </strong>

                <small>
                    復元中はブラウザーを閉じたり、Tomcatを停止したりしないでください。
                </small>

            </div>

        </li>

    </ol>

    <c:choose>

        <c:when test="${empty restorable}">

            <div
                class="admin-empty-state compact"
                id="noRestorable">

                <span
                    class="admin-empty-icon"
                    aria-hidden="true">
                    復
                </span>

                <h3>
                    復元できるバックアップがありません
                </h3>

                <p>
                    正常に完了した自動または手動バックアップを作成してください。
                </p>

            </div>

        </c:when>

        <c:otherwise>

            <form
                class="admin-restore-form"
                id="restoreForm"
                method="post"
                action="${pageContext.request.contextPath}/careshift/main/AdminRestore.action"
                onsubmit="return adminConfirmRestore(this);">

                <div class="admin-form-grid">

                    <div class="admin-form-field">

                        <label for="restoreGeneration">
                            復元する世代

                            <span class="admin-required">
                                必須
                            </span>
                        </label>

                        <select
                            class="admin-form-control"
                            id="restoreGeneration"
                            name="restoreGeneration"
                            required>

                            <option value="">
                                選択してください
                            </option>

                            <c:forEach
                                var="r"
                                items="${restorable}">

                                <option value="<c:out value='${r.fileName}'/>">

                                    <c:out value="${r.atLabel}"/>
                                    （
                                    <c:out value="${r.kindLabel}"/>
                                    ・
                                    <c:out value="${r.sizeLabel}"/>
                                    ）

                                </option>

                            </c:forEach>

                        </select>

                    </div>

                    <div class="admin-form-field">

                        <label for="password">
                            管理者のパスワード

                            <span class="admin-required">
                                必須
                            </span>
                        </label>

                        <input
                            class="admin-form-control"
                            type="password"
                            id="password"
                            name="password"
                            maxlength="128"
                            autocomplete="current-password"
                            required
                            placeholder="現在のログインパスワード">

                        <p class="admin-field-help">
                            VM引数ではなく、現在ログインしているCare Shiftアカウントのパスワードです。
                        </p>

                    </div>

                </div>

                <label class="admin-confirm-option">

                    <input
                        class="admin-confirm-checkbox"
                        type="checkbox"
                        id="restoreConfirm"
                        name="confirm"
                        value="1"
                        required>

                    <span
                        class="admin-confirm-check"
                        aria-hidden="true">
                        ✓
                    </span>

                    <span class="admin-confirm-content">

                        <strong>
                            現在のデータが上書きされることを確認しました
                        </strong>

                        <small>
                            選択したバックアップ時点より後に登録・変更されたデータは失われる可能性があります。
                        </small>

                    </span>

                </label>

                <div class="admin-restore-actions">

                    <button
                        class="admin-button admin-button-danger"
                        id="restoreButton"
                        type="submit"
                        name="restore"
                        <c:if test="${!supported}">
                            disabled
                        </c:if>>

                        <span
                            class="admin-button-icon"
                            aria-hidden="true">
                            復
                        </span>

                        <span class="admin-button-label">
                            選択したバックアップから復元
                        </span>

                    </button>

                </div>

            </form>

        </c:otherwise>

    </c:choose>

</section>

<%-- =======================================================
     障害ログ
     ======================================================= --%>

<section
    class="admin-section admin-log-section"
    aria-labelledby="admin-log-title">

    <header class="admin-section-header">

        <div class="admin-section-heading">

            <span
                class="admin-section-icon"
                aria-hidden="true">
                L
            </span>

            <div>

                <p class="admin-section-eyebrow">
                    OPERATION LOG
                </p>

                <h2
                    class="admin-section-title"
                    id="admin-log-title">
                    障害・操作ログ
                </h2>

                <p class="admin-section-description">
                    直近90日分のログから、期間と種類を指定して最大200件表示します。
                    パスワード、APIキー、相談文の本文は記録しません。
                </p>

            </div>

        </div>

        <span class="admin-result-count">

            <strong>
                <c:choose>

                    <c:when test="${empty logs}">
                        0
                    </c:when>

                    <c:otherwise>
                        <c:out value="${logs.size()}"/>
                    </c:otherwise>

                </c:choose>
            </strong>

            件
        </span>

    </header>

    <%-- =================================================
         ログ検索
         ================================================= --%>

    <form
        class="admin-log-filter"
        method="get"
        action="${pageContext.request.contextPath}/careshift/main/Admin.action">

        <div class="admin-log-filter-grid">

            <div class="admin-form-field">

                <label for="logFrom">
                    開始日
                </label>

                <input
                    class="admin-form-control${not empty errors['logFrom'] ? ' is-error' : ''}"
                    type="date"
                    id="logFrom"
                    name="logFrom"
                    value="<c:out value='${logFrom}'/>"
                    aria-invalid="${not empty errors['logFrom']}">

            </div>

            <div class="admin-form-field">

                <label for="logTo">
                    終了日
                </label>

                <input
                    class="admin-form-control${not empty errors['logTo'] ? ' is-error' : ''}"
                    type="date"
                    id="logTo"
                    name="logTo"
                    value="<c:out value='${logTo}'/>"
                    aria-invalid="${not empty errors['logTo']}">

            </div>

            <div class="admin-form-field">

                <label for="logType">
                    種類
                </label>

                <select
                    class="admin-form-control"
                    id="logType"
                    name="logType">

                    <option value="">
                        すべて
                    </option>

                    <c:forEach
                        var="t"
                        items="${logTypes}">

                        <option
                            value="<c:out value='${t}'/>"
                            ${t == logType ? 'selected' : ''}>

                            <c:out value="${t}"/>

                        </option>

                    </c:forEach>

                </select>

            </div>

            <div class="admin-log-filter-actions">

                <button
                    class="admin-button admin-button-primary"
                    type="submit"
                    name="filter">
                    絞り込む
                </button>

                <a
                    class="admin-button admin-button-secondary"
                    href="${pageContext.request.contextPath}/careshift/main/Admin.action">
                    条件をクリア
                </a>

            </div>

        </div>

        <c:if test="${not empty errors}">

            <div
                class="admin-filter-errors"
                role="alert">

                <c:forEach
                    var="entry"
                    items="${errors}">

                    <p>
                        <span aria-hidden="true">
                            !
                        </span>

                        <c:out value="${entry.value}"/>
                    </p>

                </c:forEach>

            </div>

        </c:if>

    </form>

    <%-- =================================================
         ログ一覧
         ================================================= --%>

    <c:choose>

        <c:when test="${empty logs}">

            <div class="admin-empty-state">

                <span
                    class="admin-empty-icon"
                    aria-hidden="true">
                    L
                </span>

                <h3>
                    該当するログはありません
                </h3>

                <p>
                    日付や種類の条件を変更して、もう一度検索してください。
                </p>

            </div>

        </c:when>

        <c:otherwise>

            <div class="admin-table-wrapper admin-log-table-wrapper">

                <table
                    class="admin-table admin-log-table"
                    id="logList">

                    <thead>

                        <tr>

                            <th scope="col">
                                日時
                            </th>

                            <th scope="col">
                                種類
                            </th>

                            <th scope="col">
                                内容
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        <c:forEach
                            var="l"
                            items="${logs}">

                            <tr>

                                <td>

                                    <time class="admin-date-time">
                                        <c:out value="${l.atLabel}"/>
                                    </time>

                                </td>

                                <td>

                                    <span class="admin-log-type
                                        ${l.type == 'ERROR' ? ' is-error' : ''}
                                        ${l.type == 'AUTH' ? ' is-auth' : ''}
                                        ${l.type == 'BACKUP' ? ' is-backup' : ''}
                                        ${l.type == 'RESTORE' ? ' is-restore' : ''}
                                        ${l.type == 'MAINTENANCE' ? ' is-maintenance' : ''}">

                                        <c:out value="${l.type}"/>
                                    </span>

                                </td>

                                <td class="admin-log-message">
                                    <c:out value="${l.message}"/>
                                </td>

                            </tr>

                        </c:forEach>

                    </tbody>

                </table>

            </div>

        </c:otherwise>

    </c:choose>

</section>

<%-- =======================================================
     下部ナビゲーション
     ======================================================= --%>

<nav
    class="admin-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        ホームへ戻る
    </a>

</nav>

<script>
(function () {
    "use strict";

    window.adminStartBackup = function (form) {
        var button =
            form.querySelector(
                "#backupButton"
            );

        if (!button) {
            return true;
        }

        button.disabled = true;

        var label =
            button.querySelector(
                ".admin-button-label"
            );

        if (label) {
            label.textContent =
                "バックアップ中...";
        }

        return true;
    };

    window.adminConfirmRestore = function (form) {
        var generation =
            form.querySelector(
                "#restoreGeneration"
            );

        var confirmed =
            form.querySelector(
                "#restoreConfirm"
            );

        if (!generation || generation.value === "") {
            window.alert(
                "復元するバックアップを選択してください。"
            );

            if (generation) {
                generation.focus();
            }

            return false;
        }

        if (!confirmed || !confirmed.checked) {
            window.alert(
                "現在のデータが上書きされることを確認し、チェックを入れてください。"
            );

            if (confirmed) {
                confirmed.focus();
            }

            return false;
        }

        var firstConfirm =
            window.confirm(
                "現在のデータを、選択したバックアップの内容で上書きします。よろしいですか？"
            );

        if (!firstConfirm) {
            return false;
        }

        var secondConfirm =
            window.confirm(
                "この操作はデータベース全体に影響します。本当に復元を開始しますか？"
            );

        if (!secondConfirm) {
            return false;
        }

        var button =
            form.querySelector(
                "#restoreButton"
            );

        if (button) {
            button.disabled = true;

            var label =
                button.querySelector(
                    ".admin-button-label"
                );

            if (label) {
                label.textContent =
                    "復元しています...";
            }
        }

        return true;
    };
})();
</script>

<jsp:include page="/common/footer.jsp"/>