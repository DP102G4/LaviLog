package com.example.lavilog.SearchUserId;

import java.io.Serializable;

public class Notice implements Serializable {
    private String imagePath;
    private String noticeMessage;
    private String noticeTime;
    private String id;

    public Notice(){

    }

    public Notice(String imagePath, String noticeMessage, String noticeTime, String id) {
        this.imagePath = imagePath;
        this.noticeMessage = noticeMessage;
        this.noticeTime = noticeTime;
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getNoticeMessage() {
        return noticeMessage;
    }

    public void setNoticeMessage(String noticeMessage) {
        this.noticeMessage = noticeMessage;
    }

    public String getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(String noticeTime) {
        this.noticeTime = noticeTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
