package oaei.evaluation;


import java.io.File;
import java.util.HashSet;
import java.util.Set;

import oaei.configuration.OAEIConfiguration;
import oaei.mappings.SystemMappings;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;


/**
 * To filter mappings involving imported/reused entities from other ontologies
 * @author ernesto
 *
 */
public class FilterSystemMappings  extends AbstractEvaluation { 
	
	
	public FilterSystemMappings() throws Exception {
		
		//load configuration
		configuration = new OAEIConfiguration();
		
		//load ontologies
		//loadOntologies();
		
		//load systems
		loadSystemMappings();
				
		
	}
	
	
	//Loads systems mappings and uses reasoning
	protected  void loadSystemMappings() throws Exception{
					
		File files = new File(configuration.getMappingsPath());
		String tool_files[] = files.list();
		
		String family;
		
		System.out.println("Number of mappings without namspace filtering");
		
		for(int i=0; i<tool_files.length; i++){			
			
			if (tool_files[i].contains(configuration.getFileNamePattern())){
				
				String mappings_file = configuration.getMappingsPath() + tool_files[i];
				MappingsReaderManager mappingReaderTool = new MappingsReaderManager(mappings_file, MappingsReaderManager.OAEIFormat);
				
				String name = tool_files[i].split(configuration.getFileNamePattern())[0];
				
				name = name.replaceAll("-","");
				
				
				//Before filtering
				System.out.println("Loading mappings for " + name + ": " + mappingReaderTool.getMappingObjects().size());
				
				
				
				saveFilteredMappings(tool_files[i], mappingReaderTool.getMappingObjects());
				
			}
		}
	}
	
	
	/**
	 * We filter according to namespace of the mapping entities. To avoid mappings among imported/reused ontologies (for example). 
	 * @return
	 * @throws Exception 
	 */
	protected void saveFilteredMappings(String file_name, Set<MappingObjectStr> mappings) throws Exception{
		
		OutPutFilesManager output_manager = new OutPutFilesManager();
		
		String filtered_file_name = file_name.split(configuration.getFileNamePattern())[0] + configuration.getFileNamePattern() + "-filtered";
		
		
		output_manager.createOutFiles(
				configuration.getMappingsPath() +"filtered/"+ filtered_file_name, 
				OutPutFilesManager.OAEIFormat,
				configuration.getOntologyURI1(), 
				configuration.getOntologyURI2());
		
		
		//Set<MappingObjectStr> filtered_mappings = new HashSet<MappingObjectStr>();
		
		if (configuration.getNameSpace1() == null || configuration.getNameSpace2() == null || configuration.getNameSpace1().equals("") || configuration.getNameSpace2().equals(""))
			return;
		
		int fmappings=0;
		
		for (MappingObjectStr m : mappings) {
			
			if (m.getIRIStrEnt1().contains(configuration.getNameSpace1()) && 
				m.getIRIStrEnt2().contains(configuration.getNameSpace2())) {
				
				fmappings++;
				
				
				output_manager.addClassMapping2Files(
						m.getIRIStrEnt1(), m.getIRIStrEnt2(), m.getMappingDirection(), m.getConfidence());
				
				
			}
			
			
		}
		
		System.out.println("\tFiltered mappings: "+ fmappings);
		
		output_manager.closeAndSaveFiles();
		
		
		
	}
	
	
	
	
	
	
	
	
	public static void main (String[] argss){
		try {
			new FilterSystemMappings();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
