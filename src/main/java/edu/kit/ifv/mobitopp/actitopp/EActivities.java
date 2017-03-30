package edu.kit.ifv.mobitopp.actitopp;

//TODO Klasse besser nutzen - Aktivitätstypen auf ENUM umbauen
// aktuell nicht genutzt

public enum EActivities
{
    WORK(1,0),
    EDUCATION(3,1),
    LEISURE(5,2),
    SHOPPING(41,3),
    TRANSPORT(6,4),
    HOME(7,5);
    
    private int mobiValue;
    private int hilgertValue;
    
    private EActivities(int mobiValue, int hilgertValue)
    {
        this.setMobiValue(mobiValue);
        this.setHilgertValue(hilgertValue);
    }

    public int getMobiValue()
    {
        return mobiValue;
    }

    public void setMobiValue(int mobiValue)
    {
        this.mobiValue = mobiValue;
    }

    public int getHilgertValue()
    {
        return hilgertValue;
    }

    public void setHilgertValue(int hilgertValue)
    {
        this.hilgertValue = hilgertValue;
    }
   
}
