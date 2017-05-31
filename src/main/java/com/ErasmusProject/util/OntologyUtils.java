package com.ErasmusProject.util;

import org.apache.jena.ontology.*;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Komp on 13.2.2017.
 */
public class OntologyUtils {

    /**
     * Creates OntModel from file
     *
     * @param file - ontology file
     * @return - returns the newly created OntModel
     */
    public static OntModel createOntModel(String file) {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
        //model.setStrictMode(false);
        model.read(file);
        return model;
    }

    /**
     * Accesses Model in a dataset
     *
     * @param serviceURI - dataset location
     * @return - returns the deployed ontology model
     */
    public static Model getModel(String serviceURI) {
        DatasetAccessor accessor = DatasetAccessorFactory
                .createHTTP(serviceURI);
        return accessor.getModel();
    }

    /**
     * Transformes Model into OntModel
     *
     * @param serviceURI - dataset location
     * @param namespace  - ontology namespaceStudent
     * @return - returns OntModel
     * @throws IOException
     */
    public static OntModel loadOntModel(String serviceURI, String namespace) throws IOException {
        Model dm = getModel(serviceURI);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        dm.write(bos);
        ByteArrayInputStream ios = new ByteArrayInputStream(bos.toByteArray());
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
        model.read(ios, namespace);
        return model;
    }

    /**
     * Changes dataset model
     *
     * @param model
     * @param serviceURI
     * @throws IOException
     */
    public static void reloadModel(Model model, String serviceURI)
            throws IOException {
        DatasetAccessor accessor = DatasetAccessorFactory
                .createHTTP(serviceURI);
        accessor.putModel(model);
    }

    /**
     * Executes SPARQL update
     *
     * @param serviceURI
     * @param update     - update string
     */
    public static void execUpdate(String serviceURI, String update) {
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(
                UpdateFactory.create(update),
                serviceURI);
        upp.execute();
    }

    /**
     * Executes multiple updates
     * @param serviceURI
     * @param updates
     */
    public static void execMassUpdate(String serviceURI, ArrayList<String> updates)
    {
        for(String update: updates)
        {
            execUpdate(serviceURI, update);
        }
    }

    /**
     * Creates a list of SPARQL insert statements, for each individual
     * @param m1 - OntModel with added individuals
     * @param m2 - default OntModel
     * @param namespace - ontology namespaceStudent
     * @return - returns a list of SPARQL insert statements
     */
    public static ArrayList<String> getIndividualsForInsert(OntModel m1, OntModel m2, String namespace) {
        String insert_template = "INSERT DATA"
                + "{ ";
        ArrayList<String> retVal = new ArrayList<String>();
        Model om2 = m1.difference(m2);
        StmtIterator it = om2.listStatements();
        while (it.hasNext()) {
            retVal.add(StringUtils.createTripleFromStatement(insert_template, it.next().toString()));
        }
        return retVal;
    }

    /**
     * Executes SPARQL query
     * @param serviceURI
     * @param query
     * @return
     */
    public static ResultSet execSelect(String serviceURI, String query) {
        QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI,
                query);
        ResultSet results = q.execSelect();
        return results;
    }


    /**
     * Execute SPARQL query and format results
     * @param serviceURI
     * @param query
     * @param namespaces
     * @param type
     * @param parameter
     * @return
     */
    public static ArrayList<QueryResult> formatedSelect(String serviceURI, String query, ArrayList<String> namespaces, QueryType type, String parameter)
    {
        ArrayList<QueryResult> retVal = new ArrayList<QueryResult>();
        ResultSet results = execSelect(serviceURI, query);

        String[] spo = new String[2];

        switch(type)
        {
            case SUBJECT:
                spo[0] = "p";
                spo[1] = "o";
                break;
            case PREDICATE:
                spo[0] = "s";
                spo[1] = "o";
                break;
            case OBJECT:
                spo[0] = "s";
                spo[1] = "p";
        }

        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            RDFNode x = soln.get(spo[0]);
            RDFNode r = soln.get(spo[1]);
            retVal.add(StringUtils.parseResult(x, r, parameter, type, namespaces));

        }
        return retVal;
    }

    /**
     * Adds an individual to the OntModel
     * @param className
     * @param model
     * @param namespace
     * @return
     */
    public static OntModel addIndividual(String className, OntModel model, String namespace, String id)
    {
        OntClass oclass = model.getOntClass(namespace + className);
        Individual ind = model.createIndividual(namespace + id, oclass);
        return model;
    }

    /**
     * Sets the value of an ObjectProperty for an individual of given OntModel
     * @param propName
     * @param model
     * @param namespace
     * @param indName
     * @param propVal
     * @return
     */
    public static OntModel addObjectProperty(String propName, OntModel model, String namespace, String indName, String propVal)
    {
        Individual ind = model.getIndividual(namespace + indName);
        Individual prop = model.getIndividual(namespace + propVal);
        ObjectProperty obp = model.getObjectProperty(propName);
        ind.setPropertyValue(obp, prop);
        return model;
    }

    /**
     * Sets the value of an DatatypeProperty for an individual of given OntModel
     * @param propName
     * @param model
     * @param namespace
     * @param indName
     * @param val
     * @return
     */
    public static OntModel addDatatypeProperty(String propName, OntModel model, String namespace, String indName, String val)
    {
        Individual ind = model.getIndividual(namespace + indName);
        DatatypeProperty dp = model.getDatatypeProperty(namespace + propName);
        ind.setPropertyValue(dp, model.createTypedLiteral(val));
        return model;
    }

}
