
package edu.kit.ifv.mobitopp.actitopp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @author Tim Hilgert
 * 
 * Diese Klasse ermöglicht die Benutzung von Debug-Loggern.
 * Diese Logger speichern im Modell getroffene Entscheidungen in Maps.
 * Können verwendet werden, um explizit Entscheidungen von verschiedenen Stufen zu evaluieren
 *
 */
public class DebugLoggers 
{

	/*
	 * Übergeordnete Map mit allen Loggern
	 */
	public HashMap<String,LinkedHashMap<Object,String>> debugloggers = null;
	
	
	/**
	 * 
	 * Konstruktor
	 *
	 */
	public DebugLoggers()
	{	
		debugloggers = 	new HashMap<String,LinkedHashMap<Object,String>>();
	}
	

	/**
	 * Methode zum Erzeugen eines neuen Loggers
	 * 
	 * @param key
	 */
	public void addDebugLogger(String key)
	{
		debugloggers.put(key, new LinkedHashMap<Object, String>());
	}
	
	
	/**
	 * Methode zum Prüfen, ob Logger existiert
	 * 
	 * @param key
	 * @return
	 */
	public boolean existsLogger (String key)
	{
		boolean result=false;
		if(debugloggers.containsKey(key)) result=true;
		return result;
	}
	
	/**
	 * Methode gibt referenzierten Loggers zurück
	 * 
	 * @param key
	 * @return
	 */
	public LinkedHashMap<Object, String> getLogger (String key)
	{
		return debugloggers.get(key);
	}
	
	/**
	 * Methode zum Hinzufügen von Informationen zu einem Logger
	 * 
	 * @param key
	 * @param referenceobject
	 * @param decision
	 */
	public void addDebugInfo(String key, Object referenceobject, String decision)
	{
		debugloggers.get(key).put(referenceobject, decision);
	}


	/**
	 * Export aller verfügbaren Logger
	 * 
	 * @param basepath
	 */
	public void exportallLoggerInfos(String basepath)
	{
		for (String key : debugloggers.keySet())
		{
			exportLoggerInfos(key, basepath + "/Logger_" + key + ".csv");
		}
	}


