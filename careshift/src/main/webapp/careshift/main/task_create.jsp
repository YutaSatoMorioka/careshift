<%-- S08 タスク登録 / 設計書: 画面設計書_タスク登録 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="タスク登録"/>
    <jsp:param name="pageClass" value="task-form-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/task-form.css?v=20261111-1">

<section class="task-form-page-header">

    <div class="task-form-page-heading">

        <p class="task-form-page-eyebrow">
            NEW TASK
        </p>

        <h1 class="task-form-page-title">
            タスク登録
        </h1>

        <p class="task-form-page-description">
            家族で分担する介護タスクの内容、期限、担当者を登録します。
        </p>

    </div>

    <a
        class="task-form-header-link"
        href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            タスク一覧へ戻る
        </span>

    </a>

</section>

<nav
    class="task-form-stepper"
    aria-label="タスク登録の進行状況">

    <div class="task-form-step is-current">

        <span class="task-form-step-number">
            1
        </span>

        <span class="task-form-step-content">

            <strong>
                入力
            </strong>

            <small>
                タスク情報を入力
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
                登録
            </strong>

            <small>
                タスクを保存
            </small>

        </span>

    </div>

</nav>

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

<c:if test="${not empty warnings}">

    <section
        class="task-form-warning"
        id="warnings"
        aria-labelledby="task-warning-title">

        <span
            class="task-form-warning-icon"
            aria-hidden="true">
            !
        </span>

        <div class="task-form-warning-content">

            <h2
                class="task-form-warning-title"
                id="task-warning-title">
                登録前にご確認ください
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
                入力内容に問題がなければ、画面下部の「確認して登録」を押してください。
            </p>

        </div>

    </section>

</c:if>

<form
    class="task-create-form"
    method="post"
    action="${pageContext.request.contextPath}/careshift/main/TaskCreateExecute.action">

    <c:if test="${not empty warnings}">
        <input
            type="hidden"
            name="confirm"
            value="1">
    </c:if>

    <c:set
        var="editMode"
        value="${false}"
        scope="request"/>

    <jsp:include page="/common/task_fields.jsp"/>

    <div class="task-form-actions">

        <a
            class="task-form-button task-form-button-secondary"
            href="${pageContext.request.contextPath}/careshift/main/TaskList.action">
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
                        確認して登録
                    </c:when>

                    <c:otherwise>
                        タスクを登録
                    </c:otherwise>
                </c:choose>
            </span>

        </button>

    </div>

</form>

<section class="task-form-guidance">

    <span
        class="task-form-guidance-icon"
        aria-hidden="true">
        i
    </span>

    <div>

        <h2 class="task-form-guidance-title">
            入力時のお願い
        </h2>

        <p class="task-form-guidance-description">
            タスク名には、家族が見て分かる「何をするか」を入力してください。
            病名、薬名、診断内容などの医療情報は入力しないでください。
        </p>

    </div>

</section>

<nav
    class="task-form-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="task-form-back-link"
        href="${pageContext.request.contextPath}/careshift/main/TaskList.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            タスク一覧へ戻る
        </span>

    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>