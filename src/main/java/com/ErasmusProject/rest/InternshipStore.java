package com.ErasmusProject.rest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ErasmusProject.model.Company;
import com.ErasmusProject.model.Internship;
import com.ErasmusProject.model.InternshipSearch;
import com.ErasmusProject.model.Knowledge;
import com.ErasmusProject.recommendation.InternshipRecommendation;
import com.ErasmusProject.recommendation.InternshipRecommendation.SimilarityValue;
import com.ErasmusProject.util.Conf;
import com.ErasmusProject.util.OntologyUtils;
import com.ErasmusProject.util.QueryResult;
import com.ErasmusProject.util.QueryType;
import com.ErasmusProject.util.QueryTypeConverter;
import com.ErasmusProject.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	 public static HashMap<String, ArrayList<SimilarityValue>> utilityMatrix = new HashMap<>();
	
    @PostConstruct
    public void initFuseki()
    {
    	System.out.println("\n\n"+conf.getInitialize()+"\n\n");
        if(!conf.getInitialize()) return;
        System.out.println("\n\nBaza inicijalizovana\n\n");
        try {
            //Model base=OntologyUtils.getModel(StringUtils.URL);
            Model student= OntologyUtils.createOntModel("student.owl");
            Model base=OntologyUtils.createOntModel("ectsMloMerged_individuals.owl");
            Model knowledge = OntologyUtils.createOntModel(StringUtils.knowledgeFile);        
        	Model internship = OntologyUtils.createOntModel(StringUtils.internshipFile);
        	base.add(student);
        	base.add(knowledge);
        	base.add(internship);
            OntologyUtils.reloadModel(base,StringUtils.URL);           
            

            KnowledgeStore ks=new KnowledgeStore();
            ks.createKnowledgeMatrix();
            InternshipRecommendation i = new InternshipRecommendation();
            utilityMatrix = i.generateUtilityMatrixMatrix();
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
	public Internship addInternship(@RequestParam(value="InternshipCode", required=true) String identifier,
								@RequestParam(value="InternshipTitle", required=true) String title,
                                @RequestParam(value="InternshipPositionTitle", required=false) String posTitle,
                                @RequestParam(value="InternshipDescription", required=false) String description,
                                @RequestParam(value="Company", required=true) String Company)
	{
		String query = "PREFIX int: <" + StringUtils.namespaceInternship + "> "
						+"INSERT DATA"
						+"{" 
						+"  int:" + identifier + " int:InternshipCode \"" + identifier.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:InternshipPositionTitle \"" + posTitle.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:InternshipTitle \"" + title.replaceAll("[\\t\\n\\r]","") + "\" ;"
						+"  					   int:InternshipDescription \"" + description.replaceAll("[\\t\\n\\r]"," ") + "\" ."
						+"}";

		System.out.println(query);
		try{
			OntologyUtils.execUpdate(StringUtils.URLupdate, query);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
        query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceInternship + "CompanyCode> \""+Company+"\"}";
        ResultSet result = OntologyUtils.execSelect(StringUtils.URLquery, query);
        
        QuerySolution soln = result.next();
        String identifierCompany = soln.get("s").toString().replaceAll(StringUtils.namespaceInternship, "");
        
        query = "INSERT DATA{<"+StringUtils.namespaceInternship + identifierCompany+"> <"+StringUtils.namespaceInternship+"provides> <"+StringUtils.namespaceInternship+identifier+">}";
        OntologyUtils.execUpdate(StringUtils.URLupdate, query);

		return new Internship(title,identifier,"","");
	}
	@RequestMapping(method = RequestMethod.POST, value="/modifyDegreeProgramme")
    public Internship modifyInternship(@RequestParam(value="InternshipCode", required=true) String identifier,
            @RequestParam(value="InternshipTitle", required=true) String title,
            @RequestParam(value="InternshipPositionTitle", required=false) String posTitle,
            @RequestParam(value="InternshipDescription", required=false) String description)
    {
        try{
            OntModel model = OntologyUtils.loadOntModel(StringUtils.URLdataset,  StringUtils.namespaceInternship);
            Individual ind = model.getIndividual(StringUtils.namespaceInternship + identifier);
            HashMap<String, String> propertyValues = new HashMap<>();
            propertyValues.put("InternshipTitle", title);
            propertyValues.put("InternshipPositionTitle", posTitle);
            propertyValues.put("InternshipDescription", description);
           
            model = OntologyUtils.modifyIndividual(ind, model, propertyValues, StringUtils.namespaceInternship);
            OntologyUtils.reloadModel(model, StringUtils.URL);
        }catch(IOException e){
            e.printStackTrace();
        }
        return new Internship(title, identifier,description,posTitle);
    }
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/removeInternship")
    public String removeInternship(@RequestParam(value = "InternshipCode", required=true) String InternshipCode)
    {
            String query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceInternship + "InternshipCode> \""+InternshipCode+"\"}";
            System.out.println(query);
            ResultSet result = OntologyUtils.execSelect(StringUtils.URLquery, query);
            System.out.println(result.hasNext());

            QuerySolution soln = result.next();
            String identifier = soln.get("s").toString().replaceAll(StringUtils.namespaceInternship, "");
            query="DELETE WHERE{ <"+StringUtils.namespaceInternship + identifier+"> ?p ?o }";
             OntologyUtils.execUpdate(StringUtils.URLupdate, query);
        
        return "Internship with id: " + InternshipCode + " is removed.";
    }
	@RequestMapping(method = RequestMethod.GET, value="/getInternship")
    public Internship getInternship(@RequestParam("identifier")String InternshipCode)
    {
        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceInternship);
        namespaces.add(StringUtils.namespaceW3c);

        ArrayList<Knowledge> knowledge=new ArrayList<Knowledge>();
        // get programme specification ids
        String query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceInternship + "InternshipCode> \""+InternshipCode+"\"}";
        System.out.println(query);
        ResultSet result = OntologyUtils.execSelect(StringUtils.URLquery, query);
        System.out.println(result.hasNext());

        QuerySolution soln = result.next();
        String identifier = soln.get("s").toString().replaceAll(StringUtils.namespaceInternship, "");

        String courseUnitCode = InternshipCode;
        String InternshipTitle = "";
        String InternshipDescription = "";
        String InternshipPositionTitle = "";
        String knowledgeCode="";

        // get programme instance data
        retVal = query(identifier, QueryType.SUBJECT);
        for (QueryResult queryResult2 : retVal) {
            if (queryResult2.getPredicate().equals("InternshipTitle"))
                InternshipTitle = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("InternshipDescription"))
                InternshipDescription = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("InternshipPositionTitle"))
                InternshipPositionTitle = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("requires")){
                KnowledgeStore ks= new KnowledgeStore();
                ArrayList<QueryResult> results2 = ks.query(queryResult2.getObject().split("#")[1], QueryType.SUBJECT);
                for(QueryResult queryResult3 :results2){
                    if (queryResult3.getPredicate().equals("Code"))
                        knowledgeCode = queryResult3.getObject();

                    knowledge.add(ks.getKnowledge(knowledgeCode));
                    break;
                }
            }
        }
        return new Internship(InternshipTitle,InternshipCode,InternshipDescription,InternshipPositionTitle,knowledge);
    }

    @RequestMapping(method = RequestMethod.GET, value="/getParentCompany")
	public Company getParentCompany(@RequestParam("internship")String InternshipCode)
    {
        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceInternship);
        namespaces.add(StringUtils.namespaceW3c);

        ArrayList<Knowledge> knowledge=new ArrayList<Knowledge>();
        // get programme specification ids
        String query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceInternship + "InternshipCode> \""+InternshipCode+"\"}";
        System.out.println(query);
        ResultSet result = OntologyUtils.execSelect(StringUtils.URLquery, query);
        System.out.println(result.hasNext());

        QuerySolution soln = result.next();
        String identifier = soln.get("s").toString().replaceAll(StringUtils.namespaceInternship, "");

        String courseUnitCode = InternshipCode;
        String Company="";

        // get programme instance data
        retVal = query(identifier, QueryType.SUBJECT);
        for (QueryResult queryResult2 : retVal) {
            if (queryResult2.getPredicate().equals("isProvidedAt")){
                CompanyStore ks= new CompanyStore();
                ArrayList<QueryResult> results2 = ks.query(queryResult2.getObject(), QueryType.SUBJECT);
                for(QueryResult queryResult3 :results2){
                    if (queryResult3.getPredicate().equals("CompanyCode"))
                        return ks.getCompany(queryResult3.getObject());

                    
                }
            }
        }
        return new Company();
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
        ArrayList<Knowledge> knowledge;

        for (QueryResult queryResult : retVal) {
            InternshipCode=""; InternshipTitle=""; InternshipDescription=""; InternshipPositionTitle="";      
            String knowledgeCode="";
            knowledge=new ArrayList<Knowledge>();
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
                else if (queryResult2.getPredicate().equals("requires")){
                    KnowledgeStore ks= new KnowledgeStore();
                    ArrayList<QueryResult> results2 = ks.query(queryResult2.getObject().split("#")[1], QueryType.SUBJECT);
                    for(QueryResult queryResult3 :results2){
                        if (queryResult3.getPredicate().equals("Code"))
                            knowledgeCode = queryResult3.getObject();

                        knowledge.add(ks.getKnowledge(knowledgeCode));
                        break;
                    }
                }
            }
            institutions.add(new Internship(InternshipTitle,InternshipCode , InternshipDescription, InternshipPositionTitle,knowledge));
        }

        return institutions;
    }
    @RequestMapping(method = RequestMethod.GET, value="/getRecommendation")
    public ArrayList<Internship> getRecommendation(@RequestParam("identifier") String val)
    {   ArrayList<Internship> institutions = new ArrayList<>();
    InternshipRecommendation i = new InternshipRecommendation();
    utilityMatrix=i.generateUtilityMatrixMatrix();
        LinkedHashMap<String, Double> ret = InternshipRecommendation.recommendInternshipforProgramme(val);
        int pos=ret.size();
        if(pos>2){
        String recommendationCode1 = new ArrayList<String>(ret.keySet()).get(pos-1);
        String recommendationCode2 = new ArrayList<String>(ret.keySet()).get(pos-2);
        String recommendationCode3 = new ArrayList<String>(ret.keySet()).get(pos-3);
        institutions.add(getInternship(recommendationCode1));
        institutions.add(getInternship(recommendationCode2));
        institutions.add(getInternship(recommendationCode3));
        }
        return institutions;
    }
    @RequestMapping(method = RequestMethod.GET, value="/searchInternships")
    public ArrayList<Internship> searchInternships(@RequestParam("internship") String internship){
        ArrayList<Internship> institutions = new ArrayList<Internship>();
        System.out.println(internship);
        ObjectMapper mapper = new ObjectMapper();
        InternshipSearch insS = new InternshipSearch();
        try {
            insS = mapper.readValue(internship, InternshipSearch.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        String InternshipDescription = insS.getInternshipDescription().equals("")?"":" ?s <" + StringUtils.namespaceInternship + "InternshipDescription> ?status.";
        String InternshipPositionTitle = insS.getInternshipPositionTitle().equals("")?"":" ?s <" + StringUtils.namespaceInternship + "InternshipPositionTitle> ?address.";
        
        String status = insS.getInternshipDescription().equals("")?"":"&&         CONTAINS(LCASE(STR(?status)), \""+ insS.getInternshipDescription().toLowerCase() +"\")";
        String address = insS.getInternshipPositionTitle().equals("")?"":"&&          CONTAINS(LCASE(STR(?address)), \""+ insS.getInternshipPositionTitle().toLowerCase() +"\")";
        
        String query = "SELECT DISTINCT ?s" 
                +" WHERE {"
                +" ?s <" + StringUtils.namespaceInternship + "InternshipCode> ?id."
                +" ?s <" + StringUtils.namespaceInternship + "InternshipTitle> ?name."
                + InternshipDescription
                + InternshipPositionTitle
                +" FILTER (CONTAINS(LCASE(STR(?id)), \"" + insS.getInternshipCode().toLowerCase() + "\") &&"
                +"         CONTAINS(LCASE(STR(?name)), \""+ insS.getInternshipTitle().toLowerCase() +"\")"
                + status
                + address
                + ")}";
        System.out.println(query);
        ResultSet retVal = OntologyUtils.execSelect(StringUtils.URLquery, query);

        String identifier="", title="", description="", PositionTitle=""; 
        ArrayList<Knowledge> knowledge ;
        String knowledgeCode="";
        ArrayList<QueryResult> results = new ArrayList<QueryResult>();
        QuerySolution soln = null;


        while (retVal.hasNext()) {
            identifier=""; title=""; description=""; PositionTitle=""; 
            knowledge= new ArrayList<Knowledge>();
            soln = retVal.next();
            identifier = soln.get("s").toString().replaceAll(StringUtils.namespaceInternship, "");
            results = query(identifier, QueryType.SUBJECT);
            for (QueryResult queryResult2 : results) {
                if (queryResult2.getPredicate().equals("InternshipCode"))
                    identifier = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("InternshipDescription"))
                    description = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("InternshipPositionTitle"))
                    PositionTitle = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("InternshipTitle"))
                    title = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("requires")){
                    KnowledgeStore ks= new KnowledgeStore();
                    ArrayList<QueryResult> results2 = ks.query(queryResult2.getObject().split("#")[1], QueryType.SUBJECT);
                    for(QueryResult queryResult3 :results2){
                        if (queryResult3.getPredicate().equals("Code"))
                            knowledgeCode = queryResult3.getObject();

                        knowledge.add(ks.getKnowledge(knowledgeCode));
                        break;
                    }
                }
            }
            institutions.add(new Internship(title, identifier, description, PositionTitle,knowledge));
        }
        if(!insS.getSelectedProgramme().equals("") || !insS.getKnowledges().isEmpty() || !insS.getCourses().isEmpty()){
            KnowledgeStore ks= new KnowledgeStore();
            ArrayList<Knowledge> progK= new ArrayList<Knowledge>();
            if(!insS.getSelectedProgramme().equals(""))
            progK=ks.getKnowledgeOfProgramme(insS.getSelectedProgramme());
            ArrayList<Knowledge> courseK= new ArrayList<Knowledge>();
            for(String s:insS.getCourses()){
                courseK.addAll(ks.getKnowledgeOfCourse(s));
            }
            return InternshipRecommendation.recommendInternshipforSearch(institutions,insS.getSelectedProgramme(),
                    progK,courseK,insS.getKnowledges());
        }
        else
        return institutions;
    }

}
