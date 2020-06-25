package com.example.jiptalk.vo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Tenant implements Serializable {


    String name;
    String phone;
    String buildingID;
    String unitID;

    public Tenant() {}

    public Tenant(String name, String phone, String buildingID, String unitID) {
        this.name = name;
        this.phone = phone;
        this.buildingID = buildingID;
        this.unitID = unitID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(String buildingID) {
        this.buildingID = buildingID;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("phone", phone);
        result.put("buildingID",buildingID);
        result.put("unitID",unitID);
        return result;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", buildingID='" + buildingID + '\'' +
                ", unitID='" + unitID + '\'' +
                '}';
    }
}
