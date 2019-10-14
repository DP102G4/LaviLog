package com.example.lavilog.Daily;


import java.io.Serializable;

public class Answer implements Serializable {
    private String question;
    private String answer;
    private String imagePath;
    private String textClock;
    private String id;


    public Answer() { //先不傳值所以為空
    }

    public Answer(String question,String answer, String imagePath, String textClock,String id) {
        this.question = question;
        this.answer = answer;
        this.imagePath = imagePath;
        this.textClock = textClock;
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

