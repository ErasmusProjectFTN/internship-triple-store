package com.ErasmusProject.model;

import java.util.ArrayList;

public class InternshipSearch {
    private String InternshipTitle;
    private String InternshipCode;
    private String InternshipDescription;
    
    private String InternshipPositionTitle;
    private ArrayList<String> knowledges;
    private String selectedProgramme;
    private ArrayList<String> courses;
    public InternshipSearch() {
        super();
        InternshipTitle="";
        InternshipCode="";
        InternshipDescription="";
        InternshipPositionTitle="";
        setSelectedProgramme("");

        setCourses(new ArrayList<String>());
        setKnowledges(new ArrayList<String>());
    }
    
    public String getInternshipTitle() {
        return InternshipTitle;
    }
    public void setInternshipTitle(String internshipTitle) {
        InternshipTitle = internshipTitle;
    }
    public String getInternshipCode() {
        return InternshipCode;
    }
    public void setInternshipCode(String internshipCode) {
        InternshipCode = internshipCode;
    }
    public String getInternshipDescription() {
        return InternshipDescription;
    }
    public void setInternshipDescription(String internshipDescription) {
        InternshipDescription = internshipDescription;
    }
    public String getInternshipPositionTitle() {
        return InternshipPositionTitle;
    }
    public void setInternshipPositionTitle(String internshipPositionTitle) {
        InternshipPositionTitle = internshipPositionTitle;
    }

    public String getSelectedProgramme() {
        return selectedProgramme;
    }

    public void setSelectedProgramme(String selectedProgramme) {
        this.selectedProgramme = selectedProgramme;
    }

    public ArrayList<String> getKnowledges() {
        return knowledges;
    }

    public void setKnowledges(ArrayList<String> knowledges) {
        this.knowledges = knowledges;
    }

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<String> courses) {
        this.courses = courses;
    }
}
