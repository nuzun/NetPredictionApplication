package uk.ac.bbk.cryst.sequenceanalysis.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
	static boolean isMatch = false;// pos 2 and 9 do not have to match so cond=false
	static List<Integer> positions = Arrays.asList(2,9);
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
		compareType = FastaFileType.ENSEMBLPEP;
		sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
		compareFileFullPath = properties.getValue("compareFileFullPath");
		outputPath = properties.getValue("outputPath");
		scanFile();
		//scanPeptide();

	}

	static void scanFile() throws IOException {

		File sequenceFile = new File(sequenceFileFullPath); 

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(inputType);
		sequenceComparator.setCompareFileType(compareType);

		List<Sequence> seq2List = new ArrayList<>();
		File compareFile = new File(compareFileFullPath);
		List<Sequence> tempList = sequenceFactory.getSequenceList(compareFile, compareType);// compare
																							// proteome																										// type
		seq2List.addAll(tempList);
		try {
			CustomLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}

		sequenceComparator.runMatchFinder(sequenceFile, seq2List, outputPath, positions, isMatch, kmer);

	}

	static void scanPeptide() throws IOException {

		String peptideSequence = "YYYSYQHFY";
		String tmpPath = properties.getValue("tmpPath");
		String tmpSeqFileFullContent = ">sp|" + "temp|temp" + "\n" + peptideSequence;
		String tmpFileName = "temp.fasta";

		File tmpSeqFile = new File(tmpPath + tmpFileName);
		FileUtils.writeStringToFile(tmpSeqFile, tmpSeqFileFullContent, CharEncoding.UTF_8);

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(inputType);
		sequenceComparator.setCompareFileType(compareType);
		
		List<Sequence> seq2List = new ArrayList<>();
		File compareFile = new File(compareFileFullPath);
		List<Sequence> tempList = sequenceFactory.getSequenceList(compareFile, compareType);// compare
																							// proteome																										// type
		seq2List.addAll(tempList);

		try {
			CustomLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}

		sequenceComparator.runMatchFinder(tmpSeqFile, seq2List, outputPath, positions, isMatch, kmer);

	}

}
