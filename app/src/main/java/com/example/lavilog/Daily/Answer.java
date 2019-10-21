package com.example.lavilog.Daily;


import java.io.Serializable;
import java.time.Year;

public class Answer implements Serializable {
    private String question;
    private String answer;
    private String imagePath;
    private String textClock;
    private String id;
    private String year;
    private String month;
    private String day;
    private String account;


    public Answer() { //先不傳值所以為空
    }

    public Answer(String question, String answer, String imagePath, String textClock, String id, String year, String month, String day, String account) {
        this.question = question;
        this.answer = answer;
        this.imagePath = imagePath;
        this.textClock = textClock;
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.account = account;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }



    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

