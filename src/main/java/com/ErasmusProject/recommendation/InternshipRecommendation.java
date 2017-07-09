package com.ErasmusProject.recommendation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ErasmusProject.model.Internship;
import com.ErasmusProject.model.Knowledge;
import com.ErasmusProject.model.KnowledgeNode;
import com.ErasmusProject.rest.InternshipStore;
import com.ErasmusProject.rest.KnowledgeStore;


public class InternshipRecommendation {
    
    public static class SimilarityValue{
        private String programme;
        private String internship;
        private Double value;
        public String getProgramme() {
            return programme;
        }
        public void setProgramme(String programme) {
            this.programme = programme;
        }
        public String getInternship() {
            return internship;
        }
        public void setInternship(String internship) {
            this.internship = internship;
        }
        public Double getValue() {
            return value;
        }
        public void setValue(Double value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return "SimilarityValue [programme=" + programme + ", internship="
                    + internship + ", value=" + value + "]";
        }
    }
    public static ArrayList<Internship> recommendInternshipforSearch(ArrayList<Internship> internships, String programmeCode, ArrayList<Knowledge> progK, ArrayList<Knowledge> collection, ArrayList<String> knowledge){
        System.out.println(internships);
        System.out.println(progK);
        System.out.println(collection);
        System.out.println(knowledge);
        LinkedHashMap<Internship, Double> programmeMatrix=generateProgrammeMatrix(internships,programmeCode,progK);
        LinkedHashMap<Internship, Double> courseMatrix=generateCourseMatrix(internships,collection);
        LinkedHashMap<Internship, Double> knowledgeMatrix=generateKnowledgeMatrix(internships,knowledge);
        
        LinkedHashMap<Internship, Double> appropriateInternships = new LinkedHashMap<>();
        for(Internship i: programmeMatrix.keySet())
            if(appropriateInternships.containsKey(i))
                appropriateInternships.put(i, appropriateInternships.get(i)+programmeMatrix.get(i));
            else
                appropriateInternships.put(i,programmeMatrix.get(i));
        for(Internship i: courseMatrix.keySet())
            if(appropriateInternships.containsKey(i))
                appropriateInternships.put(i, appropriateInternships.get(i)+2*courseMatrix.get(i));
            else
                appropriateInternships.put(i,2*courseMatrix.get(i));
        for(Internship i: knowledgeMatrix.keySet())
            if(appropriateInternships.containsKey(i))
                appropriateInternships.put(i, appropriateInternships.get(i)+3*knowledgeMatrix.get(i));
            else
                appropriateInternships.put(i,3*knowledgeMatrix.get(i));
            
        
        List<Map.Entry<Internship, Double>> entries =
                new ArrayList<Map.Entry<Internship, Double>>(appropriateInternships.entrySet());
              Collections.sort(entries, new Comparator<Map.Entry<Internship, Double>>() {
                public int compare(Map.Entry<Internship, Double> a, Map.Entry<Internship, Double> b){
                  return b.getValue().compareTo(a.getValue());
                }
              });
              LinkedHashMap<Internship, Double> sortedMap = new LinkedHashMap<Internship, Double>();
              for (Map.Entry<Internship, Double> entry : entries) {
                sortedMap.put(entry.getKey(), entry.getValue());
              }
        
        return new ArrayList<Internship>(sortedMap.keySet());
    }
    
