/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package oaei.evaluation;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import oaei.configuration.OAEIConfiguration;
import oaei.mappings.ReferenceMappings;
import oaei.mappings.SystemMappings;
import oaei.measures.StandardMeasures;
import oaei.results.Results;
import oaei.util.MergedOntology;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;

/**
 * Abstract EValuation Tasks: deals with loading and general reasoning
 *
 * @author ernesto
 * Created on 30 Oct 2017
 *
 */
public abstract class AbstractEvaluation {
	
	
	protected OAEIConfiguration configuration;
	
	protected OWLOntology onto1;
	protected OWLOntology onto2;
	
	
	protected TreeMap<String, SystemMappings> system_results_map = new TreeMap<String, SystemMappings>();
	protected TreeMap<String, ReferenceMappings> reference_mappings_map = new TreeMap<String, ReferenceMappings>();
	
	private Map<MappingObjectStr, Integer> allMappings2votes = new HashMap<MappingObjectStr, Integer>();
	
	//To store all entities in relevant mapping and extract modules for these entities
	//Important to cover entities from the reference alignments
	protected Set<OWLEntity> signature_for_modules = new HashSet<OWLEntity>();
	
	
	//To be selected from configuration
	protected int reasonerID = ReasonerManager.HERMIT;
	
	protected boolean classifyMergedOntologies = true;
	protected boolean extractModules = false; 
	
	

	protected  void loadOntologies() throws OWLOntologyCreationException{
		
		OntologyLoader loader1;
		OntologyLoader loader2;
				
		LogOutput.printAlways("Loading ontologies...");
		loader1 = new OntologyLoader(configuration.getOntologyFile1());
		loader2 = new OntologyLoader(configuration.getOntologyFile2());
				
		onto1 = loader1.getOWLOntology();
		onto2 = loader2.getOWLOntology();
				
		LogOutput.printAlways("...Done\n");
	}
	
	
	//Loads systems mappings and uses reasoning
	protected  void loadSystemMappings() throws Exception{
					
		File files = new File(configuration.getMappingsPath());
		String tool_files[] = files.list();
		
		String family;
		
		for(int i=0; i<tool_files.length; i++){			
			
			if (tool_files[i].contains(configuration.getFileNamePattern())){
				
				String mappings_file = configuration.getMappingsPath() + tool_files[i];
				MappingsReaderManager mappingReaderTool = new MappingsReaderManager(mappings_file, MappingsReaderManager.OAEIFormat);
				
				String name = tool_files[i].split(configuration.getFileNamePattern())[0];
				
				name = name.replaceAll("-","");
				
				
				//TODO Change for evaluation...
				//if (name.contains("2016"))
				//	continue;
				
				//TODO: Family. Add to configuration files
				//For systems with more than one year of results and variants
				if (name.startsWith("LogMap"))
					family = "LogMap";
				else if (name.startsWith("PhenoM"))
					family = "PhenomeNET";
				else if (name.startsWith("DisMatch"))
					family = "DisMatch";
				else if (name.startsWith("AML"))
					family = "AML";
				else if (name.startsWith("XMap"))
					family = "XMap";
				else if (name.startsWith("LYAM") || name.startsWith("YAM"))
					family = "YAM";
				else if (name.startsWith("FCA_Map"))
					family = "FCA-Map";
				else					
					family = name;
				
				
				long time = extractTimeFromLog(name); //tool_files[i]
				
				//Convert to seconds and round
				time = Math.round((double)time / (double)1000);;
				
				
				
				//Create entry
				system_results_map.put(name, new SystemMappings(name, tool_files[i]));
				
				
				//Add mappings
				//TODO POMAP 2017 mappings have to be reversed
				if (name.startsWith("POMAP")) {
					system_results_map.get(name).setMappings(onto1, onto2, reverse(mappingReaderTool.getMappingObjects()));
				}
				else {
					system_results_map.get(name).setMappings(onto1, onto2, mappingReaderTool.getMappingObjects());
				}		
				//Global set of mappings + voting
				addMappingsToGlobalSet(system_results_map.get(name).getMappingSet());
				
				//Set family
				system_results_map.get(name).setFamily(family);
				
				
				//Set time (ms)
				system_results_map.get(name).setComputationTime(time);
				
				
				//Entities in mappings
				signature_for_modules.addAll(system_results_map.get(name).getMappingsOntology().getSignature());
				
				
				
				
				
				//Set merged ontology
				//LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
				//system_results_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, mappingReaderTool.getMappingObjects(), extractModules, classifyMergedOntologies, reasonerID));
				//LogOutput.printAlways("\tUnsat: " + system_results_map.get(name).getUnsatisfiableClassesSize());  
				
			}
		}
	}
	
	
	
