<%-- 共通ヘッダ。使い方: <jsp:include page="/common/header.jsp"><jsp:param name="title" value="画面名"/></jsp:include> --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title><c:out value="${param.title}"/> | Care Shift</title>
  <style>
    body { font-family: sans-serif; margin: 0; }
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
</head>
<body>
<header>
  <strong>Care Shift</strong>
  <c:if test="${not empty sessionScope.user}">
    <span>
      <c:out value="${sessionScope.user.displayName}"/> さん
      | <a href="${pageContext.request.contextPath}/careshift/Logout.action">ログアウト</a>
    </span>
  </c:if>
</header>
<main>
<%-- 直前の処理結果メッセージ（Action.flash / flashError）。表示したら消す --%>
<c:if test="${not empty sessionScope.flash}">
  <p class="success"><c:out value="${sessionScope.flash}"/></p>
  <c:remove var="flash" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashError}">
  <p class="alert"><c:out value="${sessionScope.flashError}"/></p>
  <c:remove var="flashError" scope="session"/>
</c:if>
