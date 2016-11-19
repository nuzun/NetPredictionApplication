package uk.ac.bbk.cryst.netprediction.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import uk.ac.bbk.cryst.netprediction.model.AlleleGroupData;

public class AlleleGroupDataDaoImpl {

	AlleleGroupData groupData; 
	String groupFile;
	
	public AlleleGroupData getGroupData() {
		return groupData;
	}
	public String getGroupFile() {
		return groupFile;
	}
	
	public AlleleGroupDataDaoImpl(String groupFile) throws FileNotFoundException{
		this.groupFile = groupFile;
		this.groupData = new AlleleGroupData();
		
		//Get scanner instance
		File csvFile = new File(getGroupFile());
        Scanner scanner = new Scanner(csvFile);
         
        //Set the delimiter used in file
        scanner.useDelimiter(",");
        
        scanner.nextLine();
         
        //Get all tokens and store them in some data structure
        while (scanner.hasNext())
        {
        	String row = scanner.nextLine();
        	String[] elements = row.split(",");
        	
        	if(elements.length != 2){
        		System.out.println("Skipping a groupData");
        		continue;
        	}
    
        	groupData.getAlleleMap().put(String.valueOf(elements[0]), Float.valueOf(elements[1]));
        	
        }
        
        scanner.close();
        
        groupData.setSourceFileName(csvFile.getName());
        
	}
}
