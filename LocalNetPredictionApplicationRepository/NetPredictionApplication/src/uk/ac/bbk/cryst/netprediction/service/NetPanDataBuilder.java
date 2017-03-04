package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;

public class NetPanDataBuilder {

	List<String> patternList;
	PredictionType type;

	public NetPanDataBuilder(PredictionType type) throws IOException {
		PropertiesHelper properties = new PropertiesHelper();
		this.type = type;
		patternList = new ArrayList<>();

		if ((this.type == PredictionType.MHCII) || (this.type == PredictionType.MHCIIPAN20)
				|| (this.type == PredictionType.MHCIIPAN31)) {
			patternList.add(properties.getValue("scoreFileNamePatternMHCII"));
			patternList.add(properties.getValue("scoreFileNamePatternMHCIIDPQ"));
		} else
			patternList.add(properties.getValue("scoreFileNamePatternMHCI"));
	}

	public NetPanData buildSingleFileData(File outputDir) throws Exception {

		NetPanData netPanData = null;
		Pattern p = Pattern.compile(patternList.get(0));

		if (!outputDir.isDirectory()) {
			// Create a Pattern object
			for (String rx : patternList) {
				p = Pattern.compile(rx);
				if (p.matcher(outputDir.getName()).matches()) {
					break;
				}
			}

			// Now create matcher object.
			Matcher m = p.matcher(outputDir.getName());

			if (m.find()) {
				String foundProteinNameAndId = m.group(2);
				String foundAllele = m.group(3);
				if (StringUtils.isNotEmpty(foundProteinNameAndId)) {
					// found the file
					// read the file and find the rank
					// return the rank with the supertype
					NetPanFileReader reader = NetPanFileReaderFactory.getReader(type, outputDir, foundProteinNameAndId,
							foundAllele);
					netPanData = reader.read();
				}
			}
		}
		return netPanData;
	}

	/*
	 * If the path is a full file path then we're reading the score file
	 * directly from the inner location. If it is a directory then we're reading
	 * the folder above which is the allele file name
	 */
	public List<NetPanData> buildFileData(File outputDir) throws Exception {

		List<NetPanData> netPanDataList = new ArrayList<NetPanData>();
		Pattern p = Pattern.compile(patternList.get(0));

		if (!outputDir.isDirectory()) {
			// Create a Pattern object
			for (String rx : patternList) {
				p = Pattern.compile(rx);
				if (p.matcher(outputDir.getName()).matches()) {
					break;
				}
			}
			// Now create matcher object.
			Matcher m = p.matcher(outputDir.getName());

			if (m.find()) {
				String foundProteinNameAndId = m.group(2);
				String foundAllele = m.group(3);
				if (StringUtils.isNotEmpty(foundProteinNameAndId)) {
					// found the file
					// read the file and find the rank
					// return the rank with the supertype
					NetPanFileReader reader = NetPanFileReaderFactory.getReader(type, outputDir, foundProteinNameAndId,
							foundAllele);
					NetPanData netPanData = reader.read();
					netPanDataList.add(netPanData);
				}
			}
			return netPanDataList;
		}

		// for each file in the folder check the name
		// if it matches our input, then find the rank of the peptide
		for (final File fileEntry : outputDir.listFiles()) {
			if (fileEntry.isDirectory()) {
				// System.exit(0);
				continue;
			}

			// Create a Pattern object
			for (String rx : patternList) {
				p = Pattern.compile(rx);
				if (p.matcher(fileEntry.getName()).matches()) {
					break;
				}
			}
			// Now create matcher object.
			Matcher m = p.matcher(fileEntry.getName());

			if (m.find()) {
				String foundProteinNameAndId = m.group(2);
				String foundAllele = m.group(3);
				if (StringUtils.isNotEmpty(foundProteinNameAndId)) {
					// found the file
					// read the file and find the rank
					// return the rank with the supertype
					NetPanFileReader reader = NetPanFileReaderFactory.getReader(type, fileEntry, foundProteinNameAndId,
							foundAllele);
					NetPanData netPanData = reader.read();
					netPanDataList.add(netPanData);
				}

			} else {
				// no match. this is not our filetype
				continue;
			}

		}

		Collections.sort(netPanDataList);
		return netPanDataList;
	}

	// return single file object with the specified allele and filename
	public NetPanData buildFileData(String fastaFileName, String alleleName, File outputDir) throws Exception {
		// for each file in the folder check the name
		// if it matches our input, then find the rank of the peptide
		NetPanData netPanData = null;
		Pattern p = Pattern.compile(patternList.get(0));

		// full path of the file
		if (!outputDir.isDirectory()) {
			// Create a Pattern object
			for (String rx : patternList) {
				p = Pattern.compile(rx);
				if (p.matcher(outputDir.getName()).matches()) {
					break;
				}
			}
			// Now create matcher object.
			Matcher m = p.matcher(outputDir.getName());

			if (m.find()) {
				String foundProteinNameAndId = m.group(2);
				String foundAllele = m.group(3);
				if (StringUtils.isNotEmpty(foundProteinNameAndId)
						&& StringUtils.equals(foundProteinNameAndId, fastaFileName)
						&& StringUtils.equals(foundAllele, alleleName)) {

					// found the file
					// read the file and find the rank
					// return the rank with the supertype
					NetPanFileReader reader = NetPanFileReaderFactory.getReader(type, outputDir, foundProteinNameAndId,
							foundAllele);
					netPanData = reader.read();
					return netPanData;
				}

				else if (StringUtils.isNotEmpty(foundProteinNameAndId)
						&& foundProteinNameAndId.startsWith(fastaFileName)
						&& StringUtils.equals(foundAllele, alleleName)) {
					NetPanFileReader reader = NetPanFileReaderFactory.getReader(type, outputDir, foundProteinNameAndId,
							foundAllele);
					netPanData = reader.read();
					return netPanData;
				}

			}

			return netPanData;

		}

		for (final File fileEntry : outputDir.listFiles()) {
			if (fileEntry.isDirectory()) {
				// System.exit(0);
				continue;
			}
			// Create a Pattern object
			for (String rx : patternList) {
				p = Pattern.compile(rx);
				if (p.matcher(fileEntry.getName()).matches()) {
					break;
				}
			}

			// Now create matcher object.
			Matcher m = p.matcher(fileEntry.getName());

			if (m.find()) {
				String foundProteinNameAndId = m.group(2);
				String foundAllele = m.group(3);
				if (StringUtils.isNotEmpty(foundProteinNameAndId)
						&& StringUtils.equals(foundProteinNameAndId, fastaFileName)
						&& StringUtils.equals(foundAllele, alleleName)) {

					// found the file
					// read the file and find the rank
					// return the rank with the supertype
					NetPanFileReader reader = NetPanFileReaderFactory.getReader(type, fileEntry, foundProteinNameAndId,
							foundAllele);
					netPanData = reader.read();
					return netPanData;
				}

				else if (StringUtils.isNotEmpty(foundProteinNameAndId)
						&& foundProteinNameAndId.startsWith(fastaFileName)
						&& StringUtils.equals(foundAllele, alleleName)) {
					NetPanFileReader reader = NetPanFileReaderFactory.getReader(type, fileEntry, foundProteinNameAndId,
							foundAllele);
					netPanData = reader.read();
					return netPanData;
				}

			} else {
				// no match. this is not our filetype
				continue;
			}

		}

		return null;
	}
}
