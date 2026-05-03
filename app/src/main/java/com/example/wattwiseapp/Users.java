package com.example.wattwiseapp;

public class Users {

    private int id;
    private String fullname;
    private String emailAddress;
    private String password;
    private String DOB; //date of birth
    private String phoneNumber;
    private String bio;


    //construtor
    public Users(int id, String fullname, String emailAddress, String password, String DOB, String phoneNumber, String bio) {
        this.id = id;
        this.fullname = fullname;
        this.emailAddress = emailAddress;
        this.password = password;
        this.DOB = DOB;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

