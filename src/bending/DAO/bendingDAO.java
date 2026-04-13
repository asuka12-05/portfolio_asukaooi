package bending.DAO;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import bending.DTO.DrinkDto;
import bending.DTO.DrinkTemperature;
import bending.DTO.SalesDto;
import bending.tool.Const;

/**
 * 自販機シミュレータの処理クラス
 */
public class bendingDAO {
	
	/**
	 * 商品リスト取得(全件検索)
	 * @param session
	 * @return
	 */
	public List<DrinkDto> findAll(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<DrinkDto> drinkList = (List<DrinkDto>) session.getAttribute(Const.ATTR_DRINK_LIST);
		
		// 初回開始時のみ初期商品を並べる
		if (drinkList == null) {
			drinkList = createDefaultDrinks();
			session.setAttribute(Const.ATTR_DRINK_LIST, drinkList);
		}
		return drinkList;
		
	}
	
	/**
	 * 初期商品(6種)一覧取得
	 * @return	初期商品一覧
	 */
	private List<DrinkDto> createDefaultDrinks() {
		// 空のリストを取得
		List<DrinkDto> defaultList = new ArrayList<>();
		
		// 初期商品1を設定
		DrinkDto d1 = new DrinkDto();
		// 初期商品のIDは設定済、以降オートインクリメント
		d1.setID(1);
		d1.setName(Const.DRINK_WATER);
		d1.setPrice(120);
		d1.setInventory(5);
		d1.setTemperature(DrinkTemperature.COLD);
		// リストに格納
		defaultList.add(d1);
		
		// 初期商品2を設定
		DrinkDto d2 = new DrinkDto();
		d2.setID(2);
		d2.setName(Const.DRINK_GREEN_TEA);
		d2.setPrice(120);
		d2.setInventory(5);
		d2.setTemperature(DrinkTemperature.COLD);
		// リストに格納
		defaultList.add(d2);
		
		// 初期商品3を設定
		DrinkDto d3 = new DrinkDto();
		d3.setID(3);
		d3.setName(Const.DRINK_SPORT);
		d3.setPrice(150);
		d3.setInventory(5);
		d3.setTemperature(DrinkTemperature.COLD);
		// リストに格納
		defaultList.add(d3);
		
		// 初期商品4を設定
		DrinkDto d4 = new DrinkDto();
		d4.setID(4);
		d4.setName(Const.DRINK_COFFEE_HOT);
		d4.setPrice(160);
		d4.setInventory(5);
		d4.setTemperature(DrinkTemperature.HOT);
		// リストに格納
		defaultList.add(d4);
		
		// 初期商品5を設定
		DrinkDto d5 = new DrinkDto();
		d5.setID(5);
		d5.setName(Const.DRINK_GREAP_SODA);
		d5.setPrice(160);
		d5.setInventory(5);
		d5.setTemperature(DrinkTemperature.COLD);
		// リストに格納
		defaultList.add(d5);
		
		// 初期商品6を設定
		DrinkDto d6 = new DrinkDto();
		d6.setID(6);
		d6.setName(Const.DRINK_COFFEE_ICE);
		d6.setPrice(160);
		d6.setInventory(5);
		d6.setTemperature(DrinkTemperature.COLD);
		// リストに格納
		defaultList.add(d6);
		
		// 初期商品を全て設定したリストを返却
		return defaultList;
	}
	
	/**
	 * 商品をIDで検索(購入、補充、入れ替え)
	 * @param session
	 * @param id
	 * @return
	 */
	public DrinkDto findById(HttpSession session, int id) {
		List<DrinkDto> drinkList = findAll(session);
		
		// 並んでいる商品の中に
		for(DrinkDto drink : drinkList) {
			// 処理したい商品があった場合
			if (drink.getID() == id) {
				// その商品を渡す(返却)
				return drink;
			}
		}
		// 対象商品はない
		return null;
	}
	
	/**
	 * 現在の商品リストをSessionに保存()
	 * @param session
	 * @param list
	 */
	public void saveAll(HttpSession session, List<DrinkDto> drinkList) {
		session.setAttribute(Const.ATTR_DRINK_LIST, drinkList);
	}
	
	/**
	 * 在庫を一つ減らす(ドリンクを購入する)
	 * @param session
	 * @param id
	 */
	public void decreaseInventory(HttpSession session, int id) throws IllegalArgumentException, IllegalStateException  {
		// 商品を取得
		DrinkDto drink = findById(session, id);

		if (drink == null) {
	        throw new IllegalArgumentException(Const.MSG_NOT_FOUND);
	    }

	    if (drink.getInventory() <= 0) {
	        throw new IllegalStateException(Const.MSG_SOLD_OUT);
	    }

	    drink.setInventory(drink.getInventory() - 1);
	    saveAll(session, findAll(session));
	}
	
	/**
	 * 在庫を補充する(継ぎ足す)
	 * @param session	取得済セッション
	 * @param id		商品ID
	 * @param count		補充本数
	 */
	public void replenish(HttpSession session, int id, int count) {
		// 商品を取得
	    DrinkDto drink = findById(session, id);
	    // nullチェック,念のため
	    if (drink == null) {
	    	throw new IllegalArgumentException(Const.MSG_NOT_FOUND);
	    }
	    // countのチェック,念のため
	    if (count <= 0) {
	        throw new IllegalArgumentException(Const.MSG_NO_STOCK_COUNT);
	    }
	    // 在庫にcountを足す
	    drink.setInventory(drink.getInventory() + count);
	    // Sessionに保存
	    saveAll(session, findAll(session));
	}
	
