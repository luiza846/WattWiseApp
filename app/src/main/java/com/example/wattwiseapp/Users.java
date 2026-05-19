package com.example.wattwiseapp;

public class Users {

    private int id;
    private String fullname;
    private String emailAddress;
    private String password;


    //construtor vazio para que o firebase possa reconstruir o objeto a partir dos dados que estão na nuvem.
    public Users() {

    }

    //construtor
    public Users(int id, String fullname, String emailAddress, String password) {
        this.id = id;
        this.fullname = fullname;
        this.emailAddress = emailAddress;
        this.password = password;
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

}
