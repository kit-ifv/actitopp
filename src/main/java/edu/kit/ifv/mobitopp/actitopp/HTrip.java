package edu.kit.ifv.mobitopp.actitopp;


/**
 * 
 * @author Tim Hilgert
 *
 */
public class HTrip {

	// each trip is bound to an activity
	private HActivity activity;
	
	// indicator if trip is one before or after an activity
	private TripStatus status;

	private int tripduration = -1;


	public HTrip(HActivity parent, TripStatus type, int tripduration)
	{
		assert parent!=null :	"corresponding activity NULL!";
		assert tripduration>0 :	"duration is less or equal 0!";
		
		this.activity = parent;
		this.status = type;
		this.tripduration = tripduration;
	}

	/**
	 * @return the tripduration
	 */
	public int getDuration() {
		return tripduration;
	}


	/**
	 * @param tripduration the tripduration to set
	 */
	public void setDuration(int tripduration) {
		this.tripduration = tripduration;
	}

	/**
	 * @return the activity
	 */
	public HActivity getActivity() {
		return activity;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isScheduled()
	{
		return getDuration()!=-1;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getStartTime()
	{
		int starttime=-1;
		
		if (status.equals(TripStatus.TRIP_BEFORE_ACT))
		{
			starttime = activity.getStartTime() - tripduration;
		}
		if (status.equals(TripStatus.TRIP_AFTER_ACT))
		{
			starttime = activity.getEndTime();
		}
		
		assert starttime!=-1 : "could not get TripStartTime";		
		return starttime;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getEndTime()
	{
		int endtime=-1;
		
		if (status.equals(TripStatus.TRIP_BEFORE_ACT))
		{
			endtime = activity.getStartTime();
		}
		if (status.equals(TripStatus.TRIP_AFTER_ACT))
		{
			endtime = activity.getEndTime() + tripduration;
		}
		
		assert endtime!=-1 : "could not get TripEndTime";		
		return endtime;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getStartTimeWeekContext()
	{
		int starttime=-1;
		
		if (status.equals(TripStatus.TRIP_BEFORE_ACT))
		{
			starttime = activity.getStartTimeWeekContext() - tripduration;
		}
		if (status.equals(TripStatus.TRIP_AFTER_ACT))
		{
			starttime = activity.getEndTimeWeekContext();
		}
		
		assert starttime!=-1 : "could not get TripStartTime";		
		return starttime;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getEndTimeWeekContext()
	{
		int endtime=-1;
		
		if (status.equals(TripStatus.TRIP_BEFORE_ACT))
		{
			endtime = activity.getStartTimeWeekContext();
		}
		if (status.equals(TripStatus.TRIP_AFTER_ACT))
		{
			endtime = activity.getEndTimeWeekContext() + tripduration;
		}
		
		assert endtime!=-1 : "could not get TripEndTime";		
		return endtime;
	}
	
	/**
	 * 
	 * @return
	 */
	public ActivityType getType()
	{
		ActivityType type=ActivityType.UNKNOWN;
		
		if (status.equals(TripStatus.TRIP_BEFORE_ACT))
		{
			type = activity.getActivityType();
		}
		
		/*
		 * until now, trip after activities only occur after the last activity in a tour, thus they are always trips to home
		 */
		if (status.equals(TripStatus.TRIP_AFTER_ACT))
		{
			type = ActivityType.HOME;
		}
		
		assert type!=ActivityType.UNKNOWN : "could not get TripType";		
		return type;
	}
	
	/**
	 * 
	 * @return
	 */
	public JointStatus getJointStatus()
	{
		JointStatus jointStatus= JointStatus.UNKNOWN;
		
		if (status.equals(TripStatus.TRIP_BEFORE_ACT))
		{
			jointStatus = activity.getJointStatus();
		}
		
		/*
		 * until now, trip after activities are home trips and joint home trips are not yet supported
		 * 
		 */
		if (status.equals(TripStatus.TRIP_AFTER_ACT))
		{
			jointStatus = JointStatus.NOJOINTELEMENT;
		}
		
		assert jointStatus!=JointStatus.UNKNOWN : "could not get jointStatus";		
		return jointStatus;
	}
	
	
	@Override
	public String toString()
	{
		return "trip: start(week): " + getStartTimeWeekContext() + " / end(week): " + getEndTimeWeekContext() + " / duration: " + getDuration();
	}
	
}
