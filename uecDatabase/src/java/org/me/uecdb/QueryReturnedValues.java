package org.me.uecdb;

import java.util.LinkedHashMap;

/**
 * Query Returned Values
 * @author SAV2
 * @version 1.0.0
 * @since 20.04.2015 12:46
 */
public class QueryReturnedValues 
{ 
    private String resultSuccess = null;
    private String resultDescription = null;
    
    private LinkedHashMap<String, Object> queryValues = null;
    
    //own constructor
    public QueryReturnedValues(LinkedHashMap<String, Object> queryVals)
    {       
        this.queryValues = queryVals;
    }

    public String getResultSuccess()
    {
        return this.resultSuccess;
    }
    
    public String getResultDescription()
    {
        return this.resultDescription;
    }
    
    public LinkedHashMap<String, Object> getQueryValues()
    {
        return this.queryValues;
    }
   
    public void setResultSuccess(String resSuc)
    {
        this.resultSuccess = resSuc;
    }
    
    public void setResultDescription(String resDes)
    {
        this.resultDescription = resDes;
    }
    
    public void setQueryValues(LinkedHashMap<String, Object> queryVals)
    {
        this.queryValues = queryVals;
    }
}
