package com.example.epharmaapp.pojo;

import java.io.Serializable;

public class PharmacyProductDetails implements Serializable {
    private String id;
    private String image;
    private String name;
    private String company;
    private String price;
    private String expiredDate;

    public PharmacyProductDetails() {
    }

    public PharmacyProductDetails(String id, String image, String name, String company, String price, String expiredDate) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.company = company;
        this.price = price;
        this.expiredDate = expiredDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }
}
