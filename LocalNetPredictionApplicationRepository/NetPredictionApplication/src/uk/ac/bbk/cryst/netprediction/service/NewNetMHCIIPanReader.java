package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.model.MHCIIPanPeptideData;
import uk.ac.bbk.cryst.netprediction.model.NetMHCIIPanData;
import uk.ac.bbk.cryst.netprediction.model.PeptideData;

public class NewNetMHCIIPanReader extends NetPanFileReader {

	public NewNetMHCIIPanReader(File netMHCIIPanFile, String foundProteinNameAndId, String foundAllele)
			throws FileNotFoundException {
		super(PredictionType.MHCIIPAN31, netMHCIIPanFile, foundProteinNameAndId, foundAllele);

	}

	public NetMHCIIPanData read() throws Exception {

		NetMHCIIPanData netMHCIIPanFileData = new NetMHCIIPanData(this.allele, this.fastaFileName);

		// 2.0 output is different from latest 3.1 version!!!!
		String pattern = "\\s*(\\d+)" + // pos 1
				"\\s+((?:HLA\\-)?\\w+[_|\\-]\\w+)" + // allele 2 DRB1_0101 | HLA-DPA10103-DPB10201
				"\\s+([a-zA-Z]+)" + // peptide 3
				"\\s+.+" + // identity
				"\\s+(\\d+)" + // core start pos 4
				"\\s+([a-zA-Z]+)" + // core peptide 5
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + //core_rel 6
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + // mhc 7
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + // IC50 8
				"\\s+([-+]?(?:\\d*[.])?\\d+)" + // rank_percentage 9
			    "\\s+([\\w.]+)" + //exp_bind 10
				"\\s*(?:<=\\s*(\\w+))?\\s*"; // WB or SB? 11
		
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
					String startPositionTxt = m.group(1);
					// String alleleName = m.group(2);
					String peptideTxt = m.group(3);
					String coreStartPositionTxt = m.group(4);
					String corePeptideTxt = m.group(5);
					String coreRelTxt =  m.group(6);
					String mhcScoreTxt = m.group(7);
					String ic50ScoreTxt = m.group(8);
					String rankPerTxt = m.group(9);
					String expBindTxt = m.group(10);
					String binder = "";

					if (StringUtils.isNotEmpty(m.group(11))) {
						binder = m.group(11).trim();
						epitopeCounter++;
					}

					PeptideData peptide = new MHCIIPanPeptideData(counter, Integer.valueOf(startPositionTxt),
							peptideTxt, Integer.valueOf(coreStartPositionTxt), corePeptideTxt, Float.valueOf(coreRelTxt),
							Float.valueOf(mhcScoreTxt), Float.valueOf(ic50ScoreTxt), Float.valueOf(rankPerTxt),
							expBindTxt, binder);

					peptideList.add(peptide);

					counter++;

				}

			}

			netMHCIIPanFileData.setPeptideList(peptideList);
			netMHCIIPanFileData.setIdentifiedEpitopes(epitopeCounter);
		}

		catch (Exception ex) {
			throw ex;
		}

		finally {
			close();
		}
		return netMHCIIPanFileData;
	}

	public void close() {
		scanner.close();
	}

}
