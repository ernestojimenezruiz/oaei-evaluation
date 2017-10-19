/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.util;

import java.util.Calendar;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.syntactic_locality.ModuleExtractor;
import uk.ac.ox.krr.logmap2.io.LogOutput;
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
	
	public MergedOntology(OWLOntology onto1, OWLOntology onto2, Set<MappingObjectStr> mappings, boolean extractModules, Set<OWLEntity> signature_for_modules, boolean classify, int reasonerID) throws Exception{
		this(onto1, onto2, Utilities.createOWLOntologyFromRDFMappings(onto1, onto2, mappings), extractModules, signature_for_modules, classify, reasonerID);
	}
	
	public MergedOntology(OWLOntology onto1, OWLOntology onto2, OWLOntology mappings,  boolean extractModules, Set<OWLEntity> signature_for_modules, boolean classify, int reasonerID) throws Exception{
		this(onto1.getAxioms(), onto2.getAxioms(), mappings.getAxioms(), extractModules, signature_for_modules, classify, reasonerID);
	}
	
	public MergedOntology(Set<OWLAxiom> onto1, Set<OWLAxiom> onto2, Set<OWLAxiom> mappings,  boolean extractModules, Set<OWLEntity> signature_for_modules, boolean classify, int reasonerID) throws Exception{
		
		createMergedOntology(onto1, onto2, mappings, extractModules, signature_for_modules);
		
		if (classify)
			classifyMergedOntology(reasonerID);
		
		
		
	}
	
	
	
	
	public OWLOntology getOntology(){
		return mergedOntology;
	}
	
	public ReasonerAccess getReasoner(){		
		return mergedReasoner;
	}
	
	
	
	
	
	private void createMergedOntology(Set<OWLAxiom> onto1_ax, Set<OWLAxiom> onto2_ax, Set<OWLAxiom> mappings_ax, boolean extractModules, Set<OWLEntity> signature_for_modules) throws OWLOntologyCreationException{
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.addAll(onto1_ax);
		axioms.addAll(onto2_ax);
		axioms.addAll(mappings_ax);
		
		managerMerged = SynchronizedOWLManager.createOWLOntologyManager();
		
		if (extractModules){
			mergedOntology = extractMergedModule(axioms, mappings_ax, signature_for_modules);
		}
		else{				
			mergedOntology = managerMerged.createOntology(axioms, IRI.create("http://krr.ox.cs.ac.uk/logmap2/integration.owl"));
		}
		
		axioms.clear();
		
		
	}
	
	
	private OWLOntology extractMergedModule(Set<OWLAxiom> axioms, Set<OWLAxiom> mapping_axioms, Set<OWLEntity> signature_for_modules){
		
		//Set<OWLEntity> entities = new HashSet<OWLEntity>();
		
		//for (OWLAxiom ax: mapping_axioms){
		//	entities.addAll(ax.getSignature());
		//}
		
		//Ignore annotations and assertions: only required for reasoning
		ModuleExtractor module_extractor = new ModuleExtractor(axioms, SynchronizedOWLManager.createOWLOntologyManager(), false, false, true, false, true);		
		OWLOntology module = module_extractor.getLocalityModuleForSignatureGroup(signature_for_modules, "http://krr.ox.cs.ac.uk/logmap2/integration_module.owl", false);		
		
		module_extractor.clearStrutures();		
		//entities.clear();
		
		return module;
		
	}
	
	
	private void classifyMergedOntology(int reasonerID) throws Exception{
		boolean usefactory=false;
		
		long init=Calendar.getInstance().getTimeInMillis();
		
		if (reasonerID==ReasonerManager.HERMIT)
			mergedReasoner = new HermiTAccess(managerMerged, mergedOntology, usefactory);
		else if (reasonerID==ReasonerManager.ELK)
			mergedReasoner = new ELKAccess(managerMerged, mergedOntology, usefactory);
		
		mergedReasoner.classifyOntology(false);
		
		long fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.printAlways("\tReasoning time (s): " + (float)((double)fin-(double)init)/1000.0);
	}
	
	
	
	
	

}
