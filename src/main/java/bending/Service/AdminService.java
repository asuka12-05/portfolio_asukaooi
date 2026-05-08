package bending.Service;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import bending.DAO.BendingDAO;
import bending.DTO.DrinkDto;
import bending.DTO.SalesDto;
import bending.tool.CheckTool;
import bending.tool.Const;

public class AdminService {
	private BendingDAO dao = new BendingDAO();
	
	/**
	 * 在庫補充
	 * @param session	取得済セッション
	 * @param drinkId	商品ID
	 * @param count		補充本数
	 */
	public void replenish(HttpSession session, int drinkId, int count) {
		dao.replenish(session, drinkId, count);
	}

	/**
	 * 商品入れ替え
	 * @param session	取得済セッション
	 * @param drinkId	商品ID
	 * @param newDrink	入れ替え先商品
	 * @throws Exception
	 */
	public void replace(HttpSession session, int drinkId, DrinkDto newDrink) throws Exception {
		dao.replace(session, drinkId, newDrink);
	}

	/**
	 * カスタム商品作成
	 * @param session	取得済セッション
	 * @param drink		作成したカスタム商品
	 */
	public void addCustomDrink(HttpSession session, DrinkDto drink) {
		// 件数チェック用
		List<DrinkDto> list = dao.findCustomAll(session);
		// カスタム商品が上限の5件のとき
		if (list.size() == 5) {
			// エラーメッセージを表示
			throw new IllegalStateException(Const.MSG_CUSTOM_LIMIT);
		}
		// 商品名が未入力のとき
		if (!CheckTool.compulsoryCheck(drink.getName())) {
			// エラーメッセージを表示
			throw new IllegalArgumentException(Const.MSG_NO_NAME);
		}
		// カスタム商品を追加
		dao.addCustom(session, drink);
	}
	
	/**
	 * 売上回収
	 * @param session	取得済セッション
	 * @return			売上金額
	 */
	public SalesDto collectSales(HttpSession session) {
		// 回収金額取得
		SalesDto sales = dao.getSales(session);
		if (sales.getTotalSales() == 0) {
			throw new IllegalStateException(Const.MSG_ZERO_SALES);
		}
		// リセット前に金額を退避
	    int collectedAmount = sales.getTotalSales();
		// 売上リセット
		dao.resetSales(session);
		// 新しいDTOに回収金額を入れて返す
	    SalesDto result = new SalesDto();
	    result.setTotalSales(collectedAmount);
		// 売上を返却
		return result;
	}
	
	/**
	 * 全状態初期化
	 * @param session
	 */
	public void initialize(HttpSession session) {
		dao.initialize(session);
	}
}
