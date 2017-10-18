/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.util;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.hp.hpl.jena.reasoner.Reasoner;

import uk.ac.ox.krr.logmap2.io.OWLAlignmentFormat;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.reasoning.ELKAccess;
import uk.ac.ox.krr.logmap2.reasoning.HermiTAccess;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerAccess;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;

/**
 * This class creates a merged ontology and classifies it
 * @author ernesto
 * Created on 16 Oct 2017
 *
 */
public class MergedOntology {
	
	
	private OWLOntology mergedOntology;
	
	private ReasonerAccess mergedReasoner;
	
	private OWLOntologyManager managerMerged;
	
	public MergedOntology(OWLOntology onto1, OWLOntology onto2, Set<MappingObjectStr> mappings, boolean classify, int reasonerID) throws Exception{
		this(onto1, onto2, createOWLOntologyFromRDFMappings(onto1, onto2, mappings), classify, reasonerID);
	}
	
	public MergedOntology(OWLOntology onto1, OWLOntology onto2, OWLOntology mappings, boolean classify, int reasonerID) throws Exception{
		this(onto1.getAxioms(), onto2.getAxioms(), mappings.getAxioms(), classify, reasonerID);
	}
	
	public MergedOntology(Set<OWLAxiom> onto1, Set<OWLAxiom> onto2, Set<OWLAxiom> mappings, boolean classify, int reasonerID) throws Exception{
		
		createMergedOntology(onto1, onto2, mappings);
		
		if (classify)
			classifyMergedOntology(reasonerID);
		
		
		
	}
	
	
	
	
	public OWLOntology getOntology(){
		return mergedOntology;
	}
	
	public ReasonerAccess getReasoner(){		
		return mergedReasoner;
	}
	
	
	
	
	
	private void createMergedOntology(Set<OWLAxiom> onto1_ax, Set<OWLAxiom> onto2_ax, Set<OWLAxiom> mappings_ax) throws OWLOntologyCreationException{
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.addAll(onto1_ax);
		axioms.addAll(onto2_ax);
		axioms.addAll(mappings_ax);
				
		managerMerged = SynchronizedOWLManager.createOWLOntologyManager();
		mergedOntology = managerMerged.createOntology(axioms, IRI.create("http://krr.ox.cs.ac.uk/logmap2/integration.owl"));
	}
	
	
	private void classifyMergedOntology(int reasonerID) throws Exception{
		boolean usefactory=false;
		
		if (reasonerID==ReasonerManager.HERMIT)
			mergedReasoner = new HermiTAccess(managerMerged, mergedOntology, usefactory);
		else if (reasonerID==ReasonerManager.ELK)
			mergedReasoner = new ELKAccess(managerMerged, mergedOntology, usefactory);
	}
	
	
	
	private static OWLOntology createOWLOntologyFromRDFMappings(OWLOntology onto1, OWLOntology onto2, Set<MappingObjectStr> mappings) throws Exception{
		
		OWLAlignmentFormat owlOutput =  new OWLAlignmentFormat(""); // we do not want to save the file			
		
		for (MappingObjectStr mapping : mappings){			
			
				if (onto1.containsClassInSignature(IRI.create(mapping.getIRIStrEnt1()), true)
					&& onto2.containsClassInSignature(IRI.create(mapping.getIRIStrEnt2()), true)) {
					
					owlOutput.addClassMapping2Output(
							mapping.getIRIStrEnt1(), 
							mapping.getIRIStrEnt2(), 
							mapping.getMappingDirection(), 
							mapping.getConfidence());
					
					
				}
				else if (onto1.containsObjectPropertyInSignature(IRI.create(mapping.getIRIStrEnt1()), true)
						&& onto2.containsObjectPropertyInSignature(IRI.create(mapping.getIRIStrEnt2()), true)) {
					
					owlOutput.addObjPropMapping2Output(
							mapping.getIRIStrEnt1(), 
							mapping.getIRIStrEnt2(), 
							mapping.getMappingDirection(), 
							mapping.getConfidence());
					
					
					
				
				}
				else if (onto1.containsDataPropertyInSignature(IRI.create(mapping.getIRIStrEnt1()), true)
					&& onto2.containsDataPropertyInSignature(IRI.create(mapping.getIRIStrEnt2()), true)) {
					
					owlOutput.addDataPropMapping2Output(
							mapping.getIRIStrEnt1(), 
							mapping.getIRIStrEnt2(), 
							mapping.getMappingDirection(), 
							mapping.getConfidence());
					
					
				}
				
				else if (onto1.containsIndividualInSignature(IRI.create(mapping.getIRIStrEnt1()), true)
						&& onto2.containsIndividualInSignature(IRI.create(mapping.getIRIStrEnt2()), true)) {
					
					owlOutput.addInstanceMapping2Output(
							mapping.getIRIStrEnt1(), 
							mapping.getIRIStrEnt2(), 
							mapping.getConfidence());
					
				}
				
			
		}
		
		return owlOutput.getOWLOntology();
	
	}
	

}
