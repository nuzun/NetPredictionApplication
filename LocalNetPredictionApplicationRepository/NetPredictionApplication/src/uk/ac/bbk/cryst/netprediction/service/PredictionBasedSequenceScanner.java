package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;
import uk.ac.bbk.cryst.netprediction.util.FileHelper;
import uk.ac.bbk.cryst.netprediction.util.NetPanCmd;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.MatchData;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceComparator;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceFactory;

public class PredictionBasedSequenceScanner {

	PredictionType type;
	FastaFileType inputType;
	FastaFileType compareType;
	int nMer;
	boolean isMatch;
	int IC50_threshold;
	String scoreCode;
	List<Integer> anchorPositions;
	PropertiesHelper properties;
	String sequenceFileName;
	String compareFileName;
	SequenceFactory sequenceFactory;

	String tmpSequencePath;
	String alleleFileFullPath;
	String sequenceFileFullPath;
	String compareFileFullPath;

	String predictionOutputPath;
	List<Sequence> seq1List;
	List<Sequence> seq2List;

	public boolean isMatch() {
		return isMatch;
	}

	public PredictionType getType() {
		return type;
	}

	public FastaFileType getInputType() {
		return inputType;
	}

	public void setInputType(FastaFileType inputType) {
		this.inputType = inputType;
	}

	public FastaFileType getCompareType() {
		return compareType;
	}

	public void setCompareType(FastaFileType compareType) {
		this.compareType = compareType;
	}

	public void setType(PredictionType type) {
		this.type = type;
	}

	public int getnMer() {
		return nMer;
	}

	public void setnMer(int nMer) {
		this.nMer = nMer;
	}

	public int getIC50_threshold() {
		return IC50_threshold;
	}

	public void setIC50_threshold(int iC50_threshold) {
		IC50_threshold = iC50_threshold;
	}

	public String getScoreCode() {
		return scoreCode;
	}

