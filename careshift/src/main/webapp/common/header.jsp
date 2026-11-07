<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<<<<<<< HEAD
<%
  String csrfToken = (String) session.getAttribute("csrfToken");
  if (csrfToken == null) {
    csrfToken = java.util.UUID.randomUUID().toString();
    session.setAttribute("csrfToken", csrfToken);
  }
  pageContext.setAttribute("csrfTokenForForms", csrfToken);
%>
=======

>>>>>>> branch 'master' of https://github.com/YutaSatoMorioka/careshift.git
<!DOCTYPE html>
<html lang="ja">

<head>
<<<<<<< HEAD
  <meta charset="UTF-8">
  <title><c:out value="${param.title}"/> | Care Shift</title>
  <style>
    body { font-family: sans-serif; font-size: 16px; margin: 0; }
    header { background: #2f6f5e; color: #fff; padding: 12px 24px; display: flex; justify-content: space-between; }
    header a { color: #fff; }
    main { padding: 24px; }
    .error { color: #c00; }
    .success { color: #1a6b3c; background: #e8f5ee; padding: 8px 12px; border-radius: 4px; }
    .alert { color: #c00; background: #fdecec; padding: 8px 12px; border-radius: 4px; }
    .guide { background: #fff8e1; padding: 8px 16px; border-radius: 4px; }
    table { border-collapse: collapse; }
    th, td { border: 1px solid #ccc; padding: 6px 10px; text-align: left; vertical-align: top; }
    .tag { background: #2f6f5e; color: #fff; border-radius: 3px; padding: 1px 6px; font-size: 0.85em; }
    footer { padding: 12px 24px; color: #666; border-top: 1px solid #ddd; }
  </style>
=======
    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        <c:choose>
            <c:when test="${not empty param.title}">
                <c:out value="${param.title}"/> | Care Shift
            </c:when>
            <c:otherwise>
                Care Shift
            </c:otherwise>
        </c:choose>
    </title>

    <link
        rel="stylesheet"
        href="${pageContext.request.contextPath}/css/style.css">
>>>>>>> branch 'master' of https://github.com/YutaSatoMorioka/careshift.git
</head>
<<<<<<< HEAD
<body data-csrf-token="${csrfTokenForForms}">
<header>
  <strong>Care Shift</strong>
  <label>文字サイズ
    <select id="fontSizeControl" aria-label="文字サイズ">
      <option value="16">標準</option><option value="18">大きめ</option><option value="22">大きい</option>
    </select>
  </label>
  <c:if test="${not empty sessionScope.user}">
    <span>
      <c:out value="${sessionScope.user.displayName}"/> さん
      | <a href="${pageContext.request.contextPath}/careshift/Logout.action">ログアウト</a>
    </span>
  </c:if>
</header>
<section>
<%-- 直前の処理結果メッセージ（Action.flash / flashError）。表示したら消す --%>
<c:if test="${not empty sessionScope.flash}">
  <p class="success"><c:out value="${sessionScope.flash}"/></p>
  <c:remove var="flash" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashError}">
  <p class="alert"><c:out value="${sessionScope.flashError}"/></p>
  <c:remove var="flashError" scope="session"/>
</c:if>

=======

<body>

<header class="site-header">
    <a
        class="site-logo"
        href="${pageContext.request.contextPath}/index.jsp"
        aria-label="Care Shift トップページ">

        <span class="logo-mark" aria-hidden="true">
            C
        </span>

        <span>Care Shift</span>
    </a>

    <div class="header-user">

        <%-- userがセッションに存在する場合はログイン済み --%>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <span class="header-user-name">
                    ログイン中
                </span>

                <a
                    class="logout-link"
                    href="${pageContext.request.contextPath}/careshift/Logout.action">
                    ログアウト
                </a>
            </c:when>

            <%-- loginUserという名前で保存されている場合にも対応 --%>
            <c:when test="${not empty sessionScope.loginUser}">
                <span class="header-user-name">
                    ログイン中
                </span>

                <a
                    class="logout-link"
                    href="${pageContext.request.contextPath}/careshift/Logout.action">
                    ログアウト
                </a>
            </c:when>

            <%-- 未ログインの場合 --%>
            <c:otherwise>
                <a
                    class="header-login-link"
                    href="${pageContext.request.contextPath}/login.jsp">
                    ログイン
                </a>
            </c:otherwise>
        </c:choose>

    </div>
</header>
>>>>>>> branch 'master' of https://github.com/YutaSatoMorioka/careshift.git
