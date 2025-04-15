package edu.kit.ifv.mobitopp.actitopp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
	
	private static interface Input {
		InputStream newInputStream(String name) throws IOException;
	}
	
	private static class FileInput implements Input {
		
		private final File basePath;

		public FileInput(File basePath) {
			super();
			this.basePath = basePath;
		}

		@Override
		public InputStream newInputStream(String name) throws IOException {
			System.out.println("loading file: " + name + " from parameter set " + basePath.getAbsolutePath());
			return Files.newInputStream(new File(this.basePath, name).toPath());
		}
	}
	
	private static class JarInput implements Input {
		private final String parameterset;
		
		public JarInput(String parameterset) {
			super();
			this.parameterset = parameterset;
		}

		@Override
		public InputStream newInputStream(String name) {
			System.out.println("loading file from JAR: " + name + " from parameter set " + parameterset);
			return ModelFileBase.class.getResourceAsStream(this.parameterset + "/" + name);
		}
	}
	
  private HashMap<String, DCModelSteplnformation> modelInformationDCsteps;
  private HashMap<String, WRDModelSteplnformation> modelInformationWRDsteps;
  private HashMap<String,HashMap<String, LinRegEstimate>> linearregressionestimatesmap;

	private Input inputType;
  
  
  /**
   * 
   * Constructor with standard parameters (using Configuration values)
   * 
   */
  public ModelFileBase()
  {
  	this(Configuration.parameterset);  	
  }
  
  /**
   * 
   * Constructor with custom parameter set (using Configuration values for step information)
   * 
   */
  public ModelFileBase(String parameterset)
  {
  	this(new JarInput(parameterset), Configuration.dcsteps, Configuration.wrdsteps, Configuration.linregsteps_filenames);  	
  }
  
  /**
   * 
   * Constructor with custom parametersets and step information
   * 
   * @param basePath
   * @param dcsteps
   * @param wrdsteps
   * @param linregsteps_filenames
   */
  public ModelFileBase(File basePath, HashSet<String> dcsteps, HashMap<String, Integer> wrdsteps, HashSet<String> linregsteps_filenames)
  {
    this(new FileInput(basePath), dcsteps, wrdsteps, linregsteps_filenames);
  }
  
  private ModelFileBase(Input inputType, HashSet<String> dcsteps, HashMap<String, Integer> wrdsteps, HashSet<String> linregsteps_filenames) {
    this.inputType = inputType;
    
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
      try (InputStream input = newInputStream(s + "model_flow")) {
				
				CSVDCModelInformationLoader loader = new CSVDCModelInformationLoader();
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
      
      try (InputStream input = newInputStream(keyString +"Params")) 
			{
				CSVDCParameterLoader parameterLoader = new CSVDCParameterLoader();
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
        try (InputStream input = newInputStream(stepid +"_KAT_"+ index)) 
				{
					CSVWRDDistributionLoader loader = new CSVWRDDistributionLoader();
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
    for(String name : linregsteps_filenames)
    {   
      try (InputStream input = newInputStream(name)) 
			{
				CSVLinRegEstimatesLoader loader = new CSVLinRegEstimatesLoader();
				HashMap<String, LinRegEstimate> tmpmap = loader.getEstimates(input);
				linearregressionestimatesmap.put(name, tmpmap);
			}
    }
	}

  /**
   * 
   * @param name
   * @return
   * @throws IOException
   */
	private InputStream newInputStream(String name) throws IOException {
		return inputType.newInputStream(name + ".csv");
	}
 
}
