package uk.ac.bbk.cryst.netprediction.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;
import uk.ac.bbk.cryst.netprediction.service.NetPanDataBuilder;

public class AllBindersCsvFileGenerator {
	
	static PropertiesHelper properties = new PropertiesHelper();

	/**
	 * @param args
	 * @throws Exception 
	 * 
	 * Generates allbinders csv file to upload to the database
	 */
	public static void main(String[] args) throws Exception {
		
		//parameters
		File pathToRead = new File(properties.getValue("proteomeOutputPathCTL"));
		PredictionType type = PredictionType.CTL;
		
		NetPanDataBuilder builder = new NetPanDataBuilder(type);
		 
		//proteomeMHC/albumin/selectedAlleles/albumin_P02768_HLA-A01:01.txt
		
		//read the albumin directory
		for(final File fileEntry : pathToRead.listFiles()){
			if(fileEntry.isDirectory()){
				//read the allele group directory: selectedAlleles
				for(final File groupPath : fileEntry.listFiles()){
					String groupName = groupPath.getName();
					//read prediction files in the group:albumin_P02768_HLA-A01:01.txt 
					for(final File predictionFile : groupPath.listFiles()){
						String predictionFileName = getFastaFileName(predictionFile);
						List<NetPanData> netPanDataList = builder.buildFileData(groupPath);
						generateBindersFile(type, netPanDataList, predictionFileName, groupName);
					}
				}//for
			}//if
		}//for
	}//main
	
	/**
	 * selectedAlleles_albumin_P02768_allbinders_CTL.csv
	 * @param type
	 * @param netPanDataList
	 * @param sequenceFileName
	 * @param groupDirName
	 * @throws IOException
	 */
	private static void generateBindersFile(PredictionType type,List<NetPanData> netPanDataList, String sequenceFileName,String groupDirName) throws IOException{
		
		String idCode = sequenceFileName.split("_")[1]; //P02768 ??need to think about variants here?????
		File file = new File(properties.getValue("csvPath")+ groupDirName + "_" + sequenceFileName + "_allbinders_"+type+".csv");

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		String headerLine = "";
		
		switch(type){
		case CTL:
			headerLine = "uniprot_code,allele,peptide,position,rank,mhcScore,IC50Score,tapScore,cleavageScore, combinedScore, rankPercentage,binderStatus,source";
			break;
		case MHCI:
			headerLine = "uniprot_code,allele,peptide,position,corePeptide,rank,mhcScore,IC50Score,binderStatus,source";
			break;
		case MHCII:
			headerLine = "uniprot_code,allele,peptide,position,corePeptide,rank,mhcScore,IC50Score,binderStatus,source";
			break;
		default:
			headerLine = "uniprot_code,allele,peptide,position,corePeptide,rank,mhcScore,IC50Score,binderStatus,source";
			break;
		}
		
		bw.write(headerLine);
		bw.newLine();
		
		for(NetPanData netPanData: netPanDataList){
			for(PeptideData peptideData : netPanData.getPeptideList()){
				
				if(peptideData.isEpitope()){
					bw.write(idCode+",");
					bw.write(netPanData.getAllele()+",");
					bw.write(peptideData.toStringNoHeader(","));
					//source
					bw.write(","+netPanData.getFastaFileName());
					bw.newLine();
				}	
			}
		}
		bw.close();	
	}
	
	private static String getFastaFileName(File fileEntry) {
		//mega3_P43357_HLA-A01:01
		String fileName = fileEntry.getName();
		String str[] = fileName.split("_");
		
		return str[0]+"_"+str[1];
	}
}
