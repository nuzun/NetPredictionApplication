package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;

public class NetPanFileReaderFactory {

	public static NetPanFileReader getReader(PredictionType type,File netFile, String foundProteinNameAndId, String foundAllele) throws FileNotFoundException{
		switch (type){
			case CTL:
			case CTLPAN:
				return new NetCTLReader(netFile,foundProteinNameAndId,foundAllele);
			case MHCI:
			case MHCIPAN:
				return new NetMHCPanReader(netFile, foundProteinNameAndId, foundAllele);
			case MHCII:
				return new NetMHCIIReader(netFile, foundProteinNameAndId, foundAllele);
			case MHCIIPAN20:
				return new NetMHCIIPanReader(netFile, foundProteinNameAndId, foundAllele);
			case MHCIIPAN31:
				return new NewNetMHCIIPanReader(netFile, foundProteinNameAndId, foundAllele);
			default: 
				return null;
		}
	}
}
