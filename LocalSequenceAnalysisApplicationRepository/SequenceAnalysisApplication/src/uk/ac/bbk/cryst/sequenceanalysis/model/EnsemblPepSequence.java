package uk.ac.bbk.cryst.sequenceanalysis.model;

public class EnsemblPepSequence extends Sequence{
	/*>ENSP00000379138.2 
	pep:known 
	chromosome:GRCh38:4:73436163:73456174:1 
	gene:ENSG00000081051.7 
	transcript:ENST00000395792.6 
	gene_biotype:protein_coding
	transcript_biotype:protein_coding 
	gene_symbol:AFP 
	description:alpha fetoprotein [Source:HGNC Symbol;Acc:HGNC:317]*/
	
	//public enum GeneBiotype { PROTEIN_CODING,BBB}
	//public enum TranscriptBiotype { PROTEIN_CODING,BBB}
	
	String sequenceType;
	String chromosome;
	String geneId;
	String transcriptId;
	String geneBiotype;
	String transcriptBiotype;
	String geneSymbol;
	String description;
	
	
	public EnsemblPepSequence(String proteinId, String sequence){
		super(proteinId, sequence);
	}

	public String getSequenceType() {
		return sequenceType;
	}
	public void setSequenceType(String sequenceType) {
		this.sequenceType = sequenceType;
	}
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public String getGeneId() {
		return geneId;
	}
	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}
	public String getTranscriptId() {
		return transcriptId;
	}
	public void setTranscriptId(String transcriptId) {
		this.transcriptId = transcriptId;
	}
	public String getGeneBiotype() {
		return geneBiotype;
	}
	public void setGeneBiotype(String geneBiotype) {
		this.geneBiotype = geneBiotype;
	}
	public String getTranscriptBiotype() {
		return transcriptBiotype;
	}
	public void setTranscriptBiotype(String transcriptBiotype) {
		this.transcriptBiotype = transcriptBiotype;
	}
	public String getGeneSymbol() {
		return geneSymbol;
	}
	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
