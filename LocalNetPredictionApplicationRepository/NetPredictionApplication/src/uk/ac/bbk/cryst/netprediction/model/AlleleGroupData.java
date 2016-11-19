package uk.ac.bbk.cryst.netprediction.model;

import java.util.HashMap;
import java.util.Map;

public class AlleleGroupData {
	
	private String group;
	private String groupCode;
	private String sourceFileName;
	private Map<String,Float> alleleMap;
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getSourceFileName() {
		return sourceFileName;
	}
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}
	public Map<String, Float> getAlleleMap() {
		if(this.alleleMap == null){
			alleleMap = new HashMap<String, Float>();
		}
		return alleleMap;
	}
	public void setAlleleMap(Map<String, Float> alleleMap) {
		this.alleleMap = alleleMap;
	}
	
	

}
