/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.evaluation;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import oaei.configuration.OAEIConfiguration;
import oaei.mappings.ReferenceMappings;
import oaei.mappings.SystemMappings;
import oaei.measures.StandardMeasures;
import oaei.results.Results;
import oaei.util.MergedOntology;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;

/**
 * Abstract EValuation Tasks: deals with loading and general reasoning
 *
 * @author ernesto
 * Created on 30 Oct 2017
 *
 */
public abstract class AbstractEvaluation {
	
	
	protected OAEIConfiguration configuration;
	
	protected OWLOntology onto1;
	protected OWLOntology onto2;
	
	
	protected TreeMap<String, SystemMappings> system_results_map = new TreeMap<String, SystemMappings>();
	protected TreeMap<String, ReferenceMappings> reference_mappings_map = new TreeMap<String, ReferenceMappings>();
	
	private Map<MappingObjectStr, Integer> allMappings2votes = new HashMap<MappingObjectStr, Integer>();
	
	//To store all entities in relevant mapping and extract modules for these entities
	//Important to cover entities from the reference alignments
	protected Set<OWLEntity> signature_for_modules = new HashSet<OWLEntity>();
	
	
	//To be selected from configuration
	protected int reasonerID = ReasonerManager.HERMIT;
	
	protected boolean classifyMergedOntologies = true;
	protected boolean extractModules = true; 
	
	

	protected  void loadOntologies() throws OWLOntologyCreationException{
		
		OntologyLoader loader1;
		OntologyLoader loader2;
				
		LogOutput.printAlways("Loading ontologies...");
		loader1 = new OntologyLoader(configuration.getOntologyFile1());
		loader2 = new OntologyLoader(configuration.getOntologyFile2());
				
		onto1 = loader1.getOWLOntology();
		onto2 = loader2.getOWLOntology();
				
		LogOutput.printAlways("...Done\n");
	}
	
	
	//Loads systems mappings and uses reasoning
	protected  void loadSystemMappings() throws Exception{
					
		File files = new File(configuration.getMappingsPath());
		String tool_files[] = files.list();
		
		String family;
		
		for(int i=0; i<tool_files.length; i++){			
			
			if (tool_files[i].contains(configuration.getFileNamePattern())){
				
				String mappings_file = configuration.getMappingsPath() + tool_files[i];
				MappingsReaderManager mappingReaderTool = new MappingsReaderManager(mappings_file, MappingsReaderManager.OAEIFormat);
				
				String name = tool_files[i].split(configuration.getFileNamePattern())[0];
				
				name = name.replaceAll("-","");
				
				
				
				//TODO Change for evaluation...
				if (name.contains("2016"))
					continue;
				
				//TODO: Family. Add to configuration files
				if (name.startsWith("LogMap"))
					family = "LogMap";
				else if (name.startsWith("PhenoM"))
					family = "PhenomeNET";
				else if (name.startsWith("DisMatch"))
					family = "DisMatch";
				else if (name.startsWith("AML"))
					family = "AML";
				else if (name.startsWith("XMap"))
					family = "XMap";
				else if (name.startsWith("LYAM") || name.startsWith("YAM"))
					family = "YAM";
				else if (name.startsWith("FCA_Map"))
					family = "FCA-Map";
				else					
					family = name;
				
				
				long time = extractTimeFromLog(name); //tool_files[i]
				
				
				
				//Create entry
				system_results_map.put(name, new SystemMappings(name, tool_files[i]));
				
				
				//Add mappings
				//TODO POMAP 2017 mappings have to be reversed
				if (name.startsWith("POMAP")) {
					system_results_map.get(name).setMappings(onto1, onto2, reverse(mappingReaderTool.getMappingObjects()));
				}
				else {
					system_results_map.get(name).setMappings(onto1, onto2, mappingReaderTool.getMappingObjects());
				}		
				//Global set of mappings + voting
				addMappingsToGlobalSet(system_results_map.get(name).getMappingSet());
				
				//Set family
				system_results_map.get(name).setFamily(family);
				
				
				//Set time (ms)
				system_results_map.get(name).setComputationTime(time);
				
				
				//Entities in mappings
				signature_for_modules.addAll(system_results_map.get(name).getMappingsOntology().getSignature());
				
				
				
				
				
				//Set merged ontology
				//LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
				//system_results_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, mappingReaderTool.getMappingObjects(), extractModules, classifyMergedOntologies, reasonerID));
				//LogOutput.printAlways("\tUnsat: " + system_results_map.get(name).getUnsatisfiableClassesSize());  
				
			}
		}
	}
	
	
	
