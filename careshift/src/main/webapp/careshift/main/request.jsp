<%--
    旧依頼画面URLとの互換用JSP。

    旧画面:
    /careshift/main/request.jsp

    正式画面:
    /careshift/main/RequestList.action

    旧care_requests・care_request_responses方式の画面は表示せず、
    requests・request_recipients方式の正式な依頼一覧へ移動する。
--%>

<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
%>

<%
    /*
     * ブラウザーやブックマークなどから旧JSPが直接呼ばれた場合も、
     * 正式な依頼一覧へリダイレクトする。
     *
     * getContextPath()には通常「/careshift」が入る。
     */
    response.sendRedirect(
            request.getContextPath()
            + "/careshift/main/RequestList.action"
    );

    return;
%>