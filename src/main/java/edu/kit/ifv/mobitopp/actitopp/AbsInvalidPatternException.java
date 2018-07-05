package edu.kit.ifv.mobitopp.actitopp;


public abstract class AbsInvalidPatternException extends Exception{
	
	private static final long serialVersionUID = -3495343767758041143L;

	
	public abstract String getReason();

  public abstract void setReason(String reason);

  public abstract HWeekPattern getFaultyPattern();

  public abstract void setFaultyPattern(HWeekPattern faultyPattern);

	public abstract HActivity[] getInvolvedActivities();

	public abstract void setInvolvedActivities(HActivity[] involvedActivities);
}
