package uk.ac.bbk.cryst.netprediction.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.*;

public class CustomLogger {

	static private FileHandler fileTxt;
	//static private SimpleFormatter formatterTxt;

	static public void setup() throws IOException {

		// get the global logger to configure it
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		// suppress the logging output to the console
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();

		if (handlers[0] instanceof ConsoleHandler) {
			rootLogger.removeHandler(handlers[0]);
		}

		// append to the file
		int limit = 5000000; // 5MB
		int numberOfLogfiles = 100;
		fileTxt = new FileHandler("data//output//Logging.txt", limit, numberOfLogfiles, true);

		// create a TXT formatter
		//formatterTxt = new SimpleFormatter();
		MyFormatter myFormatter = new MyFormatter();
		fileTxt.setFormatter(myFormatter);
		
		logger.addHandler(fileTxt);
	}

}
