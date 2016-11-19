package uk.ac.bbk.cryst.netprediction.util;

import java.util.ArrayList;
import java.util.List;

public class PeptideHelper {
	public static List<String> chopPeptide(String proteinSeq, int length){
		if(length <= 0){
			throw new IllegalArgumentException("Provide a valid k-mer number.");
		}
		
		List<String> peptides = new ArrayList<String>();
		
		for(int i=0; i< proteinSeq.length()-length+1;i++){
			String temp = proteinSeq.substring(i, i+length);
			peptides.add(temp);
		}
		
		return peptides;
	}
	
	public static boolean isSubSequence(String peptide, String sub){
		if(peptide.indexOf(sub) >= 0){
			return true;
		}
		
		return false;
	}	

}
