package edu.kit.ifv.mobitopp.actitopp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 *
 * Objekt zur Speicherung der ModelFlow Listen für jeden Modelschritt
 * Werden in einer Modellflowmap zusammen mit der ID gespeichert 
 * 
 */
public class ModelFlowLists
{
  private Map<String, String> inParamMap;
  
  private List<String> outParamList;
  private List<String> alternativesList;

  /**
   * 
   * Konstruktor
   * 
   */
  public ModelFlowLists()
  {
    this.outParamList = new ArrayList<String>();
    this.alternativesList = new ArrayList<String>();
    this.inParamMap = new HashMap<String, String>();
  }

  public List<String> getOutParamList()
  {
    return outParamList;
  }

  public List<String> getAlternativesList()
  {
    return alternativesList;
  }

	/**
	 * @return the inParamMap
	 */
	public Map<String, String> getInParamMap() {
		return inParamMap;
	}

}
