package uk.ac.bbk.cryst.sequenceanalysis.util;

import java.util.Map;

import uk.ac.bbk.cryst.sequenceanalysis.model.UniProtSequence;

public class SequenceOperationsHelper {

	public static void generatePositionNumbers(UniProtSequence sequence){
		/* for a specific position
		int pos = 214; // starts from 1
		for(int i = pos-3;i < pos+3;i++){
			System.out.println("pos:"+ (i) + " - "+seq1.getSequenceArray().get(i-1));
		}
		*/
		
		for(int i = 1;i <=sequence.length();i++){
			System.out.println("pos:"+ (i) + " - "+sequence.getSequenceArray().get(i-1));
		}
	}
	
	/* Will provide the position in the map starting from 1, so need to adjust your sequence
	 * This will update the sequence with the position number and residue provided in the map 
	 * and return new sequence to you.
	 */
	public static String changeSequence(UniProtSequence sequence,Map<Integer,Character> changeMap){
		
		StringBuilder newSeq = new StringBuilder(sequence.getSequence());
		
		for (Integer position : changeMap.keySet()){
			newSeq.setCharAt((position-1), changeMap.get(position));
		}
		
		return newSeq.toString();
	}
	
	public static boolean isIdentical(UniProtSequence seq1, UniProtSequence seq2){
		boolean flag = true;
		
		if(seq1.length() != seq2.length()) {
			flag = false;
		}
		for(int i = 0 ; i < seq1.getSequence().length() ; i++){
			if(seq1.getSequence().toCharArray()[i] == seq2.getSequence().toCharArray()[i]){
				continue;
			}
			else{
				//Since the sequence starts from 0 in my code, then increment the position to display that starts from 1.
				System.out.println("The position:"+(i+1)+" seq1:"+seq1.getSequence().toCharArray()[i]+ " seq2:"+seq2.getSequence().toCharArray()[i]);
				flag = false;
				continue;
			}
		}
		
		return flag;
	}
	
	public static String generateSequenceHeader(){
		return null;
	}
}
