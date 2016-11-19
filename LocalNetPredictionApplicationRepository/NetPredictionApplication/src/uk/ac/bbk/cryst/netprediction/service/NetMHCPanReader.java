package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.model.MHCPeptideData;
import uk.ac.bbk.cryst.netprediction.model.NetMHCPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;

/*
 * NOT COMPLETE!!!!
 * 
 * */
public class NetMHCPanReader extends NetPanFileReader {
	
	public NetMHCPanReader(File netMHCPanFile, String foundFileName, String foundAllele) throws FileNotFoundException{
		super(PredictionType.MHCI,netMHCPanFile,foundFileName,foundAllele);
			
	}
	
	public NetMHCPanData read() throws Exception{
		
		NetMHCPanData netMHCPanFileData = new NetMHCPanData(this.allele, this.fastaFileName);
		String patternFull = "\\s*(\\d+)" + //pos 1
				"\\s+([\\w:*-]+)" + //allele 2
				"\\s+([a-zA-Z]+)" + //peptide 3
				"\\s+.+" + //sequence name
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + //mhc 4
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + //IC50 5
				"\\s+[-+]?(?:\\d*[.])?\\d+" + //rank not captured
				"\\s*(?:<=\\s*(\\w+))?\\s*"; // WB or SB? 6
		
		//What is this for?
		String patternPartial = "\\s*(\\d+)" + //pos
				"\\s+([\\w:*-]+)" + //allele
				"\\s+([a-zA-Z]+)" + //peptide 
				"\\s+.+" + //sequence name
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + //mhc
				"\\s+[-+]?(?:\\d*[.])?\\d+" + //rank
				"\\s*(?:<=\\s*(\\w+))?\\s*"; // WB or SB?
		
		String pattern = patternFull;
		int IC50Index = 5;
		int binderIndex = 6;
		
		int counter = 1;
		int epitopeCounter = 0;
		List<PeptideData> peptideList = new ArrayList<PeptideData>();
		
		try{
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				
				if(!line.matches(patternFull)){
					if(line.matches(patternPartial)){
						pattern = patternPartial;
						IC50Index = 0;
						binderIndex = 5;
					}
					else{
						continue;
					}
				}
				
		 	    Pattern r = Pattern.compile(pattern);
		 	    Matcher m = r.matcher(line);
				
		 	    //TODO this is not complete!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!USED FOR BOTH MHC AND MHCPAN
		 	   if (m.find( )) {
		 		   String startPositionTxt = m.group(1);
		 		   //String alleleName = m.group(2);
		 		   String peptideTxt = m.group(3);
		 		   String corePeptideTxt = m.group(4);
		 		   String mhcScoreTxt = m.group(5);
		 		   String ic50ScoreTxt = IC50Index==0 ? "0" : m.group(IC50Index);
		 		   String binder = "";
		 		   
		 		   if(StringUtils.isNotEmpty(m.group(binderIndex))){
		 			  binder = m.group(binderIndex);
		 			  epitopeCounter++;
		 		   }
		 		  
		 		   PeptideData peptide = new MHCPeptideData(counter,Integer.valueOf(startPositionTxt), 
		 				  peptideTxt,corePeptideTxt, Float.valueOf(mhcScoreTxt), Float.valueOf(ic50ScoreTxt), binder);
		 		   
		 		   peptideList.add(peptide);
		 		  
		 		   counter++;
	 		
		 	   }

			}
			
			netMHCPanFileData.setPeptideList(peptideList);
			netMHCPanFileData.setIdentifiedEpitopes(epitopeCounter);
		
		}
		
		catch(Exception ex){
			throw ex;
		}
		
		finally{
			close();
		}
		return netMHCPanFileData;
	}
	
	public void close(){
		scanner.close();
	}
}
