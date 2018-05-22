package edu.kit.ifv.mobitopp.actitopp;


/**
 * 
 * @author Tim Hilgert
 *
 */
public class HTrip {

	// Trip ist an eine Activity gebunden
	private HActivity activity;
	
	private int tripduration = -1;;

	
	public HTrip(HActivity parent)
	{
		assert parent!=null :	"zugehörige Aktivität ist NULL!";
		this.activity = parent;
	}

	public HTrip(HActivity parent, int tripduration)
	{
		this(parent);
		assert tripduration>0 :	"Dauer ist 0!";
		this.tripduration = tripduration;
	}


	/**
	 * @return the tripduration
	 */
	public int getTripduration() {
		return tripduration;
	}


	/**
	 * @param tripduration the tripduration to set
	 */
	public void setTripduration(int tripduration) {
		this.tripduration = tripduration;
	}

	/**
	 * @return the activity
	 */
	public HActivity getActivity() {
		return activity;
	}

	
}
