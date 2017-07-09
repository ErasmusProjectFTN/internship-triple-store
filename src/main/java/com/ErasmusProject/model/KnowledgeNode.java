package com.ErasmusProject.model;

import java.util.HashMap;

public class KnowledgeNode {

    private String Code;
    private HashMap<String,KnowledgeNode> parents;
    private HashMap<String,KnowledgeNode> dependsOn;
    public KnowledgeNode() {
        super();
        parents=new HashMap<String, KnowledgeNode>();
        dependsOn=new HashMap<String, KnowledgeNode>();
    }
    public KnowledgeNode(String code, HashMap<String, KnowledgeNode> parents,
            HashMap<String, KnowledgeNode> dependsOn) {
        super();
        Code = code;
        this.parents = parents;
        this.dependsOn = dependsOn;
    }
    public String getCode() {
        return Code;
    }
    public void setCode(String code) {
        Code = code;
    }
    public HashMap<String, KnowledgeNode> getParents() {
        return parents;
    }
    public void setParents(HashMap<String, KnowledgeNode> parents) {
        this.parents = parents;
    }
    public HashMap<String, KnowledgeNode> getDependsOn() {
        return dependsOn;
    }
    public void setDependsOn(HashMap<String, KnowledgeNode> dependsOn) {
        this.dependsOn = dependsOn;
    }
    @Override
    public String toString() {
        return "KnowledgeNode [Code=" + Code + ", parents=" + parents
                + ", dependsOn=" + dependsOn + "]";
    }
    
}
