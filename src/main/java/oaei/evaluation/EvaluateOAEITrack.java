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
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;

/**
 * 
 * Configuration:  It is expected to have a path for mappings, 
 * a path for logs from OAEI/Seals, and the reference alignments. 
 * In case of phenotype, the consensus alignment must be created. 
 * 
 *
 *
 * @author ernesto
 * Created on 15 Oct 2017
 *
 */
public class EvaluateOAEITrack extends AbstractEvaluation{
	
		
	
	public EvaluateOAEITrack(boolean extractUniqueness) throws Exception{
		
		//load configuration
		configuration = new OAEIConfiguration();
		
		
		//reasoner
		reasonerID = configuration.getResonerID();
		
		
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
		createUniqueMappings();
		
		
		//Compute measures for systems against references
		computeMeasures();
			
				
		//Print and Store results
		printResults();
		System.out.println("\n");
		printHTML();
		System.out.println("\n");
		printLatex();
		
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
