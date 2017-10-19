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
import java.util.TreeMap;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import oaei.configuration.OAEIConfiguration;
import oaei.mappings.ReferenceMappings;
import oaei.mappings.SystemMappings;
import oaei.measures.SemanticMeasures;
import oaei.measures.StandardMeasures;
import oaei.results.Results;
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
	
	
	TreeMap<String, SystemMappings> system_results_map = new TreeMap<String, SystemMappings>();
	TreeMap<String, ReferenceMappings> reference_mappings_map = new TreeMap<String, ReferenceMappings>();
	
	
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
		
		//TODO Extract uniqueness (optional)
		//Store uniqueness files: extended tsv files with labels (others than RDFS:label)?
		
		
		//Compute measures for systems against references
		computeMeasures();
			
				
		//Print and Store results
		printResults();
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
				system_results_map.put(name, new SystemMappings(name, tool_files[i]));
				
				
				//Add mappings
				system_results_map.get(name).setMappings(mappingReaderTool.getMappingObjects());
								
				//Set merged ontology
				LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
				system_results_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, mappingReaderTool.getMappingObjects(), extractModules, classifyMergedOntologies, reasonerID));
				
				
			}
		}
	}
	
	
	private void loadReferenceMappings() throws Exception{
		
		//Load reference
		File files = new File(configuration.getReferencesPath());
		String ref_files[] = files.list();
		for(int i=0; i<ref_files.length; i++){
			
			if (ref_files[i].contains(configuration.getFileNamePattern())){
			
				String mappings_file = configuration.getReferencesPath() + ref_files[i];
				MappingsReaderManager mappingReaderTool = new MappingsReaderManager(mappings_file, MappingsReaderManager.OAEIFormat);
				
				
				//TODO reference alignments must follow this pattern too
				String name = ref_files[i].split(configuration.getFileNamePattern())[0];
				
									
				//Create entry
				reference_mappings_map.put(name, new ReferenceMappings(name, ref_files[i]));
				
				
				//Add mappings
				reference_mappings_map.get(name).setMappings(mappingReaderTool.getMappingObjects());
								
				//Set merged ontology
				//Set merged ontology
				LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
				reference_mappings_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, mappingReaderTool.getMappingObjects(), extractModules, classifyMergedOntologies, reasonerID));
				
			}	
			
		}
		
		
		
	}
	
	
	private void computeMeasures(){
		
		//Extract P, R and F
		for (String tool_name : system_results_map.navigableKeySet()){
			
			SystemMappings system = system_results_map.get(tool_name);
			
			for (String reference_name : reference_mappings_map.navigableKeySet()){
				
				ReferenceMappings reference = reference_mappings_map.get(reference_name);
				
				system.addResult(reference_name, new Results(tool_name, reference_name));
				
				
				//Check if unsat: this will affect precision and/or recall
				
				//Compute semantic measures
				LogOutput.printAlways("Computing semantic measures for " + tool_name + " against " + reference_name);
				SemanticMeasures.computeSemanticMeasures(
						reference.getOWLReasonerMergedOntology(), 
						system.getOWLReasonerMergedOntology(), 
						reference.getMappingSet(), system.getMappingSet());
				
				
				//Store results
				system.getResults().get(reference_name).
						setSemanticMeasures(SemanticMeasures.getSemanticPrecision(), SemanticMeasures.getSemanticRecall(), SemanticMeasures.getSemanticFscore());
				
				//Compute standards measures
				LogOutput.printAlways("Computing standard measures for " + tool_name + " against " + reference_name);
				StandardMeasures.computeStandardMeasures(
						system.getHashAlignment(), 
						reference.getHashAlignment());
				
				//Store results
				system.getResults().get(reference_name).
						setStandardMeasures(StandardMeasures.getPrecision(), StandardMeasures.getRecall(), StandardMeasures.getFscore());
												
			}
		}
	}
	
	/**
	 * 
	 */
	private void printResults() {

		int i=0;
		
		for (String tool_name : system_results_map.navigableKeySet()){
					
			SystemMappings system = system_results_map.get(tool_name);
		
			if (i==0){
				System.out.print(system.getHeaderForResults());
				i++;
			}			
			
			System.out.print(system.toString());
		}
		
	}
	
	
	private void storeResults() {
		//TODO
		
	}


	
	
	
	public static void main (String[] argss){
		try {
			new EvaluatePhenotypeTrack(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	

}
