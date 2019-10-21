package com.example.lavilog.Daily;

import java.io.Serializable;

public class DailyQuestion implements Serializable {
    private String question;
    private String day;

    public DailyQuestion() { //先不傳值所以為空


    }

    public DailyQuestion(String question, String day) {
        this.question = question;
        this.day = day;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
