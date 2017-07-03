package com.ErasmusProject.rest;
import com.ErasmusProject.model.Internship;
import com.ErasmusProject.util.*;

import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.io.IOException;

/**
 * 
 * @author Nina
 *
 */

@RestController
@RequestMapping("/internship")
public class InternshipStore {

	 @Autowired
	 private Conf conf;
	
	
    @PostConstruct
    public void initFuseki()
    {
    	System.out.println("\n\n"+conf.getInitialize()+"\n\n");
        if(!conf.getInitialize()) return;
        System.out.println("\n\nBaza inicijalizovana\n\n");
        try {
            Model base=OntologyUtils.getModel(StringUtils.URL);
            Model knowledge = OntologyUtils.createOntModel(StringUtils.knowledgeFile);        
        	Model internship = OntologyUtils.createOntModel(StringUtils.internshipFile);
        	base.add(knowledge);
        	base.add(internship);
            OntologyUtils.reloadModel(base,StringUtils.URL);              
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(QueryType.class, new QueryTypeConverter());
    }

    
    /*
	 * Add an internship
	 * */
	@RequestMapping(method = RequestMethod.POST, value="/addInternship")
	public String addInternship(@RequestParam(value="identifier", required=true) String identifier,
								@RequestParam(value="title", required=true) String title)
	{
		String query = "PREFIX int: <" + StringUtils.namespaceInternship + "> "
						+"INSERT DATA"
						+"{" 
						+"  int:" + identifier + " int:Code \"" + identifier.replaceAll("[\\t\\n\\r]","") + "\" ;"
						+"  					   int:Title \"" + title.replaceAll("[\\t\\n\\r]"," ") + "\" ."
						+"}";

		System.out.println(query);
		try{
			OntologyUtils.execUpdate(StringUtils.URLupdate, query);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return "SUCCESS";
	}

    @RequestMapping(method = RequestMethod.GET, value = "/queryInternships")
    public ArrayList<QueryResult> queryInternships(@RequestParam("value") String val,
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
    @RequestMapping(method = RequestMethod.GET, value="/getInternships")
    public ArrayList<Internship> getInternships()
    {
        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceInternship);
        namespaces.add(StringUtils.namespaceW3c);

        retVal = queryInternships("InternshipCode", QueryType.PREDICATE);

        ArrayList<Internship> institutions = new ArrayList<>();

       String InternshipCode="", InternshipTitle="", InternshipDescription="", InternshipPositionTitle="";        
        ArrayList<QueryResult> results = new ArrayList<QueryResult>();


        for (QueryResult queryResult : retVal) {
            InternshipCode = queryResult.getSubject();
            results = query(InternshipCode, QueryType.SUBJECT);
            for (QueryResult queryResult2 : results) {
                if (queryResult2.getPredicate().equals("InternshipCode"))
                    InternshipCode = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("InternshipTitle"))
                    InternshipTitle = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("InternshipDescription"))
                    InternshipDescription = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("InternshipPositionTitle"))
                    InternshipPositionTitle = queryResult2.getObject();
            }
            institutions.add(new Internship(InternshipTitle,InternshipCode , InternshipDescription, InternshipPositionTitle));
        }

        return institutions;
    }

}
