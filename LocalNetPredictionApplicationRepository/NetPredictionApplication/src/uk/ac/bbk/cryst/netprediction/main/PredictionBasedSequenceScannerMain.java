package uk.ac.bbk.cryst.netprediction.main;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.service.PredictionBasedSequenceScanner;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;

public class PredictionBasedSequenceScannerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		PredictionBasedSequenceScanner ps =  new PredictionBasedSequenceScanner(PredictionType.CTLPAN,FastaFileType.UNIPROT, FastaFileType.UNIPROT);
		ps.scanProteome();
	}

}
