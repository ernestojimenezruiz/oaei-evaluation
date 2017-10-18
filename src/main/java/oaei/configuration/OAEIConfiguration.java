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

/**
 *
 * @author ernesto
 * Created on 16 Oct 2017
 *
 */
public class OAEIConfiguration {
	
	//Paths of mappings to be evaluated
	protected String mappings_path;
	
	//Path of the track ontologies
	protected String ontologies_path;
	
	//Path of the reference alignments
	protected String reference_path;
	
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
	
	
	public OAEIConfiguration(){
		
		try {
			loadConfiguration();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error reading configuration file");
			e.printStackTrace();
		}
	}
	
	
	public String getMappingsPath(){
		return mappings_path;
	}
	
	
	public String getOntologiesPath(){
		return ontologies_path;
	}
	
	public String getReferencesPath(){
		return reference_path;
		
	}
	
	public String getTaskName(){
		return task_name;
	}
	
	
	public String getFileNamePattern(){
		return file_name_pattern;
	}
	
	
	public String geOntologyFile1(){
		return onto_file1;
	}
	
	public String geOntologyFile2(){
		return onto_file2;
	}
	
	public String geOntologyURI1(){
		return onto_uri1;
	}
	
	public String geOntologyURI2(){
		return onto_uri2;
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
			mappings_path = properties.getProperty("mappings_path");
			
			ontologies_path = properties.getProperty("ontologies_path");
			
			reference_path = properties.getProperty("reference_path");
			
			task_name = properties.getProperty("task_name");
			
			file_name_pattern = properties.getProperty("file_name_pattern");
			
			onto_file1 = properties.getProperty("onto_file1");
			
			onto_file2 = properties.getProperty("onto_file2");
			
			onto_uri1 = properties.getProperty("onto_uri1");
			
			onto_uri2 = properties.getProperty("onto_uri2");
			
			
			//Enumeration enuKeys = properties.keys();
			//while (enuKeys.hasMoreElements()) {
			//	String key = (String) enuKeys.nextElement();
			//	String value = properties.getProperty(key);
			//	System.out.println(key + ": " + value);
			//}
			
		}
		else if (tool_files.length>1){
			System.err.println("There are more than one configuration files.");
		}
		else{
			System.err.println("No configuration file available.");
		}
		
		
	}
	
	
	public static void main (String[] args){
		new OAEIConfiguration();
		
	}
	
	
	
	
	
	
	

}
