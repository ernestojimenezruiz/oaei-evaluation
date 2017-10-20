/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.util;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ox.krr.logmap2.io.OWLAlignmentFormat;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

/**
 *
 * @author ernesto
 * Created on 19 Oct 2017
 *
 */
public class Utilities {

	public static OWLOntology createOWLOntologyFromRDFMappings(OWLOntology onto1, OWLOntology onto2, Set<MappingObjectStr> mappings) throws Exception{
		
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
