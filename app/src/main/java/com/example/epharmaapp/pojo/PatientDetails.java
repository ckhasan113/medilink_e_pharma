package com.example.epharmaapp.pojo;

import java.io.Serializable;

public class PatientDetails implements Serializable {

    private String patient_id;

    private String imageURI;
    private String firstName;
    private String lastName;

    private String flat;
    private String house;
    private String road;
    private String area;
    private String block;
    private String city;
    private String country_id;
    private String zip;

    private String phone;
    private String email;
    private String password;

    private String userType;

    public PatientDetails() {
    }

    public PatientDetails(String imageURI, String firstName, String lastName, String flat, String house, String road, String area, String block, String city, String country_id, String zip, String phone, String email, String password, String userType) {
        this.imageURI = imageURI;
        this.firstName = firstName;
        this.lastName = lastName;
        this.flat = flat;
        this.house = house;
        this.road = road;
        this.area = area;
        this.block = block;
        this.city = city;
        this.country_id = country_id;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public PatientDetails(String patient_id, String imageURI, String firstName, String lastName, String flat, String house, String road, String area, String block, String city, String country_id, String zip, String phone, String email, String password, String userType) {
        this.patient_id = patient_id;
        this.imageURI = imageURI;
        this.firstName = firstName;
        this.lastName = lastName;
        this.flat = flat;
        this.house = house;
        this.road = road;
        this.area = area;
        this.block = block;
        this.city = city;
        this.country_id = country_id;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
