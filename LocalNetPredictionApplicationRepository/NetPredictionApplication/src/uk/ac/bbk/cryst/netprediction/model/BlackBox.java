package uk.ac.bbk.cryst.netprediction.model;

public class BlackBox {

	String allele;
	String variant;
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
	public BlackBox(String allele, String variant) {
		super();
		this.allele = allele;
		this.variant = variant;
	}
	
	
}
