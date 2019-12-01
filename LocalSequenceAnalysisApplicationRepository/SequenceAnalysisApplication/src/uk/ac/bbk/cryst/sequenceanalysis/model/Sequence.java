package uk.ac.bbk.cryst.sequenceanalysis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Sequence {

	String sequence;
	String proteinId;
	
	public Sequence(String proteinId, String sequence){
		if(!isSequenceValid(sequence)){
			throw new IllegalArgumentException("The sequence is not valid.");
		}
		
		sequence = sequence.replaceAll("\\s+", "");
		
		this.sequence = sequence.toUpperCase();
		this.proteinId = proteinId;
	}
	
	
	protected boolean isSequenceValid(String sequence) {
		if(sequence != null){
			sequence = sequence.replaceAll("\\s+", "");
			Pattern pattern = Pattern.compile("^[GAVFPMILDEKRSTYHCNQWBUXZOJ*]+$",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(sequence);
			if(matcher.matches()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Converts the sequence into a string list of letters
	 *
	 * @return a string list of the sequence
	 */
	public List<String> getSequenceArray(){
		List<String> sequenceArray = new ArrayList<String>();
		for (char c : this.getSequence().toCharArray()){
			sequenceArray.add(String.valueOf(c));
		}
		
		return sequenceArray;
	}
	
	/**
	 * when passing the position, assume it starts from 1
	 * 
	 */
	public String getPanningSequence(int position, int nMer){
		int startIndex = 0;
		int endIndex = 0;
		if(position < nMer) {
			startIndex = 0;
		}
		else {
			startIndex = position - nMer;
		}
		
		if(position + nMer > this.getSequence().length()){
			endIndex = this.getSequence().length();
		}
		else{
			endIndex = position + nMer -1;
		}
		
		
		return this.getSequence().substring(startIndex, endIndex);//start inclusive, end exclusive
	}
	
	public int length(){
		return this.getSequence().length();
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getProteinId() {
		return proteinId;
	}

	public void setProteinId(String proteinId) {
		this.proteinId = proteinId;
	}
	
}
