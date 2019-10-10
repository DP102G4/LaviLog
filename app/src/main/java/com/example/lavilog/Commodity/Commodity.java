package com.example.lavilog.Commodity;

public class Commodity {
    private int imageID;
    private String name;
    private String owner;
    private String detail;
    private int price;

    public Commodity(int imageID, String name, String owner, String detail, int price) {
        this.imageID = imageID;
        this.name = name;
        this.owner = owner;
        this.detail = detail;
        this.price = price;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
