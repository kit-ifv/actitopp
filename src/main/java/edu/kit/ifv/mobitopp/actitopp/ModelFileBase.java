package edu.kit.ifv.mobitopp.actitopp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Tim Hilgert
 * 
 * Used to load ALL property files before model execution.
 * This includes property files, model flow files, time distribution files and others
 *
 */
public class ModelFileBase
{
  // Map mit Objekten, die die Einstellungen der Parameter für die Steps enthalten (Model_flow csv Dateien)
  private Map<String, ModelFlowLists> modelFlowListsMap;
  // Map mit Objekten, die die Parameternamen und -werte der Steps enthalten (Params csv Dateien)
  private Map<String,Map<String, List<ModelParameterWeight>>> modelParameterWeightsMap;
  // Map mit Objekten, die die Zeitverteilungen der Steps enthalten (KAT csv Dateien)
  private Map<String,DiscreteTimeDistribution> timeDistributionsMap;
  // Map mit Objekten, die Estimates einfacher Regressionsmodelle enthalten
  private Map<String,Map<String, LinearRegressionEstimate>> linearregressionestimatesmap;
  
  /**
   * 
   * Konstruktor
   * 
   */
  public ModelFileBase()
  {
    super();
    this.modelFlowListsMap = new HashMap<String, ModelFlowLists>();
    this.modelParameterWeightsMap = new HashMap<String, Map<String,List<ModelParameterWeight>>>();
    this.timeDistributionsMap = new HashMap<String, DiscreteTimeDistribution>();
    this.linearregressionestimatesmap = new HashMap<String, Map<String, LinearRegressionEstimate>>();
    
    try
    {
    	// Initialisierungen
      initFlowLists();
      initWeights();
      initTimeDistributionLists();
      initLinearRegressionEstimates();
    }
    catch (IOException e)
    {
      e.printStackTrace();
			throw new RuntimeException();
    }
  }
    
    



	public Map<String, ModelFlowLists> getModelFlowListsMap()
	{
    return modelFlowListsMap;
	}


	public Map<String,DiscreteTimeDistribution> getTimeDistributionsMap()
	{
    return timeDistributionsMap;
	}

	public void setTimeDistributionsMap(Map<String,DiscreteTimeDistribution> timeDistributionMap)
	{
    this.timeDistributionsMap = timeDistributionMap;
	}

  public Map<String, Map<String, List<ModelParameterWeight>>> getModelParameterWeightsMap() 
  {
  	return modelParameterWeightsMap;
	}

	public void setModelParameterWeightsMap(Map<String, Map<String, List<ModelParameterWeight>>> modelParameterWeightsMap) 
	{
		this.modelParameterWeightsMap = modelParameterWeightsMap;
	}
	
	
	/**
	 * 
	 * Gibt das ModeFlow Objekt für den angefragten Step zurück
	 * 
	 * @param ID
	 * @return
	 */
	public ModelFlowLists getModelFlowLists(String ID)
    {
      ModelFlowLists lists = modelFlowListsMap.get(ID);
			assert lists != null : modelFlowListsMap.keySet();
      return lists;
    }
    
	/**
	 * 
	 * Gibt die ParameterListe für den angefragten Step zurück
	 * 
	 * @param ID
	 * @return
	 */
  public Map<String, List<ModelParameterWeight>> getmodelParameterWeightsList(String ID)
  {
    return modelParameterWeightsMap.get(ID);
  }
    
	/**
	 * 
	 * Gibt das Objekt der Zeitverteilung für den angefragten Step zurück
	 * 
	 * @param ID
	 * @return
	 */
  public DiscreteTimeDistribution getTimeDistribution(String ID)
  {
    return timeDistributionsMap.get(ID);
  }
  
