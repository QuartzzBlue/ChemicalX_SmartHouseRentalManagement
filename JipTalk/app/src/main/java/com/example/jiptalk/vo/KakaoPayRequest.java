package com.example.jiptalk.vo;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class KakaoPayRequest {

    @SerializedName("cid")
    private String cid;

    @SerializedName("tid")
    private String tid;

    @SerializedName("partner_order_id")
    private String partner_order_id;

    @SerializedName("partner_user_id")
    private String partner_user_id;

    @SerializedName("pg_token")
    private String pg_token;


    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPartner_order_id() {
        return partner_order_id;
    }

    public void setPartner_order_id(String partner_order_id) {
        this.partner_order_id = partner_order_id;
    }

    public String getPartner_user_id() {
        return partner_user_id;
    }

    public void setPartner_user_id(String partner_user_id) {
        this.partner_user_id = partner_user_id;
    }

    public String getPg_token() {
        return pg_token;
    }

    public void setPg_token(String pg_token) {
        this.pg_token = pg_token;
    }

    @Override
    public String toString() {
        return "KakaoPayRequest{" +
                "cid='" + cid + '\'' +
                ", tid='" + tid + '\'' +
                ", partner_order_id='" + partner_order_id + '\'' +
                ", partner_user_id='" + partner_user_id + '\'' +
                ", pg_token='" + pg_token + '\'' +
                '}';
    }
}
