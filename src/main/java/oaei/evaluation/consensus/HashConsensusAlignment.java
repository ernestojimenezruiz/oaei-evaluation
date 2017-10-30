/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.evaluation.consensus;

import java.util.HashMap;




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
		
	
	public void add(String uri1, String uri2, String system, String family)
	{
		
		//If the mapping already exists in the HashAlignment, return
		if(!this.contains(uri1)){
			alignment.put(uri1, new HashMap<String, ConsensusMapping>());			
		}
		
		if (!this.contains(uri1, uri2)){ //Does contain uri1
			alignment.get(uri1).put(uri2, new ConsensusMapping(uri1, uri2));
		}
		
		alignment.get(uri1).get(uri2).addVotingFamily(family);
		alignment.get(uri1).get(uri2).addVotingSystem(system);
		
		
		
		
		
	}
	

	public boolean contains(String uri1)
	{
		return  alignment.containsKey(uri1);
	}
	
	public boolean contains(String uri1, String uri2)
	{
		return  alignment.containsKey(uri1) && alignment.get(uri1).containsKey(uri2);
	}
}