  /**
   * 
   * Gibt die Map der Regressionsestimates für die gewünschte Regression
   * 
   * @param regressionname
   * @return
   */
  public Map<String, LinearRegressionEstimate> getLinearRegressionEstimates(String regressionname)
  {
  	return linearregressionestimatesmap.get(regressionname);
  }
    
	/**
   * 
   * Initialisierung der Model-Flow-Listen (für Logit-Steps)
   * 
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void initFlowLists() throws FileNotFoundException, IOException
  {
  	// Angabe der Stufen, die Flowlisten für Logit-Modellierungen verwenden
    String[] toInitialize = Configuration.flowlist_initials;

    // Initialisierung der Listen aller Stufen
    for (String s : toInitialize)
    {
      String sourceLocation = Configuration.parameterset + "/" + s + "model_flow.csv";
      CSVModelFlowListsLoader loader = new CSVModelFlowListsLoader();
			try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) {
				ModelFlowLists lists = loader.loadLists(input);
				modelFlowListsMap.put(s, lists);
			}
    }
  }

  /**
	 * 
	 * Initialisierung der Parameter für Logit-Steps
   * @throws IOException 
	 * 
	 */
	private void initWeights() throws IOException
	{
    Set<String> keys = this.getModelFlowListsMap().keySet();

    for (String keyString : keys)
    {
    	// Referenz auf eine Modelflowliste des spezifischen Schritts
      ModelFlowLists flow = this.getModelFlowLists(keyString);
      
      String sourceLocation = Configuration.parameterset + "/"+ keyString +"Params.csv";
      CSVParameterWeightLoader weightLoader = new CSVParameterWeightLoader();
      //Map: (alternative --> list of utility weights)
			try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) {
				Map<String, List<ModelParameterWeight>> wm = weightLoader.getWeightValues(input,
						new ArrayList<String>(flow.getInParamMap().keySet()), flow.getAlternativesList());
				modelParameterWeightsMap.put(keyString, wm);
			}
    }
	}
	
	/**
   * 
   * Initialisierung der Zeitverteilungen (für MC-Steps)
   * 
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void initTimeDistributionLists() throws FileNotFoundException, IOException
  {
  	// Angabe der Stufen, die Zeitverteilungen verwenden
    String[] toInitialize = Configuration.timedistributions_initials;
    
    // Angabe der maximalen Kategorie-Indizes
    int[] toInitializeMaxIndizes = Configuration.timeDistributions_MaxIndizes;

    // Initialisierung der Listen aller Stufen und Kategorien
    for(int i = 0; i < toInitialize.length;i++)
    {
      for(int j = 0; j <= toInitializeMaxIndizes[i];j++)
      {	
        String sourceLocation = Configuration.parameterset + "/"+ toInitialize[i] +"_KAT_"+ j +".csv";
        CSVTimeDistributionLoader loader = new CSVTimeDistributionLoader();
				try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) {
					DiscreteTimeDistribution dtd = loader.loadDistribution(input);
					// ! just use the ID and the index as identifier
					timeDistributionsMap.put((toInitialize[i] + j), dtd);
				}
      }
    }
  }
  
  /**
   * 
   * Initialisierung der Estimates für einfache Regressionsparameter
   * @throws IOException
   */
  private void initLinearRegressionEstimates() throws IOException
  {
  	// Angabe der Dateinmanen bzw. Schritte, die einfache Regressionsmodelle verwenden
    String[] toInitialize = Configuration.linearregressionestimates_filenames;
 
    // Initialisierung der Listen aller Dateien
    for(int i = 0; i < toInitialize.length;i++)
    {   
      String sourceLocation = Configuration.parameterset + "/"+ toInitialize[i] +".csv";
      CSVLinearRegressionEstimatesLoader loader = new CSVLinearRegressionEstimatesLoader();
			try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) {
				Map<String, LinearRegressionEstimate> tmpmap = loader.getEstimates(input);
				linearregressionestimatesmap.put(toInitialize[i], tmpmap);
			}
    }
	}
 
}
