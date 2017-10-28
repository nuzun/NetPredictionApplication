package uk.ac.bbk.cryst.netprediction.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;
import uk.ac.bbk.cryst.netprediction.model.MHCIIPeptideData;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;
import uk.ac.bbk.cryst.netprediction.model.NovelPeptideSurface;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;
import uk.ac.bbk.cryst.netprediction.util.CustomLogger;
import uk.ac.bbk.cryst.netprediction.util.FileHelper;
import uk.ac.bbk.cryst.netprediction.util.NetPanCmd;
import uk.ac.bbk.cryst.netprediction.util.PeptideDataHelper;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceComparator;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceFactory;

/**
 * Assuming initial score files are already generated for the full sequence
 * 
 * @author naz
 *
 */
public class NovelSurfaceAnalyzer {
	// parameters
	int nMer;
	int IC50_threshold;
	String sequenceFileName;
	String scoreCode;
	PredictionType type;
	List<String> variants;
	List<Integer> anchorPositions;
	StringBuilder strForNovelFile;

	PropertiesHelper properties;
	SequenceFactory sequenceFactory;

	String variantSequencePath;
	String tmpSequencePath;
	String proteomeSequencePath;

	String alleleFileFullPath;
	String sequenceFileFullPath;
	String compareFileFullPath;
	String mutationFileFullPath;

	String variantOutputFullPath;
	String endogenousOutputFullPath;
	String proteomeOutputFullPath;

	String novelSurfacesFileFullPath;

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public String getNovelSurfacesFileFullPath() {
		return novelSurfacesFileFullPath;
	}

	public String getMutationFileFullPath() {
		return mutationFileFullPath;
	}

	public String getProteomeSequencePath() {
		return proteomeSequencePath;
	}

	public String getTmpSequencePath() {
		return tmpSequencePath;
	}

	public String getProteomeOutputFullPath() {
		return proteomeOutputFullPath;
	}

	public String getVariantSequencePath() {
		return variantSequencePath;
	}

	public String getAlleleFileFullPath() {
		return alleleFileFullPath;
	}

	public String getSequenceFileFullPath() {
		return sequenceFileFullPath;
	}

	public String getCompareFileFullPath() {
		return compareFileFullPath;
	}

	public String getVariantOutputFullPath() {
		return variantOutputFullPath;
	}

	public String getEndogenousOutputFullPath() {
		return endogenousOutputFullPath;
	}

	public int getnMer() {
		return nMer;
	}

	public String getSequenceFileName() {
		return sequenceFileName;
	}

	public String getScoreCode() {
		return scoreCode;
	}

	public PredictionType getType() {
		return type;
	}

	public List<String> getVariants() {
		return variants;
	}

	public PropertiesHelper getProperties() {
		return properties;
	}

	public SequenceFactory getSequenceFactory() {
		return sequenceFactory;
	}

	public List<Integer> getAnchorPositions() {
		return anchorPositions;
	}

	public void setnMer(int nMer) {
		this.nMer = nMer;
	}

	public void setSequenceFileName(String sequenceFileName) {
		this.sequenceFileName = sequenceFileName;
	}

	public void setScoreCode(String scoreCode) {
		this.scoreCode = scoreCode;
	}

	public void setType(PredictionType type) {
		this.type = type;
	}

	public void setVariants(List<String> variants) {
		this.variants = variants;
	}

	public void setAnchorPositions(List<Integer> anchorPositions) {
		this.anchorPositions = anchorPositions;
	}

	public int getIC50_threshold() {
		return IC50_threshold;
	}

	public void setIC50_threshold(int iC50_threshold) {
		IC50_threshold = iC50_threshold;
	}

