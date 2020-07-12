package uk.ac.bbk.cryst.sequenceanalysis.main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;

import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.common.SequenceAnalysisProperties;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceComparator;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceFactory;
import uk.ac.bbk.cryst.sequenceanalysis.util.CustomLogger;

public class SequenceScannerMain {

	static SequenceAnalysisProperties properties = new SequenceAnalysisProperties();
	static SequenceFactory sequenceFactory = new SequenceFactory();
	static int kmer = 9;
	static boolean isMatch = false;// pos 2 and 9 do not have to match so
									// cond=false
	static List<Integer> positions = Arrays.asList(1,4,6,9);
	static String sequenceFileFullPath;
	static String compareFileFullPath;
	static FastaFileType inputType;
	static FastaFileType compareType;
	static String outputPath;

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
		inputType = FastaFileType.UNIPROT;
		compareType = FastaFileType.UNIPROT;
		sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
		compareFileFullPath = properties.getValue("compareFileFullPath");
		outputPath = properties.getValue("outputPath");
		scanFile();
		// scanPeptide("YYYSYQHFY");

	}

	static void scanFile() throws IOException {

		SequenceComparator sequenceComparator = new SequenceComparator(inputType,compareType);

		File sequenceFile = new File(sequenceFileFullPath);
		List<Sequence> seq1List = sequenceFactory.getSequenceList(sequenceFile, inputType);

		File compareFile = new File(compareFileFullPath);
		List<Sequence> seq2List = sequenceFactory.getSequenceList(compareFile, compareType);

		System.out.println("INPUT/COMPARE FILES:" + sequenceFile.getName() + "/" + compareFile.getName());

		try {
			CustomLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}

		for (Sequence seq1 : seq1List) {
			sequenceComparator.findAndPrintMatches(seq1, seq2List, outputPath, positions, isMatch, kmer);
		}

	}

	static void scanPeptide(String peptideSequence) throws IOException {

		String tmpPath = properties.getValue("tmpPath");
		String tmpSeqFileFullContent = ">sp|" + "temp|temp" + "\n" + peptideSequence;
		String tmpFileName = "temp.fasta";

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(inputType);
		sequenceComparator.setCompareFileType(compareType);

		File tmpSeqFile = new File(tmpPath + tmpFileName);
		FileUtils.writeStringToFile(tmpSeqFile, tmpSeqFileFullContent, CharEncoding.UTF_8);
		Sequence seq1 = sequenceFactory.getSequenceList(tmpSeqFile, inputType).get(0);

		File compareFile = new File(compareFileFullPath);
		List<Sequence> seq2List = sequenceFactory.getSequenceList(compareFile, compareType);

		try {
			CustomLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}

		sequenceComparator.findAndPrintMatches(seq1, seq2List, outputPath, positions, isMatch, kmer);

	}

}
