package uk.ac.bbk.cryst.netprediction.main;

import java.io.File;
import java.util.List;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;
import uk.ac.bbk.cryst.netprediction.service.NetPanDataBuilder;

public class PositionSpecificBinderAnalyzer {

	static PropertiesHelper properties = new PropertiesHelper();
	
	/**
	 * @param args
	 * @throws Exception 
	 * 
	 * read the directory with score files and check
	 * certain position intervals for binders of a specific protein and allelegroup
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String proteinName = "survivin";
		File pathToRead = new File(properties.getValue("tumorAntigensOutputPathCTL") + proteinName + "/selectedAlleles");
		PredictionType type = PredictionType.CTL;
		
		//we are doing x-9 because we are using the position numbers from 1k and cosmic which
		//start from 1 not 0 like our prediction results.
		//external_position-9 <= startpos < external_position
		int external_position = 5;
		//int external_position_2 = 249;
				
		//String seqFileName = proteinName + "_129";
		NetPanDataBuilder builder = new NetPanDataBuilder(type);
		List<NetPanData> netPanDataList = builder.buildFileData(pathToRead);
		
		//check netpandatalist for specific locations now 
		for(NetPanData netPanData: netPanDataList){
			for(PeptideData peptideData : netPanData.getPeptideList()){
				if((peptideData.getStartPosition() >= (external_position-9) && peptideData.getStartPosition() < external_position)
						//|| (peptideData.getStartPosition() >= (external_position_2-9) && peptideData.getStartPosition() < external_position_2)
				  ){
					if(peptideData.isEpitope()){
						System.out.println(netPanData.getAllele() + "\t" + peptideData.toStringNoHeader());
					}
				}
			}
		}
	}
}
