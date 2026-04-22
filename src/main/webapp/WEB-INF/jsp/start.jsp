<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/header.jsp" %>
<h1>自販機シミュレーター</h1>

<%-- 消費者ボタン --%>
<a href="${pageContext.request.contextPath}/bending/consumer">
  <button class="button">消費者</button>
</a>

<%-- 業者ボタン --%>
<a href="${pageContext.request.contextPath}/bending/admin">
  <button class="button">業者</button>
</a>

<%-- 初期化ボタン --%>
<form action="${pageContext.request.contextPath}/bending/start" method="post">
  <button class="button" type="submit">初期化</button>
</form>
<%@ include file="/footer.jsp" %>