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

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;
import uk.ac.bbk.cryst.netprediction.model.CTLPanPeptideData;
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

public class AlloimmunityAnalyzerClassI {

	StringBuilder novelEntry;
	int nMer;
	int IC50_threshold;
	String scoreCode; // MHC(1) or comb (0)
	String hlaFileName;
	PredictionType predictionType; // CTLPAN
	List<Integer> anchorPositions;
	List<String> variants; // sequence differences between A*02:01 and A*02:02
	List<String> excluded;

	PropertiesHelper properties;
	SequenceFactory sequenceFactory;
	AlleleGroupData alleleGroupData;

	String variantSequencePath;
	String tmpSequencePath;
	String proteomeSequencePath;

	String alleleFileFullPath;
	String hlaFileFullPath;
	String compareFileFullPath;
	String variantFileFullPath;
	String excludeFileFullPath;

	String variantOutputFullPath;
	String endogenousOutputFullPath;
	String proteomeOutputFullPath;

	String novelSurfacesFileFullPath;

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public AlloimmunityAnalyzerClassI() throws IOException {
		novelEntry = new StringBuilder();

		this.setProperties(new PropertiesHelper());
		this.setnMer(9);
		this.setScoreCode("0");
		this.setIC50_threshold(500);
		this.setHlaFileName(properties.getValue("hlaFileName"));
		this.setPredictionType(PredictionType.CTLPAN);
		this.setAnchorPositions(Arrays.asList(2, 9));

		this.setVariants(new ArrayList<String>());
		this.setExcluded(new ArrayList<String>());
		this.setSequenceFactory(new SequenceFactory());

		this.setVariantSequencePath(properties.getValue("variantSequencePath"));
		this.setTmpSequencePath(properties.getValue("tmpSequencePath"));
		this.setProteomeSequencePath(properties.getValue("proteomeSequencePath"));

		this.setAlleleFileFullPath(properties.getValue("alleleFileFullPath"));
		this.setHlaFileFullPath(properties.getValue("hlaFileFullPath"));
		this.setCompareFileFullPath(properties.getValue("compareFileFullPath"));
		this.setVariantFileFullPath(properties.getValue("variantFileFullPath")); // residue
																					// diff
		this.setExcludeFileFullPath(properties.getValue("excludeFileFullPath"));

		// Read variant file and assign it to variants list
		File variantFile = new File(this.getVariantFileFullPath());
		readVariantFile(variantFile);

		// Read excluded file and assign it to the excluded list
		File excludedFile = new File(this.getExcludeFileFullPath());
		readExcludedFile(excludedFile);

		this.setVariantOutputFullPath(properties.getValue("variantOutputFullPathCTLPan"));
		this.setEndogenousOutputFullPath(properties.getValue("endogenousOutputFullPathCTLPan"));
		this.setProteomeOutputFullPath(properties.getValue("proteomeOutputFullPathCTLPan"));
		this.setNovelSurfacesFileFullPath(properties.getValue("novelSurfacesFileFullPath"));
		this.alleleGroupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();

		CustomLogger.setup();
		LOGGER.setLevel(Level.INFO);

	}

