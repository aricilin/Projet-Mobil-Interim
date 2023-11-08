package com.aricilingiroglu.interimproject;

import java.util.Date;

public class JobOffer {
    private String jobId;
    private String jobTitle;
    private String jobDescription;
    private String imageUri;
    private String userEmail;



    private String jobType;
    private long timestamp;
    private String location;
    private String estimatedSalary;
    private String enterpriseName;
    private String lastEditMail;
    private String sourceLink;
    private String DateCreation;
    private String DateDebut;
    private String DateFin;



    public JobOffer() {
    }
    public JobOffer(String jobTitle, String jobDescription, String location, String estimatedSalary, String enterpriseName,String userEmail) {
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.location = location;
        this.estimatedSalary = estimatedSalary;
        this.enterpriseName = enterpriseName;
        this.userEmail=userEmail;

    }

    public JobOffer(String jobTitle, String jobDescription, String userEmail, String jobType, String location, String estimatedSalary, String enterpriseName, String dateDebut, String dateFin) {
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.userEmail = userEmail;
        this.jobType = jobType;
        this.location = location;
        this.estimatedSalary = estimatedSalary;
        this.enterpriseName = enterpriseName;
        DateDebut = dateDebut;
        DateFin = dateFin;
    }

    public String getJobId() {
        return jobId;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEstimatedSalary() {
        return estimatedSalary;
    }

    public void setEstimatedSalary(String estimatedSalary) {
        this.estimatedSalary = estimatedSalary;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }


    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }
    public String getSourceLink() {
        return sourceLink;
    }
    public String getDateCreation() {
        return DateCreation;
    }

    public void setDateCreation(String dateCreation) {
        DateCreation = dateCreation;
    }

    public String getDateDebut() {
        return DateDebut;
    }

    public void setDateDebut(String dateDebut) {
        DateDebut = dateDebut;
    }

    public String getDateFin() {
        return DateFin;
    }

    public void setDateFin(String dateFin) {
        DateFin = dateFin;
    }

}
