package uk.ac.bbk.cryst.sequenceanalysis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;


public class PeptideGenerator {
	
	public static  List<String> getAllPeptides(Sequence sequence, int length){
		
		if(length <= 0){
			throw new IllegalArgumentException("Provide a valid k-mer number.");
		}
		
		List<String> peptides = new ArrayList<String>();
		String aminoAcidSeq = sequence.getSequence();
		
		for(int i=0; i< aminoAcidSeq.length()-length+1;i++){
			String temp = aminoAcidSeq.substring(i, i+length);
			peptides.add(temp);
		}
		
		return peptides;
	}
	
	public static  List<String> getUniquePeptides(Sequence sequence, int length){
		
		if(length <= 0){
			throw new IllegalArgumentException("Provide a valid k-mer number.");
		}
		
		List<String> peptides = new ArrayList<String>();
		String aminoAcidSeq = sequence.getSequence();
		
		for(int i=0; i< aminoAcidSeq.length()-length+1;i++){
			String temp = aminoAcidSeq.substring(i, i+length);
			
			if(!peptides.contains(temp)){
				peptides.add(temp);
			}
		}
		
		return peptides;
	}
	
	/*position map starts from 0*/
	public static  Map<Integer,String> getPositionPeptideMap(Sequence sequence, int length){
		
		if(length <= 0){
			throw new IllegalArgumentException("Provide a valid k-mer number.");
		}
		
		Map<Integer,String> peptideMap = new HashMap<Integer,String>();
		String aminoAcidSeq = sequence.getSequence();
		
		for(int i=0; i< aminoAcidSeq.length()-length+1;i++){
			String temp = aminoAcidSeq.substring(i, i+length);
			peptideMap.put(i,temp);
		}
		
		return peptideMap;
	}

}
