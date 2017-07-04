package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import uk.ac.bbk.cryst.netprediction.common.InhibitorStatus;
import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.HeatMapBox;
import uk.ac.bbk.cryst.netprediction.model.PatientData;
import uk.ac.bbk.cryst.netprediction.util.CSVUtils;
import uk.ac.bbk.cryst.netprediction.util.FileHelper;
import uk.ac.bbk.cryst.netprediction.util.NovelSurfaceProcessorHelper;

public class NovelSurfaceResultsProcessor {

	PropertiesHelper properties;
	NovelSurfaceProcessorHelper helper;
	String novelSurfacesResultFilePath;
	boolean proteomeScanningOn = false;
	int alleleCounter = 0;
	boolean onlyDR = true;
	PredictionType predictionType;
	Float[] thresholds = { 1000f, 500f, 300f, 200f, 100f, 50f, 25f };
	String fishersText = "";
	List<HeatMapBox> boxList = new ArrayList<>();

	public NovelSurfaceProcessorHelper getHelper() {
		return helper;
	}

	public List<HeatMapBox> getBoxList() {
		return boxList;
	}

	public PredictionType getPredictionType() {
		return predictionType;
	}

	public void setPredictionType(PredictionType predictionType) {
		this.predictionType = predictionType;
	}

	public boolean isOnlyDR() {
		return onlyDR;
	}

	public void setOnlyDR(boolean onlyDR) {
		this.onlyDR = onlyDR;
	}

	public int getAlleleCounter() {
		return isOnlyDR() ? 14 : 25;
	}

	public boolean isProteomeScanningOn() {
		return proteomeScanningOn;
	}

	public void setProteomeScanningOn(boolean proteomeScanningOn) {
		this.proteomeScanningOn = proteomeScanningOn;
	}

	public PropertiesHelper getProperties() {
		return properties;
	}

	public String getNovelSurfacesResultFilePath() {
		return novelSurfacesResultFilePath;
	}

	public void setNovelSurfacesResultFilePath(String novelSurfacesResultFilePath) {
		this.novelSurfacesResultFilePath = novelSurfacesResultFilePath;
	}

	public NovelSurfaceResultsProcessor(boolean proteomeScanning, boolean onlyDR, PredictionType predictionType)
			throws IOException {
		super();
		this.properties = new PropertiesHelper();
		this.helper = new NovelSurfaceProcessorHelper();

		switch (predictionType) {
		case MHCII:
			this.setNovelSurfacesResultFilePath(properties.getValue("novelSurfacesResultFilePathMHCII"));
			break;
		case MHCIIPAN31:
			this.setNovelSurfacesResultFilePath(properties.getValue("novelSurfacesResultFilePathMHCIIPan"));
			break;
		default:
			break;
		}

		this.setPredictionType(predictionType);
		this.setProteomeScanningOn(proteomeScanning);
		this.setOnlyDR(onlyDR);

		readNovelSurfaceResultFiles(); // populate full heatbox list
		createHeatMapFiles();

		for (Float threshold : thresholds) {
			createVariantFile(threshold);
		}

	}

