package uk.ac.bbk.cryst.netprediction.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.util.PeptideHelper;

public abstract class NetPanData implements Comparable<NetPanData> {

	List<PeptideData> peptideList;
	String allele;
	String fastaFileName;
	int identifiedEpitopes;

	public NetPanData(String allele, String fastaFileName) {
		this.allele = allele;
		this.fastaFileName = fastaFileName;
	}

	public List<PeptideData> getPeptideList() {
		return peptideList;
	}

	public void setPeptideList(List<PeptideData> peptideList) {
		this.peptideList = peptideList;
	}

	public String getAllele() {
		return allele;
	}

	public void setAllele(String allele) {
		this.allele = allele;
	}

	public String getFastaFileName() {
		return fastaFileName;
	}

	public void setFastaFileName(String fastaFileName) {
		this.fastaFileName = fastaFileName;
	}

	public int getIdentifiedEpitopes() {
		return identifiedEpitopes;
	}

	public void setIdentifiedEpitopes(int identifiedEpitopes) {
		this.identifiedEpitopes = identifiedEpitopes;
	}

	public int getNMer() {
		return this.getPeptideList().get(0).getPeptide().length();
	}

	public int getSequenceLength() {
		return this.getPeptideList().size() + this.getNMer() - 1;
	}

	public List<PeptideData> getPanningPeptideList(int panningPosition) {
		List<PeptideData> peptideList = new ArrayList<PeptideData>();

		int startIndex = panningPosition < this.getNMer() ? 0 : (panningPosition - this.getNMer());
		int endIndex = (panningPosition + this.getNMer()) > this.getSequenceLength()
				? (this.getSequenceLength() - this.getNMer()) : panningPosition - 1;

		for (int i = startIndex; i <= endIndex; i++) {
			peptideList.add(this.getSpecificPeptideData(i));
		}
		return peptideList;
	}

	public List<PeptideData> getSpecificPeptideData(String peptideStr) {
		List<PeptideData> peptideDataList = new ArrayList<PeptideData>();

		for (PeptideData peptideData : this.peptideList) {
			if (StringUtils.equals(peptideData.getPeptide(), StringUtils.trim(peptideStr))) {
				peptideDataList.add(peptideData);
			} else if (PeptideHelper.isSubSequence(peptideData.getPeptide(), StringUtils.trim(peptideStr))) {
				if (!isDuplicate(peptideDataList, peptideData)) {
					peptideDataList.add(peptideData);
				}
			}
		}
		return peptideDataList;
	}

	public List<MHCIIPeptideData> getSpecificPeptideDataByCore(String corePeptideStr) {
		List<MHCIIPeptideData> peptideDataList = new ArrayList<MHCIIPeptideData>();

		for (PeptideData peptideData : this.peptideList) {
			if (peptideData instanceof MHCIIPeptideData) {
				MHCIIPeptideData newPep = (MHCIIPeptideData) peptideData;
				if (StringUtils.equals(newPep.getCorePeptide(), StringUtils.trim(corePeptideStr))) {
					peptideDataList.add(newPep);
				}
			}
		}
		return peptideDataList;
	}

	public List<MHCIIPeptideData> getSpecificPeptideDataByMaskedCore(String corePeptideStr, List<Integer> positions,
			boolean isMatch) {
		List<MHCIIPeptideData> peptideDataList = new ArrayList<MHCIIPeptideData>();

		for (PeptideData peptideData : this.peptideList) {
			if (peptideData instanceof MHCIIPeptideData) {
				MHCIIPeptideData newPep = (MHCIIPeptideData) peptideData;

				if (isMatch(newPep.getCorePeptide(), StringUtils.trim(corePeptideStr), positions, isMatch)) {
					peptideDataList.add(newPep);
				}
			}
		}
		return peptideDataList;
	}
	
