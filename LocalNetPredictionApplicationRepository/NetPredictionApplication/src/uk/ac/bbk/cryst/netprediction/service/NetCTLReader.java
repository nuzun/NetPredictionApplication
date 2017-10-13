package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.model.CTLPeptideData;
import uk.ac.bbk.cryst.netprediction.model.NetCTLData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;

public class NetCTLReader extends NetPanFileReader {
	
	public NetCTLReader(File netCTLFile, String foundFileName, String foundAllele) throws FileNotFoundException{
		super(PredictionType.CTL,netCTLFile,foundFileName,foundAllele);
	}
	
	public NetCTLData read() throws Exception{

		// 55 ID gi|33331470| pep ISERILSTY aff   0.4989 aff_rescale   2.1184 cle 0.9764 tap   2.8980 COMB   2.4098 <-E
		NetCTLData netCTLFileData = new NetCTLData(this.allele,this.fastaFileName);
		String pattern = "\\s*(\\d+)\\s+ID.+" + //pos and sequence name 1
				"\\s+pep\\s+([a-zA-Z]+)" + //peptide 2
				"\\s+aff\\s+([-+]?(?:\\d*[.])?\\d+)" + //mhc 3
				"\\s+aff_rescale\\s+([-+]?(?:\\d*[.])?\\d+)" + //aff_rescale 4
				"\\s+cle\\s+([-+]?(?:\\d*[.])?\\d+)" + //cle 5
				"\\s+tap\\s+([-+]?(?:\\d*[.])?\\d+)" + //tap 6
				"\\s+COMB\\s+([-+]?(?:\\d*[.])?\\d+)" + //comb 7
				"\\s*((?:<-E)?)\\s*";
		
		int counter = 1;
		int epitopeCounter = 0;
		List<PeptideData> peptideList = new ArrayList<PeptideData>();
		
		try{
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				
				
				if(!line.matches(pattern)){
					continue;
				}
				
		 	    Pattern r = Pattern.compile(pattern);
		 	    Matcher m = r.matcher(line);
				
		 	   if (m.find( )) {
		 		   String startPositionTxt = m.group(1);
		 		   String peptideTxt = m.group(2);
		 		   String mhcScoreTxt = m.group(3);
		 		   String cleavageScoreTxt = m.group(5);
		 		   String tapScoreTxt = m.group(6);
		 		   String combScoreTxt = m.group(7);
		 		   boolean epitope = false;
		 		   
		 		   if(StringUtils.isNotEmpty(m.group(8))){
		 			  epitope = true;
		 			  epitopeCounter++;
		 		   }
		 		  
		 		   PeptideData peptide = new CTLPeptideData(counter,Integer.valueOf(startPositionTxt), 
		 				  peptideTxt, Float.valueOf(mhcScoreTxt), Float.valueOf(cleavageScoreTxt), Float.valueOf(tapScoreTxt),
		 				  Float.valueOf(combScoreTxt), epitope);
		 		   
		 		   peptideList.add(peptide);
		 		  
		 		   counter++;
	 		
		 	   }

			}
			
			netCTLFileData.setPeptideList(peptideList);
			netCTLFileData.setIdentifiedEpitopes(epitopeCounter);
		}
		
		catch(Exception ex){
			throw ex;
		}
		
		finally{
			close();
		}
		return netCTLFileData;
	}
	
	public void close(){
		scanner.close();
	}
}
