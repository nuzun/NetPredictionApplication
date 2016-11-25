package uk.ac.bbk.cryst.netprediction.main;

import java.util.logging.Level;

import uk.ac.bbk.cryst.netprediction.service.NovelSurfaceAnalyzer;

public class NovelSurfaceProgram {

	public static void main(String[] args) throws Exception {

		NovelSurfaceAnalyzer novelSurfaceAnalyzer = new NovelSurfaceAnalyzer(Level.INFO);
		novelSurfaceAnalyzer.generateSequenceAndScoreFiles();
		novelSurfaceAnalyzer.runEliminate();

	}

}
