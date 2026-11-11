<%-- S07 タスク一覧 / 設計書: 画面設計書_タスク一覧 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="タスク一覧"/>
    <jsp:param name="pageClass" value="task-list-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/task-list.css?v=20261111-1">

<%-- =======================================================
     ページ上部
     ======================================================= --%>

<section class="task-page-header">

    <div class="task-page-heading">

        <p class="task-page-eyebrow">
            TASK MANAGEMENT
        </p>

        <h1 class="task-page-title">
            タスク一覧
        </h1>

        <p class="task-page-description">
            家族グループの介護タスク、担当者、期限、進捗状況を確認できます。
        </p>

    </div>

    <div class="task-page-actions">

        <a
            class="task-button task-button-secondary"
            href="${pageContext.request.contextPath}/careshift/main/Calendar.action">

            <span
                class="task-button-icon"
                aria-hidden="true">
                C
            </span>

            <span>カレンダーで見る</span>
        </a>

        <a
            class="task-button task-button-primary"
            id="create"
            href="${pageContext.request.contextPath}/careshift/main/TaskCreate.action">

            <span
                class="task-button-icon"
                aria-hidden="true">
                ＋
            </span>

            <span>新規登録</span>
        </a>

    </div>

</section>

<%-- =======================================================
     検索条件
     ======================================================= --%>

<section
    class="task-filter-card"
    aria-labelledby="task-filter-title">

    <header class="task-filter-header">

        <div>

            <p class="task-section-eyebrow">
                SEARCH
            </p>

            <h2
                class="task-filter-title"
                id="task-filter-title">
                タスクを絞り込む
            </h2>

        </div>

        <c:if test="${not empty keyword
                    or not empty assignee
                    or not empty dueDate
                    or not empty status}">

            <a
                class="task-filter-reset"
                href="${pageContext.request.contextPath}/careshift/main/TaskList.action">
                条件をクリア
            </a>

        </c:if>

    </header>

    <form
        class="task-filter-form"
        method="get"
        action="${pageContext.request.contextPath}/careshift/main/TaskList.action">

        <div class="task-filter-grid">

            <div class="task-filter-field task-filter-keyword">

                <label for="keyword">
                    キーワード
                </label>

                <div class="task-input-wrapper">

                    <span
                        class="task-input-icon"
                        aria-hidden="true">
                        検
                    </span>

                    <input
                        class="task-form-control task-form-control-icon"
                        type="text"
                        id="keyword"
                        name="keyword"
                        maxlength="60"
                        value="<c:out value='${keyword}'/>"
                        placeholder="タスク名を入力"
                        autocomplete="off">

                </div>

            </div>

            <div class="task-filter-field">

                <label for="assignee">
                    担当者
                </label>

                <select
                    class="task-form-control"
                    id="assignee"
                    name="assignee">

                    <option value="">
                        すべて
                    </option>

                    <option
                        value="none"
                        ${assignee == 'none' ? 'selected' : ''}>
                        担当なし
                    </option>

                    <c:forEach
                        var="m"
                        items="${members}">

                        <option
                            value="${m.userId}"
                            ${assigneeId == m.userId ? 'selected' : ''}>
                            <c:out value="${m.displayName}"/>
                        </option>

                    </c:forEach>

                </select>

            </div>

            <div class="task-filter-field">

                <label for="dueDate">
                    期限
                </label>

                <input
                    class="task-form-control${not empty errors['dueDate'] ? ' task-form-control-error' : ''}"
                    type="date"
                    id="dueDate"
                    name="dueDate"
                    value="<c:out value='${dueDate}'/>"
                    <c:if test="${not empty errors['dueDate']}">
                        aria-invalid="true"
                        aria-describedby="dueDate-error"
                    </c:if>>

                <c:if test="${not empty errors['dueDate']}">

                    <span
                        class="task-field-error"
                        id="dueDate-error"
                        role="alert">
                        <c:out value="${errors['dueDate']}"/>
                    </span>

                </c:if>

            </div>

            <div class="task-filter-field">

                <label for="status">
                    状態
                </label>

                <select
                    class="task-form-control"
                    id="status"
                    name="status">

                    <option value="">
                        すべて
                    </option>

                    <c:forEach
                        var="s"
                        items="${statuses}">

                        <option
                            value="<c:out value='${s.key}'/>"
                            ${s.key == status ? 'selected' : ''}>
                            <c:out value="${s.value}"/>
                        </option>

                    </c:forEach>

                </select>

            </div>

        </div>

        <div class="task-filter-actions">

            <button
                class="task-button task-button-search"
                type="submit"
                name="search">

                <span
                    class="task-button-icon"
                    aria-hidden="true">
                    検
                </span>

                <span>この条件で検索</span>
            </button>

        </div>

    </form>

</section>

<%-- =======================================================
     検索結果
     ======================================================= --%>

