package uk.ac.bbk.cryst.sequenceanalysis.main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.common.SequenceAnalysisProperties;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceComparator;
import uk.ac.bbk.cryst.sequenceanalysis.util.CustomLogger;

public class SequenceScannerMain {

	/**
	 * 
	 * put your first file into the input/ put your other files to compare into
	 * input/compareDir
	 * 
	 * Compare input and compare folders sequence files position by position.
	 * Used mainly for proteome scanning.
	 * 
	 * @param args
	 * @throws Exception
	 *
	 */
	public static void main(String[] args) throws Exception {

		int kmer = 9;
		boolean isMatch = false;// pos 2 and 9 do not have to match so
								// cond=false
		SequenceAnalysisProperties properties = new SequenceAnalysisProperties();
		List<Integer> positions = Arrays.asList(2, 9);

		File sequencePath = new File(properties.getValue("sequencePath"));
		String comparePath = properties.getValue("comparePath");
		String outputPath = properties.getValue("outputPath");

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(FastaFileType.UNIPROT);
		sequenceComparator.setCompareFileType(FastaFileType.ENSEMBLPEP);

		try {
			CustomLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}

		for (final File fileEntry : sequencePath.listFiles()) {
			sequenceComparator.runMatchFinder(fileEntry, comparePath, outputPath, positions, isMatch, kmer);
		}

	}

}
