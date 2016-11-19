package uk.ac.bbk.cryst.netprediction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.model.NetPanData;

public abstract class NetPanFileReader {

	Scanner scanner;
	File netFile;
	String fastaFileName;
	String proteinNameAndId;
	String allele;
	PredictionType type;
	
	public NetPanFileReader(PredictionType type,File netFile, String foundProteinNameAndId, String foundAllele) throws FileNotFoundException{
		this.type = type;
		this.netFile = netFile;
		this.fastaFileName = netFile.getName();
		this.proteinNameAndId = foundProteinNameAndId;
		this.allele = foundAllele;
		this.scanner = new Scanner(new FileReader(netFile));
	}
	
	
	public PredictionType getType() {
		return type;
	}


	protected abstract NetPanData read() throws Exception;
}
