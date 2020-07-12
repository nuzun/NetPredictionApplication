package uk.ac.bbk.cryst.sequenceanalysis.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.MatchData;
import uk.ac.bbk.cryst.sequenceanalysis.model.MatchDataClassII;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;

public class SequenceComparator {

	String printFields = "Protein1,Position1,Peptide1,Protein2,Position2,Peptide2,Seq2Description";
	private final static Logger LOGGER = Logger.getLogger("SequenceAnalysisLog");
	List<Sequence> seq2List = new ArrayList<Sequence>();
	FastaFileType inputFileType;
	FastaFileType compareFileType;

	public SequenceComparator() {

	}
	
	public SequenceComparator(FastaFileType input, FastaFileType compare) {
		this.setInputFileType(input);
		this.setCompareFileType(compare);
	}

	public FastaFileType getInputFileType() {
		return inputFileType;
	}

	public void setInputFileType(FastaFileType inputFileType) {
		this.inputFileType = inputFileType;
	}

	public FastaFileType getCompareFileType() {
		return compareFileType;
	}

	public void setCompareFileType(FastaFileType compareFileType) {
		this.compareFileType = compareFileType;
	}

	/**
	 * We pass the ready list read from the comparePath
	 * 
	 * @param inputFile
	 * @param seq2List
	 * @param positions
	 * @param isMatch
	 * @param kmer this is mainly 9mer as core
	 * panningMer is 9mer or 15mer depending on the method
	 * @return
	 * @throws IOException
	 */
	public List<Sequence> runMatchFinder(File inputFile, List<Sequence> seq2List, List<Integer> positions,
			boolean isMatch, int kmer, int panningMer) throws IOException {
		List<Sequence> matchList = new ArrayList<Sequence>();

		SequenceFactory sequenceFactory = new SequenceFactory();
		// read the dir and the first file as we know there is only one for the
		// first file
		Sequence seq1 = sequenceFactory.getSequenceList(inputFile, inputFileType).get(0);

		// compare seq1 to each seq2,
		for (Sequence seq2 : seq2List) {

			List<MatchData> matchMap = SequenceComparator.generateMatchMap(seq1, seq2, positions, isMatch, kmer);

			if (SequenceComparator.isIdentical(seq1, seq2)) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = IDENTICAL");
				continue;
			}

			if (matchMap.size() == 0) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = NO MATCHES");
				continue;
			}

