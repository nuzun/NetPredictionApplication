package uk.ac.bbk.cryst.sequenceanalysis.model;

public class HLAProteinSequence  extends Sequence {

	//>HLA:HLA00001 A*01:01:01:01 365 bp
	
	String alleleName;
	
	public HLAProteinSequence(String proteinId,String sequence){
		super(proteinId, sequence);
	}

	public String getAlleleName() {
		return alleleName;
	}

	public void setAlleleName(String alleleName) {
		this.alleleName = alleleName;
	}
	
}
