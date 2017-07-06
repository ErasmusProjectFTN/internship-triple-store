package com.ErasmusProject.model;

public class Knowledge {
    private String Code;
    private String Description;
    private String LaboratoryRequirements;
    private String Name;
    private String NotableContributors;
    private String NotableLiterature;
    private String PracticalApplications;
    private String RequiredTeachingCredentials;

    public Knowledge() {
        super();
    }

    public Knowledge(String code, String description,
            String laboratoryRequirements, String name,
            String notableContributors, String notableLiterature,
            String practicalApplications, String requiredTeachingCredentials) {
        super();
        Code = code;
        Description = description;
        LaboratoryRequirements = laboratoryRequirements;
        Name = name;
        NotableContributors = notableContributors;
        NotableLiterature = notableLiterature;
        PracticalApplications = practicalApplications;
        RequiredTeachingCredentials = requiredTeachingCredentials;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLaboratoryRequirements() {
        return LaboratoryRequirements;
    }

    public void setLaboratoryRequirements(String laboratoryRequirements) {
        LaboratoryRequirements = laboratoryRequirements;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNotableContributors() {
        return NotableContributors;
    }

    public void setNotableContributors(String notableContributors) {
        NotableContributors = notableContributors;
    }

    public String getNotableLiterature() {
        return NotableLiterature;
    }

    public void setNotableLiterature(String notableLiterature) {
        NotableLiterature = notableLiterature;
    }

    public String getPracticalApplications() {
        return PracticalApplications;
    }

    public void setPracticalApplications(String practicalApplications) {
        PracticalApplications = practicalApplications;
    }

    public String getRequiredTeachingCredentials() {
        return RequiredTeachingCredentials;
    }

    public void setRequiredTeachingCredentials(String requiredTeachingCredentials) {
        RequiredTeachingCredentials = requiredTeachingCredentials;
    }
}
