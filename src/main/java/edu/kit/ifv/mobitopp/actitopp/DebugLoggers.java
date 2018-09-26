
package edu.kit.ifv.mobitopp.actitopp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * @author Tim Hilgert
 * 
 * debugloggers store modeling decision in maps.
 * debug loggers may be used to explictly monitor specific decision during modeling that may not be directly 
 * visible when only analysing overall modeling result (i.e. activity schedules).
 *
 */
public class DebugLoggers 
{

	/*
	 * Overall HashMap including all generated loggers
	 */
	public HashMap<String,LinkedHashMap<Object,String>> debugloggers = null;
	
	
	/**
	 * constructor to create debug loggers object
	 */
	public DebugLoggers()
	{	
		debugloggers = 	new HashMap<String,LinkedHashMap<Object,String>>();
	}
	
	
	/**
	 * 
	 * constructor to create a subordinate logger (storing information for one household only)
	 * create a logger object and created empty loggers for all elements that are already in the superordinate logger object
	 *
	 * @param overallLogger
	 */
	public DebugLoggers(DebugLoggers overallLogger)
	{
		this();
		for (String s : overallLogger.debugloggers.keySet())
		{
			this.addDebugLogger(s);
		}
	}
	

	/**
	 * add a new logger element
	 * 
	 * @param key
	 */
	public void addDebugLogger(String key)
	{
		debugloggers.put(key, new LinkedHashMap<Object, String>());
	}
	
	
	/**
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
	 * 
	 * @param key
	 * @return
	 */
	public LinkedHashMap<Object, String> getLogger (String key)
	{
		return debugloggers.get(key);
	}
	
	/**
	 * add information to a logger
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
	 * add all information of a subordinate logger to the superordinate logger
	 * 
	 * @param householdlogger
	 */
	public void addHouseholdDebugInfotoOverallLogger(DebugLoggers householdlogger)
	{
		for (Entry<String,LinkedHashMap<Object,String>> entry : householdlogger.debugloggers.entrySet())
		{
			String loggerid = entry.getKey();
			LinkedHashMap<Object,String> logger = entry.getValue();
			
			getLogger(loggerid).putAll(logger);
		}
	}
	

	/**
	 * export of all logger elements to the file system
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
	 * export of a single logger element to the file system
	 * 
	 * @param key
	 * @param filename
	 */
	public void exportLoggerInfos(String key, String filename)
	{
		
		try 
		{
			FileWriter writer = new FileWriter(filename);
			
			boolean wroteheader=false;
				
			/*
			 * Loop through all entries of the logger element
			 */
			LinkedHashMap<Object, String> relevantmap = debugloggers.get(key);
			
			for(Object referenceobject : relevantmap.keySet())
	    {
				String rowcontent ="";
				
				// HouseholdLogger
				if (referenceobject instanceof ActiToppHousehold)
				{
					ActiToppHousehold acthousehold = ((ActiToppHousehold) referenceobject);
					
					if(!wroteheader)
					{
						// Header
				  	writer.append("HHIndex;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	wroteheader=true;
					}
	
					// HHIndex
					rowcontent += acthousehold.getHouseholdIndex() + ";";		
					// Decision
					rowcontent += relevantmap.get(referenceobject);					
				}
				
				// PersonLogger
				if (referenceobject instanceof ActitoppPerson)
				{
					ActitoppPerson actperson = ((ActitoppPerson) referenceobject);
					
					if(!wroteheader)
					{
						// Header
				  	writer.append("HHIndex;PersIndex;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	wroteheader=true;
					}
	
					// HHIndex
					rowcontent += actperson.getHousehold().getHouseholdIndex() + ";";		
					// PersIndex
					rowcontent += actperson.getPersIndex() + ";";
					// Decision
					rowcontent += relevantmap.get(referenceobject);					
				}
				
				// Daylogger
				if (referenceobject instanceof HDay)
				{
					HDay actday = ((HDay) referenceobject);
					
					if(!wroteheader)
					{
						// Header
				  	writer.append("HHIndex;PersIndex;WOTAG;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	wroteheader=true;
					}
					
					// HHIndex
					rowcontent += actday.getPerson().getHousehold().getHouseholdIndex() + ";";		
					// PersIndex
					rowcontent += actday.getPerson().getPersIndex() + ";";
					// WOTAG - WeekDay
					rowcontent += actday.getWeekday() + ";";
					// Decision
					rowcontent += relevantmap.get(referenceobject);					
				}
				
				// Tourlogger
				if (referenceobject instanceof HTour)
				{
					HTour acttour = ((HTour) referenceobject);
					
					if(!wroteheader)
					{
						// Header
				  	writer.append("HHIndex;PersIndex;WOTAG;TourIndex;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	wroteheader=true;
					}
					
					// HHIndex
					rowcontent += acttour.getPerson().getHousehold().getHouseholdIndex() + ";";		
					// PersIndex
					rowcontent += acttour.getPerson().getPersIndex() + ";";
					// WOTAG
					rowcontent += acttour.getDay().getWeekday() + ";";
					// TourIndex
					rowcontent += acttour.getIndex() + ";";
					// Decision
					rowcontent += relevantmap.get(referenceobject);					
				}
				
				// Activitylogger
				if (referenceobject instanceof HActivity)
				{
					HActivity actact = ((HActivity) referenceobject);
					
					if(!wroteheader)
					{
						// Header
				  	writer.append("HHIndex;PersIndex;WOTAG;TourIndex;AktIndex;Decision");
				  	writer.append('\n');
				  	writer.flush();
				  	wroteheader=true;
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
					// Decision
					rowcontent += relevantmap.get(referenceobject);					
				}				
				
				// write row
				
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
	 * delete all information of a single person from the debug logger object
	 * e.g. when a person needs to be modeled again
	 * 
	 * @param tmpperson
	 */
	public void deleteInformationforPerson(ActitoppPerson tmpperson)
	{
		int persindex = tmpperson.getPersIndex();
		for (String key : debugloggers.keySet())
		{
			/*
			 * Loop all debug logger elements
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
