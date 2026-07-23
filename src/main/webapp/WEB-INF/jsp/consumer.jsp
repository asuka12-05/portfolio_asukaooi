<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ include file="/header.jsp"%>
<link rel="stylesheet"
  href="${pageContext.request.contextPath}/css/consumer.css">

<%-- セッションから商品リストと各種状態を取得 --%>
<c:set var="drinkList" value="${sessionScope.drinkList}" />
<c:set var="insertedMoney"
  value="${sessionScope.insertedMoney != null ? sessionScope.insertedMoney : 0}" />
<c:set var="selectedId" value="${sessionScope.selectedId}" />
<c:set var="change" value="${sessionScope.change}" />
<c:set var="errorMsg" value="${sessionScope.errorMsg}" />
<c:set var="gotDrink" value="${sessionScope.gotDrink}" />

<%-- セッションの一時メッセージをクリア（表示後に削除） --%>
<%
session.removeAttribute("change");
session.removeAttribute("errorMsg");
session.removeAttribute("gotDrink");
%>

<%-- 投入金額をJavaScript用にhiddenで渡す --%>
<input type="hidden" id="insertedMoney" value="${insertedMoney}">
<input type="hidden" id="selectedId" value="${selectedId}">

<div class="page-wrapper">

  <!-- ===== 左：自販機本体 ===== -->
  <div class="machine-area">

    <!-- 商品ボタン群 -->
    <div class="drink-grid" id="drinkGrid">
      <c:forEach var="drink" items="${drinkList}">
        <c:choose>
          <%-- 在庫切れ --%>
          <c:when test="${drink.inventory == 0}">
            <div class="drink-slot sold-out">
              <div class="sold-out-band">SOLD OUT</div>
              <img src="${ctx}/images/${drink.imagePath}" alt="${drink.name}" class="drink-img">
              <span class="drink-name">${drink.name}</span>
              <span class="drink-price">¥${drink.price}</span>
            </div>
          </c:when>
          <%-- 購入可能 --%>
          <c:otherwise>
            <div class="drink-slot ${drink.temperature == 'HOT' ? 'hot' : 'cold'} ${selectedId == drink.ID ? 'selected' : ''}"
              data-price=${drink.price} onclick="selectDrink(${drink.ID}, '${drink.name}', ${drink.price}, ${drink.inventory},
               '${drink.temperature}', '${drink.imagePath}')">
              <img src="${ctx}/images/${drink.imagePath}" alt="${drink.name}" class="drink-img">
              <span class="drink-name">${drink.name}</span>
              <span class="drink-price">¥${drink.price}</span>
            </div>
          </c:otherwise>
        </c:choose>
      </c:forEach>
    </div>

    <%-- コイン投入用hiddenフォーム --%>
    <form action="${ctx}/bending/consumer" method="post" id="coinForm">
      <input type="hidden" name="action" value="insertMoney"> <input
        type="hidden" name="amount" id="coinAmount" value="0">
    </form>
    <!-- コインボタン -->
    <div class="coin-area">
      <c:forEach var="coin" items="${[10,50,100,500,1000]}">
        <button class="coin-btn" onclick="insertCoin(${coin})">${coin}円</button>
      </c:forEach>
    </div>

    <!-- 購入・おつりボタン + 取り出し口 -->
    <div class="action-area">
      <!-- 取り出し口 -->
      <div class="tray" id="tray">
        <c:if test="${not empty gotDrink}">
          <div class="got-drink">
            <img src="${ctx}/images/${gotDrink.imagePath}" alt="${gotDrink.name}" class="tray-img">
          </div>
        </c:if>
      </div>

      <div class="buy-return-area">
        <!-- 買うボタン -->
        <form action="${ctx}/bending/consumer" method="post" id="buyForm">
          <input type="hidden" name="action" value="buy">
          <input type="hidden" name="drinkId" id="buyDrinkId" value="">
          <button type="submit" class="action-btn buy-btn" id="buyBtn" disabled>買 う</button>
        </form>
        <!-- おつりボタン -->
        <form action="${ctx}/bending/consumer" method="post">
          <input type="hidden" name="action" value="return">
          <button type="submit" class="action-btn return-btn" id="returnBtn">おつり</button>
        </form>
      </div>
    </div>
  </div>

  <!-- ===== 右：ステータス・メッセージ・戻る ===== -->
  <div class="info-area">

    <!-- ステータスウインドウ -->
    <div class="status-window" id="statusWindow">
      <div class="status-item" id="statusDrinkImg">
        <img src="${ctx}/images/hatena.png" alt="hatena" id="statusImg"
          class="status-drink-img">
      </div>
      <div class="status-detail">
        <div class="status-row" id="statusName">？</div>
        <div class="status-row">
          <span id="statusInventory">－</span>本
        </div>
        <div class="status-row">
          <span id="statusPrice">－</span>円
        </div>
        <div class="status-row" id="statusTemp">－</div>
        <div class="status-row inserted-row">
          投入金額：<span id="statusInserted">${insertedMoney}</span>円
        </div>
        <div class="hint-row" id="hintWindow"></div>
      </div>
    </div>

    <!-- メッセージウインドウ -->
    <div class="msg-window" id="msgWindow">
      <c:choose>
        <c:when test="${not empty errorMsg}">
          <span class="msg-error"><c:out value="${errorMsg}"/></span>
        </c:when>
        <c:when test="${not empty change}">
          <span class="msg-normal">お釣り：${change}円</span>
        </c:when>
        <c:otherwise>
          <span class="msg-placeholder">メッセージ</span>
        </c:otherwise>
      </c:choose>
    </div>
    

    <!-- 戻るボタン -->
    <div class="back-area">
      <a href="${ctx}/bending/start" onclick="return confirmBack()">
        <button class="back-btn">&#x2190; もどる</button>
      </a>
    </div>
  </div>
</div>

<script>
  const CONTEXT_PATH = "${ctx}";
</script>
<script src="${ctx}/js/consumer.js"></script>
<%@ include file="/footer.jsp"%>
