<%-- S18 コラム・チェックリスト / 設計書: 画面設計書_コラム・チェックリスト --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp"><jsp:param name="title" value="コラム"/></jsp:include>

<h1>コラム・チェックリスト</h1>

<form method="get" action="${pageContext.request.contextPath}/careshift/main/Column.action">
  <label for="category">カテゴリ</label>
  <select id="category" name="category">
    <option value="">すべて</option>
    <c:forEach var="cat" items="${categories}">
      <option value="<c:out value='${cat}'/>" ${cat == category ? 'selected' : ''}><c:out value="${cat}"/></option>
    </c:forEach>
  </select>
  <button type="submit" name="filter">絞り込む</button>
</form>

<c:choose>
  <c:when test="${empty articles}">
    <div class="guide" id="comingSoon"><p>このカテゴリのコラムは準備中です。</p></div>
  </c:when>
  <c:otherwise>
    <table id="articles">
      <tr><th>カテゴリ</th><th>タイトル</th><th>概要</th></tr>
      <c:forEach var="a" items="${articles}">
        <tr>
          <td><c:out value="${a.category}"/></td>
          <td><a href="${pageContext.request.contextPath}/careshift/main/Column.action?id=${a.id}&amp;category=<c:out value='${category}'/>"><c:out value="${a.title}"/></a></td>
          <td><c:out value="${a.summary}"/></td>
        </tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>

<c:if test="${not empty article}">
  <hr>
  <h2 id="articleTitle"><c:out value="${article.title}"/></h2>
  <div id="articleBody">
    <c:forEach var="p" items="${article.paragraphs}"><p><c:out value="${p}"/></p></c:forEach>
  </div>

  <h3>チェックリスト</h3>
  <p><small>チェックの状態は、あなただけに保存されます（家族には共有されません）。</small></p>
  <c:if test="${checklistError}">
    <p class="alert" id="checklistError">チェック状態を読み込めませんでした。
      <a href="${pageContext.request.contextPath}/careshift/main/Column.action?id=${article.id}&amp;category=<c:out value='${category}'/>">再試行する</a></p>
  </c:if>
  <form method="post" action="${pageContext.request.contextPath}/careshift/main/ChecklistSave.action">
    <input type="hidden" name="articleId" value="<c:out value='${article.id}'/>">
    <input type="hidden" name="category" value="<c:out value='${category}'/>">
    <ul id="checklist" style="list-style:none; padding-left:0;">
      <c:forEach var="item" items="${article.checklist}" varStatus="st">
        <li><label><input type="checkbox" name="check" value="${st.index}" ${checked.contains(st.index) ? 'checked' : ''}> <c:out value="${item}"/></label></li>
      </c:forEach>
    </ul>
    <button type="submit" name="save">チェックを保存</button>
  </form>
  <p><small>このコラムは一般的な情報で、医療・介護の判断をするものではありません。心身の状態や介護の方法は、専門職にご相談ください。</small></p>
</c:if>

<p><a href="${pageContext.request.contextPath}/careshift/main/Menu.action">ホームへ戻る</a></p>

<jsp:include page="/common/footer.jsp"/>
