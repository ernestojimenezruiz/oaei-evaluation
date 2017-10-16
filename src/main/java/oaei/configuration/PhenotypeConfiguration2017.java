/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.configuration;

/**
 *
 * @author ernesto
 * Created on 16 Oct 2017
 *
 */
public class PhenotypeConfiguration2017 extends OAEIConfiguration {
	
	public enum Task {HP_MP, DOID_ORDO, HP_MESH, HP_OMIM};
	
	PhenotypeConfiguration2017(Task task){
		
		//Shared configuration
		
		
		switch (task) {
        case HP_MP: 
        	HP_MP_task();
            break;
        case DOID_ORDO:
        	DOID_ORDO_task();
        	break;
        case HP_MESH:
        	HP_MESH_task();
        	break;
        case HP_OMIM:
        	HP_OMIM_task();
        	break;
        default:
        	HP_MP_task();
            break;
		}
		
			
		
		
	}
	
	
	private void HP_MP_task(){
		
	}
	
	private void DOID_ORDO_task(){
		
	}
	
	private void HP_OMIM_task(){
		
	}

	private void HP_MESH_task(){
	
	}
	

}
