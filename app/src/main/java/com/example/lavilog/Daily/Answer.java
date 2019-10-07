package com.example.lavilog.Daily;


import java.io.Serializable;

public class Answer implements Serializable {
    private String article;
    private String imagePath;
    private String textClock;
    private String id;


    public Answer() { //先不傳值所以為空
    }

    public Answer(String article, String imagePath, String textClock,String id) {
        this.article = article;
        this.imagePath = imagePath;
        this.textClock = textClock;
        this.id = id;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTextClock() {
        return textClock;
    }

    public void setTextClock(String textClock) {
        this.textClock = textClock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

