package com.example.epharmaapp.pojo;

import java.io.Serializable;

public class EPharmaDetails implements Serializable {
    private String id;
    private String image;
    private String farmName;
    private String tinNumber;
    private String area;
    private String city;
    private String establishYear;
    private String owner;
    private String phone;
    private String email;
    private String password;

    public EPharmaDetails() {
    }

    public EPharmaDetails(String id, String image, String farmName, String tinNumber, String area, String city, String establishYear, String owner, String phone, String email, String password) {
        this.id = id;
        this.image = image;
        this.farmName = farmName;
        this.tinNumber = tinNumber;
        this.area = area;
        this.city = city;
        this.establishYear = establishYear;
        this.owner = owner;
        this.phone = phone;
        this.email = email;
        this.password = password;
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

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEstablishYear() {
        return establishYear;
    }

    public void setEstablishYear(String establishYear) {
        this.establishYear = establishYear;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
