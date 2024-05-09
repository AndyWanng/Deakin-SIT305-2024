package com.example.personalizedquizapp;

public class User {
    private String name, email, pass, profile;
    private String tier = "Free";
    private long coins = 0;

    public User() {
    }

    public User(String name, String email, String pass, String profile) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.profile = profile;
    }

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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public long getCoins() {
        return coins;
    }

    public String getTier(){return tier;}

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
