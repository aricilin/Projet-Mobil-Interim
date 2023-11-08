package com.aricilingiroglu.interimproject;

public class User {
    private String name;
    private String surname;
    private String birthdate;
    private String nationality;
    private String cvUrl;
    private String etat;
    private String uid;
    private String usrId;
    private String paid;
    private String accountStatus;

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    private String email;



    private String phoneNumber;



    public User() {}



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthdate() {
        return birthdate;
    }
    public String getEtat() {
        return etat;
    }
    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getNationality() {

        return nationality;
    }

    public void setNationality(String nationality) {

        this.nationality = nationality;
    }
    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }


}