	/**
	 * Methode zum Export der Ergebnisse eines Loggers
	 * 
	 * @param key
	 * @param filename
	 */
	public void exportLoggerInfos(String key, String filename)
	{
		
		try 
		{
			/*
			 * FileWriter erzeugen
			 */
			FileWriter writer = new FileWriter(filename);
			
			boolean headergeschrieben=false;
				
			/*
			 * Alle Objekte eines DebugLoggers durchgehen
			 */
			LinkedHashMap<Object, String> relevantmap = debugloggers.get(key);
			
			for(Object referenceobject : relevantmap.keySet())
	    {
				String rowcontent ="";
				
				// HouseholdLogger
				if (referenceobject instanceof ActiToppHousehold)
				{
					ActiToppHousehold acthousehold = ((ActiToppHousehold) referenceobject);
					
					if(!headergeschrieben)
					{
						// Header
				  	writer.append("HHIndex;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	headergeschrieben=true;
					}
	
					// HHIndex
					rowcontent += acthousehold.getHouseholdIndex() + ";";		
					//Decision
					rowcontent += relevantmap.get(referenceobject);					
				}
				
				// PersonLogger
				if (referenceobject instanceof ActitoppPerson)
				{
					ActitoppPerson actperson = ((ActitoppPerson) referenceobject);
					
					if(!headergeschrieben)
					{
						// Header
				  	writer.append("HHIndex;PersIndex;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	headergeschrieben=true;
					}
	
					// HHIndex
					rowcontent += actperson.getHousehold().getHouseholdIndex() + ";";		
					// PersIndex
					rowcontent += actperson.getPersIndex() + ";";
					//Decision
					rowcontent += relevantmap.get(referenceobject);					
				}
				
				// Daylogger
				if (referenceobject instanceof HDay)
				{
					HDay actday = ((HDay) referenceobject);
					
					if(!headergeschrieben)
					{
						// Header
				  	writer.append("HHIndex;PersIndex;WOTAG;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	headergeschrieben=true;
					}
					
					// HHIndex
					rowcontent += actday.getPerson().getHousehold().getHouseholdIndex() + ";";		
					// PersIndex
					rowcontent += actday.getPerson().getPersIndex() + ";";
					// WOTAG
					rowcontent += actday.getWeekday() + ";";
					//Decision
					rowcontent += relevantmap.get(referenceobject);					
				}
				
				// Tourlogger
				if (referenceobject instanceof HTour)
				{
					HTour acttour = ((HTour) referenceobject);
					
					if(!headergeschrieben)
					{
						// Header
				  	writer.append("HHIndex;PersIndex;WOTAG;TourIndex;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	headergeschrieben=true;
					}
					
					// HHIndex
					rowcontent += acttour.getPerson().getHousehold().getHouseholdIndex() + ";";		
					// PersIndex
					rowcontent += acttour.getPerson().getPersIndex() + ";";
					// WOTAG
					rowcontent += acttour.getDay().getWeekday() + ";";
					// TourIndex
					rowcontent += acttour.getIndex() + ";";
					//Decision
					rowcontent += relevantmap.get(referenceobject);					
				}
				
				// Activitylogger
				if (referenceobject instanceof HActivity)
				{
					HActivity actact = ((HActivity) referenceobject);
					
					if(!headergeschrieben)
					{
						// Header
				  	writer.append("HHIndex;PersIndex;WOTAG;TourIndex;AktIndex;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	headergeschrieben=true;
					}
					
					// HHIndex
					rowcontent += actact.getPerson().getHousehold().getHouseholdIndex() + ";";		
					// PersIndex
					rowcontent += actact.getPerson().getPersIndex() + ";";
					// WOTAG
					rowcontent += actact.getDay().getWeekday() + ";";
					// TourIndex
					rowcontent += actact.getTourIndex() + ";";
					// AktIndex
					rowcontent += actact.getIndex() + ";";
					//Decision
					rowcontent += relevantmap.get(referenceobject);					
				}				
				
				// Zeile schreiben
				
				rowcontent +="\n";
				
				writer.append(rowcontent);
				writer.flush();
				
	    }
	
	  	writer.close();
		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}

	/**
	 * 
	 * Entfernt alle Logging Ergebnisse für einen Haushalt
	 * Ist notwendig, falls der Haushalt beispielsweise erneut modelliert wird
	 * 
	 * @param tmphousehold
	 */
	public void deleteInformationforHousehold(ActiToppHousehold tmphousehold)
	{
		int hhindex = tmphousehold.getHouseholdIndex();
		for (String key : debugloggers.keySet())
		{
			/*
			 * Alle Objekte eines DebugLoggers durchgehen
			 */
			LinkedHashMap<Object, String> relevantmap = debugloggers.get(key);
			
			for(Iterator<Object> it = relevantmap.keySet().iterator(); it.hasNext();)
	    {			
				Object referenceobject = it.next();	
				// HouseholdLogger
				if (referenceobject instanceof ActiToppHousehold)
				{
					ActiToppHousehold acthousehold = ((ActiToppHousehold) referenceobject);
					if (acthousehold.getHouseholdIndex()==hhindex) it.remove();			
				}
				
				// PersonLogger
				if (referenceobject instanceof ActitoppPerson)
				{
					ActitoppPerson actperson = ((ActitoppPerson) referenceobject);
					if (actperson.getHousehold().getHouseholdIndex()==hhindex) it.remove();
				}
				
				// Daylogger
				if (referenceobject instanceof HDay)
				{
					HDay actday = ((HDay) referenceobject);
					if (actday.getPerson().getHousehold().getHouseholdIndex()==hhindex) it.remove();						
				}
				
				// Tourlogger
				if (referenceobject instanceof HTour)
				{
					HTour acttour = ((HTour) referenceobject);
					if (acttour.getPerson().getHousehold().getHouseholdIndex()==hhindex) it.remove();
				}
				
				// Activitylogger
				if (referenceobject instanceof HActivity)
				{
					HActivity actact = ((HActivity) referenceobject);
					if (actact.getPerson().getHousehold().getHouseholdIndex()==hhindex) it.remove();	
				}				
	    }
		}
	}
	
	/**
	 * 
	 * Entfernt alle Logging Ergebnisse für eine Person
	 * Ist notwendig, falls die Person beispielsweise erneut modelliert wird
	 * 
	 * @param tmpperson
	 */
	public void deleteInformationforPerson(ActitoppPerson tmpperson)
	{
		int persindex = tmpperson.getPersIndex();
		for (String key : debugloggers.keySet())
		{
			/*
			 * Alle Objekte eines DebugLoggers durchgehen
			 */
			LinkedHashMap<Object, String> relevantmap = debugloggers.get(key);
			
			for(Iterator<Object> it = relevantmap.keySet().iterator(); it.hasNext();)
	    {			
				Object referenceobject = it.next();
				
				// PersonLogger
				if (referenceobject instanceof ActitoppPerson)
				{
					ActitoppPerson actperson = ((ActitoppPerson) referenceobject);
					if (actperson.getPersIndex()==persindex) it.remove();			
				}
				
				// Daylogger
				if (referenceobject instanceof HDay)
				{
					HDay actday = ((HDay) referenceobject);
					if (actday.getPerson().getPersIndex()==persindex) it.remove();						
				}
				
				// Tourlogger
				if (referenceobject instanceof HTour)
				{
					HTour acttour = ((HTour) referenceobject);
					if (acttour.getPerson().getPersIndex()==persindex) it.remove();				
				}
				
				// Activitylogger
				if (referenceobject instanceof HActivity)
				{
					HActivity actact = ((HActivity) referenceobject);
					if (actact.getPerson().getPersIndex()==persindex) it.remove();				
				}				
	    }
		}
	}
	
}
