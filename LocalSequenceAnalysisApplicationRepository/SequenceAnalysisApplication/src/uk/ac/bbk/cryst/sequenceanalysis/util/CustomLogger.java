package uk.ac.bbk.cryst.sequenceanalysis.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CustomLogger {

	static private FileHandler fileTxt;
	private static Logger logger = Logger.getLogger("SequenceAnalysisLog");
	
	static public void setup() throws IOException {
		
	    fileTxt = new FileHandler("SystemOut.log");
	    logger.addHandler(fileTxt);
	    
	    SimpleFormatter formatter = new SimpleFormatter();
	    fileTxt.setFormatter(formatter);
	    
	    //logger.setUseParentHandlers(false);
	    //logger.setLevel(Level.OFF);
	}
}
