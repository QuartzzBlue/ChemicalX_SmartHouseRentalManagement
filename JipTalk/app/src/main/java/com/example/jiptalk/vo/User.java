package com.example.jiptalk.vo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User implements Serializable {
    String UID;
    String email;
    String phone;
    String name;
    String sex;
    String category;    // 임대인 or 임차인
    Boolean isAlarmOn;
    String token;
    String depositor;  // 예금자명
    String bank;    // 은행
    String accountNum; //  계좌번호

    /* 세입자 */
    String buildingID;
    String unitID;
    String landlordID;



    public User() {}

    public User(String UID) {
        this.UID = UID;
    }

    public User(String email, String phone, String name, String depositor, String bank, String accountNum, String sex, String category, Boolean isAlarmOn, String token) {
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.depositor = depositor;
        this.bank = bank;
        this.accountNum = accountNum;
        this.sex = sex;
        this.category = category;
        this.isAlarmOn = isAlarmOn;
        this.token = token;
    }

    public User(String UID, String email, String phone, String name, String depositor, String bank, String accountNum, String sex, String category, Boolean isAlarmOn, String token) {
        this.UID = UID;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.depositor = depositor;
        this.bank = bank;
        this.accountNum = accountNum;
        this.sex = sex;
        this.category = category;
        this.isAlarmOn = isAlarmOn;
        this.token = token;
    }

    public User(String UID, String email, String phone, String name, String depositor, String bank, String accountNum, String sex, String category, Boolean isAlarmOn, String token,String BuildingID, String unitID) {
        this.UID = UID;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.depositor = depositor;
        this.bank = bank;
        this.accountNum = accountNum;
        this.sex = sex;
        this.category = category;
        this.isAlarmOn = isAlarmOn;
        this.token = token;
        this.buildingID = buildingID;
        this.unitID = unitID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepositor() { return depositor; }

    public void setDepositor(String depositor) { this.depositor = depositor; }

    public String getBank() { return bank; }

    public void setBank(String bank) { this.bank = bank; }

    public String getAccountNum() { return accountNum; }

    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getAlarmOn() {
        return isAlarmOn;
    }

    public void setAlarmOn(Boolean alarmOn) {
        isAlarmOn = alarmOn;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getLandlordID() {
        return landlordID;
    }

    public void setLandlordID(String landlordID) {
        this.landlordID = landlordID;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("phone", phone);
        result.put("name", name);
        result.put("depositor", depositor);
        result.put("bank", bank);
        result.put("accountNum", accountNum);
        result.put("sex", sex);
        result.put("category", category);
        result.put("isAlarmOn", isAlarmOn);
        result.put("buildingID",buildingID);
        result.put("unitID",unitID);
        result.put("landlordID",landlordID);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "UID='" + UID + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", category='" + category + '\'' +
                ", isAlarmOn=" + isAlarmOn +
                ", token='" + token + '\'' +
                ", depositor='" + depositor + '\'' +
                ", bank='" + bank + '\'' +
                ", accountNum='" + accountNum + '\'' +
                ", buildingID='" + buildingID + '\'' +
                ", unitID='" + unitID + '\'' +
                ", landlordID='" + landlordID + '\'' +
                '}';
    }
}
