package uk.ac.bbk.cryst.netprediction.util;

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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;
import uk.ac.bbk.cryst.netprediction.model.PatientData;

public class NovelSurfaceProcessorHelper {

	PropertiesHelper properties = new PropertiesHelper();
	List<String> variants = new ArrayList<>();
	List<PatientData> patientList = new ArrayList<>();
 
	

	public List<String> getVariants() {
		return variants;
	}
	
	

	public List<PatientData> getPatientList() {
		return patientList;
	}



	public NovelSurfaceProcessorHelper() throws IOException{
		readNonSevereMutationFile();
		readNonSeverePatientFile();
	}
	
	public List<String> readAlleleFile(PredictionType type) throws FileNotFoundException{
		List<String> alleles = new ArrayList<>();
		String path = null;
		switch(type){
		case MHCII:
			path = "data//input//mhcII_full_list_netmhcii.csv";
			break;
		case MHCIIPAN31:
			path = "data//input//mhcII_full_list.csv";
		default:
			path = "data//input//mhcII.csv";
		}
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(path).getGroupData();
		for (String allele : groupData.getAlleleMap().keySet()) {
			alleles.add(allele);
			
		}
		return alleles;

	}
	
	public List<String> readVariantFile(File variantFile) throws FileNotFoundException {

		String line = "";
		List<String> variantList = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(variantFile));
		try {
			br.readLine();
			while ((line = br.readLine()) != null && !line.trim().equals("")) {
				variantList.add(line.trim());
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return variantList;
	}
	
	public void readNonSevereMutationFile() throws IOException {

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

	}
	
	public void readNonSeverePatientFile() {
		// Set<String> uniqueList = new HashSet<>();
		Map<Integer, String> varMap = new HashMap<>();

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
					}

					String item = getLetter(m.group(1)) + "-" + oldIndex + "-" + getLetter(m.group(3));
					varMap.put(oldIndex, getLetter(m.group(1)));

					if (!isCrossCheckCorrect(varMap)) {
						System.out.println("ERR on crosscheck");
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
	}
	
	public void printList(List<PatientData> list) {
		list.forEach(p -> System.out.println(p));
	}

	public void generateCustomPatientCsvFile(List<PatientData> patientList) {
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
