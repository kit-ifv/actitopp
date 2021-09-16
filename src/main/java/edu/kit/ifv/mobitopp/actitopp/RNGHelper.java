package edu.kit.ifv.mobitopp.actitopp;

import java.util.Random;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class RNGHelper {
	
	private long seed;
  private Random rng;
  private double lastRandomValue;

  /**
   * 
   * constructor
   * 
   * @param seed
   */
  public RNGHelper (long seed)
  {
  	rng = new Random(seed);
  	this.seed = seed;
  }
  
  
  /**
   * 
   * @return
   */
  public long getSeed()
  {
  	return seed;
  }
  
  
  /**
   *   
   * @return
   */
  public double getLastRandomValue()
  {
    return lastRandomValue;
  }


  /**
   * 
   * @return
   */
  public double getRandomValue()
  {
  	// create randomValue
  	double randomvalue = rng.nextDouble();
  	
  	// Save for access possibility
  	lastRandomValue = randomvalue;
  	
  	return randomvalue;
  }
  
  /**
   * 
   * creates a random key between 0 and bound
   * used to draw a random person out of a list 
   * 
   * @param bound
   * @return
   */
  public int getRandomPersonKey(int bound)
  {
  	return rng.nextInt(bound);
  }
  
  
  /**
   * get random from range (from...to) with the specified "size" of the steps
   * uniform distribution!
   * 
   * @param from
   * @param to
   * @param stepSize
   * @return
   */
  public int getRandomValueBetween(int from, int to, int stepSize)
  {
    if (from > to) throw new IllegalArgumentException("FROM bigger than TO");
    int steps = (to - from) / stepSize;
    int[] range = new int[steps + 1];
    for (int i = 0; i < steps; i++)
    {
      range[i] = from + (i * stepSize);
    }
    range[steps] = to;
   
    int rangeSize = range.length - 1;
    int result = range[rng.nextInt(rangeSize - 0 + 1) + 0];
          
    return result;
  }
  
  
}
