/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.mappings;

import java.util.HashSet;
import java.util.Set;

import oaei.util.HashAlignment;
import oaei.util.MergedOntology;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

/**
 *
 * @author ernesto
 * Created on 18 Oct 2017
 *
 */
public abstract class Mappings {
	
	private String name;
	
	private String file_name;
	
	private int mappings_size;
	
	private Set<MappingObjectStr> mappingSet = new HashSet<MappingObjectStr>();
	
	private HashAlignment hashAlignment;
	
	private MergedOntology alignedOntology;
	
	private int unsat_classes;
	
	
	public Mappings (String name, String file_name){
		
		this.name=name;
		this.file_name=file_name;
	}
	
	
	public void setMappings(Set<MappingObjectStr> mappings){
		mappingSet.addAll(mappings);
		hashAlignment =  new HashAlignment(mappings);		
		mappings_size=mappings.size();
	}
	
	
	public void setAlignedOntology(MergedOntology mergedO){
		
		alignedOntology = mergedO;
		
		unsat_classes = alignedOntology.getReasoner().getUnsatisfiableClasses().size();
		
	}
	
	
	public int getUnsatisfiableClassesSize() {
		return unsat_classes;
	}
	
	
	
	public int getMappingsSize() {
		return mappings_size;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the file_name
	 */
	public String getFileName() {
		return file_name;
	}


	/**
	 * @return the hashALignment
	 */
	public HashAlignment getHashAlignment() {
		return hashAlignment;
	}


	
	public Set<MappingObjectStr> getMappingSet(){
		return mappingSet;
	}

	
	


	
	
	
	

}