    private static LinkedHashMap<Internship, Double> generateKnowledgeMatrix(
            ArrayList<Internship> internships, ArrayList<String> knowledge) {
        LinkedHashMap<Internship, Double> utilityMatrix = new LinkedHashMap<Internship, Double>();
        if(knowledge.size()==0)return utilityMatrix;
        for(Internship i:internships){

            HashMap<String, Double> values1 = new HashMap<>();
            HashMap<String, Double> values2 = new HashMap<>();
            Double value = 0.0;
            SimilarityValue sv = new SimilarityValue();
            sv.setInternship(i.getInternshipCode());
            for(Knowledge k:i.getKnowledge()){
                if(knowledge.contains(k.getCode()))
                    values1.put(k.getCode(),1.0);
                else{
                    KnowledgeNode node = KnowledgeStore.knowledgeMatrix.get(k.getCode());
                    boolean parent=false;
                    for(String s:node.getParents().keySet()){
                        if(knowledge.contains(s)){
                            parent=true;
                            System.out.println("found parent");
                            break;
                        }
                    }
                    if(parent)
                        values1.put(k.getCode(), 0.5);
                    else
                        values1.put(k.getCode(), 0.0);
                }
                values2.put(k.getCode(), 1.0);
            }
            for(String str:knowledge){
                if(!values1.containsKey(str)){
                    values1.put(str, 1.0);
                    values2.put(str, 0.0);
                }
            }
            if(!values1.isEmpty())
            value=cosineSimilarity(values1.values(), values2.values());
            System.out.println(value);
            sv.setValue(value);
            utilityMatrix.put(i,sv.getValue());

        }
    return utilityMatrix;
    }

    private static LinkedHashMap<Internship, Double> generateCourseMatrix(
            ArrayList<Internship> internships,
            ArrayList<Knowledge> knowledge) {
        LinkedHashMap<Internship, Double> utilityMatrix = new LinkedHashMap<Internship, Double>();
        if(knowledge.size()==0)
            return utilityMatrix;
        
        ArrayList<String> prog= new ArrayList<String>();
        for(Knowledge k:knowledge)prog.add(k.getCode());
        
        for(Internship i:internships){

            HashMap<String, Double> values1 = new HashMap<>();
            HashMap<String, Double> values2 = new HashMap<>();
            Double value = 0.0;
            SimilarityValue sv = new SimilarityValue();
            sv.setInternship(i.getInternshipCode());
            for(Knowledge k:i.getKnowledge()){
                if(prog.contains(k.getCode()))
                    values1.put(k.getCode(),1.0);
                else{
                    KnowledgeNode node = KnowledgeStore.knowledgeMatrix.get(k.getCode());
                    boolean parent=false;
                    for(String s:node.getParents().keySet()){
                        if(prog.contains(s)){
                            parent=true;
                            System.out.println("found parent");
                            break;
                        }
                    }
                    if(parent)
                        values1.put(k.getCode(), 0.5);
                    else
                        values1.put(k.getCode(), 0.0);
                }
                values2.put(k.getCode(), 1.0);
            }
            for(String str:prog){
                if(!values1.containsKey(str)){
                    values1.put(str, 1.0);
                    values2.put(str, 0.0);
                }
            }
            if(!values1.isEmpty())
            value=cosineSimilarity(values1.values(), values2.values());
            System.out.println(value);
            sv.setValue(value);
            utilityMatrix.put(i,sv.getValue());

        }
    return utilityMatrix;
    }

