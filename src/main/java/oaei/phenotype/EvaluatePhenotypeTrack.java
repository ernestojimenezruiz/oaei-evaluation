/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.phenotype;

import java.io.File;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import oaei.configuration.OAEIConfiguration;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.LogOutput;

/**
 *
 * @author ernesto
 * Created on 15 Oct 2017
 *
 */
public class EvaluatePhenotypeTrack {
	
	OAEIConfiguration configuration;
	
	OWLOntology onto1;
	OWLOntology onto2;
	
	
	
	public EvaluatePhenotypeTrack() throws OWLOntologyCreationException{
		
		//load configuration
		configuration = new OAEIConfiguration();
				
		//load ontologies
		loadOntologies();
		
		//load systems
		
	}
	
	
	private void loadOntologies() throws OWLOntologyCreationException{
		
		OntologyLoader loader1;
		OntologyLoader loader2;
				
		LogOutput.printAlways("Loading ontologies...");
		loader1 = new OntologyLoader(configuration.geOntologyFile1());
		loader2 = new OntologyLoader(configuration.geOntologyFile2());
				
		onto1 = loader1.getOWLOntology();
		onto2 = loader2.getOWLOntology();
				
		LogOutput.printAlways("...Done\n");
	}
	
	
	private void loadSystemMappings(){
		
		//Load mappings				
		File files = new File(configuration.getMappingsPath());
		String tool_files[] = files.list();
		
		for(int i=0; i<tool_files.length; i++){			
			
			if (tool_files[i].contains(configuration.getFileNamePattern())){
				
				//Load and do sth
				
				//new MergedOntology();
				
				
				
			}
		}
	}
	
	
	private void loadReferenceMappings(){
		
		//Detect in name a number and use it as identifier for P,R,F, if onlye one then PRF, if many ref. alignments use name of file.
		
		//Load reference
		File files = new File(configuration.getReferencesPath());
		String ref_files[] = files.list();
		for(int i=0; i<ref_files.length; i++){			
			
		}
		
		
		
	}
	
	
	
	
	//Load ontologies: path
	//Read from configuration path
	
	//Load Mappings
	
	
	
	
	

}
