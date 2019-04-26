package uk.ac.bbk.cryst.netprediction.main;

import java.io.IOException;

import uk.ac.bbk.cryst.netprediction.service.AlloimmunityAnalyzerClassII;

public class AlloimmunityProgram {

	public static void main(String[] args) {
		// Call analyzer
		AlloimmunityAnalyzerClassII alloimmunityAnalyzer;
		try {
			alloimmunityAnalyzer = new AlloimmunityAnalyzerClassII();
			alloimmunityAnalyzer.generateOriginalEndogeneousSequenceScoreFiles();
			alloimmunityAnalyzer.runEliminate();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
