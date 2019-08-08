package uk.ac.bbk.cryst.netprediction.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

import uk.ac.bbk.cryst.netprediction.model.HeatMapTable;

public class HeatMapTableHelper {

	public static void readHeatMapFiles(String heatMapFile) throws FileNotFoundException{
	    List<HeatMapTable> heatMapRows = new ArrayList<>();

		Scanner scanner = new Scanner(new File(heatMapFile));
		scanner.useDelimiter(",");
		
		String header = scanner.nextLine();
		
		String[] elements = header.split(",");
		
		for(int i = 1 ; i < elements.length ; i++){
			HeatMapTable t = new HeatMapTable(elements[i]);
			heatMapRows.add(t);
		}
		
		while(scanner.hasNextLine()){
			String row = scanner.nextLine();
			
			String[] items = row.split(",");
			
			for(int i = 1 ; i < items.length ; i++){
				if(Float.valueOf(items[i]) <= 1000)
				heatMapRows.get(i-1).setTotalBinders(heatMapRows.get(i-1).getTotalBinders()+1);
				
				else if(Float.valueOf(items[i]) >= 1001){
					heatMapRows.get(i-1).setTotalBlackGreys(heatMapRows.get(i-1).getTotalBlackGreys()+1);
				}
					
			}
		}
		
		scanner.close();
		
		System.out.println(heatMapFile);
		for(HeatMapTable h: heatMapRows){
			System.out.println(h.getVariant());
			System.out.println("binders:" + h.getTotalBinders());
			System.out.println("blacksGreys:" + h.getTotalBlackGreys());
			
			System.out.println();

			
		}
		
	}
	
	public static void determineOverallColour(String heatMapFile) throws FileNotFoundException{
		
		Map<String,Integer> alleleMap = new HashMap<>();
		
		Scanner scanner = new Scanner(new File(heatMapFile));
		scanner.useDelimiter(",");
		
		//header
		scanner.nextLine();
		
		while(scanner.hasNextLine()){
			String row = scanner.nextLine();
			String[] items = row.split(",");
			
			alleleMap.put(items[0],0);
			
		    for(int i = 1 ; i < items.length ; i++ ){	
		    	if(Float.valueOf(items[i]) <= 1000){
					alleleMap.put(items[0],alleleMap.get(items[0])+1);
		    	}
		    	
		    }
		}
		
		Map<String,Integer> sorted = alleleMap 
				.entrySet() .stream() .sorted(comparingByKey()) 
				.collect( toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

		scanner.close();

		int totalColouredSquares = 0;
		int totalBlacks = 0;
		
		for(String a :sorted.keySet()){
			System.out.println(a + " : " + alleleMap.get(a));
			if(alleleMap.get(a) > 0){
				totalColouredSquares++;
			}
			else{
				totalBlacks++;
			}
		}
		
		System.out.println("Coloured:" + totalColouredSquares);
		System.out.println("Blacks:" + totalBlacks);
		
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		String heatMapFile = "data//output//heatmap_CTLPAN_false_A0206_A0202.csv";

		readHeatMapFiles(heatMapFile);
		readHeatMapFiles(heatMapFile.replace("false", "true"));

		determineOverallColour(heatMapFile);
		determineOverallColour(heatMapFile.replace("false", "true"));

	}
}
