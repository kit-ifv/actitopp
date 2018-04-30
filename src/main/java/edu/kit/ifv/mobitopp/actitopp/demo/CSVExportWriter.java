package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import edu.kit.ifv.mobitopp.actitopp.*;


public class CSVExportWriter 
{
	
	FileWriter writer;
	
	/**
	 * 
	 * Konstruktor
	 *
	 * @param filename
	 * @throws IOException
	 */
	public CSVExportWriter(String filename) throws IOException
	{
		writer = new FileWriter(filename);
	}
	

	/**
	 * 
	 * Export der Wegedaten der Personen
	 * 
	 * @param personmap
	 * @throws IOException
	 */
	public void exportTripData(HashMap<Number,ActitoppPerson> personmap) throws IOException
	{
						  	
	  	// Header
	  	writer.append("ID;WOTAG;anzeit;anzeit_woche;abzeit;abzeit_woche;Dauer;zweck_text;jointStatus");
	  	writer.append('\n');
	  	writer.flush();

	  	// Durchlaufe alle Personen
	  	for (Number key : personmap.keySet())
			{
				ActitoppPerson actperson = personmap.get(key);
	  		
	  		// Weginformation aus Aktivitätsdaten herauslesen
	  		for (HActivity act : actperson.getWeekPattern().getAllActivities())
	  		{
	  			if (act.tripBeforeActivityisScheduled() && act.getEstimatedTripTime()>0)
	  			{
	    			writer.append(writeTripBeforeActivity(act));
	    			writer.flush();
	  			}
	  			if (act.tripAfterActivityisScheduled() && act.getEstimatedTripTimeAfterActivity()>0)
	  			{
	    			writer.append(writeTripAfterActivity(act));
	    			writer.flush();
	  			}
	  		}
	  	}
	  	writer.close();
	}
	
	
	/**
	 * 
	 * Export der Aktivitätsdaten der Personen
	 * 
	 * @param personmap
	 * @throws IOException
	 */
	public void exportActivityData(HashMap<Number,ActitoppPerson> personmap) throws IOException
	{
						  	
	  	// Header
	  	writer.append("ID;WOTAG;startzeit;startzeit_woche;endzeit;endzeit_woche;Dauer;zweck;jointStatus");
	  	writer.append('\n');
	  	writer.flush();

	  	// Durchlaufe alle Personen
	  	for (Number key : personmap.keySet())
			{
				ActitoppPerson actperson = personmap.get(key);
	  		
	  		// Füge alle Aktivitäten hinzu
	  		for (HActivity act : actperson.getWeekPattern().getAllActivities())
	  		{
	  			if (act.isScheduled())
	  			{
	    			writer.append(writeActivity(act));
	    			writer.flush();
	  			}
	  		}
	  	}
	  	writer.close();
	}
	
	
	/**
	 * 
	 * Schreibe Zeile mit Aktivitäteninfos
	 * 
	 * @param act
	 * @return
	 */
	public String writeActivity(HActivity act)
	{
		
		assert act.isScheduled():"Activity is not fully scheduled";
		
		String rueckgabe="";
		
		// ID
		rueckgabe += act.getPerson().getPersIndex() + ";";
		// WOTAG
		rueckgabe += act.getWeekDay() + ";";
		// Startzeit
		rueckgabe += act.getStartTime() + ";";
		// Startzeit_woche
		rueckgabe += act.getStartTimeWeekContext() + ";"; 
		// Endzeit
		rueckgabe += act.getEndTime() + ";";		
		// Endzeit_woche
		rueckgabe += act.getEndTimeWeekContext() + ";";  
		// Dauer
		rueckgabe += act.getDuration() + ";";
		// Zweck
		rueckgabe += act.getType() + ";";
		// joint Status
		rueckgabe += act.getJointStatus() + "";
		
		rueckgabe +="\n";
		return rueckgabe;		
	}
	
	/**
	 * 
	 * Schreibe Zeile mit Weginfos (vor der Aktivität)
	 * 
	 * @param act
	 * @return
	 */
	public String writeTripBeforeActivity(HActivity act)
	{
		
		assert act.tripBeforeActivityisScheduled(): "Trip is not scheduled!";
	
		String rueckgabe="";
		
		// ID
		rueckgabe += act.getPerson().getPersIndex() + ";";
		// WOTAG
		rueckgabe += act.getWeekDay() + ";";
		//anzeit
		rueckgabe += act.getStartTime() + ";";
		// anzeit_woche
		rueckgabe += act.getStartTimeWeekContext() + ";"; 
		// abzeit
		rueckgabe += act.getTripStartTime() + ";";		
		// abzeit_woche
		rueckgabe += act.getTripStartTimeWeekContext() + ";";  
		// Dauer
		rueckgabe += act.getEstimatedTripTime() + ";";
		// Zweck
		rueckgabe += act.getType() + ";";
		// jointStatus
		rueckgabe += act.getJointStatus() + "";
		
		rueckgabe +="\n";
		return rueckgabe;		
	}
	
	/**
	 * 
	 * Schreibe Zeile mit Weginfos (nach der Aktivität)
	 * 
	 * @param act
	 * @return
	 */
	public String writeTripAfterActivity(HActivity act)
	{
		
		assert act.tripAfterActivityisScheduled(): "Trip is not scheduled!";
				
		String rueckgabe="";
		
		// ID
		rueckgabe += act.getPerson().getPersIndex() + ";";
		// WOTAG
		rueckgabe += act.getWeekDay() + ";";
		//anzeit
		rueckgabe += act.getTripStartTimeAfterActivity() + act.getEstimatedTripTimeAfterActivity() + ";";
		// anzeit_woche
		rueckgabe += act.getTripStartTimeAfterActivityWeekContext() + act.getEstimatedTripTimeAfterActivity() + ";";
		// abzeit
		rueckgabe += act.getEndTime() + ";";		
		// abzeit_woche
		rueckgabe += act.getEndTimeWeekContext() + ";";  
		// Dauer
		rueckgabe += act.getEstimatedTripTimeAfterActivity() + ";";
		// Zweck ist hier immer HOME, da letzter Weg in Tour nach Hause
		rueckgabe += "H" + ";";
		// jointStatus - aktuell immer 4 (keine Gemeinsamkeit, da Gemeinsamkeiten bei NachHause Wegen noch nicht modelliert werden!
		rueckgabe += "4" + "";
		
		rueckgabe +="\n";
		return rueckgabe;		
	}
	
}
