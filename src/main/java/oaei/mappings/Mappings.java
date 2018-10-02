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
import oaei.util.Utilities;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerAccess;

/**
 * 
 * Structure to keep reference mappings or mappings computed by a system
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
	
	private OWLOntology mappingsOntology;
	
	private int unsat_classes;
	
	
	public Mappings (String name, String file_name){
		
		this.name=name;
		this.file_name=file_name;
	}
	
	
	public void setMappings(OWLOntology onto1, OWLOntology onto2, Set<MappingObjectStr> mappings) throws Exception{
		
		//Avoid mappings between same URIs
		for (MappingObjectStr mapping : mappings){
			if (mapping.getIRIStrEnt1().equals(mapping.getIRIStrEnt2()))
				continue;
			
			mappingSet.add(mapping);
			
		}
		//mappingSet.addAll(mappings);
		hashAlignment =  new HashAlignment(mappingSet);		
		mappings_size=mappingSet.size();
		
		mappingsOntology= Utilities.createOWLOntologyFromRDFMappings(onto1, onto2, mappingSet);
		
		
	}
	
	
	public void setAlignedOntology(MergedOntology mergedO){
		
		alignedOntology = mergedO;
		
		unsat_classes = alignedOntology.getReasoner().getUnsatisfiableClasses().size();
		
	}
	
	
	public int getUnsatisfiableClassesSize() {
		return unsat_classes;
	}
	
	
	public double getUnsatisfiableClassesDegree() {
		return (double)unsat_classes/(double)getOWLMergedOntology().getClassesInSignature(true).size();
	}
	
	
	
	public boolean isReasonerComplete() {
		return alignedOntology.isCompleteReasoner();
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


	/**
	 * @return the mappingsOntology
	 */
	public OWLOntology getMappingsOntology() {
		return mappingsOntology;
	}


	
	
	

}
