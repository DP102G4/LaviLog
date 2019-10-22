package com.example.lavilog.SearchFriend;

import java.io.Serializable;

public class Friend implements Serializable {
    private String name;
    private String imagePath;
    private String id;

    public Friend() {

    }

    public Friend(String name, String imagePath, String id) {
        this.name = name;
        this.imagePath = imagePath;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
