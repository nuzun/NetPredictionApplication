package uk.ac.bbk.cryst.netprediction.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class MHCIIPeptideData extends PeptideData {

	String corePeptide;
	int coreStartPosition;
	Float rankPercentage;
	String identity;

	public MHCIIPeptideData(int rank, Integer startPosition, String peptide, String corePeptide, int coreStartPosition ,Float mhcScore,
			Float ic50Score, Float rankPercentage, String identity, String bindingLevel) {
		this.rank = rank;
		this.startPosition = startPosition;
		this.peptide = peptide;
		this.corePeptide = corePeptide;
		this.coreStartPosition = coreStartPosition;
		this.mhcScore = mhcScore;
		this.IC50Score = ic50Score;
		this.rankPercentage = rankPercentage;
		this.identity = identity;
		this.bindingLevel = bindingLevel;
		this.epitope = StringUtils.isEmpty(bindingLevel) ? false : true;

	}

	public MHCIIPeptideData() {
		// TODO Auto-generated constructor stub
	}

	public String getCorePeptide() {
		return corePeptide;
	}

	public void setCorePeptide(String corePeptide) {
		this.corePeptide = corePeptide;
	}

	public int getCoreStartPosition() {
		return coreStartPosition;
	}

	public void setCoreStartPosition(int coreStartPosition) {
		this.coreStartPosition = coreStartPosition;
	}

	public Float getRankPercentage() {
		return rankPercentage;
	}

	public void setRankPercentage(Float rankPercentage) {
		this.rankPercentage = rankPercentage;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	
	@Override
	public String toString() {
		return "MHCIIPeptideData [rank=" + rank + ", startPosition=" + startPosition + ", peptide=" + peptide
				+ ", IC50Score=" + IC50Score + ", mhcScore=" + mhcScore + ", bindingLevel=" + bindingLevel
				+ ", epitope=" + epitope + ", coreStartPosition=" + coreStartPosition + ", corePeptide=" + corePeptide
				+ ", rankPercentage=" + rankPercentage + ", identity=" + identity + "]";
	}
	

	@Override
	public String toStringNoHeader(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toStringNoHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(rank);
		builder.append(startPosition);
		builder.append(peptide);
		builder.append(corePeptide);
		builder.append(coreStartPosition);
		builder.append(mhcScore);
		builder.append(IC50Score);
		builder.append(rankPercentage);
		builder.append(identity);
		builder.append(bindingLevel);
		return builder.toHashCode();

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MHCIIPeptideData) {
			MHCIIPeptideData other = (MHCIIPeptideData) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.rank, other.rank);
			builder.append(this.startPosition, other.startPosition);
			builder.append(this.peptide, other.peptide);
			builder.append(this.corePeptide, other.corePeptide);
			builder.append(this.coreStartPosition, other.coreStartPosition);
			builder.append(this.mhcScore, other.mhcScore);
			builder.append(this.IC50Score, other.IC50Score);
			builder.append(this.rankPercentage, other.rankPercentage);
			builder.append(this.identity, other.identity);
			builder.append(this.bindingLevel, other.bindingLevel);
			return builder.isEquals();
		}
		return false;

	}
	
	@Override
	public int compareTo(PeptideData other) {
		return this.getIC50Score() < other.getIC50Score() ? -1 : (this.getIC50Score() > other.getIC50Score() ? 1 :0);
	}

}
