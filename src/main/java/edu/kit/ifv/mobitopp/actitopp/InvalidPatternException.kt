package edu.kit.ifv.mobitopp.actitopp;


/**
 * @author Tim Hilgert
 * This Exception is thrown if the weekly pattern of a person is invalid.
 * In that case, it is advised to redo the whole activity creation process but with a different seed.
 * 
 * Depending on the source of the error, only one person or the whole household needs to be remodeled
 */
public class InvalidPatternException extends AbsInvalidPatternException
{

	private static final long serialVersionUID = -3030772908826568766L;
	private HWeekPattern faultyHWeekPattern;
  private String reason;
  private HActivity[] involvedActivities;
  
  // Household or Person
  private String errorType;
    

  public InvalidPatternException(String errortype, HWeekPattern faultyPattern, String reason)
  {
  	this.errorType = errortype;
    this.faultyHWeekPattern = faultyPattern;
    this.reason = reason;
  }
    
  
	public String getReason()
  {
      return errorType + " Error - "
      				+ "HH" + faultyHWeekPattern.getPerson().getHousehold().getHouseholdIndex() + "/" 
      				+ "P" + faultyHWeekPattern.getPerson().getPersIndex() 
      				+ " - " + reason;
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


	/**
	 * @return the errortype
	 */
	public String getErrorType() {
		return errorType;
	}


	/**
	 * @param errortype the errortype to set
	 */
	public void setErrorType(String errortype) {
		this.errorType = errortype;
	}

}
