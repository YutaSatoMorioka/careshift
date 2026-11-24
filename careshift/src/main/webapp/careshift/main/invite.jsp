<%-- S04 招待から参加 / 設計書: S04_招待参加 --%>
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
        value="家族グループへの招待"/>

    <jsp:param
        name="pageClass"
        value="invite-main"/>

</jsp:include>

<link
    rel="stylesheet"
    type="text/css"
    href="${pageContext.request.contextPath}/css/invite.css?v=20261124-1">

<%-- =======================================================
     ページヘッダー
     ======================================================= --%>

<section class="invite-page-header">

    <div class="invite-page-heading">

        <p class="invite-page-eyebrow">
            FAMILY GROUP INVITATION
        </p>

        <h1 class="invite-page-title">
            家族グループへの招待
        </h1>

        <p class="invite-page-description">
            家族グループへの招待が届いています。
            グループ名、招待者、登録される続柄と役割を確認してください。
        </p>

    </div>

    <a
        class="invite-header-link"
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
     招待案内
     ======================================================= --%>

<section
    class="invite-introduction-card"
    aria-labelledby="invite-introduction-title">

    <span
        class="invite-introduction-icon"
        aria-hidden="true">
        招
    </span>

    <div class="invite-introduction-content">

        <h2 id="invite-introduction-title">
            このグループに参加しますか？
        </h2>

        <p>
            参加すると、家族グループ内の予定、タスク、依頼などを
            ほかのメンバーと共有できるようになります。
        </p>

        <p>
            内容に心当たりがない場合や、表示されているグループが異なる場合は、
            参加せずに招待者へ確認してください。
        </p>

    </div>

</section>

<%-- =======================================================
     招待内容
     ======================================================= --%>

<section
    class="invite-card"
    aria-labelledby="invite-details-title">

    <header class="invite-card-header">

        <div>

            <p class="invite-section-eyebrow">
                INVITATION DETAILS
            </p>

            <h2
                class="invite-card-title"
                id="invite-details-title">
                招待内容
            </h2>

        </div>

        <span class="invite-status-badge">
            有効な招待
        </span>

    </header>

    <div class="invite-group-summary">

        <span
            class="invite-group-icon"
            aria-hidden="true">
            家
        </span>

        <div class="invite-group-content">

            <span class="invite-group-label">
                招待された家族グループ
            </span>

            <strong
                class="invite-group-name"
                id="groupName">
                <c:out value="${invitation.groupName}"/>
            </strong>

        </div>

    </div>

    <dl class="invite-details">

        <div class="invite-detail-row">

            <dt>
                招待者
            </dt>

            <dd id="inviter">

                <span class="invite-person">

                    <span
                        class="invite-person-avatar"
                        aria-hidden="true">
                        人
                    </span>

                    <span>
                        <c:out value="${invitation.inviterName}"/>
                    </span>

                </span>

            </dd>

        </div>

        <div class="invite-detail-row">

            <dt>
                続柄・役割
            </dt>

            <dd id="relationshipRole">

                <span class="invite-role-badge">
                    <c:out value="${invitation.relationshipRole}"/>
                </span>

            </dd>

        </div>

        <div class="invite-detail-row">

            <dt>
                招待の有効期限
            </dt>

            <dd id="expiresAt">

                <span class="invite-expiration">

                    <span
                        class="invite-expiration-icon"
                        aria-hidden="true">
                        日
                    </span>

                    <c:out value="${invitation.expiresAtLabel}"/>

                </span>

            </dd>

        </div>

    </dl>

</section>

<%-- =======================================================
     参加前の確認事項
     ======================================================= --%>

<section
    class="invite-confirmation-card"
    aria-labelledby="invite-confirmation-title">

    <header class="invite-confirmation-header">

        <span
            class="invite-confirmation-icon"
            aria-hidden="true">
            確
        </span>

        <div>

            <h2 id="invite-confirmation-title">
                参加前に確認してください
            </h2>

            <p>
                「参加する」を押す前に、次の内容を確認してください。
            </p>

        </div>

    </header>

    <ul class="invite-confirmation-list">

        <li>
            1つのアカウントが所属できる家族グループは1つです。
        </li>

        <li>
            この招待リンクは1名分のみ有効で、一度だけ使用できます。
        </li>

        <li>
            参加すると、招待者と主介護者に参加したことが通知されます。
        </li>

        <li>
            グループ内では、表示名や登録された続柄・役割が共有されます。
        </li>

    </ul>

</section>

<%-- =======================================================
     操作ボタン
     ======================================================= --%>

<div class="invite-actions">

    <a
        class="invite-button invite-button-secondary"
        href="${pageContext.request.contextPath}/careshift/main/Menu.action">

        <span aria-hidden="true">
            &lsaquo;
        </span>

        <span>
            今は参加しない
        </span>

    </a>

    <form
        class="invite-join-form"
        method="post"
        action="${pageContext.request.contextPath}/careshift/InviteJoin.action"
        onsubmit="return inviteConfirmJoin(this);">

        <%--
            招待トークンは画面本文には表示せず、
            InviteJoinActionへの送信値としてのみ保持する。
        --%>
        <input
            type="hidden"
            name="token"
            value="<c:out value='${token}'/>">

        <button
            class="invite-button invite-button-primary"
            id="invite-join-button"
            type="submit"
            name="join">

            <span
                class="invite-button-icon"
                aria-hidden="true">
                参
            </span>

            <span class="invite-button-label">
                このグループに参加する
            </span>

        </button>

    </form>

</div>

<%-- =======================================================
     セキュリティ案内
     ======================================================= --%>

<section class="invite-security-note">

    <span
        class="invite-security-icon"
        aria-hidden="true">
        鍵
    </span>

    <div>

        <h2>
            招待リンクの取り扱い
        </h2>

        <p>
            招待リンクを第三者へ転送しないでください。
            心当たりのない招待の場合は参加せず、リンクを開いた画面を閉じてください。
        </p>

    </div>

</section>

<nav
    class="invite-footer-nav"
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

    window.inviteConfirmJoin = function (form) {
        var groupNameElement =
            document.getElementById("groupName");

        var groupName =
            groupNameElement
                ? groupNameElement.textContent.trim()
                : "この家族グループ";

        var confirmed =
            window.confirm(
                "「"
                + groupName
                + "」に参加します。よろしいですか？"
            );

        if (!confirmed) {
            return false;
        }

        var button =
            form.querySelector(
                "#invite-join-button"
            );

        if (button) {
            button.disabled = true;
            button.classList.add("is-processing");

            var label =
                button.querySelector(
                    ".invite-button-label"
                );

            if (label) {
                label.textContent =
                    "参加処理中...";
            }
        }

        return true;
    };
})();
</script>

<jsp:include page="/common/footer.jsp"/>