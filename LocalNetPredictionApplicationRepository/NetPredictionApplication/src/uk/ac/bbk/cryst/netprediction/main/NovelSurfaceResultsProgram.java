package uk.ac.bbk.cryst.netprediction.main;

import java.io.IOException;

import uk.ac.bbk.cryst.netprediction.service.NovelSurfaceResultsProcessor;

public class NovelSurfaceResultsProgram {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		NovelSurfaceResultsProcessor processor = new NovelSurfaceResultsProcessor();
		processor.readNovelSurfaceResults();

	}

}