	/**
	 * We keep voting of mappings too
	 */
	private void addMappingsToGlobalSet(Set<MappingObjectStr> mappings){
		
		for (MappingObjectStr mapping : mappings){
			
			if (!allMappings2votes.containsKey(mapping))
				allMappings2votes.put(mapping, 1);
			else
				allMappings2votes.put(mapping, allMappings2votes.get(mapping)+1);
		
		}
		
	}
	
	
	/**
	 * Extract computation time from SEALS logs (in ms)
	 * @param string
	 * @return
	 */
	private long extractTimeFromLog(String tool_name) {

		
		try {
			
			File files = new File(configuration.getLogsPath());
			String log_files[] = files.list();
			
			for(int i=0; i<log_files.length; i++){			
		
				//System.out.println(log_files[i] + " " + tool_name);
				
				if (log_files[i].contains(configuration.getFileNamePattern())&&
					log_files[i].contains(tool_name) &&
					log_files[i].contains("out")){
				
					//Log file name
					String tool_log_file = configuration.getLogsPath() + log_files[i]; 
							//"out_" + tool_file_name.split(".rdf")[0];
					
				
					//After line starting with >>> Evaluation: read time in next line (4th element):	0.968	0.208	0.342	1185 
					ReadFile reader = new ReadFile(tool_log_file);
					
					String line;
					String[] elements;
					
					line=reader.readLine();
					boolean isResulstLine=false;
					
					
					
					while (line!=null) {
						
						if (line.startsWith("Precision	Recall	F-measure	Run Time")) { //next line >>> Evaluation:
							isResulstLine=true;
							
						}
						else if(isResulstLine) {
							//System.out.println(line);
							if (line.indexOf("\t")>=0){
								elements=line.split("\\t");
								//System.out.println(elements[3]);
								return Long.valueOf(elements[3]);
							}
							
						}
							
						//keep reading
						line=reader.readLine();
					
					}
				}
			}
			return 0;
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1; //in case of error or missing file
		}
		
		
	}
	
	
	/**
	 * Syntactic unique mappings
	 */
	protected void createUniqueMappings() {
		
		for (String tool_name : system_results_map.navigableKeySet()){
			
			for (MappingObjectStr mapping : system_results_map.get(tool_name).getMappingSet()) {
				
				//Unique vote
				if (allMappings2votes.containsKey(mapping) && allMappings2votes.get(mapping)==1) {
					
					system_results_map.get(tool_name).addUniqueMapping(mapping);
					
				}
				
			}
			
			
		}
		
	}


	private Set<MappingObjectStr> reverse(
			Set<MappingObjectStr> mappingObjects) {

		//Set<MappingObjectStr> reversedMappings = new HashSet<MappingObjectStr>();
		
		String tmp;
		for (MappingObjectStr mapping : mappingObjects){
			tmp = mapping.getIRIStrEnt1();
			mapping.setIRIStrEnt1(mapping.getIRIStrEnt2());
			mapping.setIRIStrEnt2(tmp);
			
			//mapping.setIRIStrEnt1(iri1)
			//reversedMappings.add(new MappingObjectStr());
		}
		
		return mappingObjects;
	}

	
	
