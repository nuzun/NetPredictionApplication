package uk.ac.bbk.cryst.netprediction.main;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.service.ProteomeScanner;

public class ProteomeScannerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ProteomeScanner ps =  new ProteomeScanner(PredictionType.CTLPAN);
		ps.scanProteome();
	}

}
