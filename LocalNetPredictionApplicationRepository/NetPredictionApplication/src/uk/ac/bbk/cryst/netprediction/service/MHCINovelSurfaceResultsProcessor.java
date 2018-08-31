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

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.HeatMapBox;
import uk.ac.bbk.cryst.netprediction.util.FileHelper;
import uk.ac.bbk.cryst.netprediction.util.MHCINovelSurfaceProcessorHelper;

public class MHCINovelSurfaceResultsProcessor {

	PropertiesHelper properties;
	MHCINovelSurfaceProcessorHelper helper;
	String novelSurfacesResultFilePath;
	boolean proteomeScanningOn = false;
	PredictionType predictionType;
	List<HeatMapBox> boxList = new ArrayList<>();

	public MHCINovelSurfaceResultsProcessor(boolean proteomeScanning, PredictionType predictionType)
			throws IOException {
		super();
		this.properties = new PropertiesHelper();
		this.helper = new MHCINovelSurfaceProcessorHelper();

		switch (predictionType) {
		case CTLPAN:
			this.setNovelSurfacesResultFilePath(properties.getValue("novelSurfacesResultFilePathCTLPan"));
			break;
		default:
			break;
		}

		this.setPredictionType(predictionType);
		this.setProteomeScanningOn(proteomeScanning);

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

					if (elements.length != 7) {
						System.out.println("Error: Missing data");
						return;
					}

					String variant = elements[0];
					String allele = elements[1];
					String peptide_1 = elements[2];
					String IC50_1 = elements[3];
					String peptide_2 = elements[4];
					String IC50_2 = elements[5];
					String colour = elements[6];

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
		Map<String, String> nomenclatureList = new HashMap<>();
		nomenclatureList.put("HLA-A02:01", "HLA-A*02:01");
		nomenclatureList.put("HLA-A01:01", "HLA-A*01:01");
		nomenclatureList.put("HLA-A03:01", "HLA-A*03:01");
		nomenclatureList.put("HLA-A24:02", "HLA-A*24:02");
		nomenclatureList.put("HLA-A11:01", "HLA-A*11:01");
		nomenclatureList.put("HLA-A29:02", "HLA-A*29:02");
		nomenclatureList.put("HLA-A32:01", "HLA-A*32:01");
		nomenclatureList.put("HLA-A68:01", "HLA-A*68:01");
		nomenclatureList.put("HLA-A31:01", "HLA-A*31:01");
		nomenclatureList.put("HLA-A26:01", "HLA-A*26:01");
		nomenclatureList.put("HLA-B07:02", "HLA-B*07:02");
		nomenclatureList.put("HLA-B08:01", "HLA-B*08:01");
		nomenclatureList.put("HLA-B44:02", "HLA-B*44:02");
		nomenclatureList.put("HLA-B35:01", "HLA-B*35:01");
		nomenclatureList.put("HLA-B51:01", "HLA-B*51:01");
		nomenclatureList.put("HLA-B40:01", "HLA-B*40:01");
		nomenclatureList.put("HLA-B44:03", "HLA-B*44:03");
		nomenclatureList.put("HLA-B15:01", "HLA-B*15:01");
		nomenclatureList.put("HLA-B18:01", "HLA-B*18:01");
		nomenclatureList.put("HLA-B57:01", "HLA-B*57:01");

		return nomenclatureList.get(allele);
	}

	
	/************************GETTERS and SETTERS***************************/
	public PropertiesHelper getProperties() {
		return properties;
	}

	public void setProperties(PropertiesHelper properties) {
		this.properties = properties;
	}

	public MHCINovelSurfaceProcessorHelper getHelper() {
		return helper;
	}

	public void setHelper(MHCINovelSurfaceProcessorHelper helper) {
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

}
