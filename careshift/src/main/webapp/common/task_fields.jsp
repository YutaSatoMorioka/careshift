<%-- タスク登録・編集の共通入力項目 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<section
    class="task-form-card"
    aria-labelledby="task-basic-title">

    <header class="task-form-card-header">

        <span
            class="task-form-card-icon"
            aria-hidden="true">
            1
        </span>

        <div>

            <h2
                class="task-form-card-title"
                id="task-basic-title">
                タスク内容
            </h2>

            <p class="task-form-card-description">
                家族が見て分かる、具体的な作業内容を入力します。
            </p>

        </div>

    </header>

    <div class="task-form-card-body">

        <div class="task-form-field">

            <div class="task-form-label-row">

                <label for="taskTitle">

                    タスク内容

                    <span class="task-form-required">
                        必須
                    </span>

                </label>

                <span class="task-form-character-limit">
                    100文字以内
                </span>

            </div>

            <input
                class="task-form-control${not empty errors['taskTitle'] ? ' is-error' : ''}"
                type="text"
                id="taskTitle"
                name="taskTitle"
                maxlength="100"
                value="<c:out value='${form.taskTitle}'/>"
                aria-invalid="${not empty errors['taskTitle']}"
                placeholder="例：買い物に行く"
                autocomplete="off">

            <c:if test="${not empty errors['taskTitle']}">

                <span
                    class="task-form-field-error"
                    id="taskTitle-error"
                    role="alert">
                    <c:out value="${errors['taskTitle']}"/>
                </span>

            </c:if>

            <p class="task-form-field-help">
                病名、薬名、診断内容ではなく、実際に行う作業を入力してください。
            </p>

        </div>

    </div>

</section>

<section
    class="task-form-card"
    aria-labelledby="task-schedule-title">

    <header class="task-form-card-header">

        <span
            class="task-form-card-icon"
            aria-hidden="true">
            2
        </span>

        <div>

            <h2
                class="task-form-card-title"
                id="task-schedule-title">
                期限と所要時間
            </h2>

            <p class="task-form-card-description">
                タスクの期限と、作業に必要な時間を設定します。
            </p>

        </div>

    </header>

    <div class="task-form-card-body">

        <div class="task-form-grid">

            <div class="task-form-field">

                <label for="dueDate">

                    期限

                    <span class="task-form-required">
                        必須
                    </span>

                </label>

                <input
                    class="task-form-control${not empty errors['dueDate'] ? ' is-error' : ''}"
                    type="date"
                    id="dueDate"
                    name="dueDate"
                    value="<c:out value='${form.dueDate}'/>"
                    aria-invalid="${not empty errors['dueDate']}">

                <c:if test="${not empty errors['dueDate']}">

                    <span
                        class="task-form-field-error"
                        id="dueDate-error"
                        role="alert">
                        <c:out value="${errors['dueDate']}"/>
                    </span>

                </c:if>

                <p class="task-form-field-help">
                    この日までに対応する期限を指定します。
                </p>

            </div>

            <div class="task-form-field">

                <label for="duration">

                    所要時間

                    <span class="task-form-required">
                        必須
                    </span>

                </label>

                <div class="task-form-number-wrapper">

                    <input
                        class="task-form-control${not empty errors['duration'] ? ' is-error' : ''}"
                        type="number"
                        id="duration"
                        name="duration"
                        min="5"
                        max="1440"
                        step="5"
                        value="<c:out value='${form.duration}'/>"
                        aria-invalid="${not empty errors['duration']}"
                        placeholder="30">

                    <span class="task-form-number-suffix">
                        分
                    </span>

                </div>

                <c:if test="${not empty errors['duration']}">

                    <span
                        class="task-form-field-error"
                        id="duration-error"
                        role="alert">
                        <c:out value="${errors['duration']}"/>
                    </span>

                </c:if>

                <p class="task-form-field-help">
                    5分以上、1440分以内で入力してください。
                </p>

            </div>

        </div>

    </div>

</section>

