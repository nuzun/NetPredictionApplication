package uk.ac.bbk.cryst.netprediction.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import uk.ac.bbk.cryst.netprediction.util.FileHelper;
import uk.ac.bbk.cryst.netprediction.util.NetPanCmd;
import uk.ac.bbk.cryst.netprediction.util.PeptideDataHelper;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.EnsemblPepSequence;
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
	String sequenceFileName;
	String scoreCode;
	PredictionType type;
	List<String> variants;
	List<Integer> anchorPositions;

	PropertiesHelper properties;
	SequenceFactory sequenceFactory;

	String variantSequencePath;
	String comparePath;
	String tmpSequencePath;
	String proteomeSequencePath;

	String alleleFileFullPath;
	String sequenceFileFullPath;
	String mutationFileFullPath;

	String variantOutputFullPathMHCIIPan;
	String endogenousOutputFullPathMHCIIPan;
	String proteomeOutputFullPathMHCIIPan;

	public String getMutationFileFullPath() {
		return mutationFileFullPath;
	}

	public String getProteomeSequencePath() {
		return proteomeSequencePath;
	}

	public String getTmpSequencePath() {
		return tmpSequencePath;
	}

	public String getProteomeOutputFullPathMHCIIPan() {
		return proteomeOutputFullPathMHCIIPan;
	}

	public String getComparePath() {
		return comparePath;
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

	public String getVariantOutputFullPathMHCIIPan() {
		return variantOutputFullPathMHCIIPan;
	}

	public String getEndogenousOutputFullPathMHCIIPan() {
		return endogenousOutputFullPathMHCIIPan;
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

	public NovelSurfaceAnalyzer() throws IOException {
		// parameters
		nMer = 15;
		anchorPositions = Arrays.asList(1, 4, 6, 9);
		sequenceFileName = "customFactorviii_P00451.fasta";
		scoreCode = "0"; // MHC(1) or comb (0) used for CTL only
		type = PredictionType.MHCIIPAN31;
		//

		variants = new ArrayList<String>();
		properties = new PropertiesHelper();
		sequenceFactory = new SequenceFactory();

		alleleFileFullPath = properties.getValue("alleleFileFullPath");
		sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
		comparePath = properties.getValue("comparePath");
		tmpSequencePath = properties.getValue("tmpSequencePath");
		proteomeSequencePath = properties.getValue("proteomeSequencePath");
		variantSequencePath = properties.getValue("variantSequencePath");

		variantOutputFullPathMHCIIPan = properties.getValue("variantOutputFullPathMHCIIPan");
		endogenousOutputFullPathMHCIIPan = properties.getValue("endogenousOutputFullPathMHCIIPan");
		proteomeOutputFullPathMHCIIPan = properties.getValue("proteomeOutputFullPathMHCIIPan");

		mutationFileFullPath = properties.getValue("mutationFileFullPath");
		File mutationFile = new File(mutationFileFullPath);
		readMutationFile(mutationFile);
	}

	private void readMutationFile(File mutationFile) throws FileNotFoundException {

		String line = "";

		BufferedReader br = new BufferedReader(new FileReader(mutationFile));
		try {
			while ((line = br.readLine()) != null) {
				variants.add(line.trim());
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void generateSequenceAndScoreFiles() throws Exception {

		// Read the alleles straight from region/group of alleles file
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();

		// Read the sequence file test_P00451.fasta
		File sequenceFile = new File(this.getSequenceFileFullPath());
		Sequence inputSequence = this.getSequenceFactory().getSequenceList(sequenceFile, FastaFileType.UNIPROT).get(0);

		// for each allele, generate the score file for the full sequence once
		String variantOutputFileFullPath = "";
		for (String allele : groupData.getAlleleMap().keySet()) {
			variantOutputFileFullPath = this.getVariantOutputFullPathMHCIIPan()
					+ FilenameUtils.removeExtension(this.getSequenceFileName()) + "_" + allele + ".txt";

			System.out.println(variantOutputFileFullPath);

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
			int variantPosition = Integer.valueOf(parts[1]);
			String to = parts[2];

			String subSequence = inputSequence.getPanningSequence(variantPosition, this.getnMer());

			// generate endogeneous sequence file
			StringBuilder endSeq = new StringBuilder(subSequence);
			int charIndex = variantPosition <= this.getnMer() ? variantPosition - 1 : this.getnMer() - 1;

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

				endogeneousOutputFileFullPath = this.getEndogenousOutputFullPathMHCIIPan()
						+ FilenameUtils.removeExtension(this.getSequenceFileName()) + "_" + allele + ".txt" + "_"
						+ variantPosition + from + to;
				System.out.println(endogeneousOutputFileFullPath);

				File scoreFileToCreate = new File(endogeneousOutputFileFullPath);
				if (!scoreFileToCreate.exists()) {
					NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
							endSequenceFile.getPath(), endogeneousOutputFileFullPath);
				}
			}
		} // variants
	}

	public void runEliminate() throws Exception {

		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();

		NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());

		for (String variant : this.getVariants()) {
			String[] parts = variant.split("-");
			String from = parts[0];
			int variantPosition = Integer.valueOf(parts[1]);
			String to = parts[2];

			for (String allele : groupData.getAlleleMap().keySet()) {
				List<MHCIIPeptideData> remainingPeptides = new ArrayList<MHCIIPeptideData>();

				String fileName = FilenameUtils.removeExtension(this.getSequenceFileName()) + "_" + allele + ".txt";
				NetPanData variantNetPanData = builder
						.buildSingleFileData(new File(this.getVariantOutputFullPathMHCIIPan() + fileName));

				for (PeptideData peptide : variantNetPanData.getPanningPeptideList(variantPosition)) {
					MHCIIPeptideData therPeptide = (MHCIIPeptideData) peptide;

					// continue if the core contains the variant and binds
					// strong, note that both startPos and coreStartPos starts
					// from 0, variantPos starts from 1
					int start = therPeptide.getStartPosition() + therPeptide.getCoreStartPosition();
					int variantIndexAtCore = variantPosition - start - 1; // 0-8

					if ((9 > variantIndexAtCore) && (variantIndexAtCore >= 0) && (therPeptide.getIC50Score() < 1000)) {

						// check endo criteria
						String endoFileName = fileName + "_" + variantPosition + from + to;
						NetPanData endoNetPanData = builder.buildSingleFileData(
								new File(this.getEndogenousOutputFullPathMHCIIPan() + endoFileName));

						// continue if the core is the same with any endo core
						StringBuilder endoCore = new StringBuilder(therPeptide.getCorePeptide());
						endoCore.setCharAt(variantIndexAtCore, to.charAt(0));
						List<MHCIIPeptideData> endoMatchList = endoNetPanData
								.getSpecificPeptideDataByCore(endoCore.toString());

						if (endoMatchList.size() > 0) {
							int allWeak = 1;
							for (MHCIIPeptideData endoMatch : endoMatchList) {
								// Check if we have at least one good binder
								if (endoMatch.getIC50Score() < 1000) {
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

				System.out.println(variant);
				for (MHCIIPeptideData p : remainingPeptides) {
					System.out.println(p.toString());
				}

				// start proteome check:
				System.out.println();
				runProteomeCheck(allele, variant, remainingPeptides);
			}
		} // variant
	}

	private void runProteomeCheck(String allele, String variant, List<MHCIIPeptideData> remainingPeptides)
			throws Exception {

		int coreNMer = 9;
		boolean isMatch = false;// positions do not have to match so false
		String[] parts = variant.split("-");
		// String from = parts[0];
		int variantPosition = Integer.valueOf(parts[1]);
		// String to = parts[2];

		List<Sequence> matchList = new ArrayList<Sequence>();
		NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());
		Map<MHCIIPeptideData, MHCIIPeptideData> matchMap = new HashMap<MHCIIPeptideData, MHCIIPeptideData>();
		Map<String, MHCIIPeptideData> tempMap = new HashMap<String, MHCIIPeptideData>();

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(FastaFileType.UNIPROT);
		sequenceComparator.setCompareFileType(FastaFileType.ENSEMBLPEP);

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

			matchList = sequenceComparator.runMatchFinder(tmpSeqFile, this.getComparePath(), this.getAnchorPositions(),
					isMatch, coreNMer);

			List<MHCIIPeptideData> matchingPeptides = new ArrayList<MHCIIPeptideData>();
			// run predictions on the matching proteome sequences
			for (Sequence seq : matchList) {
				EnsemblPepSequence ensemblPepSeq = (EnsemblPepSequence) seq;
				String proteomeSeqFileFullContent = ">sp|" + ensemblPepSeq.getProteinId() + "|"
						+ ensemblPepSeq.getDescription() + "\n" + ensemblPepSeq.getSequence();
				String proteomeSeqFileName = ensemblPepSeq.getProteinId() + ".fasta";
				File proteomeSeqFile = new File(this.getProteomeSequencePath() + proteomeSeqFileName);

				if (!proteomeSeqFile.exists()) {
					FileHelper.writeToFile(proteomeSeqFile, proteomeSeqFileFullContent);
				}

				String proteomeOutputFileFullPath = this.getProteomeOutputFullPathMHCIIPan()
						+ FilenameUtils.removeExtension(proteomeSeqFileName) + "_" + allele + ".txt";
				File proteomeScoreFileToCreate = new File(proteomeOutputFileFullPath);
				if (!proteomeScoreFileToCreate.exists()) {
					NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
							proteomeSeqFile.getPath(), proteomeOutputFileFullPath);
				}
				NetPanData protNetPanData = builder.buildSingleFileData(new File(proteomeOutputFileFullPath));

				// print the results
				System.out.println(ensemblPepSeq.getProteinId() + ":" + ensemblPepSeq.getGeneSymbol() + ":"
						+ ensemblPepSeq.getDescription());
				System.out.println("------------------------------------------------------");
				/*
				 * if (StringUtils.isNotEmpty(ensemblPepSeq.getGeneSymbol()) &&
				 * ensemblPepSeq.getGeneSymbol().equals("F8")) { for
				 * (MHCIIPeptideData pep :
				 * protNetPanData.getSpecificPeptideDataByMaskedCore(
				 * remaining.getCorePeptide(), this.getAnchorPositions(),
				 * isMatch, variantPosition)) {
				 * System.out.println(pep.toString()); } } else{ for
				 * (MHCIIPeptideData pep :
				 * protNetPanData.getSpecificPeptideDataByMaskedCore(
				 * remaining.getCorePeptide(), this.getAnchorPositions(),
				 * isMatch)) { System.out.println(pep.toString()); } }
				 * System.out.println(
				 * "------------------------------------------------------"); //
				 * 
				 * //pass the variant position as the panning position to ignore
				 * the peptides in that section for F8 if
				 * (StringUtils.isNotEmpty(ensemblPepSeq.getGeneSymbol()) &&
				 * ensemblPepSeq.getGeneSymbol().equals("F8")) {
				 * matchingPeptides.addAll(protNetPanData.
				 * getSpecificPeptideDataByMaskedCore(
				 * remaining.getCorePeptide(), this.getAnchorPositions(),
				 * isMatch, variantPosition)); } else {
				 * matchingPeptides.addAll(protNetPanData.
				 * getSpecificPeptideDataByMaskedCore(
				 * remaining.getCorePeptide(), this.getAnchorPositions(),
				 * isMatch)); }
				 */

				if (StringUtils.isNotEmpty(ensemblPepSeq.getProteinId())
						&& (ensemblPepSeq.getProteinId().startsWith("ENSP00000353393")
								|| ensemblPepSeq.getProteinId().startsWith("ENSP00000471364"))) {
					for (MHCIIPeptideData pep : protNetPanData.getSpecificPeptideDataByMaskedCore(
							remaining.getCorePeptide(), this.getAnchorPositions(), isMatch, variantPosition)) {
						System.out.println(pep.toString());
					}
				} else {
					for (MHCIIPeptideData pep : protNetPanData.getSpecificPeptideDataByMaskedCore(
							remaining.getCorePeptide(), this.getAnchorPositions(), isMatch)) {
						System.out.println(pep.toString());
					}
				}
				System.out.println("------------------------------------------------------");
				//

				// pass the variant position as the panning position to ignore
				// the peptides in that section for F8
				if (StringUtils.isNotEmpty(ensemblPepSeq.getProteinId())
						&& (ensemblPepSeq.getProteinId().startsWith("ENSP00000353393")
								|| ensemblPepSeq.getProteinId().startsWith("ENSP00000471364"))) {
					matchingPeptides.addAll(protNetPanData.getSpecificPeptideDataByMaskedCore(
							remaining.getCorePeptide(), this.getAnchorPositions(), isMatch, variantPosition));
				} else {
					matchingPeptides.addAll(protNetPanData.getSpecificPeptideDataByMaskedCore(
							remaining.getCorePeptide(), this.getAnchorPositions(), isMatch));
				}
			} // for matching proteome sequences

			MHCIIPeptideData bestMatch = (MHCIIPeptideData) PeptideDataHelper.getTheStrongestBinderII(matchingPeptides);
			matchMap.put(remaining, bestMatch);
			tempMap.put(remaining.getCorePeptide(), bestMatch);
		}

		System.out.println("-------------------------PRINTING MATCH MAP-----------------------------");
		for (MHCIIPeptideData key : matchMap.keySet()) {
			System.out.println("REMAINING=" + key.toString());
			MHCIIPeptideData match = matchMap.get(key);
			if (match != null) {
				System.out.println("match=" + match.toString() + "\n");
			} else {
				System.out.println("NO MATCH");
			}

		}
		System.out.println("-------------------------END MATCH MAP-----------------------------");

		int matchExists = 0;

		for (MHCIIPeptideData key : matchMap.keySet()) {
			MHCIIPeptideData match = matchMap.get(key);
			if (match != null) {
				matchExists = 1;
				if (match.getIC50Score() > 1000) {

					if (pep2.getPeptide() == null) {
						pep2 = key;
					} else {
						if (key.getIC50Score() < pep2.getIC50Score()) {
							pep2 = key;
						}
					}
				}
			}
		} // for matchmap

		novel.setPeptide1(pep1);
		novel.setPeptide2(pep2);

		if (remainingPeptides.isEmpty()) {
			novel.setColour("black");
		} else {
			if (pep2.getPeptide() == null && matchExists == 1) {
				novel.setColour("pep1color/grey");
			} else if (pep2.getPeptide() == null && matchExists == 0) {
				novel.setColour("pep1color/pep1color");
			} else {
				novel.setColour("pep1color/pep2color");
			}
		}

		System.out.println("PEP1:" + novel.getPeptide1() + "\n" + "PEP2:" + novel.getPeptide2() + "\n"
				+ novel.getVariant() + "\n" + novel.getAllele() + "\n" + novel.getColour());
		System.out.println(
				"******************************************************************************************************");
		FileUtils.cleanDirectory(new File(this.getTmpSequencePath()));

	}

}
