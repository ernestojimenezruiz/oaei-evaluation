/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.configuration;

/**
 *
 * @author ernesto
 * Created on 16 Oct 2017
 *
 */
public abstract class OAEIConfiguration {
	
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
	
	
	
	

}
