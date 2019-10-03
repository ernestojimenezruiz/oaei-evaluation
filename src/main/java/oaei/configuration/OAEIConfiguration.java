/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;


/**
 *
 * @author ernesto
 * Created on 16 Oct 2017
 *
 */
public class OAEIConfiguration {
	
	
	//Protocol for the physical ontology uri: file: (linux), file:/ (windows)
	protected String protocol;
	
	//Base path if all resources are in same path
	protected String base_path;
	
	//Paths of mappings to be evaluated
	protected String mappings_path;
	
	//Paths of seals logs
	protected String logs_path="";
	
	
	//Path of the track ontologies
	protected String ontologies_path;
	
	//Path of the reference alignments
	protected String reference_path;
	
	//Path to results
	protected String results_path;
	
	//Name of the task to be evaluated
	protected String task_name;
	
	//Pattern to identify relevant mapping files
	protected String file_name_pattern;
	
	//Physical uri onto1
	protected String onto_file1;
	
	//Physical uri onto2
	protected String onto_file2;
	
	//Logical uri onto1
	protected String onto_uri1;
	
	//Logical uri onto2
	protected String onto_uri2;
	
	//Reasoner
	protected int reasoner_id;
	
	public OAEIConfiguration(){
		
		try {
			loadConfiguration();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error reading configuration file");
			e.printStackTrace();
		}
	}
	
	
	public String getProtocol(){
		return protocol;
	}
	
	public String getBasePath(){
		return base_path;
	}
	
	public String getMappingsPath(){
		return mappings_path;
	}
	
	public String getLogsPath(){
		return logs_path;
	}
	
	
	public String getOntologiesPath(){
		return ontologies_path;
	}
	
	public String getReferencesPath(){
		return reference_path;
		
	}
	
	public String getResultsPath(){
		return results_path;
		
	}
	
	public String getTaskName(){
		return task_name;
	}
	
	
	public String getFileNamePattern(){
		return file_name_pattern;
	}
	
	
	public String getOntologyFile1(){
		return onto_file1;
	}
	
	public String getOntologyFile2(){
		return onto_file2;
	}
	
	public String getOntologyURI1(){
		return onto_uri1;
	}
	
	public String getOntologyURI2(){
		return onto_uri2;
	}
	
	public int getResonerID() {
		return reasoner_id;
	}
	
	
	protected void loadConfiguration() throws IOException{
	
		String config_path = System.getProperty("user.dir") + "/configuration/";
		
		//System.out.println(config_path);
		
		
		//Keep only files ending in .properties
		File files = new File(config_path);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".properties")) {
					return true;
				} else {
					return false;
				}
			}
		};		
		String[] tool_files = files.list(filter);
		
		
		if (tool_files.length==1){
			
			//We expect only one
			FileInputStream fileInput = new FileInputStream(new File(config_path + tool_files[0]));
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
			
			//read parameters
			protocol = properties.getProperty("protocol");
			
			base_path = properties.getProperty("base_path");
									
			mappings_path = base_path + properties.getProperty("mappings_path");
			
			if(properties.containsKey("logs_path"))
				logs_path = base_path + properties.getProperty("logs_path");
			
			//ontologies_path = base_path + properties.getProperty("ontologies_path");
			
			ontologies_path = properties.getProperty("ontologies_path");
			
			reference_path = base_path + properties.getProperty("reference_path");
			
			results_path = base_path + properties.getProperty("results_path");
			
			task_name = properties.getProperty("task_name");
			
			file_name_pattern = properties.getProperty("file_name_pattern");
			
			onto_file1 = protocol + ontologies_path + properties.getProperty("onto_file1");
			
			onto_file2 = protocol + ontologies_path + properties.getProperty("onto_file2");
			
			onto_uri1 = properties.getProperty("onto_uri1");
			
			String reasoner_name;
			if (properties.containsKey("reasoner")){
				reasoner_name = properties.getProperty("reasoner");
				if (reasoner_name.equals("ELK"))
					reasoner_id = ReasonerManager.ELK;
				else
					reasoner_id = ReasonerManager.HERMIT;
					
			}
			else
				reasoner_id = ReasonerManager.HERMIT;
			
			
			/*Enumeration enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				System.out.println(key + ": " + value);
			}*/
			
		}
		else if (tool_files.length>1){
			System.err.println("There are more than one configuration file.");
		}
		else{
			System.err.println("No configuration file available.");
		}
		
		
	}
	
	
	public static void main (String[] args){
		new OAEIConfiguration();
		
	}
	
	
	
	
	
	
	

}
