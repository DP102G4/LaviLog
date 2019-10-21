package com.example.lavilog.Daily;

public class LogBook {
    private String image; // 照片
    private String name; // 名稱

    public LogBook(){super();}

    public LogBook(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
