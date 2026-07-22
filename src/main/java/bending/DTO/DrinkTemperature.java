package bending.DTO;

/**
 * ドリンクの温度状態DTO
 */
public enum DrinkTemperature {

	HOT("あったかい"),
	COLD("つめたい");

	/** 温度状態 */
	private final String label;

	/**
	 * 引数ありコンストラクタ
	 * @param label
	 */
	private DrinkTemperature(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
