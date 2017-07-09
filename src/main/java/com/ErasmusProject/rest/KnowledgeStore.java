package com.ErasmusProject.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ErasmusProject.model.Internship;
import com.ErasmusProject.model.Knowledge;
import com.ErasmusProject.model.KnowledgeNode;
import com.ErasmusProject.model.KnowledgeSearch;
import com.ErasmusProject.util.OntologyUtils;
import com.ErasmusProject.util.QueryResult;
import com.ErasmusProject.util.QueryType;
import com.ErasmusProject.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeStore {
    public static HashMap<String,KnowledgeNode> knowledgeMatrix;
    
    @RequestMapping(method = RequestMethod.POST, value="/createKnowledgeMatrix")
    public HashMap<String,KnowledgeNode> createKnowledgeMatrix(){
        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceKnowledge);
        namespaces.add(StringUtils.namespaceW3c);

        retVal = queryKnowledge("Code", QueryType.PREDICATE);

        HashMap<String,KnowledgeNode> nodes = new HashMap<String,KnowledgeNode>();

        String Code = "";
        String identifier;
        KnowledgeNode node;
        ArrayList<QueryResult> results = new ArrayList<QueryResult>();


        for (QueryResult queryResult : retVal) {
            node=new KnowledgeNode();
            Code = "";
            identifier="";
            Code = queryResult.getSubject();
            results = query(Code, QueryType.SUBJECT);
            for (QueryResult queryResult2 : results) {
                if (queryResult2.getPredicate().equals("Code"))
                    Code = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("dependsOn")){
                    identifier= queryResult2.getObject();
                    String subject = "<" + StringUtils.namespaceKnowledge + identifier + ">";
                    
                    ArrayList<QueryResult> results2 =  OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,subject,"?p","?o","?p","\""+StringUtils.namespaceKnowledge +"\""), namespaces, QueryType.SUBJECT, identifier);
                    
                    for (QueryResult queryResult3 : results2) {
                        if (queryResult3.getPredicate().equals("Code"))
                            identifier = queryResult3.getObject();
                   
                    node.getDependsOn().put(identifier, null);
                    }
                }
                else if (queryResult2.getPredicate().equals("isContainedIn")){
                    identifier = queryResult2.getObject();
                    String subject = "<" + StringUtils.namespaceKnowledge + identifier + ">";
                    
                    ArrayList<QueryResult> results2 =  OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,subject,"?p","?o","?p","\""+StringUtils.namespaceKnowledge +"\""), namespaces, QueryType.SUBJECT, identifier);
                    
                    for (QueryResult queryResult3 : results2) {
                        if (queryResult3.getPredicate().equals("Code"))
                            identifier = queryResult3.getObject();
                   
                    node.getParents().put(identifier, null);
                    }
                }
            }
            nodes.put(Code, node);
        }
        for(KnowledgeNode n:nodes.values()){
            for(String s:n.getParents().keySet())
                n.getParents().put(s, nodes.get(s));

            for(String s:n.getDependsOn().keySet())
                n.getDependsOn().put(s, nodes.get(s));
        }
        
        knowledgeMatrix=nodes;
        return knowledgeMatrix;
    }
    
    @RequestMapping(method = RequestMethod.POST, value="/addKnowledge")
    public Knowledge addKnowledge(@RequestParam(value="Code", required=true) String identifier,
                                @RequestParam(value="Description", required=true) String description,
                                @RequestParam(value="Name", required=false) String name,
                                @RequestParam(value="NotableContributors", required=false) String contributors,
                                @RequestParam(value="NotableLiterature", required=false) String literature,
                                @RequestParam(value="PracticalApplications", required=false) String applications,
                                @RequestParam(value="LaboratoryRequirements", required=false) String lab,
                                @RequestParam(value="RequiredTeachingCredentials", required=false) String teaching)
    {
        String query = "PREFIX int: <" + StringUtils.namespaceKnowledge + "> "
                        +"INSERT DATA"
                        +"{" 
                        +"  int:" + identifier + " int:Code \"" + identifier.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:Name \"" + name.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:Description \"" + description.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:NotableContributors \"" + contributors.replaceAll("[\\t\\n\\r]","") + "\" ;"                        
                        +"                         int:PracticalApplications \"" + applications.replaceAll("[\\t\\n\\r]","") + "\" ;"                        
                        +"                         int:RequiredTeachingCredentials \"" + teaching.replaceAll("[\\t\\n\\r]","") + "\" ;"                        
                        +"                         int:LaboratoryRequirements \"" + lab.replaceAll("[\\t\\n\\r]","") + "\" ;"
                        +"                         int:NotableLiterature \"" + literature.replaceAll("[\\t\\n\\r]"," ") + "\" ."
                        +"}";

        System.out.println(query);
        try{
            OntologyUtils.execUpdate(StringUtils.URLupdate, query);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new Knowledge(identifier,"","",name,"","","","");
    }@RequestMapping(method = RequestMethod.GET, value="/getKnowledge")
    public Knowledge getKnowledge(@RequestParam("identifier")String Code)
    {
        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceKnowledge);
        namespaces.add(StringUtils.namespaceW3c);

        // get programme specification ids
        String query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceKnowledge + "Code> \""+Code+"\"}";
        System.out.println(query);
        ResultSet result = OntologyUtils.execSelect(StringUtils.URLquery, query);
        System.out.println(result.hasNext());

        QuerySolution soln = result.next();
        String identifier = soln.get("s").toString().replaceAll(StringUtils.namespaceKnowledge, "");

        String courseUnitCode = Code;
        String Description = "",
        LaboratoryRequirements = "",
        Name = "",
        NotableContributors = "",
        NotableLiterature = "",
        PracticalApplications = "",
        RequiredTeachingCredentials = "";    
        // get programme instance data
        retVal = query(identifier, QueryType.SUBJECT);
        for (QueryResult queryResult2 : retVal) {
            if (queryResult2.getPredicate().equals("Description"))
                Description = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("LaboratoryRequirements"))
                LaboratoryRequirements = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("Name"))
                Name = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("NotableContributors"))
                NotableContributors = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("NotableLiterature"))
                NotableLiterature = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("PracticalApplications"))
                PracticalApplications = queryResult2.getObject();
            else if (queryResult2.getPredicate().equals("RequiredTeachingCredentials"))
                RequiredTeachingCredentials = queryResult2.getObject();
        }
        return new Knowledge(courseUnitCode,Description , LaboratoryRequirements, Name,NotableContributors,NotableLiterature,PracticalApplications,RequiredTeachingCredentials);
    }
    @RequestMapping(method = RequestMethod.GET, value="/getKnowledges")
    public ArrayList<Knowledge> getKnowledges()
    {
        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceKnowledge);
        namespaces.add(StringUtils.namespaceW3c);

        retVal = queryKnowledge("Code", QueryType.PREDICATE);

        ArrayList<Knowledge> institutions = new ArrayList<>();

        String Code = "",
        Description = "",
        LaboratoryRequirements = "",
        Name = "",
        NotableContributors = "",
        NotableLiterature = "",
        PracticalApplications = "",
        RequiredTeachingCredentials = "";    
        ArrayList<QueryResult> results = new ArrayList<QueryResult>();


        for (QueryResult queryResult : retVal) {

            Code = "";
            Description = "";
            LaboratoryRequirements = "";
            Name = "";
            NotableContributors = "";
            NotableLiterature = "";
            PracticalApplications = "";
            RequiredTeachingCredentials = "";   
            Code = queryResult.getSubject();
            results = query(Code, QueryType.SUBJECT);
            for (QueryResult queryResult2 : results) {
                if (queryResult2.getPredicate().equals("Code"))
                    Code = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("Description"))
                    Description = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("LaboratoryRequirements"))
                    LaboratoryRequirements = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("Name"))
                    Name = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("NotableContributors"))
                    NotableContributors = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("NotableLiterature"))
                    NotableLiterature = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("PracticalApplications"))
                    PracticalApplications = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("RequiredTeachingCredentials"))
                    RequiredTeachingCredentials = queryResult2.getObject();
            }
            institutions.add(new Knowledge(Code,Description,LaboratoryRequirements,Name,NotableContributors,NotableLiterature,PracticalApplications,RequiredTeachingCredentials));
        }

        return institutions;
    }
    @RequestMapping(method = RequestMethod.GET, value = "/queryKnowledge")
    public ArrayList<QueryResult> queryKnowledge(@RequestParam("value") String val,
                              @RequestParam("type") QueryType type)
    {

        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceKnowledge);

        switch(type)
        {
            case SUBJECT:
                String subject = "<" + StringUtils.namespaceKnowledge + val + ">";
                retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,subject,"?p","?o","?p","\""+StringUtils.namespaceKnowledge +"\""), namespaces, type, val);
                break;
            case PREDICATE:
                String predicate = "<" + StringUtils.namespaceKnowledge + val + ">";
                retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s",predicate,"?o","?s","\""+StringUtils.namespaceKnowledge +"\""), namespaces, type, val);
                break;
            case OBJECT:
                retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s","?p","\"" + val + "\"","?p","\""+StringUtils.namespaceKnowledge +"\""), namespaces, type, val);
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
        namespaces.add(StringUtils.namespaceKnowledge);
        namespaces.add(StringUtils.namespaceW3c);


        switch(type)
        {
        case SUBJECT:
            String subject = "<" + StringUtils.namespaceKnowledge + val + ">";
            retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,subject,"?p","?o","?p","\""+StringUtils.namespaceKnowledge +"\""), namespaces, type, val);
            break;
        case PREDICATE:
            String predicate = "<" + StringUtils.namespaceKnowledge + val + ">";
            retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s",predicate,"?o","?s","\""+StringUtils.namespaceKnowledge +"\""), namespaces, type, val);
            break;
        case OBJECT:
            retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s","?p","\"" + val + "\"","?p","\""+StringUtils.namespaceKnowledge +"\""), namespaces, type, val);
            break;
        }

        return retVal;
    }
    @RequestMapping(method = RequestMethod.GET, value="/searchKnowledge")
    public ArrayList<Knowledge> searchKnowledge(@RequestParam("knowledge") String knowledge){
        ArrayList<Knowledge> institutions = new ArrayList<Knowledge>();
        System.out.println(knowledge);
        ObjectMapper mapper = new ObjectMapper();
        KnowledgeSearch insS = new KnowledgeSearch();
        try {
            insS = mapper.readValue(knowledge, KnowledgeSearch.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        String Description = insS.getDescription().equals("")?"":" ?s <" + StringUtils.namespaceKnowledge + "Description> ?desc.";
        String LaboratoryRequirements = insS.getLaboratoryRequirements().equals("")?"":" ?s <" + StringUtils.namespaceKnowledge + "LaboratoryRequirements> ?lab.";
        String NotableContributors  = insS.getNotableContributors().equals("")?"":" ?s <" + StringUtils.namespaceKnowledge + "NotableContributors> ?cont.";
        String NotableLiterature = insS.getNotableLiterature().equals("")?"":" ?s <" + StringUtils.namespaceKnowledge + "NotableLiterature> ?lit.";
        String PracticalApplications = insS.getPracticalApplications().equals("")?"":" ?s <" + StringUtils.namespaceKnowledge + "PracticalApplications> ?app.";
        String RequiredTeachingCredentials  = insS.getRequiredTeachingCredentials().equals("")?"":" ?s <" + StringUtils.namespaceKnowledge + "RequiredTeachingCredentials> ?cred.";
       
        String desc = insS.getDescription().equals("")?"":"&&         CONTAINS(LCASE(STR(?desc)), \""+ insS.getDescription().toLowerCase() +"\")";
        String lab = insS.getLaboratoryRequirements().equals("")?"":"&&          CONTAINS(LCASE(STR(?lab)), \""+ insS.getLaboratoryRequirements().toLowerCase() +"\")";
        String cont = insS.getNotableContributors().equals("")?"":"&&          CONTAINS(LCASE(STR(?cont)), \""+ insS.getNotableContributors().toLowerCase() +"\")";
        String lit = insS.getNotableLiterature().equals("")?"":"&&         CONTAINS(LCASE(STR(?lit)), \""+ insS.getNotableLiterature().toLowerCase() +"\")";
        String app = insS.getPracticalApplications().equals("")?"":"&&          CONTAINS(LCASE(STR(?app)), \""+ insS.getPracticalApplications().toLowerCase() +"\")";
        String cred = insS.getRequiredTeachingCredentials().equals("")?"":"&&          CONTAINS(LCASE(STR(?cred)), \""+ insS.getRequiredTeachingCredentials().toLowerCase() +"\")";
       
        String query = "SELECT DISTINCT ?s" 
                +" WHERE {"
                +" ?s <" + StringUtils.namespaceKnowledge + "Code> ?id."
                +" ?s <" + StringUtils.namespaceKnowledge + "Name> ?name."
                + Description
                + LaboratoryRequirements
                + NotableContributors
                + NotableLiterature
                + PracticalApplications
                + RequiredTeachingCredentials
                +" FILTER (CONTAINS(LCASE(STR(?id)), \"" + insS.getCode().toLowerCase() + "\") &&"
                +"         CONTAINS(LCASE(STR(?name)), \""+ insS.getName().toLowerCase() +"\")"
                + desc
                + lab
                + cont
                + lit
                + app
                + cred
                + ")}";
        System.out.println(query);
        ResultSet retVal = OntologyUtils.execSelect(StringUtils.URLquery, query);

        String Code = "",
        descr = "",
        labo = "",
        Name = "",
        notcont = "",
        notlit = "",
        papp = "",
        rec = "";
        ArrayList<QueryResult> results = new ArrayList<QueryResult>();
        QuerySolution soln = null;


        while (retVal.hasNext()) {
            soln = retVal.next();
            Code = soln.get("s").toString().replaceAll(StringUtils.namespaceKnowledge, "");
            results = query(Code, QueryType.SUBJECT);
            for (QueryResult queryResult2 : results) {
                if (queryResult2.getPredicate().equals("Code"))
                    Code = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("Description"))
                    descr = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("LaboratoryRequirements"))
                    labo = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("Name"))
                    Name = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("NotableContributors"))
                    notcont = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("NotableLiterature"))
                    notlit = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("PracticalApplications"))
                    papp = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("RequiredTeachingCredentials"))
                    rec = queryResult2.getObject();
            }
            institutions.add(new Knowledge(Code,descr, labo, Name,notcont,notlit,papp,rec));
        }
        return institutions;
    }
    @RequestMapping(method = RequestMethod.GET, value="/getKnowledgeOfProgrammes")
    public HashMap<String,ArrayList<Knowledge>> getKnowledgeOfProgrammes()
    {
        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceEcts);
        namespaces.add(StringUtils.namespaceW3c);
        namespaces.add(StringUtils.namespaceKnowledge);
        retVal = queryEcts("DegreeUnitCode", QueryType.PREDICATE);

        HashMap<String,ArrayList<Knowledge>> degreeProgrammes = new HashMap<>();

        String identifier="",courseIdentifier,knowledgeIdentifier;
        //Double credits = -1.0;
        ArrayList<QueryResult> results = new ArrayList<QueryResult>();
        ArrayList<Knowledge> know= new ArrayList<Knowledge>();

        for (QueryResult queryResult : retVal) {
            know= new ArrayList<Knowledge>();
            identifier = queryResult.getSubject();
            results = queryEcts(identifier, QueryType.SUBJECT);
            for (QueryResult queryResult2 : results) {
                if (queryResult2.getPredicate().equals("DegreeUnitCode"))
                    identifier = queryResult2.getObject();
                else if (queryResult2.getPredicate().equals("contains")){
                    courseIdentifier = queryResult2.getObject();

                    String subject = "<" + StringUtils.namespaceEcts + courseIdentifier + ">";
                    
                    ArrayList<QueryResult> results2 =  OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,subject,"?p","?o","?p","\""+StringUtils.namespaceKnowledge +"\""), namespaces, QueryType.SUBJECT, courseIdentifier);
                    
                    for (QueryResult queryResult3 : results2) {
                        if (queryResult3.getPredicate().equals("CourseUnitCode"))
                            courseIdentifier = queryResult3.getObject();
                        else if (queryResult3.getPredicate().equals("teaches")){
                            knowledgeIdentifier=queryResult3.getObject();
                            ArrayList<QueryResult> results3 = query(queryResult3.getObject(), QueryType.SUBJECT);
                            for(QueryResult queryResult4 :results3){
                                if (queryResult4.getPredicate().equals("Code"))
                                    knowledgeIdentifier = queryResult4.getObject();

                                know.add(getKnowledge(knowledgeIdentifier));
                                break;
                            }
                        }
                    }
                }
            }
            degreeProgrammes.put(identifier, know);
        }
        return degreeProgrammes;
    }
    public ArrayList<QueryResult> queryEcts(@RequestParam("value") String val,
            @RequestParam("type") QueryType type)
    {

        ArrayList<String> namespaces = new ArrayList<String>();
        ArrayList<QueryResult> retVal = null;
        namespaces.add(StringUtils.namespaceEcts);
        namespaces.add(StringUtils.namespaceW3c);
        namespaces.add(StringUtils.namespaceKnowledge);


        switch(type)
        {
        case SUBJECT:
            String subject = "<" + StringUtils.namespaceEcts + val + ">";
            retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,subject,"?p","?o","?p","\""+StringUtils.namespaceEcts +"\""), namespaces, type, val);
            break;
        case PREDICATE:
            String predicate = "<" + StringUtils.namespaceEcts + val + ">";
            retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s",predicate,"?o","?s","\""+StringUtils.namespaceEcts +"\""), namespaces, type, val);
            break;
        case OBJECT:
            retVal = OntologyUtils.formatedSelect(StringUtils.URLquery, String.format(StringUtils.sparqlTemplate,"?s","?p","\"" + val + "\"","?p","\""+StringUtils.namespaceEcts +"\""), namespaces, type, val);
            break;
        }

        return retVal;
    }
    @RequestMapping(method = RequestMethod.POST, value="/dependKnowledge")
    public Knowledge dependKnowledge(@RequestParam("parentKnowledge")String parentKnowledge,
            @RequestParam("sonKnowledge")String sonKnowledge)
    {   
        
        
        String query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceKnowledge + "Code> \""+parentKnowledge+"\"}";
        ResultSet result = OntologyUtils.execSelect(StringUtils.URLquery, query);

        QuerySolution soln = result.next();
        String identifierKnowledgeParent = soln.get("s").toString().replaceAll(StringUtils.namespaceKnowledge, "");
        
        query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceKnowledge + "Code> \""+sonKnowledge+"\"}";
        result = OntologyUtils.execSelect(StringUtils.URLquery, query);

        soln = result.next();
        String identifierKnowledgeSon = soln.get("s").toString().replaceAll(StringUtils.namespaceKnowledge, "");

        query = "INSERT DATA{<"+StringUtils.namespaceKnowledge + identifierKnowledgeParent+"> <"+StringUtils.namespaceKnowledge+"dependsOn> <"+StringUtils.namespaceKnowledge+identifierKnowledgeSon+">}";
        OntologyUtils.execUpdate(StringUtils.URLupdate, query);
        return null;
    }
    @RequestMapping(method = RequestMethod.POST, value="/parentKnowledge")
    public Knowledge parentKnowledge(@RequestParam("parentKnowledge")String parentKnowledge,
            @RequestParam("sonKnowledge")String sonKnowledge)
    {   
        
        
        String query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceKnowledge + "Code> \""+parentKnowledge+"\"}";
        ResultSet result = OntologyUtils.execSelect(StringUtils.URLquery, query);

        QuerySolution soln = result.next();
        String identifierKnowledgeParent = soln.get("s").toString().replaceAll(StringUtils.namespaceKnowledge, "");
        
        query = "SELECT ?s WHERE {?s <" + StringUtils.namespaceKnowledge + "Code> \""+sonKnowledge+"\"}";
        result = OntologyUtils.execSelect(StringUtils.URLquery, query);

        soln = result.next();
        String identifierKnowledgeSon = soln.get("s").toString().replaceAll(StringUtils.namespaceKnowledge, "");

        query = "INSERT DATA{<"+StringUtils.namespaceKnowledge + identifierKnowledgeParent+"> <"+StringUtils.namespaceKnowledge+"isContainedIn> <"+StringUtils.namespaceKnowledge+identifierKnowledgeSon+">}";
        OntologyUtils.execUpdate(StringUtils.URLupdate, query);
        return null;
    }
}
