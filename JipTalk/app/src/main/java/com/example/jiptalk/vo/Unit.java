package com.example.jiptalk.vo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Unit {

    String buildingID;
    String unitNum;

    String leaseType;
    String userID; // hire
    String contact;

    int deposit;
    int mngFee;
    int monthlyFee;
    int payDay;

    String startDate;
    String endDate;

    boolean isPaid;
    boolean isOccupied;

    public Unit() {
    }

    public Unit(String buildingID, String unitNum, String leaseType, String userID, String contact, int deposit, int mngFee, int monthlyFee, int payDay, String startDate, String endDate, boolean isPaid, boolean isOccupied) {
        this.buildingID = buildingID;
        this.unitNum = unitNum;
        this.leaseType = leaseType;
        this.userID = userID;
        this.contact = contact;
        this.deposit = deposit;
        this.mngFee = mngFee;
        this.monthlyFee = monthlyFee;
        this.payDay = payDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.isOccupied = isOccupied;
    }

    public String getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(String buildingID) {
        this.buildingID = buildingID;
    }

    public String getUnitNum() {
        return unitNum;
    }

    public void setUnitNum(String unitNum) {
        this.unitNum = unitNum;
    }

    public String getLeaseType() {
        return leaseType;
    }

    public void setLeaseType(String leaseType) {
        this.leaseType = leaseType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public int getMngFee() {
        return mngFee;
    }

    public void setMngFee(int mngFee) {
        this.mngFee = mngFee;
    }

    public int getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(int monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public int getPayDay() {
        return payDay;
    }

    public void setPayDay(int payDay) {
        this.payDay = payDay;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "buildingID='" + buildingID + '\'' +
                ", unitNum='" + unitNum + '\'' +
                ", leaseType='" + leaseType + '\'' +
                ", userID='" + userID + '\'' +
                ", contact='" + contact + '\'' +
                ", deposit=" + deposit +
                ", mngFee=" + mngFee +
                ", monthlyFee=" + monthlyFee +
                ", payDay=" + payDay +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", isPaid=" + isPaid +
                ", isOccupied=" + isOccupied +
                '}';
    }


    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("unitNum", unitNum);
        result.put("leaseType", leaseType);
        result.put("userID", userID);
        result.put("contact", contact);
        result.put("deposit", deposit);
        result.put("mngFee", mngFee);
        result.put("monthlyFee", monthlyFee);
        result.put("payDay", payDay);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("isPaid", isPaid);
        result.put("isOccupied", isOccupied);
        return result;
    }
}
