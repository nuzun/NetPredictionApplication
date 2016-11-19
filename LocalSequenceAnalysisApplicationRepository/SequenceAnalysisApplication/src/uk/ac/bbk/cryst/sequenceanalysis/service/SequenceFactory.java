package uk.ac.bbk.cryst.sequenceanalysis.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.EnsemblPepSequence;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.model.UniProtSequence;

public class SequenceFactory {

	String line;
	String sequenceStr = "";
	String fastaName = "";
	String identifier = "";
	String sequenceType;
	String chromosome;
	String geneId;
	String transcriptId;
	String geneBiotype;
	String transcriptBiotype;
	String geneSymbol;
	String description;

	private Sequence getSequence(FastaFileType type, String id, String seq) {
		if (type == FastaFileType.UNIPROT) {
			UniProtSequence uniSeq = new UniProtSequence(id, seq);
			uniSeq.setName(fastaName);
			return uniSeq;
		} else {
			EnsemblPepSequence eSeq = new EnsemblPepSequence(id, seq);
			eSeq.setSequenceType(sequenceType);
			eSeq.setChromosome(chromosome);
			eSeq.setGeneId(geneId);
			eSeq.setTranscriptId(transcriptId);
			eSeq.setGeneBiotype(geneBiotype);
			eSeq.setGeneSymbol(geneSymbol);
			eSeq.setDescription(description);
			return eSeq;
		}
	}

	public List<Sequence> getSequenceList(File inputFile, FastaFileType type) throws IOException {

		List<Sequence> sequenceList = new ArrayList<Sequence>();
		// read line by line
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		try {
			// System.out.println("Starting file:" + fastaFile.getName()+
			// DateUtils.now());
			while ((line = br.readLine()) != null) {
				if (line.startsWith(">")) {
					if (!StringUtils.isEmpty(sequenceStr) && !StringUtils.isEmpty(identifier)) {
						// create the sequence object
						Sequence sequence = getSequence(type, identifier, sequenceStr);
						sequenceList.add(sequence);
						// reset the sequence
						sequenceStr = "";
					}

					if (type == FastaFileType.UNIPROT) {
						String[] headerStrings = line.trim().split("\\|");
						if (headerStrings != null && headerStrings.length > 1) {
							identifier = headerStrings[1];
							fastaName = headerStrings[headerStrings.length - 1];
						}
					} else {
						String[] headerStrings = line.trim().split(" ");
						if (headerStrings != null && headerStrings.length > 0) {
							identifier = headerStrings[0].replace(">", "");// ENSP00000446015.1
							sequenceType = headerStrings[1];// pep:known
							chromosome = headerStrings[2].replace("chromosome:", "");// chromosome:GRCh38:14:21924063:21924651:1
							geneId = headerStrings[3].replace("gene:", "");// gene:ENSG00000211792.2
							transcriptId = headerStrings[4].replace("transcript:", "");// transcript:ENST00000390440.2
							geneBiotype = headerStrings[5].replace("gene_biotype:", "");// gene_biotype:TR_V_gene
							transcriptBiotype = headerStrings[6].replace("transcript_biotype:", "");// transcript_biotype:TR_V_gene
							
							if(headerStrings.length > 7){
								geneSymbol = headerStrings[7].replace("gene_symbol:", "");// gene_symbol:TRAV14DV4

								String lineText = line.trim();
								description = lineText.substring(lineText.lastIndexOf("description:") + 12,
									lineText.length());
								// description:T cell receptor alpha variable
								// 14/delta variable 4 [Source:HGNC
								// Symbol;Acc:HGNC:12110]
							}
							else{
								geneSymbol="";
								description="";
							}
						}
					}
				} else {
					sequenceStr += line.trim();
				}
			}

			br.close();

			// do the last one
			Sequence sequence = getSequence(type, identifier, sequenceStr);
			sequenceList.add(sequence);
			sequenceStr = "";
			// System.out.println("End file:" + DateUtils.now());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return sequenceList;
	}

}