    public static LinkedHashMap<String, Double> recommendInternshipforProgramme(String programmeId){
        LinkedHashMap<String, Double> appropriateInternships = new LinkedHashMap<>();
        
            for(SimilarityValue sv:InternshipStore.utilityMatrix.get(programmeId)){
                appropriateInternships.put(sv.getInternship(),sv.getValue());
            }
               
        
        //sort
        List<Map.Entry<String, Double>> entries =
                new ArrayList<Map.Entry<String, Double>>(appropriateInternships.entrySet());
              Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b){
                  return a.getValue().compareTo(b.getValue());
                }
              });
              LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
              for (Map.Entry<String, Double> entry : entries) {
                sortedMap.put(entry.getKey(), entry.getValue());
              }
        
        return sortedMap;
    }
    private static LinkedHashMap<Internship, Double> generateProgrammeMatrix(ArrayList<Internship> internships, String programmeCode, ArrayList<Knowledge> progK){
        ArrayList<String> prog= new ArrayList<String>();
        for(Knowledge k:progK)prog.add(k.getCode());
        LinkedHashMap<Internship, Double> utilityMatrix = new LinkedHashMap<Internship, Double>();
        if(progK.size()==0)
            return utilityMatrix;
        for(Internship i:internships){

            HashMap<String, Double> values1 = new HashMap<>();
            HashMap<String, Double> values2 = new HashMap<>();
            Double value = 0.0;
            SimilarityValue sv = new SimilarityValue();
            sv.setProgramme(programmeCode);
            sv.setInternship(i.getInternshipCode());
            for(Knowledge k:i.getKnowledge()){
                if(prog.contains(k.getCode()))
                    values1.put(k.getCode(),1.0);
                else{
                    KnowledgeNode node = KnowledgeStore.knowledgeMatrix.get(k.getCode());
                    boolean parent=false;
                    for(String s:node.getParents().keySet()){
                        if(prog.contains(s)){
                            parent=true;
                            System.out.println("found parent");
                            break;
                        }
                    }
                    if(parent)
                        values1.put(k.getCode(), 0.5);
                    else
                        values1.put(k.getCode(), 0.0);
                }
                values2.put(k.getCode(), 1.0);
            }
            for(String str:prog){
                if(!values1.containsKey(str)){
                    values1.put(str, 1.0);
                    values2.put(str, 0.0);
                }
            }
            if(!values1.isEmpty())
            value=cosineSimilarity(values1.values(), values2.values());
            System.out.println(value);
            sv.setValue(value);
            utilityMatrix.put(i,sv.getValue());

        }
    return utilityMatrix;
    }
    public HashMap<String, ArrayList<SimilarityValue>> generateUtilityMatrixMatrix() {
        KnowledgeStore ks= new KnowledgeStore();
        HashMap<String, ArrayList<Knowledge>> programmesP = ks.getKnowledgeOfProgrammes();
        HashMap<String,HashMap<String,Knowledge>>programmes=new HashMap<String, HashMap<String,Knowledge>>();
        for(String s: programmesP.keySet()){
            programmes.put(s, new HashMap<String, Knowledge>());
            for(Knowledge k:programmesP.get(s)){
                programmes.get(s).put(k.getCode(), k);
            }
        }
        InternshipStore is= new InternshipStore();
        ArrayList<Internship> internships = is.getInternships();
        
        HashMap<String, ArrayList<SimilarityValue>> utilityMatrix= new HashMap<String, ArrayList<SimilarityValue>>();
        SimilarityValue sv = new SimilarityValue();
        for (String key : programmes.keySet()) {
            for(Internship i:internships){

                HashMap<String, Double> values1 = new HashMap<>();
                HashMap<String, Double> values2 = new HashMap<>();
                Double value = 0.0;
                sv = new SimilarityValue();
                sv.setProgramme(key);
                sv.setInternship(i.getInternshipCode());
                for(Knowledge k:i.getKnowledge()){
                    if(programmes.get(key).containsKey(k.getCode()))
                        values1.put(k.getCode(),1.0);
                    else{
                        KnowledgeNode node = KnowledgeStore.knowledgeMatrix.get(k.getCode());
                        boolean parent=false;
                        for(String s:node.getParents().keySet()){
                            if(programmes.get(key).containsKey(s)){
                                parent=true;
                                System.out.println("found parent");
                                break;
                            }
                        }
                        if(parent)
                            values1.put(k.getCode(), 0.5);
                        else
                            values1.put(k.getCode(), 0.0);
                    }
                    values2.put(k.getCode(), 1.0);
                }
                for(String str:programmes.get(key).keySet()){
                    if(!values1.containsKey(str)){
                        values1.put(str, 1.0);
                        values2.put(str, 0.0);
                    }
                }
                if(!values1.isEmpty())
                value=cosineSimilarity(values1.values(), values2.values());
                System.out.println(value);
                sv.setValue(value);
                if(!utilityMatrix.containsKey(key))
                    utilityMatrix.put(key, new ArrayList<SimilarityValue>());
                utilityMatrix.get(key).add(sv);

            }
        }
        return utilityMatrix;
    }
    public static double cosineSimilarity(Collection<Double> vector1, Collection<Double> vector2) {
        Double[] vectorA = vector1.toArray(new Double[vector1.size()]);
        Double[] vectorB = vector2.toArray(new Double[vector2.size()]);

    
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }   
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
}
}
