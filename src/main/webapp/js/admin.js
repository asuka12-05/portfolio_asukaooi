/**
 * admin.js
 * 業者画面のJavaScript
 */

// 現在選択中のスロット情報
let currentSlot = {
    id:        0,
    name:      "",
    price:     0,
    inventory: 0,
    temp:      "",
    isSoldOut: false
};

// カスタム画像スライダー
let imageIndex = 0;

/**
 * 商品スロット選択
 * @param {number}  id			商品ID
 * @param {string}  name		商品名
 * @param {number}  price		価格
 * @param {number}  inventory	在庫数
 * @param {string}  temp		温度
 * @param {boolean} isSoldOut	売り切れか
 */
function selectSlot(id, name, price, inventory, temp, isSoldOut, imagePath) {
    currentSlot = { id, name, price, inventory, temp, isSoldOut, imagePath };

    // スロットハイライト
    document.querySelectorAll(".drink-slot").forEach(function (s) {
        s.classList.remove("selected");
    });
    event.currentTarget.classList.add("selected");

    // 詳細ウインドウ更新
    showDetailView(id, name, price, inventory, temp, imagePath);

    // 補充ボタン活性（在庫が最大(5本)でなければ活性）
    document.getElementById("jsi-replenishBtn").disabled = (inventory >= 5);

    // カスタムボタン活性（売り切れのみ）
    document.getElementById("jsi-customBtn").disabled = !isSoldOut;

    // 補充プルダウンを閉じる
    document.getElementById("jsi-replenishDropdown").style.display = "none";
    document.getElementById("customView").style.display = "none";
    document.getElementById("detailView").style.display = "flex";
}

/**
 * 詳細ビューに商品情報を表示
 */
function showDetailView(id, name, price, inventory, temp, imagePath) {
    document.getElementById("detailImg").src          = CONTEXT_PATH + "/images/" + imagePath;
    document.getElementById("detailName").textContent  = name;
    document.getElementById("detailStock").textContent = inventory;
    document.getElementById("detailPrice").textContent = price;
    document.getElementById("detailTemp").textContent  = temp === "HOT" ? "あったかい" : "つめたい";
}

/**
 * 補充プルダウンを開閉する
 */
function toggleReplenish() {
    const dropdown = document.getElementById("jsi-replenishDropdown");
    const isHidden = dropdown.style.display === "none";

    dropdown.style.display = isHidden ? "block" : "none";

    if (isHidden) {
        document.getElementById("jsi-replenishDrinkId").value = currentSlot.id;
        // カスタムビューは閉じる
        document.getElementById("customView").style.display = "none";
        document.getElementById("detailView").style.display = "flex";
    }
}

/**
 * カスタム画面を開く
 */
function openCustom() {
    // カスタム対象のIDをセット
    document.getElementById("customTargetId").value = currentSlot.id;

    // 補充プルダウンを閉じる
    document.getElementById("jsi-replenishDropdown").style.display = "none";

    // カスタムビューを表示
    document.getElementById("detailView").style.display = "none";
    document.getElementById("customView").style.display = "block";

    // 画像を初期化
    imageIndex = 0;
    updateCustomImage();
}

/**
 * 画像スライダー：次へ
 */
function nextImage() {
    imageIndex = (imageIndex + 1) % IMAGE_LIST.length;
    updateCustomImage();
}

/**
 * 画像スライダー：前へ
 */
function prevImage() {
    imageIndex = (imageIndex - 1 + IMAGE_LIST.length) % IMAGE_LIST.length;
    updateCustomImage();
}

/**
 * カスタム画像を更新
 */
function updateCustomImage() {
    const imgFile = IMAGE_LIST[imageIndex];
    document.getElementById("customImg").src          = CONTEXT_PATH + "/images/" + imgFile;
    document.getElementById("customImageFile").value  = imgFile;
}

/**
 * HOT/COLD切り替え
 */
function toggleTemp() {
    const btn   = document.getElementById("tempToggleBtn");
    const input = document.getElementById("customTemp");

    if (input.value === "HOT") {
        input.value = "COLD";
        btn.textContent = "COLD";
        btn.classList.add("cold");
    } else {
        input.value = "HOT";
        btn.textContent = "HOT";
        btn.classList.remove("cold");
    }
}

/**
 * カスタム決定：confirm後にフォーム送信
 */
function confirmCustom() {
    const name = document.getElementById("customName").value.trim();
    if (!name) {
        showMsg("商品名を入力してください", true);
        return;
    }

    if (confirm("この内容でカスタム商品を登録しますか？")) {
        document.getElementById("customForm").submit();
    }
}

/**
 * 売上回収：confirm後にフォーム送信、ボタンを回収完了に変える
 */
function confirmCollect() {
    if (confirm("売上を回収しますか？")) {
        const btn = document.getElementById("collectBtn");
        btn.textContent = "回収完了";
        btn.disabled    = true;
        document.getElementById("collectForm").submit();
    }
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