	public void generateOriginalEndogeneousSequenceScoreFiles() throws IOException {
		LOGGER.entering("AlloimmunityAnalyzerClassI", "generateOriginalEndogeneousSequenceScoreFiles");
		// Read the alleles straight from region/group of alleles file

		// Read the original HLA file HLA-A*02:01
		File hlaFile = new File(this.getHlaFileFullPath());
		Sequence inputSequence = this.getSequenceFactory().getSequenceList(hlaFile, FastaFileType.HLA).get(0);

		// For each allele generate the score file for the full sequence once
		String outputFileFullPath = "";
		for (String allele : alleleGroupData.getAlleleMap().keySet()) {
			outputFileFullPath = this.getVariantOutputFullPath() + FilenameUtils.removeExtension(this.getHlaFileName())
					+ "_" + allele + ".txt";

			File scoreFile = new File(outputFileFullPath);
			if (!scoreFile.exists()) {

				NetPanCmd.run(this.getPredictionType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
						hlaFile.getPath(), outputFileFullPath);
			}
		}

		// Generate endogeneous versions for each variant and allele R-593-C
		for (String variant : this.getVariants()) {
			String[] parts = variant.split("-");
			String from = parts[0]; // R
			int variantPosition = Integer.valueOf(parts[1]); // 593
			String to = parts[2]; // C

			// Generate endogeneous sequence file where the C exists instead of
			// R
			String subSequence = inputSequence.getPanningSequence(variantPosition, this.getnMer());
			StringBuilder endogeneousSequence = new StringBuilder(subSequence);

			int charIndex = variantPosition <= this.getnMer() ? variantPosition - 1 : this.getnMer() - 1;
			if (endogeneousSequence.charAt(charIndex) != from.charAt(0)) {
				LOGGER.severe("Variant residue does not match to the full sequence." + variant);
				continue;
			}

			endogeneousSequence.setCharAt(charIndex, to.charAt(0));
			String endogeneousFilefullContent = ">sp|" + inputSequence.getProteinId() + "|" + variantPosition + " "
					+ from + "_" + to + "\n" + endogeneousSequence.toString();
			String endogeneousFileName = this.getHlaFileName() + "_" + variantPosition + from + to; // testProtein_P99999.fasta_20AC
			File endogeneousSequenceFile = new File(this.getVariantSequencePath() + endogeneousFileName);

			if (!endogeneousSequenceFile.exists()) {
				FileHelper.writeToFile(endogeneousSequenceFile, endogeneousFilefullContent);
			}

			// For each allele, generate the scores for endogeneous
			String endogeneousOutputFileFullPath = "";

			for (String allele : alleleGroupData.getAlleleMap().keySet()) {
				endogeneousOutputFileFullPath = this.getEndogenousOutputFullPath()
						+ FilenameUtils.removeExtension(this.getHlaFileName()) + "_" + allele + ".txt" + "_"
						+ variantPosition + from + to;

				File scoreFile = new File(endogeneousOutputFileFullPath);
				if (!scoreFile.exists()) {
					NetPanCmd.run(this.getPredictionType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
							endogeneousSequenceFile.getPath(), endogeneousOutputFileFullPath);
				}
			}
		} // variants
		LOGGER.exiting("AlloimmunityAnalyzerClassI", "generateOriginalEndogeneousSequenceScoreFiles");
	}

