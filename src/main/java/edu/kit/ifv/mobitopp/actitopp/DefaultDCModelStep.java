package edu.kit.ifv.mobitopp.actitopp;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Tim Hilgert
 *
 * Objekt f�r einen Discrete Choice Modell Schritt
 */
public class DefaultDCModelStep extends AbsHModelStep
{
	
//TODO Methoden checken und kommentieren!
	
		private AttributeLookup attributeLookup;

    private int decision = 0;
    private String alternativeChosen;
    protected ChoiceFunction choiceFunction;
    
    protected Map<String, String> inParamMap;
    protected List<String> alternativeNames;
    protected List<ModelAlternative> alternatives;
    protected List<String> outParamList;
        
    protected FileBaseParameterWeightLoader parameterLoader;  
    
    //restrict alternatives to a specific range
    private int fromRangeLimiter = 0;
    private int toRangeLimiter = -1;
    
    private boolean modifiedUtility;
    private int utilityBonusTarget;
    private double utilityBonusPercentage;

     
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
        
        this.outParamList = new ArrayList<String>();  
        
        this.choiceFunction = new LogitFunction();
        this.parameterLoader = new FileBaseParameterWeightLoader(modelCoordinator.getFileBase());
        
        
        // Erzeuge die ModelFlowListen
        
        ModelFlowLists mf = modelCoordinator.getFileBase().getModelFlowLists(id); 	    	
  			assert mf != null : "ModelFlow Object is null";
  	
  			// Alternativenlisten erzeugen
  	    for (String s : mf.getAlternativesList())
  	    {
  	    	alternativeNames.add(s);
  	    	alternatives.add(new ModelAlternative(s));
  	    }
  	    
  	    /*
  	     *  Parameter f�r den Schritt festlegen
  	     * 	Enth�lt alle Parameter die in diesem Schritt verwendet werden inkl. zugeh�riger Referenzen (default, person, tour, ...)
  	     */
  	    for (String s : mf.getInParamMap().keySet())
  	    {
  	    	inParamMap.put(s, mf.getInParamMap().get(s));
  	    }
  	