	public List<PeptideData> getSpecificPeptideDataByMaskedMatch(String corePeptideStr, List<Integer> positions,
			boolean isMatch) {
		List<PeptideData> peptideDataList = new ArrayList<PeptideData>();

		for (PeptideData peptideData : this.peptideList) {
			if (peptideData instanceof MHCIIPeptideData) {
				MHCIIPeptideData newPep = (MHCIIPeptideData) peptideData;

				if (isMatch(newPep.getCorePeptide(), StringUtils.trim(corePeptideStr), positions, isMatch)) {
					peptideDataList.add(newPep);
				}
			}
			
			else{
				if (isMatch(peptideData.getPeptide(), StringUtils.trim(corePeptideStr), positions, isMatch)) {
					peptideDataList.add(peptideData);
				}
			}
		}
		return peptideDataList;
	}

	public static boolean isMatch(String peptide1, String peptide2, List<Integer> positions, boolean condition) {
		// TODO: AJS confirmed exact match for * but the characters like B
		// etc???
		if (condition == true) {
			for (int position : positions) {
				if (peptide1.charAt(position - 1) == peptide2.charAt(position - 1)) {
					continue;
				} else {
					return false;
				}
			}
			return true;
		}

		else {

			for (int i = 0; i < peptide1.length(); i++) {

				if (positions.contains(i + 1)) {
					continue;
				}
				if (peptide1.charAt(i) != peptide2.charAt(i)) {
					return false;
				} else {
					continue;
				}
			}

			return true;

		}
	}

	private boolean isDuplicate(List<PeptideData> peptideDataList, PeptideData peptideData) {

		for (PeptideData item : peptideDataList) {
			if (StringUtils.equals(item.getPeptide(), peptideData.getPeptide())) {
				return true;
			}
		}

		return false;
	}

	public List<PeptideData> getEpitopes() {
		List<PeptideData> epitopes = new ArrayList<PeptideData>();

		for (PeptideData peptideData : this.getPeptideList()) {
			if (peptideData.isEpitope()) {
				epitopes.add(peptideData);
			}
		}

		return epitopes;
	}

	public List<PeptideData> getStrongBinderPeptides() {
		List<PeptideData> binders = new ArrayList<PeptideData>();

		for (PeptideData peptideData : this.getPeptideList()) {
			if (peptideData.isStrongBinder()) {
				binders.add(peptideData);
			}
		}

		return binders;
	}

	public List<PeptideData> getWeakBinderPeptides() {
		List<PeptideData> binders = new ArrayList<PeptideData>();

		for (PeptideData peptideData : this.getPeptideList()) {
			if (peptideData.isWeakBinder()) {
				binders.add(peptideData);
			}
		}

		return binders;
	}

	public List<PeptideData> getTopNBinders(int n) {
		List<PeptideData> binders = new ArrayList<PeptideData>();
		binders.addAll(this.getPeptideList());
		Collections.sort(binders);

		return binders.subList(0, n);
	}

	public PeptideData getSpecificPeptideData(String peptide, int position) {

		for (PeptideData peptideData : this.getPeptideList()) {
			if (StringUtils.equals(peptideData.getPeptide(), peptide) && peptideData.getStartPosition() == position) {
				return peptideData;
			}
		}

		return null;
	}

	public PeptideData getSpecificPeptideData(int position) {

		for (PeptideData peptideData : this.getPeptideList()) {
			if (peptideData.getStartPosition() == position) {
				return peptideData;
			}
		}

		return null;
	}

	@Override
	public int compareTo(NetPanData other) {
		int last = this.allele.compareTo(other.allele);
		return last == 0 ? this.fastaFileName.compareTo(other.fastaFileName) : last;
	}

	public String toString() {

		String out = " ALLELE:" + this.getAllele() + " SEQUENCE:" + this.getFastaFileName() + " IDENTIFIED_EPITOPES:"
				+ this.getIdentifiedEpitopes();
		return out;
	}

}
