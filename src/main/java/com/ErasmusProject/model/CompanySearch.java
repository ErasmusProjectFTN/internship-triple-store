package com.ErasmusProject.model;

public class CompanySearch {
    private String CompanyDescription;
    private String CompanyAddress;
    private String CompanyCode;
    private String CompanyWebsite;
    private String CompanyName;
    public CompanySearch() {
        super();
        CompanyDescription = "";
        CompanyAddress = "";
        CompanyCode = "";
        CompanyWebsite = "";
        CompanyName = "";
    }
    public CompanySearch(String companyDescription, String companyAddress,
            String companyCode, String companyWebsite, String companyName) {
        super();
        CompanyDescription = companyDescription;
        CompanyAddress = companyAddress;
        CompanyCode = companyCode;
        CompanyWebsite = companyWebsite;
        CompanyName = companyName;
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
}
