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
    }@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((InternshipCode == null) ? 0 : InternshipCode.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Internship other = (Internship) obj;
        if (InternshipCode == null) {
            if (other.InternshipCode != null)
                return false;
        } else if (!InternshipCode.equals(other.InternshipCode))
            return false;
        return true;
    }
}
