<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ include file="/header.jsp"%>
<link rel="stylesheet" href="${ctx}/css/admin.css">

<c:set var="drinkList" value="${sessionScope.drinkList}" />
<c:set var="customDrinkList" value="${sessionScope.customDrinkList}" />
<c:set var="sales" value="${requestScope.sales}" />
<c:set var="errorMsg" value="${sessionScope.errorMsg}" />
<c:set var="successMsg" value="${sessionScope.successMsg}" />
<c:set var="collectedSales" value="${sessionScope.collectedSales}" />

<%
session.removeAttribute("errorMsg");
session.removeAttribute("successMsg");
session.removeAttribute("collectedSales");
%>

<%-- 画像リスト（10種類）をJavaScript用にJSON形式で渡す --%>
<script>
    const IMAGE_LIST = [
        "drink1.png","drink2.png","drink3.png","drink4.png","drink5.png",
        "drink6.png","drink7.png","drink8.png","drink9.png","drink10.png"
    ];
    const CONTEXT_PATH = "${ctx}";
</script>

<div class="page-wrapper">

  <!-- ===== 左：自販機本体 ===== -->
  <div class="machine-area">

    <!-- 商品ボタン群 -->
    <div class="drink-btn" id="jsi-drink-btn">
      <c:forEach var="drink" items="${drinkList}">
        <c:choose>
          <!-- 商品売り切れ時 -->
          <c:when test="${drink.inventory == 0}">
            <div class="drink-slot no-stock"
              onclick="selectSlot(${drink.ID}, '${drink.name}', ${drink.price}, ${drink.inventory}, '${drink.temperature}', true, '${drink.imagePath}')">
              <img src="${ctx}/images/${drink.imagePath}" alt="${drink.name}" class="drink-img">
               <span class="drink-name">${drink.name}</span>
               <span class="drink-stock">0本</span>
            </div>
          </c:when>
          <!-- 商品在庫あり -->
          <c:otherwise>
            <div
              class="drink-slot ${drink.temperature == 'HOT' ? 'hot' : 'cold'}"
              onclick="selectSlot(${drink.ID}, '${drink.name}', ${drink.price}, ${drink.inventory}, '${drink.temperature}', false, '${drink.imagePath}')">
              <img src="${ctx}/images/${drink.imagePath}" alt="${drink.name}" class="drink-img">
               <span class="drink-name">${drink.name}</span>
               <span class="drink-stock">${drink.inventory}本</span>
            </div>
          </c:otherwise>
        </c:choose>
      </c:forEach>
    </div>

    <!-- 補充ボタン・カスタムボタン -->
    <div class="option-btns">
      <div class="replenish-group">
        <button class="op-btn replenish-btn" id="jsi-replenishBtn" onclick="toggleReplenish()" disabled>補 充</button>
        <!-- 補充プルダウン -->
        <div class="replenish-dropdown" id="jsi-replenishDropdown" style="display: none">
          <form action="${ctx}/bending/admin" method="post" id="jsi-replenishForm">
            <input type="hidden" name="action" value="replenish">
            <input type="hidden" name="drinkId" id="jsi-replenishDrinkId" value="">
            <select name="count" class="count-select" id="jsi-replenishCount">
              <option value="default" selected>本数を選択</option>
              <option value="1">1本</option>
              <option value="2">2本</option>
              <option value="3">3本</option>
              <option value="4">4本</option>
              <option value="5">5本</option>
            </select>
            <button type="submit" class="decide-btn">決定</button>
          </form>
        </div>
      </div>
      <button class="op-btn custom-btn" id="jsi-customBtn" onclick="openCustom()" disabled>カスタム</button>
    </div>

    <!-- 売上エリア -->
    <div class="sales-area">
      <div class="sales-total">
        売上合計： <span id="salesTotalDisplay">
          <c:choose>
            <c:when test="${not empty sales}">${sales.totalSales}円</c:when>
            <c:otherwise>0円</c:otherwise>
          </c:choose>
        </span>
      </div>
      <form action="${ctx}/bending/admin" method="post" id="collectForm">
        <input type="hidden" name="action" value="collect">
        <button type="button" class="collect-btn" id="collectBtn"
          onclick="confirmCollect()"
          <c:if test="${empty sales or sales.totalSales == 0}">disabled</c:if>>
          売上回収</button>
      </form>
    </div>

    <!-- 予備スペース（必要に応じて追加） -->
    <div class="machine-bottom"></div>
  </div>

  <!-- 右：詳細・カスタム・メッセージ・戻る -->
  <div class="info-area">

    <!-- 詳細・カスタムウインドウ -->
    <div class="detail-window" id="detailWindow">

      <!-- 補充時：商品詳細表示 -->
      <div id="detailView">
        <div class="detail-img-area">
          <img src="${ctx}/images/1.png"
            alt="${drink.name}" id="detailImg" class="drink-img">
        </div>
        <div class="detail-info">
          <div class="detail-row" id="detailName">商品を選んでください</div>
          <div class="detail-row">
            <span id="detailStock">－</span>本
          </div>
          <div class="detail-row">
            <span id="detailPrice">－</span>円
          </div>
          <div class="detail-row" id="detailTemp">－</div>
        </div>
      </div>

      <!-- カスタム時：カスタム入力画面 -->
      <div id="customView" style="display: none">
        <!-- 画像スライダー -->
        <div class="img-slider">
          <button type="button" class="slide-btn" onclick="prevImage()">&#9664;</button>
          <img
            src="${ctx}/images/drink1.png"
            alt="ドリンク画像" id="customImg" class="custom-drink-img">
          <button type="button" class="slide-btn" onclick="nextImage()">&#9654;</button>
        </div>

        <!-- カスタム入力フォーム -->
        <form action="${ctx}/bending/admin" method="post" id="customForm">
          <input type="hidden" name="action" value="addCustom">
          <input type="hidden" name="drinkId" id="customTargetId" value="">
          <input type="hidden" name="imageFile" id="customImageFile" value="drink1.png">
          <div class="custom-inputs">
            <div class="custom-row">
              <label>NAME</label>
              <input type="text" name="name" id="customName" class="custom-input" placeholder="商品名を入力">
              <select name="count" class="custom-select">
                <option value="">本数</option>
                <option value="1">1本</option>
                <option value="2">2本</option>
                <option value="3">3本</option>
                <option value="4">4本</option>
                <option value="5">5本</option>
              </select>
            </div>
            <div class="custom-row">
              <label>PRICE</label>
              <select name="price" class="custom-select">
                <c:forEach begin="100" end="200" step="10" var="p">
                  <option value="${p}">${p}円</option>
                </c:forEach>
              </select>
              <button type="button" class="temp-toggle-btn" id="tempToggleBtn" onclick="toggleTemp()">HOT</button>
              <input type="hidden" name="temperature" id="customTemp" value="HOT">
            </div>
          </div>

          <!-- 決定ボタン -->
          <button type="button" class="decide-btn custom-decide-btn" onclick="confirmCustom()">決 定</button>
        </form>
      </div>
    </div>

    <!-- メッセージウインドウ -->
    <div class="msg-window" id="msgWindow">
      <c:choose>
        <!-- エラーメッセージがある場合は表示 -->
        <c:when test="${not empty errorMsg}">
          <span class="msg-error">${errorMsg}</span>
        </c:when>
        <!--  -->
        <c:when test="${not empty successMsg}">
          <span class="msg-normal">${successMsg}</span>
        </c:when>
        <c:when test="${not empty collectedSales}">
          <span class="msg-normal">${collectedSales.totalSales}円を回収しました</span>
        </c:when>
        <c:otherwise>
          <span class="msg-placeholder">メッセージ</span>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- 戻るボタン -->
    <div class="back-area">
      <a href="${ctx}/bending/start">
        <button class="back-btn">&#x2190; もどる</button>
      </a>
    </div>
  </div>
</div>

<script src="${ctx}/js/admin.js"></script>
<%@ include file="/footer.jsp"%>
