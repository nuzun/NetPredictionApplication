package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.HeatMapBox;
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

	List<HeatMapBox> boxList = new ArrayList<>();

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

		readNovelSurfaceResultFiles();

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

					HeatMapBox box = new HeatMapBox(allele, variant, colour);
					boxList.add(box);

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
	
	public void createHeatMapFiles() throws IOException{
		//write first line all variants comma separated
		StringJoiner content = new StringJoiner(",");
		String newLine = "\n";
		content.add("allele");
		for(String variant : helper.getHeatmapVariants()){
			content.add(variant);
		}
		String fullContent = content.toString() + newLine;
		

		//then for each variant and for each allele 
		//find the box and examine the colour
		//item[0] for prot scan off
		//item[1] for prot scan on, could be grey
		//could also be black
		
		for(String allele : helper.readAlleleFile(this.getPredictionType())){
			content = new StringJoiner(",");
			content.add(allele);
			
			for(String variant : helper.getHeatmapVariants()){
				List<HeatMapBox> filteredList = boxList.stream().filter(p -> variant.equals(p.getVariant()) 
						&& allele.equals(p.getAllele()))
						.collect(Collectors.toList());
				HeatMapBox box = filteredList.get(0);
				
				if(!box.getColour().equals("black")){
					String[] items = box.getColour().split("/");
					if(this.isProteomeScanningOn() && items[1].equals("grey")){
						content.add(String.valueOf(1101f));
					}
					else if(this.isProteomeScanningOn() && !items[1].equals("grey") ){
						content.add(items[1]);
					}
					else{
						content.add(items[0]);
					}
				}
				else{
					//black
					content.add(String.valueOf(1001f));
				}
			}
			fullContent+=content.toString()+newLine;
		}
		
		File file = new File("data//output//heatmap_" + this.getPredictionType()+ "_" + this.isProteomeScanningOn() +".csv");
		FileHelper.writeToFile(file, fullContent);
		
	}

	public void createVariantFiles() {
		Float[] thresholds = { 1000f, 500f, 300f, 200f, 100f, 50f, 25f };

		for (Float threshold : thresholds) {
			System.out.println("Threshold:" + threshold);
			createVariantFile(threshold);
		}
	}

	public void createVariantFile(Float threshold) {

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

		List<HeatMapBox> variantBlacks = new ArrayList<>();
		List<HeatMapBox> variantGreys = new ArrayList<>();

		for (HeatMapBox box : boxList) {

			if (isOnlyDR() && box.getAllele().startsWith("HLA")) {
				continue;
			}

			if (!box.getColour().equals("black")) {
				String[] items = box.getColour().split("/");

				if (this.isProteomeScanningOn()) {
					if (items[1].equals("grey")) {
						variantBlacks.add(box);
						variantGreys.add(box);
					} else {
						if (Float.valueOf(items[1]) >= threshold) {
							variantBlacks.add(box);
						}
					}
				} // if isProteomeScanningOn
				else {
					if (items[1].equals("grey")) {
						variantGreys.add(box);
					}

					if (Float.valueOf(items[0]) >= threshold) {
						variantBlacks.add(box);

					}
				}

			} // if colour not black
			else {
				variantBlacks.add(box);
			}

		}

		writeToVariantsCsvFile(variantBlacks, threshold);

	}

	private void writeToVariantsCsvFile(List<HeatMapBox> variantBlacks, Float threshold) {
		String blackCsvFile = "data//output//variants_allBlack_" + threshold.intValue() + ".csv";
		String includeCsvFile = "data//output//variants_include_" + threshold.intValue() + ".csv";
		String patientSpecificBlackCsvFile = "data//output//variants_patient_allBlack_" + threshold.intValue() + ".csv";

		String[] set1 = { "DRB1_0101", "DRB1_0301", "DRB1_0401", "DRB1_0404", "DRB1_0405", "DRB1_0701", "DRB1_0802",
				"DRB1_0901", "DRB1_1101", "DRB1_1302", "DRB1_1501" };

		String[] set2 = { "DRB3_0101", "DRB4_0101", "DRB5_0101" };

		//HLA-DPA101-DPB10401 vs HLA-DPA10103-DPB10401
		String[] set3 = { "HLA-DPA10103-DPB10201", "HLA-DPA10103-DPB10401", "HLA-DPA101-DPB10401","HLA-DPA10201-DPB10101",
				"HLA-DPA10201-DPB10501", "HLA-DPA10301-DPB10402" };

		String[] set4 = { "HLA-DQA10101-DQB10501", "HLA-DQA10102-DQB10602", "HLA-DQA10301-DQB10302",
				"HLA-DQA10401-DQB10402", "HLA-DQA10501-DQB10201", "HLA-DQA10501-DQB10301" };
		
		String[] set5 = {"HLA-DPA10301-DPB10402","HLA-DPA101-DPB10401","HLA-DQA10501-DQB10301","HLA-DQA10301-DQB10302",
				"DRB1_0301","DRB1_0701","DRB4_0101","DRB3_0101","HLA-DPA10103-DPB10401"};

		// print all black: genotypes with no novel surfaces: no risk of
		// inhibitor formation
		List<String> variantBlackList = new ArrayList<>();
		List<String> variantIncludeList = new ArrayList<>();
		List<String> variantPatientBlackList = new ArrayList<>();


		List<String> variants = helper.getVariants();
		
		for (String variant : variants) {
			List<HeatMapBox> filteredList = variantBlacks.stream().filter(p -> variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			if (filteredList.size() == this.getAlleleCounter()) {
				//all blacks predicted no risk
				variantBlackList.add(variant);
			}

			List<HeatMapBox> variantOnly1 = variantBlacks.stream()
					.filter(p -> (Arrays.asList(set1)).contains(p.getAllele()) && variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			List<HeatMapBox> variantOnly2 = variantBlacks.stream()
					.filter(p -> (Arrays.asList(set2)).contains(p.getAllele()) && variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			List<HeatMapBox> variantOnly3 = variantBlacks.stream()
					.filter(p -> (Arrays.asList(set3)).contains(p.getAllele()) && variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			List<HeatMapBox> variantOnly4 = variantBlacks.stream()
					.filter(p -> (Arrays.asList(set4)).contains(p.getAllele()) && variant.equals(p.getVariant()))
					.collect(Collectors.toList());

			List<HeatMapBox> variantOnly5 = variantBlacks.stream()
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
			if(variantOnly5.size() == 8){
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
