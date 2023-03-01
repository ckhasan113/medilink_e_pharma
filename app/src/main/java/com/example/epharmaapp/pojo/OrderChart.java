package com.example.epharmaapp.pojo;

import java.io.Serializable;
import java.util.List;

public class OrderChart implements Serializable {

    private String Quentity;
    private PharmacyProductDetails productDetails;
    private String price;
    private String Type;

    public OrderChart() {
    }

    public OrderChart(String quentity, PharmacyProductDetails productDetails, String price, String type) {
        Quentity = quentity;
        this.productDetails = productDetails;
        this.price = price;
        Type = type;
    }

    public String getQuentity() {
        return Quentity;
    }

    public void setQuentity(String quentity) {
        Quentity = quentity;
    }

    public PharmacyProductDetails getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(PharmacyProductDetails productDetails) {
        this.productDetails = productDetails;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
