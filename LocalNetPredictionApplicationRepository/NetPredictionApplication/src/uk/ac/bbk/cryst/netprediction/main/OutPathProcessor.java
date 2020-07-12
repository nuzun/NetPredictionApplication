package uk.ac.bbk.cryst.netprediction.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;
import uk.ac.bbk.cryst.netprediction.service.NetPanDataBuilder;

public class OutPathProcessor {
	static PropertiesHelper properties = new PropertiesHelper();

	public static void main(String[] args) throws Exception {

		String sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
		File sequenceFile = new File(sequenceFileFullPath);
		// ebv_P03206.fasta
		String fileName = FilenameUtils.removeExtension(sequenceFile.getName());
		
		File pathToRead = new File(properties.getValue("tmpOutputPathMHCIIPan") + fileName + "/");
		PredictionType type = PredictionType.MHCIIPAN31;
		NetPanDataBuilder builder = new NetPanDataBuilder(type);
		List<NetPanData> netPanDataList = new ArrayList<>();

		// read the testProtein_P00451 directory
		for (final File fileEntry : pathToRead.listFiles()) {
			if (fileEntry.isDirectory()) {
				continue;
			}

			else {
				NetPanData netPanData = builder.buildSingleFileData(fileEntry);
				netPanDataList.add(netPanData);
				
			}
		} // for
		//printEpitopes(netPanDataList);
		//printAll(netPanDataList);
		printBelowThreshold(netPanDataList);
	}

	private static void printEpitopes(List<NetPanData> netPanDataList) {
		for (NetPanData netPanData : netPanDataList) {
			List<PeptideData> peptideList = netPanData.getEpitopes();
			System.out.println(netPanData.getFastaFileName() + " " + netPanData.getAllele());
			for (PeptideData peptide : peptideList) {
				System.out.println(peptide.toString());
			}
		}
	}

	private static void printAll(List<NetPanData> netPanDataList) {
		for (NetPanData netPanData : netPanDataList) {
			List<PeptideData> peptideList = netPanData.getPeptideList();
			System.out.println(netPanData.getFastaFileName() + " " + netPanData.getAllele());
			for (PeptideData peptide : peptideList) {
				System.out.println(peptide.toString());
			}
		}
	}
	
	private static void printBelowThreshold(List<NetPanData> netPanDataList) {
		int counter;
		
		for (NetPanData netPanData : netPanDataList) {
			counter = 0;
			List<PeptideData> peptideList = netPanData.getPeptideList();
			System.out.println(netPanData.getFastaFileName() + " " + netPanData.getAllele());
			System.out.println("*******************************************");
			for (PeptideData peptide : peptideList) {
				if(peptide.getIC50Score() <= 100){
					counter ++;
					//System.out.println(peptide.toString());
					//System.out.println(peptide.getPeptide());
				}
			}
			System.out.println("COUNTER:" + counter + "   ----------------------------");
			System.out.println();
		}
	}
}
