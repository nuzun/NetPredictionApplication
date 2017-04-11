package uk.ac.bbk.cryst.netprediction.model;

public class HeatMapBox {

	String allele;
	String variant;
	String colour;
	
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getAllele() {
		return allele;
	}
	public void setAllele(String allele) {
		this.allele = allele;
	}
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public HeatMapBox(String allele, String variant,String colour) {
		super();
		this.allele = allele;
		this.variant = variant;
		this.colour = colour;
	}
	
	
}