	public void runEliminate() throws Exception {
		LOGGER.entering("AlloimmunityAnalyzerClassI", "runEliminate");
		NetPanDataBuilder builder = new NetPanDataBuilder(this.getPredictionType());

		// Go through each residue difference
		for (String variant : this.getVariants()) {
			String[] parts = variant.split("-");
			String from = parts[0]; // R
			int variantPosition = Integer.valueOf(parts[1]); // 593
			String to = parts[2]; // C

			// And each allele
			for (String allele : alleleGroupData.getAlleleMap().keySet()) {
				List<PeptideData> remainingPeptides = new ArrayList<>();

				String fileName = FilenameUtils.removeExtension(this.getHlaFileName()) + "_" + allele + ".txt";
				NetPanData variantNetPanData = builder
						.buildSingleFileData(new File(this.getVariantOutputFullPath() + fileName));

				for (PeptideData peptide : variantNetPanData.getPanningPeptideList(variantPosition)) {
					CTLPanPeptideData therapeuticPeptide = (CTLPanPeptideData) peptide;

					// Continue if the therapeuticPeptide (donor/therapeutic)
					// binds strong
					if (therapeuticPeptide.getIC50Score() < this.getIC50_threshold()) {
						// Check endogeneous/recipient criteria
						String endogeneousFileName = fileName + "_" + variantPosition + from + to;
						NetPanData endogeneousNetPanData = builder.buildSingleFileData(
								new File(this.getEndogenousOutputFullPath() + endogeneousFileName));

						StringBuilder endogeneousPeptide = new StringBuilder(therapeuticPeptide.getPeptide());
						int charIndex = variantPosition - therapeuticPeptide.getStartPosition() - 1;
						endogeneousPeptide.setCharAt(charIndex, to.charAt(0));
						List<PeptideData> endogeneousMatchList = endogeneousNetPanData
								.getSpecificPeptideData(endogeneousPeptide.toString());

						if (endogeneousMatchList.size() > 0) {
							int allWeak = 1;
							for (PeptideData endogeneousMatch : endogeneousMatchList) {
								if (endogeneousMatch.getIC50Score() < this.getIC50_threshold()) {
									// check MHC/TCR
									allWeak = 0;
									if (this.getAnchorPositions().contains(charIndex + 1)) {
										// eliminate it is not novel, variation
										// is on 2,9 MHC facing you have
										// protection from TCR
									} else {
										// add newPeptide to the list for
										// proteome check
										remainingPeptides.add(therapeuticPeptide);
										break;
									}
								}
							} // for
								// not a single good binder then check proteome
							if (allWeak == 1) {
								remainingPeptides.add(therapeuticPeptide);
							}
						} else {
							remainingPeptides.add(therapeuticPeptide);
						}

					}
				} // peptide

				/*********** helpful output ***********************/
				LOGGER.log(Level.INFO, "VARIANT:" + variant + " " + "ALLELE:" + allele);
				StringBuilder sb = new StringBuilder();
				for (PeptideData p : remainingPeptides) {
					sb.append(p.toString() + "\n");
				}
				LOGGER.info("REMAINING PEPTIDES:" + sb.toString());
				/************************************************/

				// Start proteome check
				runProteomeCheck(allele, variant, remainingPeptides);

			} // allele

		} // variant

		// write the final novel data
		writeToFinalOutputFile();
		LOGGER.exiting("AlloimmunityAnalyzerClassI", "runEliminate");

	}

