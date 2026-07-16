package bending.DTO;

/**
 * ドリンク：エンティティ
 */
public class DrinkDto {
	/** ID */
	private int ID;
	/** ドリンクの名前 */
	private String name;
	/** 価格(円) */
	private int price;
	/** 在庫(本数) */
	private int inventory;
	/** ドリンクの温度状態 */
	private DrinkTemperature temperature;
	/** ドリンクの画像 */
	private String imageFile;
	
	public String getImagePath() {
	    if (imageFile != null && !imageFile.isEmpty()) {
	        return imageFile;
	    }
	    return ID + ".png";
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getInventory() {
		return inventory;
	}
	public void setInventory(int inventory) {
		this.inventory = inventory;
	}
	public DrinkTemperature getTemperature() {
		return temperature;
	}
	public void setTemperature(DrinkTemperature temperature) {
		this.temperature = temperature;
	}
	public String getImageFile() {
		return imageFile; 
	}
	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}
}
