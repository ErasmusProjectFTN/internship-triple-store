package com.ErasmusProject.rest;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ErasmusProject.model.Company;
import com.ErasmusProject.model.CompanySearch;
import com.ErasmusProject.model.Internship;
import com.ErasmusProject.model.InternshipSearch;
import com.ErasmusProject.util.OntologyUtils;
import com.ErasmusProject.util.QueryResult;
import com.ErasmusProject.util.QueryType;
import com.ErasmusProject.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/company")
public class CompanyStore {

    @RequestMapping(method = RequestMethod.POST, value="/addCompany")
    public Company addCompany(@RequestParam(value="CompanyCode", required=true) String identifier,
                                @RequestParam(value="CompanyName", required=true) String name,
                                @RequestParam(value="CompanyWebsite", required=false) String website,
                                @RequestParam(value="CompanyAddress", required=false) String address,
                                @RequestParam(value="CompanyDescription", required=false) String description)
    {
        String query = "PREFIX int: <" + StringUtils.namespaceInternship + "> "
                        +"INSERT DATA"
                        +"{" 
                        +"  int:" + identifier + " int:CompanyCode \"" + identifier.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:CompanyName \"" + name.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:CompanyWebsite \"" + website.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:CompanyAddress \"" + address.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:CompanyDescription \"" + description.replaceAll("[\\t\\n\\r]"," ") + "\" ."
                        +"}";

        System.out.println(query);
        try{
            OntologyUtils.execUpdate(StringUtils.URLupdate, query);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new Company("","",identifier,"",name);
    }
    @RequestMapping(method = RequestMethod.GET, value="/getCompany")
    public Company getCompany(@RequestParam("identifier")String CompanyCode)
    {
        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceInternship);
        namespaces.add(StringUtils.namespaceW3c);

        // get programme specification ids
        String query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceInternship + "CompanyCode> \""+CompanyCode+"\"}";
        System.out.println(query);
        ResultSet result = OntologyUtils.execSelect(StringUtils.URLquery, query);
        System.out.println(result.hasNext());

        QuerySolution soln = result.next();
        String identifier = soln.get("s").toString().replaceAll(StringUtils.namespaceInternship, "");
        ArrayList<Internship> inter=new ArrayList<Internship>();
        InternshipStore is=new InternshipStore();
        String courseUnitCode = CompanyCode;
        String  CompanyName="", CompanyWebsite="", CompanyAddress="",CompanyDescription=""; 
        // get programme instance data
        retVal = query(identifier, QueryType.SUBJECT);
        for (QueryResult queryResult2 : retVal) {
            if (queryResult2.getPredicate().equals("CompanyName"))
                CompanyName = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("CompanyWebsite"))
                CompanyWebsite = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("CompanyAddress"))
                CompanyAddress = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("CompanyDescription"))
                CompanyDescription = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("provides")){
                String InternshipId= queryResult2.getObject();
                ArrayList<QueryResult> results3 = query(InternshipId, QueryType.SUBJECT);
                for(QueryResult queryResult4 :results3){
                    if (queryResult4.getPredicate().equals("InternshipCode")){
                        InternshipId = queryResult4.getObject();

                        inter.add(is.getInternship(InternshipId));
                    break;
                    }
                }
            }
        }
        return new Company(CompanyDescription,CompanyAddress , CompanyCode, CompanyWebsite,CompanyName,inter);
    }
    @RequestMapping(method = RequestMethod.GET, value="/getCompanies")
    public ArrayList<Company> getCompanies()
    {
        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceInternship);
        namespaces.add(StringUtils.namespaceW3c);

        retVal = queryCompanies("CompanyCode", QueryType.PREDICATE);

        ArrayList<Company> institutions = new ArrayList<>();

       String CompanyCode="", CompanyName="", CompanyWebsite="", CompanyAddress="",CompanyDescription="";        
        ArrayList<QueryResult> results = new ArrayList<QueryResult>();


        for (QueryResult queryResult : retVal) {
            CompanyCode = queryResult.getSubject();
            results = query(CompanyCode, QueryType.SUBJECT);
            for (QueryResult queryResult2 : results) {
                if (queryResult2.getPredicate().equals("CompanyCode"))
                    CompanyCode = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("CompanyName"))
                    CompanyName = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("CompanyWebsite"))
                    CompanyWebsite = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("CompanyAddress"))
                    CompanyAddress = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("CompanyDescription"))
                    CompanyDescription = queryResult2.getObject();
            }
            institutions.add(new Company(CompanyDescription,CompanyAddress , CompanyCode, CompanyWebsite,CompanyName));
        }

        return institutions;
    }
    @RequestMapping(method = RequestMethod.GET, value = "/queryCompanies")
    public ArrayList<QueryResult> queryCompanies(@RequestParam("value") String val,
                              @RequestParam("type") QueryType type)
    {

        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceInternship);

        switch(type)
        {
            case SUBJECT:
                String subject = "<" + StringUtils.namespaceInternship + val + ">";
                retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,subject,"?p","?o","?p","\""+StringUtils.namespaceInternship +"\""), namespaces, type, val);
                break;
            case PREDICATE:
                String predicate = "<" + StringUtils.namespaceInternship + val + ">";
                retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s",predicate,"?o","?s","\""+StringUtils.namespaceInternship +"\""), namespaces, type, val);
                break;
            case OBJECT:
                retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s","?p","\"" + val + "\"","?p","\""+StringUtils.namespaceInternship +"\""), namespaces, type, val);
                break;
        }

        return retVal;
    }
    @RequestMapping(method = RequestMethod.GET, value = "/query")
    public ArrayList<QueryResult> query(@RequestParam("value") String val,
            @RequestParam("type") QueryType type)
    {

        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceInternship);
        namespaces.add(StringUtils.namespaceW3c);


        switch(type)
        {
        case SUBJECT:
            String subject = "<" + StringUtils.namespaceInternship + val + ">";
            retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,subject,"?p","?o","?p","\""+StringUtils.namespaceInternship +"\""), namespaces, type, val);
            break;
        case PREDICATE:
            String predicate = "<" + StringUtils.namespaceInternship + val + ">";
            retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s",predicate,"?o","?s","\""+StringUtils.namespaceInternship +"\""), namespaces, type, val);
            break;
        case OBJECT:
            retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s","?p","\"" + val + "\"","?p","\""+StringUtils.namespaceInternship +"\""), namespaces, type, val);
            break;
        }

        return retVal;
    }
    @RequestMapping(method = RequestMethod.GET, value="/searchCompanies")
    public ArrayList<Company> searchCompanies(@RequestParam("company") String company){
        ArrayList<Company> institutions = new ArrayList<Company>();
        System.out.println(company);
        ObjectMapper mapper = new ObjectMapper();
        CompanySearch insS = new CompanySearch();
        try {
            insS = mapper.readValue(company, CompanySearch.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        String CompanyDescription = insS.getCompanyDescription().equals("")?"":" ?s <" + StringUtils.namespaceInternship + "CompanyDescription> ?status.";
        String CompanyAddress = insS.getCompanyAddress().equals("")?"":" ?s <" + StringUtils.namespaceInternship + "CompanyAddress> ?address.";
        String CompanyWebsite  = insS.getCompanyWebsite().equals("")?"":" ?s <" + StringUtils.namespaceInternship + "CompanyWebsite> ?website.";
       
        String status = insS.getCompanyDescription().equals("")?"":"&&         CONTAINS(LCASE(STR(?status)), \""+ insS.getCompanyDescription().toLowerCase() +"\")";
        String address = insS.getCompanyAddress().equals("")?"":"&&          CONTAINS(LCASE(STR(?address)), \""+ insS.getCompanyAddress().toLowerCase() +"\")";
        String website = insS.getCompanyWebsite().equals("")?"":"&&          CONTAINS(LCASE(STR(?website)), \""+ insS.getCompanyWebsite().toLowerCase() +"\")";
        
        String query = "SELECT DISTINCT ?s" 
                +" WHERE {"
                +" ?s <" + StringUtils.namespaceInternship + "CompanyCode> ?id."
                +" ?s <" + StringUtils.namespaceInternship + "CompanyName> ?name."
                + CompanyDescription
                + CompanyAddress
                + CompanyWebsite
                +" FILTER (CONTAINS(LCASE(STR(?id)), \"" + insS.getCompanyCode().toLowerCase() + "\") &&"
                +"         CONTAINS(LCASE(STR(?name)), \""+ insS.getCompanyName().toLowerCase() +"\")"
                + status
                + address
                + website
                + ")}";
        System.out.println(query);
        ResultSet retVal = OntologyUtils.execSelect(StringUtils.URLquery, query);

        String identifier="", title="", description="", add="",web=""; 
        ArrayList<QueryResult> results = new ArrayList<QueryResult>();
        QuerySolution soln = null;


        while (retVal.hasNext()) {
            soln = retVal.next();
            identifier = soln.get("s").toString().replaceAll(StringUtils.namespaceInternship, "");
            results = query(identifier, QueryType.SUBJECT);
            for (QueryResult queryResult2 : results) {
                if (queryResult2.getPredicate().equals("CompanyCode"))
                    identifier = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("CompanyDescription"))
                    description = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("CompanyWebsite"))
                    web = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("CompanyAddress"))
                    add = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("CompanyName"))
                    title = queryResult2.getObject();
            }
            institutions.add(new Company(description, add, identifier, web,title));
        }
        return institutions;
    }
}
