package uk.ac.bbk.cryst.sequenceanalysis.main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;

import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.common.SequenceAnalysisProperties;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceComparator;
import uk.ac.bbk.cryst.sequenceanalysis.util.CustomLogger;

public class SequenceScannerMain {

	static SequenceAnalysisProperties properties = new SequenceAnalysisProperties();
	static int kmer = 9;
	static boolean isMatch = false;// pos 2 and 9 do not have to match so cond=false
	static List<Integer> positions = Arrays.asList(1,2,3,9);

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

		scanFile();
		//scanPeptide();

	}

	static void scanFile() throws IOException {
		String fileName = "ebv_P03206.fasta";

		File sequenceFile = new File(properties.getValue("sequencePath") + fileName);
		String comparePath = properties.getValue("comparePath");
		String outputPath = properties.getValue("outputPath");

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(FastaFileType.UNIPROT);
		sequenceComparator.setCompareFileType(FastaFileType.UNIPROT);

		try {
			CustomLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}

		sequenceComparator.runMatchFinder(sequenceFile, comparePath, outputPath, positions, isMatch, kmer);

	}

	static void scanPeptide() throws IOException {

		String comparePath = properties.getValue("comparePath");
		String outputPath = properties.getValue("outputPath");

		String peptideSequence = "YYYSYQHFY";
		String tmpPath = properties.getValue("tmpPath");
		String tmpSeqFileFullContent = ">sp|" + "temp|temp" + "\n" + peptideSequence;
		String tmpFileName = "temp.fasta";

		File tmpSeqFile = new File(tmpPath + tmpFileName);
		FileUtils.writeStringToFile(tmpSeqFile, tmpSeqFileFullContent, CharEncoding.UTF_8);

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(FastaFileType.UNIPROT);
		sequenceComparator.setCompareFileType(FastaFileType.UNIPROT);

		try {
			CustomLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}

		sequenceComparator.runMatchFinder(tmpSeqFile, comparePath, outputPath, positions, isMatch, kmer);

	}

}
