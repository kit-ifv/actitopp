package edu.kit.ifv.mobitopp.actitopp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

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
 
 
  private HashMap<String, ModellnformationDC> modelInformationDCsteps;
  private HashMap<String, ModellnformationWRD> modelInformationWRDsteps;
  
  // Map mit Objekten, die Estimates einfacher Regressionsmodelle enthalten
  private HashMap<String,HashMap<String, LinearRegressionEstimate>> linearregressionestimatesmap;
  
  
  /**
   * 
   * Konstruktor
   * 
   */
  public ModelFileBase()
  {
    super();
    
    this.modelInformationDCsteps = new HashMap<String, ModellnformationDC>();
    this.modelInformationWRDsteps = new HashMap<String, ModellnformationWRD>();
    this.linearregressionestimatesmap = new HashMap<String, HashMap<String, LinearRegressionEstimate>>();
  
    try
    {
    	// Initialisierungen
    	initDCStepInformation();
    	initDCStepParameters();
    	initWRDSteps();
    	
      initLinearRegressionEstimates();
    }
    catch (IOException e)
    {
      e.printStackTrace();
			throw new RuntimeException();
    }
  }
  
  
  /**
   * 
   * returns {@link ModellnformationDC} object for specific id
   * 
   * @param modelstepid
   * @return
   */
  public ModellnformationDC getModelInformationforDCStep(String modelstepid)
  {
  	return modelInformationDCsteps.get(modelstepid);
  }
    
  
  /**
   * 
   * returns {@link ModellnformationWRD} object for specific id
   * 
   * @param modelstepid
   * @return
   */
  public ModellnformationWRD getModelInformationforWRDStep(String modelstepid)
  {
  	return modelInformationWRDsteps.get(modelstepid);
  }
    
  
  /**
   * 
   * Gibt die Map der Regressionsestimates für die gewünschte Regression
   * 
   * @param regressionname
   * @return
   */
  public HashMap<String, LinearRegressionEstimate> getLinearRegressionEstimates(String regressionname)
  {
  	return linearregressionestimatesmap.get(regressionname);
  }
    
	/**
   * 
   * read all relevant model flow information from files fpor dc steps
   * 
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void initDCStepInformation() throws FileNotFoundException, IOException
  {
    for (String s : Configuration.dcsteps_ids)
    {
      String sourceLocation = Configuration.parameterset + "/" + s + "model_flow.csv";
      CSVModelFlowInformationLoader loader = new CSVModelFlowInformationLoader();
			try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) {
				
				// Creates ModelInformationOject
				ModellnformationDC modelStep = new ModellnformationDC();
				// Load ParameterNames, Contexts and Alternatives
				loader.loadModelFlowData(input, modelStep);
				// Adds the modelinformation to the map
				modelInformationDCsteps.put(s, modelStep);
			}
    }
  }
  
  /**
	 * 
	 * Initialisierung der Parameter für Logit-Steps
   * @throws IOException 
	 * 
	 */
	private void initDCStepParameters() throws IOException
	{
		// parameters need to be available for all DC model steps
    for (String keyString : modelInformationDCsteps.keySet())
    {
    	// Referenz auf eine Modelflowliste des spezifischen Schritts
      ModellnformationDC modelstep = modelInformationDCsteps.get(keyString);
      
      String sourceLocation = Configuration.parameterset + "/"+ keyString +"Params.csv";
      CSVParameterLoader parameterLoader = new CSVParameterLoader();
      
			try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) 
			{
				parameterLoader.loadParameterValues(input, modelstep);		
			}
    }
	}
  
	/**
   * 
   * initialize weighted random draw steps
   * 
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void initWRDSteps() throws FileNotFoundException, IOException
  {
    // initialize all steps and all categories for each step
    for(Entry<String, Integer> mapentry : Configuration.wrdsteps.entrySet())
    {
    	String stepid = mapentry.getKey();
    	int maxinidex = mapentry.getValue();
    	
    	ModellnformationWRD modelstep = new ModellnformationWRD();
    	modelInformationWRDsteps.put(stepid, modelstep);
    	
      for(int index = 0; index <= maxinidex; index++)
      {	
        String sourceLocation = Configuration.parameterset + "/"+ stepid +"_KAT_"+ index +".csv";
        CSVWRDDistributionLoader loader = new CSVWRDDistributionLoader();
        
				try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) {
					ModelDistributionInformation wrddist = loader.loadDistributionInformation(input);
					modelstep.addDistributionInformation(String.valueOf(index), wrddist);
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
				HashMap<String, LinearRegressionEstimate> tmpmap = loader.getEstimates(input);
				linearregressionestimatesmap.put(toInitialize[i], tmpmap);
			}
    }
	}
 
}
