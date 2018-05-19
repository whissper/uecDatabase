package org.me.uecdb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 * UEC web-service
 * @author SAV2
 * @version 1.0.2
 * @since 03.05.2015 23:05
 */
@WebService(serviceName = "uecDBWS")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class uecDB
{
    @Resource
    WebServiceContext wsc;
    
    //own Constructor
    public uecDB(){}
    
    //check for authentification
    private boolean isAuth()
    {
        MessageContext mc = wsc.getMessageContext();
        Map requestHeader = (Map) mc.get(MessageContext.HTTP_REQUEST_HEADERS);
        List userList = (List) requestHeader.get("Username");
        List passList = (List) requestHeader.get("Password");
        String username = "";
        String password = "";
        if(userList != null && passList != null)
        {
            username = (String)userList.get(0);
            password = (String)passList.get(0);
        }
        return ("userasadmin".equals(username) && "JcjUblGjcByc".equals(password));
    }
    
    //***** STUDENT ------------------------------------ START: ------------------------------------ *****

    /**
     * Select-Query for Authentication
     * @param hashValue : <code>String</code> -IN
     * @param regnomValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param studentRegnomVal : <code>String</code> -OUT
     * @param studentFioVal : <code>String</code> -OUT
     * @param studentUnittVal : <code>String</code> -OUT
     * @param studentGrouppVal : <code>String</code> -OUT
     * @param studentUnittIdVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getAuth")
    //@WebResult(name = "XMLReturnMessage")
    public void selectQueryForAuth2(@WebParam(name = "hashvalue", mode = WebParam.Mode.IN) String hashValue,
                                    @WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                    @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                    @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                    @WebParam(name = "studentRegnom", mode = WebParam.Mode.OUT) Holder<String> studentRegnomVal,
                                    @WebParam(name = "studentFio", mode = WebParam.Mode.OUT) Holder<String>  studentFioVal,
                                    @WebParam(name = "studentUnitt", mode = WebParam.Mode.OUT) Holder<String> studentUnittVal,
                                    @WebParam(name = "studentGroupp", mode = WebParam.Mode.OUT) Holder<String> studentGrouppVal,
                                    @WebParam(name = "studentUnittId", mode = WebParam.Mode.OUT) Holder<String> studentUnittIdVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT st.regnom, st.fio, unitt.unitt, st.groupp, st.unitt AS unitt_id " +
                             "FROM student AS st LEFT JOIN unitt ON unitt.id = st.unitt " +
                                                "LEFT JOIN auth ON st.regnom = auth.regnom " +
                             "WHERE auth.hash = ? AND auth.regnom = ? ";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(hashValue);
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("regnom", "");
        queryValues.put("fio", "");
        queryValues.put("unitt", "");
        queryValues.put("groupp", "");
        queryValues.put("unitt_id", "");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  null);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        studentRegnomVal.value = (String)queryReturnVals.getQueryValues().get("regnom");
        studentFioVal.value = (String)queryReturnVals.getQueryValues().get("fio");
        studentUnittVal.value = (String)queryReturnVals.getQueryValues().get("unitt");
        studentGrouppVal.value = (String)queryReturnVals.getQueryValues().get("groupp");
        studentUnittIdVal.value = (String)queryReturnVals.getQueryValues().get("unitt_id");
        
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for Register in Authentication system (write hash value for current student) 
     * @param hashValue : <code>String</code> -IN
     * @param regnomValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param studentRegnomVal : <code>String</code> -OUT
     * @param studentFioVal : <code>String</code> -OUT
     * @param studentUnittVal : <code>String</code> -OUT
     * @param studentGrouppVal : <code>String</code> -OUT
     * @param studentUnittIdVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getRegAuth")
    public void selectQueryForRegAuth(@WebParam(name = "hashvalue", mode = WebParam.Mode.IN) String hashValue,
                                      @WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                      @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                      @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                      @WebParam(name = "studentRegnom", mode = WebParam.Mode.OUT) Holder<String> studentRegnomVal,
                                      @WebParam(name = "studentFio", mode = WebParam.Mode.OUT) Holder<String>  studentFioVal,
                                      @WebParam(name = "studentUnitt", mode = WebParam.Mode.OUT) Holder<String> studentUnittVal,
                                      @WebParam(name = "studentGroupp", mode = WebParam.Mode.OUT) Holder<String> studentGrouppVal,
                                      @WebParam(name = "studentUnittId", mode = WebParam.Mode.OUT) Holder<String> studentUnittIdVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "UPDATE dbo.student SET dbo.student.hash = ? WHERE dbo.student.regnom = ?";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(hashValue);
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.UPDATE, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  null);
        
        String resSucVal = queryReturnVals.getResultSuccess();
        String resDesVal = queryReturnVals.getResultDescription();
        
        queryString = "SELECT st.regnom, st.fio, dbo.unitt.unitt, st.groupp, st.unitt AS unitt_id " +
                      "FROM dbo.student AS st " +
                      "LEFT JOIN dbo.unitt ON dbo.unitt.id = st.unitt " +
                      "WHERE st.hash = ? AND st.regnom = ?";
        
        queryValues.put("regnom", "");
        queryValues.put("fio", "");
        queryValues.put("unitt", "");
        queryValues.put("groupp", "");
        queryValues.put("unitt_id", "");
        
        queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                              queryString, 
                                              statementValues, 
                                              new QueryReturnedValues(queryValues),
                                              null);
        
        resSucVal += "|" + queryReturnVals.getResultSuccess();
        resDesVal += "|" + queryReturnVals.getResultDescription();
        
        resultSuccessVal.value = resSucVal;
        resultDescriptionVal.value = resDesVal;
        studentRegnomVal.value = (String)queryReturnVals.getQueryValues().get("regnom");
        studentFioVal.value = (String)queryReturnVals.getQueryValues().get("fio");
        studentUnittVal.value = (String)queryReturnVals.getQueryValues().get("unitt");
        studentGrouppVal.value = (String)queryReturnVals.getQueryValues().get("groupp");
        studentUnittIdVal.value = (String)queryReturnVals.getQueryValues().get("unitt_id");
                
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for getting MarkList
     * @param regnomValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param markListData : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getMarkList")
    public void selectQueryForMarkList(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                       @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                       @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                       @WebParam(name = "markList", mode = WebParam.Mode.OUT) Holder<String> markListData)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT dbo.BASOC.SEMESTR AS semestr," +
                             " dbo.PREDMET.ZNACHENIE AS predmet," + 
                             " dbo.VID_KONT.ZNACHENIE AS vidkont," +
                             " dbo.PREPOD.ZNACHENIE AS prepod," +
                             " dbo.BASOC.OCENKA AS ocenka," +
                             " dbo.BASOC.OCENKA2," +
                             " dbo.BASOC.OCENKA3," +
                             " dbo.BASOC.OCENKA_F" +
                             " FROM dbo.BASOC" +
                             " LEFT JOIN dbo.PREDMET ON dbo.PREDMET.SHIFR = dbo.BASOC.PREDMET" +
                             " LEFT JOIN dbo.PREPOD ON dbo.PREPOD.KOD = dbo.BASOC.PREPOD" +
                             " LEFT JOIN dbo.VID_KONT ON dbo.VID_KONT.SHIFR = dbo.BASOC.VID_KONTR" +
                             " WHERE dbo.BASOC.REGNOM = ?" +
                             " ORDER BY semestr, predmet";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("markListMessage", "");
        
        ArrayList<String> fields = new ArrayList<>();
        fields.add("semestr");
        fields.add("predmet");
        fields.add("vidkont");
        fields.add("prepod");
        fields.add("ocenka");
        fields.add("OCENKA2");
        fields.add("OCENKA3");
        fields.add("OCENKA_F");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  fields);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        markListData.value = (String)queryReturnVals.getQueryValues().get("markListMessage");
        
        dbQueryCont = null;
    }
    
    /**
     * Insert-Query for setting Question
     * @param regnomValue : <code>String</code> -IN
     * @param questionValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "setQuestion")
    public void insertQueryForQuestion(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                       @WebParam(name = "question", mode = WebParam.Mode.IN) String questionValue,
                                       @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                       @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "INSERT INTO dbo.question (dbo.question.regnom, dbo.question.question) VALUES (?, ?)";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(regnomValue);
        statementValues.add(questionValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.INSERT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  null);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for getting News
     * @param unittIdValue : <code>int</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param newsValue : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getNews")
    public void selectQueryForGetNews(@WebParam(name = "unitt_id", mode = WebParam.Mode.IN) int unittIdValue,
                                      @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                      @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                      @WebParam(name = "news", mode = WebParam.Mode.OUT) Holder<String> newsValue)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT dbo.news.news, dbo.news.date_n, dbo.status.status " +
                             "FROM dbo.news " +
                             "LEFT JOIN dbo.status on dbo.news.status = dbo.status.id " +
                             "WHERE dbo.news.unitt = ? " +
                             "ORDER BY dbo.status.status";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(unittIdValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("newsMessage", "");
        
        ArrayList<String> fields = new ArrayList<>();
        fields.add("news");
        fields.add("date_n");
        fields.add("status");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  fields);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        newsValue.value = (String)queryReturnVals.getQueryValues().get("newsMessage");
        
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for password Authentication
     * @param regnomValue : <code>String</code> -IN
     * @param passhashValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param studentRegnomVal : <code>String</code> -OUT
     * @param studentFioVal : <code>String</code> -OUT
     * @param studentUnittVal : <code>String</code> -OUT
     * @param studentGrouppVal : <code>String</code> -OUT
     * @param studentUnittIdVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getPasswordAuth")
    public void selectQueryForPasswordAuth(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                           @WebParam(name = "passhash", mode = WebParam.Mode.IN) String passhashValue,
                                           @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                           @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                           @WebParam(name = "studentRegnom", mode = WebParam.Mode.OUT) Holder<String> studentRegnomVal,
                                           @WebParam(name = "studentFio", mode = WebParam.Mode.OUT) Holder<String>  studentFioVal,
                                           @WebParam(name = "studentUnitt", mode = WebParam.Mode.OUT) Holder<String> studentUnittVal,
                                           @WebParam(name = "studentGroupp", mode = WebParam.Mode.OUT) Holder<String> studentGrouppVal,
                                           @WebParam(name = "studentUnittId", mode = WebParam.Mode.OUT) Holder<String> studentUnittIdVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT st.regnom, st.fio, unitt.unitt, st.groupp, st.unitt AS unitt_id " +
                             "FROM student AS st LEFT JOIN unitt ON unitt.id = st.unitt " +
                                                "LEFT JOIN auth ON st.regnom = auth.regnom " +
                             "WHERE auth.pass = ? AND auth.regnom = ? ";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(passhashValue);
        statementValues.add(regnomValue);
        
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("regnom", "");
        queryValues.put("fio", "");
        queryValues.put("unitt", "");
        queryValues.put("groupp", "");
        queryValues.put("unitt_id", "");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  null);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        studentRegnomVal.value = (String)queryReturnVals.getQueryValues().get("regnom");
        studentFioVal.value = (String)queryReturnVals.getQueryValues().get("fio");
        studentUnittVal.value = (String)queryReturnVals.getQueryValues().get("unitt");
        studentGrouppVal.value = (String)queryReturnVals.getQueryValues().get("groupp");
        studentUnittIdVal.value = (String)queryReturnVals.getQueryValues().get("unitt_id");
        
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for getting Orders *FAM*
     * @param regnomValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param studentMessageVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getOrderFam")
    public void selectQueryForOrderFam(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                       @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                       @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                       @WebParam(name = "studentMessage", mode = WebParam.Mode.OUT) Holder<String> studentMessageVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT CONVERT(varchar(10), dbo.KSFAM.DATAPR, 104) AS DATAPR, dbo.KSFAM.FAMILIA, dbo.KSFAM.FAMILIAS, dbo.KSFAM.N_PRIKAZA " +
                             "FROM dbo.KSFAM WHERE dbo.KSFAM.REGNOM = ? AND dbo.KSFAM.AKT = 'Y'";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("studentMessage", "");
        
        ArrayList<String> fields = new ArrayList<>();
        fields.add("N_PRIKAZA");
        fields.add("DATAPR");
        fields.add("FAMILIAS");
        fields.add("FAMILIA");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  fields);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        studentMessageVal.value = (String)queryReturnVals.getQueryValues().get("studentMessage");
        
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for getting Orders *OTC*
     * @param regnomValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param studentMessageVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getOrderOtc")
    public void selectQueryForOrderOtc(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                       @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                       @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                       @WebParam(name = "studentMessage", mode = WebParam.Mode.OUT) Holder<String> studentMessageVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT dbo.KSOTC.*, dbo.KSOTC.*, dbo.KSOTC.*, dbo.KSOTC.* " +
                             "FROM dbo.KSOTC WHERE dbo.KSOTC.REGNOM = ?";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("studentMessage", "");
        
        ArrayList<String> fields = new ArrayList<>();
        fields.add("*");
        fields.add("*");
        fields.add("*");
        fields.add("*");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  fields);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        studentMessageVal.value = (String)queryReturnVals.getQueryValues().get("studentMessage");
        
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for getting Orders *ZAC*
     * @param regnomValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param studentMessageVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getOrderZac")
    public void selectQueryForOrderZac(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                       @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                       @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                       @WebParam(name = "studentMessage", mode = WebParam.Mode.OUT) Holder<String> studentMessageVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT CONVERT(varchar(10), dbo.KSZAC.DATAPR, 104) AS DATAPR, dbo.KSZAC.FAKYLTET, dbo.KSZAC.GRUPPA " +
                             "FROM dbo.KSZAC "+
                             "WHERE dbo.KSZAC.REGNOM = ?";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("studentMessage", "");
        
        ArrayList<String> fields = new ArrayList<>();
        fields.add("DATAPR");
        fields.add("FAKYLTET");
        fields.add("GRUPPA");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  fields);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        studentMessageVal.value = (String)queryReturnVals.getQueryValues().get("studentMessage");
        
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for getting Orders *PER*
     * @param regnomValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param studentMessageVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getOrderPer")
    public void selectQueryForOrderPer(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                       @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                       @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                       @WebParam(name = "studentMessage", mode = WebParam.Mode.OUT) Holder<String> studentMessageVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT dbo.KSPER.N_PRIKAZA, CONVERT(varchar(10), dbo.KSPER.DATAPR, 104) AS DATAPR, FAKYL_OLD.ZNACHENIE AS FAKYL_OLD_VAL, dbo.KSPER.GRUPPAS, FAKYL_NEW.ZNACHENIE AS FAKYL_NEW_VAL, dbo.KSPER.GRUPPAN " +
                             "FROM dbo.KSPER " +
                             "LEFT JOIN KODIF AS FAKYL_OLD ON KSPER.FAKYL_OLD = FAKYL_OLD.SHIFR " +
                             "LEFT JOIN KODIF AS FAKYL_NEW ON KSPER.FAKYL_NEW = FAKYL_NEW.SHIFR " +
                             "WHERE dbo.KSPER.REGNOM = ? AND FAKYL_OLD.KOD = 2 AND FAKYL_NEW.KOD = 2";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("studentMessage", "");
        
        ArrayList<String> fields = new ArrayList<>();
        fields.add("N_PRIKAZA");
        fields.add("DATAPR");
        fields.add("FAKYL_OLD_VAL");
        fields.add("GRUPPAS");
        fields.add("FAKYL_NEW_VAL");
        fields.add("GRUPPAN");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  fields);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        studentMessageVal.value = (String)queryReturnVals.getQueryValues().get("studentMessage");
        
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for getting Achievement
     * @param regnomValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param achievementMessageVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "getAchievement")
    public void selectQueryForGetAchievement(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                             @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                             @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                             @WebParam(name = "achievementMessage", mode = WebParam.Mode.OUT) Holder<String> achievementMessageVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT type_achvmnt.type_achievement, " +
                             "category.category, " +
                             "achievement.ats, " +
                             "achievement.theme, " +
                             "achievement.teacher, " +
                             "achievement.words, " +
                             "achievement.reference, " +
                             "achievement.review,  " +
                             "achievement.pathh " +
                             "FROM achievement LEFT JOIN category ON achievement.category = category.id " +
                             "LEFT JOIN type_achvmnt ON achievement.type_ach = type_achvmnt.id " +
                             "WHERE achievement.regnom = ?";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("achievementMessage", "");
        
        ArrayList<String> fields = new ArrayList<>();
        fields.add("type_achievement");
        fields.add("category");
        fields.add("ats");
        fields.add("theme");
        fields.add("teacher");
        fields.add("words");
        fields.add("reference");
        fields.add("review");
        fields.add("pathh");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  fields);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        achievementMessageVal.value = (String)queryReturnVals.getQueryValues().get("achievementMessage");
        
        dbQueryCont = null;
    }
    
    /**
     * Insert-Query for setting Achievement
     * @param regnomValue : <code>String</code> -IN
     * @param typeAchievementVal : <code>int</code> -IN
     * @param categoryVal : <code>int</code> -IN
     * @param atsVal : <code>String</code> -IN
     * @param themeVal : <code>String</code> -IN
     * @param teacherVal : <code>String</code> -IN
     * @param wordsVal : <code>String</code> -IN
     * @param pathhVal : <code>String</code> -IN
     * @param referenceVal : <code>String</code> -IN
     * @param reviewVal : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal  : <code>String</code> -OUT
     */
    @WebMethod(operationName = "setAchievement")
    public void insertQueryForSetAchievement(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                             @WebParam(name = "typeAchievement", mode = WebParam.Mode.IN) int typeAchievementVal,
                                             @WebParam(name = "category", mode = WebParam.Mode.IN) int categoryVal,
                                             @WebParam(name = "ats", mode = WebParam.Mode.IN) String atsVal,
                                             @WebParam(name = "theme", mode = WebParam.Mode.IN) String themeVal,
                                             @WebParam(name = "teacher", mode = WebParam.Mode.IN) String teacherVal,
                                             @WebParam(name = "words", mode = WebParam.Mode.IN) String wordsVal,
                                             @WebParam(name = "pathh", mode = WebParam.Mode.IN) String pathhVal,
                                             @WebParam(name = "reference", mode = WebParam.Mode.IN) String referenceVal,
                                             @WebParam(name = "review", mode = WebParam.Mode.IN) String reviewVal,
                                             @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                             @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "INSERT INTO achievement (regnom, type_ach, category, ats, theme, teacher, words, pathh, reference, review) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(regnomValue);
        statementValues.add(typeAchievementVal);
        statementValues.add(categoryVal);
        statementValues.add(atsVal);
        statementValues.add(themeVal);
        statementValues.add(teacherVal);
        statementValues.add(wordsVal);
        statementValues.add(pathhVal);
        statementValues.add(referenceVal);
        statementValues.add(reviewVal);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.INSERT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  null);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for getting TimeTable (*.txt file)
     * @param unitValue : <code>String</code> -IN
     * @param grouppValue : <code>String</code> -IN
     * @param semestrValue : <code>String</code> -IN
     * @param yearrValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param timetableVal : <code>byte[]</code> -OUT
     */
    @WebMethod(operationName = "getTimeTable")
    public void selectQueryForGetTimetable(@WebParam(name = "unit", mode = WebParam.Mode.IN) String unitValue,
                                           @WebParam(name = "groupp", mode = WebParam.Mode.IN) String grouppValue,
                                           @WebParam(name = "semestr", mode = WebParam.Mode.IN) String semestrValue,
                                           @WebParam(name = "yearr", mode = WebParam.Mode.IN) String yearrValue,
                                           @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                           @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                           @WebParam(name = "timetable", mode = WebParam.Mode.OUT) Holder<byte[]> timetableVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT timetable.timetable " +
                             "FROM timetable " +
                             "WHERE unit = ? AND " +
                             "groupp = ? AND " +
                             "semestr = ? AND " +
                             "yearr = ?";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(unitValue);
        statementValues.add(grouppValue);
        statementValues.add(semestrValue);
        statementValues.add(yearrValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("timetable", new byte[]{});
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  null);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        timetableVal.value = (byte[])queryReturnVals.getQueryValues().get("timetable");
        
        dbQueryCont = null;
    }
    
    //***** STUDENT ------------------------------------ END; ------------------------------------ *****
    
    //***** ASPIRANT ------------------------------------ START: ------------------------------------ *****
    
    /**
     * Select-Query for Aspirant Authentication by password
     * @param regnomValue : <code>String</code> -IN
     * @param passValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param aspirantFioVal : <code>String</code> -OUT
     * @param aspirantCourseVal : <code>String</code> -OUT
     * @param aspirantDirectionVal : <code>String</code> -OUT
     * @param aspirantDirectivityVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "aspAuthPass")
    public void selectQueryForAspirantAuthPass(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                               @WebParam(name = "pass", mode = WebParam.Mode.IN) String passValue,
                                               @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                               @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                               @WebParam(name = "fio", mode = WebParam.Mode.OUT) Holder<String> aspirantFioVal,
                                               @WebParam(name = "course", mode = WebParam.Mode.OUT) Holder<String> aspirantCourseVal,
                                               @WebParam(name = "direction", mode = WebParam.Mode.OUT) Holder<String> aspirantDirectionVal,
                                               @WebParam(name = "directivity", mode = WebParam.Mode.OUT) Holder<String> aspirantDirectivityVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT asp.fio, asp.course, dir.direction, d.directivity " +
                             "FROM aspirant AS asp LEFT JOIN direction AS dir ON dir.id = asp.direction " +
                                                  "LEFT JOIN directivity AS d ON d.id = asp.directivity " +
                                                  "LEFT JOIN auth ON asp.regnom = auth.regnom " +
                             "WHERE auth.pass = ? AND auth.regnom = ? ";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(passValue);
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("fio", "");
        queryValues.put("course", "");
        queryValues.put("direction", "");
        queryValues.put("directivity", "");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  null);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        aspirantFioVal.value = (String)queryReturnVals.getQueryValues().get("fio");
        aspirantCourseVal.value = (String)queryReturnVals.getQueryValues().get("course");
        aspirantDirectionVal.value = (String)queryReturnVals.getQueryValues().get("direction");
        aspirantDirectivityVal.value = (String)queryReturnVals.getQueryValues().get("directivity");
                
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for Aspirant Authentication by hash(UEC number)
     * @param regnomValue : <code>String</code> -IN
     * @param hashValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param aspirantFioVal : <code>String</code> -OUT
     * @param aspirantCourseVal : <code>String</code> -OUT
     * @param aspirantDirectionVal : <code>String</code> -OUT
     * @param aspirantDirectivityVal : <code>String</code> -OUT 
     */
    @WebMethod(operationName = "aspGetAuth")
    public void selectQueryForAspirantAuth(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                           @WebParam(name = "hash", mode = WebParam.Mode.IN) String hashValue,
                                           @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                           @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                           @WebParam(name = "fio", mode = WebParam.Mode.OUT) Holder<String> aspirantFioVal,
                                           @WebParam(name = "course", mode = WebParam.Mode.OUT) Holder<String> aspirantCourseVal,
                                           @WebParam(name = "direction", mode = WebParam.Mode.OUT) Holder<String> aspirantDirectionVal,
                                           @WebParam(name = "directivity", mode = WebParam.Mode.OUT) Holder<String> aspirantDirectivityVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT asp.fio, asp.course, dir.direction, d.directivity " +
                             "FROM aspirant AS asp LEFT JOIN direction AS dir ON dir.id = asp.direction " +
                                                  "LEFT JOIN directivity AS d ON d.id = asp.directivity " +
                                                  "LEFT JOIN auth ON asp.regnom = auth.regnom " +
                             "WHERE auth.hash = ? AND auth.regnom = ? ";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(hashValue);
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("fio", "");
        queryValues.put("course", "");
        queryValues.put("direction", "");
        queryValues.put("directivity", "");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  null);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        aspirantFioVal.value = (String)queryReturnVals.getQueryValues().get("fio");
        aspirantCourseVal.value = (String)queryReturnVals.getQueryValues().get("course");
        aspirantDirectionVal.value = (String)queryReturnVals.getQueryValues().get("direction");
        aspirantDirectivityVal.value = (String)queryReturnVals.getQueryValues().get("directivity");
                
        dbQueryCont = null;
    }
    
    /**
     * Select-Query for getting Aspirant's Exams
     * @param regnomValue : <code>String</code> -IN
     * @param resultSuccessVal : <code>String</code> -OUT
     * @param resultDescriptionVal : <code>String</code> -OUT
     * @param examMessageVal : <code>String</code> -OUT
     */
    @WebMethod(operationName = "aspGetExam")
    public void selectQueryForAspirantGetExam(@WebParam(name = "regnom", mode = WebParam.Mode.IN) String regnomValue,
                                              @WebParam(name = "resultSuccess", mode = WebParam.Mode.OUT) Holder<String> resultSuccessVal,
                                              @WebParam(name = "resultDescription", mode = WebParam.Mode.OUT) Holder<String> resultDescriptionVal,
                                              @WebParam(name = "examMessage", mode = WebParam.Mode.OUT) Holder<String> examMessageVal)
    {
        //check auth http-headers (start):
        if(!isAuth())
        {
            resultSuccessVal.value = "false";
            resultDescriptionVal.value = "authentication error";
            
            return;
        }
        //check auth http-headers (end);
        
        DBQueryContainer dbQueryCont = new DBQueryContainer();
        
        String queryString = "SELECT minimum.minimum, examination.mark, CONVERT(varchar(10), examination.date_m, 104) AS date_m, examination.ets " +
                             "FROM examination " +
                             "LEFT JOIN minimum ON minimum.id = examination.minimum " +
                             "WHERE examination.regnom = ? " +
                             "ORDER BY date_m";
        
        ArrayList<Object> statementValues = new ArrayList<>();
        statementValues.add(regnomValue);
        
        LinkedHashMap<String, Object> queryValues = new LinkedHashMap<>();
        queryValues.put("examMessage", "");
        
        ArrayList<String> fields = new ArrayList<>();
        fields.add("minimum");
        fields.add("mark");
        fields.add("date_m");
        fields.add("ets");
        
        QueryReturnedValues queryReturnVals = dbQueryCont.doQuery(SQLQueryTypes.SELECT, 
                                                                  queryString, 
                                                                  statementValues, 
                                                                  new QueryReturnedValues(queryValues),
                                                                  fields);
        
        resultSuccessVal.value = queryReturnVals.getResultSuccess();
        resultDescriptionVal.value = queryReturnVals.getResultDescription();
        examMessageVal.value = (String)queryReturnVals.getQueryValues().get("examMessage");
        
        dbQueryCont = null;
    }
    
    //***** ASPIRANT ------------------------------------ END; ------------------------------------ *****
}
