package com.example.jiptalk;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    String email;
    String pwd;
    String phone;
    String name;
    String sex;
    String category;    // 임대인 or 임차인

    public User() {
    }

    public User(String email, String pwd) {
        this.email = email;
        this.pwd = pwd;
    }

    public User(String email, String pwd, String phone, String name, String sex, String category) {
        this.email = email;
        this.pwd = pwd;
        this.phone = phone;
        this.name = name;
        this.sex = sex;
        this.category = category;
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("pwd", pwd);
        result.put("phone", phone);
        result.put("name", name);
        result.put("sex", sex);
        result.put("category", category);

        return result;
    }
}
