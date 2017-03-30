package edu.kit.ifv.mobitopp.actitopp;


/**
 * @author Tim Hilgert
 * This Exception is thrown if the weekly pattern of a person is invalid.
 * In that case, it is advised to redo the whole activity creation proces for that person, but with a different seed
 */
public class InvalidPatternException extends Exception
{

	private static final long serialVersionUID = 6106849388004860905L;
	private HWeekPattern faultyHWeekPattern;
  private String reason;
  private HActivity[] involvedActivities;
    

  public InvalidPatternException(HWeekPattern faultyPattern, String reason)
  {
    this.faultyHWeekPattern = faultyPattern;
    this.reason = reason;
  }
    
  public InvalidPatternException(HActivity[] involved, HWeekPattern faultyPattern, String reason) 
  {
    this.involvedActivities = involved;
  	this.faultyHWeekPattern = faultyPattern;
    this.reason = reason;
	}

	public String getReason()
  {
      return reason;
  }


  public void setReason(String reason)
  {
      this.reason = reason;
  }


  public HWeekPattern getFaultyPattern()
  {
      return faultyHWeekPattern;
  }


  public void setFaultyPattern(HWeekPattern faultyPattern)
  {
      this.faultyHWeekPattern = faultyPattern;
  }


	public HActivity[] getInvolvedActivities() {
		return involvedActivities;
	}
	

	public void setInvolvedActivities(HActivity[] involvedActivities) {
		this.involvedActivities = involvedActivities;
	}

}
