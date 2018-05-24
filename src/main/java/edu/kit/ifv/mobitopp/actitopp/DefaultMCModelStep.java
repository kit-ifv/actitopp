package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * @author Tim Hilgert
 *
 * Objekt für einen Modell Schritt zur zufälligen Wahl aus einer Verteilung
 */
public class DefaultMCModelStep extends AbsHModelStep
{

//TODO Methoden checken und kommentieren
	
    private int chosenTime;
       
    //indicates which modified DTD should be used, -1 indicates that the original DTD should be used
    // 0 = W, 1 =E etc...
    private int useTypedModifiedDTD;
    // Legt fest, für welche Kategorie die Zeitverteilung modifiziert werden soll
    private int useCatModifiedDTD;
    // Legt den Zeitverteilungstyp fest 0=Aktdauern 1=Startzeiten
    private int DTDType;
    //limits the range in which a number should be picked randomly.
    //if one of these bounds is out of range of the distribution table, assume that there is no specific upper, respectivly, lowerbound in that case
    private int lowerBoundLimiter = -1;
    private int upperBoundLimiter = -1;
    
    private boolean modifyDTDAfterStep = false;
    
    public DefaultMCModelStep(String id, Coordinator modelCoordinator)
    {
        super(id, modelCoordinator);
        useTypedModifiedDTD = -1;
    }

    @Override
    protected int doStep()
    {
      //default case: use standard dtd with id 
      DiscreteTimeDistribution odtd = modelCoordinator.getFileBase().getTimeDistributionsMap().get(id);
      odtd = new DiscreteTimeDistribution(odtd); //copy to avoid modifications in the original table. any modifications should be places in arrays in the coordinator
      //choose modificated dtd if required
      if(useTypedModifiedDTD != -1)
      {
      	// Versuche die modifizierte Zeitverteilung zu laden
        DiscreteTimeDistribution dtd = modelCoordinator.getModifiedDTDs(DTDType)[useTypedModifiedDTD][useCatModifiedDTD];
        // Erstelle eine Kopie des Originals, falls noch keine modifizierte vorliegt
        if(dtd == null)
        {
            DiscreteTimeDistribution tmp = new DiscreteTimeDistribution(odtd);
            modelCoordinator.getModifiedDTDs(DTDType)[useTypedModifiedDTD][useCatModifiedDTD] = tmp;
            dtd =tmp;
        }
        // Arbeite jetzt mit der modifizierten bzw. kopierten Zeitverteilung weiter
        odtd = dtd;
      }  

      setChosenTime(odtd.getRandomPickFromDistribution(this.lowerBoundLimiter, this.upperBoundLimiter, modelCoordinator.getRandomGenerator()));
      
      // DEBUG USE ONLY
      if (Configuration.debugenabled)
      {
      	printDecisionProcess(odtd.getLastTempDTD());
      }
      
      if(modifyDTDAfterStep)
      {
          modifyDTD(odtd);
      }       
      return this.getChosenTime();
    }
    
    public void printDecisionProcess(DiscreteTimeDistribution dtd)
    {
    	System.out.println("--------------- MC-Simulation @ " + this.id +" ---------------");
        System.out.println("From " + this.lowerBoundLimiter + "| MIN in DTD: " + dtd.getStartPoint());
        System.out.println("To " + this.upperBoundLimiter + "| MAX in DTD: " + dtd.getEndPoint());
//        for(int i = 0; i < dtd.getDistributionAsSum().length;i++)
//        {
//            System.out.println("Slot: " + (i+dtd.getStartPoint()) + "| P (sum) " + dtd.getDistributionAsSum()[i]);
//        }
        System.out.println("Random Value: " + modelCoordinator.getRandomGenerator().getLastRandomValue());
        System.out.println("Chosen: " + chosenTime);
        System.out.println("");
    }
   
    /**
     * 
     * @param lowerbound
     * @param upperbound
     */
    public void setRangeBounds(int lowerbound, int upperbound)
    {
        this.lowerBoundLimiter = lowerbound;
        this.upperBoundLimiter = upperbound;
    }

    public void modifyDTD(DiscreteTimeDistribution dtd)
    {
        int chosenTimeIndex = this.getChosenTime() - dtd.getStartPoint();
        int modifier = (int) (0.5 * (dtd.getShareSum()));
        dtd.getShares()[chosenTimeIndex] += modifier;       
        dtd.reevaluateDistribution();
    }
    
    /**
     * this method should be invoked before doStep().
     * It indicates if a certain modified DTD should be used.
     * 
     * @param activityType
     * @param catToUse
     */
    public void setModifiedDTDtoUse(char activityType, int catToUse)
    {
        int feldindex=Configuration.ACTIVITY_TYPES.indexOf(activityType);
       
        this.useTypedModifiedDTD = feldindex;
        this.useCatModifiedDTD = catToUse;
    }
    
    /**
     * this method indicates that after the step is processed, the DTD will be modified and saved.
     * the method relies on a set "useModifiedDTD" value != -1. 
     * @param dtd
     */
    public void setModifyDTDAfterStep(boolean modifyDTDAfterStep)
    {
        this.modifyDTDAfterStep = modifyDTDAfterStep;
    }
    
    
    /**
     * Indicated which type of DTD will be used and modified in this step.
     * Types are according to steps, e.g.: step8 uses a different type of dtd than step10
     * 
     * 0 - Zeitverteilungen für Aktivitätendauern
     * 1 - Zeitverteilungen für Startzeiten
     * 
     * @param type
     */
    public void setDTDTypeToUse(int type)
    {
        this.DTDType = type;
    }

    public int getChosenTime()
    {
        return chosenTime;
    }

    public void setChosenTime(int chosenTime)
    {
        this.chosenTime = chosenTime;
    }

    public boolean isModifyDTDAfterStep()
    {
        return modifyDTDAfterStep;
    }

   

}
