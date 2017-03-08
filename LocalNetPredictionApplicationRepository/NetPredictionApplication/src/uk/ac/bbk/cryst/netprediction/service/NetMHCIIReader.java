package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.model.MHCIIPeptideData;
import uk.ac.bbk.cryst.netprediction.model.NetMHCIIData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;

public class NetMHCIIReader extends NetPanFileReader {

	public NetMHCIIReader(File netFile, String foundFileName, String foundAllele) throws FileNotFoundException {
		super(PredictionType.MHCII, netFile, foundFileName, foundAllele);
	}

	@Override
	public NetMHCIIData read() throws Exception {
		NetMHCIIData netMHCIIFileData = new NetMHCIIData(this.allele, this.fastaFileName);

		/*
		 * 
		 * ---------------------------------------------------------------------
		 * --------------------------- Allele pos peptide core 1-log50k(aff)
		 * affinity(nM) Bind Level %Random Identity
		 * ---------------------------------------------------------------------
		 * --------------------------- HLA-DRB10101 0 TMDKSELVQKAKLAE ELVQKAKLA
		 * 0.6168 63.2 WB 32.00 143B_BOVIN
		 * 
		 * 
		 */

		// 2.2 version
		// TODO:reconsider MIN_VALUE stuff and identity maybe you need a different model class and corestartposition

		String pattern = "\\s*((?:HLA\\-)\\w+[-]?\\w+?)" + // allele 1
				"\\s+(\\d+)" + // pos 2
				"\\s+([a-zA-Z]+)" + // peptide 3
				"\\s+([a-zA-Z]+)" + // core peptide 4
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + // mhc 5
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + // IC50 6
				"\\s+(.+)" + // bind level WB or SB 7
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + // rank_percentage 8
				"\\s+(.+)"; // identity 9

		int counter = 1;
		int epitopeCounter = 0;
		List<PeptideData> peptideList = new ArrayList<PeptideData>();

		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (!line.matches(pattern)) {
					continue;
				}

				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(line);

				if (m.find()) {
					// String alleleName = m.group(1);
					String startPositionTxt = m.group(2);
					String peptideTxt = m.group(3);
					String corePeptideTxt = m.group(4);
					String mhcScoreTxt = m.group(5);
					String ic50ScoreTxt = m.group(6);
					String rankPerTxt = m.group(8);
					String identity = m.group(9);
					String binder = "";
				
					int coreStartPosition = peptideTxt.indexOf(corePeptideTxt);

					if (StringUtils.isNotEmpty(m.group(7))) {
						binder = m.group(7).trim();
						epitopeCounter++;
					}

					PeptideData peptide = new MHCIIPeptideData(counter, Integer.valueOf(startPositionTxt), peptideTxt,
							corePeptideTxt, coreStartPosition, Float.valueOf(mhcScoreTxt),
							Float.valueOf(ic50ScoreTxt), Float.valueOf(rankPerTxt), identity, binder);
			
					peptideList.add(peptide);

					counter++;

				}

			}

			netMHCIIFileData.setPeptideList(peptideList);
			netMHCIIFileData.setIdentifiedEpitopes(epitopeCounter);
		}

		catch (Exception ex) {
			throw ex;
		}

		finally {
			close();
		}
		return netMHCIIFileData;
	}

	public void close() {
		scanner.close();
	}

}
