package com.example.lavilog;


import java.io.Serializable;

public class Answer implements Serializable {
    private String article;

    public Answer() { //先不傳值所以為空
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Answer(String article) {
        this.article = article;
    }
}