	protected  void loadReferenceMappings() throws Exception{
		
		//Load reference
		File files = new File(configuration.getReferencesPath());
		//System.out.println(configuration.getReferencesPath() + files.exists());
		String ref_files[] = files.list();
		for(int i=0; i<ref_files.length; i++){
			
			if (ref_files[i].contains(configuration.getFileNamePattern())){
			
				String mappings_file = configuration.getReferencesPath() + ref_files[i];
				MappingsReaderManager mappingReadeReference = new MappingsReaderManager(mappings_file, MappingsReaderManager.OAEIFormat);
				
				
				//TODO reference alignments must follow this pattern too
				String name = ref_files[i].split(configuration.getFileNamePattern())[0];
				
									
				//Create entry
				reference_mappings_map.put(name, new ReferenceMappings(name, ref_files[i]));
				
				
				//Add mappings
				reference_mappings_map.get(name).setMappings(onto1, onto2, mappingReadeReference.getMappingObjects());
				
				
				//Entities in mappings
				signature_for_modules.addAll(reference_mappings_map.get(name).getMappingsOntology().getSignature());
				
				
				//Set merged ontology
				//LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
				//reference_mappings_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, mappingReaderTool.getMappingObjects(), extractModules, classifyMergedOntologies, reasonerID));
				//LogOutput.printAlways("\tUnsat: " + reference_mappings_map.get(name).getUnsatisfiableClassesSize());
				
			}	
			
		}
		
	}
	