	public NovelSurfaceAnalyzer(Level logLevel, PredictionType type) throws IOException {
		// parameters
		nMer = 15;
		IC50_threshold = 1000;
		anchorPositions = Arrays.asList(1, 4, 6, 9);
		scoreCode = "0"; // MHC(1) or comb (0) used for CTL only
		strForNovelFile = new StringBuilder();
		this.setType(type);
		CustomLogger.setup();
		LOGGER.setLevel(logLevel);
		//

		variants = new ArrayList<String>();
		properties = new PropertiesHelper();
		sequenceFactory = new SequenceFactory();

		sequenceFileName = properties.getValue("sequenceFileName");
		alleleFileFullPath = properties.getValue("alleleFileFullPath");
		sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
		compareFileFullPath = properties.getValue("compareFileFullPath");
		tmpSequencePath = properties.getValue("tmpSequencePath");
		proteomeSequencePath = properties.getValue("proteomeSequencePath");
		variantSequencePath = properties.getValue("variantSequencePath");

		variantOutputFullPath = this.getType().equals(PredictionType.MHCIIPAN31)
				? properties.getValue("variantOutputFullPathMHCIIPan")
				: properties.getValue("variantOutputFullPathMHCII");
		endogenousOutputFullPath = this.getType().equals(PredictionType.MHCIIPAN31)
				? properties.getValue("endogenousOutputFullPathMHCIIPan")
				: properties.getValue("endogenousOutputFullPathMHCII");
		proteomeOutputFullPath = this.getType().equals(PredictionType.MHCIIPAN31)
				? properties.getValue("proteomeOutputFullPathMHCIIPan")
				: properties.getValue("proteomeOutputFullPathMHCII");

		mutationFileFullPath = properties.getValue("mutationFileFullPath");
		File mutationFile = new File(mutationFileFullPath);
		readMutationFile(mutationFile);

		novelSurfacesFileFullPath = properties.getValue("novelSurfacesFileFullPath");
	}

