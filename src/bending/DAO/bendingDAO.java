package bending.DAO;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import bending.DTO.DrinkDto;
import bending.DTO.SalesDto;

public class bendingDAO {
	
	/**
	 * 商品リスト取得
	 * @param session
	 * @return
	 */
	public List<DrinkDto> findAll(HttpSession session) {
		return null;
		
	}
	
	/**
	 * 商品一件獲得
	 * @param session
	 * @param id
	 * @return
	 */
	public DrinkDto findById(HttpSession session, int id) {
		return null;
	}
	
	/**
	 * 商品リストをSessionに保存
	 * @param session
	 * @param list
	 */
	public void saveAll(HttpSession session, List<DrinkDto> list) {
		
	}
	
	/**
	 * 在庫を一つ減らす
	 * @param session
	 * @param id
	 */
	public void decreaseInvebentory(HttpSession session, int id) {
		
	}
	
	/**
	 * 在庫を補充する
	 * @param session
	 * @param id
	 * @param count
	 */
	public void replenish(HttpSession session, int id, int count) {
		
	}
	
	/**
	 * 商品を入れ替える(売り切れ商品のみ)
	 * @param session
	 * @param id
	 * @param newDrink
	 */
	public void replace(HttpSession session, int id, DrinkDto newDrink) {
		
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
}
