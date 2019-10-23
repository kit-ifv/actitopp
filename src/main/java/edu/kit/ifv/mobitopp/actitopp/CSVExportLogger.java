package edu.kit.ifv.mobitopp.actitopp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class CSVExportLogger 
{
	File basepath;
	
	FileWriter activitywriter;
	FileWriter tripwriter;
	FileWriter personwriter;
	
	/**
	 * 
	 * constructor with basepath
	 *
	 * @param basepath
	 * @throws IOException
	 */
	public CSVExportLogger(File basepath) throws IOException
	{
		this.basepath = basepath;
		openLogging(false);
		
		writeActivityData_header();
		writeTripData_header();
		writePersonData_header();
		
		closeLogging();
	}

	public void writeLogging(HashMap<Integer,?> maptoexport) throws IOException
	{
		openLogging(true);
		for(Object referenceobject : maptoexport.values())
    {  		
			// Householdmap
			if (referenceobject instanceof ActiToppHousehold)
			{
				ActiToppHousehold acthousehold = ((ActiToppHousehold) referenceobject);
				for (ActitoppPerson actperson : acthousehold.getHouseholdmembersasList())
				{
					exportsinglePerson(actperson);					
				}
			}			
			// Personmap
			if (referenceobject instanceof ActitoppPerson)
			{
				ActitoppPerson actperson = ((ActitoppPerson) referenceobject);
				exportsinglePerson(actperson);	
			}
		}
		closeLogging();
	}
	

	private void openLogging(boolean appendToExistingFile) throws IOException  
	{
		activitywriter = new FileWriter(new File(basepath, "actitopp_activities.csv"), appendToExistingFile);
		tripwriter = new FileWriter(new File(basepath, "actitopp_trips.csv"), appendToExistingFile);
		personwriter = new FileWriter(new File(basepath, "actitopp_persons.csv"), appendToExistingFile);
	}
	
	private void closeLogging() throws IOException  
	{
		activitywriter.close();
		tripwriter.close();
		personwriter.close();
	}
	
	
	private void writeActivityData_header() throws IOException
	{
		// Header
		activitywriter.append("HHIndex;PersNr;PersIndex;WOTAG;TourIndex;AktIndex;startzeit;startzeit_woche;endzeit;endzeit_woche;Dauer;zweck;jointStatus");
		activitywriter.append('\n');
		activitywriter.flush();
	}


	private void writeTripData_header() throws IOException  
	{
		// Header
		tripwriter.append("HHIndex;PersNr;PersIndex;WOTAG;anzeit;anzeit_woche;abzeit;abzeit_woche;Dauer;zweck_text;jointStatus");
		tripwriter.append('\n');
		tripwriter.flush();
	}


	private void writePersonData_header() throws IOException
	{
		// Header
		personwriter.append("HHIndex;numberofhhmembers;children010;childrenunder18;areatype;numberofcarsinhh;PersNr;PersIndex;Age;Employment;Gender;isAllowedToWork;CommutingdistanceWork;CommutingdistanceEducation");
		personwriter.append('\n');
		personwriter.flush();
	}
	
	private void exportsinglePerson(ActitoppPerson person) throws IOException
	{
		// Export activity data
		for (HActivity act : person.getWeekPattern().getAllActivities())
		{
			if (act.isScheduled())
			{
				activitywriter.append(writeActivity(act));
				activitywriter.flush();
			}
		}
		// Export trip data
		for (HTrip trip : person.getWeekPattern().getAllTrips())
		{
			if (trip.getDuration()>0)
			{
				tripwriter.append(writeTrip(trip));
				tripwriter.flush();
			}
		}
		// Export person information
		personwriter.append(writePersonInformation(person));
		personwriter.flush();
	}


	private String writePersonInformation(ActitoppPerson person)
	{
		String returnstring="";
					
		
		// HHIndex
		returnstring += person.getHousehold().getHouseholdIndex() + ";";
		// Number of HH members
		returnstring += person.getHousehold().getNumberofPersonsinHousehold() + ";";
		// Children 0-10
		returnstring += person.getHousehold().getChildren0_10() + ";";
		// Children under 18
		returnstring += person.getHousehold().getChildren_u18() + ";";
		// AreaType
		returnstring += person.getHousehold().getAreatype() + ";";
		// Number of cars in HH
		returnstring += person.getHousehold().getNumberofcarsinhousehold() + ";";
		
		// PersNr
		returnstring += person.getPersNrinHousehold() + ";";
		// PersIndex
		returnstring += person.getPersIndex() + ";";
		// Age
		returnstring += person.getAge() + ";";		
		// Employment
		returnstring += person.getEmployment() + ";";		
		// Gender
		returnstring += person.getGender() + ";";
		// isAllowedToWork
		returnstring += person.isAllowedToWork() + ";";
		// CommutingDistance Work
		returnstring += person.getCommutingdistance_work() + ";";
		// CommutingDistance Education
		returnstring += person.getCommutingdistance_education() + ";";		
		
		returnstring +="\n";
		return returnstring;		
	}
	
	
	
	/**
	 * 
	 * create output activity information
	 * 
	 * @param act
	 * @return
	 */
	private String writeActivity(HActivity act)
	{
		assert act.isScheduled():"Activity is not fully scheduled";
		String returnstring="";
		
		/*
		 * estimate tourindex and activityindex for home activities
		 * if day is home-day only, return 0/0, otherwise -99/-99
		 */
		int tmptourindex=0;
		int tmpaktindex=0;
		if (act.getDay().getAmountOfTours()>0)
		{
			tmptourindex=-99;
			tmpaktindex=-99;
		}
			
		// HHIndex
		returnstring += act.getPerson().getHousehold().getHouseholdIndex() + ";";		
		// PersNr
		returnstring += act.getPerson().getPersNrinHousehold() + ";";
		// PersIndex
		returnstring += act.getPerson().getPersIndex() + ";";
		// WOTAG
		returnstring += act.getWeekDay() + ";";
		// TourIndex
		returnstring += (act.isHomeActivity() ? tmptourindex : act.getTourIndex()) + ";";
		// AktIndex
		returnstring += (act.isHomeActivity() ? tmpaktindex : act.getIndex()) + ";";
		// starttime
		returnstring += act.getStartTime() + ";";
		// starttime_week
		returnstring += act.getStartTimeWeekContext() + ";"; 
		// endtime
		returnstring += act.getEndTime() + ";";		
		// endtime_week
		returnstring += act.getEndTimeWeekContext() + ";";  
		// duration
		returnstring += act.getDuration() + ";";
		// purpose
		returnstring += act.getActivityType().getTypeasChar() + ";";
		// joint Status
		returnstring += (Configuration.model_joint_actions ? act.getJointStatus(): "-99") + "";
		
		returnstring +="\n";
		return returnstring;		
	}


	/**
	 * 
	 * create output trip information
	 * 
	 * @param trip
	 * @return
	 */
	private String writeTrip(HTrip trip)
	{
		assert trip.isScheduled(): "Trip is not scheduled!";
		String returnstring="";
		
		// HHIndex
		returnstring += trip.getActivity().getPerson().getHousehold().getHouseholdIndex() + ";";		
		// PersNr
		returnstring += trip.getActivity().getPerson().getPersNrinHousehold() + ";";
		// PersIndex
		returnstring += trip.getActivity().getPerson().getPersIndex() + ";";
		// Weekday
		returnstring += trip.getActivity().getWeekDay() + ";";
		// endtime
		returnstring += trip.getEndTime() + ";";
		// endtime_week
		returnstring += trip.getEndTimeWeekContext() + ";"; 
		// starttime
		returnstring += trip.getStartTime() + ";";		
		// starttime_week
		returnstring += trip.getStartTimeWeekContext() + ";";  
		// duration
		returnstring += trip.getDuration() + ";";
		// type of trip
		returnstring += trip.getType().getTypeasChar() + ";";
		// jointStatus
		returnstring += (Configuration.model_joint_actions ? trip.getJointStatus(): "-99") + "";
		
		returnstring +="\n";
		return returnstring;		
	}
	
}

