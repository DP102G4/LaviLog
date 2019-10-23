package com.example.lavilog.SearchUserId;

import java.io.Serializable;

public class Friend implements Serializable {
    private String name;
    private String imagePath;
    private String id;
    private String account;
    private String friend_account;

    public Friend() {

    }

    public Friend(String name, String imagePath, String id, String account, String friend_account) {
        this.name = name;
        this.imagePath = imagePath;
        this.id = id;
        this.account = account;
        this.friend_account = friend_account;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFriend_account() {
        return friend_account;
    }

    public void setFriend_account(String friend_account) {
        this.friend_account = friend_account;
    }
}
