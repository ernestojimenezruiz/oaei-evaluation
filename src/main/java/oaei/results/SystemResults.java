/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.results;

import java.util.HashMap;
import java.util.Map;
import oaei.mappings.Mappings;

/**
 *
 * @author ernesto
 * Created on 18 Oct 2017
 *
 */
public class SystemResults extends Mappings{
	
	private double time;
	
	private int uniqueMappingsSize;
	
	
	public static String getHeaderForResults(){
		return ""; //TODO build nice results header line
	}
	
	
	//Hashmap with pairs reference-results
	private Map<String, Results> results = new HashMap<String, Results>(); 
	
	
	public SystemResults (String name, String file_name){
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
		return "";  //TODO build nice results line 
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