	private void readMutationFile(File mutationFile) throws FileNotFoundException {

		String line = "";

		BufferedReader br = new BufferedReader(new FileReader(mutationFile));
		try {
			while ((line = br.readLine()) != null && !line.trim().equals("")) {
				variants.add(line.trim());
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void generateSequenceAndScoreFiles() throws Exception {

		LOGGER.info("Enter generateSequenceAndScoreFiles");
		// Read the alleles straight from region/group of alleles file
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();

		// Read the sequence file test_P00451.fasta
		File sequenceFile = new File(this.getSequenceFileFullPath());
		Sequence inputSequence = this.getSequenceFactory().getSequenceList(sequenceFile, FastaFileType.UNIPROT).get(0);

		// for each allele, generate the score file for the full sequence once
		String variantOutputFileFullPath = "";
		for (String allele : groupData.getAlleleMap().keySet()) {
			variantOutputFileFullPath = this.getVariantOutputFullPath()
					+ FilenameUtils.removeExtension(this.getSequenceFileName()) + "_" + allele + ".txt";

			File scoreFileToCreate = new File(variantOutputFileFullPath);
			if (!scoreFileToCreate.exists()) {

				NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
						sequenceFile.getPath(), variantOutputFileFullPath);
			}
		}

		// Generate endogeneous versions for each variant and allele
		for (String variant : this.getVariants()) {
			String[] parts = variant.split("-");
			String from = parts[0];
			int variantPosition = Integer.valueOf(parts[1]) + 19; // add for the
																	// numbering
																	// issue
			String to = parts[2];

			String subSequence = inputSequence.getPanningSequence(variantPosition, this.getnMer());

			// generate endogeneous sequence file
			StringBuilder endSeq = new StringBuilder(subSequence);
			int charIndex = variantPosition <= this.getnMer() ? variantPosition - 1 : this.getnMer() - 1;
			if (endSeq.charAt(charIndex) != from.charAt(0)) {
				LOGGER.severe("Variant residue does not match to the full sequence." + variant);
				continue;
			}

			endSeq.setCharAt(charIndex, to.charAt(0));
			String endFilefullContent = ">sp|" + inputSequence.getProteinId() + "|" + variantPosition + " " + from + "_"
					+ to + "\n" + endSeq.toString();
			String endFileName = this.getSequenceFileName() + "_" + variantPosition + from + to; // testProtein_P00451.fasta_20AC
			File endSequenceFile = new File(this.getVariantSequencePath() + endFileName);

			if (!endSequenceFile.exists()) {
				FileHelper.writeToFile(endSequenceFile, endFilefullContent);
			}

			// for each allele, generate the scores for endogeneous
			String endogeneousOutputFileFullPath = "";
			for (String allele : groupData.getAlleleMap().keySet()) {

				endogeneousOutputFileFullPath = this.getEndogenousOutputFullPath()
						+ FilenameUtils.removeExtension(this.getSequenceFileName()) + "_" + allele + ".txt" + "_"
						+ variantPosition + from + to;

				File scoreFileToCreate = new File(endogeneousOutputFileFullPath);
				if (!scoreFileToCreate.exists()) {
					NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
							endSequenceFile.getPath(), endogeneousOutputFileFullPath);
				}
			}
		} // variants

		LOGGER.info("Exit generateSequenceAndScoreFiles");
	}

	public void runEliminate() throws Exception {

		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();

		NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());

		for (String variant : this.getVariants()) {
			String[] parts = variant.split("-");
			String from = parts[0];
			int variantPosition = Integer.valueOf(parts[1]) + 19; // add for the
																	// numbering
																	// issue;
			String to = parts[2];

			for (String allele : groupData.getAlleleMap().keySet()) {
				List<MHCIIPeptideData> remainingPeptides = new ArrayList<MHCIIPeptideData>();

				String fileName = FilenameUtils.removeExtension(this.getSequenceFileName()) + "_" + allele + ".txt";
				NetPanData variantNetPanData = builder
						.buildSingleFileData(new File(this.getVariantOutputFullPath() + fileName));

				for (PeptideData peptide : variantNetPanData.getPanningPeptideList(variantPosition)) {
					MHCIIPeptideData therPeptide = (MHCIIPeptideData) peptide;

					// continue if the core contains the variant and binds
					// strong, note that both startPos and coreStartPos starts
					// from 0, variantPos starts from 1
					int start = therPeptide.getStartPosition() + therPeptide.getCoreStartPosition();
					int variantIndexAtCore = variantPosition - start - 1; // 0-8

					if ((9 > variantIndexAtCore) && (variantIndexAtCore >= 0)
							&& (therPeptide.getIC50Score() < this.getIC50_threshold())) {

						// check endo criteria
						String endoFileName = fileName + "_" + variantPosition + from + to;
						NetPanData endoNetPanData = builder
								.buildSingleFileData(new File(this.getEndogenousOutputFullPath() + endoFileName));

						// continue if the core is the same with any endo core
						StringBuilder endoCore = new StringBuilder(therPeptide.getCorePeptide());
						endoCore.setCharAt(variantIndexAtCore, to.charAt(0));
						List<MHCIIPeptideData> endoMatchList = endoNetPanData
								.getSpecificPeptideDataByCore(endoCore.toString());

						if (endoMatchList.size() > 0) {
							int allWeak = 1;
							for (MHCIIPeptideData endoMatch : endoMatchList) {
								// Check if we have at least one good binder
								if (endoMatch.getIC50Score() < this.getIC50_threshold()) {
									// check MHC/TCR
									allWeak = 0;
									if (this.getAnchorPositions().contains(variantIndexAtCore + 1)) {
										// eliminate it is not novel, mutation
										// is on 1,4,6,9 you have protection
									} else {
										// add newPeptide to the list for
										// proteome check
										remainingPeptides.add(therPeptide);
										break;
									}
								}
							} // for
								// not a single good binder then check proteome
							if (allWeak == 1) {
								remainingPeptides.add(therPeptide);
							}
						} else {
							remainingPeptides.add(therPeptide);
						}
					}
				}

				/*
				 * helpful output
				 */
				LOGGER.info(variant + "\n" + allele);

				StringBuilder sb = new StringBuilder();
				for (MHCIIPeptideData p : remainingPeptides) {
					sb.append(p.toStringLessFields() + "\n");
				}
				LOGGER.info(sb.toString());

				// start proteome check:
				runProteomeCheck(allele, variant, remainingPeptides);
			}
		} // variant

		// write the final novel data
		writeToFinalOutputFile();
	}

