/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.util;

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
