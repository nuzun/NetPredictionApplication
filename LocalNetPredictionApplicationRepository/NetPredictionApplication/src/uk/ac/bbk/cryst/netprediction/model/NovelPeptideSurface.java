package uk.ac.bbk.cryst.netprediction.model;

import java.util.List;

public class NovelPeptideSurface {
	String allele;
	String variant; // R-30-C
	List<PeptideData> peptideList;
	PeptideData peptide1;
	PeptideData peptide2;
	String colour;
	
	public NovelPeptideSurface(){
		
	}

	
	public String getColour() {
		return colour;
	}


	public void setColour(String colour) {
		this.colour = colour;
	}


	public PeptideData getPeptide1() {
		return peptide1;
	}


	public void setPeptide1(PeptideData peptide1) {
		this.peptide1 = peptide1;
	}


	public PeptideData getPeptide2() {
		return peptide2;
	}


	public void setPeptide2(PeptideData peptide2) {
		this.peptide2 = peptide2;
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

	public List<PeptideData> getPeptideList() {
		return peptideList;
	}

	public void setPeptideList(List<PeptideData> peptideList) {
		this.peptideList = peptideList;
	}

}
