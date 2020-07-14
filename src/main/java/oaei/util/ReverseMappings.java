package oaei.util;

import java.util.Set;

import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

public class ReverseMappings {
	
	public ReverseMappings(String file_in, String file_out, String uri_onto1, String uri_onto2) throws Exception {	
		
		MappingsReaderManager mappingReaderTool = new MappingsReaderManager(file_in, MappingsReaderManager.OAEIFormat);
		
		System.out.println(mappingReaderTool.getMappingObjects().size());
		
		
		storeReversedmappings(
				reverse(mappingReaderTool.getMappingObjects()), 
				file_out, 
				uri_onto1, 
				uri_onto2);
		
		
		
	}
	
	private void storeReversedmappings(Set<MappingObjectStr> mappingObjects, String file_out, String uri_onto1, String uri_onto2) throws Exception {
		
		OutPutFilesManager output_manager = new OutPutFilesManager();
		
		output_manager.createOutFiles(
				file_out, 
				OutPutFilesManager.OAEIFormat,
				uri_onto1, 
				uri_onto2);
		
		
		//TODO
		//output_manager.addClassMapping2Files(iri_str1, iri_str2, dir_mapping, conf);
		for (MappingObjectStr mapping : mappingObjects) {
			
			//All as classes
			//if (mapping.isClassMapping())
				output_manager.addClassMapping2Files(
						mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getMappingDirection(), mapping.getConfidence());
		/*	else if (mapping.isDataPropertyMapping())
				output_manager.addDataPropMapping2Files(
						mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getMappingDirection(), mapping.getConfidence());
			else if (mapping.isObjectPropertyMapping())
				output_manager.addObjPropMapping2Files(
						mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getMappingDirection(), mapping.getConfidence());
			else if (mapping.isInstanceMapping())
				output_manager.addInstanceMapping2Files(
						mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getConfidence());
			*/
			
		}
		
		
		
		output_manager.closeAndSaveFiles();
		
	}
	
	
	
	
	/**
	 * Assumes all equivalences		
	 * @param mappingObjects
	 * @return
	 */
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
		
		System.out.println(mappingObjects.size());
		
		return mappingObjects;
	}
	
	
	
	public static void main (String[] args) {
		
		
		try {
			

			String path;
			
			String file_in = "/home/ernesto/Documents/BioPortal/PAXO/phase4results/output/task20/PAXO_Strict-mesh-efo.rdf";
			String file_out = "/home/ernesto/Documents/BioPortal/PAXO/phase4results/output/task20/PAXO_Strict-efo-mesh";
			file_in = "/home/ernesto/Documents/BioPortal/PAXO/phase4results/output/task19/PAXO_Relaxed-mesh-efo.rdf";
			file_out = "/home/ernesto/Documents/BioPortal/PAXO/phase4results/output/task19/PAXO_Relaxed-efo-mesh";
			String onto1 = "http://purl.obolibrary.org/mesh.owl";
			String onto2 = "http://purl.obolibrary.org/efo.owl";
			
			
			String ero = "http://purl.obolibrary.org/ero.owl";
			String ncit	= "http://purl.obolibrary.org/ncit.owl";
			
			

			String afo = "http://purl.obolibrary.org/obo/afo.owl";
			String chmo	= "http://purl.obolibrary.org/obo/chmo.owl";
			
		
			
			
			
			
			String mp = "http://purl.obolibrary.org/mp.owl";
			String hp = "http://purl.obolibrary.org/hp.owl";
			
			String ordo = "http://purl.obolibrary.org/ordo.owl";
			String doid = "http://purl.obolibrary.org/doid.owl";
			
			/*
			
			path = "/home/ernesto/Documents/BioPortal/PAXO/phase3_part1/O8/";
			file_in = path + "calculated_output_mp_hp.rdf";
			file_out = path + "PAXO_Strict-hp-mp";
			onto1 = mp;
			onto2 = hp;
			
			
			
			file_in = path + "calculated_output_ordo_doid.rdf";
			file_out = path + "PAXO_Strict-doid-ordo";
			onto1 = ordo;
			onto2 = doid;
			
			file_in = path + "calculated_output_ordo_hp.rdf";
			file_out = path + "PAXO_Strict-hp-ordo";
			onto1 = ordo;
			onto2 = hp;
			
			
			file_in = path + "calculated_output_ordo_mp.rdf";
			file_out = path + "PAXO_Strict-mp-ordo";
			onto1 = ordo;
			onto2 = mp;
			
			
			
			path = "/home/ernesto/Documents/BioPortal/PAXO/phase3_part1/O_relaxed/";
			file_in = path + "calculated_output_mp_hp.rdf";
			file_out = path + "PAXO_Relaxed-hp-mp";
			onto1 = mp;
			onto2 = hp;
			
			
			
			file_in = path + "calculated_output_ordo_doid.rdf";
			file_out = path + "PAXO_Relaxed-doid-ordo";
			onto1 = ordo;
			onto2 = doid;
			
			file_in = path + "calculated_output_ordo_hp.rdf";
			file_out = path + "PAXO_Relaxed-hp-ordo";
			onto1 = ordo;
			onto2 = hp;
			
			
			file_in = path + "calculated_output_ordo_mp.rdf";
			file_out = path + "PAXO_Relaxed-mp-ordo";
			onto1 = ordo;
			onto2 = mp;
			
			*/
			
			
			path = "/home/ernesto/Desktop/Pomap/";
			
			file_in = path + "POMAP++-doid-ordo-2018.rdf";
			file_out = path + "POMAP++-doid-ordo-2018";
			onto1 = doid;
			onto2 =ordo;

			
			file_in = path + "POMAP++-doid-ordo-2019.rdf";
			file_out = path + "POMAP++-doid-ordo-2019";
			onto1 = doid;
			onto2 =ordo;
			
			file_in = path + "POMAP-hp-doid_noimports.rdf";
			file_out = path + "POMAP-hp-doid_noimports";
			onto1 = hp;
			onto2 =doid;
			
			file_in = path + "POMAP++-hp-mp-2018.rdf";
			file_out = path + "POMAP++-hp-mp-2018";
			onto1 = hp;
			onto2 =mp;
			
			
			file_in = path + "POMAP++-hp-mp-2019.rdf";
			file_out = path + "POMAP++-hp-mp-2019";
			onto1 = hp;
			onto2 =mp;
			
			
			file_in = path + "POMAP-hp-ordo_noimports.rdf";
			file_out = path + "POMAP-hp-ordo_noimports";
			onto1 = hp;
			onto2 =ordo;
			
			
			file_in = path + "POMAP-mp-doid_noimports.rdf";
			file_out = path + "POMAP-mp-doid_noimports";
			onto1 = mp;
			onto2 =doid;
			
			
			file_in = path + "POMAP-mp-ordo_noimports.rdf";
			file_out = path + "POMAP-mp-ordo_noimports";
			onto1 = mp;	
			onto2 =ordo;
			
			
			file_in = path + "POMAP++-afo-chmo-2019.rdf";
			file_out = path + "POMAP++-afo-chmo-2019";
			onto1 = afo;
			onto2 =chmo;
			
			
			
			//new ReverseMappings(file_in, file_out, onto2, onto1);
			new ReverseMappings(file_in, file_out, onto1, onto2);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
