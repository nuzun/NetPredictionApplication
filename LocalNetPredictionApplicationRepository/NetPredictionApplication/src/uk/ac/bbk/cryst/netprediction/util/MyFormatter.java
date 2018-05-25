package uk.ac.bbk.cryst.netprediction.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class MyFormatter extends Formatter{
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

	@Override
	public String format(LogRecord record) {
		// TODO Auto-generated method stub
		 StringBuilder builder = new StringBuilder();
	        builder.append(df.format(new Date(record.getMillis()))).append(" ");
	        builder.append(record.getSourceMethodName()).append(" \n");
	        builder.append(formatMessage(record));
	        builder.append("\n");
	        return builder.toString();
	}
}
