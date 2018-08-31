package edu.kit.ifv.mobitopp.actitopp;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * 
 * @author Tim Hilgert
 *
 * Objekt für einen Discrete Choice Modell Schritt
 */
public class DefaultDCModelStep extends AbsHModelStep
{

	private AttributeLookup attributeLookup;

  private int decision = -1;
  private String alternativeChosen;
  protected ChoiceFunction choiceFunction;
  
  private ModellnformationDC modelinfo;
	
  
//  protected Map<String, String> inParamMap;
  protected ArrayList<ModelAlternative> alternatives;

	/*
	 *  mappedParameters = enthält für jede Alternative eine Liste (ArrayList) mit ModelParameterWeights
	 *  ModelParameterWeights enthalten Name, Gewicht (Nutzenanteil) sowie Attribut (trifft zu oder nicht bzw. Größe)
	 */
//  protected Map<String, List<ModelParameterWeight>> mappedParameters;
  
  
  
  //restrict alternatives to a specific range
  private int fromRangeLimiter = 0;
  private int toRangeLimiter = -1;
  
     
  /**
   * 
   * Konstruktor
   * 
   * @param id
   * @param modelCoordinator
   * @param attributeLookup
   */
  public DefaultDCModelStep(String id, Coordinator modelCoordinator, AttributeLookup attributeLookup)
  {
    super(id, modelCoordinator);
    
    this.attributeLookup = attributeLookup;
    this.alternatives = new ArrayList<ModelAlternative>();  
    this.choiceFunction = new LogitFunction();   
    
    
 //   this.inParamMap = new HashMap<String, String>();
   
    /*
     * load model information from file base
     */
    modelinfo = modelCoordinator.getFileBase().getModelInformationforDCStep(id);
    assert modelinfo != null : "ModelInformationDC Object is null";
    
    
 //   ModelFlowInformation mf = modelCoordinator.getFileBase().getModelFlowInformation(id); 	    	
//		assert mf != null : "ModelFlow Object is null";

		/*
		 *  create object for each step alternative
		 */
    for (String s : modelinfo.getAlternativesList())
    {
    	alternatives.add(new ModelAlternative(s));
    }
    
    /*
     *  Parameter für den Schritt festlegen
     * 	Enthält alle Parameter die in diesem Schritt verwendet werden inkl. zugehöriger Referenzen (default, person, tour, ...)
     */
//    for (Entry<String, String> s : mf.getParamMap().entrySet())
//    {
 //   	inParamMap.put(s.getKey(), s.getValue());
//    } 
    
    /*
     * mappedParameters = enthält für jede Alternative eine Liste (ArrayList) mit ModelParameterWeights
     * ModelParameterWeights enthalten Name, Gewicht (Nutzenanteil) sowie Attribut (trifft zu oder nicht bzw. Größe)
     * 
     */
 //   mappedParameters = new FileBaseParameterWeightLoader(modelCoordinator.getFileBase()).getWeightValues(id);
  }
  
  
  /**
   * 
   * Methode zur Durchführung des DC-Model-Schritts
   * 
   */
  @Override
  public int doStep()
  {
      	
    // set rangeLimiter (UpperBound) if not yet determinded
    toRangeLimiter = (toRangeLimiter >= 0) ? toRangeLimiter : alternatives.size();
    
    assert toRangeLimiter>=fromRangeLimiter : "fromRangeLimiter > toRangeLimiter!";

    // disable alternatives out of Lower-UpperBound range!
    for (int i=0; i<alternatives.size(); i++)
    {
    	if (i<fromRangeLimiter || i>toRangeLimiter) alternatives.get(i).setEnabled(false);
    }
    
    /*
     * check that there is at least one alternative still enabled
     */
    boolean alternativeverfuegbar=false;
    for (ModelAlternative mAlt : alternatives)
    {
    	if (mAlt.isEnabled()) alternativeverfuegbar=true;
    }
    assert alternativeverfuegbar==true : "Keine Alternative verfügbar!";
    
	  // initialize utility function for each alternative
  	initAlternatives();
    
    // Wahrscheinlichkeiten der aktiven Alternativen berechnen
    choiceFunction.calculateProbabilities(alternatives);
    
    // Alternative bestimmen
    double randomvalue = modelCoordinator.getRandomGenerator().getRandomValue();
    decision = choiceFunction.chooseAlternative(alternatives, randomvalue);
    alternativeChosen = alternatives.get(decision).getName();
    
    // DEBUG USE ONLY
    if (Configuration.debugenabled)
    {
    	printDecisionProcess();
    }

    assert decision!=-1 : "Entscheidung konnte nicht getroffen werden!";
    return decision;  
  }
  

