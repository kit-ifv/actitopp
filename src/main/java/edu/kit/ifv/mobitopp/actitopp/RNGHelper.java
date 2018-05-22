package edu.kit.ifv.mobitopp.actitopp;

import java.util.Random;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class RNGHelper {
	
	// Seed des Zufallszahlengenerators
	private long seed;
	
	// Zufallszahlengenerator
  private Random rng;
  
  // letzte generierte Zufallszahl
  private double lastRandomValue;

  /**
   * 
   * Konstruktor
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
   * Rückgabe des letzten erzeugten Zufallszahlenwerts
   *   
   * @return
   */
  public double getLastRandomValue()
  {
    return lastRandomValue;
  }


  /**
   * 
   * Erzeugung einer Zufallszahl
   * 
   * @return
   */
  public double getRandomValue()
  {
  	// Zufallszahl erzeugen
  	double randomvalue = rng.nextDouble();
  	
  	// Zum späteren Zugriff zwischenspeichern
  	lastRandomValue = randomvalue;
  	
  	return randomvalue;
  }
  
  /**
   * 
   * Erzeugt eine ganzzahlige Zufallszahl für Personenwahl
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
   * @param interval
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
