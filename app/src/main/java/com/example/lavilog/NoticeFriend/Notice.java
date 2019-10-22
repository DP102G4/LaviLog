package com.example.lavilog.NoticeFriend;

import java.io.Serializable;

public class Notice implements Serializable {
    private String imagePath;
    private String noticeMessage;
    private String noticeTime;
    private String id;
    private String noticeMessage2;

    public Notice(){

    }

    public Notice(String imagePath, String noticeMessage, String noticeTime, String id, String noticeMessage2) {
        this.imagePath = imagePath;
        this.noticeMessage = noticeMessage;
        this.noticeTime = noticeTime;
        this.id = id;
        this.noticeMessage2 = noticeMessage2;
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

    public String getNoticeMessage2() {
        return noticeMessage2;
    }

    public void setNoticeMessage2(String noticeMessage2) {
        this.noticeMessage2 = noticeMessage2;
    }
}
