package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.util.CSVUtils;

public class NovelSurfaceResultsProcessor {

	PropertiesHelper properties;
	String novelSurfacesResultFilePath;
	boolean proteomeScanningOn = false;
	int alleleCounter = 0;
	boolean onlyDR = true;
	
	
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

	public void setProperties(PropertiesHelper properties) {
		this.properties = properties;
	}

	public String getNovelSurfacesResultFilePath() {
		return novelSurfacesResultFilePath;
	}

	public void setNovelSurfacesResultFilePath(String novelSurfacesResultFilePath) {
		this.novelSurfacesResultFilePath = novelSurfacesResultFilePath;
	}

	public NovelSurfaceResultsProcessor(boolean proteomeScanning) throws IOException {
		super();

		properties = new PropertiesHelper();
		novelSurfacesResultFilePath = properties.getValue("novelSurfacesResultFilePath");
		proteomeScanningOn = proteomeScanning;
	}

	public void readNovelSurfaceResults(Float threshold) {

		// Variant,Allele,Peptide_1,CorePeptide_1,IC50_1,Peptide_2,CorePeptide_2,IC50_2,Colour
		// R-3-I,DRB1_0101,CLLRFCFSATRRYYL,FCFSATRRY,11.78,CLLRFCFSATRRYYL,FCFSATRRY,11.78,12/12
		// G-22-C,DRB1_0101,WDYMQSDLGELPVDA,MQSDLGELP,449.39,null,null,null,450/grey
		// T-49-A,DRB1_0101,,,,null,null,null,black

		/*String[] DRAlleles = { "DRB1_0101", "DRB1_0301", "DRB1_0401", "DRB1_0404", "DRB1_0405", "DRB1_0701",
				"DRB1_0802", "DRB1_0901", "DRB1_1101", "DRB1_1302", "DRB1_1501", "DRB3_0101", "DRB4_0101",
				"DRB5_0101" };
		"HLA-DPA10103-DPB10201","HLA-DPA10103-DPB10401","HLA-DPA10201-DPB10101",
		"HLA-DPA10201-DPB10501","HLA-DPA10301-DPB10402","HLA-DQA10101-DQB10501",
		"HLA-DQA10102-DQB10602","HLA-DQA10301-DQB10302","HLA-DQA10401-DQB10402",
		"HLA-DQA10501-DQB10201","HLA-DQA10501-DQB10301"*/

		Map<String, Integer> variantBlacks = new LinkedHashMap<>();
		Map<String, Integer> variantGreys = new LinkedHashMap<>();

		File root = new File(this.getNovelSurfacesResultFilePath());
        FilenameFilter beginswith = new FilenameFilter()
        {
         public boolean accept(File directory, String filename) {
              return filename.startsWith("novelSurfaces_");
          }
        };

        File[] files = root.listFiles(beginswith);
		
		for (File novelSurfaceResultsFile: files) {
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
					
					
					if(isOnlyDR() && allele.startsWith("HLA")){
						continue;
					}

					if (!colour.equals("black")) {
						String[] items = colour.split("/");

						if (this.isProteomeScanningOn()) {
							if (items[1].equals("grey")) {
								if (variantBlacks.containsKey(variant)) {
									variantBlacks.put(variant, variantBlacks.get(variant).intValue() + 1);
								} else {
									variantBlacks.put(variant, 1);
								}

								if (variantGreys.containsKey(variant)) {
									variantGreys.put(variant, variantGreys.get(variant).intValue() + 1);
								} else {
									variantGreys.put(variant, 1);
								}
							} else {
								if (Float.valueOf(items[1]) >= threshold) {
									if (variantBlacks.containsKey(variant)) {
										variantBlacks.put(variant, variantBlacks.get(variant).intValue() + 1);
									} else {
										variantBlacks.put(variant, 1);
									}
								}
							}
						} // if isProteomeScanningOn
						else {
							if (items[1].equals("grey")) {
								if (variantGreys.containsKey(variant)) {
									variantGreys.put(variant, variantGreys.get(variant).intValue() + 1);
								} else {
									variantGreys.put(variant, 1);
								}
							}

							if (Float.valueOf(items[0]) >= threshold) {
								if (variantBlacks.containsKey(variant)) {
									variantBlacks.put(variant, variantBlacks.get(variant).intValue() + 1);
								} else {
									variantBlacks.put(variant, 1);
								}
							}

						}

					}//if colour not black
					else{
						if (variantBlacks.containsKey(variant)) {
							variantBlacks.put(variant, variantBlacks.get(variant).intValue() + 1);
						} else {
							variantBlacks.put(variant, 1);
						}
					}

				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}

		} // for drAlleles

		// print all black: genotypes with no novel surfaces: no risk of
		// inhibitor formation
		// copy them to variants_allBlack.csv
		List<String> variantBlackList = new ArrayList<>();

		int numberOfNoNovelGenotypes = 0;
		for (String key : variantBlacks.keySet()) {
			if (variantBlacks.get(key).intValue() == this.getAlleleCounter()) {
				System.out.println(key);
				variantBlackList.add(key);
				numberOfNoNovelGenotypes++;
			}
		}

		writeToCsvFile(variantBlackList, threshold);
		System.out.println("Number of Novel Surfaces:" + numberOfNoNovelGenotypes);
	}

	private void writeToCsvFile(List<String> variantBlackList, Float threshold) {
		String csvFile = "data//output//variants_allBlack_" + threshold.intValue() + ".csv";
		try {
			FileWriter writer = new FileWriter(csvFile);
			CSVUtils.writeLine(writer, Arrays.asList("Variant"));

			for (String variant : variantBlackList) {
				List<String> list = new ArrayList<>();
				list.add(variant);
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
