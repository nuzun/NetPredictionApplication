package uk.ac.bbk.cryst.netprediction.main;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.service.MHCINovelSurfaceResultsProcessor;

public class AlloimmunityNovelSurfaceAnalyzerMain {

	public static void main(String[] args) throws Exception {
		try {

			//boolean proteomeScanning = false;
			MHCINovelSurfaceResultsProcessor processort = new MHCINovelSurfaceResultsProcessor(true,
					PredictionType.CTLPAN); // createVariantFile prints square
											// based stats in the const
			processort.process();
			
			MHCINovelSurfaceResultsProcessor processorf = new MHCINovelSurfaceResultsProcessor(false,
					PredictionType.CTLPAN); // createVariantFile prints square
											// based stats in the const
			processorf.process();

		}

		catch (Exception e) {
			throw e;
		}

		finally {

		}
	}

}
