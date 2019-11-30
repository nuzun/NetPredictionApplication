package uk.ac.bbk.cryst.sequenceanalysis.model;

public class MatchDataClassII {

	int coreStartPosition1;
	int coreStartPosition2;
	String corePeptide1;
	String corePeptide2;
	String panningPeptide1;
	String panningPeptide2;
	String proteinId1;
	String proteinId2;
	
	
	public MatchDataClassII(String proteinId1, int coreStartPosition1, String corePeptide1,String panningPeptide1,
							String proteinId2, int coreStartPosition2, String corePeptide2,String panningPeptide2) {
		super();
		this.coreStartPosition1 = coreStartPosition1;
		this.coreStartPosition2 = coreStartPosition2;
		this.corePeptide1 = corePeptide1;
		this.corePeptide2 = corePeptide2;
		this.panningPeptide1 = panningPeptide1;
		this.panningPeptide2 = panningPeptide2;
		this.proteinId1 = proteinId1;
		this.proteinId2 = proteinId2;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("protein1:" + getProteinId1() + "\tcoreStart1:"+ getCoreStartPosition1() + "\tcorePeptide1:" + getCorePeptide1() + "\tpeptide1:" + getPanningPeptide1() + "\t");
		sb.append("protein2:" + getProteinId2() + "\tcoreStart2:"+ getCoreStartPosition2() + "\tcorePeptide2:" + getCorePeptide2() + "\tpeptide2:" + getPanningPeptide2());
		return sb.toString();
	}
	
	public String toStringOnlyValues(){
		StringBuilder sb = new StringBuilder();
		sb.append(getProteinId1() + "," + getCoreStartPosition1() + "," + getCorePeptide1() + "," + getPanningPeptide1() + ",");
		sb.append(getProteinId2() + "," + getCoreStartPosition2() + "," + getCorePeptide2() + "," + getPanningPeptide2());
		return sb.toString();
	}
	
	public int getCoreStartPosition1() {
		return coreStartPosition1;
	}
	public void setCoreStartPosition1(int coreStartPosition1) {
		this.coreStartPosition1 = coreStartPosition1;
	}
	public int getCoreStartPosition2() {
		return coreStartPosition2;
	}
	public void setCoreStartPosition2(int coreStartPosition2) {
		this.coreStartPosition2 = coreStartPosition2;
	}
	public String getCorePeptide1() {
		return corePeptide1;
	}
	public void setCorePeptide1(String corePeptide1) {
		this.corePeptide1 = corePeptide1;
	}
	public String getCorePeptide2() {
		return corePeptide2;
	}
	public void setCorePeptide2(String corePeptide2) {
		this.corePeptide2 = corePeptide2;
	}
	public String getPanningPeptide1() {
		return panningPeptide1;
	}
	public void setPanningPeptide1(String panningPeptide1) {
		this.panningPeptide1 = panningPeptide1;
	}
	public String getPanningPeptide2() {
		return panningPeptide2;
	}
	public void setPanningPeptide2(String panningPeptide2) {
		this.panningPeptide2 = panningPeptide2;
	}
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
	
	
}