	/**
	 * 商品を入れ替える(売り切れ商品のみ)
	 * @param session	取得済セッション
	 * @param id		商品ID
	 * @param newDrink
	 */
	public void replace(HttpSession session, int id, DrinkDto newDrink) throws Exception {
		// 商品を取得
	    DrinkDto drink = findById(session, id);
	    // null確認
	    if (drink == null) {
	        throw new IllegalArgumentException(Const.MSG_NOT_FOUND);
	    }
	    // 在庫が0か確認
	    if (drink.getInventory() == 0) {
	    	// セッションから商品リストを取得
	    	List<DrinkDto> list = findAll(session);
	    	// 全商品確認
	    	for (int i = 0; i < list.size(); i++) {
	    		// 対象の売り切れ商品を検索
	    	    if (list.get(i).getID() == id) {
	    	    	// 入れ替える新商品で上書き
	    	        list.set(i, newDrink);
	    	        break;
	    	    }
	    	}
	    	// Sessionに保存
	    	saveAll(session, list);	
	    } else {
	    	 throw new IllegalStateException(Const.MSG_NOT_SOLDOUT);
	    }
	}
	
	/**
	 * カスタム商品リスト取得
	 * @param session	取得済セッション
	 * @return			カスタム商品リスト
	 */
	public List<DrinkDto> findCustomAll(HttpSession session) {
		// セッションから "customDrinkList" という名前で取得
		@SuppressWarnings("unchecked")
		List<DrinkDto> customDrinkList = (List<DrinkDto>) session.getAttribute(Const.ATTR_CUSTOM_DRINK_LIST);
		// nullか確認
		if (customDrinkList == null) {
			// 新規でカスタム商品のリストを作成
			List<DrinkDto> list = new ArrayList<>();
			// 空のリストをセッションに保存
			session.setAttribute(Const.ATTR_CUSTOM_DRINK_LIST, list);
			customDrinkList = list;
		}
		// リストを返却
		return customDrinkList;
		
	}
	
	/**
	 * カスタム商品を追加
	 * @param session	取得済セッション
	 * @param drink		新しく追加する商品
	 */
	public void addCustom(HttpSession session, DrinkDto drink) {
		// カスタム商品リストを取得
	    List<DrinkDto> list = findCustomAll(session);

	    // 現在の最大IDを探す。ない場合は0とする
	    int maxId = list.stream().mapToInt(DrinkDto::getID).max().orElse(0);
	    // +1した新しいIDを設定
	    drink.setID(maxId + 1);
	    // カスタム商品リストに加える
	    list.add(drink);
	    // セッションに保存
	    session.setAttribute(Const.ATTR_CUSTOM_DRINK_LIST, list);

	}
	
	// 売上関連
	/**
	 * 売上取得
	 * @param session	取得済セッション
	 * @return			売上
	 */
	public SalesDto getSales(HttpSession session) {
		// セッションから売上の取得
		SalesDto sales = (SalesDto) session.getAttribute(Const.ATTR_SALES);
		
		// 初回アクセス時
	    if (sales == null) {
	    	// SalesDtoを新規作成
	        sales = new SalesDto();
	        // セッションに保存
	        session.setAttribute(Const.ATTR_SALES, sales);
	    }
		return sales;
		
	}
	
	/**
	 * 売上に加算
	 * @param session	取得済みセッション
	 * @param price		今回購入したドリンクの金額
	 */
	public void addSales(HttpSession session, int price) {
		SalesDto sales = getSales(session);
		
		sales.setTotalSales(price + sales.getTotalSales());
		
		session.setAttribute(Const.ATTR_SALES, sales);
	}
	
	/**
	 * 売上をリセット(回収)
	 * @param session	取得済セッション
	 */
	public void resetSales(HttpSession session) {
		// セッションから現在の売上の取得
		SalesDto sales = getSales(session);
		
		// 売上を0にする
		sales.setTotalSales(0);
		// 売上本数も0にする
		sales.setTotalCount(0);
		
		// セッションに保存
		session.setAttribute(Const.ATTR_SALES, sales);
	}
	
	// 投入金額について
	/**
	 * 投入金額獲得
	 * @param session
	 * @return
	 */
	public int getInsertedMoney(HttpSession session) {
		Integer amount = (Integer) session.getAttribute(Const.ATTR_AMOUNT);
		
		// 初回アクセス時
	    if (amount == null) {
	    	amount = 0;
	        // セッションに保存
	        session.setAttribute(Const.ATTR_AMOUNT, amount);
	    }
		return amount;
		
	}
	
	/**
	 * 投入金額を加算
	 * @param session
	 * @param amount
	 */
	public void addMoney(HttpSession session, int amount) {
		
	}
	
	/**
	 * 投入金額リセット
	 * @param session
	 */
	public void resetMoney(HttpSession session) {
		
	}
	
	/**
	 * 全状態をデフォルトに戻す(初期化)
	 * @param session
	 */
	public void initialize(HttpSession session) {

	}
}
