package uk.ac.bbk.cryst.sequenceanalysis.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.common.SequenceAnalysisProperties;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.model.UniProtSequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceComparator;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceFactory;
import uk.ac.bbk.cryst.sequenceanalysis.util.SequenceOperationsHelper;

public class SeqeunceOperationsMain {

	/**
	 * @param args
	 * @throws IOException 
	 * 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
				
		SequenceAnalysisProperties programProperties = new SequenceAnalysisProperties();

		SequenceFactory sequenceFactory = new SequenceFactory();
		
	
		//File file1 = new File(programProperties.getValue("inputPath") + "test/test1_peptide.fasta"); 	
	 	File file2 = new File(programProperties.getValue("sequenceFileFullPath"));
		
		//Sequence seq1 = sequenceFactory.getSequence(file1, FastaFileType.UNIPROT);
		List<Sequence> seq2List = sequenceFactory.getSequenceList(file2, FastaFileType.UNIPROT);

		for(Sequence seq : seq2List){
			System.out.println(seq.getProteinId());
			System.out.println(seq.getSequence());
			System.out.println("==========================================================================");
		}
		
		
		//Generates position numbers starting from 1
		//SequenceOperationsHelper.generatePositionNumbers(seq1);
		
		//System.out.println("==========================================================================");
		//will provide the position in the map starting from 1 so directly provide the numbers
		//from 1000genomes or cosmic
		//Map<Integer,Character> changeMap = new HashMap<Integer, Character>();
		//changeMap.put(32, 'A');
		//changeMap.put(72, 'Q');
		
		//String newSeq = SequenceOperationsHelper.changeSequence(seq1, changeMap);
		//System.out.println(newSeq);

	}

}
