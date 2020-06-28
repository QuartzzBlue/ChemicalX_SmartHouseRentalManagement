package com.example.jiptalk.vo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Credit {
    String unitID;
    String date;
    String payerName;
    String credit;
    String status;

    public Credit() { }

    public Credit(String unitID, String date, String payerName, String credit, String status) {
        this.unitID = unitID;
        this.date = date;
        this.payerName = payerName;
        this.credit = credit;
        this.status = status;
    }

    public Credit(String unitID, String date, String credit, String status) {
        this.unitID = unitID;
        this.date = date;
        this.credit = credit;
        this.status = status;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
        result.put("date", date);
        result.put("payerName", payerName);
        result.put("credit", credit);
        result.put("status", status);

        return result;
    }

    @Override
    public String toString() {
        return "Credit{" +
                "unitID='" + unitID + '\'' +
                ", date='" + date + '\'' +
                ", payerName='" + payerName + '\'' +
                ", credit='" + credit + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
