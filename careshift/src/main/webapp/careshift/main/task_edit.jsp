<%-- S08-2 タスク編集 / 設計書: 画面設計書_タスク編集 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="タスク編集"/>
    <jsp:param name="pageClass" value="task-form-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/task-form.css?v=20261111-2">

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/task-detail.css?v=20261111-2">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="task-form-page-header">

    <div class="task-form-page-heading">

        <p class="task-form-page-eyebrow">
            EDIT TASK
        </p>

        <h1 class="task-form-page-title">
            タスク編集
        </h1>

        <p class="task-form-page-description">
            タスク内容、期限、所要時間、担当者、進捗状況を編集します。
        </p>

    </div>

    <a
        class="task-form-header-link"
        href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${task.id}">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            タスク詳細へ戻る
        </span>

    </a>

</section>

<%-- =======================================================
     編集ステップ
     ======================================================= --%>

<nav
    class="task-form-stepper"
    aria-label="タスク編集の進行状況">

    <div class="task-form-step is-current">

        <span class="task-form-step-number">
            1
        </span>

        <span class="task-form-step-content">

            <strong>
                編集
            </strong>

            <small>
                タスク情報を変更
            </small>

        </span>

    </div>

    <span
        class="task-form-step-line"
        aria-hidden="true">
    </span>

    <div class="task-form-step">

        <span class="task-form-step-number">
            2
        </span>

        <span class="task-form-step-content">

            <strong>
                保存
            </strong>

            <small>
                変更を反映
            </small>

        </span>

    </div>

</nav>

<%-- =======================================================
     保存エラー
     ======================================================= --%>

<c:if test="${not empty errors['save']}">

    <div
        class="task-form-error-notice"
        role="alert">

        <span
            class="task-form-error-icon"
            aria-hidden="true">
            !
        </span>

        <div>

            <strong>
                タスクを保存できませんでした
            </strong>

            <p>
                <c:out value="${errors['save']}"/>
            </p>

        </div>

    </div>

</c:if>

<%-- =======================================================
     保存前の確認事項
     ======================================================= --%>

<c:if test="${not empty warnings}">

    <section
        class="task-form-warning"
        id="warnings"
        aria-labelledby="task-edit-warning-title">

        <span
            class="task-form-warning-icon"
            aria-hidden="true">
            !
        </span>

        <div class="task-form-warning-content">

            <h2
                class="task-form-warning-title"
                id="task-edit-warning-title">
                保存前にご確認ください
            </h2>

            <ul class="task-form-warning-list">

                <c:forEach
                    var="w"
                    items="${warnings}">

                    <li>
                        <c:out value="${w}"/>
                    </li>

                </c:forEach>

            </ul>

            <p class="task-form-warning-help">
                入力内容に問題がなければ、画面下部の「確認して保存」を押してください。
            </p>

        </div>

    </section>

</c:if>

<%-- =======================================================
     編集フォーム
     ======================================================= --%>

<form
    class="task-create-form"
    method="post"
    action="${pageContext.request.contextPath}/careshift/main/TaskEditExecute.action">

    <input
        type="hidden"
        name="id"
        value="${task.id}">

    <%-- 
        画面を開いた時点の版番号。
        保存時に、ほかの家族が先に更新していないか確認する。
    --%>
    <input
        type="hidden"
        name="version"
        value="<c:out value='${form.version}'/>">

    <%-- 警告内容を確認して、再度保存した場合 --%>
    <c:if test="${not empty warnings}">

        <input
            type="hidden"
            name="confirm"
            value="1">

    </c:if>

    <%-- 共通入力欄へ編集画面であることを渡す --%>
    <c:set
        var="editMode"
        value="${true}"
        scope="request"/>

    <jsp:include page="/common/task_fields.jsp"/>

    <div class="task-form-actions">

        <a
            class="task-form-button task-form-button-secondary"
            href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${task.id}">
            キャンセル
        </a>

        <button
            class="task-form-button task-form-button-primary"
            type="submit"
            name="save">

            <span
                class="task-form-button-icon"
                aria-hidden="true">
                ✓
            </span>

            <span>

                <c:choose>

                    <c:when test="${not empty warnings}">
                        確認して保存
                    </c:when>

                    <c:otherwise>
                        変更を保存
                    </c:otherwise>

                </c:choose>

            </span>

        </button>

    </div>

</form>

<%-- =======================================================
     同時編集の案内
     ======================================================= --%>

<section class="task-form-guidance">

    <span
        class="task-form-guidance-icon"
        aria-hidden="true">
        i
    </span>

    <div>

        <h2 class="task-form-guidance-title">
            同時編集について
        </h2>

        <p class="task-form-guidance-description">
            ほかの家族が先にこのタスクを更新していた場合は、
            入力内容を上書きせず、最新の内容を読み直して表示します。
            最新の内容を確認してから、もう一度編集してください。
        </p>

    </div>

</section>

<%-- =======================================================
     削除
     ======================================================= --%>

<section class="task-edit-danger-zone">

    <div>

        <h2 class="task-edit-danger-title">
            タスクの削除
        </h2>

        <p class="task-edit-danger-description">
            削除したタスクは元に戻せません。
            担当者が設定されている場合は、削除されたことが担当者へ通知されます。
        </p>

    </div>

    <form
        method="post"
        action="${pageContext.request.contextPath}/careshift/main/TaskDelete.action"
        onsubmit="return confirm('このタスクを削除します。担当者にも通知されます。よろしいですか？');">

        <input
            type="hidden"
            name="id"
            value="${task.id}">

        <button
            class="task-edit-delete-button"
            type="submit"
            name="delete">
            このタスクを削除
        </button>

    </form>

</section>

<%-- =======================================================
     下部ナビゲーション
     ======================================================= --%>

<nav
    class="task-form-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="task-form-back-link"
        href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${task.id}">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            タスク詳細へ戻る
        </span>

    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>