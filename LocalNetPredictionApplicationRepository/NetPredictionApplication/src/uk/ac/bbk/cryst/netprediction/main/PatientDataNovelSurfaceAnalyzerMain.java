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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import uk.ac.bbk.cryst.netprediction.model.PatientData;
import uk.ac.bbk.cryst.netprediction.util.CSVUtils;

public class PatientDataNovelSurfaceAnalyzerMain {

	public static void main(String[] args) throws IOException {

		try {
			List<PatientData> patientList = readPatientFile();
			
			String allBlackFilePath = "data//output//variants_allBlack_1000.csv";
			System.out.println(1000);
			getResults(patientList, allBlackFilePath);
			
			allBlackFilePath = "data//output//variants_allBlack_500.csv";
			System.out.println(500);
			getResults(patientList, allBlackFilePath);

			allBlackFilePath = "data//output//variants_allBlack_300.csv";
			System.out.println(300);
			getResults(patientList, allBlackFilePath);
			
			allBlackFilePath = "data//output//variants_allBlack_200.csv";
			System.out.println(200);
			getResults(patientList, allBlackFilePath);
			
			allBlackFilePath = "data//output//variants_allBlack_100.csv";
			System.out.println(100);
			getResults(patientList, allBlackFilePath);
			
			allBlackFilePath = "data//output//variants_allBlack_50.csv";
			System.out.println(50);
			getResults(patientList, allBlackFilePath);
		

		}

		catch (Exception e) {

		}

		finally {

		}

	}

	private static void getResults(List<PatientData> patientList, String filePath) {
		try {
			File allBlackFile = new File(filePath);
			final List<String> allBlack = readAllBlackFile(allBlackFile);

			// A: Patients without inhibitors having a missense mutation for
			// which we predict no risk of
			// inhibitor development with any of the 14 HLA alleles in our set.
			List<PatientData> aList = patientList.stream()
					.filter(p -> allBlack.contains(p.getVariant()) && !p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(aList);
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
			
			List<PatientData> xList = patientList.stream()
					.filter(p ->  p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("observed inhibitor formation=" + xList.size());
			
			List<PatientData> yList = patientList.stream()
					.filter(p ->  !p.isInhibitorFormation())
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("observed no inhibitors=" + yList.size());
			
			List<PatientData> zList = patientList.stream()
					.filter(p ->  !allBlack.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("predicted inhibitor formation=" + zList.size());
			
			List<PatientData> tList = patientList.stream()
					.filter(p ->  allBlack.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("predicted no inhibitors=" + tList.size());
			
		} catch (Exception ex) {

		}

	}

	private static List<PatientData> readPatientFile() {
		Set<String> uniqueList = new HashSet<>();
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

			// writeToCsvFile(patientList);
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

	private static void writeToCsvFile(List<PatientData> patientList) {
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
