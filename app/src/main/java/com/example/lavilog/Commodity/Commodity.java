package com.example.lavilog.Commodity;

public class Commodity {
    private String productId;
    private String imageUrl;
    private String productName;
    private int productPrice;
    private boolean onsale;
    private String productInfo;



    public Commodity(){

    }

    public Commodity(String productId, String imageUrl, String productName, int productPrice, boolean onsale, String productInfo) {
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.productName = productName;
        this.productPrice = productPrice;
        this.onsale = onsale;
        this.productInfo = productInfo;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public boolean isOnsale() {
        return onsale;
    }

    public void setOnsale(boolean onsale) {
        this.onsale = onsale;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }
}