<section
    class="task-form-card"
    aria-labelledby="task-assignment-title">

    <header class="task-form-card-header">

        <span
            class="task-form-card-icon"
            aria-hidden="true">
            3
        </span>

        <div>

            <h2
                class="task-form-card-title"
                id="task-assignment-title">
                頻度と担当者
            </h2>

            <p class="task-form-card-description">
                タスクを行う頻度と、担当する家族を設定します。
            </p>

        </div>

    </header>

    <div class="task-form-card-body">

        <div class="task-form-grid">

            <div class="task-form-field">

                <label for="frequency">

                    頻度

                    <span class="task-form-required">
                        必須
                    </span>

                </label>

                <select
                    class="task-form-control${not empty errors['frequency'] ? ' is-error' : ''}"
                    id="frequency"
                    name="frequency"
                    aria-invalid="${not empty errors['frequency']}">

                    <c:forEach
                        var="f"
                        items="${frequencies}">

                        <option
                            value="<c:out value='${f.key}'/>"
                            ${f.key == form.frequency ? 'selected' : ''}>
                            <c:out value="${f.value}"/>
                        </option>

                    </c:forEach>

                </select>

                <c:if test="${not empty errors['frequency']}">

                    <span
                        class="task-form-field-error"
                        id="frequency-error"
                        role="alert">
                        <c:out value="${errors['frequency']}"/>
                    </span>

                </c:if>

                <p class="task-form-field-help">
                    定期的に行うタスクか、1回だけのタスクかを選択します。
                </p>

            </div>

            <div class="task-form-field">

                <label for="assignee">

                    担当者

                    <span class="task-form-optional">
                        任意
                    </span>

                </label>

                <select
                    class="task-form-control${not empty errors['assignee'] ? ' is-error' : ''}"
                    id="assignee"
                    name="assignee"
                    aria-invalid="${not empty errors['assignee']}">

                    <option value="">
                        担当なし
                    </option>

                    <c:forEach
                        var="m"
                        items="${members}">

                        <option
                            value="${m.userId}"
                            ${form.assigneeId == m.userId ? 'selected' : ''}>
                            <c:out value="${m.displayName}"/>

                            <c:if test="${m.primary}">
                                （主介護者）
                            </c:if>
                        </option>

                    </c:forEach>

                </select>

                <c:if test="${not empty errors['assignee']}">

                    <span
                        class="task-form-field-error"
                        id="assignee-error"
                        role="alert">
                        <c:out value="${errors['assignee']}"/>
                    </span>

                </c:if>

                <p class="task-form-field-help">
                    担当者を設定すると、選択された家族へ通知されます。
                </p>

            </div>

        </div>

    </div>

</section>

<c:if test="${editMode}">

    <section
        class="task-form-card"
        aria-labelledby="task-status-title">

        <header class="task-form-card-header">

            <span
                class="task-form-card-icon"
                aria-hidden="true">
                4
            </span>

            <div>

                <h2
                    class="task-form-card-title"
                    id="task-status-title">
                    進捗
                </h2>

                <p class="task-form-card-description">
                    現在の対応状況を設定します。
                </p>

            </div>

        </header>

        <div class="task-form-card-body">

            <div class="task-form-field">

                <label for="status">

                    状態

                    <span class="task-form-required">
                        必須
                    </span>

                </label>

                <select
                    class="task-form-control${not empty errors['status'] ? ' is-error' : ''}"
                    id="status"
                    name="status"
                    aria-invalid="${not empty errors['status']}">

                    <c:forEach
                        var="s"
                        items="${statuses}">

                        <option
                            value="<c:out value='${s.key}'/>"
                            ${s.key == form.status ? 'selected' : ''}>
                            <c:out value="${s.value}"/>
                        </option>

                    </c:forEach>

                </select>

                <c:if test="${not empty errors['status']}">

                    <span
                        class="task-form-field-error"
                        id="status-error"
                        role="alert">
                        <c:out value="${errors['status']}"/>
                    </span>

                </c:if>

            </div>

        </div>

    </section>

</c:if>