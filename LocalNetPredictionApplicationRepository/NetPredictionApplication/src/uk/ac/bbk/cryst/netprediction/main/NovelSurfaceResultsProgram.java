package uk.ac.bbk.cryst.netprediction.main;

import java.io.IOException;

import uk.ac.bbk.cryst.netprediction.service.NovelSurfaceResultsProcessor;

public class NovelSurfaceResultsProgram {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		NovelSurfaceResultsProcessor processor = new NovelSurfaceResultsProcessor(true);
		processor.readNovelSurfaceResults(1000f);
		processor.readNovelSurfaceResults(500f);
		processor.readNovelSurfaceResults(300f);
		processor.readNovelSurfaceResults(200f);
		processor.readNovelSurfaceResults(100f);
		processor.readNovelSurfaceResults(50f);

	}

}
