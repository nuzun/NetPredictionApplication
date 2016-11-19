package uk.ac.bbk.cryst.sequenceanalysis.util;

import org.apache.commons.lang3.StringUtils;

public class FileOperationsHelper {

	public static String generateValidFileName(String fileName){	
		String newFileName = fileName;
		
		if(!StringUtils.isEmpty(newFileName)){
			newFileName = fileName.replace(":", "");
			newFileName = newFileName.replace("/", "");
		}
		else{
			newFileName = "file"+ DateUtils.now();
		}
		
		return newFileName;
	}
	
	public static String trimFileName(String fileName){
		
		String newFileName = fileName;
		
		if(!StringUtils.isEmpty(newFileName)){
			//length could be 30 for a filename but we can use longer description or as it is to write in a file.
			newFileName = (fileName.length() > 30) ? fileName.substring(0,30) : fileName;
		}
		else{
			newFileName = "file"+ DateUtils.now();
		}
		
		return newFileName;
	}
	
	public static String generateFileName(){
		return null;
	}
	
}
	

