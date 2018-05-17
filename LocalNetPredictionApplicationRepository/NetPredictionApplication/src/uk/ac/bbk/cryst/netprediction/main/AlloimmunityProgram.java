package uk.ac.bbk.cryst.netprediction.main;

import java.io.IOException;

import uk.ac.bbk.cryst.netprediction.service.AlloimmunityAnalyzer;

public class AlloimmunityProgram {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Call analyzer
		AlloimmunityAnalyzer alloimmunityAnalyzer;
		try {
			alloimmunityAnalyzer = new AlloimmunityAnalyzer();
			alloimmunityAnalyzer.generateOriginalEndogeneousSequenceScoreFiles();
			alloimmunityAnalyzer.runEliminate();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
