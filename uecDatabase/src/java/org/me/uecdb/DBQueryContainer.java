package org.me.uecdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Data Base Query Container
 * @author SAV2
 * @version 1.0.0
 * @since 20.04.2015 12:48
 */
public class DBQueryContainer
{
    //own Constructor
    public DBQueryContainer(){}
        
    /**
    * initial context
    */
    private InitialContext ctx = null;
    /**
    * data source
    */
    private DataSource ds = null;
    /**
    * connection
    */
    private Connection con = null;
    /**
    * statement
    */
    private PreparedStatement st = null;
    /**
    * result set
    */
    private ResultSet rs = null;
    /**
    * data source String for opendbSQL
    */
    private static final String OPENDBSQL_DATA_SOURCE_STRING = "jdbc/opendbSQL";
    //private static final String OPENDBSQL_DATA_SOURCE_STRING = "jdbc/opendbSQL2";

    
    /**
    * Safe trim() method
    * @param value : <code>String</code>
    * @return either trimmed value or current value (if null or empty)
    */
    private String safeTrim(String value)
    {
        if((value!=null) && (value.length()>0))
        {
            return value.trim();
        }
        else
        {
            return value;
        }
    }
    
    /**
     * method for setting up values of Prepared Statement (SQL)
     * @param statement : <code>PreparedStatement</code>
     * @param statementValues : <code>ArrayList&lt;Object&gt;</code>
     * @throws SQLException 
     */
    private void setStatementValues(PreparedStatement statement, ArrayList<Object> statementValues) throws SQLException
    {
        if( statementValues==null || statement==null){return;}
        
        int i=0;//index number
        
        for(Object statementValue : statementValues)
        {
            if(statementValue instanceof Integer)//set Integer
            {
                statement.setInt(++i, (int)statementValue);
            }
            else if(statementValue instanceof String)//set String
            {
                statement.setString(++i, (String)statementValue);
            }
        }
    }
    
    /**
     * fill current LinkedHashMap with values
     * (1) if fields    IS NOT  null then values present multiple rows of data
     * (2) if fields    IS      null then values present 1 row of data
     * @param linkedHashMap : <code>LinkedHashMap&lt;String, Object&gt;</code>
     * @param fields : <code>ArrayList&lt;String&gt;</code>
     */
    private void fillLinkedHashMapValues(ResultSet resultSet, LinkedHashMap<String, Object> linkedHashMap, ArrayList<String> fields) throws SQLException
    {
        if(linkedHashMap==null){return;}

        if(fields!=null)
        {
            for(String key: linkedHashMap.keySet())
            {
                //try
                //{
                    if(linkedHashMap.get(key) instanceof String)
                    {
                        int size = fields.size();
                        for(String fieldLabel : fields)
                        {
                            if(--size==0)
                            {
                                linkedHashMap.put(key, linkedHashMap.get(key) + safeTrim(resultSet.getString(fieldLabel)) + "|");
                            }
                            else
                            {
                                linkedHashMap.put(key, linkedHashMap.get(key) + safeTrim(resultSet.getString(fieldLabel)) + "$");
                            }
                        }
                    }
                //}
                //catch(SQLException ex){}
            }
        }
        else
        {
            for(String key: linkedHashMap.keySet())
            {
                //try
                //{
                    if(linkedHashMap.get(key) instanceof String)
                    {
                        linkedHashMap.put(key, safeTrim(resultSet.getString(key)));
                    }
                    else if(linkedHashMap.get(key) instanceof byte[])
                    {
                        linkedHashMap.put(key, resultSet.getBytes(key));
                    }
                //}
                //catch(SQLException ex){}
            }
        }  
    }
    
    /**
     * make SQL query (SELECT, UPDATE, INSERT) and get returned values
     * @param sqlQueryType : <code>SQLQueryTypes</code>
     * @param queryString : <code>String</code>
     * @param statementValues : <code> ArrayList&lt;Object&gt;></code>
     * @param returnedValues : <code>QueryReturnedValues</code>
     * @param fields : <code>ArrayList<&lt;String&gt;</code>
     * @return returnVals : <code>QueryReturnedValues</code> set of values that are returned by query
     */
    QueryReturnedValues doQuery(SQLQueryTypes sqlQueryType, String queryString, ArrayList<Object> statementValues, QueryReturnedValues returnedValues, ArrayList<String> fields)
    {
        QueryReturnedValues returnVals = returnedValues;
        
        try
        {
            ctx = new InitialContext();
            ds = (DataSource)ctx.lookup(OPENDBSQL_DATA_SOURCE_STRING);//throws NamingException
            
            //throws SQLEXception (start):
            con = ds.getConnection();
            
            st = con.prepareStatement(queryString);
            
            setStatementValues(st, statementValues);
            
            if(sqlQueryType==SQLQueryTypes.SELECT)//SELECT
            {
                rs = st.executeQuery();
                
                int rows = 0;

                if( !rs.next() )
                {
                    returnVals.setResultSuccess("true");
                    returnVals.setResultDescription(""+rows);
                }
                else
                {
                    returnVals.setResultSuccess("true");
                    do
                    {
                        fillLinkedHashMapValues(rs, returnVals.getQueryValues(), fields);
                        rows++;
                    }
                    while( rs.next() );
                    returnVals.setResultDescription(""+rows);
                }
            }
            else if( (sqlQueryType==SQLQueryTypes.UPDATE) || (sqlQueryType==SQLQueryTypes.INSERT) )//UPDATE OR INSERT
            {
                int rows = st.executeUpdate();
                returnVals.setResultSuccess("true");
                returnVals.setResultDescription(""+rows);
            }
            //throws SQLEXception (end);
            
            return returnVals;
        }
        catch (NamingException ex)
        {
            returnVals.setResultSuccess("false");
            returnVals.setResultDescription("Can't create data source object, got Naming exception: " + ex); 
            
            return returnVals;
        }
        catch(SQLException ex)
        {
            returnVals.setResultSuccess("false");
            returnVals.setResultDescription("Got SQL Exception, the reason is: " + ex);
            
            return returnVals;
        }
        finally
        {
            //close current ResultSet
            try{if(rs!=null)rs.close();}catch(SQLException ex){}
            //close current Statement
            try{if(st!=null)st.close();}catch(SQLException ex){}
            //close current Connection
            try{if(con!=null)con.close();}catch(SQLException ex){}
        }
        
    }
}
