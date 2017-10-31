/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.evaluation.consensus;

import java.util.HashMap;
import java.util.Set;




/**
 *
 * @author ernesto
 * Created on 30 Oct 2017
 *
 */
public class HashConsensusAlignment {

	private HashMap<String,HashMap<String,ConsensusMapping>> alignment;
	private int size;
	
	/**
	 * Constructs a new empty HashAlignment
	 */
	public HashConsensusAlignment()
	{
		alignment = new HashMap<String,HashMap<String,ConsensusMapping>>();
		size = 0;
	}
		
	
	public void add(String uri1, String uri2, String system, String family, int type, String label1, String label2)
	{
		
		//If the mapping already exists in the HashAlignment, return
		if(!this.contains(uri1)){
			alignment.put(uri1, new HashMap<String, ConsensusMapping>());			
		}
		
		if (!this.contains(uri1, uri2)){ //Does contain uri1 but not uri2
			alignment.get(uri1).put(uri2, new ConsensusMapping(uri1, uri2));
		}
		
		alignment.get(uri1).get(uri2).setTypeOfMapping(type);
		
		alignment.get(uri1).get(uri2).addVotingFamily(family);
		alignment.get(uri1).get(uri2).addVotingSystem(system);
		
		//Labels to easy reading
		alignment.get(uri1).get(uri2).setLabelEntity1(label1);
		alignment.get(uri1).get(uri2).setLabelEntity2(label2);
		
		
		
		
	}
	

	public boolean contains(String uri1)
	{
		return  alignment.containsKey(uri1);
	}
	
	public boolean contains(String uri1, String uri2)
	{
		return  alignment.containsKey(uri1) && alignment.get(uri1).containsKey(uri2);
	}
	
	
	
	public int getMaxVotes(){
	
		int max_votes=-1;
		
		for (String key1: alignment.keySet()){
			
			for (String key2: alignment.get(key1).keySet()){
				
				if (alignment.get(key1).get(key2).getFamilyVotes()>max_votes)
					max_votes=alignment.get(key1).get(key2).getFamilyVotes();
				
			}
			
		}
		
		return max_votes;
		
	}
	
	
	public void setConfidenceForMappings(int max_votes){
	
		//int max_votes = getMaxVotes();
		

		for (String key1: alignment.keySet()){
			
			for (String key2: alignment.get(key1).keySet()){
				
				alignment.get(key1).get(key2).setConfidenceConsensusMapping(max_votes);
				
			}
		}
		
		
	}
	
	
	/**
	 * @return the set of source ontology entity URIs in this alignment
	 */
	public Set<String> getSources()
	{
		return alignment.keySet();
	}
	
	/**
	 * @param source: the source ontology entity URI to retrieve
	 * @return the set of target ontology entity URIs aligned to
	 * the given entity in this alignment
	 */
	public Set<String> getTargets(String source)
	{
		//if(alignment.containsKey(source))
		return alignment.get(source).keySet();
		//return null;
	}
	
	
	/**
	 * @param source: the source ontology entity URI to retrieve
	 * @param target: the target ontology entity URI to retrieve
	 * @return the ConsensusMapping between the given entities
	 */
	public ConsensusMapping getConsensusMapping(String source, String target)
	{
		//if(alignment.containsKey(source) && alignment.get(source).containsKey(target))
		return alignment.get(source).get(target);
		//return null;
	}
	
	
}
