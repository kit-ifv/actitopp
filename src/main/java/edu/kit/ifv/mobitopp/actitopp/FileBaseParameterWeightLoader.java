package edu.kit.ifv.mobitopp.actitopp;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class FileBaseParameterWeightLoader
{
    
    private ModelFileBase fileBase;
    
    
    public FileBaseParameterWeightLoader(ModelFileBase fileBase)
    {
        this.fileBase = fileBase;
    }
    

    public Map<String, List<ModelParameterWeight>> getWeightValues(String sourceLocation) throws IllegalArgumentException
    {        
        Map<String, List<ModelParameterWeight>> m = fileBase.getmodelParameterWeightsList(sourceLocation);
        if(m == null) throw new IllegalArgumentException("Weight Map not Found in File Base: ID: " + sourceLocation);
        
        return m;
    }
    

    public ModelFileBase getFileBase()
    {
        return fileBase;
    }


    public void setFileBase(ModelFileBase fileBase)
    {
        this.fileBase = fileBase;
    }

}
