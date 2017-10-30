/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.evaluation;



import oaei.configuration.OAEIConfiguration;
import oaei.mappings.ReferenceMappings;
import oaei.mappings.SystemMappings;
import oaei.measures.SemanticMeasures;
import oaei.measures.StandardMeasures;
import oaei.results.Results;
import uk.ac.ox.krr.logmap2.io.LogOutput;

/**
 *
 * @author ernesto
 * Created on 15 Oct 2017
 *
 */
public class EvaluateOAEITrack extends AbstractEvaluation{
	
		
	
	public EvaluateOAEITrack(boolean extractUniqueness) throws Exception{
		
		//load configuration
		configuration = new OAEIConfiguration();
				
		//load ontologies
		loadOntologies();
		
		//load reference
		loadReferenceMappings();
		
		//load systems
		loadSystemMappings();
		
		//Create Merged ontologies
		createMergedOntologies();
		
		//TODO Extract uniqueness (optional)
		//Store uniqueness files: extended tsv files with labels (others than RDFS:label)?
		
		
		//Compute measures for systems against references
		computeMeasures();
			
				
		//Print and Store results
		printResults();
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
				System.out.println(system.getHeaderForResults());
				i++;
			}			
			
			System.out.println(system.toString());
		}
		
	}
	
	
	private void storeResults() {
		//TODO
		
	}


	
	
	
	public static void main (String[] argss){
		try {
			new EvaluateOAEITrack(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	

}
