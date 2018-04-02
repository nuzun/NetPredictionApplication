package uk.ac.bbk.cryst.netprediction.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.service.PredictionBasedSequenceScanner;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;

public class PredictionBasedSequenceScannerMain {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		PrintStream out = new PrintStream(new FileOutputStream("/home/nuzun/git/LocalNetPredictionApplicationRepository/NetPredictionApplication/data/console_output.txt"));
		System.setOut(out);
		System.out.println(new Date().toString());
		
		PredictionBasedSequenceScanner ps = new PredictionBasedSequenceScanner(PredictionType.CTLPAN,
				FastaFileType.UNIPROT, FastaFileType.UNIPROT,9);
		ps.scanProteome();
		
		System.out.println(new Date().toString());
	}

}
