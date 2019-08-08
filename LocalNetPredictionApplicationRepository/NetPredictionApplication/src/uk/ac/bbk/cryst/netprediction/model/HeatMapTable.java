package uk.ac.bbk.cryst.netprediction.model;

public class HeatMapTable {

	private String variant;
	private int totalBinders;
	private int totalBlackGreys;
	
	public HeatMapTable(String variant){
		this.variant = variant;
	}
	
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public int getTotalBinders() {
		return totalBinders;
	}
	public void setTotalBinders(int totalBinders) {
		this.totalBinders = totalBinders;
	}
	public int getTotalBlackGreys() {
		return totalBlackGreys;
	}
	public void setTotalBlackGreys(int totalBlackGreys) {
		this.totalBlackGreys = totalBlackGreys;
	}
	
	
}