	private void runProteomeCheck(String allele, String variant, List<MHCIIPeptideData> remainingPeptides)
			throws Exception {

		int coreNMer = 9;
		boolean isMatch = false;// positions do not have to match so false
		String[] parts = variant.split("-");
		// String from = parts[0];
		int variantPosition = Integer.valueOf(parts[1]) + 19; // add for the
																// numbering
																// issue;
		// String to = parts[2];

		List<Sequence> matchList = new ArrayList<Sequence>();
		NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());
		Map<MHCIIPeptideData, MHCIIPeptideData> matchMap = new HashMap<MHCIIPeptideData, MHCIIPeptideData>();
		Map<String, MHCIIPeptideData> tempMap = new HashMap<String, MHCIIPeptideData>();

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(FastaFileType.UNIPROT);
		sequenceComparator.setCompareFileType(FastaFileType.ENSEMBLPEP);// compare
																		// proteome
																		// type

		// read the compareDir and all the files as there might be more than one
		List<Sequence> seq2List = new ArrayList<>();
		File compareFileFullPath = new File(this.getCompareFileFullPath());

		List<Sequence> tempList = this.getSequenceFactory().getSequenceList(compareFileFullPath,
				FastaFileType.ENSEMBLPEP);// compare proteome type
		seq2List.addAll(tempList);

		NovelPeptideSurface novel = new NovelPeptideSurface();
		novel.setAllele(allele);
		novel.setVariant(variant);

		MHCIIPeptideData pep1 = new MHCIIPeptideData();
		MHCIIPeptideData pep2 = new MHCIIPeptideData();
		pep1 = PeptideDataHelper.getTheStrongestBinderII(remainingPeptides);

