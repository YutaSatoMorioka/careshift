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
