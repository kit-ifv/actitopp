package edu.kit.ifv.mobitopp.actitopp;

public abstract class AbsHModelStep
{

    protected String id;
    
    public AbsHModelStep(String id)
    {
        super();
        this.id = id;
    }
        
    protected abstract int doStep() throws IllegalArgumentException;

}
