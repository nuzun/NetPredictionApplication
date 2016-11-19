package uk.ac.bbk.cryst.netprediction.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netprediction.common.PropertiesHelper;
import uk.ac.bbk.cryst.netprediction.model.BinderData;

public class FormattedPoolCsvFileGenerator {

	private static PropertiesHelper properties = new PropertiesHelper();
	
	/**
	 * This class is to generate the formatted pool output to directly copy to excel
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		//Create the CSVFormat object
        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
        //initialize the CSVParser object
        CSVParser parser = new CSVParser(new FileReader(properties.getValue("inputPath") +"pool3.csv"), format);
        String[] selectedAlleles = {"HLA-A02:01","HLA-A01:01","HLA-A03:01","HLA-A24:02","HLA-A11:01","HLA-A29:02","HLA-A32:01","HLA-A68:01","HLA-A31:01","HLA-A26:01","HLA-A25:01","HLA-A23:01","HLA-A68:02","HLA-A30:02","HLA-A30:01","HLA-A02:02","HLA-A74:01","HLA-A36:01","HLA-A33:03","HLA-B07:02","HLA-B08:01","HLA-B44:02","HLA-B35:01","HLA-B51:01","HLA-B40:01","HLA-B44:03","HLA-B15:01","HLA-B18:01","HLA-B57:01","HLA-B14:02","HLA-B27:05","HLA-B13:02","HLA-B38:01","HLA-B55:01","HLA-B37:01","HLA-B35:03","HLA-B14:01","HLA-B49:01","HLA-B50:01","HLA-B39:01","HLA-B40:02","HLA-B53:01","HLA-B15:03","HLA-B42:01","HLA-B58:02","HLA-B58:01","HLA-B52:01","HLA-B78:01","HLA-B41:01","HLA-B56:01"};
        
         
        List<BinderData> poolBinders = new ArrayList<BinderData>();
        
        for(CSVRecord record : parser){
        	BinderData binderData = new BinderData();
        	binderData.setUniprot_code(record.get("UNIPROT_CODE"));
        	binderData.setAllele(record.get("ALLELE"));
        	binderData.setPeptide(record.get("PEPTIDE"));
        	binderData.setIC50Score(Float.valueOf(record.get("IC50_SCORE")));
        	poolBinders.add(binderData);
        }
        //close the parser
        parser.close();
        
        for(String selected : selectedAlleles){
        	for(BinderData poolBinder : poolBinders){
        		
        		if(StringUtils.equals(poolBinder.getAllele(),selected)){
        			
        			System.out.print(poolBinder.getAllele() + "\t");
        			System.out.print(poolBinder.getPeptide() + "\t");
        			System.out.print(poolBinder.getIC50Score() + "\t");
        			System.out.print(poolBinder.getUniprot_code() + "\t");
        		}
        	}
        	
        	System.out.println();
        }
        

	}

}
