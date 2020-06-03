package com.example.jiptalk.vo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    String UID;
    String email;
    String pwd;
    String phone;
    String name;
    String sex;
    String category;    // 임대인 or 임차인
    Boolean isAlarmOn;
    String token;


    public User() {
    }

    public User(String UID) {
        this.UID = UID;
    }

    public User(String email, String pwd) {
        this.email = email;
        this.pwd = pwd;
    }


    public User(String email, String pwd, String phone, String name, String sex, String category, Boolean isAlarmOn) {
        this.email = email;
        this.pwd = pwd;
        this.phone = phone;
        this.name = name;
        this.sex = sex;
        this.category = category;
        this.isAlarmOn = isAlarmOn;

    }

    public User(String UID, String email, String pwd, String phone, String name, String sex, String category, Boolean isAlarmOn, String token) {
        this.UID = UID;
        this.email = email;
        this.pwd = pwd;
        this.phone = phone;
        this.name = name;
        this.sex = sex;
        this.category = category;
        this.isAlarmOn = isAlarmOn;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("pwd", pwd);
        result.put("phone", phone);
        result.put("name", name);
        result.put("sex", sex);
        result.put("category", category);
        result.put("isAlarmOn", isAlarmOn);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "UID='" + UID + '\'' +
                ", email='" + email + '\'' +
                ", pwd='" + pwd + '\'' +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", category='" + category + '\'' +
                ", isAlarmOn=" + isAlarmOn +
                ", token='" + token + '\'' +
                '}';
    }
}