	private void runProteomeCheck(String allele, String variant, List<PeptideData> remainingPeptides) throws Exception {
		LOGGER.entering("AlloimmunityAnalyzerClassI", "runProteomeCheck");

		boolean isMatch = false;// positions do not have to match so false
		// String[] parts = variant.split("-");
		// String from = parts[0];
		// int variantPosition = Integer.valueOf(parts[1]);
		// String to = parts[2];

		List<Sequence> matchList = new ArrayList<Sequence>();
		NetPanDataBuilder builder = new NetPanDataBuilder(this.getPredictionType());
		Map<PeptideData, PeptideData> matchMap = new HashMap<>();
		Map<String, PeptideData> tempMap = new HashMap<>();
		SequenceComparator sequenceComparator = new SequenceComparator(FastaFileType.HLA, FastaFileType.ENSEMBLPEP);

		// read the compare directory and all the files - usually it is just one
		List<Sequence> rightList = new ArrayList<>();
		File compareFileFullPath = new File(this.getCompareFileFullPath());
		List<Sequence> tempList = this.getSequenceFactory().getSequenceList(compareFileFullPath,
				FastaFileType.ENSEMBLPEP);// compare proteome type
		rightList.addAll(tempList);

		NovelPeptideSurface novel = new NovelPeptideSurface();
		novel.setAllele(allele);
		novel.setVariant(variant);

		PeptideData peptide1 = new CTLPanPeptideData();
		PeptideData peptide2 = new CTLPanPeptideData();
		peptide1 = PeptideDataHelper.getTheStrongestBinder(remainingPeptides);

		for (PeptideData remaining : remainingPeptides) {
			// Create a temporary fasta file from peptides in order to run a
			// comparison
			String tmpSeqFileFullContent = ">HLA:" + remaining.getPeptide() + " temp" + "\n" + remaining.getPeptide();
			String tmpFileName = remaining.getPeptide() + ".fasta"; // testProtein_P00451.fasta_20AC
			File tmpSeqFile = new File(this.getTmpSequencePath() + tmpFileName);

			if (tmpSeqFile.exists()) {
				matchMap.put(remaining, tempMap.get(remaining.getPeptide()));
				continue;
			}

			FileHelper.writeToFile(tmpSeqFile, tmpSeqFileFullContent);

			// Returns matching proteome subsequences
			matchList = sequenceComparator.runMatchFinder(tmpSeqFile, rightList, this.getAnchorPositions(), isMatch,
					this.getnMer(), this.getnMer());
			List<PeptideData> matchingPeptides = new ArrayList<>();

			// Run predictions on the matching proteome sequences
			for (Sequence matchSequence : matchList) {

				if (!excluded.contains(matchSequence.getProteinId().split("_")[0])) {

					String proteomeSeqFileFullContent = ">sp|" + matchSequence.getProteinId() + "\n"
							+ matchSequence.getSequence();
					String proteomeSeqFileName = matchSequence.getProteinId() + ".fasta";
					File proteomeSeqFile = new File(this.getProteomeSequencePath() + proteomeSeqFileName);

					if (!proteomeSeqFile.exists()) {
						FileHelper.writeToFile(proteomeSeqFile, proteomeSeqFileFullContent);
					}

					String proteomeOutputFileFullPath = this.getProteomeOutputFullPath()
							+ FilenameUtils.removeExtension(proteomeSeqFileName) + "_" + allele + ".txt";
					File proteomeScoreFileToCreate = new File(proteomeOutputFileFullPath);
					if (!proteomeScoreFileToCreate.exists()) {
						NetPanCmd.run(this.getPredictionType(), this.getScoreCode(), String.valueOf(this.getnMer()),
								allele, proteomeSeqFile.getPath(), proteomeOutputFileFullPath);
					}
					NetPanData protNetPanData = builder.buildSingleFileData(new File(proteomeOutputFileFullPath));

					/*********** helpful output ***********************/
					StringBuilder sb = new StringBuilder();
					sb.append(matchSequence.getProteinId() + "\n");

					// exclude some from proteome check
					if (!excluded.contains(matchSequence.getProteinId().split("_")[0])) {
						for (PeptideData pep : protNetPanData.getSpecificPeptideDataByMaskedMatch(
								remaining.getPeptide(), this.getAnchorPositions(), isMatch)) {
							sb.append(pep.toString());
						}
					}
					LOGGER.info(sb.toString());

					/************************************************/

					// exclude some from proteome check
					if (!excluded.contains(matchSequence.getProteinId().split("_")[0])) {
						matchingPeptides.addAll(protNetPanData.getSpecificPeptideDataByMaskedMatch(
								remaining.getPeptide(), this.getAnchorPositions(), isMatch));
					}
					
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(matchSequence.getProteinId() + "-exc" + "\n");

					LOGGER.info(sb.toString());

				}

			} // proteome matches

			PeptideData bestMatch = (PeptideData) PeptideDataHelper.getTheStrongestBinder(matchingPeptides);
			matchMap.put(remaining, bestMatch);
			tempMap.put(remaining.getPeptide(), bestMatch);

		} // remaining peptides

		printMatchMap(matchMap);

		// Set the novel surface object
		int fullProtection = 1;
		for (PeptideData key : matchMap.keySet()) {
			PeptideData match = matchMap.get(key);
			if (match != null) {
				if (match.getIC50Score() > this.getIC50_threshold()) {
					fullProtection = 0;
					if (peptide2.getPeptide() == null) {
						peptide2 = key;
					} else {
						if (key.getIC50Score() < peptide2.getIC50Score()) {
							peptide2 = key;
						}
					}
				}
			} else {
				fullProtection = 0;
				if (peptide2.getPeptide() == null) {
					peptide2 = key;
				} else {
					if (key.getIC50Score() < peptide2.getIC50Score()) {
						peptide2 = key;
					}
				}
			}
		} // matchmap

		novel.setPeptide1(peptide1);
		novel.setPeptide2(peptide2);

		if (remainingPeptides.isEmpty()) {
			novel.setColour("black");
		} else {
			if (fullProtection == 1) {
				// novel.setColour("pep1color/grey");
				novel.setColour((int) Math.ceil(peptide1.getIC50Score()) + "/grey");
			} else {
				// novel.setColour("pep1color/pep2color");
				novel.setColour(
						(int) Math.ceil(peptide1.getIC50Score()) + "/" + (int) Math.ceil(peptide2.getIC50Score()));
			}
		}

		FileUtils.cleanDirectory(new File(this.getTmpSequencePath()));

		printNovelObejct(novel);

		LOGGER.exiting("AlloimmunityAnalyzerClassI", "runProteomeCheck");

	}

