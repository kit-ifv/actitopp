package edu.kit.ifv.mobitopp.actitopp;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

  private int decision = 0;
  private String alternativeChosen;
  protected ChoiceFunction choiceFunction;
  
  protected Map<String, String> inParamMap;
  protected List<String> alternativeNames;
  protected List<ModelAlternative> alternatives;
      
  protected FileBaseParameterWeightLoader parameterLoader;  
  
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
    
    this.inParamMap = new HashMap<String, String>();
    this.alternativeNames= new ArrayList<String>();  
    this.alternatives = new ArrayList<ModelAlternative>();  
    
    this.choiceFunction = new LogitFunction();
    this.parameterLoader = new FileBaseParameterWeightLoader(modelCoordinator.getFileBase());
    
    
    // Modellflowlisten aus der FileBase laden
    
    ModelFlowLists mf = modelCoordinator.getFileBase().getModelFlowLists(id); 	    	
		assert mf != null : "ModelFlow Object is null";

		/*
		 *  Alternativenlisten erzeugen
		 */
    for (String s : mf.getAlternativesList())
    {
    	alternativeNames.add(s);
    	alternatives.add(new ModelAlternative(s));
    }
    
    /*
     *  Parameter für den Schritt festlegen
     * 	Enthält alle Parameter die in diesem Schritt verwendet werden inkl. zugehöriger Referenzen (default, person, tour, ...)
     */
    for (String s : mf.getInParamMap().keySet())
    {
    	inParamMap.put(s, mf.getInParamMap().get(s));
    } 
  }
  
  
  /**
   * 
   * Methode zur Durchführung des DC-Model-Schritts
   * 
   */
  @Override
  public int doStep()
  {
    
    // Alternativen initialisieren
    initializeAlternatives(inParamMap, alternativeNames);

    // Nutzenfeld initialisieren
//    double[] utilities = new double[alternatives.size()];
 
    // Alternativen ggf. limitieren
//    int upperbound = (toRangeLimiter >= 0) ? toRangeLimiter+1 : alternatives.size();
    int toRangeLimiter2 = (toRangeLimiter >= 0) ? toRangeLimiter : alternatives.size();
 
/*
 * OLD    
 
    
    // Nutzen für jede der Alternativen bestimmen
    for(int i = 0+fromRangeLimiter; i < upperbound;i++)
    {
        utilities[i] = alternatives.get(i).getUtility();
    }

    // Feld anpassen, falls Alternativenlimitierung
    int arrTrimSize = upperbound-fromRangeLimiter;
    double[] chgUtilities = new double[arrTrimSize];
    for(int i = 0; i < arrTrimSize;i++)
    {
        chgUtilities[i] = utilities[fromRangeLimiter+i];
    }
 */   
 /*
  * NEW
  */
    // Alternativen, die außerhalb der Range liegen inaktiv setzen!
    for (int i=0; i<alternatives.size(); i++)
    {
    	if (i<fromRangeLimiter || i>toRangeLimiter2) alternatives.get(i).setEnabled(false);
    }
    
    // Wahrscheinlichkeiten berechnen
/*
 * OLD
 */
 //   double[] probabilities = choiceFunction.calculateProbabilities(chgUtilities);
/*
 * NEW    
 */
    choiceFunction.calculateProbabilities(alternatives);
    
    
    // Alternative bestimmen
    double randomvalue = modelCoordinator.getRandomGenerator().getRandomValue();
/*
 * OLD
 */
//    int decisionIndex = choiceFunction.chooseAlternative(probabilities, randomvalue);
/*
 * NEW    
 */    
    int tmpindex2 = choiceFunction.chooseAlternative(alternatives, randomvalue);
    
    //must add the offset because decisionIndex here might refer to a limited alternative range
//    decision = fromRangeLimiter + decisionIndex;
    decision = tmpindex2;
    //assert decisionIndex==tmpindex2 : "Entscheidungsfindungsprozess uneinheitlich!";
//    assert decision==tmpindex2 : "Entscheidungsfindungsprozess uneinheitlich!";
    alternativeChosen = alternativeNames.get(decision);
    
    // DEBUG USE ONLY
    if (Configuration.debugenabled)
    {
//    	printDecisionProcess(probabilities, alternativeNames,decision, chgUtilities, fromRangeLimiter,toRangeLimiter);
    }

    return decision;  
  }
  
  /**
	 * 
	 * @param inParamMap
	 * @param alternativeNames
	 */
	private void initializeAlternatives(Map<String, String> inParamMap, List<String> alternativeNames)
	{
	
		/*
		 *  Parameter laden für jede Alternative
		 *  
		 *  mappedParameters = enthält für jede Alternative eine Liste (ArrayList) mit ModelParameterWeights
		 *  ModelParameterWeights enthalten Name, Gewicht (Nutzenanteil) sowie Attribut (trifft zu oder nicht bzw. Größe)
		 */
		Map<String, List<ModelParameterWeight>> mappedParameters = parameterLoader.getWeightValues(id);
	  
		/*
		 *  Attribute zu den Parametern für jede Alternative laden
		 *  
		 *  mappedAttributes = enthält für jede Alternative eine Map mit Namen des Attributes und der Ausprägung für die Alternative
		 */
		Map<String, Map<String, Double>> mappedAttributes = initMappedAttributes(inParamMap, alternativeNames);
	
	  // Alternativen kalkulieren basierend auf Parametern
	  calculateAlternatives(alternativeNames, mappedParameters, mappedAttributes);
		
	}


	/**
	 * 
	 * @param inParamMap
	 * @param alternativeNames
	 * @return
	 */
	private Map<String, Map<String, Double>> initMappedAttributes(Map<String, String> inParamMap, List<String> alternativeNames)
	{
		
		Map<String, Map<String, Double>> mappedAttributes = new HashMap<String, Map<String, Double>>();  
	    
	  for (String alternativeName : alternativeNames)
	  {
	  	// Erzeuge eine eigene Map für die Alternative
	  	Map<String, Double> alternativeMap = new HashMap<String, Double>();
	
	  	// Setzte den Grundnutzen immer aktiv, d.h. das zugehörige Attribut ist 1
	  	alternativeMap.put("Grundnutzen", 1.0);
	  	
	  	// Ermittle die Werte für die restlichen Attribute
	    for (Entry<String, String> mapentry : inParamMap.entrySet())
	    {
	      double propertyValue = 0;
	      propertyValue = attributeLookup.getAttributeValue(mapentry.getValue(), mapentry.getKey());
	      alternativeMap.put(mapentry.getKey(), propertyValue);
	    }
	    
	    // Füge Alternativen Map ger Gesamt-Attributs-Map hinzu
	    mappedAttributes.put(alternativeName, alternativeMap);
	  }
	  return mappedAttributes;
	}


	/**
	 * 
	 * Methode erzeugt und initialisiert die Alternativen mit den zugeordneten Parametern
	 * 
	 * @param alternativeNames
	 * @param mappedParameters
	 * @param mappedAttributes
	 */
	private void calculateAlternatives(List<String> alternativeNames, Map<String, List<ModelParameterWeight>> mappedParameters, Map<String, Map<String, Double>> mappedAttributes)
	{
	  for (int i = 0; i < alternativeNames.size(); i++)
	  {
	
			// Referenz auf entsprechende Alternative setzen
	    ModelAlternative mAlt = alternatives.get(i);
	    
	    // Referenz auf Parameter für diese Alternative setzen
	    List<ModelParameterWeight> parameter = mappedParameters.get(alternativeNames.get(i));
	    
	    // Referenz auf Attribute für diese Alternative
	    Map<String, Double> attribute = mappedAttributes.get(alternativeNames.get(i));
	    
	    /*
	     * Vorgehen allgemein: Iteriere über alle Parameter aus parameter-Objekt und ordne den entsprechenden attributeValue aus attribute zu
	     */
	    
	    //Ermittle Grundnutzen           
	    ModelParameterWeight grundutzen = parameter.get(0);
	    assert grundutzen.getName().equals("Grundnutzen") : "erstes ModelParameterWeight ist nicht Grundnutzen! - " + grundutzen.getName();
	    
	    mAlt.getUtilityFunction().setBaseWeight(grundutzen.getWeight());
	    
	    //Ermittel alle weiteren Nutzen
	    for (int j = 1; j < parameter.size(); j++)
	    {
	        // Parameter bestimmen
	        ModelParameterWeight modelparameter = parameter.get(j);
	        // zugehörige Attributausprägung bestimmen
	        double attributauspraegung = attribute.get(modelparameter.getName());                
	        modelparameter.setattributevalue(attributauspraegung);
	        
	        // Parameterpaar hinzufügen
	        mAlt.getUtilityFunction().getUtilityPairs().add(modelparameter);      
	    }
	    
	    // this.printUtilityDetails(mAlt);
	      
	  }
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
   * Methode entfernt eine Alternative aus der Alternativenmenge.
   * 
   * 
   * ACHTUNG: AUFPASSEN, falls rangeLimiter und removeAlternative zusammen benutzt werden.
   * 					removeAlternative verändert die Alternativenliste und die unteren und oberen Grenzen sind 
   * 					dadurch möglicherweise anders, da nicht die ursprüngliche Liste angepasst wird
   * 
   * @param name
   */
  public void removeAlternative(String name)
  {
  	alternativeNames.remove(name);
  	for (Iterator<ModelAlternative> it = alternatives.iterator(); it.hasNext();)
  	{
  		ModelAlternative ma = it.next();
  		if (ma.getName().equals(name)) it.remove();
  	}
  }
  
  /**
   * Prüft, ob eine Alternative mit dem Name existiert
   * 
   * @param name
   * @return
   */
  public boolean existsAlternative(String name)
  {
  	boolean result=false;
  	for (Iterator<ModelAlternative> it = alternatives.iterator(); it.hasNext();)
  	{
  		ModelAlternative ma = it.next();
  		if (ma.getName().equals(name)) result=true;
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
	 *  
	 * @param probabilities
	 * @param altNames
	 * @param decisionIndex
	 * @param utilities
	 * @param fromRangeLimiter
	 * @param toRangeLimiter
	 */
	private void printDecisionProcess(double[] probabilities, List<String> altNames, int decisionIndex,double[] utilities, int fromRangeLimiter, int toRangeLimiter)
	{
	    int upperbound = (toRangeLimiter >= 0) ? toRangeLimiter+1 : alternatives.size();
	    int startOffset = fromRangeLimiter;
	    System.out.println("-------- DECISIONS FOR STEP " + this.id +" ---------------");
	           
	    int i = 0+startOffset;
	    for(;i< upperbound;i++)
	    {
	        System.out.println("Alternative: "+altNames.get(i) +" mit P: "+ NumberFormat.getPercentInstance().format(probabilities[i-startOffset]) +" - U: " + utilities[i-startOffset]);
	    }
	
	    System.out.println("Chosen alternative: " + altNames.get(decisionIndex));
	    System.out.println("Random Value: " + modelCoordinator.getRandomGenerator().getLastRandomValue());
	    System.out.println("SAVED for: " + attributeLookup);
	    System.out.println();
	}


	@SuppressWarnings("unused")
  private void printUtilityDetails(ModelAlternative alternative)
  {
      System.out.println("ALT: " + alternative.getName() );
      //print base utility
      System.out.print("Grundnutzen (real):"+alternative.getUtilityFunction().getBaseWeight() +" ___ ");
      for(int i = 0; i < alternative.getUtilityFunction().getUtilityPairs().size(); i++)
      {
          ModelParameterWeight pair = alternative.getUtilityFunction().getUtilityPairs().get(i);
          System.out.print(pair.getName() + ":" + pair.getattributevalue() + "*" + pair.getWeight());
          System.out.print(" __ ");
      }
      System.out.println("\nTOTAL UTILITY: " + alternative.getUtility());
      
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
