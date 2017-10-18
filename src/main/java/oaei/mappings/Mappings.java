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
	
	String name;
	
	String file_name;
	
	int mappings_size;
	
	Set<MappingObjectStr> mappingSet = new HashSet<MappingObjectStr>();
	
	HashAlignment hashALignment;
	
	MergedOntology alignedOntology;
	
	int unsat_classes;
	
	
	public Mappings (String name, String file_name){
		
		this.name = name;
		this.file_name = file_name;
	}
	
	
	public void setMappings(Set<MappingObjectStr> mappings){
		mappingSet.addAll(mappings);		
		hashALignment =  new HashAlignment(mappings);
		mappings_size=mappings.size();
	}
	
	
	public void setAlignedOntology(MergedOntology mergedO){
		alignedOntology = mergedO;
		
		unsat_classes = alignedOntology.getReasoner().getUnsatisfiableClasses().size();
		
	}
	
	
	

}
