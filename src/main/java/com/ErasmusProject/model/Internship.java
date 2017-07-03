package com.ErasmusProject.model;

public class Internship {
    private String InternshipTitle;
    private String InternshipCode;
    private String InternshipDescription;
    private String InternshipPositionTitle;
   
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
    @Override
    public String toString() {
        return "Internship [InternshipTitle=" + InternshipTitle
                + ", InternshipCode=" + InternshipCode
                + ", InternshipDescription=" + InternshipDescription
                + ", InternshipPositionTitle=" + InternshipPositionTitle + "]";
    }
}
