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
		
		/*
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
		
		*/
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
		
		SequenceFactory sequenceFactory = new SequenceFactory();
		
		File pairFile = new File("/home/nuzun/Desktop/HLA_Allo_Analysis/Class_I/"
				+ "HLAB4405(donor)_vs_HLAB4404(recipient).txt");
		
		List<Sequence> seqList = sequenceFactory.getSequenceList(pairFile, FastaFileType.HLA);
		
		String s1 = "";
		String s2 = "";

		if(seqList.size() > 2){
			System.out.println("Something wrong!");
		}
		
		s1 = seqList.get(0).getSequence();
		s2 = seqList.get(1).getSequence();
		

		if(s1.length() != s2.length()){
			System.out.println("Not the same length");
			return;
		}
		else{
			System.out.println(seqList.get(0).getProteinId());
			System.out.println(seqList.get(1).getProteinId());

			System.out.println(s1.length());
		}
		
		for(int i=0; i< s1.length() && i< s2.length();i++){
			if (s1.charAt(i) == s2.charAt(i)){
				//System.out.println("Y");
			}
			else{
				System.out.println(s1.charAt(i)+ "-" + (i+1) + "-"+s2.charAt(i));
			}
				
		}

	}

}
