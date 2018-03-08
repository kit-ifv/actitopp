package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import edu.kit.ifv.mobitopp.actitopp.*;


public class CSVActivityExportWriter 
{

	public void exportActivityData(HashMap<Number,ActitoppPerson> personmap, String filepath) throws IOException
	{
					
			// Erstelle die Datei
	  	FileWriter writer = new FileWriter(filepath);
	  	
	  	// Header
	  	writer.append("ID;Jahr;PersNr;BERTAG;WOTAG;anzeit;anzeit_woche;abzeit;abzeit_woche;Dauer;zweck");
	  	writer.append('\n');
	  	writer.flush();

	  	// Durlaufe alle Personen
	  	for (Number key : personmap.keySet())
			{
				ActitoppPerson actperson = personmap.get(key);
	  		
	  		// Füge alle Aktivitäten hinzu
	  		for (HActivity act : actperson.getWeekPattern().getAllActivities())
	  		{
	  			if (act.tripBeforeActivityisScheduled())
	  			{
	    			writer.append(writeTripBeforeActivity(act));
	    			writer.flush();
	  			}
	  			if (act.tripAfterActivityisScheduled())
	  			{
	    			writer.append(writeTripAfterActivity(act));
	    			writer.flush();
	  			}
	  		}
	  	}
	  	writer.close();
	}
	
	public String writeTripBeforeActivity(HActivity act)
	{
		String rueckgabe="";
		
		// ID
		rueckgabe += act.getPerson().getPersIndex() + ";";
		// Jahr
		rueckgabe += "2016" + ";";
		// PersNr
		rueckgabe += "1" + ";";
		// BERTAG
		rueckgabe += act.getWeekDay() + ";";
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
		rueckgabe += act.getMobiToppActType() + "";
		
		rueckgabe +="\n";
		return rueckgabe;		
	}
	
	public String writeTripAfterActivity(HActivity act)
	{
		String rueckgabe="";
		
		// ID
		rueckgabe += act.getPerson().getPersIndex() + ";";
		// Jahr
		rueckgabe += "2016" + ";";
		// PersNr
		rueckgabe += "1" + ";";
		// BERTAG
		rueckgabe += act.getWeekDay() + ";";
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
		// Zweck ist hier immer HOME, also 7
		rueckgabe += "7" + "";
		
		rueckgabe +="\n";
		return rueckgabe;		
	}
	
}
