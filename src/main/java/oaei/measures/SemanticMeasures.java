/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.measures;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerAccess;

/**
 *
 * Computes Semantic Precision and Recall
 * @author ernesto
 * Created on 17 Oct 2017
 *
 */
public class SemanticMeasures {
	
	private static double sem_precision=0.0;
	private static double sem_recall=0.0;
	private static double sem_fscore=0.0;
	/**
	 * @return the semantic precision
	 */
	public static double getSemanticPrecision() {
		return sem_precision;
	}
	
	/**
	 * @return the semantic recall
	 */
	public static double getSemanticRecall() {
		return sem_recall;
	}
	
	/**
	 * @return the semantic fscore
	 */
	public static double getSemanticFscore() {
		return sem_fscore;
	}
	
	
	
	public static void computeSemanticMeasures(ReasonerAccess reference_reasoner, ReasonerAccess system_reasoner, Set<MappingObjectStr> reference_mappings,  Set<MappingObjectStr> system_mappings){
		
		sem_precision = getSemanticPrecision(reference_reasoner, system_mappings);
		
		sem_recall = getSemanticRecall(system_reasoner, reference_mappings);
		
		sem_fscore = Math.round((2000.0 * sem_precision * sem_recall) / (sem_precision + sem_recall))/1000.0;
		
		
	}
	
		
	public static double getSemanticPrecision(ReasonerAccess reference_reasoner, Set<MappingObjectStr> system_mappings){
		return checkMappingEntailment(reference_reasoner, system_mappings);
		
	}
	
	public static double getSemanticRecall(ReasonerAccess system_reasoner, Set<MappingObjectStr> reference_mappings){
		return checkMappingEntailment(system_reasoner, reference_mappings);	
		
	}
	
	
	protected static double checkMappingEntailment(ReasonerAccess reasoner, Set<MappingObjectStr> mappings){
	
		int positive_hits=0;
		for (MappingObjectStr mapping_c : mappings){
			
			OWLClass cls1 = getOWLClassFromURIStr(reasoner, mapping_c.getIRIStrEnt1());
			OWLClass cls2 = getOWLClassFromURIStr(reasoner, mapping_c.getIRIStrEnt2());
			
			if (reasoner.isSubClassOf(cls1, cls2) || reasoner.isSubClassOf(cls2, cls1)){
				positive_hits++;
			}							
		}
		
		return (double)((double)positive_hits/(double)mappings.size());
		
	}
	
	
	private static OWLClass getOWLClassFromURIStr(ReasonerAccess reasoner, String uri_str){		
		return reasoner.getOntology().getOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create(uri_str));
	}
	
	

}
