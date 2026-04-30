/**
 * consumer.js
 * 消費者画面のJavaScript
 */

// 現在の投入金額（hidden項目から取得）
let insertedMoney = parseInt(document.getElementById("insertedMoney").value) || 0;
// 現在選択中の商品ID
let selectedId    = parseInt(document.getElementById("selectedId").value)    || 0;

// ページ読み込み時にボタン状態を更新
window.onload = function () {
    updateBuyBtn();
};

/**
 * コイン投入
 * 合計1000円を超えようとした場合はエラーメッセージを表示
 * @param {number} amount - 投入金額
 */
function insertCoin(amount) {
    if (insertedMoney + amount > 1000) {
        showMsg("1000円を超えて投入できません", true);
        return;
    }

    // サーバーに投入金額を送信
    const form = document.createElement("form");
    form.method = "post";
    form.action = document.querySelector("input[name='action']")
                    ? "" : getContextPath() + "/bending/consumer";

    const actionInput = document.createElement("input");
    actionInput.type  = "hidden";
    actionInput.name  = "action";
    actionInput.value = "insertMoney";

    const amountInput = document.createElement("input");
    amountInput.type  = "hidden";
    amountInput.name  = "amount";
    amountInput.value = amount;

    form.appendChild(actionInput);
    form.appendChild(amountInput);
    document.body.appendChild(form);
    form.submit();
}

/**
 * 商品選択
 * @param {number} id          - 商品ID
 * @param {string} name        - 商品名
 * @param {number} price       - 価格
 * @param {number} inventory   - 在庫数
 * @param {string} temperature - 温度(HOT/COLD)
 */
function selectDrink(id, name, price, inventory, temperature) {
    selectedId = id;

    // hidden項目にdrinkIdをセット
    document.getElementById("buyDrinkId").value = id;

    // ステータスウインドウ更新
    document.getElementById("statusName").textContent      = name;
    document.getElementById("statusPrice").textContent     = price;
    document.getElementById("statusInventory").textContent = inventory;
    document.getElementById("statusTemp").textContent      = temperature === "HOT" ? "あったかい" : "つめたい";
    document.getElementById("statusImg").src =
        getContextPath() + "/images/" + id + ".png";

    // 選択状態のハイライト更新
    document.querySelectorAll(".drink-slot").forEach(function (slot) {
        slot.classList.remove("selected");
    });
    event.currentTarget.classList.add("selected");

    updateBuyBtn();
}

/**
 * 買うボタンの活性・非活性を更新
 * 商品選択済み かつ 投入金額 >= 商品価格 のときのみ活性
 */
function updateBuyBtn() {
    const btn   = document.getElementById("buyBtn");
    const price = parseInt(document.getElementById("statusPrice").textContent) || 0;

    btn.disabled = !(selectedId > 0 && insertedMoney >= price && price > 0);
}

/**
 * メッセージを表示
 * @param {string}  msg     - メッセージ
 * @param {boolean} isError - trueのとき赤字
 */
function showMsg(msg, isError) {
    const win = document.getElementById("msgWindow");
    win.innerHTML = isError
        ? '<span class="msg-error">'  + msg + '</span>'
        : '<span class="msg-normal">' + msg + '</span>';
}

/**
 * コンテキストパスを取得
 */
function getContextPath() {
    return window.location.pathname.split("/").slice(0, 2).join("/");
}
