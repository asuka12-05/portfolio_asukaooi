<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ include file="/header.jsp"%>
<link rel="stylesheet" href="${ctx}/css/start.css">
<div class="start-wrapper">
  <div class="bg-overlay"></div>
  <div class="start-container">
    <div class="title-box">
      <h1>自販機シミュレーター</h1>
    </div>
    <div class="btn-group">
      <a href="${ctx}/bending/consumer">
        <button class="start-btn consumer-btn">買 う</button>
      </a> <a href="${ctx}/bending/admin">
        <button class="start-btn admin-btn">売 る</button>
      </a>
    </div>
  </div>
  <form action="${ctx}/bending/start" method="post" id="resetForm">
    <button class="reset-btn" type="button" onclick="confirmReset()">&#x21BB; RESET</button>
  </form>
</div>
<script src="${ctx}/js/start.js"></script>
<%@ include file="/footer.jsp"%>