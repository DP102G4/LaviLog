package com.example.lavilog;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String account;
    private String password;
    private String name;
    private String imagePath;
    private String phone;
    private String birthDay;
    private String gender;
    private String verificationId;
    private String verificationCode;
    private String status;

    public User(){}
    public User(String id,String account,String password,String name,String phone){
        this.id=id;
        this.account=account;
        this.password=password;
        this.name=name;
        this.phone=phone;
    }
    public User(String id,String account,String password,String name,String phone,String imagePath){
        this.id=id;
        this.account=account;
        this.password=password;
        this.name=name;
        this.phone=phone;
        this.imagePath=imagePath;
    }

    public User(String id,String account,String password,String name,String phone,String imagePath,String gender,String birthDay){
        this.id=id;
        this.account=account;
        this.password=password;
        this.name=name;
        this.phone=phone;
        this.imagePath=imagePath;
        this.birthDay=birthDay;
        this.gender=gender;
    }

    public User(String id,String account,String password,String name,String phone,String imagePath,String gender,String birthDay,String verificationId,String verificationCode,String status){
        this.id=id;
        this.account=account;
        this.password=password;
        this.name=name;
        this.phone=phone;
        this.imagePath=imagePath;
        this.birthDay=birthDay;
        this.gender=gender;
        this.verificationId=verificationId;
        this.verificationCode=verificationCode;
        this.status=status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
