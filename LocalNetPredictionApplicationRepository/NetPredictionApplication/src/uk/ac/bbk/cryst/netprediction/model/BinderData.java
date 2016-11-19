package uk.ac.bbk.cryst.netprediction.model;


/*
 * This is the temporary databean class to read the csv file for pool3
 * The csv contains the following fields and it is the 
 * result of a SQL statement
 * 
 */
public class BinderData {

	String uniprot_code;
	String proteinName;
	String allele;
	String peptide;
	int startPosition;
	Float mhcScore;
	Float IC50Score;
	Float cleavageScore;
	String binder;
	
	
	public String getUniprot_code() {
		return uniprot_code;
	}
	public void setUniprot_code(String uniprot_code) {
		this.uniprot_code = uniprot_code;
	}
	public String getProteinName() {
		return proteinName;
	}
	public void setProteinName(String proteinName) {
		this.proteinName = proteinName;
	}
	public String getAllele() {
		return allele;
	}
	public void setAllele(String allele) {
		this.allele = allele;
	}
	public String getPeptide() {
		return peptide;
	}
	public void setPeptide(String peptide) {
		this.peptide = peptide;
	}
	public int getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	public Float getMhcScore() {
		return mhcScore;
	}
	public void setMhcScore(Float mhcScore) {
		this.mhcScore = mhcScore;
	}
	public Float getIC50Score() {
		return IC50Score;
	}
	public void setIC50Score(Float iC50Score) {
		IC50Score = iC50Score;
	}
	public Float getCleavageScore() {
		return cleavageScore;
	}
	public void setCleavageScore(Float cleavageScore) {
		this.cleavageScore = cleavageScore;
	}
	public String getBinder() {
		return binder;
	}
	public void setBinder(String binder) {
		this.binder = binder;
	}

	public String toString(){
		String str = "UNIPROT_CODE:" + this.getUniprot_code() +
				" NAME:" + this.getProteinName() +
				" ALLELE:" + this.getAllele() + 
				" PEPTIDE:" + this.getPeptide() +
				" POSITION:" + this.getStartPosition() +
				" MHC_SCORE:" + this.getMhcScore() +
				" IC50_SCORE:" + this.getIC50Score() +
				" BINDER:" + this.getBinder();
		return str;
	}
	
}
