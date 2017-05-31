package com.ErasmusProject.rest;
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
        	Model internship = OntologyUtils.createOntModel(StringUtils.internshipFile);
            OntologyUtils.reloadModel(internship,StringUtils.URL);            
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

    @RequestMapping(method = RequestMethod.GET, value = "/query")
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

}
