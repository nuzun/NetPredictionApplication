package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;
import uk.ac.bbk.cryst.netprediction.model.MHCIIPeptideData;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;
import uk.ac.bbk.cryst.netprediction.util.FileHelper;
import uk.ac.bbk.cryst.netprediction.util.NetPanCmd;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.MatchData;
import uk.ac.bbk.cryst.sequenceanalysis.model.MatchDataClassII;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceComparator;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceFactory;

/* 1. Find matching 9mers
 * 2. Run prediction on 9mers only to see if both left and right
 * bind with enough affinity
 *  
 * The MHC score does not change if you run the panning sequence
 * of the 9mer on CTLPAN or if you just run the 9mer instead
 * that's why we only ran the matching peptides on NetCTLPan
 * and did not consider the surrounding residues.
 * In class II analysis we need the 15mer not core 9mer so match object will not
 * suit our needs. We need a new match object then we can use a similar flow for class II.
 * */
public class PredictionBasedSequenceScanner {

	PredictionType type;
	FastaFileType inputType;
	FastaFileType compareType;
	int nMer;
	int coreNMer;
	boolean isMatch;
	int IC50_threshold;
	String scoreCode;
	List<Integer> anchorPositions;
	PropertiesHelper properties;
	String sequenceFileName;
	String compareFileName;
	SequenceFactory sequenceFactory;

	String subSequencePath;
	String alleleFileFullPath;
	String sequenceFileFullPath;
	String compareFileFullPath;

	String predictionOutputPath;
	List<Sequence> seq1List;
	List<Sequence> seq2List;
	
	int counter =0;

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

	public int getCoreNMer() {
		return coreNMer;
	}

	public void setCoreNMer(int coreNMer) {
		this.coreNMer = coreNMer;
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

	public String getSubSequencePath() {
		return subSequencePath;
	}

	public void setSubSequencePath(String subSequencePath) {
		this.subSequencePath = subSequencePath;
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

	public PredictionBasedSequenceScanner(PredictionType type, FastaFileType inputType, FastaFileType compareType,
			int coreNMer, int nMer) {
		this.type = type;
		this.coreNMer = coreNMer;
		this.nMer = nMer;
		this.IC50_threshold = 100;
		this.anchorPositions = Arrays.asList(1, 4, 6, 9);
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
			subSequencePath = properties.getValue("subSequencePath");

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
	
	public void scan(){
		if(this.getType() == PredictionType.MHCIIPAN31){
			scanProteomeClassII();
		}
		else{
			scanProteomeClassI();
		}
	}

	public void scanProteomeClassI() {
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
						scanClassI(allele, match);
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void scanProteomeClassII() {
		try {
			AlleleGroupData groupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();

			SequenceComparator sequenceComparator = new SequenceComparator();

			for (String allele : groupData.getAlleleMap().keySet()) {
				System.out.println("ALLELE:" + allele);
				System.out.println("*******************************************");
				
			    counter = 0;

				for (Sequence seq1 : seq1List) {

					List<MatchDataClassII> matchDataList = sequenceComparator.getMatchData(seq1, seq2List,
							this.getAnchorPositions(), this.isMatch(), this.getCoreNMer(), this.getnMer());

					for (MatchDataClassII match : matchDataList) {
						scanClassII(allele, match);
					}
				}
				System.out.println("ALLELE COUNTER:" + allele + ":" + counter);
				System.out.println("------------------------------------------");

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void scanClassII(String allele, MatchDataClassII match) {
		// TODO Auto-generated method stub

		try {

			//System.out.println("MATCH FOUND:");
			//System.out.println(match.toString());

			NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());

			String seq1FileFullContent = ">sp|" + match.getProteinId1() + "_" + match.getCoreStartPosition1() + "\n"
					+ match.getPanningPeptide1();
			String seq1FileName = match.getProteinId1() + "_" + match.getCoreStartPosition1() + ".fasta";
			File seq1File = new File(this.getSubSequencePath() + seq1FileName);

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

			List<MHCIIPeptideData> peptideData1List = inputSeqNetPanData
					.getSpecificPeptideDataByMaskedCore(match.getCorePeptide1(), this.getAnchorPositions(), isMatch());

			List<MHCIIPeptideData> result1 = peptideData1List.stream()
					.filter(x -> x.getIC50Score() <= this.getIC50_threshold()).collect(Collectors.toList());

			// if we have a good affinity then continue
			if (result1.size() > 0) {
				//System.out.println("INPUT PEPTIDE FOUND WITH ENOUGH AFF:");
				//result1.forEach(System.out::println);
				// Now work on the second sequence

				String seq2FileFullContent = ">sp|" + match.getProteinId2() + "_" + match.getCoreStartPosition2() + "\n"
						+ match.getPanningPeptide2();
				String seq2FileName = match.getProteinId2() + "_" + match.getCoreStartPosition2() + ".fasta";
				File seq2File = new File(this.getSubSequencePath() + seq2FileName);

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

				List<MHCIIPeptideData> peptideData2List = matchNetPanData.getSpecificPeptideDataByMaskedCore(
						match.getCorePeptide1(), this.getAnchorPositions(), this.isMatch());

			
				List<MHCIIPeptideData> result2 = peptideData2List.stream()
						.filter(x -> x.getIC50Score() <= this.getIC50_threshold()).collect(Collectors.toList());

				if (result2.size() > 0) {
					//System.out.println("INPUT PEPTIDE FOUND WITH ENOUGH AFF:");
					//result1.forEach(System.out::println);
					//System.out.println("MATCH FOUND WITH ENOUGH AFF:" + match.getProteinId2());
					//result2.forEach(System.out::println);
					//System.out.println("");
					counter++;	
					
					for(MHCIIPeptideData r: result1){
						System.out.println(match.getCoreStartPosition1() + ":" + r.getPeptide());
					}
					
				} else {
					//System.out.println("MATCHING PEPTIDE:");
					//peptideData2List.forEach(System.out::println);
				}
			}

			else {

				//System.out.println("INPUT PEPTIDE:");
				//peptideData1List.forEach(System.out::println);
			}

			//System.out.println("");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void scanClassI(String allele, MatchData match) {
		// TODO Auto-generated method stub

		try {

			System.out.println("MATCH FOUND:");
			System.out.println(match.toString());

			NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());

			String seq1FileFullContent = ">sp|" + match.getProteinId1() + "_" + match.getPosition1() + "\n"
					+ match.getPeptide1();
			String seq1FileName = match.getProteinId1() + "_" + match.getPosition1() + ".fasta";
			File seq1File = new File(this.getSubSequencePath() + seq1FileName);
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
				File seq2File = new File(this.getSubSequencePath() + seq2FileName);

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
