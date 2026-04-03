package bending.DAO;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import bending.DTO.DrinkDto;
import bending.DTO.DrinkTemperature;
import bending.DTO.SalesDto;

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
		List<DrinkDto> drinkList = (List<DrinkDto>) session.getAttribute("drinkList");
		
		// 初回開始時のみ初期商品を並べる
		if (drinkList == null) {
			drinkList = createDefaultDrinks();
			session.setAttribute("drinkList", drinkList);
		}
		return drinkList;
		
	}
	
	/**
	 * 初期商品一覧取得
	 * @return	初期商品一覧
	 */
	private List<DrinkDto> createDefaultDrinks() {
		// 空のリストを取得
		List<DrinkDto> defaultList = new ArrayList<>();
		
		// 初期商品1を設定
		DrinkDto d1 = new DrinkDto();
		// 初期商品のIDは設定済、以降オートインクリメント
		d1.setID(1);
		d1.setName("みず");
		d1.setPrice(120);
		d1.setInventory(5);
		d1.setTemperature(DrinkTemperature.COLD);
		// リストに格納
		defaultList.add(d1);
		
		// 初期商品2を設定
		DrinkDto d2 = new DrinkDto();
		d2.setID(2);
		d2.setName("緑茶");
		d2.setPrice(120);
		d2.setInventory(5);
		d2.setTemperature(DrinkTemperature.COLD);
		// リストに格納
		defaultList.add(d2);
		
		// 初期商品3を設定
		DrinkDto d3 = new DrinkDto();
		d3.setID(3);
		d3.setName("スポーツドリンク");
		d3.setPrice(150);
		d3.setInventory(5);
		d3.setTemperature(DrinkTemperature.COLD);
		// リストに格納
		defaultList.add(d3);
		
		// 初期商品4を設定
		DrinkDto d4 = new DrinkDto();
		d4.setID(4);
		d4.setName("コーヒー");
		d4.setPrice(160);
		d4.setInventory(5);
		d4.setTemperature(DrinkTemperature.HOT);
		// リストに格納
		defaultList.add(d4);
		
		// 初期商品5を設定
		DrinkDto d5 = new DrinkDto();
		d5.setID(5);
		d5.setName("グレープソーダ");
		d5.setPrice(160);
		d5.setInventory(5);
		d5.setTemperature(DrinkTemperature.COLD);
		// リストに格納
		defaultList.add(d5);
		
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
		session.setAttribute("drinkList", drinkList);
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
	        throw new IllegalArgumentException("存在しません");
	    }

	    if (drink.getInventory() <= 0) {
	        throw new IllegalStateException("売り切れ");
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
	    	throw new IllegalArgumentException("存在しません");
	    }
	    // countのチェック,念のため
	    if (count <= 0) {
	        throw new IllegalArgumentException("補充本数は1以上を指定してください");
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
	public void replace(HttpSession session, int id, DrinkDto newDrink) {
		// 商品を取得
	    DrinkDto drink = findById(session, id);
	    if (drink.getInventory() == 0) {
	    // 在庫にcountを足す
	    drink.setInventory(drink.getInventory() + count);
	    // Sessionに保存
	    saveAll(session, findAll(session));	
	    }
	}
	
	/**
	 * カスタム商品リスト取得
	 * @param session
	 * @param drink
	 * @return
	 */
	public List<DrinkDto> findCustomAll(HttpSession session, DrinkDto drink) {
		return null;
		
	}
	
	/**
	 * カスタム商品を追加
	 * @param session
	 * @param drink
	 */
	public void addCustom(HttpSession session, DrinkDto drink) {
		
	}
	
	// 売上関連
	/**
	 * 売上取得
	 * @param session
	 * @return
	 */
	public SalesDto getSales(HttpSession session) {
		return null;
		
	}
	
	/**
	 * 売上に加算
	 * @param session
	 * @param price
	 */
	public void addSales(HttpSession session, int price) {
		
	}
	
	/**
	 * 売上をリセット(回収)
	 * @param session
	 */
	public void resetSales(HttpSession session) {
		
	}
	
	// 投入金額について
	/**
	 * 投入金額獲得
	 * @param session
	 * @param totalSales
	 * @return
	 */
	public int getInsertedMoney(HttpSession session, int totalSales) {
		return totalSales;
		
	}
	
	public void addMoney() {
		
	}
}
