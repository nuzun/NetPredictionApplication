package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.HeatMapBox;
import uk.ac.bbk.cryst.netprediction.util.FileHelper;
import uk.ac.bbk.cryst.netprediction.util.MHCIIPanNovelSurfaceProcessorHelper;

public class MHCIIPanNovelSurfaceResultsProcessor {

	PropertiesHelper properties;
	MHCIIPanNovelSurfaceProcessorHelper helper;
	String novelSurfacesResultFilePath;
	boolean proteomeScanningOn = false;
	PredictionType predictionType;
	List<HeatMapBox> boxList = new ArrayList<>();
	Map<String, String> nomenclatureList;

	public MHCIIPanNovelSurfaceResultsProcessor(boolean proteomeScanning, PredictionType predictionType)
			throws IOException {
		super();
		this.properties = new PropertiesHelper();
		this.helper = new MHCIIPanNovelSurfaceProcessorHelper();
		setNomenclature();

		switch (predictionType) {
		case MHCIIPAN31:
			this.setNovelSurfacesResultFilePath(properties.getValue("novelSurfacesResultFilePathMHCIIPan"));
			break;
		default:
			break;
		}

		this.setPredictionType(predictionType);
		this.setProteomeScanningOn(proteomeScanning);

	}

	public void process() throws IOException {
		readNovelSurfaceResultFiles(); // populate full heatbox list
		createHeatMapFiles();
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
			content.add(variant.replace("-", ""));
		}
		String fullContent = content.toString() + newLine;

		// then for each variant and for each allele
		// find the box and examine the colour
		// item[0] for prot scan off
		// item[1] for prot scan on, could be grey
		// could also be black

		for (String allele : this.getHelper().readAlleleFile(this.getPredictionType())) {
			content = new StringJoiner(",");
			content.add(getNomenclature(allele));

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

	private CharSequence getNomenclature(String allele) {
		if(StringUtils.isBlank(nomenclatureList.get(allele))){
			System.out.println("Update nomeclature list!!!");
			throw new IllegalArgumentException("Update nomeclature list!!!");
		}
		return nomenclatureList.get(allele);
	}

	/************************ GETTERS and SETTERS ***************************/
	public PropertiesHelper getProperties() {
		return properties;
	}

	public void setProperties(PropertiesHelper properties) {
		this.properties = properties;
	}

	public MHCIIPanNovelSurfaceProcessorHelper getHelper() {
		return helper;
	}

	public void setHelper(MHCIIPanNovelSurfaceProcessorHelper helper) {
		this.helper = helper;
	}

	public String getNovelSurfacesResultFilePath() {
		return novelSurfacesResultFilePath;
	}

	public void setNovelSurfacesResultFilePath(String novelSurfacesResultFilePath) {
		this.novelSurfacesResultFilePath = novelSurfacesResultFilePath;
	}

	public boolean isProteomeScanningOn() {
		return proteomeScanningOn;
	}

	public void setProteomeScanningOn(boolean proteomeScanningOn) {
		this.proteomeScanningOn = proteomeScanningOn;
	}

	public PredictionType getPredictionType() {
		return predictionType;
	}

	public void setPredictionType(PredictionType predictionType) {
		this.predictionType = predictionType;
	}

	public List<HeatMapBox> getBoxList() {
		return boxList;
	}

	public void setBoxList(List<HeatMapBox> boxList) {
		this.boxList = boxList;
	}

	private void setNomenclature() {
		nomenclatureList = new HashMap<>();
		nomenclatureList.put("HLA-DPA10103-DPB10201", "HLA-DPA1*01:03-DPB1*02:01");
		nomenclatureList.put("HLA-DPA10103-DPB10401", "HLA-DPA1*01:03-DPB1*04:01");
		nomenclatureList.put("HLA-DPA10103-DPB10401", "HLA-DPA1*01:03-DPB1*04:01");
		nomenclatureList.put("HLA-DPA10201-DPB10101", "HLA-DPA1*02:01-DPB1*01:01");
		nomenclatureList.put("HLA-DPA10201-DPB10501", "HLA-DPA1*02:01-DPB1*05:01");
		nomenclatureList.put("HLA-DPA10301-DPB10402", "HLA-DPA1*03:01-DPB1*04:02");
		nomenclatureList.put("HLA-DQA10101-DQB10501", "HLA-DQA1*01:01-DQB1*05:01");
		nomenclatureList.put("HLA-DQA10102-DQB10602", "HLA-DQA1*01:02-DQB1*06:02");
		nomenclatureList.put("HLA-DQA10301-DQB10302", "HLA-DQA1*03:01-DQB1*03:02");
		nomenclatureList.put("HLA-DQA10401-DQB10402", "HLA-DQA1*04:01-DQB1*04:02");
		nomenclatureList.put("HLA-DQA10501-DQB10201", "HLA-DQA1*05:01-DQB1*02:01");
		nomenclatureList.put("HLA-DQA10501-DQB10301", "HLA-DQA1*05:01-DQB1*03:01");
		nomenclatureList.put("DRB1_0101", "HLA-DRB1*01:01");
		nomenclatureList.put("DRB1_0301", "HLA-DRB1*03:01");
		nomenclatureList.put("DRB1_0401", "HLA-DRB1*04:01");
		nomenclatureList.put("DRB1_0404", "HLA-DRB1*04:04");
		nomenclatureList.put("DRB1_0405", "HLA-DRB1*04:05");
		nomenclatureList.put("DRB1_0701", "HLA-DRB1*07:01");
		nomenclatureList.put("DRB1_0802", "HLA-DRB1*08:02");
		nomenclatureList.put("DRB1_0901", "HLA-DRB1*09:01");
		nomenclatureList.put("DRB1_1101", "HLA-DRB1*11:01");
		nomenclatureList.put("DRB1_1302", "HLA-DRB1*13:02");
		nomenclatureList.put("DRB1_1501", "HLA-DRB1*15:01");
		nomenclatureList.put("DRB3_0101", "HLA-DRB3*01:01");
		nomenclatureList.put("DRB4_0101", "HLA-DRB4*01:01");
		nomenclatureList.put("DRB5_0101", "HLA-DRB5*01:01");
	}
}
