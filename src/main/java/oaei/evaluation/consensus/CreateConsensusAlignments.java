/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.evaluation.consensus;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import oaei.configuration.OAEIConfiguration;
import oaei.evaluation.AbstractEvaluation;
import oaei.mappings.ReferenceMappings;
import oaei.mappings.SystemMappings;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;

/**
 *
 * @author ernesto
 * Created on 30 Oct 2017
 *
 */
public class CreateConsensusAlignments extends AbstractEvaluation {
	
	//Set<ConsensusMapping> consensus = new HashSet<ConsensusMapping>();  
	HashConsensusAlignment consensusAlignment = new HashConsensusAlignment();
	
	
	public CreateConsensusAlignments() throws Exception{
		//load configuration
		configuration = new OAEIConfiguration();
				
		//load ontologies
		loadOntologies();
		
		//load systems
		loadSystemMappings();
		
		//TODO
		createConsensusAlignment();
		
					
		//TODO Set confidence for consensus alignments! One could also weight the votes here
		
		
		
			
	}


	/**
	 * 
	 */
	private void createConsensusAlignment() {
		for (String system : system_results_map.keySet()){
			
			for (MappingObjectStr mapping : system_results_map.get(system).getMappingSet()){
				
				
				
				
			}
			
		}
		
	}

}
