package bending.DAO;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import bending.DTO.DrinkDto;
import bending.DTO.DrinkTemperature;
import bending.DTO.SalesDto;

public class bendingDAO {
	
	/**
	 * 商品リスト取得
	 * @param session
	 * @return
	 */
	public List<DrinkDto> findAll(HttpSession session) {
		List<DrinkDto> drinkList = (List<DrinkDto>) session.getAttribute("drinkList");
		
		if (drinkList == null) {
			drinkList = createDefaultDrinks();
			session.setAttribute("drinkList", drinkList);
		}
		return drinkList;
		
	}
	
	/**
	 * 初期商品リスト取得
	 * @return
	 */
	private List<DrinkDto> createDefaultDrinks() {
		List<DrinkDto> defaultList = new ArrayList<>();
		
		DrinkDto d1 = new DrinkDto();
		d1.setID(1);
		d1.setName("みず");
		d1.setPrice(120);
		d1.setInventory(5);
		d1.setTemperature(DrinkTemperature.COLD);
		defaultList.add(d1);
		
		return defaultList;
	}
	
	/**
	 * 商品一件獲得
	 * @param session
	 * @param id
	 * @return
	 */
	public DrinkDto findById(HttpSession session, int id) {
		List<DrinkDto> drinkList = findAll(session);
		
		for(DrinkDto drink : drinkList) {
			if (drink.getID() == id) {
				return drink;
			}
		}
		return null;
	}
	
	/**
	 * 商品リストをSessionに保存
	 * @param session
	 * @param list
	 */
	public void saveAll(HttpSession session, List<DrinkDto> drinkList) {
		session.setAttribute("drinkList", drinkList);
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
