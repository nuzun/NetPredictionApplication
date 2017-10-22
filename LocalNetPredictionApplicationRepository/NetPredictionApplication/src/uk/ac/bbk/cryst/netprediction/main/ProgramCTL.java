package uk.ac.bbk.cryst.netprediction.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;
import uk.ac.bbk.cryst.netprediction.util.NetPanCmd;

public class ProgramCTL {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		PropertiesHelper properties = new PropertiesHelper();

		// parameters
		String peptideLength = "9";
		String scoreCode = "0"; // MHC(1) or comb (0) used for CTL only
		PredictionType type = PredictionType.CTLPAN;

		String outputPath = properties.getValue("outputPathCTLPan");
		String alleleFileFullPath = properties.getValue("alleleFileFullPath");
		// Read the alleles from region/group of alleles file
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(alleleFileFullPath).getGroupData();

		// read the input sequence(s)
		try {
			String sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
			File sequenceFile = new File(sequenceFileFullPath);

			// ebv_P03206.fasta
			String fileName = FilenameUtils.removeExtension(sequenceFile.getName());

			// for each allele, generate the scores
			for (String allele : groupData.getAlleleMap().keySet()) {

				String outputFileFullPath = outputPath + fileName + "/" + fileName + "_" + allele + ".txt";

				NetPanCmd.run(type, scoreCode, peptideLength, allele, sequenceFileFullPath, outputFileFullPath);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