	/**
	 * We keep voting of mappings too
	 */
	private void addMappingsToGlobalSet(Set<MappingObjectStr> mappings){
		
		for (MappingObjectStr mapping : mappings){
			
			if (!allMappings2votes.containsKey(mapping))
				allMappings2votes.put(mapping, 1);
			else
				allMappings2votes.put(mapping, allMappings2votes.get(mapping)+1);
		
		}
		
	}
	
	
	/**
	 * Extract computation time from SEALS logs (in ms)
	 * @param string
	 * @return
	 */
	private long extractTimeFromLog(String tool_name) {

		
		try {
			
			File files = new File(configuration.getLogsPath());
			String log_files[] = files.list();
			
			for(int i=0; i<log_files.length; i++){			
		
				//System.out.println(log_files[i] + " " + tool_name);
				
				if (log_files[i].contains(configuration.getFileNamePattern())&&
					log_files[i].contains(tool_name) &&
					log_files[i].contains("out")){
				
					//Log file name
					String tool_log_file = configuration.getLogsPath() + log_files[i]; 
							//"out_" + tool_file_name.split(".rdf")[0];
					
				
					//After line starting with >>> Evaluation: read time in next line (4th element):	0.968	0.208	0.342	1185 
					ReadFile reader = new ReadFile(tool_log_file);
					
					String line;
					String[] elements;
					
					line=reader.readLine();
					boolean isResulstLine=false;
					
					
					
					while (line!=null) {
						
						if (line.startsWith("Precision	Recall	F-measure	Run Time")) { //next line >>> Evaluation:
							isResulstLine=true;
							
						}
						else if(isResulstLine) {
							//System.out.println(line);
							if (line.indexOf("\t")>=0){
								elements=line.split("\\t");
								//System.out.println(elements[3]);
								return Long.valueOf(elements[3]);
							}
							
						}
							
						//keep reading
						line=reader.readLine();
					
					}
				}
			}
			return 0;
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1; //in case of error or missing file
		}
		
		
	}
	
	
	/**
	 * Syntactic unique mappings
	 */
	protected void createUniqueMappings() {
		
		for (String tool_name : system_results_map.navigableKeySet()){
			
			for (MappingObjectStr mapping : system_results_map.get(tool_name).getMappingSet()) {
				
				//Unique vote
				if (allMappings2votes.containsKey(mapping) && allMappings2votes.get(mapping)==1) {
					
					system_results_map.get(tool_name).addUniqueMapping(mapping);
					
				}
				
			}
			
			
		}
		
	}


	private Set<MappingObjectStr> reverse(
			Set<MappingObjectStr> mappingObjects) {

		//Set<MappingObjectStr> reversedMappings = new HashSet<MappingObjectStr>();
		
		String tmp;
		for (MappingObjectStr mapping : mappingObjects){
			tmp = mapping.getIRIStrEnt1();
			mapping.setIRIStrEnt1(mapping.getIRIStrEnt2());
			mapping.setIRIStrEnt2(tmp);
			
			//mapping.setIRIStrEnt1(iri1)
			//reversedMappings.add(new MappingObjectStr());
		}
		
		return mappingObjects;
	}

	
	
