package uk.ac.bbk.cryst.netprediction.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;
import uk.ac.bbk.cryst.netprediction.util.NetPanCmd;

public class Program {

	/**
	 * @param args
	 *            Start position in the peptide starts from 0 for CTL. Start
	 *            position for a peptide sequence starts from 0 for NetMHCPan
	 * @throws IOException
	 *             HLA-DRB11101 for MHCII-2.2 DRB1_1101 for MHCIIPan-2.0
	 */
	public static void main(String[] args) throws IOException {

		PropertiesHelper properties = new PropertiesHelper();

		// parameters
		String peptideLength = "15";
		String scoreCode = "0"; // MHC(1) or comb (0) used for CTL only
		PredictionType type = PredictionType.MHCII;
		
		String outputFullPath = properties.getValue("testOutputFullPathMHCII");
		String alleleFileFullPath = properties.getValue("alleleFileFullPath");

		// Read the alleles from region/group of alleles file
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(alleleFileFullPath).getGroupData();

		// read the input sequence(s)
		try {
			String sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
			File sequenceFile = new File(properties.getValue("sequenceFileFullPath"));

			// testProtein_P00451
			String fileName = FilenameUtils.removeExtension(sequenceFile.getName());

			// for each allele, generate the scores
			for (String allele : groupData.getAlleleMap().keySet()) {

				String outputFileFullPath = outputFullPath + "/" + fileName + "_" + allele + ".txt";

				NetPanCmd.run(type, scoreCode, peptideLength, allele, sequenceFileFullPath, outputFileFullPath);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