	private void readNovelSurfaceResultFiles() {
		File root = new File(this.getNovelSurfacesResultFilePath());
		FilenameFilter beginswith = new FilenameFilter() {
			public boolean accept(File directory, String filename) {
				return filename.startsWith("novelSurfaces_");
			}
		};

		File[] files = root.listFiles(beginswith);

		for (File novelSurfaceResultsFile : files) {
			Scanner scanner = null;

			try {

				scanner = new Scanner(novelSurfaceResultsFile);
				// Set the delimiter used in file
				scanner.useDelimiter(",");
				scanner.nextLine();

				while (scanner.hasNext()) {
					String row = scanner.nextLine();
					String[] elements = row.split(",");

					if (elements.length != 9) {
						System.out.println("Error: Missing data");
						return;
					}

					String variant = elements[0];
					String allele = elements[1];
					String peptide_1 = elements[2];
					String corePeptide_1 = elements[3];
					String IC50_1 = elements[4];
					String peptide_2 = elements[5];
					String corePeptide_2 = elements[6];
					String IC50_2 = elements[7];
					String colour = elements[8];

					// check variants we want to calculate things for first
					// before creating
					if (this.getHelper().getVariants().contains(variant)) {
						HeatMapBox box = new HeatMapBox(allele, variant, colour);
						boxList.add(box);
					}

				}
			}

			catch (Exception ex) {

			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
		}
	}

	private void createHeatMapFiles() throws IOException {
		// write first line all variants comma separated
		StringJoiner content = new StringJoiner(",");
		String newLine = "\n";
		content.add("allele");
		for (String variant : this.getHelper().getHeatmapVariants()) {
			content.add(variant);
		}
		String fullContent = content.toString() + newLine;

		// then for each variant and for each allele
		// find the box and examine the colour
		// item[0] for prot scan off
		// item[1] for prot scan on, could be grey
		// could also be black

		for (String allele : this.getHelper().readAlleleFile(this.getPredictionType())) {
			content = new StringJoiner(",");
			content.add(allele);

			for (String variant : this.getHelper().getHeatmapVariants()) {
				List<HeatMapBox> filteredList = boxList.stream()
						.filter(p -> variant.equals(p.getVariant()) && allele.equals(p.getAllele()))
						.collect(Collectors.toList());
				HeatMapBox box = filteredList.get(0);

				if (!box.getColour().equals("black")) {
					String[] items = box.getColour().split("/");
					if (this.isProteomeScanningOn() && items[1].equals("grey")) {
						content.add(String.valueOf(1101f));
					} else if (this.isProteomeScanningOn() && !items[1].equals("grey")) {
						content.add(items[1]);
					} else {
						content.add(items[0]);
					}
				} else {
					// black
					content.add(String.valueOf(1001f));
				}
			}
			fullContent += content.toString() + newLine;
		}

		File file = new File(
				"data//output//heatmap_" + this.getPredictionType() + "_" + this.isProteomeScanningOn() + ".csv");
		FileHelper.writeToFile(file, fullContent);

	}

	private void createVariantFile(Float threshold) throws FileNotFoundException {

		// Variant,Allele,Peptide_1,CorePeptide_1,IC50_1,Peptide_2,CorePeptide_2,IC50_2,Colour
		// R-3-I,DRB1_0101,CLLRFCFSATRRYYL,FCFSATRRY,11.78,CLLRFCFSATRRYYL,FCFSATRRY,11.78,12/12
		// G-22-C,DRB1_0101,WDYMQSDLGELPVDA,MQSDLGELP,449.39,null,null,null,450/grey
		// T-49-A,DRB1_0101,,,,null,null,null,black

		/*
		 * String[] DRAlleles = { "DRB1_0101", "DRB1_0301", "DRB1_0401",
		 * "DRB1_0404", "DRB1_0405", "DRB1_0701", "DRB1_0802", "DRB1_0901",
		 * "DRB1_1101", "DRB1_1302", "DRB1_1501", "DRB3_0101", "DRB4_0101",
		 * "DRB5_0101" }; "HLA-DPA10103-DPB10201","HLA-DPA10103-DPB10401",
		 * "HLA-DPA10201-DPB10101",
		 * "HLA-DPA10201-DPB10501","HLA-DPA10301-DPB10402",
		 * "HLA-DQA10101-DQB10501",
		 * "HLA-DQA10102-DQB10602","HLA-DQA10301-DQB10302",
		 * "HLA-DQA10401-DQB10402",
		 * "HLA-DQA10501-DQB10201","HLA-DQA10501-DQB10301"
		 */

		List<HeatMapBox> heatmapBlacks = new ArrayList<>();
		List<HeatMapBox> heatmapGreys = new ArrayList<>();

		for (HeatMapBox box : boxList) {

			if (isOnlyDR() && box.getAllele().startsWith("HLA")) {
				continue;
			}

			if (!box.getColour().equals("black")) {
				String[] items = box.getColour().split("/");

				if (this.isProteomeScanningOn()) {
					if (items[1].equals("grey")) {
						heatmapBlacks.add(box);
						heatmapGreys.add(box);
					} else {
						if (Float.valueOf(items[1]) > threshold) {
							heatmapBlacks.add(box);
						}
					}
				} // if isProteomeScanningOn
				else {
					if (items[1].equals("grey")) {
						heatmapGreys.add(box);
					}

					if (Float.valueOf(items[0]) > threshold) {
						heatmapBlacks.add(box);

					}
				}

			} // if colour not black
			else {
				heatmapBlacks.add(box);
			}

		}

		writeToVariantsCsvFile(heatmapBlacks, threshold);
		System.out.println("Threshold:" + threshold);
		printSquareBasedRiskStatistics(heatmapBlacks.size());
		printAlleleBasedRiskStatistics(heatmapBlacks, threshold);

	}

	private void printSquareBasedRiskStatistics(int black) {
		// greys only revelant with 1000nM as we decrease the threshold squares
		// turn to black

		System.out.println("NoRisk = " + black + "/" + boxList.size() + "=" + ((black * 100) / boxList.size()));

		System.out.println("----------------------------------------------------------");

	}

	private void printAlleleBasedRiskStatistics(List<HeatMapBox> heatmapBlacks, Float threshold)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		int totalVariants = this.getHelper().getVariants().size();
		for (String allele : this.getHelper().readAlleleFile(this.getPredictionType())) {
			List<HeatMapBox> filteredList = heatmapBlacks.stream().filter(p -> allele.equals(p.getAllele()))
					.collect(Collectors.toList());

			int noRisk = filteredList.size();
			int percent = (noRisk * 100) / totalVariants;

			System.out.println(allele + " with " + threshold + " threshold NoRisk:" + noRisk + "/" + totalVariants + "="
					+ percent);
		}

		System.out.println("----------------------------------------------------------");

	}

