package edu.kit.ifv.mobitopp.actitopp;

import java.util.TreeMap;

/**
 * 
 * represents the information of a distribution loaded from the file system
 * 
 * @author Tim Hilgert
 *
 */
public class WRDModelDistributionInformation {
	
	/*
	 * main information about the distribution
	 * contains all elements with an identifier (e.g. duration in minutes) and their amount based on empirical data
	 */
	private TreeMap<Integer, Integer> distributionElements;
	
	/**
	 * 
	 * constructor
	 *
	 */
	public WRDModelDistributionInformation()
	{
		distributionElements = new TreeMap<Integer, Integer>();
	}
	
	/**
	 * @return the distributionElements
	 */
	public TreeMap<Integer, Integer> getDistributionElements() 
	{
		return distributionElements;
	}
	
	/**
	 * 
	 * @param id
	 * @param amount
	 */
	public void addDistributionElement(Integer slot, Integer amount)
	{
		distributionElements.put(slot, amount);
	}

}
