/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.evaluation.consensus;


import java.util.TreeMap;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;

import oaei.configuration.OAEIConfiguration;
import oaei.evaluation.AbstractEvaluation;
import uk.ac.ox.krr.logmap2.indexing.ExtractStringFromAnnotationAssertionAxiom;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;


/**
 * 
 * Creates consensus mappings for the given configuration
 * For Phenotype track:
 * 1. Create consensus for all available mappings sets
 * 2. Evaluate track against reference alignments
 * 3. Create times.log
 * 4. Latex and HTML tables should be created automatically
 *
 * @author ernesto
 * Created on 30 Oct 2017
 *
 */
public class CreateConsensusAlignments extends AbstractEvaluation {
	
	//Set<ConsensusMapping> consensus = new HashSet<ConsensusMapping>();  
	HashConsensusAlignment consensusAlignment = new HashConsensusAlignment();
	
	ExtractStringFromAnnotationAssertionAxiom annotationExtractor = new ExtractStringFromAnnotationAssertionAxiom();
	
	int max_votes;
	
	
	public CreateConsensusAlignments() throws Exception{
		//load configuration
		configuration = new OAEIConfiguration();
				
		//load ontologies
		loadOntologies();
		
		//load systems
		loadSystemMappings();
		
		//Create consensus
		createConsensusAlignment();
		
					
		//Set confidence for consensus alignments! One could also weight the votes here
		setConfidenceConsensusAlignments();
		
		
		//Output
		printConsensusAlignments();		
		
		
		
		
		
			
	}


	WriteFile writer_statistics;
	/**
	 * @throws Exception 
	 * 
	 */
	private void printConsensusAlignments() throws Exception {
		
		writer_statistics = new WriteFile(configuration.getResultsPath() + "Consensus" + configuration.getFileNamePattern() + ".txt", false);
		
		//We use max votes!
		for (int votes=2; votes<=max_votes; votes++){
		
			//The following methods explore the same structure, but for readability we explore the alignments 3 times
			
			storeReadableAlignments(votes);
			
			storeRDFAlignments(votes);
			
			//print statistics
			keepStatistics(votes);
			
		}
		
		writer_statistics.closeBuffer();
		
		
	}
	
	
	/**
	 * 
	 */
	private void keepStatistics(int min_required_votes) {
		
		
		
		ConsensusMapping cmapping;
		
		int num_mappings=0;
		TreeMap<String, Integer> contributingSystems = new TreeMap<String, Integer>(); 
		TreeMap<String, Integer> contributingFamilies = new TreeMap<String, Integer>(); 
		

		for (String source : consensusAlignment.getSources()){

			for (String target : consensusAlignment.getTargets(source)){
		
				cmapping = consensusAlignment.getConsensusMapping(source, target);							
		
				if (cmapping.getFamilyVotes()>=min_required_votes){
					
					num_mappings++;
					
					for (String csystem : cmapping.getVotingSystems()){
						
						contributingSystems.put(csystem, contributingSystems.containsKey(csystem) ? contributingSystems.get(csystem) + 1 : 1);
												
					}
					
					for (String cfamily : cmapping.getVotingFamilies()){
						contributingFamilies.put(cfamily, contributingFamilies.containsKey(cfamily) ? contributingFamilies.get(cfamily) + 1 : 1);
					}	
				
				}	
		
			}
		}
		
		writer_statistics.writeLine("Vote " + min_required_votes);
		writer_statistics.writeLine("\tMappings: " + num_mappings);
					
		//System.out.println(contributingFamilies);
		writer_statistics.writeLine("\tFamilies:");
		for (String family: contributingFamilies.navigableKeySet()){
			double contribution = Math.round((double)contributingFamilies.get(family) * 1000.0 / (double)num_mappings)/10.0;
			writer_statistics.writeLine("\t\t" + family + ": " + contribution + "%");
		}
		
		//System.out.println(contributingSystems);
		writer_statistics.writeLine("\tSystems:");
		for (String system: contributingSystems.navigableKeySet()){
			double contribution = Math.round((double)contributingSystems.get(system) * 1000.0 / (double)num_mappings)/10.0;
			writer_statistics.writeLine("\t\t" + system + ": " + contribution + "%");
		}
		
		writer_statistics.writeEmptyLine();
		
	
		
	}


