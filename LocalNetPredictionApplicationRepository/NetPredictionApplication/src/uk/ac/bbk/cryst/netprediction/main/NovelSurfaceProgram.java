package uk.ac.bbk.cryst.netprediction.main;

import java.util.logging.Level;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.service.NovelSurfaceAnalyzer;

public class NovelSurfaceProgram {

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out.println("Please enter the prediction type: MHCII or MHCIIPAN31");
			System.exit(0);
		}

		String typeStr = args[0];

		try {
			PredictionType type = PredictionType.valueOf(typeStr);

			NovelSurfaceAnalyzer novelSurfaceAnalyzer = new NovelSurfaceAnalyzer(Level.INFO, type);
			novelSurfaceAnalyzer.generateSequenceAndScoreFiles();
			novelSurfaceAnalyzer.runEliminate();
		} catch (IllegalArgumentException e) {
			System.out.println("Please enter the prediction type: MHCII or MHCIIPAN31");
			System.exit(0);
		}

	}

}
