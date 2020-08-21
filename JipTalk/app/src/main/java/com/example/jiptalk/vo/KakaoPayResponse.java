package com.example.jiptalk.vo;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class KakaoPayResponse {

    @SerializedName("tid")
    private String tid;

    @SerializedName("tms_result")
    private Boolean tms_result;

    @SerializedName("next_redirect_app_url")
    private String redirect_app_url;

    @SerializedName("next_redirect_mobile_url")
    private String redirect_mobile_url;

    @SerializedName("next_redirect_pc_url")
    private String redirect_pc_url;

    @SerializedName("android_app_scheme")
    private String android_app_scheme;

    @SerializedName("ios_app_scheme")
    private String ios_app_scheme;

    @SerializedName("created_at")
    private String created_at;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public Boolean getTms_result() {
        return tms_result;
    }

    public void setTms_result(Boolean tms_result) {
        this.tms_result = tms_result;
    }

    public String getRedirect_app_url() {
        return redirect_app_url;
    }

    public void setRedirect_app_url(String redirect_app_url) {
        this.redirect_app_url = redirect_app_url;
    }

    public String getRedirect_mobile_url() {
        return redirect_mobile_url;
    }

    public void setRedirect_mobile_url(String redirect_mobile_url) {
        this.redirect_mobile_url = redirect_mobile_url;
    }

    public String getRedirect_pc_url() {
        return redirect_pc_url;
    }

    public void setRedirect_pc_url(String redirect_pc_url) {
        this.redirect_pc_url = redirect_pc_url;
    }

    public String getAndroid_app_scheme() {
        return android_app_scheme;
    }

    public void setAndroid_app_scheme(String android_app_scheme) {
        this.android_app_scheme = android_app_scheme;
    }

    public String getIos_app_scheme() {
        return ios_app_scheme;
    }

    public void setIos_app_scheme(String ios_app_scheme) {
        this.ios_app_scheme = ios_app_scheme;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @NonNull
    @Override
    public String toString() {
        return "KakaoPaymentReady{" +
                "tid" + tid +
                "tms_result" + tms_result +
                "redirect_app_url" + redirect_app_url +
                "redirect_mobile_url" + redirect_mobile_url +
                "android_app_scheme" + android_app_scheme +
                "created_at" + created_at +
                "}";

    }
}
