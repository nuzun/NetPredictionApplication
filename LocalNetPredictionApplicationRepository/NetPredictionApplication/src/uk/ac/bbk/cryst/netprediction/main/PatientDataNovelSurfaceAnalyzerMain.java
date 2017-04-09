package uk.ac.bbk.cryst.netprediction.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.PatientData;
import uk.ac.bbk.cryst.netprediction.service.NovelSurfaceResultsProcessor;
import uk.ac.bbk.cryst.netprediction.util.CSVUtils;

public class PatientDataNovelSurfaceAnalyzerMain {
	
	static String fishersSimple = "";
	static String fishersComplex = "";
	static PropertiesHelper properties = new PropertiesHelper();
	static List<PatientData> patientList = new ArrayList<>();
	static List<String> variants = new ArrayList<>();


	public static void main(String[] args) throws IOException {

		try {
			patientList = readNonSeverePatientFile();
			variants = readNonSevereMutationFile();

			NovelSurfaceResultsProcessor processor = new NovelSurfaceResultsProcessor(false,false,PredictionType.MHCIIPAN31);
			processor.readNovelSurfaceResults();
			
			printCategoricalNumbersSimple();
			printCategoricalNumbersComplex();
			printVariousStatistics();

		}

		catch (Exception e) {
			throw e;
		}

		finally {

		}

	}

	private static void printVariousStatistics() throws IOException {
		
		Map<String,Integer> positionMap = new HashMap<>();
		
		for(String variant:variants){
			String[] arr = variant.split("-");
			String pos = arr[1];
			if(positionMap.containsKey(pos)){
				positionMap.put(pos, positionMap.get(pos).intValue()+1);
			}
			else{
				positionMap.put(pos, 1);
			}
		}
		
		int counter = 0;
		for(String pos:positionMap.keySet()){
			if(positionMap.get(pos) > 1){
				counter++;
			}
		}
		
		System.out.println("Number of variant positions with more than one mutation:" + counter);
		
		List<PatientData> list = patientList.stream()
				.filter(p -> p.isInhibitorFormation())
				.collect(Collectors.toList());
		System.out.println("Number of patients with inhibitors in the patient data:" + list.size());
		
		//System.out.println("Variants from patient data with inhibitors:");
		//for(PatientData p : list){
		//	System.out.println(p.getVariant());
		//}
		
	}
	private static void printCategoricalNumbersComplex() {
		// TODO Auto-generated method stub
		Float[] thresholds = {1000f,500f,300f,200f,100f,50f};
		
		for(Float threshold : thresholds){
			String allBlackFilePath = "data//output//variants_allBlack_"+ threshold.intValue() +".csv";
			System.out.println(threshold.intValue());
			calculateCategoricalNumbersComplex(patientList, allBlackFilePath);
		}
		
		System.out.println(fishersComplex);
		
	}
	
	

