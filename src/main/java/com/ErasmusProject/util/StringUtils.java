package com.ErasmusProject.util;

import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;

/**
 * Created by Komp on 13.2.2017.
 */
public class StringUtils {
    
    public static String URL = "http://localhost:3030/inf";
    public static String URLdataset = "http://localhost:3030/inf/data";
    public static String URLupdate = "http://localhost:3030/inf/update";
    //public static String URLquery = "http://localhost:3030/inf/query";
    //URLquery constant for inf configuration
    public static String URLquery = "http://localhost:3030/inf/sparql";
    public static String namespaceInternship = "http://www.semanticweb.org/sasa/ontologies/2017/6/internship#";
    public static String namespaceKnowledge = "http://www.semanticweb.org/sasa/ontologies/2017/6/knowledge#";
    public static String namespaceEcts = "http://www.semanticweb.org/nina/ontologies/2016/11/ects#";

    public static String namespaceW3c = "^^http://www.w3.org/2001/XMLSchema#integer";    
    public static String internshipFile = "internship.owl";    
    public static String knowledgeFile = "knowledge.owl";
    public static String sparqlTemplate = "SELECT * WHERE{%s %s  %s FILTER(STRSTARTS(STR(%s), %s))}";

    /**
     * Formats jena statements into SPARQL INSERT
     * @param template
     * @param statement - statement to be formated
     * @return
     */
    public static String createTripleFromStatement(String template, String statement)
    {
        String retVal = new String(template);
        String[] spo = statement.replace("[", "").replace("]","").replace(","," ").replace("\"","").split("  ");
        for(String part: spo)
        {
            retVal += "<" + part + ">" + " ";
        }
        retVal += ".}";
        return retVal;
    }

    /**
     * Formats SPARQL query results by removing namespaceStudent
     * @param x
     * @param y
     * @return
     */
    public static QueryResult parseResult(RDFNode x, RDFNode y, String parameter, QueryType qt, ArrayList<String> namespaces) {
        String strX = x.toString();
        String strY = y.toString();

        for (String ns : namespaces)
        {
            strX = strX.replace(ns,"");
            strY = strY.replace(ns,"");
        }

        QueryResult retVal = null;

        switch(qt)
        {
            case SUBJECT:
                retVal = new QueryResult(parameter, strX, strY);
                break;
            case PREDICATE:
                retVal = new QueryResult(strX, parameter, strY);
                break;
            case OBJECT:
                retVal = new QueryResult(strX, strY, parameter);
                break;
        }

        return retVal;
    }

}