	private void printNovelObejct(NovelPeptideSurface novel) {

		StringBuilder sb = new StringBuilder();
		sb.append(novel.getVariant() + ",");
		sb.append(novel.getAllele() + ",");
		if (novel.getPeptide1() != null) {
			sb.append(((PeptideData) novel.getPeptide1()).getPeptide() + ",");
			sb.append(((PeptideData) novel.getPeptide1()).getIC50Score() + ",");
		} else {
			sb.append("" + ",");
			sb.append("" + ",");
		}
		if (novel.getPeptide2() != null) {
			sb.append(((PeptideData) novel.getPeptide2()).getPeptide() + ",");
			sb.append(((PeptideData) novel.getPeptide2()).getIC50Score() + ",");
		} else {
			sb.append("" + ",");
			sb.append("" + ",");
		}
		sb.append(novel.getColour());
		sb.append("\n");

		novelEntry.append(sb.toString());

		/* helpful output */
		LOGGER.info("NOVEL:" + sb.toString()
				+ "#################################################################################");
	}

	private void printMatchMap(Map<PeptideData, PeptideData> matchMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("==============================PRINTING MATCH MAP=================================\n");

		for (PeptideData key : matchMap.keySet()) {
			sb.append("REMAINING: " + key.toString() + "\n");

			PeptideData match = matchMap.get(key);
			if (match != null) {
				sb.append("MATCH: " + match.toString() + "\n");
			} else {
				sb.append("NO MATCH\n");
			}

		}
		sb.append("==============================END MATCH MAP======================================");
		LOGGER.info(sb.toString());
	}

	private void writeToFinalOutputFile() throws IOException {
		LOGGER.entering("AlloimmunityAnalyzerClassI", "writeToFinalOutputFile");

		String header = "Variant,Allele,Peptide_1,IC50_1,Peptide_2,IC50_2,Colour";
		String newLine = "\n";

		String fileName = this.getNovelSurfacesFileFullPath().split("\\.")[0] + properties.getValue("fileExtension") + ".csv";
		
		File file = new File(fileName);
		if (!file.exists()) {
			Path path = Paths.get(fileName);
			Files.write(path, header.getBytes());
			Files.write(path, newLine.getBytes(), StandardOpenOption.APPEND);
		}

		Path path = Paths.get(fileName);
		Files.write(path, novelEntry.toString().getBytes(), StandardOpenOption.APPEND);

		LOGGER.exiting("AlloimmunityAnalyzerClassI", "writeToFinalOutputFile");
	}

