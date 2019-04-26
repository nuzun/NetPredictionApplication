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
			analyzer.setDonorHlaId("HLA-B44:02");//donor
			//analyzer.setHlaA1("HLA-A02:03");// recipient
			//analyzer.setHlaA2("HLA-A29:02");// coloured allele square
			analyzer.setHlaB1("HLA-B44:03");//recipient
			analyzer.setHlaB2("HLA-B35:01");//coloured allele square
			
			analyzer.generateOriginalEndogeneousSequenceScoreFiles();
			analyzer.runEliminate();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
