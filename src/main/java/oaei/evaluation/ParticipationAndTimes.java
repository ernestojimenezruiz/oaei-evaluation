package oaei.evaluation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import oaei.configuration.OAEIConfiguration;
import uk.ac.ox.krr.logmap2.io.ReadFile;

/**
 * We read from log to extract participation and times
 * @author ejimenez-ruiz
 *
 */
public class ParticipationAndTimes  extends AbstractEvaluation{

	
	TreeSet<ResultObjectTimes> orderedObjectsTime = 
			new TreeSet<ResultObjectTimes>(new ResultObjectComparatorTimes());
	
	
	public ParticipationAndTimes() throws Exception {
		
		//load configuration
		configuration = new OAEIConfiguration();
		
		loadObjectsTime(configuration.getLogsPath() + "times.log");
		
		
		
		printTableTimeHTML();
		
		System.out.println("\n");
		
		printTableTimeLatex();
		
		
		
	}
	
	
	
	private void loadObjectsTime(String file) throws Exception{
		
		ReadFile reader = new ReadFile(file);
		
		
		String line;
		String[] elements;
		
		line=reader.readLine();
		
		
		List<Long> task_times = new ArrayList<Long>(); 
		
		
		while (line!=null) {
			
			if (line.startsWith("#")){
				line=reader.readLine();
				continue;
			}	
			
			if (line.indexOf("|")<0 && line.indexOf("\t")<0){
				line=reader.readLine();
				continue;
			}
			
			if (line.indexOf("|")>=0)
				elements=line.split("\\|");
			else { // if (line.indexOf("\t")>=0){
				elements=line.split("\\t");
			}

			//System.out.println(elements[0]);
			
			
			for (int i=1; i< elements.length; i++){

				//System.out.println(elements[i]);

				if (Integer.valueOf(elements[i])>=0)
					task_times.add(Long.valueOf(elements[i])); ///1000
				else
					task_times.add(Long.valueOf(elements[i]));
			}
			
			//create object
			orderedObjectsTime.add(
					new ResultObjectTimes(
							elements[0], //tool
							task_times   //times
							));
			
			task_times.clear();;
			line=reader.readLine();
		}		
		
		reader.closeBuffer();
		

	}
	
	
	
