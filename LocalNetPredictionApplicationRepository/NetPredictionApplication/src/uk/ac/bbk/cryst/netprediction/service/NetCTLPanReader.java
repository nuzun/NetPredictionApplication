package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.model.CTLPanPeptideData;
import uk.ac.bbk.cryst.netprediction.model.NetCTLPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;

public class NetCTLPanReader extends NetPanFileReader {
	
	public NetCTLPanReader(File netCTLPanFile, String foundFileName, String foundAllele) throws FileNotFoundException{
		super(PredictionType.CTL,netCTLPanFile,foundFileName,foundAllele);
	}
	
	public NetCTLPanData read() throws Exception{

		//#  N   Sequence Name       Allele      Peptide      MHC      TAP      Cle     Comb  %Rank
		//# 219 143B_BOVIN_(P29  HLA-A*02:01    QLLRDNLTL  0.42500  1.04100  0.97391  0.67015   3.00 
		NetCTLPanData netCTLPanFileData = new NetCTLPanData(this.allele,this.fastaFileName);
		String pattern = "\\s*(\\d+)\\s+.+" + //pos and sequence name
				"\\s+([\\w:*-]+)" + //allele
				"\\s+([a-zA-Z]+)" + //peptide
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + //mhc
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + //tap
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + //cle
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + //comb
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + //rank
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
		 		  // String alleleName = m.group(2);
		 		   String peptideTxt = m.group(3);
		 		   String mhcScoreTxt = m.group(4);
		 		   String tapScoreTxt = m.group(5);
		 		   String cleavageScoreTxt = m.group(6);
		 		   String combScoreTxt = m.group(7);
		 		   String rankPercentageTxt = m.group(8);
		 		   boolean epitope = false;
		 		   
		 		   if(StringUtils.isNotEmpty(m.group(9))){
		 			  epitope = true;
		 			  epitopeCounter++;
		 		   }
		 		  
		 		   PeptideData peptide = new CTLPanPeptideData(counter,Integer.valueOf(startPositionTxt), 
		 				  peptideTxt, Float.valueOf(mhcScoreTxt), Float.valueOf(tapScoreTxt),Float.valueOf(cleavageScoreTxt),
		 				  Float.valueOf(combScoreTxt),Float.valueOf(rankPercentageTxt), epitope);
		 		   
		 		   peptideList.add(peptide);
		 		  
		 		   counter++;
	 		
		 	   }

			}
			
			netCTLPanFileData.setPeptideList(peptideList);
			netCTLPanFileData.setIdentifiedEpitopes(epitopeCounter);
		}
		
		catch(Exception ex){
			throw ex;
		}
		
		finally{
			close();
		}
		return netCTLPanFileData;
	}
	
	public void close(){
		scanner.close();
	}
}