			// instead of adding the full sequence just get the panning sequence
			// name the sequence with its original name_match position
			for (MatchData match : matchMap) {
				int matchPosition = match.getPosition2();
				String panningSeq = seq2.getPanningSequence(matchPosition + 1, panningMer);// need
																					// to
																					// pass
																					// +1
																					// as
																					// the
																					// panning
																					// function
																					// starts
																					// from
																					// 1
				String newId = seq2.getProteinId() + "_" + matchPosition;
				Sequence newSeq = new Sequence(newId, panningSeq) {
				};
				matchList.add(newSeq);
			}

		}
		return matchList;

	}
	

	public List<Sequence> runMatchFinder(Sequence inputSequence, List<Sequence> seq2List, List<Integer> positions,
			boolean isMatch, int kmer, int panningMer) throws IOException {
		List<Sequence> matchList = new ArrayList<Sequence>();

		// compare seq1 to each seq2,
		for (Sequence seq2 : seq2List) {

			List<MatchData> matchMap = SequenceComparator.generateMatchMap(inputSequence, seq2, positions, isMatch, kmer);

			if (SequenceComparator.isIdentical(inputSequence, seq2)) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = IDENTICAL");
				continue;
			}

			if (matchMap.size() == 0) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = NO MATCHES");
				continue;
			}

			// instead of adding the full sequence just get the panning sequence
			// name the sequence with its original name_match position
			for (MatchData match : matchMap) {
				int matchPosition = match.getPosition2();
				String panningSeq = seq2.getPanningSequence(matchPosition + 1, panningMer);// need
																					// to
																					// pass
																					// +1
																					// as
																					// the
																					// panning
																					// function
																					// starts
																					// from
																					// 1
				String newId = seq2.getProteinId() + "_" + matchPosition;
				Sequence newSeq = new Sequence(newId, panningSeq) {
				};
				matchList.add(newSeq);
			}

		}
		return matchList;

	}
	

	/**
	 * Prints the results to a csv file
	 * This can be used to retrieve only coreMer or classI
	 */
	public void findAndPrintMatches(Sequence seq1, List<Sequence> seq2List, String resultsFilePath, List<Integer> positions,
			boolean isMatch, int kmer) throws IOException {
		
		// copy the results to a csv file in the csv path
		File outFile = new File(
				resultsFilePath + seq1.getProteinId() + "_" + kmer + "_vs_" + generatePositionStr(positions) + ".csv");

		try {
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
		} catch (Exception ex) {
			LOGGER.severe("Exception occurred creating the file:");
			ex.printStackTrace();
			throw new RuntimeException("Exception occurred creating the file");
		}

		// populate the match file
		FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(printFields);
		bw.newLine();

		// compare seq1 to each seq2,
		for (Sequence seq2 : seq2List) {

			List<MatchData> matchMap = SequenceComparator.generateMatchMap(seq1, seq2, positions, isMatch, kmer);

			if (SequenceComparator.isIdentical(seq1, seq2)) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = IDENTICAL");
				continue;
			}

			if (matchMap.size() == 0) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = NO MATCHES");
				continue;
			}

			for (MatchData match : matchMap) {
				bw.write(match.toStringOnlyValues());
				bw.newLine();
				//System.out.println(match.toStringOnlyValues());
			}

		}

		bw.close();
	}
	
	
	public void findAndPrintMatchesClassII(Sequence seq1, List<Sequence> seq2List, String outPath, List<Integer> positions,
			boolean isMatch, int kmer, int panningMer) throws IOException {
		
		// copy the results to a csv file in the csv path
		File outFile = new File(
				outPath + seq1.getProteinId() + "_" + panningMer + "_vs_" + generatePositionStr(positions) + ".csv");

		try {
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
		} catch (Exception ex) {
			LOGGER.severe("Exception occurred creating the file:");
			ex.printStackTrace();
			throw new RuntimeException("Exception occurred creating the file");
		}

		// populate the match file
		FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(printFields);
		bw.newLine();

		// compare seq1 to each seq2,
		for (Sequence seq2 : seq2List) {

			List<MatchDataClassII> matchMap = SequenceComparator.generateMatchMapClassII(seq1, seq2, positions, isMatch, kmer, panningMer);

			if (SequenceComparator.isIdentical(seq1, seq2)) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = IDENTICAL");
				continue;
			}

			if (matchMap.size() == 0) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = NO MATCHES");
				continue;
			}

			for (MatchDataClassII match : matchMap) {
				bw.write(match.toStringOnlyValues());
				bw.newLine();
				//System.out.println(match.toStringOnlyValues());
			}

		}

		bw.close();
	}

	/**
	 * This method only returns if we are only interested in coreMer matches
	 * or class I matches
	 * @param seq1
	 * @param seq2List
	 * @param positions
	 * @param isMatch
	 * @param kmer
	 * @return
	 * @throws IOException
	 */
	public List<MatchData> getMatchData(Sequence seq1, List<Sequence> seq2List, List<Integer> positions,
			boolean isMatch, int kmer) throws IOException {
		
		List<MatchData> matchMap = new ArrayList<>();
		List<MatchData> tmpMap = new ArrayList<>();
		// compare seq1 to each seq2,
		for (Sequence seq2 : seq2List) {

			tmpMap = SequenceComparator.generateMatchMap(seq1, seq2, positions, isMatch, kmer);

			if (SequenceComparator.isIdentical(seq1, seq2)) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = IDENTICAL");
				continue;
			}

			if (tmpMap.size() == 0) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = NO MATCHES");
				continue;
			}
			
			matchMap.addAll(tmpMap);
		}
		return matchMap;
		
	}
	
	
	/**
	 * This method will be used for ClassII as we need core match data
	 * as well as the panning 15mer for the matching data
	 * @param seq1
	 * @param seq2List
	 * @param positions
	 * @param isMatch
	 * @param coreNmer
	 * @param nMer
	 * @return
	 * @throws IOException
	 */
	public List<MatchDataClassII> getMatchData(Sequence seq1, List<Sequence> seq2List, List<Integer> positions,
			boolean isMatch, int coreNmer, int nMer) throws IOException {
		
		List<MatchDataClassII> matchMap = new ArrayList<>();
		List<MatchDataClassII> tmpMap = new ArrayList<>();
		// compare seq1 to each seq2,
		for (Sequence seq2 : seq2List) {

			tmpMap = SequenceComparator.generateMatchMapClassII(seq1, seq2, positions, isMatch, coreNmer,nMer);

			if (SequenceComparator.isIdentical(seq1, seq2)) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = IDENTICAL");
				continue;
			}

			if (tmpMap.size() == 0) {
				// LOGGER.info(seq1.getProteinId() + " vs " +
				// seq2.getProteinId() + " = NO MATCHES");
				continue;
			}
			
			matchMap.addAll(tmpMap);
		}
		return matchMap;
		
	}
	
	private static String generatePositionStr(List<Integer> positions) {
		String positionToStr = "";
		for (int pos : positions) {
			positionToStr = positionToStr + pos + "_";
		}
		return positionToStr;
	}

	/*
	 * int[] positions starts from 1. sequence positions start from 0. if
	 * condition true then positions have to match and others don't have to if
	 * condition false then positions don't have to match but others have to
	 */

	public static boolean isMatch(String peptide1, String peptide2, List<Integer> positions, boolean condition) {
		// TODO: AJS confirmed exact match for * but the characters like B
		// etc???
		if (condition == true) {
			for (int position : positions) {
				if (peptide1.charAt(position - 1) == peptide2.charAt(position - 1)) {
					continue;
				} else {
					return false;
				}
			}
			return true;
		}

		else {

			for (int i = 0; i < peptide1.length(); i++) {

				if (positions.contains(i + 1)) {
					continue;
				}
				if (peptide1.charAt(i) != peptide2.charAt(i)) {
					return false;
				} else {
					continue;
				}
			}

			return true;

		}
	}

	/**
	 * This method is the usual method to generate the 9mer/coreMer match data 
	 * position member here starts from 0.. maybe change this??? TODO
	 */
	public static List<MatchData> generateMatchMap(Sequence seq1, Sequence seq2, List<Integer> positions, boolean cond,
			int peptideLength) {

		List<MatchData> matchList = new ArrayList<MatchData>();

		Map<Integer, String> seq1Map = PeptideGenerator.getPositionPeptideMap(seq1, peptideLength);
		Map<Integer, String> seq2Map = PeptideGenerator.getPositionPeptideMap(seq2, peptideLength);

		for (Integer pos1 : seq1Map.keySet()) {
			String peptide1 = seq1Map.get(pos1);

			for (Integer pos2 : seq2Map.keySet()) {
				String peptide2 = seq2Map.get(pos2);

				if (isMatch(peptide1, peptide2, positions, cond)) {
					MatchData newMatch = new MatchData(seq1.getProteinId(), pos1, peptide1, seq2.getProteinId(), pos2,
							peptide2);
					matchList.add(newMatch);
				}
			}
		}

		return matchList;

	}
	
	/**
	 * We will use this method to capture the 15mer as we only match
	 * core 9mer for pattern search 
	 * @param seq1
	 * @param seq2
	 * @param positions
	 * @param cond
	 * @param peptideLength
	 * while getting the panning sequence we need to start from +1 as the panning
	 * sequence function assumes positions start from 1 not 0.
	 * position member here starts from 0.. I changed it at least to make it start from 1
	 * for the results/output maybe change this??? TODO
	 * @return
	 */
	public static List<MatchDataClassII> generateMatchMapClassII(Sequence seq1, Sequence seq2, List<Integer> positions, 
			boolean cond,int kMer, int nMer) {

		List<MatchDataClassII> matchList = new ArrayList<MatchDataClassII>();

		Map<Integer, String> seq1Map = PeptideGenerator.getPositionPeptideMap(seq1, kMer);
		Map<Integer, String> seq2Map = PeptideGenerator.getPositionPeptideMap(seq2, kMer);

		for (Integer pos1 : seq1Map.keySet()) {
			String peptide1 = seq1Map.get(pos1);

			for (Integer pos2 : seq2Map.keySet()) {
				String peptide2 = seq2Map.get(pos2);

				if (isMatch(peptide1, peptide2, positions, cond)) {
					MatchDataClassII newMatch = new MatchDataClassII(
							seq1.getProteinId(), pos1, peptide1,seq1.getPanningSequence(pos1+1, nMer),
							seq2.getProteinId(), pos2, peptide2,seq2.getPanningSequence(pos2+1, nMer));
					matchList.add(newMatch);
				}
			}
		}

		return matchList;

	}
	

	public static boolean isIdentical(Sequence seq1, Sequence seq2) {
		if (seq1.length() != seq2.length()) {
			return false;
		}
		for (int i = 0; i < seq1.getSequence().length(); i++) {
			if (seq1.getSequence().toCharArray()[i] == seq2.getSequence().toCharArray()[i]) {
				continue;
			} else {
				// System.out.print(i);
				return false;
			}
		}

		return true;
	}

}
