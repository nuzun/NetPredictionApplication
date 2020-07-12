package uk.ac.bbk.cryst.netprediction.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.service.PredictionBasedSequenceScanner;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;

/* 1. Find matching 9mers
 * 2. Run prediction on 9mers only to see if both left and right
 * bind with enough affinity
 *  
 * The MHC score does not change if you run the panning sequence
 * of the 9mer on CTLPAN or if you just run the 9mer instead
 * that's why we only ran the matching peptides on NetCTLPan
 * and did not consider the surrounding residues.
 * In class II analysis we need the 15mer not core 9mer so match object will not
 * suit our needs. We needed a new match object then we can use a similar flow for class II.
 * */
public class PredictionBasedSequenceScannerMain {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PropertiesHelper properties = new PropertiesHelper();
		String consoleOutputFullPath = properties.getValue("consoleOutputFileFullPath");

		//PrintStream out = new PrintStream(new FileOutputStream("/home/nuzun/git/LocalNetPredictionApplicationRepository/NetPredictionApplication/data/console_output.txt"));
		PrintStream out = new PrintStream(new FileOutputStream(consoleOutputFullPath));
		System.setOut(out);
		System.out.println(new Date().toString());
		
		PredictionBasedSequenceScanner ps = new PredictionBasedSequenceScanner(PredictionType.MHCIIPAN31,
				FastaFileType.UNIPROT, FastaFileType.ENSEMBLPEP,9,15);
		ps.scan();
		
		System.out.println(new Date().toString());
	}

}
