package uk.ac.bbk.cryst.netprediction.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.PatientData;
import uk.ac.bbk.cryst.netprediction.service.NovelSurfaceResultsProcessor;
import uk.ac.bbk.cryst.netprediction.util.CSVUtils;
import uk.ac.bbk.cryst.netprediction.util.NovelSurfaceProcessorHelper;

public class PatientDataNovelSurfaceAnalyzerMain {

	static String fishersSimple = "";
	static String fishersComplex = "";
	static PropertiesHelper properties = new PropertiesHelper();
	static List<PatientData> patientList = new ArrayList<>();
	static List<String> variants = new ArrayList<>();
	static NovelSurfaceProcessorHelper helper;

	public static void main(String[] args) throws IOException {

		try {

			helper = new NovelSurfaceProcessorHelper();
			patientList = helper.getPatientList();
			variants = helper.getVariants();

			NovelSurfaceResultsProcessor processor = new NovelSurfaceResultsProcessor(false, false,
					PredictionType.MHCII);
			processor.createVariantFiles();

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
		Map<String, Integer> positionMap = new HashMap<>();

		for (String variant : variants) {
			String[] arr = variant.split("-");
			String pos = arr[1];
			if (positionMap.containsKey(pos)) {
				positionMap.put(pos, positionMap.get(pos).intValue() + 1);
			} else {
				positionMap.put(pos, 1);
			}
		}

		int counter = 0;
		for (String pos : positionMap.keySet()) {
			if (positionMap.get(pos) > 1) {
				counter++;
			}
		}

		System.out.println("Number of variant positions with more than one mutation:" + counter);

		List<PatientData> list = patientList.stream().filter(p -> p.isInhibitorFormation())
				.collect(Collectors.toList());
		System.out.println("Number of patients with inhibitors in the patient data:" + list.size());

		// System.out.println("Variants from patient data with inhibitors:");
		// for(PatientData p : list){
		// System.out.println(p.getVariant());
		// }

	}

	private static void printCategoricalNumbersComplex() {
		// TODO Auto-generated method stub
		Float[] thresholds = { 1000f, 500f, 300f, 200f, 100f, 50f };

		for (Float threshold : thresholds) {
			String allBlackFilePath = "data//output//variants_allBlack_" + threshold.intValue() + ".csv";
			String excludeFilePath = "data//output//variants_exclude_" + threshold.intValue() + ".csv";
			System.out.println("calculateCategoricalNumbersComplex:" +threshold);
			calculateCategoricalNumbersComplex(patientList, allBlackFilePath,excludeFilePath);
		}

		System.out.println(fishersComplex);

	}

	private static void calculateCategoricalNumbersComplex(List<PatientData> patientList, String allBlackFilePath, String excludeFilePath) {
		// TODO Auto-generated method stub

		try {

			File excludeFile = new File(excludeFilePath);
			final List<String> exclude = helper.readVariantFile(excludeFile);
			
			File allBlackFile = new File(allBlackFilePath);
			final List<String> allBlack = helper.readVariantFile(allBlackFile);

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
					.filter(p -> !allBlack.contains(p.getVariant()) && p.isInhibitorFormation() && !exclude.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(cList);
			System.out.println("C=" + cList.size());

			// D: Patients without inhibitors having a missense mutation for
			// which we predict a risk of
			// inhibitor development with at least one of the 14 HLA alleles in
			// our set.
			List<PatientData> dList = patientList.stream()
					.filter(p -> !allBlack.contains(p.getVariant()) && !p.isInhibitorFormation() && !exclude.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("D=" + dList.size());

			fishersComplex += "challenge.df = matrix(c(" + aList.size() + "," + bList.size() + "," + dList.size() + ","
					+ cList.size() + "), nrow = 2)\n";
			// challenge.df = matrix(c(1,4,7,4), nrow = 2)
			// fisher.test(challenge.df)
			// chisq.test(challenge.df,correct=FALSE)

		} catch (Exception ex) {

		}

	}

	private static void printCategoricalNumbersSimple() {
		Float[] thresholds = { 1000f, 500f, 300f, 200f, 100f, 50f };

		for (Float threshold : thresholds) {
			String allBlackFilePath = "data//output//variants_allBlack_" + threshold.intValue() + ".csv";
			System.out.println(threshold.intValue());
			try {
				System.out.println("calculateCategoricalNumbersSimple:" +threshold);
				calculateCategoricalNumbersSimple(patientList, allBlackFilePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println(fishersSimple);

	}

	private static void calculateCategoricalNumbersSimple(List<PatientData> patientList, String allBlackFilePath)
			throws Exception {
		try {

			File allBlackFile = new File(allBlackFilePath);
			final List<String> allBlack = helper.readVariantFile(allBlackFile);

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

			/*
			 * observed vs predicted List<PatientData> xList =
			 * patientList.stream().filter(p -> p.isInhibitorFormation())
			 * .collect(Collectors.toList()); // printList(dList);
			 * System.out.println("observed inhibitor formation=" +
			 * xList.size());
			 * 
			 * List<PatientData> yList = patientList.stream().filter(p ->
			 * !p.isInhibitorFormation()) .collect(Collectors.toList()); //
			 * printList(dList); System.out.println("observed no inhibitors=" +
			 * yList.size());
			 * 
			 * List<PatientData> zList = patientList.stream().filter(p ->
			 * !allBlack.contains(p.getVariant()))
			 * .collect(Collectors.toList()); // printList(dList);
			 * System.out.println("predicted inhibitor formation=" +
			 * zList.size());
			 * 
			 * List<PatientData> tList = patientList.stream().filter(p ->
			 * allBlack.contains(p.getVariant())) .collect(Collectors.toList());
			 * // printList(dList);
			 * System.out.println("predicted no inhibitors=" + tList.size());
			 * 
			 */

			fishersSimple += "challenge.df = matrix(c(" + aList.size() + "," + bList.size() + "," + dList.size() + ","
					+ cList.size() + "), nrow = 2)\n";
			// challenge.df = matrix(c(1,4,7,4), nrow = 2)
			// fisher.test(challenge.df)
			// chisq.test(challenge.df,correct=FALSE)

		} catch (Exception ex) {
			throw ex;
		}

	}

	private static void printList(List<PatientData> list) {
		list.forEach(p -> System.out.println(p));
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

}
