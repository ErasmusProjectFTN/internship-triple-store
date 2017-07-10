package com.ErasmusProject.model;

import java.util.ArrayList;

public class Company {
    private String CompanyDescription;
    private String CompanyAddress;
    private String CompanyCode;
    private String CompanyWebsite;
    private String CompanyName;
    private ArrayList<Internship> internships;
    public Company() {
        super();
    }
    public Company(String companyDescription, String companyAddress,
            String companyCode, String companyWebsite, String companyName) {
        super();
        CompanyDescription = companyDescription;
        CompanyAddress = companyAddress;
        CompanyCode = companyCode;
        CompanyWebsite = companyWebsite;
        CompanyName = companyName;
    }
    public Company(String companyDescription, String companyAddress,
            String companyCode, String companyWebsite, String companyName,ArrayList<Internship> internship) {
        super();
        CompanyDescription = companyDescription;
        CompanyAddress = companyAddress;
        CompanyCode = companyCode;
        CompanyWebsite = companyWebsite;
        CompanyName = companyName;
        internships=internship;
    }
    public String getCompanyDescription() {
        return CompanyDescription;
    }
    public void setCompanyDescription(String companyDescription) {
        CompanyDescription = companyDescription;
    }
    public String getCompanyAddress() {
        return CompanyAddress;
    }
    public void setCompanyAddress(String companyAddress) {
        CompanyAddress = companyAddress;
    }
    public String getCompanyCode() {
        return CompanyCode;
    }
    public void setCompanyCode(String companyCode) {
        CompanyCode = companyCode;
    }
    public String getCompanyWebsite() {
        return CompanyWebsite;
    }
    public void setCompanyWebsite(String companyWebsite) {
        CompanyWebsite = companyWebsite;
    }
    public String getCompanyName() {
        return CompanyName;
    }
    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }
    public ArrayList<Internship> getInternships() {
        return internships;
    }
    public void setInternships(ArrayList<Internship> internships) {
        this.internships = internships;
    }
}
