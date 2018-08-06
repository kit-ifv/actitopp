package edu.kit.ifv.mobitopp.actitopp;


/**
 * @author Tim Hilgert
 * This Exception is thrown if the weekly pattern of a person is invalid.
 * In that case, it is advised to redo the whole activity creation proces for that person, but with a different seed
 */
public class InvalidPersonPatternException extends AbsInvalidPatternException
{

	private static final long serialVersionUID = -3030772908826568766L;
	private HWeekPattern faultyHWeekPattern;
  private String reason;
  private HActivity[] involvedActivities;
  
  private String errorType ="Person";
    

  public InvalidPersonPatternException(HWeekPattern faultyPattern, String reason)
  {
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
