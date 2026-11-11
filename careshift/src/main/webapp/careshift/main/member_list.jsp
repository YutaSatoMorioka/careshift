<%-- S16 家族メンバー / 設計書: 画面設計書_メンバー管理 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="家族メンバー"/>
    <jsp:param name="pageClass" value="member-list-main"/>
</jsp:include>

<link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/css/member-list.css?v=20261111-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="member-page-header">

    <div class="member-page-heading">

        <p class="member-page-eyebrow">
            FAMILY MEMBERS
        </p>

        <h1 class="member-page-title">
            家族メンバー
        </h1>

        <p class="member-page-description">
            家族グループのメンバー情報、続柄、連絡先、役割を管理できます。
        </p>

    </div>

    <a
        class="member-button member-button-primary"
        href="${pageContext.request.contextPath}/careshift/main/Group.action">

        <span
            class="member-button-icon"
            aria-hidden="true">
            ＋
        </span>

        <span>
            招待・グループ設定
        </span>

    </a>

</section>

<%-- =======================================================
     グループ概要
     ======================================================= --%>

<section
    class="member-group-summary"
    aria-labelledby="member-group-title">

    <div class="member-group-main">

        <span
            class="member-group-icon"
            aria-hidden="true">
            G
        </span>

        <div class="member-group-content">

            <span class="member-group-label">
                家族グループ
            </span>

            <h2
                class="member-group-name"
                id="member-group-title">
                <c:out
                    value="${group.name}"
                    default="家族グループ"/>
            </h2>

        </div>

    </div>

    <div class="member-count-summary">

        <div class="member-count-text">

            <span>
                参加メンバー
            </span>

            <strong>
                <c:out
                    value="${members.size()}"
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
            class="member-count-bar"
            aria-label="メンバー数">

            <span
                class="member-count-bar-fill"
                style="width: ${(members.size() * 100) / maxMembers}%;">
            </span>

        </div>

    </div>

</section>

<%-- =======================================================
     権限案内
     ======================================================= --%>

<c:choose>

    <c:when test="${member.primary}">

        <div class="member-permission-note is-primary">

            <span
                class="member-permission-icon"
                aria-hidden="true">
                主
            </span>

            <div>

                <strong>
                    主介護者として管理できます
                </strong>

                <p>
                    すべてのメンバーの続柄と連絡先を編集できます。
                    また、別のメンバーへ主介護者の役割を引き継ぐことができます。
                </p>

            </div>

        </div>

    </c:when>

    <c:otherwise>

        <div class="member-permission-note">

            <span
                class="member-permission-icon"
                aria-hidden="true">
                i
            </span>

            <div>

                <strong>
                    自分の情報を編集できます
                </strong>

                <p>
                    自分の「続柄」と「連絡先」だけ変更できます。
                    その他のメンバーや役割の変更は主介護者が行います。
                </p>

            </div>

        </div>

    </c:otherwise>

</c:choose>

<%-- =======================================================
     メンバー一覧
     ======================================================= --%>

