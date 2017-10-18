/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.phenotype;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import oaei.configuration.OAEIConfiguration;
import oaei.mappings.ReferenceMappings;
import oaei.results.SystemResults;
import oaei.util.MergedOntology;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;

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
	
	
	Map<String, SystemResults> system_results_map = new HashMap<String, SystemResults>();
	Map<String, ReferenceMappings> reference_mappings_map = new HashMap<String, ReferenceMappings>();
	
	
	int reasonerID = ReasonerManager.HERMIT;
	
	boolean classifyMergedOntologies = true;
	boolean extractModules = true; 
	
	
	
	public EvaluatePhenotypeTrack(boolean extractUniqueness) throws Exception{
		
		//load configuration
		configuration = new OAEIConfiguration();
				
		//load ontologies
		loadOntologies();
		
		//load reference
		loadReferenceMappings();
		
		//load systems
		loadSystemMappings();
		
		//Extract uniqueness (optional)
		//Store uniqueness files: extended tsv files with labels (others than RDFS:label)?
		
		//Extract P, R and F
		
		
		
		//Store results: implement the toString() method
		
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
	
	
	//Loads systems mappings and uses reasoning
	private void loadSystemMappings() throws Exception{
					
		File files = new File(configuration.getMappingsPath());
		String tool_files[] = files.list();
		
		for(int i=0; i<tool_files.length; i++){			
			
			if (tool_files[i].contains(configuration.getFileNamePattern())){
				
				String mappings_file = configuration.getMappingsPath() + tool_files[i];
				MappingsReaderManager mappingReaderTool = new MappingsReaderManager(mappings_file, MappingsReaderManager.OAEIFormat);
				
				String name = tool_files[i].split(configuration.getFileNamePattern())[0];
				
				//Create entry
				system_results_map.put(name, new SystemResults(name, tool_files[i]));
				
				
				//Add mappings
				system_results_map.get(name).setMappings(mappingReaderTool.getMappingObjects());
								
				//Set merged ontology
				system_results_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, mappingReaderTool.getMappingObjects(), extractModules, classifyMergedOntologies, reasonerID));
				
				
			}
		}
	}
	
	
	private void loadReferenceMappings() throws Exception{
		
		//Load reference
		File files = new File(configuration.getReferencesPath());
		String ref_files[] = files.list();
		for(int i=0; i<ref_files.length; i++){
			
			
			String mappings_file = configuration.getReferencesPath() + ref_files[i];
			MappingsReaderManager mappingReaderTool = new MappingsReaderManager(mappings_file, MappingsReaderManager.OAEIFormat);
			
			
			String name;
		
			if (ref_files.length==1)
				name="reference";
			else if (ref_files[i].contains("2")){
				name="consensus-2";
			}
			else if (ref_files[i].contains("3")){
				name="consensus-3";
			}
			else if (ref_files[i].contains("4")){
				name="consensus-4";
			}
			else if (ref_files[i].contains("5")){
				name="consensus-5";
			}
			else {
				name = ref_files[i];
			}
								
			//Create entry
			reference_mappings_map.put(name, new ReferenceMappings(name, ref_files[i]));
			
			
			//Add mappings
			reference_mappings_map.get(name).setMappings(mappingReaderTool.getMappingObjects());
							
			//Set merged ontology
			reference_mappings_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, mappingReaderTool.getMappingObjects(), extractModules, classifyMergedOntologies, reasonerID));
			
			
			
		}
		
		
		
	}
	
	
	
	
	
	

}
