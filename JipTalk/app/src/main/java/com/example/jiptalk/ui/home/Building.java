package com.example.jiptalk.ui.home;

import java.util.ArrayList;
import java.util.Date;

public class Building{
    String name;
    ArrayList<PaymentStatus> paymentStatuses;

    public Building(){}

    public Building(String name){
        this.name = name;
        paymentStatuses = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PaymentStatus> getPaymentStatuses() {
        return paymentStatuses;
    }

    public void setPaymentStatuses(ArrayList<PaymentStatus> paymentStatuses) {
        this.paymentStatuses = paymentStatuses;
    }
}

class PaymentStatus{

    String tenantName; //세입자 이름
    String rentType; //전세,월세
    int payDay; //매달 납부일
    int rentFee; //전세계약자는 0
    int mngFee; //관리비
    String status; //미납,완납,연체
    int delay; //연체일
    Date payDate; //납부한 날짜

    public PaymentStatus(String tenantName, String rentType, int payDay, int rentFee, int mngFee, String status, int delay, Date payDate) {
        this.tenantName = tenantName;
        this.rentType = rentType;
        this.payDay = payDay;
        this.rentFee = rentFee;
        this.mngFee = mngFee;
        this.status = status;
        this.delay = delay;
        this.payDate = payDate;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getRentType() {
        return rentType;
    }

    public void setRentType(String rentType) {
        this.rentType = rentType;
    }

    public int getPayDay() {
        return payDay;
    }

    public void setPayDay(int payDay) {
        this.payDay = payDay;
    }

    public int getRentFee() {
        return rentFee;
    }

    public void setRentFee(int rentFee) {
        this.rentFee = rentFee;
    }

    public int getMngFee() {
        return mngFee;
    }

    public void setMngFee(int mngFee) {
        this.mngFee = mngFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }
}
