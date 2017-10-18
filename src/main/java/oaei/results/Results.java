/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.results;

/**
 * Results of a given systems agains a refernece alignment
 * @author ernesto
 * Created on 18 Oct 2017
 *
 */
public class Results {	
	
	private String system_name;
	private String reference_name;
	
	private double sem_precision=0.0;
	private double sem_recall=0.0;
	private double sem_fscore=0.0;
	
	private double precision=0.0;
	private double recall=0.0;
	private double fscore=0.0;
	
	public Results(String system_name, String reference_name){
		this.system_name = system_name;
		this.reference_name = reference_name;
	}
	
	public void setStandardMeasures(double precision, double recall, double fscore){
		this.precision=precision;
		this.recall=recall;
		this.fscore=fscore;
	}
	
	public void setemanticMeasures(double precision, double recall, double fscore){
		this.sem_precision=precision;
		this.sem_recall=recall;
		this.sem_fscore=fscore;
	}
	
	/**
	 * @return the semantic precision
	 */
	public double getSemanticPrecision() {
		return sem_precision;
	}
	
	/**
	 * @return the semantic recall
	 */
	public double getSemanticRecall() {
		return sem_recall;
	}
	
	/**
	 * @return the semantic fscore
	 */
	public double getSemanticFscore() {
		return sem_fscore;
	}
	
	
	/**
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}
	
	/**
	 * @return the recall
	 */
	public double getRecall() {
		return recall;
	}
	
	/**
	 * @return the fscore
	 */
	public double getFscore() {
		return fscore;
	}

	/**
	 * @return the reference_name
	 */
	public String getReferenceName() {
		return reference_name;
	}

	/**
	 * @return the system_name
	 */
	public String getSystemName() {
		return system_name;
	}


	

}
