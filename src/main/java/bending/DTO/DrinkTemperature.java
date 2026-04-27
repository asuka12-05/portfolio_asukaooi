package main.java.bending.DTO;

/**
 * ドリンクの温度状態
 */
public enum DrinkTemperature {
	HOT("あったかい"),
	COLD("つめたい");

	private final String label;

	private DrinkTemperature(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