		for (MHCIIPeptideData remaining : remainingPeptides) {

			// create a temporary fasta file from peptides
			String tmpSeqFileFullContent = ">sp|" + remaining.getCorePeptide() + "|temp" + "\n"
					+ remaining.getCorePeptide();
			String tmpFileName = remaining.getCorePeptide() + ".fasta"; // testProtein_P00451.fasta_20AC
			File tmpSeqFile = new File(this.getTmpSequencePath() + tmpFileName);

			if (tmpSeqFile.exists()) {
				matchMap.put(remaining, tempMap.get(remaining.getCorePeptide()));
				continue;
			}

			FileHelper.writeToFile(tmpSeqFile, tmpSeqFileFullContent);

			// this returns only panning seq of the matching proteome seq
			// 9mer match start position is the center and we get 15mer panning
			// sequence
			// aaaaaaaaaaaaaaMAAAAAAAAaaaaaa
			matchList = sequenceComparator.runMatchFinder(tmpSeqFile, seq2List, this.getAnchorPositions(), isMatch,
					coreNMer);

			List<MHCIIPeptideData> matchingPeptides = new ArrayList<MHCIIPeptideData>();
			// run predictions on the matching proteome sequences
			for (Sequence seq : matchList) {

				String proteomeSeqFileFullContent = ">sp|" + seq.getProteinId() + "\n" + seq.getSequence();
				String proteomeSeqFileName = seq.getProteinId() + ".fasta";
				File proteomeSeqFile = new File(this.getProteomeSequencePath() + proteomeSeqFileName);

				if (!proteomeSeqFile.exists()) {
					FileHelper.writeToFile(proteomeSeqFile, proteomeSeqFileFullContent);
				}

				String proteomeOutputFileFullPath = this.getProteomeOutputFullPath()
						+ FilenameUtils.removeExtension(proteomeSeqFileName) + "_" + allele + ".txt";
				File proteomeScoreFileToCreate = new File(proteomeOutputFileFullPath);
				if (!proteomeScoreFileToCreate.exists()) {
					NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
							proteomeSeqFile.getPath(), proteomeOutputFileFullPath);
				}
				NetPanData protNetPanData = builder.buildSingleFileData(new File(proteomeOutputFileFullPath));

				/*
				 * helpful output
				 */
				StringBuilder sb = new StringBuilder();
				sb.append(seq.getProteinId() + "\n");

				/* helpful output */
				if (StringUtils.isNotEmpty(seq.getProteinId())
						&& seq.getProteinId().matches("(ENSP00000471364|ENSP00000327895|ENSP00000470213|"
								+ "ENSP00000409446|ENSP00000469822|ENSP00000389153|ENSP00000469039).*")) {
					// do nothing
				} else if (StringUtils.isNotEmpty(seq.getProteinId())
						&& (seq.getProteinId().startsWith("ENSP00000353393")
								|| seq.getProteinId().startsWith("P00451"))) {
					// get the match start position from new protein id
					// for variant 593 you have 612 as new variant pos meaning
					// 611 index starting from 0
					// if that value => than match start pos and <= the end of
					// the match then we have our match
					// in the variant area of the factor8.
					int protMatchStartPos = Integer
							.valueOf(seq.getProteinId().substring(seq.getProteinId().lastIndexOf("_") + 1));
					if (protMatchStartPos <= (variantPosition - 1) && (variantPosition - 1) <= protMatchStartPos + 8) {
						continue;
					}
					for (MHCIIPeptideData pep : protNetPanData.getSpecificPeptideDataByMaskedCore(
							remaining.getCorePeptide(), this.getAnchorPositions(), isMatch)) {
						sb.append(pep.toStringLessFields() + "\n");
					}
				} else {
					for (MHCIIPeptideData pep : protNetPanData.getSpecificPeptideDataByMaskedCore(
							remaining.getCorePeptide(), this.getAnchorPositions(), isMatch)) {
						sb.append(pep.toStringLessFields() + "\n");
					}
				}
				LOGGER.info(sb.toString());

				// pass the variant position as the panning position to ignore
				// the peptides in that section for F8 also ignore other
				// transcripts
				// F8-001:ENSP00000353393, ENSP00000471364 = they are the same
				// F8-003:ENSP00000327895, ENSP00000470213
				// F8-004:ENSP00000409446, ENSP00000469822
				// F8-005:ENSP00000389153, ENSP00000469039
				if (StringUtils.isNotEmpty(seq.getProteinId())
						&& seq.getProteinId().matches("(ENSP00000471364|ENSP00000327895|ENSP00000470213|"
								+ "ENSP00000409446|ENSP00000469822|ENSP00000389153|ENSP00000469039).*")) {
					// do nothing
				} else if (StringUtils.isNotEmpty(seq.getProteinId())
						&& (seq.getProteinId().startsWith("ENSP00000353393")
								|| seq.getProteinId().startsWith("P00451"))) {
					// get the match start position from new protein id
					// for variant 593 you have 612 as new variant pos meaning
					// 611 index starting from 0
					// if that value => than match start pos and <= the end of
					// the match then we have our match
					// in the variant area of the factor8.
					int protMatchStartPos = Integer
							.valueOf(seq.getProteinId().substring(seq.getProteinId().lastIndexOf("_") + 1));
					if (protMatchStartPos <= (variantPosition - 1) && (variantPosition - 1) <= protMatchStartPos + 8) {
						continue;
					}
					matchingPeptides.addAll(protNetPanData.getSpecificPeptideDataByMaskedCore(
							remaining.getCorePeptide(), this.getAnchorPositions(), isMatch));
				} else {
					matchingPeptides.addAll(protNetPanData.getSpecificPeptideDataByMaskedCore(
							remaining.getCorePeptide(), this.getAnchorPositions(), isMatch));
				}
			} // for matching proteome sequences