	private static void calculateCategoricalNumbersComplex(List<PatientData> patientList, String allBlackFilePath) {
		// TODO Auto-generated method stub
		
		try {
			
			File allBlackFile = new File(allBlackFilePath);
			final List<String> allBlack = readAllBlackFile(allBlackFile);

			// A: Patients without inhibitors having a missense mutation for
			// which we predict no risk of
			// inhibitor development with any of the 14 HLA alleles in our set.
			List<PatientData> aList = patientList.stream()
					.filter(p -> allBlack.contains(p.getVariant()) && !p.isInhibitorFormation())
					.collect(Collectors.toList());
			 //printList(aList);
			System.out.println("A=" + aList.size());

			// B: Patients with inhibitors having a missense mutation for which
			// we predict no risk of inhibitor
			// development with any of the 14 HLA alleles in our set.
			List<PatientData> bList = patientList.stream()
					.filter(p -> allBlack.contains(p.getVariant()) && p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(bList);
			System.out.println("B=" + bList.size());

			// C: Patients with inhibitors having a missense mutation for which
			// we predict a risk of inhibitor
			// development with at least one of the 14 HLA alleles in our set.
			List<PatientData> cList = patientList.stream()
					.filter(p -> !allBlack.contains(p.getVariant()) && p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(cList);
			System.out.println("C=" + cList.size());

			// D: Patients without inhibitors having a missense mutation for
			// which we predict a risk of
			// inhibitor development with at least one of the 14 HLA alleles in
			// our set.
			List<PatientData> dList = patientList.stream()
					.filter(p -> !allBlack.contains(p.getVariant()) && !p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("D=" + dList.size());

			
			fishersComplex += "challenge.df = matrix(c(" + aList.size()  + "," + bList.size() + "," + 
														  dList.size()  + "," + cList.size()  + "), nrow = 2)\n";
			//challenge.df = matrix(c(1,4,7,4), nrow = 2)
			//fisher.test(challenge.df)
			//chisq.test(challenge.df,correct=FALSE)

		} catch (Exception ex) {

		}
		
	}

	private static void printCategoricalNumbersSimple() {		
		Float[] thresholds = {1000f,500f,300f,200f,100f,50f};
		
		for(Float threshold : thresholds){
			String allBlackFilePath = "data//output//variants_allBlack_"+ threshold.intValue() +".csv";
			System.out.println(threshold.intValue());
			try {
				calculateCategoricalNumbersSimple(patientList, allBlackFilePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println(fishersSimple);
		
	}



	private static void calculateCategoricalNumbersSimple(List<PatientData> patientList, String allBlackFilePath) throws Exception {
		try {
			
			File allBlackFile = new File(allBlackFilePath);
			final List<String> allBlack = readAllBlackFile(allBlackFile);

			// A: Patients without inhibitors having a missense mutation for
			// which we predict no risk of
			// inhibitor development with any of the 14 HLA alleles in our set.
			List<PatientData> aList = patientList.stream()
					.filter(p -> allBlack.contains(p.getVariant()) && !p.isInhibitorFormation())
					.collect(Collectors.toList());
			 //printList(aList);
			System.out.println("A=" + aList.size());

			// B: Patients with inhibitors having a missense mutation for which
			// we predict no risk of inhibitor
			// development with any of the 14 HLA alleles in our set.
			List<PatientData> bList = patientList.stream()
					.filter(p -> allBlack.contains(p.getVariant()) && p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(bList);
			System.out.println("B=" + bList.size());

			// C: Patients with inhibitors having a missense mutation for which
			// we predict a risk of inhibitor
			// development with at least one of the 14 HLA alleles in our set.
			List<PatientData> cList = patientList.stream()
					.filter(p -> !allBlack.contains(p.getVariant()) && p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(cList);
			System.out.println("C=" + cList.size());

			// D: Patients without inhibitors having a missense mutation for
			// which we predict a risk of
			// inhibitor development with at least one of the 14 HLA alleles in
			// our set.
			List<PatientData> dList = patientList.stream()
					.filter(p -> !allBlack.contains(p.getVariant()) && !p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("D=" + dList.size());

			
			/* observed vs predicted
			List<PatientData> xList = patientList.stream().filter(p -> p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("observed inhibitor formation=" + xList.size());

			List<PatientData> yList = patientList.stream().filter(p -> !p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("observed no inhibitors=" + yList.size());

			List<PatientData> zList = patientList.stream().filter(p -> !allBlack.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("predicted inhibitor formation=" + zList.size());

			List<PatientData> tList = patientList.stream().filter(p -> allBlack.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("predicted no inhibitors=" + tList.size());
			 
			 */
			
			fishersSimple += "challenge.df = matrix(c(" + aList.size()  + "," + bList.size() + "," + 
														  dList.size()  + "," + cList.size()  + "), nrow = 2)\n";
			//challenge.df = matrix(c(1,4,7,4), nrow = 2)
			//fisher.test(challenge.df)
			//chisq.test(challenge.df,correct=FALSE)

		} catch (Exception ex) {
			throw ex;
		}

	}

	private static List<PatientData> readNonSeverePatientFile() {
		// Set<String> uniqueList = new HashSet<>();
		Map<Integer, String> varMap = new HashMap<>();
		List<PatientData> patientList = new ArrayList<>();

		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		CSVFormat csvFileFormat = CSVFormat.RFC4180;

		try {

			fileReader = new FileReader(
					"data//input//factorviii_multiple_mutation_interim_nonsevere_withInhibitorData_nonBlank.csv");
			csvFileParser = new CSVParser(fileReader, csvFileFormat);
			List<CSVRecord> csvRecords = csvFileParser.getRecords();

			for (int i = 0; i < csvRecords.size(); i++) {
				CSVRecord record = (CSVRecord) csvRecords.get(i);
				int newIndex = Integer.valueOf(record.get(0));
				int oldIndex = Integer.valueOf(record.get(1));
				String proChange = record.get(2); // 22,3,p.Arg22Ile
				String severity = record.get(3);
				String inhibitor = record.get(4);

				boolean inhibitorFormation = false;
				if (inhibitor.trim().equals("Yes")) {
					inhibitorFormation = true;
				} else {
					inhibitorFormation = false;
				}

				if (oldIndex < 0) {
					continue;
				}

				String pattern = "p\\.(\\w{3})(\\d+)(\\w{3})";
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(proChange);

				if (m.find()) {

					if (newIndex != Integer.valueOf(m.group(2))) {
						System.out.println("ISSUE WITH INDEX");
						return null;
					}

					String item = getLetter(m.group(1)) + "-" + oldIndex + "-" + getLetter(m.group(3));
					varMap.put(oldIndex, getLetter(m.group(1)));

					if (!isCrossCheckCorrect(varMap)) {
						System.out.println("ERR on crosscheck");
						return null;
					}

					/*
					 * This generates unique variant list: mutations.txt file
					 * if(!uniqueList.contains(item)) {
					 * System.out.println(item); uniqueList.add(item); }
					 */

					PatientData p = new PatientData(item, oldIndex, severity, inhibitorFormation);
					patientList.add(p);

				} else {
					System.out.println("NO MATCH");
				}
			}

			// generateCustomPatientCsvFile(patientList);
		}

		catch (Exception e) {

		}

		finally {
			if (csvFileParser != null) {
				try {
					csvFileParser.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return patientList;

	}
	
	private static List<String> readNonSevereMutationFile() throws IOException {

		String mutationFileFullPath = properties.getValue("mutationFileNonSevereFullPath");
		File mutationFile = new File(mutationFileFullPath);
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
		
		return variants;
	}

	private static void printList(List<PatientData> list) {
		list.forEach(p -> System.out.println(p));
	}

	private static List<String> readAllBlackFile(File allBlackFile) throws FileNotFoundException {

		String line = "";
		List<String> allBlack = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(allBlackFile));
		try {
			br.readLine();
			while ((line = br.readLine()) != null && !line.trim().equals("")) {
				allBlack.add(line.trim());
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return allBlack;
	}

	private static void generateCustomPatientCsvFile(List<PatientData> patientList) {
		String csvFile = "data//output//custom_factorviii_multiple_mutation_interim_nonsevere_withInhibitorData_nonBlank.csv";
		try {
			FileWriter writer = new FileWriter(csvFile);
			CSVUtils.writeLine(writer, Arrays.asList("Variant", "Position", "Severity", "InhibitorFormation"));

			for (PatientData p : patientList) {
				List<String> list = new ArrayList<>();
				list.add(p.getVariant());
				list.add(String.valueOf(p.getPosition()));
				list.add(p.getSeverity());
				list.add(String.valueOf(p.isInhibitorFormation()));

				CSVUtils.writeLine(writer, list);
			}
			writer.flush();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static boolean isCrossCheckCorrect(Map<Integer, String> varMap) throws IOException {
		// cross check with the sequence
		BufferedReader br = new BufferedReader(new FileReader("data//input//sequence//factorviii_P00451.fasta"));
		String line;
		String seq = "";
		while ((line = br.readLine()) != null) {
			if (line.startsWith(">")) {
				continue;
			}
			seq += line.trim();
		}

		br.close();

		for (int i : varMap.keySet()) {
			if (seq.charAt(i + 18) != varMap.get(i).charAt(0)) {
				System.out.println("ERR:" + i + ":" + seq.charAt(i + 18) + varMap.get(i).charAt(0));
				return false;
			} else {
				// System.out.println("YAY:"+ i + ":" + seq.charAt(i+18) +
				// varMap.get(i).charAt(0));
			}
		}

		return true;
	}

	private static String getLetter(String threeLetter) {
		String letter;
		switch (threeLetter) {
		case "Ala":
			letter = "A";
			break;
		case "Arg":
			letter = "R";
			break;
		case "Asn":
			letter = "N";
			break;
		case "Asp":
			letter = "D";
			break;
		case "Cys":
			letter = "C";
			break;
		case "Gln":
			letter = "Q";
			break;
		case "Glu":
			letter = "E";
			break;
		case "Gly":
			letter = "G";
			break;
		case "His":
			letter = "H";
			break;
		case "Ile":
			letter = "I";
			break;
		case "Leu":
			letter = "L";
			break;
		case "Lys":
			letter = "K";
			break;
		case "Met":
			letter = "M";
			break;
		case "Phe":
			letter = "F";
			break;
		case "Pro":
			letter = "P";
			break;
		case "Ser":
			letter = "S";
			break;
		case "Thr":
			letter = "T";
			break;
		case "Trp":
			letter = "W";
			break;
		case "Tyr":
			letter = "Y";
			break;
		case "Val":
			letter = "V";
			break;
		case "Asx":
			letter = "B";
			break;
		case "Glx":
			letter = "Z";
			break;
		default:
			throw new IllegalArgumentException("Invalid argument:" + threeLetter);
		}
		return letter;

	}

}