<section
    class="member-result-card"
    aria-labelledby="member-list-title">

    <header class="member-result-header">

        <div>

            <p class="member-section-eyebrow">
                MEMBER LIST
            </p>

            <h2
                class="member-result-title"
                id="member-list-title">
                メンバー一覧
            </h2>

        </div>

        <span class="member-result-count">
            <strong>
                <c:out
                    value="${members.size()}"
                    default="0"/>
            </strong>

            <span>
                名
            </span>
        </span>

    </header>

    <div
        class="member-card-grid"
        id="members">

        <c:forEach
            var="m"
            items="${members}">

            <c:set
                var="canEdit"
                value="${member.primary or m.userId == member.userId}"/>

            <c:set
                var="isMe"
                value="${m.userId == member.userId}"/>

            <article class="member-card${m.primary ? ' is-primary' : ''}${isMe ? ' is-me' : ''}">

                <header class="member-card-header">

                    <span
                        class="member-avatar"
                        aria-hidden="true">
                        人
                    </span>

                    <div class="member-identity">

                        <div class="member-name-row">

                            <h3 class="member-name">
                                <c:out
                                    value="${m.displayName}"
                                    default="メンバー"/>
                            </h3>

                            <c:if test="${isMe}">
                                <span class="member-me-badge">
                                    あなた
                                </span>
                            </c:if>

                        </div>

                        <div class="member-role-row">

                            <c:choose>

                                <c:when test="${m.primary}">
                                    <span class="member-role-badge is-primary">
                                        主介護者
                                    </span>
                                </c:when>

                                <c:otherwise>
                                    <span class="member-role-badge">
                                        家族等
                                    </span>
                                </c:otherwise>

                            </c:choose>

                        </div>

                    </div>

                </header>

                <form
                    class="member-edit-form"
                    id="save-${m.userId}"
                    method="post"
                    action="${pageContext.request.contextPath}/careshift/main/MemberSave.action">

                    <input
                        type="hidden"
                        name="userId"
                        value="${m.userId}">

                    <div class="member-form-field">

                        <label for="relationship-${m.userId}">
                            続柄
                        </label>

                        <select
                            class="member-form-control"
                            id="relationship-${m.userId}"
                            name="relationship"
                            ${canEdit ? '' : 'disabled'}>

                            <option value="">
                                未設定
                            </option>

                            <c:forEach
                                var="r"
                                items="${relationships}">

                                <option
                                    value="<c:out value='${r}'/>"
                                    ${r == m.relationship ? 'selected' : ''}>
                                    <c:out value="${r}"/>
                                </option>

                            </c:forEach>

                        </select>

                    </div>

                    <div class="member-form-field">

                        <label for="role-${m.userId}">
                            役割
                        </label>

                        <c:choose>

                            <c:when test="${member.primary}">

                                <select
                                    class="member-form-control"
                                    id="role-${m.userId}"
                                    name="role">

                                    <option
                                        value="PRIMARY"
                                        ${m.primary ? 'selected' : ''}>
                                        主介護者
                                    </option>

                                    <option
                                        value="FAMILY"
                                        ${m.primary ? '' : 'selected'}>
                                        家族等
                                    </option>

                                </select>

                            </c:when>

                            <c:otherwise>

                                <div class="member-readonly-value">

                                    <c:choose>
                                        <c:when test="${m.primary}">
                                            主介護者
                                        </c:when>

                                        <c:otherwise>
                                            家族等
                                        </c:otherwise>
                                    </c:choose>

                                </div>

                            </c:otherwise>

                        </c:choose>

                    </div>

                    <div class="member-form-field member-contact-field">

                        <label for="contact-${m.userId}">
                            連絡先
                        </label>

                        <input
                            class="member-form-control"
                            type="text"
                            id="contact-${m.userId}"
                            name="contact"
                            maxlength="100"
                            value="<c:out value='${m.contact}'/>"
                            placeholder="電話番号またはメールアドレス"
                            ${canEdit ? '' : 'disabled'}>

                        <c:if test="${canEdit}">
                            <p class="member-field-help">
                                電話番号やメールアドレスなど、家族間で共有する連絡先を入力します。
                            </p>
                        </c:if>

                    </div>

                    <c:choose>

                        <c:when test="${canEdit}">

                            <div class="member-card-actions">

                                <button
                                    class="member-button member-button-save"
                                    type="submit"
                                    name="save">

                                    <span
                                        class="member-button-icon"
                                        aria-hidden="true">
                                        ✓
                                    </span>

                                    <span>
                                        情報を保存
                                    </span>

                                </button>

                            </div>

                        </c:when>

                        <c:otherwise>

                            <div class="member-card-locked">

                                <span
                                    class="member-card-locked-icon"
                                    aria-hidden="true">
                                    鍵
                                </span>

                                <span>
                                    このメンバーの情報は主介護者のみ編集できます。
                                </span>

                            </div>

                        </c:otherwise>

                    </c:choose>

                </form>

                <c:if test="${member.primary and not m.primary}">

                    <div class="member-management-actions">

                        <form
                            method="post"
                            action="${pageContext.request.contextPath}/careshift/main/MemberSetPrimary.action"
                            onsubmit="return confirm('この人を主介護者に設定しますか？ 現在の主介護者は「家族等」に変更されます。');">

                            <input
                                type="hidden"
                                name="userId"
                                value="${m.userId}">

                            <button
                                class="member-management-button member-primary-button"
                                type="submit"
                                name="setPrimary">

                                <span aria-hidden="true">
                                    主
                                </span>

                                <span>
                                    主介護者に設定
                                </span>

                            </button>

                        </form>

                        <form
                            method="post"
                            action="${pageContext.request.contextPath}/careshift/main/MemberRemove.action"
                            onsubmit="return confirm('このメンバーを家族グループから削除します。担当しているタスクは未割当になります。よろしいですか？');">

                            <input
                                type="hidden"
                                name="userId"
                                value="${m.userId}">

                            <button
                                class="member-management-button member-remove-button"
                                type="submit"
                                name="remove">

                                <span aria-hidden="true">
                                    ×
                                </span>

                                <span>
                                    グループから削除
                                </span>

                            </button>

                        </form>

                    </div>

                </c:if>

            </article>

        </c:forEach>

    </div>

</section>

<%-- =======================================================
     グループ管理案内
     ======================================================= --%>

<section class="member-group-management">

    <div class="member-group-management-content">

        <span
            class="member-group-management-icon"
            aria-hidden="true">
            G
        </span>

        <div>

            <h2 class="member-group-management-title">
                家族グループの設定
            </h2>

            <p class="member-group-management-description">
                招待リンクの発行、グループ名の変更、参加方法の確認ができます。
            </p>

        </div>

    </div>

    <a
        class="member-button member-button-secondary"
        href="${pageContext.request.contextPath}/careshift/main/Group.action">
        グループ設定を開く
        <span aria-hidden="true">
            &rsaquo;
        </span>
    </a>

</section>

<%-- =======================================================
     下部ナビゲーション
     ======================================================= --%>

<nav
    class="member-page-footer-nav"
    aria-label="ページ下部のナビゲーション">

    <a
        class="member-back-link"
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