			MHCIIPeptideData bestMatch = (MHCIIPeptideData) PeptideDataHelper.getTheStrongestBinderII(matchingPeptides);
			matchMap.put(remaining, bestMatch);
			tempMap.put(remaining.getCorePeptide(), bestMatch);
		}

		/*
		 * helpful output
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("-------------------------PRINTING MATCH MAP-----------------------------\n");

		for (MHCIIPeptideData key : matchMap.keySet()) {
			sb.append("REMAINING=" + key.toStringLessFields() + "\n");

			MHCIIPeptideData match = matchMap.get(key);
			if (match != null) {
				sb.append("MATCH=" + match.toStringLessFields() + "\n");
			} else {
				sb.append("NO MATCH\n");
			}

		}
		sb.append("-------------------------END MATCH MAP------------------------------------");
		LOGGER.info(sb.toString());

		int fullProtection = 1;

		for (MHCIIPeptideData key : matchMap.keySet()) {
			MHCIIPeptideData match = matchMap.get(key);
			if (match != null) {
				if (match.getIC50Score() > this.getIC50_threshold()) {
					fullProtection = 0;
					if (pep2.getPeptide() == null) {
						pep2 = key;
					} else {
						if (key.getIC50Score() < pep2.getIC50Score()) {
							pep2 = key;
						}
					}
				}
			} else {
				fullProtection = 0;
				if (pep2.getPeptide() == null) {
					pep2 = key;
				} else {
					if (key.getIC50Score() < pep2.getIC50Score()) {
						pep2 = key;
					}
				}
			}
		} // for matchmap

		novel.setPeptide1(pep1);
		novel.setPeptide2(pep2);

		if (remainingPeptides.isEmpty()) {
			novel.setColour("black");
		} else {
			if (fullProtection == 1) {
				// novel.setColour("pep1color/grey");
				novel.setColour((int) Math.ceil(pep1.getIC50Score()) + "/grey");
			} else {
				// novel.setColour("pep1color/pep2color");
				novel.setColour((int) Math.ceil(pep1.getIC50Score()) + "/" + (int) Math.ceil(pep2.getIC50Score()));
			}
		}

		FileUtils.cleanDirectory(new File(this.getTmpSequencePath()));

		sb = new StringBuilder();
		sb.append(novel.getVariant() + ",");
		sb.append(novel.getAllele() + ",");
		if (novel.getPeptide1() != null) {
			sb.append(((MHCIIPeptideData) novel.getPeptide1()).getPeptide() + ",");
			sb.append(((MHCIIPeptideData) novel.getPeptide1()).getCorePeptide() + ",");
			sb.append(((MHCIIPeptideData) novel.getPeptide1()).getIC50Score() + ",");
		} else {
			sb.append("" + ",");
			sb.append("" + ",");
			sb.append("" + ",");
		}
		if (novel.getPeptide2() != null) {
			sb.append(((MHCIIPeptideData) novel.getPeptide2()).getPeptide() + ",");
			sb.append(((MHCIIPeptideData) novel.getPeptide2()).getCorePeptide() + ",");
			sb.append(((MHCIIPeptideData) novel.getPeptide2()).getIC50Score() + ",");
		} else {
			sb.append("" + ",");
			sb.append("" + ",");
			sb.append("" + ",");
		}
		sb.append(novel.getColour());
		sb.append("\n");

		strForNovelFile.append(sb.toString());

		/* helpful output */
		LOGGER.info("NOVEL:" + sb.toString()
				+ "******************************************************************************");

	}

	private void writeToFinalOutputFile() throws IOException {
		LOGGER.info("Enter writeToFinalOutputFile");

		String header = "Variant,Allele,Peptide_1,CorePeptide_1,IC50_1,Peptide_2,CorePeptide_2,IC50_2,Colour";
		String newLine = "\n";

		File file = new File(this.getNovelSurfacesFileFullPath());
		if (!file.exists()) {
			Path path = Paths.get(this.getNovelSurfacesFileFullPath());
			Files.write(path, header.getBytes());
			Files.write(path, newLine.getBytes(), StandardOpenOption.APPEND);
		}

		Path path = Paths.get(this.getNovelSurfacesFileFullPath());
		Files.write(path, strForNovelFile.toString().getBytes(), StandardOpenOption.APPEND);

		LOGGER.info("Exit writeToFinalOutputFile");
	}

}