<section
    class="task-result-card"
    aria-labelledby="task-result-title">

    <header class="task-result-header">

        <div>

            <p class="task-section-eyebrow">
                TASK LIST
            </p>

            <h2
                class="task-result-title"
                id="task-result-title">
                検索結果
            </h2>

        </div>

        <div class="task-result-count">

            <strong>
                <c:choose>
                    <c:when test="${empty tasks}">
                        0
                    </c:when>

                    <c:otherwise>
                        <c:out value="${tasks.size()}"/>
                    </c:otherwise>
                </c:choose>
            </strong>

            <span>件</span>
        </div>

    </header>

    <c:choose>

        <%-- =================================================
             空状態
             ================================================= --%>

        <c:when test="${empty tasks}">

            <div
                class="task-empty-state"
                id="empty">

                <span
                    class="task-empty-icon"
                    aria-hidden="true">
                    T
                </span>

                <h3 class="task-empty-title">
                    条件に合うタスクはありません
                </h3>

                <p class="task-empty-description">
                    検索条件を変更するか、新しいタスクを登録してください。
                </p>

                <div class="task-empty-actions">

                    <a
                        class="task-button task-button-secondary"
                        href="${pageContext.request.contextPath}/careshift/main/TaskList.action">
                        検索条件をクリア
                    </a>

                    <a
                        class="task-button task-button-primary"
                        href="${pageContext.request.contextPath}/careshift/main/TaskCreate.action">
                        タスクを登録する
                    </a>

                </div>

            </div>

        </c:when>

        <%-- =================================================
             タスク一覧
             ================================================= --%>

        <c:otherwise>

            <%-- PC・タブレット向けテーブル --%>
            <div class="task-table-wrapper">

                <table
                    class="task-table"
                    id="taskList">

                    <thead>

                        <tr>
                            <th scope="col">
                                内容
                            </th>

                            <th scope="col">
                                担当者
                            </th>

                            <th scope="col">
                                期限
                            </th>

                            <th scope="col">
                                所要時間
                            </th>

                            <th scope="col">
                                状態
                            </th>

                            <th
                                class="task-table-action-heading"
                                scope="col">
                                詳細
                            </th>
                        </tr>

                    </thead>

                    <tbody>

                        <c:forEach
                            var="t"
                            items="${tasks}">

                            <tr class="${t.overdue ? 'task-row-overdue' : ''}">

                                <td class="task-title-cell">

                                    <a
                                        class="task-title-link"
                                        href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}">
                                        <c:out value="${t.title}"/>
                                    </a>

                                    <c:if test="${t.overdue}">

                                        <span class="task-overdue-text">
                                            期限を過ぎています
                                        </span>

                                    </c:if>

                                </td>

                                <td>

                                    <c:choose>

                                        <c:when test="${t.unassigned}">

                                            <span class="task-assignee-unassigned">
                                                担当なし
                                            </span>

                                        </c:when>

                                        <c:otherwise>

                                            <span class="task-assignee">

                                                <span
                                                    class="task-assignee-avatar"
                                                    aria-hidden="true">

                                                    <c:choose>
                                                        <c:when test="${not empty t.assigneeName}">
                                                            <c:out
                                                                value="${t.assigneeName.substring(0, 1)}"/>
                                                        </c:when>

                                                        <c:otherwise>
                                                            人
                                                        </c:otherwise>
                                                    </c:choose>

                                                </span>

                                                <span>
                                                    <c:out
                                                        value="${t.assigneeName}"
                                                        default="担当者"/>
                                                </span>

                                            </span>

                                        </c:otherwise>

                                    </c:choose>

                                </td>

                                <td>

                                    <span class="task-due-date">
                                        <c:out
                                            value="${t.dueDate}"
                                            default="未設定"/>
                                    </span>

                                    <c:if test="${t.overdue}">

                                        <span
                                            class="task-overdue-badge"
                                            role="status">
                                            期限超過
                                        </span>

                                    </c:if>

                                </td>

                                <td>
                                    <span class="task-duration">
                                        <c:out
                                            value="${t.durationLabel}"
                                            default="未設定"/>
                                    </span>
                                </td>

                                <td>

                                    <span class="task-status-badge">
                                        <c:out
                                            value="${t.statusLabel}"
                                            default="未対応"/>
                                    </span>

                                </td>

                                <td class="task-table-action-cell">

                                    <a
                                        class="task-detail-button"
                                        href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}"
                                        aria-label="<c:out value='${t.title}'/>の詳細を見る">
                                        &rsaquo;
                                    </a>

                                </td>

                            </tr>

                        </c:forEach>

                    </tbody>

                </table>

            </div>

            <%-- スマートフォン向けカード一覧 --%>
            <div
                class="task-mobile-list"
                aria-label="タスク一覧">

                <c:forEach
                    var="t"
                    items="${tasks}">

                    <article class="task-mobile-card${t.overdue ? ' task-mobile-card-overdue' : ''}">

                        <div class="task-mobile-header">

                            <span class="task-status-badge">
                                <c:out
                                    value="${t.statusLabel}"
                                    default="未対応"/>
                            </span>

                            <c:if test="${t.overdue}">

                                <span class="task-overdue-badge">
                                    期限超過
                                </span>

                            </c:if>

                        </div>

                        <h3 class="task-mobile-title">

                            <a
                                href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}">
                                <c:out value="${t.title}"/>
                            </a>

                        </h3>

                        <dl class="task-mobile-details">

                            <div>
                                <dt>担当者</dt>

                                <dd>

                                    <c:choose>
                                        <c:when test="${t.unassigned}">
                                            <span class="task-assignee-unassigned">
                                                担当なし
                                            </span>
                                        </c:when>

                                        <c:otherwise>
                                            <c:out
                                                value="${t.assigneeName}"
                                                default="担当者"/>
                                        </c:otherwise>
                                    </c:choose>

                                </dd>
                            </div>

                            <div>
                                <dt>期限</dt>

                                <dd>
                                    <c:out
                                        value="${t.dueDate}"
                                        default="未設定"/>
                                </dd>
                            </div>

                            <div>
                                <dt>所要時間</dt>

                                <dd>
                                    <c:out
                                        value="${t.durationLabel}"
                                        default="未設定"/>
                                </dd>
                            </div>

                        </dl>

                        <a
                            class="task-mobile-detail-link"
                            href="${pageContext.request.contextPath}/careshift/main/TaskDetail.action?id=${t.id}">
                            詳細を見る
                            <span aria-hidden="true">
                                &rsaquo;
                            </span>
                        </a>

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
    class="task-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="task-back-link"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>ホームへ戻る</span>
    </a>

</nav>

<jsp:include page="/common/footer.jsp"/>