package uk.ac.bbk.cryst.netprediction.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CTLPanPeptideData extends PeptideData {

	Float tapScore;
	Float cleavageScore;
	Float combinedScore;
	Float rankPercentage;
	
	public Float getTapScore() {
		return tapScore;
	}
	public Float getCleavageScore() {
		return cleavageScore;
	}
	public Float getCombinedScore() {
		return combinedScore;
	}
	public Float getRankPercentage() {
		return rankPercentage;
	}
	
	public CTLPanPeptideData(){
		
	}
	
	public CTLPanPeptideData(int rank, int startPosition,String peptide,Float mhcScore, Float tapScore, Float cleavageScore,
			Float combinedScore, Float rankPercentage, boolean epitope){
		this.rank = rank;
		this.startPosition = startPosition;
		this.peptide = peptide;
		this.mhcScore = mhcScore;
		this.tapScore = tapScore;
		this.cleavageScore = cleavageScore;
		this.combinedScore = combinedScore;
		this.rankPercentage = rankPercentage;
		this.epitope = epitope;
		this.IC50Score = (float) Math.pow(50000, (1-mhcScore));
		this.bindingLevel = this.isEpitope() ? "SB" : "";
	}
	
	public String toString(){
		String out = " peptide:" + this.getPeptide() +
				" start:" + this.getStartPosition() +
				" rank:" + this.getRank() +
				" mhc:" + this.getMhcScore()+
				" IC50:" + this.getIC50Score() +
				" tap:" + this.getTapScore() +
				" cleavage:" + this.getCleavageScore() +
				" combined:" + this.getCombinedScore() +
				" isEpitope:" + this.isEpitope();
		
		return out;
		
	}
	
	@Override
	public String toStringNoHeader(String del) {
		String out = this.getPeptide() + del +
				 this.getStartPosition() + del +
			     this.getRank() + del +
				 this.getMhcScore() + del +
			     this.getIC50Score() + del +
			     this.getTapScore() + del +
			     this.getCleavageScore() + del +
			     this.getCombinedScore() + del +
			     this.getRankPercentage() + del +
			     this.getBindingLevel();
	
		return out;
	}
	
	@Override
	 public int hashCode()
	 {
		  HashCodeBuilder builder = new HashCodeBuilder();
	        builder.append(rank);
	        builder.append(peptide);
	        builder.append(startPosition);
	        builder.append(mhcScore);
	        builder.append(IC50Score);
	        builder.append(tapScore);
	        builder.append(cleavageScore);
	        builder.append(combinedScore);
	        builder.append(rankPercentage);
	        return builder.toHashCode();

	 }
	@Override
	 public boolean equals(Object obj) {
		if (obj instanceof CTLPanPeptideData) {
			CTLPanPeptideData other = (CTLPanPeptideData) obj;
           EqualsBuilder builder = new EqualsBuilder();
           builder.append(this.rank, other.rank);
           builder.append(this.peptide, other.peptide);
           builder.append(this.startPosition, other.startPosition);
           builder.append(this.mhcScore, other.mhcScore);
           builder.append(this.tapScore, other.tapScore);
           builder.append(this.cleavageScore, other.cleavageScore);
           builder.append(this.combinedScore, other.combinedScore);
           builder.append(this.rankPercentage, other.rankPercentage);
           return builder.isEquals();
       }
       return false;

	}
	
	@Override
	public int compareTo(PeptideData other) {
		int last = this.combinedScore.compareTo(((CTLPanPeptideData)other).combinedScore);
        return last * -1;
	}
	@Override
	public String toStringNoHeader() {
		String out = this.getPeptide() + "\t" +
				 this.getStartPosition() + "\t" +
			     this.getRank() + "\t" +
				 this.getMhcScore() + "\t" +
			     this.getIC50Score() + "\t" +
			     this.getTapScore() + "\t" +
			     this.getCleavageScore() + "\t" +
			     this.getCombinedScore() + "\t" +
			     this.getBindingLevel();
	
		return out;
	}
	
}
