package uk.ac.bbk.cryst.netprediction.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;
import uk.ac.bbk.cryst.netprediction.service.NetPanDataBuilder;

public class OutPathProcessor {
	static PropertiesHelper properties = new PropertiesHelper();

	public static void main(String[] args) throws Exception {

		File pathToRead = new File(properties.getValue("testOutputFullPathMHCII"));
		PredictionType type = PredictionType.MHCII;
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
		printAll(netPanDataList);
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

}
