package uk.ac.bbk.cryst.netprediction.main;

import java.io.IOException;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.service.MHCIINovelSurfaceResultsProcessor;

public class PatientDataNovelSurfaceAnalyzerMain {

	public static void main(String[] args) throws IOException {

		try {

			boolean onlyDR = false;
			boolean protScan = true;
			MHCIINovelSurfaceResultsProcessor processor = new MHCIINovelSurfaceResultsProcessor(protScan, onlyDR,
					PredictionType.MHCII); //createVariantFile prints square based stats in the const

			processor.printRepresentativePatientStatistics();
			processor.printCategoricalNumbersComplex();
			processor.printVariousStatistics();

		}

		catch (Exception e) {
			throw e;
		}

		finally {

		}

	}

}
