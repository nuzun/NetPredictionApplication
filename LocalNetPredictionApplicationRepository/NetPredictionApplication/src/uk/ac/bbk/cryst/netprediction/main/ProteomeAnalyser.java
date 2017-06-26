package uk.ac.bbk.cryst.netprediction.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.PeptideGenerator;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceFactory;

public class ProteomeAnalyser {

	public static void main(String[] args) throws IOException {
		int nMer = 9;
		PropertiesHelper properties = new PropertiesHelper();
		String comparePath = properties.getValue("comparePath");
		SequenceFactory sequenceFactory = new SequenceFactory();
		Set<String> uniquePeptideList = new HashSet<>();

		
		// read the compareDir and all the files as there might be more than one
		List<Sequence> sequenceList = new ArrayList<>();
		File compareDir = new File(comparePath);
		for (final File fileEntry : compareDir.listFiles()) {
			if (fileEntry.isDirectory()) {
				// ignore the directory and continue, we want one compare file
				continue;
			}
			List<Sequence> tempList = sequenceFactory.getSequenceList(fileEntry, FastaFileType.ENSEMBLPEP);
			sequenceList.addAll(tempList);
		}
		
		//how many sequences in the proteome
		System.out.println("Total " + sequenceList.size() + " proteins in the proteome");
		
		//how many total 9mers
		int totalPeptides = 0;
		for(Sequence seq : sequenceList){
			totalPeptides += PeptideGenerator.getAllPeptides(seq, nMer).size();
		}
		System.out.println("Total " + totalPeptides + " 9mers in the proteome");

	
		//how many unique 9mers
		for(Sequence seq : sequenceList){
			uniquePeptideList.addAll(PeptideGenerator.getUniquePeptides(seq, nMer));
		}
		System.out.println("Total " + uniquePeptideList.size() + " unique 9mers in the proteome");

	}

}
