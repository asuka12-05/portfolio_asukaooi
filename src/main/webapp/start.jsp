<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="start-wrapper">
    <div class="bg-overlay"></div>

    <div class="start-container">
        <div class="title-box">
            <h1>自販機シミュレーター</h1>
        </div>

        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/bending/consumer">
                <button class="start-btn consumer-btn">買　う</button>
            </a>
            <a href="${pageContext.request.contextPath}/bending/admin">
                <button class="start-btn admin-btn">売　る</button>
            </a>
        </div>
    </div>

    <form action="${pageContext.request.contextPath}/bending/start" method="post">
        <button class="reset-btn" type="submit">&#x21BB; RESET</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