	public void printVariousStatistics() throws IOException {
		Map<String, Integer> positionMap = new HashMap<>();

		for (String variant : this.getHelper().getVariants()) {
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

		System.out.println("----------------------------------------------------------");

		System.out.println("Number of variant positions with more than one mutation:" + counter);

		List<PatientData> list = this.getHelper().getPatientList().stream()
				.filter(p -> p.getInhibitorStatus().equals(InhibitorStatus.YES)).collect(Collectors.toList());
		System.out.println("Number of patients with inhibitors in the patient data:" + list.size());

	}

	public void printRepresentativePatientStatistics() throws FileNotFoundException {
		// TODO Auto-generated method stub

		int totalVariants = this.getHelper().getVariants().size();
		System.out.println("Patient Specific Stats:");

		for (Float threshold : thresholds) {
			String patientBlackFilePath = "data//output//variants_patient_allBlack_" + threshold.intValue() + ".csv";

			File patientBlackFile = new File(patientBlackFilePath);
			final List<String> patientAllBlack = this.getHelper().readVariantFile(patientBlackFile);

			int noRisk = patientAllBlack.size();
			int percent = (noRisk * 100) / totalVariants;

			System.out.println(threshold + " threshold NoRisk:" + noRisk + "/" + totalVariants + "=" + percent);
		}

		System.out.println("----------------------------------------------------------");

	}

	public void printCategoricalNumbersComplex() throws FileNotFoundException {
		// TODO Auto-generated method stub

		for (Float threshold : thresholds) {
			String allBlackFilePath = "data//output//variants_allBlack_" + threshold.intValue() + ".csv";
			String includeFilePath = "data//output//variants_include_" + threshold.intValue() + ".csv";
			System.out.println("calculateCategoricalNumbersComplex:" + threshold);
			calculateCategoricalNumbersComplex(this.getHelper().getPatientList(), allBlackFilePath, includeFilePath);
			// calculateNewRiskStatistics(allBlackFilePath,includeFilePath);
		}

		System.out.println(fishersText);

	}

	private void calculateCategoricalNumbersComplex(List<PatientData> patientList, String allBlackFilePath,
			String includeFilePath) {
		// TODO Auto-generated method stub

		try {

			File includeFile = new File(includeFilePath);
			final List<String> include = this.getHelper().readVariantFile(includeFile);

			File allBlackFile = new File(allBlackFilePath);
			final List<String> allBlack = this.getHelper().readVariantFile(allBlackFile);

			// A: Patients without inhibitors having a missense mutation for
			// which we predict no risk of
			// inhibitor development with any of the 14 HLA alleles in our set.
			List<PatientData> aList = patientList
					.stream().filter(p -> allBlack.contains(p.getVariant())
							&& p.getInhibitorStatus().equals(InhibitorStatus.NO) && include.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(aList);
			System.out.println("A=" + aList.size());

			// B: Patients with inhibitors having a missense mutation for which
			// we predict no risk of inhibitor
			// development with any of the 14 HLA alleles in our set.
			List<PatientData> bList = patientList
					.stream().filter(p -> allBlack.contains(p.getVariant())
							&& p.getInhibitorStatus().equals(InhibitorStatus.YES) && include.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(bList);
			System.out.println("B=" + bList.size());

			// C: Patients with inhibitors having a missense mutation for which
			// we predict a risk of inhibitor
			// development with at least one of the 14 HLA alleles in our set.
			List<PatientData> cList = patientList.stream()
					.filter(p -> !allBlack.contains(p.getVariant())
							&& p.getInhibitorStatus().equals(InhibitorStatus.YES) && include.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(cList);
			System.out.println("C=" + cList.size());

			// D: Patients without inhibitors having a missense mutation for
			// which we predict a risk of
			// inhibitor development with at least one of the 14 HLA alleles in
			// our set.
			List<PatientData> dList = patientList
					.stream().filter(p -> !allBlack.contains(p.getVariant())
							&& p.getInhibitorStatus().equals(InhibitorStatus.NO) && include.contains(p.getVariant()))
					.collect(Collectors.toList());
			// printList(dList);
			System.out.println("D=" + dList.size());

			fishersText += "challenge.df = matrix(c(" + aList.size() + "," + bList.size() + "," + dList.size() + ","
					+ cList.size() + "), nrow = 2)\n";
			// challenge.df = matrix(c(1,4,7,4), nrow = 2)
			// fisher.test(challenge.df)
			// chisq.test(challenge.df,correct=FALSE)

		} catch (Exception ex) {

		}

	}

	private void writeToVariantsCsvFile(List<HeatMapBox> heatmapBlacks, Float threshold) {
		String blackCsvFile = "data//output//variants_allBlack_" + threshold.intValue() + ".csv";
		String includeCsvFile = "data//output//variants_include_" + threshold.intValue() + ".csv";
		String patientSpecificBlackCsvFile = "data//output//variants_patient_allBlack_" + threshold.intValue() + ".csv";

		String[] set1 = { "DRB1_0101", "DRB1_0301", "DRB1_0401", "DRB1_0404", "DRB1_0405", "DRB1_0701", "DRB1_0802",
				"DRB1_0901", "DRB1_1101", "DRB1_1302", "DRB1_1501" };

		String[] set2 = { "DRB3_0101", "DRB4_0101", "DRB5_0101" };

		// HLA-DPA101-DPB10401 vs HLA-DPA10103-DPB10401
		String[] set3 = { "HLA-DPA10103-DPB10201", "HLA-DPA10103-DPB10401", "HLA-DPA101-DPB10401",
				"HLA-DPA10201-DPB10101", "HLA-DPA10201-DPB10501", "HLA-DPA10301-DPB10402" };

		String[] set4 = { "HLA-DQA10101-DQB10501", "HLA-DQA10102-DQB10602", "HLA-DQA10301-DQB10302",
				"HLA-DQA10401-DQB10402", "HLA-DQA10501-DQB10201", "HLA-DQA10501-DQB10301" };

		// one patient
		String[] set5 = { "HLA-DPA10301-DPB10402", "HLA-DPA101-DPB10401", "HLA-DQA10501-DQB10301",
				"HLA-DQA10301-DQB10302", "DRB1_0301", "DRB1_0701", "DRB4_0101", "DRB3_0101", "HLA-DPA10103-DPB10401" };

		// print all black: genotypes with no novel surfaces: no risk of
		// inhibitor formation
		List<String> variantBlackList = new ArrayList<>();
		List<String> variantIncludeList = new ArrayList<>();
		List<String> variantPatientBlackList = new ArrayList<>();

		List<String> variants = this.getHelper().getVariants();

		for (String variant : variants) {
			List<HeatMapBox> filteredList = heatmapBlacks.stream().filter(p -> variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			if (filteredList.size() == this.getAlleleCounter()) {
				// all blacks predicted no risk
				variantBlackList.add(variant);
			}

			List<HeatMapBox> variantOnly1 = heatmapBlacks.stream()
					.filter(p -> (Arrays.asList(set1)).contains(p.getAllele()) && variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			List<HeatMapBox> variantOnly2 = heatmapBlacks.stream()
					.filter(p -> (Arrays.asList(set2)).contains(p.getAllele()) && variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			List<HeatMapBox> variantOnly3 = heatmapBlacks.stream()
					.filter(p -> (Arrays.asList(set3)).contains(p.getAllele()) && variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			List<HeatMapBox> variantOnly4 = heatmapBlacks.stream()
					.filter(p -> (Arrays.asList(set4)).contains(p.getAllele()) && variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			List<HeatMapBox> variantOnly5 = heatmapBlacks.stream()
					.filter(p -> (Arrays.asList(set5)).contains(p.getAllele()) && variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			if (variantOnly1.size() < 2 || variantOnly2.size() < 2 || variantOnly3.size() < 2
					|| variantOnly4.size() < 2) {
				// calculate
				variantIncludeList.add(variant);
			} else {
				// exclude the patients with that variant
				// for only the ones with risk calculation or for all???
			}

			// patient specific all 8 black
			if (variantOnly5.size() == 8) {
				variantPatientBlackList.add(variant);
			}
		}

		System.out.println("Number of no risk black variants:" + variantBlackList.size());

		Set<String> uniqueVariantsToInclude = new HashSet<>();
		uniqueVariantsToInclude.addAll(variantBlackList);
		uniqueVariantsToInclude.addAll(variantIncludeList);

		try {
			FileWriter writer = new FileWriter(blackCsvFile);
			FileWriter writer2 = new FileWriter(includeCsvFile);
			FileWriter writer3 = new FileWriter(patientSpecificBlackCsvFile);

			CSVUtils.writeLine(writer, Arrays.asList("Variant"));
			CSVUtils.writeLine(writer2, Arrays.asList("Variant"));
			CSVUtils.writeLine(writer3, Arrays.asList("Variant"));

			for (String variant : variantBlackList) {
				List<String> list = new ArrayList<>();
				list.add(variant);
				CSVUtils.writeLine(writer, list);
			}

			for (String variant : uniqueVariantsToInclude) {
				List<String> list = new ArrayList<>();
				list.add(variant);
				CSVUtils.writeLine(writer2, list);
			}

			for (String variant : variantPatientBlackList) {
				List<String> list = new ArrayList<>();
				list.add(variant);
				CSVUtils.writeLine(writer3, list);
			}

			writer.flush();
			writer.close();

			writer2.flush();
			writer2.close();

			writer3.flush();
			writer3.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
