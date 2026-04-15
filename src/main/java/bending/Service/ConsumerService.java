package bending.Service;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import bending.DAO.BendingDAO;
import bending.DTO.DrinkDto;
import bending.tool.Const;

public class ConsumerService {
	private BendingDAO dao = new BendingDAO();
	
	/**
	 * 購入・おつり計算
	 * @return
	 */
	public int purchase(HttpSession session, int drinkId) {
		// 商品取得
		DrinkDto drink = dao.findById(session, drinkId);
		// 対象商品がない場合
		if (drink == null) {
			// エラーメッセージが表示される
			throw new IllegalArgumentException(Const.MSG_NOT_FOUND);
		}
		// 投入金額確認
		int insertedMoney = dao.getInsertedMoney(session);
		// 在庫を減らす
		dao.decreaseInventory(session, drinkId);
		// 売上に加算
		dao.addSales(session, drink.getPrice());
		// 投入金額からドリンクの値段を引いておつりを計算する
		int change = insertedMoney - drink.getPrice();
		// 投入金額リセット
		dao.resetMoney(session);
		
		// おつりを返却
		return change;
	}
	
	/**
	 * 投入金額返却
	 * お金を投入したが何も買わずそのまま返金されるとき
	 * @param session
	 * @return
	 */
	public int returnChange(HttpSession session) {
		// 返金額確認
		int insertedMoney = dao.getInsertedMoney(session);
		// お金が投入されていない時
		if (insertedMoney == 0) {
			// エラーメッセージを表示
			throw new IllegalStateException(Const.MSG_NO_CHANGE);
		}
		// 投入金額リセット
		dao.resetMoney(session);
		// 投入金額を返却する
		return insertedMoney;
	}
	
	/**
	 * 全商品売切れ判定
	 * @param session	取得済セッション
	 * @return			在庫があるか(真：全商品在庫0、偽：在庫のある商品あり)
	 */
	public boolean isAllSoldOut(HttpSession session) {
		// 全商品取得
		List<DrinkDto> list = dao.findAll(session);
		// 商品一つ一つ在庫確認
		for (DrinkDto drink : list) {
			// 在庫が一本以上ある商品があるとき
			if (drink.getInventory() > 0) {
				// false返却
				return false;
			}
		}
		// 全商品在庫0だったのでtrue返却
		return true;
	}
}
