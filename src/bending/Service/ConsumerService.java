package bending.Service;

import jakarta.servlet.http.HttpSession;

public class ConsumerService {
	/**
	 * コイン投入
	 */
	public void insertMoney(HttpSession session, int amount) {
		addMoney();
	}
	/**
	 * 購入・おつり計算
	 * @return
	 */
	public int purchase(HttpSession session, int drinkId) {
		findById();      // 商品取得
		getInsertedMoney(); // 投入金額確認
		decreaseInventory(); // 在庫を減らす
		addSales();        // 売上に加算
		resetMoney();      // 投入金額リセット
	}
	
	/**
	 * おつり返却
	 */
	public int returnChange(HttpSession session) {
		getInsertedMoney(); // 返金額確認
		resetMoney();     // 投入金額リセット
	}
	
	/**
	 * 全商品売切れ判定
	 * @param session
	 * @return
	 */
	public boolean isAllSoldOut(HttpSession session) {
		 findAll();        // 全商品取得して在庫確認
	}
}