	/**
	 * Stores consensus alignments in a readable format: uri1   label1   uri2   label2   confidence   #fam. votes   families	#syst. votes    systems 
	 * @param min_required_votes
	 */
	private void storeReadableAlignments(int min_required_votes){
		
		WriteFile writer = new WriteFile(configuration.getReferencesPath() + "Consensus-" + String.valueOf(min_required_votes) + configuration.getFileNamePattern() + ".tsv");
		
		writer.writeLine("#URI 1" + "\t" + "Label 1"  + "\t" + "URI 2" + "\t" + "Label 2"  + "\t" + "Confidence"  + "\t" + "Family votes"  + "\t" + "Families"  + "\t" + "System votes"  + "\t" + "Systems");
		
		ConsensusMapping cmapping;
		
		for (String source : consensusAlignment.getSources()){

			for (String target : consensusAlignment.getTargets(source)){
				
				cmapping = consensusAlignment.getConsensusMapping(source, target);							
				
				if (cmapping.getFamilyVotes()>=min_required_votes)				
					writer.writeLine(cmapping.toString());
				
			}
		}
		
		
		writer.closeBuffer();
		
		
	}
	
	
	/**
	 * OAEI-RDF Alignment format
	 * @param min_required_votes
	 * @throws Exception
	 */
	private void storeRDFAlignments(int min_required_votes) throws Exception{
		OutPutFilesManager output_manager = new OutPutFilesManager();
		
		output_manager.createOutFiles(
				configuration.getReferencesPath() + "Consensus-" + String.valueOf(min_required_votes) + configuration.getFileNamePattern(), 
				OutPutFilesManager.OAEIFormat,
				configuration.getOntologyURI1(), 
				configuration.getOntologyURI2());
		
		ConsensusMapping cmapping;
		
		for (String source : consensusAlignment.getSources()){

			for (String target : consensusAlignment.getTargets(source)){
				
				cmapping = consensusAlignment.getConsensusMapping(source, target);
				
				if (cmapping.getFamilyVotes()>=min_required_votes){
					
					if (cmapping.isClassMapping())
						output_manager.addClassMapping2Files(
								cmapping.getIRIStrEnt1(), cmapping.getIRIStrEnt2(), cmapping.getMappingDirection(), cmapping.getConfidence());
					else if (cmapping.isDataPropertyMapping())
						output_manager.addDataPropMapping2Files(
								cmapping.getIRIStrEnt1(), cmapping.getIRIStrEnt2(), cmapping.getMappingDirection(), cmapping.getConfidence());
					else if (cmapping.isObjectPropertyMapping())
						output_manager.addObjPropMapping2Files(
								cmapping.getIRIStrEnt1(), cmapping.getIRIStrEnt2(), cmapping.getMappingDirection(), cmapping.getConfidence());
					else if (cmapping.isInstanceMapping())
						output_manager.addInstanceMapping2Files(
								cmapping.getIRIStrEnt1(), cmapping.getIRIStrEnt2(), cmapping.getConfidence());
				}
			}
			
		}
		
		output_manager.closeAndSaveFiles();
		
		
	}


	/**
	 * 
	 */
	private void createConsensusAlignment() {
		for (String system : system_results_map.keySet()){
			
			for (MappingObjectStr mapping : system_results_map.get(system).getMappingSet()){
				
				//We avoid mappings among same URIS
				if (!mapping.getIRIStrEnt1().equals(mapping.getIRIStrEnt2())){
					
					consensusAlignment.add(
							mapping.getIRIStrEnt1(), 
							mapping.getIRIStrEnt2(), 
							system, 
							system_results_map.get(system).getFamily(),
							getMappingType(IRI.create(mapping.getIRIStrEnt1()), onto1),
							getLabel4Entity(IRI.create(mapping.getIRIStrEnt1()), onto1),
							getLabel4Entity(IRI.create(mapping.getIRIStrEnt2()), onto2));
				}
			}			
		}		
	}
	
	
	
	
	/**
	 * 
	 * @return
	 */
	private int getMappingType(IRI uri, OWLOntology onto) {
		if (onto.containsClassInSignature(uri, Imports.INCLUDED)){
			return MappingObjectStr.CLASSES;
		}
		else if (onto.containsDataPropertyInSignature(uri, Imports.INCLUDED)){
			return MappingObjectStr.DATAPROPERTIES;					
		}
		else if (onto.containsObjectPropertyInSignature(uri, Imports.INCLUDED)){
			return MappingObjectStr.OBJECTPROPERTIES;
		}
		else if (onto.containsIndividualInSignature(uri, Imports.INCLUDED)){
			return MappingObjectStr.INSTANCES;
		}
		else{
			return MappingObjectStr.UNKNOWN;
		}
			
	}


	private String getLabel4Entity(IRI uri, OWLOntology onto){
		
		OWLEntity entity;
		String label=uri.toString();
		
		if (onto.containsClassInSignature(uri, Imports.INCLUDED)){
			entity = onto.getOWLOntologyManager().getOWLDataFactory().getOWLClass(uri);
		}
		else if (onto.containsDataPropertyInSignature(uri, Imports.INCLUDED)){
			entity = onto.getOWLOntologyManager().getOWLDataFactory().getOWLDataProperty(uri);					
		}
		else if (onto.containsObjectPropertyInSignature(uri, Imports.INCLUDED)){
			entity = onto.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(uri);
		}
		else if (onto.containsIndividualInSignature(uri, Imports.INCLUDED)){
			entity = onto.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(uri);
		}
		else{
			return uri.toString(); //we return uri
		}
			
		
		//Label from accepted annotations
		//for (OWLAnnotationAssertionAxiom annAx : entity.getAnnotationAssertionAxioms(onto)){
		for (OWLAnnotationAssertionAxiom annAx : EntitySearcher.getAnnotationAssertionAxioms(entity, onto)) {
			label = annotationExtractor.getSingleLabel(annAx, onto, onto.getOWLOntologyManager().getOWLDataFactory());
			if (!label.equals(""))
				return label;
		}
		
		
		//URI if not accepted annotation found
		return uri.toString();
			
			
			
	}
	
	
	
	
	
	
	private void setConfidenceConsensusAlignments(){
		
		max_votes = consensusAlignment.getMaxVotes();
		
		consensusAlignment.setConfidenceForMappings(max_votes);
		
	}
	
	
	public static void main (String[] argss){
		try {
			new CreateConsensusAlignments();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
