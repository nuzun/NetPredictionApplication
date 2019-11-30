package uk.ac.bbk.cryst.netprediction.main;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.service.MHCIIPanNovelSurfaceResultsProcessor;
import uk.ac.bbk.cryst.netprediction.service.MHCINovelSurfaceResultsProcessor;

public class AlloimmunityNovelSurfaceAnalyzerMain {

	public static void main(String[] args) throws Exception {
		try {

			
			//boolean proteomeScanning = false;
			PropertiesHelper properties = new PropertiesHelper();
			
			MHCIIPanNovelSurfaceResultsProcessor processort = new MHCIIPanNovelSurfaceResultsProcessor(true,
					PredictionType.MHCIIPAN31, properties.getValue("fileExtension")); // createVariantFile prints square
											// based stats in the const
			processort.process();
			
			MHCIIPanNovelSurfaceResultsProcessor processorf = new MHCIIPanNovelSurfaceResultsProcessor(false,
					PredictionType.MHCIIPAN31, properties.getValue("fileExtension")); // createVariantFile prints square
											// based stats in the const
			processorf.process();
			
			
			
			/*
			//boolean proteomeScanning = false;
			
			PropertiesHelper properties = new PropertiesHelper();
			
			MHCINovelSurfaceResultsProcessor processort = new MHCINovelSurfaceResultsProcessor(true,
					PredictionType.CTLPAN,properties.getValue("fileExtension")); // createVariantFile prints square
											// based stats in the const
			processort.process();
			
			MHCINovelSurfaceResultsProcessor processorf = new MHCINovelSurfaceResultsProcessor(false,
					PredictionType.CTLPAN,properties.getValue("fileExtension")); // createVariantFile prints square
											// based stats in the const
			processorf.process();
			*/

		}

		catch (Exception e) {
			throw e;
		}

		finally {

		}
	}

}
