package uk.ac.bbk.cryst.netprediction.main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.*;
import uk.ac.bbk.cryst.netprediction.service.NetPanDataBuilder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/*
 * This is the main program to generate pool3:Any WB/SB from MHC predictions with the
 * lowest cleavage scores. Since MySQL db doesn't have all the CTL predictions but 
 * only epitopes, I chose to generate the results programmatically instead of
 * the usual SQL way.
 * 
 * The results ctl_low_cleavage_binders are loaded to the temporary table tblpool3 to run the topN SQL query
 * 
 */


public class Pool3Generator {
	
	private static PropertiesHelper properties = new PropertiesHelper();
	
	//Delimiter used in CSV file
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	//CSV file header for CTL
	private static final Object [] FILE_HEADER = {"uniprot_code","allele","peptide","position","rank","mhcScore",
		"IC50Score","tapScore","cleavageScore","combinedScore","rankPercentage","binderStatus","source"};


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
				
		//Create the CSVFormat object
        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
        //initialize the CSVParser object
        CSVParser parser = new CSVParser(new FileReader(properties.getValue("inputPath") +"mhc_binders_selected_alleles.csv"), format);
         
        List<BinderData> mhcBinders = new ArrayList<BinderData>();
        
        for(CSVRecord record : parser){
        	BinderData binderData = new BinderData();
        	binderData.setUniprot_code(record.get("UNIPROT_CODE"));
        	binderData.setProteinName(record.get("NAME"));
        	binderData.setAllele(record.get("ALLELE"));
        	binderData.setPeptide(record.get("PEPTIDE"));
        	binderData.setStartPosition(Integer.valueOf(record.get("POSITION")));
        	binderData.setMhcScore(Float.valueOf(record.get("MHC_SCORE")));
        	binderData.setIC50Score(Float.valueOf(record.get("IC50_SCORE")));
        	binderData.setBinder(record.get("BINDER_STATUS"));
        	mhcBinders.add(binderData);
        }
        //close the parser
        parser.close();
		
		//output the ctl peptide to other csv
    	FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		
		//Create the CSVFormat object with "\n" as a record delimiter
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
        try{
        	String csvFileName = "ctl_low_cleavage_binders.csv";
    		File fileName = new File(properties.getValue("csvPath") + csvFileName);
        	//initialize FileWriter object
			fileWriter = new FileWriter(fileName);
			
			//initialize CSVPrinter object 
	        csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
	        
	        //Create CSV file header
	        csvFilePrinter.printRecord(FILE_HEADER);
	        
	        for(BinderData binderPeptide : mhcBinders){
	        	
	        	NetPanData netPanData = findMatchingFile(binderPeptide.getProteinName(), binderPeptide.getUniprot_code(),binderPeptide.getAllele(),binderPeptide.getPeptide(),binderPeptide.getStartPosition());
	        	CTLPeptideData ctlPeptide = (CTLPeptideData) netPanData.getSpecificPeptideData(binderPeptide.getPeptide(), binderPeptide.getStartPosition());
	        	
	        	//allele,peptide,position,rank,mhcScore,IC50Score,tapScore,cleavageScore, combinedScore, rankPercentage,binderStatus,source
	        	//Test the Object ref??????
	        	List<Object> ctlDataRecord = new ArrayList<Object>();
	        	ctlDataRecord.add(binderPeptide.getUniprot_code());
	        	ctlDataRecord.add(binderPeptide.getAllele());
	        	ctlDataRecord.add(ctlPeptide.getPeptide());
	        	ctlDataRecord.add(ctlPeptide.getStartPosition());
	        	ctlDataRecord.add(ctlPeptide.getRank());
	        	ctlDataRecord.add(ctlPeptide.getMhcScore());
	        	ctlDataRecord.add(ctlPeptide.getIC50Score());
	        	ctlDataRecord.add(ctlPeptide.getTapScore());
	        	ctlDataRecord.add(ctlPeptide.getCleavageScore());
	        	ctlDataRecord.add(ctlPeptide.getCombinedScore());
	        	ctlDataRecord.add(ctlPeptide.getRankPercentage());
	        	ctlDataRecord.add(ctlPeptide.getBindingLevel());
	        	ctlDataRecord.add(netPanData.getFastaFileName());
	        	csvFilePrinter.printRecord(ctlDataRecord);
	        }
	     
        }
        catch(Exception e){
        	System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
        }
        
        finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
			}
		}
     
      
	}


	private static NetPanData findMatchingFile(String proteinName, String uniprot_code, String allele,
			String peptide, int startPosition) throws Exception {
		
		String inputFileName = proteinName+"_"+uniprot_code;
		
		//find the matching file
        File outDir = new File(properties.getValue("tumorAntigensOutputPathCTL")+ proteinName +"/selectedAlleles/");
        PredictionType type = PredictionType.CTL;
        NetPanDataBuilder builder = new NetPanDataBuilder(type);
        
    	return builder.buildFileData(inputFileName, allele, outDir);
		 			
	}

}
