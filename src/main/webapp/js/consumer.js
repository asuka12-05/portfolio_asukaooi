/**
 * 消費者画面のJavaScript
 */

// ページ読み込み時にボタン状態を更新
window.onload = function () {
	updateDrinkButtons();
};

function updateDrinkButtons() {
    const inserted = parseInt(document.getElementById("insertedMoney").value) || 0;
    document.querySelectorAll(".drink-slot:not(.sold-out)").forEach(function (slot) {
        const price = parseInt(slot.getAttribute("data-price"));
        if (inserted >= price) {
            slot.classList.remove("cannot-buy");
            slot.style.pointerEvents = "";
            slot.style.opacity = "";
        } else {
            slot.classList.add("cannot-buy");
            slot.style.pointerEvents = "none";
            slot.style.opacity = "0.5";
        }
    });
	updateHint();
}

/**
 * 現在の投入金額から、次の目標（次に買えるもの）を計算して表示する
 */
function updateHint() {
    const inserted = parseInt(document.getElementById("insertedMoney").value) || 0;
    const hintWindow = document.getElementById("hintWindow");
    
    // 全商品の価格と名前を取得
    const drinks = [];
    document.querySelectorAll(".drink-slot").forEach(slot => {
        const price = parseInt(slot.getAttribute("data-price"));
        const name = slot.querySelector(".drink-name").textContent;
		const isSoldOut = slot.classList.contains("sold-out");
        if (!isNaN(price) && !isSoldOut) {
            drinks.push({ name: name, price: price });
        }
    });

    // 投入金額より高い商品だけを抽出
    const affordable = drinks.filter(d => d.price > inserted);

	if (drinks.length === 0) {
		hintWindow.textContent = "売り切れです";
	} else if (affordable.length === 0) {
		hintWindow.textContent = "全商品購入可能です！";
	} else {
		affordable.sort((a, b) => a.price - b.price);
		const nextTarget = affordable[0];
		const diff = nextTarget.price - inserted;
		// strongタグで数字を強調
		hintWindow.innerHTML = `あと<strong>${diff}円</strong>で<br>${nextTarget.name}が買えます`;
	}
}

/**
 * コイン投入
 * 合計1000円を超えようとした場合はエラーメッセージを表示
 * @param {number} amount - 投入金額
 */
function insertCoin(amount) {
	document.getElementById("coinAmount").value = amount;
	document.getElementById("coinForm").submit();
}

/**
 * 商品選択
 * @param {number} id			商品ID
 * @param {string} name			商品名
 * @param {number} price		価格
 * @param {number} inventory	在庫数
 * @param {string} temperature	温度(HOT/COLD)
 */
function selectDrink(id, name, price, inventory, temperature, imagePath) {
    selectedId = id;

    // hidden項目にdrinkIdをセット
    document.getElementById("buyDrinkId").value = id;

    // ステータスウインドウ更新
    document.getElementById("statusName").textContent      = name;
    document.getElementById("statusPrice").textContent     = price;
    document.getElementById("statusInventory").textContent = inventory;
    document.getElementById("statusTemp").textContent      = temperature === "HOT" ? "あったかい" : "つめたい";
	document.getElementById("statusImg").src = CONTEXT_PATH + "/images/" + imagePath;
   
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
	// ここで毎回最新の投入金額を取得する
	const currentInserted = parseInt(document.getElementById("insertedMoney").value) || 0;
	const price = parseInt(document.getElementById("statusPrice").textContent) || 0;

	// selectedIdも毎回取得
	const selectedId = parseInt(document.getElementById("buyDrinkId").value) || 0;

    btn.disabled = !(selectedId > 0 && currentInserted >= price && price > 0);
}

/**
 * メッセージを表示
 * @param {string} msg			メッセージ
 * @param {boolean} isError		trueのとき赤字
 */
function showMsg(msg, isError) {
    const win = document.getElementById("msgWindow");
    win.innerHTML = isError
        ? '<span class="msg-error">'  + msg + '</span>'
        : '<span class="msg-normal">' + msg + '</span>';
}
