package uk.ac.bbk.cryst.netprediction.main;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.service.MHCINovelSurfaceResultsProcessor;

public class AlloimmunityNovelSurfaceAnalyzerMain {

	public static void main(String[] args) throws Exception {
		try {

			boolean proteomeScanning = false;
			MHCINovelSurfaceResultsProcessor processor = new MHCINovelSurfaceResultsProcessor(proteomeScanning,
					PredictionType.CTLPAN); // createVariantFile prints square
											// based stats in the const

		}

		catch (Exception e) {
			throw e;
		}

		finally {

		}
	}

}
