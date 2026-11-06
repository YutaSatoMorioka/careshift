<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="プラン"/></jsp:include><section class="page-content"><h1>プラン管理</h1><p>現在のプラン：<strong><c:out value="${plan.plan_name}"/></strong></p><p>決済事業者の契約設定がないため、このプロジェクトでは有料化・決済を開始できません。カード情報を入力しないでください。</p><p>基本機能は無料で利用できます。</p></section><jsp:include page="/common/footer.jsp"/>