	protected  void createMergedOntologies() throws Exception{
		
		createMergedOntologiesForReferenceMappingSets();
		
		createMergedOntologiesForSystemMappingSets();
		
	}
	
	
	protected  void createMergedOntologiesForReferenceMappingSets() throws Exception{
				
		for (String name : reference_mappings_map.keySet()){
			//Set merged ontology
			LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
			reference_mappings_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, reference_mappings_map.get(name).getMappingsOntology(), extractModules, signature_for_modules, classifyMergedOntologies, reasonerID));
			LogOutput.printAlways("\tUnsat: " + reference_mappings_map.get(name).getUnsatisfiableClassesSize());
		}		
				
	}
	
	
	protected  void createMergedOntologiesForSystemMappingSets() throws Exception{
			
		for (String name : system_results_map.keySet()){
			//Set merged ontology
			LogOutput.printAlways("Creating merged ontology and reasoning for mappings: " + name);
			system_results_map.get(name).setAlignedOntology(new MergedOntology(onto1, onto2, system_results_map.get(name).getMappingsOntology(), extractModules, signature_for_modules, classifyMergedOntologies, reasonerID));
			LogOutput.printAlways("\tUnsat: " + system_results_map.get(name).getUnsatisfiableClassesSize());
		}
		
	}
	
	
	protected void computeMeasures(){
		
		//Extract P, R and F
		for (String tool_name : system_results_map.navigableKeySet()){
			
			SystemMappings system = system_results_map.get(tool_name);
			
			for (String reference_name : reference_mappings_map.navigableKeySet()){
				
				ReferenceMappings reference = reference_mappings_map.get(reference_name);
				
				system.addResult(reference_name, new Results(tool_name, reference_name));
				
				
				//Check if unsat: this will affect precision and/or recall
				
				//Compute semantic measures
				/*LogOutput.printAlways("Computing semantic measures for " + tool_name + " against " + reference_name);
				SemanticMeasures.computeSemanticMeasures(
						reference.getOWLReasonerMergedOntology(), 
						system.getOWLReasonerMergedOntology(), 
						reference.getMappingSet(), system.getMappingSet());
				
				
				//Store results
				system.getResults().get(reference_name).
						setSemanticMeasures(SemanticMeasures.getSemanticPrecision(), SemanticMeasures.getSemanticRecall(), SemanticMeasures.getSemanticFscore());
				*/
				
				//Compute standards measures
				LogOutput.printAlways("Computing standard measures for " + tool_name + " against " + reference_name);
				StandardMeasures.computeStandardMeasures(
						system.getHashAlignment(), 
						reference.getHashAlignment());
				
				//Store results
				system.getResults().get(reference_name).
						setStandardMeasures(StandardMeasures.getPrecision(), StandardMeasures.getRecall(), StandardMeasures.getFscore());
												
			}
		}
	}
	
	
	
	/**
	 * 
	 */
	protected void printResults() {

		int i=0;
		
		for (String tool_name : system_results_map.navigableKeySet()){
					
			SystemMappings system = system_results_map.get(tool_name);
		
			if (i==0){
				System.out.println(system.getHeaderForResults());
				i++;
			}			
			
			System.out.println(system.toString());
		}
		
	}
	
	
	
	
	protected void printHTML() {
		
		TreeSet<SystemMappings> orderedSystemMappings = 
				new TreeSet<SystemMappings>(new SystemMappingsComparator());
		
		orderedSystemMappings.addAll(system_results_map.values());
		
		
		
		//HEADER
		System.out.println("<table cellpadding=\"4\" cellspacing=\"0\">");
		System.out.println("");
		System.out.println("<tr class=\"header\">");
		System.out.println("<td  class=\"header\" rowspan=\"2\"> System </td>");
		System.out.println("<td  class=\"header\" rowspan=\"2\" colspan=\"1\"> Time (s) </td>");
		System.out.println("<td  class=\"header\" rowspan=\"2\" colspan=\"1\"> # Mappings </td> </td>");
		System.out.println("<td  class=\"header\" rowspan=\"2\" colspan=\"1\"> # Unique </td> </td>");
		System.out.println("<td  class=\"header\" colspan=\"3\"> Scores </td>");
		System.out.println("<td  class=\"header\" colspan=\"2\"> Incoherence Analysis</td>");
		System.out.println("</tr>");


		System.out.println("<tr class=\"header\">");
		System.out.println("<td  class=\"header\"> Precision </td><td  class=\"header\"> &nbsp;Recall&nbsp; </td> <td  class=\"header\"> F-measure </td>"); 
		System.out.println("<td  class=\"header\"> Unsat.</td> <td  class=\"header\"> Degree </td>"); 
		System.out.println("</tr>");
		System.out.println("");
		
		
		
		
		
		Iterator<SystemMappings> it = orderedSystemMappings.iterator();
		SystemMappings object;
		
		//odd or even
		String type_line;
		int i=1;
		
		String prefix;
		
		while (it.hasNext()){
			
			object = it.next();
			
			
			if ( i % 2 == 0 ) { type_line="even"; } else { type_line="odd"; }
			
			System.out.println("<tr class=\""+ type_line + "\">");
			
			System.out.println("<td class=\"text\">"+object.getName()+"</td>");
			
			//http://docs.oracle.com/javase/tutorial/java/data/numberformat.html
			System.out.format("<td> %,d </td>", object.getComputationTime());
			if (object.getMappingsSize()>0)
				System.out.format("<td> %,d </td>%n", object.getMappingsSize());
			else
				System.out.println("<td> - </td>");
			
			
			//if (object.getUniqueMappingsSize()>0)
				System.out.format("<td> %,d </td>%n", object.getUniqueMappingsSize());
			//else
			//	System.out.println("<td> - </td>");
			
			
			
			for (String key : object.getResults().keySet()) {
				System.out.format("<td> %.3f </td> ", object.getResults().get(key).getPrecision());
				System.out.format("<td> %.3f </td> ", object.getResults().get(key).getRecall());
				System.out.format("<td class=\"bold\"> %.3f </td>%n", object.getResults().get(key).getFscore());
			}
			
			//System.out.println("<td>" + object.unsat + "</td> ");
			
			prefix = "";
			if (!object.isReasonerComplete()){
				prefix = "&ge;";
			}
			
			
			if (object.getUnsatisfiableClassesSize()>=0){
				System.out.format("<td> "+prefix+"%,d </td>", object.getUnsatisfiableClassesSize());
				//System.out.println("<td class=\"right\">" + object.degreee + "&#37</td> ");
				
				if ((object.getUnsatisfiableClassesDegree()==0.0))
					System.out.format("<td class=\"right\"> " + prefix + "%.0f&#37</td>", object.getUnsatisfiableClassesDegree());
				else if ((object.getUnsatisfiableClassesDegree()*100)<0.1)
					
					System.out.format("<td class=\"right\"> " + prefix + "%.3f&#37</td>", object.getUnsatisfiableClassesDegree()*100);
				else
					System.out.format("<td class=\"right\"> " + prefix + "%.1f&#37</td>", object.getUnsatisfiableClassesDegree()*100);
			}
			else{
				System.out.print("<td> - </td>");
				System.out.println("<td> - </td>");
			}
			System.out.println("</tr>");
			
			System.out.println("\n");
			
			i++;
		
		}
		
		//Closing table
		System.out.println("");
		System.out.println("<caption><b>Table X:</b> Results for the largebio task X.</caption>");
		System.out.println("</table>");
		
		
	}
	
	
	protected void printLatex() {
		
		
		TreeSet<SystemMappings> orderedSystemMappings = 
				new TreeSet<SystemMappings>(new SystemMappingsComparator());
		
		orderedSystemMappings.addAll(system_results_map.values());
		
		
		
		//Print header
		
		
		
		Iterator<SystemMappings> it = orderedSystemMappings.iterator();
		SystemMappings object;
		
		
		String prefix;
		
		
		
		while (it.hasNext()){
			
			
			object = it.next();
			
			System.out.println("{\\small\\sf " + object.getName() + " }");
			
			
			//http://docs.oracle.com/javase/tutorial/java/data/numberformat.html
			
			System.out.format("& %,d", object.getComputationTime());
			
			if (object.getMappingsSize()>0)
				System.out.format("& %,d %n", object.getMappingsSize());
			else
				System.out.println("& - ");
			
			
			System.out.format("& %,d %n", object.getUniqueMappingsSize());
			
			
			for (String key : object.getResults().keySet()) {
				System.out.format("& %.2f", object.getResults().get(key).getPrecision());
				System.out.format("& %.2f", object.getResults().get(key).getFscore());
				System.out.format("& %.2f%n", object.getResults().get(key).getRecall());
			}
						
			prefix = "";
			if (!object.isReasonerComplete()){
				prefix = "$\\geq$";
			}
			
			
			if (object.getUnsatisfiableClassesSize()>=0){
				System.out.format("& "+prefix+"%,d", object.getUnsatisfiableClassesSize());

				if (object.getUnsatisfiableClassesDegree()==0.0){
					
					System.out.println("& " + prefix + object.getUnsatisfiableClassesDegree() + "\\%");
//					System.out.format("& " + prefix + "%.0f", object.degreee);
//					System.out.print("\\%");
				}
				else if ((object.getUnsatisfiableClassesDegree()*100)<0.1){
					//System.out.println("& " + prefix + object.degreee + "\\%");
					System.out.format("& " + prefix + "%.3f", object.getUnsatisfiableClassesDegree()*100);
					System.out.print("\\%");
				}
				else{
					System.out.format("& " + prefix + "%.1f", object.getUnsatisfiableClassesDegree()*100);
					System.out.print("\\%");
				}
				
				
				
				
			}
			else{//for tests
				System.out.print("& - ");
				System.out.println("& - ");
			}
			
			System.out.println("\\\\");
			System.out.println("\n");
			
			
		}
		
		
		//Print ctable closing
		
		
	}
	
	
	
	private class SystemMappingsComparator implements Comparator<SystemMappings> {

		@Override
		public int compare(SystemMappings s1, SystemMappings s2) {
			
			if (s1.getMainFscore() < s2.getMainFscore()){
				return 1;
			}
			else if (s1.getMainFscore() == s2.getMainFscore()){
				
				//if (r1.precision < r2.precision){ //do it with unsat
				if (s1.getUnsatisfiableClassesSize() > s2.getUnsatisfiableClassesSize()){
					return 1;						
				}
				else{
					return -1;
				}
				
			}
			else{
				return -1;
			}
		}
	
	
	}	
	
	
	
	
}