	public void setScoreCode(String scoreCode) {
		this.scoreCode = scoreCode;
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

	public String getSequenceFileName() {
		return sequenceFileName;
	}

	public void setSequenceFileName(String sequenceFileName) {
		this.sequenceFileName = sequenceFileName;
	}

	public String getCompareFileName() {
		return compareFileName;
	}

	public void setCompareFileName(String compareFileName) {
		this.compareFileName = compareFileName;
	}

	public String getCompareFileFullPath() {
		return compareFileFullPath;
	}

	public void setCompareFileFullPath(String compareFileFullPath) {
		this.compareFileFullPath = compareFileFullPath;
	}

	public SequenceFactory getSequenceFactory() {
		return sequenceFactory;
	}

	public void setSequenceFactory(SequenceFactory sequenceFactory) {
		this.sequenceFactory = sequenceFactory;
	}

	public String getTmpSequencePath() {
		return tmpSequencePath;
	}

	public void setTmpSequencePath(String tmpSequencePath) {
		this.tmpSequencePath = tmpSequencePath;
	}

	public String getAlleleFileFullPath() {
		return alleleFileFullPath;
	}

	public void setAlleleFileFullPath(String alleleFileFullPath) {
		this.alleleFileFullPath = alleleFileFullPath;
	}

	public String getSequenceFileFullPath() {
		return sequenceFileFullPath;
	}

	public void setSequenceFileFullPath(String sequenceFileFullPath) {
		this.sequenceFileFullPath = sequenceFileFullPath;
	}

	public String getPredictionOutputPath() {
		return predictionOutputPath;
	}

	public void setPredictionOutputPath(String predictionOutputPath) {
		this.predictionOutputPath = predictionOutputPath;
	}

	public PredictionBasedSequenceScanner(PredictionType type, FastaFileType inputType, FastaFileType compareType, int nMer) {
		this.type = type;
		this.nMer = nMer;
		this.IC50_threshold = 500;
		this.anchorPositions = Arrays.asList(1,2,3,nMer);
		this.scoreCode = "0";
		this.inputType = inputType;
		this.compareType = compareType;
		this.isMatch = false;

		properties = new PropertiesHelper();
		sequenceFactory = new SequenceFactory();

		try {
			sequenceFileName = properties.getValue("sequenceFileName");
			compareFileName = properties.getValue("compareFileName");
			alleleFileFullPath = properties.getValue("alleleFileFullPath");
			sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
			compareFileFullPath = properties.getValue("compareFileFullPath");
			tmpSequencePath = properties.getValue("tmpSequencePath");

			switch (this.type) {
			case MHCIIPAN31:
				predictionOutputPath = properties.getValue("outputPathMHCIIPan");
				break;
			case MHCII:
				predictionOutputPath = properties.getValue("outputPathMHCII");
				break;
			case CTL:
				predictionOutputPath = properties.getValue("outputPathCTL");
				break;
			case CTLPAN:
				predictionOutputPath = properties.getValue("outputPathCTLPan");
				break;
			default:
				predictionOutputPath = "Invalid";
				break;
			}

			File sequenceFile = new File(this.getSequenceFileFullPath());
			seq1List = this.getSequenceFactory().getSequenceList(sequenceFile, this.getInputType());

			File compareFile = new File(this.getCompareFileFullPath());
			seq2List = this.getSequenceFactory().getSequenceList(compareFile, this.getCompareType());
			
			System.out.println("INPUT/COMPARE FILES:" + sequenceFile.getName() + "/" + compareFile.getName());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void scanProteome() {
		try {
			AlleleGroupData groupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();

			SequenceComparator sequenceComparator = new SequenceComparator();

			for (String allele : groupData.getAlleleMap().keySet()) {
				System.out.println("ALLELE:" + allele);
				System.out.println("*******************************************");

				for (Sequence seq1 : seq1List) {


					List<MatchData> matchDataList = sequenceComparator.getMatchData(seq1, seq2List,
							this.getAnchorPositions(), this.isMatch(), this.getnMer());
				
					for (MatchData match : matchDataList) {
						scan(allele, match);
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void scan(String allele, MatchData match) {
		// TODO Auto-generated method stub

		try {

			System.out.println("MATCH FOUND:");
			System.out.println(match.toString());

			NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());

			String seq1FileFullContent = ">sp|" + match.getProteinId1() + "_" + match.getPosition1() + "\n"
					+ match.getPeptide1();
			String seq1FileName = match.getProteinId1() + "_" + match.getPosition1() + ".fasta";
			File seq1File = new File(this.getTmpSequencePath() + seq1FileName);
			if (!seq1File.exists()) {
				FileHelper.writeToFile(seq1File, seq1FileFullContent);
			}

			String seq1OutputFileFullPath = this.getPredictionOutputPath() + FilenameUtils.removeExtension(seq1FileName)
					+ "_" + allele + ".txt";
			File predictionFileToCreate = new File(seq1OutputFileFullPath);
			if (!predictionFileToCreate.exists()) {
				NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
						seq1File.getPath(), seq1OutputFileFullPath);
			}

			NetPanData inputSeqNetPanData = builder.buildSingleFileData(new File(seq1OutputFileFullPath));

			List<PeptideData> peptideData1List = inputSeqNetPanData.getSpecificPeptideData(match.getPeptide1());
			PeptideData peptideData1 = peptideData1List.get(0);

			if (peptideData1.getIC50Score() <= this.getIC50_threshold()) {
				// get the second sequence and generate predictions and
				// the peptide affinity score
				System.out.println("INPUT PEPTIDE FOUND WITH ENOUGH AFF:");
				System.out.println(peptideData1.toString());

				String seq2FileFullContent = ">sp|" + match.getProteinId2() + "_" + match.getPosition2() + "\n"
						+ match.getPeptide2();
				String seq2FileName = match.getProteinId2() + "_" + match.getPosition2() + ".fasta";
				File seq2File = new File(this.getTmpSequencePath() + seq2FileName);

				if (!seq2File.exists()) {
					FileHelper.writeToFile(seq2File, seq2FileFullContent);
				}

				String seq2OutputFileFullPath = this.getPredictionOutputPath()
						+ FilenameUtils.removeExtension(seq2FileName) + "_" + allele + ".txt";
				File seq2PredictionFileToCreate = new File(seq2OutputFileFullPath);
				if (!seq2PredictionFileToCreate.exists()) {
					NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
							seq2File.getPath(), seq2OutputFileFullPath);
				}

				NetPanData matchNetPanData = builder.buildSingleFileData(new File(seq2OutputFileFullPath));

				List<PeptideData> peptideData2List = matchNetPanData.getSpecificPeptideDataByMaskedMatch(
						match.getPeptide1(), this.getAnchorPositions(), this.isMatch());

				if (peptideData2List.size() != 1) {
					throw new Exception(
							"More than 1 match peptide:" + match.getProteinId1() + " vs " + match.getProteinId2());
				}

				PeptideData peptideData2 = peptideData2List.get(0);

				if (peptideData2.getIC50Score() <= this.getIC50_threshold()) {
					System.out.println("MATCH FOUND WITH ENOUGH AFF:");
					System.out.println(peptideData2.toString());
				} else {
					System.out.println("MATCHING PEPTIDE:");
					System.out.println(peptideData2.toString());
				}

			}

			else {

				System.out.println("INPUT PEPTIDE:");
				System.out.println(peptideData1.toString());
			}
			System.out.println("");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
