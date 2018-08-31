package uk.ac.bbk.cryst.netprediction.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;

public class MHCINovelSurfaceProcessorHelper {

	PropertiesHelper properties = new PropertiesHelper();
	List<String> variants = new ArrayList<>();
	List<String> heatmapVariants = new ArrayList<>();

	public MHCINovelSurfaceProcessorHelper() throws FileNotFoundException, IOException {

		readFullVariantsFile(properties.getValue("variantFileFullPath")); // variants
																			// for
																			// all
																			// mutations
																			// we
																			// are
																			// interested
																			// in
		readHeatmapVariantsFile(); // heatmapVariants just contains the variants
									// we want to generate the heatmap for
	}

	private void readHeatmapVariantsFile() throws IOException {
		String heatmapVariantFileFullPath = properties.getValue("heatmapVariantFileFullPath");
		File heatmapVariantFile = new File(heatmapVariantFileFullPath);
		String line = "";

		BufferedReader br = new BufferedReader(new FileReader(heatmapVariantFile));
		try {
			while ((line = br.readLine()) != null && !line.trim().equals("")) {
				heatmapVariants.add(line.trim());
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void readFullVariantsFile(String value) throws FileNotFoundException {
		File variantFile = new File(value);
		String line = "";

		BufferedReader br = new BufferedReader(new FileReader(variantFile));
		try {
			while ((line = br.readLine()) != null && !line.trim().equals("")) {
				variants.add(line.trim());
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public List<String> readAlleleFile(PredictionType type) throws FileNotFoundException {
		List<String> alleles = new ArrayList<>();
		String path = null;
		switch (type) {
		case CTLPAN:
			path = "data//input//mhcI.csv";
			break;
		default:
			path = "data//input//mhcI_full_list.csv";
			break;
		}
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(path).getGroupData();
		for (String allele : groupData.getAlleleMap().keySet()) {
			alleles.add(allele);

		}

		Collections.sort(alleles);
		return alleles;

	}

	public List<String> getHeatmapVariants() {
		return heatmapVariants;
	}

	public List<String> getVariants() {
		return variants;
	}
}
