package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;

public class NovelSurfaceResultsProcessor {

	PropertiesHelper properties;
	String novelSurfacesFileFullPath;

	public PropertiesHelper getProperties() {
		return properties;
	}

	public void setProperties(PropertiesHelper properties) {
		this.properties = properties;
	}

	public String getNovelSurfacesFileFullPath() {
		return novelSurfacesFileFullPath;
	}

	public void setNovelSurfacesFileFullPath(String novelSurfacesFileFullPath) {
		this.novelSurfacesFileFullPath = novelSurfacesFileFullPath;
	}

	public NovelSurfaceResultsProcessor() throws IOException {
		super();

		properties = new PropertiesHelper();
		novelSurfacesFileFullPath = properties.getValue("novelSurfacesFileFullPath");
	}

	public void readNovelSurfaceResults() {

		// Variant,Allele,Peptide_1,CorePeptide_1,IC50_1,Peptide_2,CorePeptide_2,IC50_2,Colour
		// R-3-I,DRB1_0101,CLLRFCFSATRRYYL,FCFSATRRY,11.78,CLLRFCFSATRRYYL,FCFSATRRY,11.78,12/12
		// G-22-C,DRB1_0101,WDYMQSDLGELPVDA,MQSDLGELP,449.39,null,null,null,450/grey
		// T-49-A,DRB1_0101,,,,null,null,null,black
		int numberOfBlacks = 0;
		int numberOfGreys = 0;
		int totalNumber = 0;

		File novelSurfaceResultsFile = new File(this.getNovelSurfacesFileFullPath());
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

				if (!colour.equals("black")) {
					String[] items = colour.split("/");
					if (items[1].equals("grey")) {
						numberOfGreys++;
					}
				} else {
					numberOfBlacks++;
				}

				totalNumber++;

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			scanner.close();
		}
		
		System.out.println("Black:" + numberOfBlacks);
		System.out.println("Grey:" + numberOfGreys);
		System.out.println("Total:" + totalNumber);
		

	}
}