	private void printTableTimeLatex(){
	
		Iterator<ResultObjectTimes> it = orderedObjectsTime.iterator();
		
		ResultObjectTimes object;
		
		int i=0;
		
				
		while (it.hasNext()){
			object = it.next();
			
		
			System.out.println("{\\small\\sf " + object.tool + " }");
			
			for (long time : object.task_times){
				
				System.out.print("& \\text{");
	
				
				if (time>0)
					System.out.format("%,d }%n", time);
				else
					System.out.println("-  }");
			}
			
			
			//System.out.println("<td class=\"header\">"+object.average_time+"</td>");
			System.out.format("& %,.0f %n", object.average_time);
			
			System.out.println("& "+object.completed);
			
			//System.out.println("\\\\\\hline");
			System.out.println("\\\\");
			System.out.println("\n");
			
			i++;
		}
		
		
		//Add summary row?
		double average_total = 0.0;
		int completed_total = 0; 
		int[] num_complete_task = new int[6];
		for (int j=0; j<num_complete_task.length; j++){
			num_complete_task[j]=0;
		}
		
		for (ResultObjectTimes objTimes : orderedObjectsTime){
			average_total += objTimes.average_time;
			completed_total += objTimes.completed;
			
			for (int j=0; j<objTimes.task_times.size(); j++){
				//num_complete_task.
				if (objTimes.task_times.get(j)>=0){
					num_complete_task[j]++;
				}
			}
		}
		
		average_total = average_total / (double) i;//orderedObjectsTime.size();
		
		System.out.println("\\hline");

		System.out.println(" \\# Systems ");
		
		//System.out.println("<td class=\"header\">"+object.average_time+"</td>");
		
		for (int j=0; j<num_complete_task.length; j++){
			System.out.format("& \\textbf{%,d }%n", num_complete_task[j]);
		}
		
		System.out.format("& \\textbf{ %,.0f }%n", average_total);
		System.out.println("& \\textbf{"+completed_total+"}");
		System.out.println("\\\\\\hline");
		System.out.println("\n");
		
		
		
		
		
		
	}
	
	
	
	
	
	
	private void printTableTimeHTML(){
	
			
		Iterator<ResultObjectTimes> it = orderedObjectsTime.iterator();
		
		ResultObjectTimes object;
		
		int i=0;
		
		String type_line;
		
		
		
		//HEADER
		System.out.println("<table cellpadding=\"4\" cellspacing=\"0\">");
		System.out.println("<tr class=\"header\">"); 
		System.out.println("<td  class=\"header\" rowspan=\"2\"  colspan=\"1\"> System </td>"); 
		System.out.println("<td  class=\"header\" colspan=\"2\"> FMA-NCI </td>");
		System.out.println("<td  class=\"header\" colspan=\"2\"> FMA-SNOMED </td>"); 
		System.out.println("<td  class=\"header\" colspan=\"2\"> SNOMED-NCI </td> "); 
		System.out.println("<td  class=\"header\" rowspan=\"2\"  colspan=\"1\"> Average </td>"); 
		System.out.println("<td  class=\"header\" rowspan=\"2\"  colspan=\"1\"> # Tasks </td>"); 
		System.out.println("</tr>");
		System.out.println("");
		System.out.println("<tr class=\"header\">"); 
		System.out.println("<td  class=\"header\"> Task 1 </td><td  class=\"header\"> Task 2 </td>"); 
		System.out.println("<td  class=\"header\"> Task 3 </td><td  class=\"header\"> Task 4 </td>"); 
		System.out.println("<td  class=\"header\"> Task 5 </td><td  class=\"header\"> Task 6 </td>"); 
		System.out.println("</tr>"); 

		
		
		
		
		
		while (it.hasNext()){
			object = it.next();
			
			i++;
			
			
			if ( i % 2 == 0 ) { type_line="even"; } else { type_line="odd"; }
			
			
			System.out.println("<tr class=\""+ type_line + "\">");
			
			
			System.out.println("<td class=\"text\">"+object.tool+"</td>");
			
			
			for (long time : object.task_times){
				
				System.out.print("<td>");
				
				if (time>0)
					System.out.format("%,d </td>%n", time);
				else
					System.out.println("-  </td>");
			}
			
			
			//System.out.println("<td class=\"header\">"+object.average_time+"</td>");
			System.out.format("<td class=\"header\"> %,.0f </td>%n", object.average_time);
			
			System.out.println("<td class=\"header\">"+object.completed+"</td>");
			
			System.out.println("</tr>");
			System.out.println("\n");
			
			
		}
		
		
		
		
		//Add summary row?
		double average_total = 0.0;
		int completed_total = 0; 
		int[] num_complete_task = new int[6];
		for (int j=0; j<num_complete_task.length; j++){
			num_complete_task[j]=0;
		}
		
		for (ResultObjectTimes objTimes : orderedObjectsTime){
			average_total += objTimes.average_time;
			completed_total += objTimes.completed;
			
			for (int j=0; j<objTimes.task_times.size(); j++){
				//num_complete_task.
				if (objTimes.task_times.get(j)>=0){
					num_complete_task[j]++;
				}
			}
		}
		
		average_total = average_total / (double) orderedObjectsTime.size();
		
		System.out.println("<tr class=\"base\">");
		System.out.println("<td class=\"header\"> # Systems </td> ");
		
		//System.out.println("<td class=\"header\">"+object.average_time+"</td>");
		
		for (int j=0; j<num_complete_task.length; j++){
			System.out.format("<td class=\"header\">%,d </td>%n", num_complete_task[j]);
		}
		
		System.out.format("<td class=\"header\"> %,.0f </td>%n", average_total);
		System.out.println("<td class=\"header\">"+completed_total+"</td>");
		System.out.println("</tr>");
		System.out.println("\n");
		
		
		
		
		System.out.println("<caption><b>Table 1:</b> System runtimes (s) and task completion.</caption>");
		System.out.println("</table>");
		
		
		
		
	}
	
	
	
	
	
	


	private class ResultObjectTimes  {
		
		public String tool;
		
		//Time for each task
		public List<Long> task_times; //Order: task 1, 2,...,6 
		public List<Long> task_times2; //Order: forst small tasks: task 1,3,5,2,4,6
		public double average_time;
		public int completed;
		
		
		ResultObjectTimes(
				String tool, 
				List<Long> times){
			
			this.tool = tool;
			task_times = new ArrayList<Long>(times);
			task_times2 = new ArrayList<Long>();
			
			completed = 0;
			average_time = 0.0;
			for (long t : task_times){
				if (t>=0){
					completed++;
					average_time += t;
				}
			}
			
			
			task_times2.add(task_times.get(0));
			task_times2.add(task_times.get(3));
			task_times2.add(task_times.get(1));
			task_times2.add(task_times.get(4));
			task_times2.add(task_times.get(2));
			task_times2.add(task_times.get(5));
			
			average_time = average_time / (double) completed;
			
			
		}
	
	
	}
	
	
	
	/**
	 * Comparator fscore
	 * @author Ernesto
	 *
	 */
	private class ResultObjectComparatorTimes implements Comparator<ResultObjectTimes> {
		
		
		/**

		 * @param m1
		 * @param m2
		 * @return
		 */
		public int compare(ResultObjectTimes r1, ResultObjectTimes r2) {

			if (r1.completed < r2.completed){
				return 1;
			}
			else if (r1.completed == r2.completed){
				
				if (r1.average_time > r2.average_time){ 
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
	
	
	
	
	public static void main(String[] arags) {
		try {
			new ParticipationAndTimes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
