package edu.kit.ifv.mobitopp.actitopp;

import java.util.TreeMap;

/**
 * 
 * represents the information of a distribution loaded from the file system
 * 
 * @author Tim Hilgert
 *
 */
public class ModelDistributionInformation {
	
	private TreeMap<Integer, ModelDistributionElement> distributionElements;
	

	public ModelDistributionInformation()
	{
		distributionElements = new TreeMap<Integer, ModelDistributionElement>();
	}
	
	/**
	 * @return the distributionElements
	 */
	public TreeMap<Integer, ModelDistributionElement> getDistributionElements() {
		return distributionElements;
	}

	/**
	 * 
	 * @param name
	 * @param element
	 */
	public void addDistributionElement(Integer id, ModelDistributionElement element)
	{
		distributionElements.put(id, element);
	}

}
