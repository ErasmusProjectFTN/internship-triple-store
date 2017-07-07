package com.ErasmusProject.recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ErasmusProject.model.Internship;
import com.ErasmusProject.model.Knowledge;
import com.ErasmusProject.rest.InternshipStore;
import com.ErasmusProject.rest.KnowledgeStore;


public class InternshipRecommendation {
    
    public class SimilarityValue{
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
    
    public static LinkedHashMap<String, Double> recommendInternshipforProgramme(String programmeId){
        LinkedHashMap<String, Double> appropriateInternships = new LinkedHashMap<>();
        for(String key:InternshipStore.utilityMatrix.keySet()){
            if(InternshipStore.utilityMatrix.get(key).getProgramme().equals(programmeId)){
                appropriateInternships.put(InternshipStore.utilityMatrix.get(key).getInternship(), InternshipStore.utilityMatrix.get(key).getValue());
            }
        }
        System.out.println(appropriateInternships);
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

    public HashMap<String, SimilarityValue> generateUtilityMatrixMatrix() {
        KnowledgeStore ks= new KnowledgeStore();
        HashMap<String, ArrayList<Knowledge>> programmes = ks.getKnowledgeOfProgrammes();
        InternshipStore is= new InternshipStore();
        ArrayList<Internship> internships = is.getInternships();
        for(String s:programmes.keySet()){
            System.out.println(s);
            for(Knowledge k:programmes.get(s)){
                System.out.println(k.getName());
            }
        }
        // TODO Auto-generated method stub
        return null;
    }
}