	/**
	 * initialize utility functions of all enabled alternatives
	 */
	private void initAlternatives()
	{
		for (ModelAlternative mAlt : alternatives)
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
		  	// Erzeuge eine eigene Map für die Alternative
		  	Map<String, Double> attribute = new HashMap<String, Double>();
		
		  	// Setzte den Grundnutzen immer aktiv, d.h. das zugehörige Attribut ist 1
		  	attribute.put("Grundnutzen", 1.0);
		  	
		  	// Ermittle die Werte für die restlichen Attribute aus der inParamMap und dem attributeLookup
		    for (Entry<String, String> mapentry : inParamMap.entrySet())
		    {
		      double propertyValue = 0;
		      propertyValue = attributeLookup.getAttributeValue(mapentry.getValue(), mapentry.getKey());
		      attribute.put(mapentry.getKey(), propertyValue);
		    }
		    				

		    // Referenz auf Parameter für diese Alternative setzen
		    List<ModelParameterAttributeCombination> parameter = mappedParameters.get(mAlt.getName());
		    
		    /*
		     * Vorgehen allgemein: Iteriere über alle Parameter aus parameter-Objekt und ordne den entsprechenden attributeValue aus attribute zu
		     */
/*		    
		    //Ermittle Grundnutzen           
		    ModelParameterAttributeCombination grundnutzen = parameter.get(0);
		    assert grundnutzen.getName().equals("Grundnutzen") : "erstes ModelParameterWeight ist nicht Grundnutzen! - " + grundnutzen.getName();
		    
		    mAlt.getUtilityFunction().setBaseWeight(grundnutzen.getparameterValue());
		    
		    //Ermittel alle weiteren Nutzen
		    for (int j = 1; j < parameter.size(); j++)
		    {
		        // Parameter bestimmen
		        ModelParameterAttributeCombination modelparameter = parameter.get(j);
		        // zugehörige Attributausprägung bestimmen
		        double attributauspraegung = attribute.get(modelparameter.getName());                
		        modelparameter.setattributevalue(attributauspraegung);
		        
		        // Parameterpaar hinzufügen
		        mAlt.getUtilityFunction().getUtilityPairs().add(modelparameter);      
		    }
		    
			}
	  }*/
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
  
  public int getUpperBound()
  {
  	return toRangeLimiter;
  }
  
  public int getLowerBound()
  {
  	return fromRangeLimiter;
  }
  
  /**
   * Methode deaktiviert eine Alternative aus der Alternativenmenge.
   * Diese wird dann bei der Auswahl der Alternative NICHT berücksichtigt.
   * 
   * @param name
   */
  public void disableAlternative(String name)
  {
  	for (ModelAlternative ma : alternatives)
  	{
  		if (ma.getName().equals(name)) ma.setEnabled(false);
  	}
  }
  
  /**
   * Prüft, ob die Alternative mit dem Name aktiviert ist
   * 
   * @param name
   * @return
   */
  public boolean alternativeisEnabled(String name)
  {
  	boolean result=false;
  	for (Iterator<ModelAlternative> it = alternatives.iterator(); it.hasNext();)
  	{
  		ModelAlternative ma = it.next();
  		if (ma.getName().equals(name) && ma.isEnabled()) result=true;
  	}
  	return result;
  }
  
  
  
  /**
   * Verändert den UtilityFactor einer Modelalternative zur Nutzenbeeinflussung
   * auf Basis des Alternativennamens
   * 
   * @param name
   * @param utilityfactor
   */
  public void adaptUtilityFactor(String alternativename, double utilityfactor)
  {
  	for (ModelAlternative ma : alternatives)
  	{
  		if (ma.getName().equals(alternativename)) ma.setUtilityfactor(utilityfactor);
  	}
  }

  
  /**
   * Verändert den UtilityFactor einer Modelalternative zur Nutzenbeeinflussung
   * auf Basis des Feldindizes der Alternative
   * 
   * @param name
   * @param utilityfactor
   */
  public void adaptUtilityFactor(int alternativeindex, double utilityfactor)
  {
  	alternatives.get(alternativeindex).setUtilityfactor(utilityfactor);
  }

	/**
	 * Schreibt den Entschedungsprozess in die Konsole
	 */
	public void printDecisionProcess()
	{
	    System.out.println("-------- DECISIONS FOR STEP " + this.id +" ---------------");
	           
	    for(ModelAlternative mAlt : alternatives)
	    {
	    	if(mAlt.isEnabled())
	    	{
	    		System.out.println("Alternative: "+mAlt.getName() +" mit P: "+ NumberFormat.getPercentInstance().format(mAlt.getProbability()) +" - U: " + mAlt.getUtility());
	    	}
	    }
	
	    System.out.println("Chosen alternative: " + alternativeChosen);
	    System.out.println("Random Value: " + modelCoordinator.getRandomGenerator().getLastRandomValue());
	    System.out.println("SAVED for: " + attributeLookup);
	    System.out.println();
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
