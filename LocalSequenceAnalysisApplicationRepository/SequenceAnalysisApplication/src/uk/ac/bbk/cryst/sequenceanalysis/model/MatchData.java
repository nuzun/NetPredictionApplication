package uk.ac.bbk.cryst.sequenceanalysis.model;

public class MatchData {
	
	int position1;
	int position2;
	String peptide1;
	String peptide2;
	String proteinId1;
	String proteinId2;
	
	public String getProteinId1() {
		return proteinId1;
	}


	public void setProteinId1(String proteinId1) {
		this.proteinId1 = proteinId1;
	}


	public String getProteinId2() {
		return proteinId2;
	}


	public void setProteinId2(String proteinId2) {
		this.proteinId2 = proteinId2;
	}


	public int getPosition1() {
		return position1;
	}


	public void setPosition1(int position1) {
		this.position1 = position1;
	}


	public int getPosition2() {
		return position2;
	}


	public void setPosition2(int position2) {
		this.position2 = position2;
	}


	public String getPeptide1() {
		return peptide1;
	}


	public void setPeptide1(String peptide1) {
		this.peptide1 = peptide1;
	}


	public String getPeptide2() {
		return peptide2;
	}


	public void setPeptide2(String peptide2) {
		this.peptide2 = peptide2;
	}


	public MatchData(String proteinId1, int pos1, String peptide1, String proteinId2, int pos2, String peptide2 ){
		this.proteinId1 = proteinId1;
		this.proteinId2 = proteinId2;
		this.position1 = pos1;
		this.position2 = pos2;
		this.peptide1 = peptide1;
		this.peptide2 = peptide2;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("protein1:" + getProteinId1() + "\tpos1:"+ getPosition1() + "\tpeptide1:" + getPeptide1() + "\t");
		sb.append("protein2:" + getProteinId2() + "\tpos2:"+ getPosition2() + "\tpeptide2:" + getPeptide2());
		return sb.toString();
	}
	
	public String toStringOnlyValues(){
		StringBuilder sb = new StringBuilder();
		sb.append(getProteinId1() + "," + getPosition1() + "," + getPeptide1() + ",");
		sb.append(getProteinId2() + "," + getPosition2() + "," + getPeptide2());
		return sb.toString();
	}
	
	
}
