package uk.ac.bbk.cryst.netprediction.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.util.MHCIINovelSurfaceProcessorHelper;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.PeptideGenerator;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceFactory;

public class ProteomeAnalyser {
	static PropertiesHelper properties = new PropertiesHelper();
	static List<Sequence> sequenceList = new ArrayList<>();
	static List<String> sequenceIds = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		processProteomeFile();
		processLog();
	}

	private static void processProteomeFile() throws IOException {
		int nMer = 9;
		String comparePath = properties.getValue("comparePath");
		SequenceFactory sequenceFactory = new SequenceFactory();
		Set<String> uniquePeptideList = new HashSet<>();

		// read the compareDir and all the files as there might be more than one
		File compareDir = new File(comparePath);
		for (final File fileEntry : compareDir.listFiles()) {
			if (fileEntry.isDirectory()) {
				// ignore the directory and continue, we want one compare file
				continue;
			}
			List<Sequence> tempList = sequenceFactory.getSequenceList(fileEntry, FastaFileType.ENSEMBLPEP);// compare
																											// prot
																											// type
			sequenceList.addAll(tempList);
		}

		for (Sequence seq : sequenceList) {
			sequenceIds.add(seq.getProteinId());
		}
		// how many sequences in the proteome
		System.out.println("Total " + sequenceList.size() + " proteins in the proteome");

		// how many total 9mers
		//int totalPeptides = 0;
		//for (Sequence seq : sequenceList) {
		//	totalPeptides += PeptideGenerator.getAllPeptides(seq, nMer).size();
		//}
		//System.out.println("Total " + totalPeptides + " 9mers in the proteome");

		// how many unique 9mers
		// for (Sequence seq : sequenceList) {
		// uniquePeptideList.addAll(PeptideGenerator.getUniquePeptides(seq,
		// nMer));
		// }
		// System.out.println("Total " + uniquePeptideList.size() + " unique
		// 9mers in the proteome");

	}

	private static void processLog() throws IOException {
		Map<String, ArrayList<String>> matchMapByMutationAndAllele = new HashMap<>();
		Map<String, ArrayList<String>> matchMapByMutationOnly = new HashMap<>();
		Map<String, Integer> proteomeMatchMap = new HashMap<>();
		MHCIINovelSurfaceProcessorHelper helper = new MHCIINovelSurfaceProcessorHelper();

		Scanner scanner = null;
		File logFile = new File(properties.getValue("logPath"));

		String pVariantLine = "INFO:\\s*(\\w{1}-\\d+-\\w{1})";
		String runProteomeCheckText = "runProteomeCheck";
		String pIC50Line = "IC50Score=(.*?)[,\\]]";

		String mutation = "";
		String allele = "";
		String key = "";

		try {
			scanner = new Scanner(logFile);

			while (scanner.hasNext()) {
				String line = scanner.nextLine();

				if (line.matches(pVariantLine)) {

					Pattern r = Pattern.compile(pVariantLine);
					Matcher m = r.matcher(line);

					if (m.find()) {
						mutation = m.group(1);
						// System.out.println("mutation:" + mutation );
					}

					allele = scanner.nextLine();
					// System.out.println("allele:" + allele);

					key = mutation + "," + allele;

					// check if the mutation is in our list
					if (helper.getVariants().contains(mutation)) {

						if (!matchMapByMutationAndAllele.keySet().contains(key)) {
							matchMapByMutationAndAllele.put(key, new ArrayList<>());
						}

						if (!matchMapByMutationOnly.keySet().contains(mutation)) {
							matchMapByMutationOnly.put(mutation, new ArrayList<>());
						}

					}
				}

				if (line.contains(runProteomeCheckText)) {
					String pNameLine = scanner.nextLine();
					String pName = "";
					String proteinName = "";

					pName = pNameLine.replace("INFO: ", "");

					if (pName.contains("_")) {
						proteinName = pName.split("_")[0];

						if (!sequenceIds.contains(proteinName)) {
							continue;
						}

						int toPrint = 0;

						while (scanner.hasNext()) {
							String lineScore = scanner.nextLine();
							if (lineScore.contains("IC50Score=")) {
								Pattern r = Pattern.compile(pIC50Line);
								Matcher m = r.matcher(lineScore);
								if (m.find()) {
									Float score = Float.valueOf(m.group(1));
									if (score < 1000) {
										toPrint = 1;
									}
								}
							} else {
								if (toPrint == 1) {
									// System.out.println("protein:" + pName);
									// check if the mutation is in our list
									if (helper.getVariants().contains(mutation)) {
										matchMapByMutationAndAllele.get(key).add(pName);
										matchMapByMutationOnly.get(mutation).add(pName);

										// if we want to do a sum instead of
										// position speicific
										// then use proteinName instead of pName
										// +1 for each variant position, different core match and different position match 
										if (proteomeMatchMap.keySet().contains(proteinName)) {
											proteomeMatchMap.put(proteinName,
													proteomeMatchMap.get(proteinName).intValue() + 1);
										} else {
											proteomeMatchMap.put(proteinName, 1);
										}
									}

								}
								break;
							}
						} //while

					}

				} // runProteomeCheck line

			} // while

			// matchMap.forEach((k, v) -> {
			// System.out.println("Key : " + k + " Value : " + v);
			// });

			Comparator<Entry<String, ArrayList<String>>> byValue = Comparator.comparing(e -> e.getValue().size());

			// matchMapByMutationAndAllele.entrySet().stream().sorted(byValue.reversed()).limit(10)
			// .forEach(System.out::println);
			matchMapByMutationAndAllele.entrySet().stream().sorted(byValue.reversed()).limit(10)
					.forEach(s -> System.out.println(s.getKey() + ":" + s.getValue().size()));

			System.out.println("**********************************************************");

			// matchMapByMutationOnly.entrySet().stream().sorted(byValue.reversed()).limit(10)
			// .forEach(System.out::println);
			matchMapByMutationOnly.entrySet().stream().sorted(byValue.reversed()).limit(10)
					.forEach(s -> System.out.println(s.getKey() + ":" + s.getValue().size()));

			System.out.println("**********************************************************");

			Map<String, Integer> result = proteomeMatchMap.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(50)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
							LinkedHashMap::new));
			System.out.println(result);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();

		}

	}

}
