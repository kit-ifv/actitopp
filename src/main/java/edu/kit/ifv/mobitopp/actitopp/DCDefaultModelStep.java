package edu.kit.ifv.mobitopp.actitopp;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * 
 * @author Tim Hilgert
 *
 * object for a discrete choice model step
 */
public class DCDefaultModelStep extends AbsHModelStep
{
	
	private RNGHelper randomgenerator;
	private AttributeLookup attributeLookup;

  private int decision = -1;
  private String alternativeChosen;
  private ChoiceFunction choiceFunction;
  
  
  private DCModelSteplnformation modelinfo;
  private ArrayList<DCAlternative> alternatives;  
  
  //restrict alternatives to a specific range
  private int fromRangeLimiter = 0;
  private int toRangeLimiter = -1;
  
     
  /**
   * 
   * @param id
   * @param modelFileBase
   * @param attributeLookup
   */
  public DCDefaultModelStep(String id, ModelFileBase modelFileBase, AttributeLookup attributeLookup, RNGHelper randomgenerator)
  {
    super(id);
    
    this.randomgenerator = randomgenerator;
    this.attributeLookup = attributeLookup;
    this.alternatives = new ArrayList<DCAlternative>();  
    this.choiceFunction = new LogitFunction();   
    
    /*
     * load model information from file base
     */
    modelinfo = modelFileBase.getModelInformationforDCStep(id);
    assert modelinfo != null : "ModelInformationDC Object is null";
    
		/*
		 *  create an object for each step alternative
		 */
    for (String s : modelinfo.getAlternativesList())
    {
    	alternatives.add(new DCAlternative(s));
    }
  }
  
  /**
   * 
   * @param id
   * @param modelCoordinator
   * @param attributeLookup
   */
  @Deprecated
  public DCDefaultModelStep(String id, Coordinator modelCoordinator, AttributeLookup attributeLookup)
  {
  	 this(id, modelCoordinator.getFileBase(), attributeLookup, modelCoordinator.getRandomGenerator());
  }
  
  
  /**
   * 
   * method to do a dc model step
   * 
   */
  @Override
  public int doStep()
  {
      	
    /*
     * set rangeLimiter (UpperBound) if not yet determinded
     */
    toRangeLimiter = (toRangeLimiter >= 0) ? toRangeLimiter : alternatives.size();
    
    assert toRangeLimiter>=fromRangeLimiter : "fromRangeLimiter > toRangeLimiter!";

    /*
     * disable alternatives out of Lower-UpperBound range!
     */
    for (int i=0; i<alternatives.size(); i++)
    {
    	if (i<fromRangeLimiter || i>toRangeLimiter) alternatives.get(i).setEnabled(false);
    }
    
    /*
     * check that there is at least one alternative still enabled
     */
    boolean alternativeavailable=false;
    for (DCAlternative mAlt : alternatives)
    {
    	if (mAlt.isEnabled()) alternativeavailable=true;
    }
    assert alternativeavailable==true : "no alternative available!";
    
	  /*
	   * initialize utility function for each alternative
	   */
    for (DCAlternative mAlt : alternatives)
	  {  
			if (mAlt.isEnabled())
			{
				UtilityFunction uf = mAlt.getUtilityFunction();
				
				// Loop through all parameters of this alternative
				for (Entry<String, Double> mapentry : modelinfo.getParameterValuesforAlternative(mAlt.getName()).entrySet())
				{
					String parameterName = mapentry.getKey();
					Double parameterValue = mapentry.getValue();
					
					if(parameterName.equals("Grundnutzen") || parameterName.equals("Intercept"))
					{ 
						uf.setBaseWeight(parameterValue);
					}
					else
					{
						String parameterContext = modelinfo.getContextforParameter(parameterName);
						double attributeValue = attributeLookup.getAttributeValue(parameterContext, parameterName);
						uf.addParameterAttributeCombination(new UtilityParameterAttributeCombination(parameterName, parameterValue, attributeValue));
					}
				}
			}
	  }
    
    /*
     * determine probabilities of enabled alternatives
     */
    choiceFunction.calculateProbabilities(alternatives);
    
    /*
     * decide for one alternative
     */
    double randomvalue = randomgenerator.getRandomValue();
    decision = choiceFunction.chooseAlternative(alternatives, randomvalue);
    alternativeChosen = alternatives.get(decision).getName();
    
    // DEBUG USE ONLY
    if (Configuration.debugenabled)
    {
    	printDecisionProcess();
    }

    assert decision!=-1 : "could not make a decision!";
    return decision;  
  }

