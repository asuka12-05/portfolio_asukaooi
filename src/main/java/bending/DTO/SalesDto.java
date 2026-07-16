package bending.DTO;

/**
 * 売り上げDTO
 */
public class SalesDto {
	/** 売上合計 */
	private int totalSales;
	/** 販売本数 */
	private int totalCount;
	
	public int getTotalSales() {
		return totalSales;
	}
	public void setTotalSales(int totalSales) {
		this.totalSales = totalSales;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
