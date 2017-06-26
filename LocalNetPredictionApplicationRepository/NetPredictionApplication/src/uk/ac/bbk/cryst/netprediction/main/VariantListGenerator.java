package uk.ac.bbk.cryst.netprediction.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class VariantListGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readPatientFile("data//input//factorviii_multiple_mutation_interim_allseverity_withInhibitorData_full.csv");
	}

	
	public static void readPatientFile(String filePath) {
	    Set<String> uniqueList = new HashSet<>();
		Map<Integer, String> varMap = new HashMap<>();

		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		CSVFormat csvFileFormat = CSVFormat.RFC4180;

		try {

			fileReader = new FileReader(filePath);
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

					
					  //This generates unique variant list: mutations.txt file
					  if(!uniqueList.contains(item)) {
					  System.out.println(item); 
					  uniqueList.add(item); 
					  }
					


				} else {
					System.out.println("NO MATCH");
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
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
