/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.evaluation.consensus;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

/**
 *
 * @author ernesto
 * Created on 30 Oct 2017
 *
 */
public class ConsensusMapping extends MappingObjectStr {

	private String label1;	
	private String label2;
	
	private Set<String> voting_systems = new HashSet<String>();
	private Set<String> voting_families  = new HashSet<String>();
	
	
	/**
	 * @param iri_ent1
	 * @param iri_ent2
	 */
	public ConsensusMapping(String iri_ent1, String iri_ent2) {
		super(iri_ent1, iri_ent2);
		
	}
	
	public void addVotingSystem(String system){
		voting_systems.add(system);
	}
	
	
	
	public void addVotingFamily(String family){
		voting_families.add(family);
	}
	
	
	public int getSystemVotes(){
		return voting_systems.size();
	}
	
	public int getFamilyVotes(){
		return voting_families.size();
	}
	
	
	public void setConfidenceConsensusMapping(int max_votes, int min_required_votes){
		
		//For the output confidence
		double y1_min_confidence = 0.7;		
		double y2_max_confidence = 1.0;
				
				
		int x1_min_votes = min_required_votes;			
		int x2_max_votes = max_votes;
		
		double coef = (y2_max_confidence-y1_min_confidence)/(double)((double)x2_max_votes-(double)x1_min_votes);
		
		setConfidenceMapping(coef*(getFamilyVotes()-x1_min_votes) + y1_min_confidence);
	}
	
	
	
	
	public String toString(){

		String systems="";
		for (String s : voting_systems){
			systems+=s+";";
		}
		systems = StringUtils.removeEnd(systems, ";");
		
		String families="";
		for (String f : voting_families){
			families+=f+";";
		}
		families = StringUtils.removeEnd(families, ";");
		
		
		
		return this.getIRIStrEnt1() + "\t" + this.getLabelEntity1() + "\t" +  this.getIRIStrEnt2() + "\t" + this.getLabelEntity2() + "\t" + this.getConfidence() + "\t" + this.getSystemVotes()  + "\t" +  systems + "\t" + this.getFamilyVotes() + "\t" +  families;
		
	}

	/**
	 * @return the label2
	 */
	public String getLabelEntity2() {
		return label2;
	}

	/**
	 * @param label2 the label2 to set
	 */
	public void setLabelEntity2(String label2) {
		this.label2 = label2;
	}

	/**
	 * @return the label1
	 */
	public String getLabelEntity1() {
		return label1;
	}

	/**
	 * @param label1 the label1 to set
	 */
	public void setLabelEntity1(String label1) {
		this.label1 = label1;
	}
	
	

}
