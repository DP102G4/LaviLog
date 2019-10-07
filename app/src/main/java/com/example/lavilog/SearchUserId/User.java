package com.example.lavilog.SearchUserId;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String imagePath;


    public User() {

    }

    public User(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

