package uk.ac.bbk.cryst.netprediction.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/*
 * NOT COMPLETE!!!!
 * 
 * */
public class MHCPeptideData extends PeptideData {

	String corePeptide;

	public MHCPeptideData(int rank, int startPosition, String peptide, String corePeptide, Float mhcScore,
			Float IC50Score, String bindingLevel) {
		this.rank = rank;
		this.startPosition = startPosition;
		this.peptide = peptide;
		this.corePeptide = corePeptide;
		this.mhcScore = mhcScore;
		this.bindingLevel = bindingLevel;
		this.epitope = StringUtils.isEmpty(bindingLevel) ? false : true;
		this.IC50Score = IC50Score;
	}

	public String getCorePeptide() {
		return corePeptide;
	}

	public String toString() {
		String out = " peptide:" + this.getPeptide() + " start:" + this.getStartPosition() + " rank:" + this.getRank()
				+ " aff:" + this.getMhcScore() + " IC50:" + this.getIC50Score() + " identified:"
				+ this.getBindingLevel();

		return out;

	}

	public String toStringNoHeader() {
		String out = this.getPeptide() + "\t" + this.getStartPosition() + "\t" + this.getRank() + "\t"
				+ this.getMhcScore() + "\t" + this.getIC50Score() + "\t" + this.getBindingLevel();

		return out;

	}

	// print with delimiter
	public String toStringNoHeader(String del) {
		String out = this.getPeptide() + del + this.getStartPosition() + del + this.getRank() + del
				+ this.getCorePeptide() + del + this.getMhcScore() + del + this.getIC50Score() + del
				+ this.getBindingLevel();

		return out;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(rank);
		builder.append(peptide);
		builder.append(startPosition);
		builder.append(mhcScore);
		return builder.toHashCode();

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MHCPeptideData) {
			MHCPeptideData other = (MHCPeptideData) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.rank, other.rank);
			builder.append(this.peptide, other.peptide);
			builder.append(this.startPosition, other.startPosition);
			builder.append(this.mhcScore, other.mhcScore);

			return builder.isEquals();
		}
		return false;
	}

	@Override
	public int compareTo(PeptideData other) {
		System.out.println("YAY4!");

		int last = this.mhcScore.compareTo(other.mhcScore);
		return last * -1;
	}
}
