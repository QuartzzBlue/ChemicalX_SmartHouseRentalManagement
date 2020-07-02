package com.example.jiptalk.vo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Credit {
    String unitID;
    String creditID;
    String billingDate; // 청구날짜
    String depositDate; // 입금날짜
    String payerName;
    String credit;
    String status;

    public Credit() { }

    public Credit(String unitID, String payerName, String credit) {
        this.unitID = unitID;
        this.payerName = payerName;
        this.credit = credit;
    }

    public Credit(String unitID, String credit) {
        this.unitID = unitID;
        this.credit = credit;
    }


    public Credit(String unitID, String creditID, String billingDate, String payerName, String credit, String status) {
        this.unitID = unitID;
        this.creditID = creditID;
        this.billingDate = billingDate;
        this.payerName = payerName;
        this.credit = credit;
        this.status = status;
    }

    public Credit(String unitID, String creditID, String billingDate, String depositDate, String payerName, String credit, String status) {
        this.unitID = unitID;
        this.creditID = creditID;
        this.billingDate = billingDate;
        this.depositDate = depositDate;
        this.payerName = payerName;
        this.credit = credit;
        this.status = status;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public String getCreditID() {
        return creditID;
    }

    public void setCreditID(String creditID) {
        this.creditID = creditID;
    }

    public String getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(String billingDate) {
        this.billingDate = billingDate;
    }

    public String getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(String depositDate) {
        this.depositDate = depositDate;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("unitID", unitID);
        result.put("billingDate", billingDate);
        result.put("depositDate", depositDate);
        result.put("payerName", payerName);
        result.put("credit", credit);
        result.put("status", status);

        return result;
    }

    @Override
    public String toString() {
        return "Credit{" +
                "unitID='" + unitID + '\'' +
                ", date='" + billingDate + '\'' +
                ", payerName='" + payerName + '\'' +
                ", billingDate='" + billingDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
