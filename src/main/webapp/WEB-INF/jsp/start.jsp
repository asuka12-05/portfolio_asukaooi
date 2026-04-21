<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>自販機シミュレーター</title>
</head>
<body>
    <h1>自販機シミュレーター</h1>

    <%-- 消費者ボタン --%>
    <a href="${pageContext.request.contextPath}/bending/consumer">
        <button>消費者</button>
    </a>

    <%-- 業者ボタン --%>
    <a href="${pageContext.request.contextPath}/bending/admin">
        <button>業者</button>
    </a>

    <%-- 初期化ボタン --%>
    <form action="${pageContext.request.contextPath}/bending/start" method="post">
        <button type="submit">初期化</button>
    </form>
</body>
</html>