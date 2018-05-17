package uk.ac.bbk.cryst.netprediction.model;

import org.apache.commons.lang3.StringUtils;

public abstract class PeptideData implements Comparable<PeptideData> {

	int rank;
	int startPosition;
	String peptide;
	Float mhcScore;
	Float IC50Score;
	boolean epitope;
	String bindingLevel;

	public PeptideData() {

	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public void setPeptide(String peptide) {
		this.peptide = peptide;
	}

	public void setMhcScore(Float mhcScore) {
		this.mhcScore = mhcScore;
	}

	public void setIC50Score(Float iC50Score) {
		IC50Score = iC50Score;
	}

	public void setEpitope(boolean epitope) {
		this.epitope = epitope;
	}

	public void setBindingLevel(String bindingLevel) {
		this.bindingLevel = bindingLevel;
	}

	public int getRank() {
		return rank;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public String getPeptide() {
		return peptide;
	}

	public Float getMhcScore() {
		return mhcScore;
	}

	public Float getIC50Score() {
		return IC50Score;
	}

	public boolean isEpitope() {
		return epitope;
	}

	public String getBindingLevel() {
		return bindingLevel;
	}

	public boolean isStrongBinder() {
		return StringUtils.equals(this.getBindingLevel(), "SB") ? true : false;
	}

	public boolean isWeakBinder() {
		return (StringUtils.equals(this.getBindingLevel(), "WB") || this.getBindingLevel() == "") ? true : false;
	}

	@Override
	public String toString() {
		return "PeptideData [startPosition=" + startPosition + ", peptide=" + peptide + ", mhcScore=" + mhcScore
				+ ", IC50Score=" + IC50Score + "]";
	};
	
	public String toStringNoHeader(String string) {
		return "";
	};

	public String toStringNoHeader() {
		return "";
	}

}
