package uk.ac.bbk.cryst.netprediction.main;

import java.io.IOException;

import uk.ac.bbk.cryst.netprediction.service.AlloimmunityAnalyzerRoundTwo;

public class AlloimmunityRoundTwoProgram {

	public static void main(String[] args) {
		// Call analyzer
		AlloimmunityAnalyzerRoundTwo analyzer;
		try {
			// set the variant,variant heatmap and mhc1 alleles (LHN) files
			// accordingly
			analyzer = new AlloimmunityAnalyzerRoundTwo();
			analyzer.setDonorHlaId("HLA-A02:01");
			analyzer.setHlaA1("HLA-A02:02");// recipient
			analyzer.setHlaA2("HLA-A68:01");// coloured allele square
			//analyzer.setHlaB1("");
			//analyzer.setHlaB2("HLA-B35:01");
			
			analyzer.generateOriginalEndogeneousSequenceScoreFiles();
			analyzer.runEliminate();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
