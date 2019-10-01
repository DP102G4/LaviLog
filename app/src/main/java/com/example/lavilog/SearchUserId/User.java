package com.example.lavilog.SearchUserId;

import android.icu.text.PluralRules;
import android.media.Image;

public class User {
    private int ImageId;
    private String name;

    public User(int imageId, String name) {
        ImageId = imageId;
        this.name = name;
    }

    public User() {
    }

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
