package uk.ac.bbk.cryst.netprediction.main;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.service.MHCIIPanNovelSurfaceResultsProcessor;

public class AlloimmunityNovelSurfaceAnalyzerMain {

	public static void main(String[] args) throws Exception {
		try {

			//boolean proteomeScanning = false;
			MHCIIPanNovelSurfaceResultsProcessor processort = new MHCIIPanNovelSurfaceResultsProcessor(true,
					PredictionType.MHCIIPAN31); // createVariantFile prints square
											// based stats in the const
			processort.process();
			
			MHCIIPanNovelSurfaceResultsProcessor processorf = new MHCIIPanNovelSurfaceResultsProcessor(false,
					PredictionType.MHCIIPAN31); // createVariantFile prints square
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
