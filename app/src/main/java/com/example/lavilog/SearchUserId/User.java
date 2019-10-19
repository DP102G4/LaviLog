package com.example.lavilog.SearchUserId;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String imagePath;
    private String account;


    public User() {

    }

    public User(String name, String imagePath, String account) {
        this.name = name;
        this.imagePath = imagePath;
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

