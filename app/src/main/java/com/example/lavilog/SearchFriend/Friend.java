package com.example.lavilog.SearchFriend;

import java.io.Serializable;

public class Friend implements Serializable {
    private String name;
    private String imagePath;

    public Friend() {

    }

    public Friend(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
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
}
