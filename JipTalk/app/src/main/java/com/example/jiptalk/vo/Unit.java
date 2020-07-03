package com.example.jiptalk.vo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Unit implements Serializable {

    String unitID;
    String unitNum;

    String leaseType;
    String tenantID; // hire
    String tenantName;
    String tenantPhone;
    String payerName;

    String deposit;
    String mngFee;
    String monthlyFee;
    String payDay;

    String startDate;
    String endDate;

    String isPaid;
    String isOccupied;

    public Unit() {
    }

    public Unit(String unitNum, String leaseType, String tenantName, String tenantPhone, String payerName, String deposit, String mngFee, String monthlyFee, String payDay, String startDate, String endDate) {
        this.unitNum = unitNum;
        this.leaseType = leaseType;
        this.tenantName = tenantName;
        this.tenantPhone = tenantPhone;
        this.payerName = payerName;
        this.deposit = deposit;
        this.mngFee = mngFee;
        this.monthlyFee = monthlyFee;
        this.payDay = payDay;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Unit(String unitNum, String leaseType, String tenantName, String tenantPhone, String payerName, String deposit, String mngFee, String monthlyFee, String payDay, String startDate, String endDate, String isPaid, String isOccupied) {
        this.unitNum = unitNum;
        this.leaseType = leaseType;
        this.tenantName = tenantName;
        this.tenantPhone = tenantPhone;
        this.payerName = payerName;
        this.deposit = deposit;
        this.mngFee = mngFee;
        this.monthlyFee = monthlyFee;
        this.payDay = payDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.isOccupied = isOccupied;
    }

    public Unit(String unitNum, String leaseType, String tenantID, String tenantName, String tenantPhone, String payerName, String deposit, String mngFee, String monthlyFee, String payDay, String startDate, String endDate, String isPaid, String isOccupied) {
        this.unitNum = unitNum;
        this.leaseType = leaseType;
        this.tenantID = tenantID;
        this.tenantName = tenantName;
        this.tenantPhone = tenantPhone;
        this.payerName = payerName;
        this.deposit = deposit;
        this.mngFee = mngFee;
        this.monthlyFee = monthlyFee;
        this.payDay = payDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.isOccupied = isOccupied;
    }

    public Unit(String buildingID, String unitNum, String leaseType, String tenantID, String tenantName, String tenantPhone, String payerName, String deposit, String mngFee, String monthlyFee, String payDay, String startDate, String endDate, String isPaid, String isOccupied) {
        this.unitID = buildingID;
        this.unitNum = unitNum;
        this.leaseType = leaseType;
        this.tenantID = tenantID;
        this.tenantName = tenantName;
        this.tenantPhone = tenantPhone;
        this.payerName = payerName;
        this.deposit = deposit;
        this.mngFee = mngFee;
        this.monthlyFee = monthlyFee;
        this.payDay = payDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.isOccupied = isOccupied;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
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

    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getMngFee() {
        return mngFee;
    }

    public void setMngFee(String mngFee) {
        this.mngFee = mngFee;
    }

    public String getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(String monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public String getPayDay() {
        return payDay;
    }

    public void setPayDay(String payDay) {
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

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }

    public String getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(String isOccupied) {
        this.isOccupied = isOccupied;
    }

    
    @Override
    public String toString() {
        return "Unit{" +
                "unitID='" + unitID + '\'' +
                ", unitNum='" + unitNum + '\'' +
                ", leaseType='" + leaseType + '\'' +
                ", tenantID='" + tenantID + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", tenantPhone='" + tenantPhone + '\'' +
                ", payerName='" + payerName + '\'' +
                ", deposit='" + deposit + '\'' +
                ", mngFee='" + mngFee + '\'' +
                ", monthlyFee='" + monthlyFee + '\'' +
                ", payDay='" + payDay + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", isPaid='" + isPaid + '\'' +
                ", isOccupied='" + isOccupied + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("unitNum", unitNum);
        result.put("leaseType", leaseType);
        result.put("tenantID", tenantID);
        result.put("tenantName", tenantName);
        result.put("tenantPhone", tenantPhone);
        result.put("payerName",payerName);
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
