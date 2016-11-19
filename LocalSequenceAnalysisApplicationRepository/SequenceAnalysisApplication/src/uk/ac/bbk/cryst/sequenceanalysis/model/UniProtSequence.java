package uk.ac.bbk.cryst.sequenceanalysis.model;

public class UniProtSequence extends Sequence {
	
	//>sp|P02771|FETA_HUMAN Alpha-fetoprotein OS=Homo sapiens GN=AFP PE=1 SV=1
	
	String name;
	
	public UniProtSequence(String proteinId,String sequence){
		
		super(proteinId,sequence);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