  	    /*
  	     * Ausgabeparameter festlegen - identisch mit Alternativen?
  	     * 
  	     */
  	    for (String s : mf.getOutParamList())
  	    {
  	    	outParamList.add(s);
  	    }     
          
    }
    
    
    /**
     * 
     * Methode zur Durchf�hrung des DC-Model-Schritts
     * 
     */
    @Override
    public int doStep()
    {
        
        // Alternativen initialisieren
        initializeAlternatives(inParamMap, alternativeNames);

        // Nutzenfeld initialisieren
        double[] utilities = new double[alternatives.size()];
     
        // Alternativen ggf. limitieren
        int upperbound = (toRangeLimiter >= 0) ? toRangeLimiter+1 : alternatives.size();
        
        // Nutzen f�r jede der Alternativen bestimmen
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
        
        // Nutzenbonus berechnen, falls gefordert und ZIelkategorie noch in der Alternativenmenge enthalten
        if(modifiedUtility && utilityBonusTarget>=fromRangeLimiter && utilityBonusTarget<=toRangeLimiter) 
        {
        	chgUtilities = addUtilityBonus(chgUtilities, utilityBonusTarget-fromRangeLimiter, utilityBonusPercentage);
        }
        
        // Wahrscheinlichkeiten berechnen
        double[] probabilities = choiceFunction.calculateProbabilities(chgUtilities);
        
        // Alternative bestimmen
        int decisionIndex = choiceFunction.chooseAlternative(probabilities, modelCoordinator.getRandomGenerator());
        
        //must add the offset because decisionIndex here might refer to a limited alternative range
        decision = fromRangeLimiter + decisionIndex;
        alternativeChosen = alternativeNames.get(decision);
        
        // DEBUG USE ONLY
        if (Configuration.debugenabled)
        {
        	printDecisionProcess(probabilities, alternativeNames,decision, chgUtilities, fromRangeLimiter,toRangeLimiter);
        }

        return decision;  
    }
    
    /**
		 * 
		 * @param inParamMap
		 * @param alternativeNames
		 */
		public void initializeAlternatives(Map<String, String> inParamMap, List<String> alternativeNames)
		{
		
			/*
			 *  Parameter laden f�r jede Alternative
			 *  
			 *  mappedParameters = enth�lt f�r jede Alternative eine Liste (ArrayList) mit ModelParameterWeights
			 *  ModelParameterWeights enthalten Name, Gewicht (Nutzenanteil) sowie Attribut (trifft zu oder nicht bzw. Gr��e)
			 */
			Map<String, List<ModelParameterWeight>> mappedParameters = parameterLoader.getWeightValues(id);
		  
			/*
			 *  Attribute zu den Parametern f�r jede Alternative laden
			 *  
			 *  mappedAttributes = enth�lt f�r jede Alternative eine Map mit Namen des Attributes und der Auspr�gung f�r die Alternative
			 */
			Map<String, Map<String, Double>> mappedAttributes = initMappedAttributes(inParamMap, alternativeNames);
		
		  // Alternativen kalkulieren basierend auf Parametern
		  calculateAlternatives(alternativeNames, mappedParameters, mappedAttributes);
		    
		    //System.out.println("Utilities..." + mappedParameters);
		
		}


		/**
     * Limits the DC-process to a certain alternative range. this method must be called before doStep() if necessary
     * @param from
     * @param to
     */
    public void limitAlternatives(int from, int to)
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
		    
		    // Referenz auf Parameter f�r diese Alternative setzen
		    List<ModelParameterWeight> parameter = mappedParameters.get(alternativeNames.get(i));
		    
		    // Referenz auf Attribute f�r diese Alternative
		    Map<String, Double> attribute = mappedAttributes.get(alternativeNames.get(i));
		    
		    /*
		     * Vorgehen allgemein: Iteriere �ber alle Parameter aus parameter-Objekt und ordne den entsprechenden attributeValue aus attribute zu
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
		        // zugeh�rige Attributauspr�gung bestimmen
		        double attributauspraegung = attribute.get(modelparameter.getName());                
		        modelparameter.setattributevalue(attributauspraegung);
		        
		        // Parameterpaar hinzuf�gen
		        mAlt.getUtilityFunction().getUtilityPairs().add(modelparameter);      
		    }
		    
		    // this.printUtilityDetails(mAlt);
		      
		  }
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
    public void printDecisionProcess(double[] probabilities, List<String> altNames, int decisionIndex,double[] utilities, int fromRangeLimiter, int toRangeLimiter)
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


    
    public void applyUtilityModification(int target, double percentage)
		{
		    utilityBonusPercentage = percentage;
		    utilityBonusTarget = target;
		    modifiedUtility = true;
		}


		private double[] addUtilityBonus(double[] utilities, int target, double bonus)
		{
		    double[] modified = utilities;
		    modified[target] *= bonus;
		    return modified;
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
      	// Erzeuge eine eigene Map f�r die Alternative
      	Map<String, Double> alternativeMap = new HashMap<String, Double>();

      	// Setzte den Grundnutzen immer aktiv, d.h. das zugeh�rige Attribut ist 1
      	alternativeMap.put("Grundnutzen", 1.0);
      	
      	// Ermittle die Werte f�r die restlichen Attribute
        for (Entry<String, String> mapentry : inParamMap.entrySet())
        {
          double propertyValue = 0;
          propertyValue = attributeLookup.getAttributeValue(mapentry.getValue(), mapentry.getKey());
          alternativeMap.put(mapentry.getKey(), propertyValue);
        }
        
        // F�ge Alternativen Map ger Gesamt-Attributs-Map hinzu
        mappedAttributes.put(alternativeName, alternativeMap);
      }
      return mappedAttributes;
    }
    

    public int getDecision()
		{
		    return decision;
		}


		public String getAlternativeChosen()
		{
		    return alternativeChosen;
		}


		public void setAlternativeChosen(String alternativeChosen)
		{
		    this.alternativeChosen = alternativeChosen;
		}


		public List<ModelAlternative> getAlternatives()
    {
        return alternatives;
    }

    public void setAlternatives(List<ModelAlternative> alternatives)
    {
        this.alternatives = alternatives;
    }

    public List<String> getOutParamList()
		{
		    return outParamList;
		}


		public void setOutParamList(List<String> outParamList)
		{
		    this.outParamList = outParamList;
		}

}