	private void readVariantFile(File variantFile) throws FileNotFoundException {
		String line = "";

		try {
			BufferedReader br = new BufferedReader(new FileReader(variantFile));

			while ((line = br.readLine()) != null && !line.trim().equals("")) {
				variants.add(line.trim());
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void readExcludedFile(File excludedFile) throws FileNotFoundException {
		String line = "";

		try {
			BufferedReader br = new BufferedReader(new FileReader(excludedFile));

			while ((line = br.readLine()) != null && !line.trim().equals("")) {
				excluded.add(line.trim());
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/****************************
	 * GETTERS and SETTERS
	 *****************************/

	public int getnMer() {
		return nMer;
	}

	public void setnMer(int nMer) {
		this.nMer = nMer;
	}

	public String getScoreCode() {
		return scoreCode;
	}

	public void setScoreCode(String scoreCode) {
		this.scoreCode = scoreCode;
	}

	public int getIC50_threshold() {
		return IC50_threshold;
	}

	public void setIC50_threshold(int iC50_threshold) {
		IC50_threshold = iC50_threshold;
	}

	public String getHlaFileName() {
		return hlaFileName;
	}

	public void setHlaFileName(String hlaFileName) {
		this.hlaFileName = hlaFileName;
	}

	public PredictionType getPredictionType() {
		return predictionType;
	}

	public void setPredictionType(PredictionType predictionType) {
		this.predictionType = predictionType;
	}

	public List<String> getVariants() {
		return variants;
	}

	public void setVariants(List<String> variants) {
		this.variants = variants;
	}

	public List<String> getExcluded() {
		return excluded;
	}

	public void setExcluded(List<String> excluded) {
		this.excluded = excluded;
	}

	public List<Integer> getAnchorPositions() {
		return anchorPositions;
	}

	public void setAnchorPositions(List<Integer> anchorPositions) {
		this.anchorPositions = anchorPositions;
	}

	public PropertiesHelper getProperties() {
		return properties;
	}

	public void setProperties(PropertiesHelper properties) {
		this.properties = properties;
	}

	public SequenceFactory getSequenceFactory() {
		return sequenceFactory;
	}

	public void setSequenceFactory(SequenceFactory sequenceFactory) {
		this.sequenceFactory = sequenceFactory;
	}

	public String getVariantSequencePath() {
		return variantSequencePath;
	}

	public void setVariantSequencePath(String variantSequencePath) {
		this.variantSequencePath = variantSequencePath;
	}

	public String getTmpSequencePath() {
		return tmpSequencePath;
	}

	public void setTmpSequencePath(String tmpSequencePath) {
		this.tmpSequencePath = tmpSequencePath;
	}

	public String getProteomeSequencePath() {
		return proteomeSequencePath;
	}

	public void setProteomeSequencePath(String proteomeSequencePath) {
		this.proteomeSequencePath = proteomeSequencePath;
	}

	public String getAlleleFileFullPath() {
		return alleleFileFullPath;
	}

	public void setAlleleFileFullPath(String alleleFileFullPath) {
		this.alleleFileFullPath = alleleFileFullPath;
	}

	public String getHlaFileFullPath() {
		return hlaFileFullPath;
	}

	public void setHlaFileFullPath(String hlaFileFullPath) {
		this.hlaFileFullPath = hlaFileFullPath;
	}

	public String getCompareFileFullPath() {
		return compareFileFullPath;
	}

	public void setCompareFileFullPath(String compareFileFullPath) {
		this.compareFileFullPath = compareFileFullPath;
	}

	public String getVariantFileFullPath() {
		return variantFileFullPath;
	}

	public void setVariantFileFullPath(String variantFileFullPath) {
		this.variantFileFullPath = variantFileFullPath;
	}

	public String getVariantOutputFullPath() {
		return variantOutputFullPath;
	}

	public void setVariantOutputFullPath(String variantOutputFullPath) {
		this.variantOutputFullPath = variantOutputFullPath;
	}

	public String getEndogenousOutputFullPath() {
		return endogenousOutputFullPath;
	}

	public void setEndogenousOutputFullPath(String endogenousOutputFullPath) {
		this.endogenousOutputFullPath = endogenousOutputFullPath;
	}

	public String getProteomeOutputFullPath() {
		return proteomeOutputFullPath;
	}

	public void setProteomeOutputFullPath(String proteomeOutputFullPath) {
		this.proteomeOutputFullPath = proteomeOutputFullPath;
	}

	public String getNovelSurfacesFileFullPath() {
		return novelSurfacesFileFullPath;
	}

	public void setNovelSurfacesFileFullPath(String novelSurfacesFileFullPath) {
		this.novelSurfacesFileFullPath = novelSurfacesFileFullPath;
	}

	public String getExcludeFileFullPath() {
		return excludeFileFullPath;
	}

	public void setExcludeFileFullPath(String excludeFileFullPath) {
		this.excludeFileFullPath = excludeFileFullPath;
	}

}
