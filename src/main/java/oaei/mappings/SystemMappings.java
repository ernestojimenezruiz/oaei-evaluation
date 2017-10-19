/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.mappings;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import oaei.results.Results;

/**
 *
 * @author ernesto
 * Created on 18 Oct 2017
 *
 */
public class SystemMappings extends Mappings{
	
	private double time;
	
	private int uniqueMappingsSize;
	
	
	public String getHeaderForResults(){
		StringBuilder builder = new StringBuilder();
		
		String separator = "\t";
		
		builder.append(separator).append(separator).append(separator).append(separator);
		for (String key : results.navigableKeySet()){
			builder.append(key).append(separator).append(separator).append(separator);
		}
		builder.append("\n");
		
		
		builder.append("System").append(separator).
			append("Time (ms)").append(separator).
			append("Size").append(separator).
			append("Unique").append(separator);
		
		for (String key : results.navigableKeySet()){
			
			builder.append("P").append(separator).
				append("F").append(separator).
				append("R").append(separator).
				append("S-P").append(separator).
				append("S-F").append(separator).
				append("S-R").append(separator);
			
		}
			
			
		builder.append(getUnsatisfiableClassesSize()).append(separator).
			append("File nanme");//.append("\n");
		
		
		return builder.toString();  //TODO build nice results line 
	}
	
	
	//Map with ordered pairs reference-results
	private TreeMap<String, Results> results = new TreeMap<String, Results>(new Comparator<String>() {
	    public int compare(String o1, String o2) {
	        return o1.toLowerCase().compareTo(o2.toLowerCase());
	    }
	});
	
	
	public SystemMappings (String name, String file_name){
		super(name, file_name);
	}


	/**
	 * @return the time
	 */
	public double getComputationTime() {
		return time;
	}


	/**
	 * @param time the time to set
	 */
	public void setComputationTime(double time) {
		this.time = time;
	}
	
	
	
	public String toString(){
		
		StringBuilder builder = new StringBuilder();
		
		String separator = "\t";
		
		builder.append(getName()).append(separator).
			append(getComputationTime()).append(separator).
			append(getMappingsSize()).append(separator).
			append(getUniqueMappingsSize()).append(separator);
		
		for (String key : results.navigableKeySet()){
			
			builder.append(results.get(key).getPrecision()).append(separator).
				append(results.get(key).getFscore()).append(separator).
				append(results.get(key).getRecall()).append(separator).
				append(results.get(key).getSemanticPrecision()).append(separator).
				append(results.get(key).getSemanticFscore()).append(separator).
				append(results.get(key).getSemanticRecall()).append(separator);
			
		}
			
			
		builder.append(getUnsatisfiableClassesSize()).append(separator).
			append(getFileName());//.append("\n");
		
		
		return builder.toString();  //TODO build nice results line 
	}


	/**
	 * @return the results
	 */
	public Map<String, Results> getResults() {
		return results;
	}

	
	
	/**
	 * @return the results
	 */
	public void addResult(String reference_name, Results measures) {
		results.put(reference_name, measures);
	}


	/**
	 * @return the uniqueMappings
	 */
	public int getUniqueMappingsSize() {
		return uniqueMappingsSize;
	}


	/**
	 * @param uniqueMappings the uniqueMappings to set
	 */
	public void setUniqueMappingsSize(int uniqueMappings) {
		this.uniqueMappingsSize = uniqueMappings;
	}

	
	
	

	
	
	
	
	
	
	
	

}