	protected  void loadReferenceMappings() throws Exception{
		
		//Load reference
		File files = new File(configuration.getReferencesPath());
		//System.out.println(configuration.getReferencesPath() + files.exists());
		String ref_files[] = files.list();
		for(int i=0; i<ref_files.length; i++){
			
			if (ref_files[i].contains(configuration.getFileNamePattern())){
			
				String mappings_file = configuration.getReferencesPath() + ref_files[i];
				MappingsReaderManager mappingReadeReference = new MappingsReaderManager(mappings_file, MappingsReaderManager.OAEIFormat);
				
				
				//TODO reference alignments must follow this pattern too
				String name = ref_files[i].split(configuration.getFileNamePattern())[0];
				
									
				//Create entry
				reference_mappings_map.put(name, new ReferenceMappings(name, ref_files[i]));
				
				
				//Add mappings
				reference_mappings_map.get(name).setMappings(onto1, onto2, mappingReadeReference.getMappingObjects());
				
				
				//Entities in mappings
				signature_for_modules.addAll(reference_mappings_map.get(name).getMappingsOntology().getSignature());
				
				
				//Set merged ontology
				//LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
				//reference_mappings_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, mappingReaderTool.getMappingObjects(), extractModules, classifyMergedOntologies, reasonerID));
				//LogOutput.printAlways("\tUnsat: " + reference_mappings_map.get(name).getUnsatisfiableClassesSize());
				
			}	
			
		}
		
	}
	
	protected  void createMergedOntologies() throws Exception{
		
		createMergedOntologiesForReferenceMappingSets();
		
		createMergedOntologiesForSystemMappingSets();
		
	}
	
	
	protected  void createMergedOntologiesForReferenceMappingSets() throws Exception{
				
		for (String name : reference_mappings_map.keySet()){
			//Set merged ontology
			LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
			reference_mappings_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, reference_mappings_map.get(name).getMappingsOntology(), extractModules, signature_for_modules, classifyMergedOntologies, reasonerID));
			LogOutput.printAlways("\tUnsat: " + reference_mappings_map.get(name).getUnsatisfiableClassesSize());
		}		
				
	}
	
	
	protected  void createMergedOntologiesForSystemMappingSets() throws Exception{
			
		for (String name : system_results_map.keySet()){
			//Set merged ontology
			LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
			system_results_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, system_results_map.get(name).getMappingsOntology(), extractModules, signature_for_modules, classifyMergedOntologies, reasonerID));
			LogOutput.printAlways("\tUnsat: " + system_results_map.get(name).getUnsatisfiableClassesSize());
		}
		
	}
	
	
	protected void computeMeasures(){
		
		//Extract P, R and F
		for (String tool_name : system_results_map.navigableKeySet()){
			
			SystemMappings system = system_results_map.get(tool_name);
			
			for (String reference_name : reference_mappings_map.navigableKeySet()){
				
				ReferenceMappings reference = reference_mappings_map.get(reference_name);
				
				system.addResult(reference_name, new Results(tool_name, reference_name));
				
				
				//Check if unsat: this will affect precision and/or recall
				
				//Compute semantic measures
				/*LogOutput.printAlways("Computing semantic measures for " + tool_name + " against " + reference_name);
				SemanticMeasures.computeSemanticMeasures(
						reference.getOWLReasonerMergedOntology(), 
						system.getOWLReasonerMergedOntology(), 
						reference.getMappingSet(), system.getMappingSet());
				
				
				//Store results
				system.getResults().get(reference_name).
						setSemanticMeasures(SemanticMeasures.getSemanticPrecision(), SemanticMeasures.getSemanticRecall(), SemanticMeasures.getSemanticFscore());
				*/
				
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
	protected void printResults() {

		int i=0;
		
		for (String tool_name : system_results_map.navigableKeySet()){
					
			SystemMappings system = system_results_map.get(tool_name);
		
			if (i==0){
				System.out.println(system.getHeaderForResults());
				i++;
			}			
			
			System.out.println(system.toString());
		}
		
	}
	
	
}
