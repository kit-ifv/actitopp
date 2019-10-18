package edu.kit.ifv.mobitopp.actitopp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
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
	
	private String parameterset;
	
  private HashMap<String, DCModelSteplnformation> modelInformationDCsteps;
  private HashMap<String, WRDModelSteplnformation> modelInformationWRDsteps;
  private HashMap<String,HashMap<String, LinRegEstimate>> linearregressionestimatesmap;
  
  
  /**
   * 
   * Constructor with standard parameters (using Configuration values)
   * 
   */
  public ModelFileBase()
  {
  	this(Configuration.parameterset, Configuration.dcsteps, Configuration.wrdsteps, Configuration.linregsteps_filenames);  	
  }
  
  
  /**
   * 
   * Constructor to enable custom parametersets or step information
   * 
   * @param parameterset
   * @param dcsteps
   * @param wrdsteps
   * @param linregsteps_filenames
   */
  public ModelFileBase(String parameterset, HashSet<String> dcsteps, HashMap<String, Integer> wrdsteps, HashSet<String> linregsteps_filenames)
  {
    super();
  
    this.parameterset = parameterset;
    
    this.modelInformationDCsteps = new HashMap<String, DCModelSteplnformation>();
    this.modelInformationWRDsteps = new HashMap<String, WRDModelSteplnformation>();
    this.linearregressionestimatesmap = new HashMap<String, HashMap<String, LinRegEstimate>>();
  
    try
    {
    	// Initializations
    	if (dcsteps!=null) 
    	{
    		initDCStepInformation(dcsteps);
    		initDCStepParameters(dcsteps);
    	}
    	if (wrdsteps != null)
    	{
    		initWRDSteps(wrdsteps);
    	}
    	if (linregsteps_filenames != null)
      {
    		initLinearRegressionEstimates(linregsteps_filenames);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
			throw new RuntimeException();
    }
  }
  
  
  /**
   * 
   * returns {@link DCModelSteplnformation} object for specific id
   * 
   * @param modelstepid
   * @return
   */
  public DCModelSteplnformation getModelInformationforDCStep(String modelstepid)
  {
  	return modelInformationDCsteps.get(modelstepid);
  }
    
  
  /**
   * 
   * returns {@link WRDModelSteplnformation} object for specific id
   * 
   * @param modelstepid
   * @return
   */
  public WRDModelSteplnformation getModelInformationforWRDStep(String modelstepid)
  {
  	return modelInformationWRDsteps.get(modelstepid);
  }
    
  
  /**
   * 
   * return linear regression estimated map for specified regressionname
   * 
   * @param regressionname
   * @return
   */
  public HashMap<String, LinRegEstimate> getLinearRegressionEstimates(String regressionname)
  {
  	return linearregressionestimatesmap.get(regressionname);
  }
    
/**
 * 
 * read all relevant model flow information from files for dc steps
 * 
 * @param dcsteps
 * @throws FileNotFoundException
 * @throws IOException
 */
  private void initDCStepInformation(HashSet<String> dcsteps) throws FileNotFoundException, IOException
  {
    for (String s : dcsteps)
    {
      String sourceLocation = parameterset + "/" + s + "model_flow.csv";
      CSVDCModelInformationLoader loader = new CSVDCModelInformationLoader();
			try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) {
				
				// Creates ModelInformationOject
				DCModelSteplnformation modelStep = new DCModelSteplnformation();
				// Load ParameterNames, Contexts and Alternatives
				loader.loadModelFlowData(input, modelStep);
				// Adds the modelinformation to the map
				modelInformationDCsteps.put(s, modelStep);
			}
    }
  }
  
	/**
	 * 
	 * read all relevant parameter from files for dc steps 
	 * 
	 * @param dcsteps
	 * @throws IOException
	 */
	private void initDCStepParameters(HashSet<String> dcsteps) throws IOException
	{
		// parameters need to be available for all DC model steps
    for (String keyString : dcsteps)
    {
      DCModelSteplnformation modelstep = modelInformationDCsteps.get(keyString);
      
      String sourceLocation = parameterset + "/"+ keyString +"Params.csv";
      CSVDCParameterLoader parameterLoader = new CSVDCParameterLoader();
      
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
	 * @param wrdsteps
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
  private void initWRDSteps(HashMap<String, Integer> wrdsteps) throws FileNotFoundException, IOException
  {
    // initialize all steps and all categories for each step
    for(Entry<String, Integer> mapentry : wrdsteps.entrySet())
    {
    	String stepid = mapentry.getKey();
    	int maxinidex = mapentry.getValue();
    	
    	WRDModelSteplnformation modelstep = new WRDModelSteplnformation();
    	modelInformationWRDsteps.put(stepid, modelstep);
    	
      for(int index = 0; index <= maxinidex; index++)
      {	
        String sourceLocation = parameterset + "/"+ stepid +"_KAT_"+ index +".csv";
        CSVWRDDistributionLoader loader = new CSVWRDDistributionLoader();
        
				try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) 
				{
					WRDModelDistributionInformation wrddist = loader.loadDistributionInformation(input);
					modelstep.addDistributionInformation(String.valueOf(index), wrddist);
				}
      }
    }
  }
  
  /**
   * 
   * initialize estimated for linear regression steps
   * 
   * @param linregsteps_filenames
   * @throws IOException
   */
  private void initLinearRegressionEstimates(HashSet<String> linregsteps_filenames) throws IOException
  {
    for(String s : linregsteps_filenames)
    {   
      String sourceLocation = parameterset + "/"+ s +".csv";
      CSVLinRegEstimatesLoader loader = new CSVLinRegEstimatesLoader();
			try (InputStream input = ModelFileBase.class.getResourceAsStream(sourceLocation)) 
			{
				HashMap<String, LinRegEstimate> tmpmap = loader.getEstimates(input);
				linearregressionestimatesmap.put(s, tmpmap);
			}
    }
	}
 
}
