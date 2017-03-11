package uk.ac.bbk.cryst.sequenceanalysis.main;

import java.io.IOException;

import uk.ac.bbk.cryst.sequenceanalysis.common.SequenceAnalysisProperties;

public class SeqeunceOperationsMain {

	/**
	 * @param args
	 * @throws IOException 
	 * 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		SequenceAnalysisProperties programProperties = new SequenceAnalysisProperties();

		//File file1 = new File(programProperties.getValue("inputPath") + "survivin_O15392.fasta"); 		 	
	 	
		//passed!
		//File file1 = new File(programProperties.getValue("inputPath") + "test/test12.fasta"); 	
	 	//File file2 = new File(programProperties.getValue("inputPath") + "test/test12_org.fasta");
		
		//UniProtSequence seq1 = SequenceFactory.readFile(file1).get(0);
	 	//Sequence seq2 = FastaFileReader.readFile(file2).get(0);
		
	 	/* Sequence compare
	 	if(SequenceOperationsHelper.isIdentical(seq1, seq2)){
	 		System.out.println("IDENTICAL");
	 	}
	 	else{
	 		System.out.println("NOT--------IDENTICAL");
	 	}
		*/
		
		//Generates position numbers starting from 1
		//SequenceOperationsHelper.generatePositionNumbers(seq1);
		
		System.out.println("==========================================================================");
		//will provide the position in the map starting from 1 so directly provide the numbers
		//from 1000genomes or cosmic
		//Map<Integer,Character> changeMap = new HashMap<Integer, Character>();
		//changeMap.put(32, 'A');
		//changeMap.put(72, 'Q');
		
		//String newSeq = SequenceOperationsHelper.changeSequence(seq1, changeMap);
		//System.out.println(newSeq);

	}

}
