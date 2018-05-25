package uk.ac.bbk.cryst.netprediction.main;

import java.io.IOException;

import uk.ac.bbk.cryst.netprediction.service.AlloimmunityAnalyzer;

public class AlloimmunityProgram {

	public static void main(String[] args) {
		// Call analyzer
		AlloimmunityAnalyzer alloimmunityAnalyzer;
		try {
			alloimmunityAnalyzer = new AlloimmunityAnalyzer();
			alloimmunityAnalyzer.generateOriginalEndogeneousSequenceScoreFiles();
			alloimmunityAnalyzer.runEliminate();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
