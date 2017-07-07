package com.ErasmusProject.model;

import java.util.ArrayList;

public class Internship {
    private String InternshipTitle;
    private String InternshipCode;
    private String InternshipDescription;
    private String InternshipPositionTitle;
    private ArrayList<Knowledge> knowledge;
    public Internship() {
        super();
    }
    public Internship(String internshipTitle, String internshipCode,
            String internshipDescription, String internshipPositionTitle) {
        super();
        InternshipTitle = internshipTitle;
        InternshipCode = internshipCode;
        InternshipDescription = internshipDescription;
        InternshipPositionTitle = internshipPositionTitle;
        setKnowledge(new ArrayList<Knowledge>());
    }
    
    public Internship(String internshipTitle, String internshipCode,
            String internshipDescription, String internshipPositionTitle,
            ArrayList<Knowledge> knowledge) {
        super();
        InternshipTitle = internshipTitle;
        InternshipCode = internshipCode;
        InternshipDescription = internshipDescription;
        InternshipPositionTitle = internshipPositionTitle;
        this.setKnowledge(knowledge);
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
    public ArrayList<Knowledge> getKnowledge() {
        return knowledge;
    }
    public void setKnowledge(ArrayList<Knowledge> knowledge) {
        this.knowledge = knowledge;
    }
    @Override
    public String toString() {
        return "Internship [InternshipTitle=" + InternshipTitle
                + ", InternshipCode=" + InternshipCode
                + ", InternshipDescription=" + InternshipDescription
                + ", InternshipPositionTitle=" + InternshipPositionTitle + "]";
    }
}
