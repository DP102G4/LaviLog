package com.example.lavilog.Account;

import java.io.Serializable;

public class Order implements Serializable {
    private String account;
    private String imageUrl;
    private String productId;
    private String productName;
    public Order(){}
    public Order(String account,String imageUrl,String productId,String productName){
        this.account=account;
        this.imageUrl=imageUrl;
        this.productId=productId;
        this.productName=productName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