	/**
   * Limits the DC-process to a certain alternative range. this method must be called before doStep() if necessary
   * @param from
   * @param to
   */
  public void limitUpperandLowerBound(int from, int to)
  {
    fromRangeLimiter = from;
    toRangeLimiter = to;
  }
  
	/**
   * Limits the DC-process to a certain alternative upperBound. this method must be called before doStep() if necessary
   * @param from
   * @param to
   */
  public void limitUpperBoundOnly(int to)
  {
  	toRangeLimiter = to;
  }
  
	/**
   * Limits the DC-process to a certain alternative lowerBound. this method must be called before doStep() if necessary
   * @param from
   * @param to
   */
  public void limitLowerBoundOnly(int from)
  {
  	fromRangeLimiter = from;
  }
  
  /**
   * methode disables an alternative of the set of alternatives.
   * this alternative will not be considered when choosing an alternative.
   * 
   * @param name
   */
  public void disableAlternative(String name)
  {
  	for (DCAlternative ma : alternatives)
  	{
  		if (ma.getName().equals(name)) ma.setEnabled(false);
  	}
  }
  
  /**
   * changes the utilityfactor of an alternative based on an alternativename.
   * utilityfactir is used to influence the utility/significance of alternatives
   * 
   * @param name
   * @param utilityfactor
   */
  public void adaptUtilityFactor(String alternativename, double utilityfactor)
  {
  	for (DCAlternative ma : alternatives)
  	{
  		if (ma.getName().equals(alternativename)) ma.setUtilityfactor(utilityfactor);
  	}
  }

  
  /**
   * changes the utilityfactor of an alternative based on an alternativeindex.
   * utilityfactir is used to influence the utility/significance of alternatives
   * 
   * @param name
   * @param utilityfactor
   */
  public void adaptUtilityFactor(int alternativeindex, double utilityfactor)
  {
  	alternatives.get(alternativeindex).setUtilityfactor(utilityfactor);
  }

	/**
	 * prints the decision process for debug reasons
	 */
	public void printDecisionProcess()
	{
	    System.out.println("-------- DECISIONS FOR STEP " + this.id +" ---------------");
	           
	    for(DCAlternative mAlt : alternatives)
	    {
	    	if(mAlt.isEnabled())
	    	{
	    		System.out.println("alternative: "+mAlt.getName() +" prob: "+ NumberFormat.getPercentInstance().format(mAlt.getProbability()) +" - utility: " + mAlt.getUtility());
	    	}
	    }
	
	    System.out.println("Chosen alternative: " + alternativeChosen);
	    System.out.println("Random Value: " + randomgenerator.getLastRandomValue());
	    System.out.println("SAVED for: " + attributeLookup);
	    System.out.println();
	}

  /**
	 * check if alternative is enabled or not
	 * 
	 * @param name
	 * @return
	 */
	public boolean alternativeisEnabled(String name)
	{
		boolean result=false;
		for (Iterator<DCAlternative> it = alternatives.iterator(); it.hasNext();)
		{
			DCAlternative ma = it.next();
			if (ma.getName().equals(name) && ma.isEnabled()) result=true;
		}
		return result;
	}


	public int getLowerBound()
	{
		return fromRangeLimiter;
	}


	public int getUpperBound()
	{
		return toRangeLimiter;
	}


	public int getDecision()
	{
	    return decision;
	}

	public String getAlternativeChosen()
	{
	    return alternativeChosen;
	}

}
