package com.ErasmusProject.model;

public class InternshipSearch {
    private String InternshipTitle;
    private String InternshipCode;
    private String InternshipDescription;
    
    private String InternshipPositionTitle;
    
    public InternshipSearch() {
        super();
        InternshipTitle="";
        InternshipCode="";
        InternshipDescription="";
        InternshipPositionTitle="";
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
}
