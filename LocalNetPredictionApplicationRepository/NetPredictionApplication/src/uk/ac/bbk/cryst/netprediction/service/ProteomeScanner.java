package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.CharEncoding;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;
import uk.ac.bbk.cryst.netprediction.util.FileHelper;
import uk.ac.bbk.cryst.netprediction.util.NetPanCmd;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.PeptideGenerator;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceComparator;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceFactory;

public class ProteomeScanner {

	PredictionType type;
	int nMer;
	int IC50_threshold;
	String scoreCode;
	List<Integer> anchorPositions;
	PropertiesHelper properties;
	String sequenceFileName;
	SequenceFactory sequenceFactory;

	String comparePath;
	String tmpSequencePath;
	String proteomeSequencePath;
	String alleleFileFullPath;
	String sequenceFileFullPath;

	String proteomeOutputPath;

	public PredictionType getType() {
		return type;
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

	public SequenceFactory getSequenceFactory() {
		return sequenceFactory;
	}

	public void setSequenceFactory(SequenceFactory sequenceFactory) {
		this.sequenceFactory = sequenceFactory;
	}

	public String getComparePath() {
		return comparePath;
	}

	public void setComparePath(String comparePath) {
		this.comparePath = comparePath;
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

	public String getSequenceFileFullPath() {
		return sequenceFileFullPath;
	}

	public void setSequenceFileFullPath(String sequenceFileFullPath) {
		this.sequenceFileFullPath = sequenceFileFullPath;
	}

	public String getProteomeOutputPath() {
		return proteomeOutputPath;
	}

	public void setProteomeOutputPath(String proteomeOutputPath) {
		this.proteomeOutputPath = proteomeOutputPath;
	}

	public ProteomeScanner(PredictionType type) {
		this.type = type;
		this.nMer = 9;
		this.IC50_threshold = 1000;
		this.anchorPositions = Arrays.asList(1, 2, 3, 9);
		this.scoreCode = "0";

		properties = new PropertiesHelper();
		sequenceFactory = new SequenceFactory();

		try {
			sequenceFileName = properties.getValue("sequenceFileName");
			alleleFileFullPath = properties.getValue("alleleFileFullPath");
			sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
			comparePath = properties.getValue("comparePath");
			tmpSequencePath = properties.getValue("tmpSequencePath");
			proteomeSequencePath = properties.getValue("proteomeSequencePath");

			switch (this.type) {
			case MHCIIPAN31:
				proteomeOutputPath = properties.getValue("proteomeOutputPathMHCIIPan");
				break;
			case MHCII:
				proteomeOutputPath = properties.getValue("proteomeOutputPathMHCII");
				break;
			case CTL:
				proteomeOutputPath = properties.getValue("proteomeOutputPathCTL");
				break;
			case CTLPAN:
				proteomeOutputPath = properties.getValue("proteomeOutputPathCTLPan");
				break;
			default:
				proteomeOutputPath = "Invalid";
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void scanProteome() {
		try {
			AlleleGroupData groupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();
			// Read the sequence file test_P00451.fasta
			File sequenceFile = new File(this.getSequenceFileFullPath());
			Sequence inputSequence = this.getSequenceFactory().getSequenceList(sequenceFile, FastaFileType.UNIPROT)
					.get(0);
			
			List<Sequence> seq2List = new ArrayList<>();
			File compareDir = new File(this.getComparePath());
			for (final File fileEntry : compareDir.listFiles()) {
				if (fileEntry.isDirectory()) {
					// ignore the directory and continue, we want one compare file
					continue;
				}
				List<Sequence> tempList = this.getSequenceFactory().getSequenceList(fileEntry, FastaFileType.ENSEMBLPEP);//compare proteome type
				seq2List.addAll(tempList);
			}

			for (String allele : groupData.getAlleleMap().keySet()) {
				scan(allele, PeptideGenerator.getAllPeptides(inputSequence, this.getnMer()), seq2List);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void scan(String allele, List<String> allPeptides, List<Sequence> seq2List) {
		// TODO Auto-generated method stub
		boolean isMatch = false;
		List<Sequence> matchList = new ArrayList<Sequence>();
		List<PeptideData> matchingPeptides = new ArrayList<PeptideData>();
		
		try {

			NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());
			SequenceComparator sequenceComparator = new SequenceComparator();
			sequenceComparator.setInputFileType(FastaFileType.UNIPROT);
			sequenceComparator.setCompareFileType(FastaFileType.ENSEMBLPEP);

			for (String peptide : allPeptides) {
				String tmpSequencePath = this.getTmpSequencePath();
				String tmpSeqFileFullContent = ">sp|" + "temp|temp" + "\n" + peptide;
				String tmpFileName = "temp.fasta";

				File tmpSeqFile = new File(tmpSequencePath + tmpFileName);
				FileUtils.writeStringToFile(tmpSeqFile, tmpSeqFileFullContent, CharEncoding.UTF_8);

				matchList = sequenceComparator.runMatchFinder(tmpSeqFile, seq2List, this.getAnchorPositions(),
						isMatch, this.getnMer());
				
				for (Sequence seq : matchList) {
					String proteomeSeqFileFullContent = ">sp|" + seq.getProteinId() + "\n" + seq.getSequence();
					String proteomeSeqFileName = seq.getProteinId() + ".fasta";
					File proteomeSeqFile = new File(this.getProteomeSequencePath() + proteomeSeqFileName);

					if (!proteomeSeqFile.exists()) {
						FileHelper.writeToFile(proteomeSeqFile, proteomeSeqFileFullContent);
					}

					String proteomeOutputFilePath = this.getProteomeOutputPath()
							+ FilenameUtils.removeExtension(proteomeSeqFileName) + "_" + allele + ".txt";
					File proteomeScoreFileToCreate = new File(proteomeOutputFilePath);
					if (!proteomeScoreFileToCreate.exists()) {
						NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
								proteomeSeqFile.getPath(), proteomeOutputFilePath);
					}
					NetPanData protNetPanData = builder.buildSingleFileData(new File(proteomeOutputFilePath));

					matchingPeptides.addAll(protNetPanData.getSpecificPeptideDataByMaskedMatch(
							peptide, this.getAnchorPositions(), isMatch));
					
					for(PeptideData match : matchingPeptides){
						if(match.getIC50Score() < this.getIC50_threshold()){
							//we have a match in proteome with sufficient binding
							System.out.println("ALLELE:" + allele + " PEPTIDE:" + peptide);
							System.out.println("MATCH SEQ:" + seq.getProteinId());
							System.out.println(match.toString());
						}
					}
				}

			}
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
}
