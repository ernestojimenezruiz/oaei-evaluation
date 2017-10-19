/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.mappings;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

import oaei.util.HashAlignment;
import oaei.util.MergedOntology;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerAccess;

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
		
		//Avoid mappings between same URIs
		for (MappingObjectStr mapping : mappings){
			if (mapping.getIRIStrEnt1().equals(mapping.getIRIStrEnt2()))
				continue;
			
			mappingSet.add(mapping);
			
		}
		//mappingSet.addAll(mappings);
		hashAlignment =  new HashAlignment(mappingSet);		
		mappings_size=mappingSet.size();
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

	
	public MergedOntology getMergedOntology(){
		return alignedOntology;
	}

	public OWLOntology getOWLMergedOntology(){
		return alignedOntology.getOntology();
	}
	
	public ReasonerAccess getOWLReasonerMergedOntology(){
		return alignedOntology.getReasoner();
	}
	
	

	
	
	
	

}
