package uk.ac.bbk.cryst.netprediction.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import uk.ac.bbk.cryst.netprediction.common.PredictionType;
import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;

public class NetPanCmd {

	
	/**
	 * HLA-DRB11101 for MHCII-2.2
	 * DRB1_1101 for MHCIIPan-2.0
	 */
	private static String generateScriptLine(PredictionType type, String scoreCode,String peptideLength, String allele,String sequenceFileFullPath,String outputFileFullPath) throws IOException{
		
		PropertiesHelper properties = new PropertiesHelper();
	    String scriptLine = "";
		
		switch(type){
		case CTL:
			scriptLine = properties.getValue("netCTLPanScript") + " " + scoreCode +  " " +
					 peptideLength + " " +
					 allele + " " +
					 sequenceFileFullPath+ " " +
					 outputFileFullPath;
			break;
		case MHCI:
			scriptLine = properties.getValue("netMHCPanScript") + " "+ allele + " " +
					 peptideLength + " " + 
					 sequenceFileFullPath + " " +
					 outputFileFullPath;
			break;
		case MHCII:
			scriptLine = properties.getValue("netMHCIIScript") + " "+ ("HLA-"+ allele.replace("_", "")) + " " +
					 peptideLength + " " + 
					 sequenceFileFullPath + " " +
					 outputFileFullPath;
			break;
		case MHCIIPAN20:
			scriptLine = properties.getValue("netMHCIIPanScript") + " "+ allele + " " +
					 peptideLength + " " + 
					 sequenceFileFullPath + " " +
					 outputFileFullPath;
			break;
		case MHCIIPAN31:
			scriptLine = properties.getValue("netMHCIIPan31Script") + " "+ allele + " " +
					 peptideLength + " " + 
					 sequenceFileFullPath + " " +
					 outputFileFullPath;
			break;
		default:	
			break;
		}
		
		return scriptLine;
	}
	
	public static void run(PredictionType type, String scoreCode,String peptideLength, String allele,String sequenceFileFullPath,String outputFileFullPath){
		try{
			
			String 	scriptLine = generateScriptLine(type, scoreCode, peptideLength, allele, sequenceFileFullPath, outputFileFullPath);
			
			String response = "";
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", scriptLine);
			
			pb.redirectErrorStream(true);
			//System.out.println("Linux command: " + scriptLine);
			
			Process shell = pb.start();
			
			// To capture output from the shell
			InputStream shellIn = shell.getInputStream();
			 
			// Wait for the shell to finish and get the return code
			int shellExitStatus = shell.waitFor();
			//System.out.println("Exit status" + shellExitStatus);
			 
			response = convertStreamToStr(shellIn);
			 
			shellIn.close();
			
			//System.out.println("response: " + response);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String convertStreamToStr(InputStream is) throws IOException {
		 
		if (is != null) {
			Writer writer = new StringWriter();
			 
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		}
		else {
			return "";
		}
		
	}

}
