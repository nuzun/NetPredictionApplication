package uk.ac.bbk.cryst.netprediction.model;

import java.util.List;

public class NetCTLData extends NetPanData{

	public NetCTLData(String allele, String fastaFileName){
		super(allele,fastaFileName);
	}

	@Override
	public List<PeptideData> getStrongBinderPeptides() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PeptideData> getWeakBinderPeptides() {
		// TODO Auto-generated method stub
		return null;
	}

}
