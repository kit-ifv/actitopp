package edu.kit.ifv.mobitopp.actitopp;

public abstract class AbsHModelStep
{

    protected String id;
    protected Coordinator modelCoordinator;

    public AbsHModelStep(String id, Coordinator modelCoordinator)
    {
        super();

        this.id = id;
        this.modelCoordinator = modelCoordinator;

    }
        
    protected abstract int doStep() throws IllegalArgumentException;

}
