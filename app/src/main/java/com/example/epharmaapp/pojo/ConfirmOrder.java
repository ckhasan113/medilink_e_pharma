package com.example.epharmaapp.pojo;

import java.io.Serializable;
import java.util.List;

public class ConfirmOrder implements Serializable {

    private String orderId;
    private List<OrderChart> chartList;
    private PatientDetails patientDetails;
    private EPharmaDetails ePharmaDetails;
    private String totalPrice;
    private String totalPlusVat;
    private String date;
    private String status;

    public ConfirmOrder() {
    }

    public ConfirmOrder(String orderId, List<OrderChart> chartList, PatientDetails patientDetails, EPharmaDetails ePharmaDetails, String totalPrice, String totalPlusVat, String date, String status) {
        this.orderId = orderId;
        this.chartList = chartList;
        this.patientDetails = patientDetails;
        this.ePharmaDetails = ePharmaDetails;
        this.totalPrice = totalPrice;
        this.totalPlusVat = totalPlusVat;
        this.date = date;
        this.status = status;
    }

    public String getTotalPlusVat() {
        return totalPlusVat;
    }

    public void setTotalPlusVat(String totalPlusVat) {
        this.totalPlusVat = totalPlusVat;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<OrderChart> getChartList() {
        return chartList;
    }

    public void setChartList(List<OrderChart> chartList) {
        this.chartList = chartList;
    }

    public PatientDetails getPatientDetails() {
        return patientDetails;
    }

    public void setPatientDetails(PatientDetails patientDetails) {
        this.patientDetails = patientDetails;
    }

    public EPharmaDetails getePharmaDetails() {
        return ePharmaDetails;
    }

    public void setePharmaDetails(EPharmaDetails ePharmaDetails) {
        this.ePharmaDetails = ePharmaDetails;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
