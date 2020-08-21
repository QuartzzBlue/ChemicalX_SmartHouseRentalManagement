package com.example.jiptalk.tenant.retrofit;

import com.example.jiptalk.vo.KakaoPayResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface RetrofitService {

    //https://kapi.kakao.com/v1/payment/ready

    // @POST( EndPoint-자원위치(URI) )
    @FormUrlEncoded
    @POST("v1/payment/ready")
    Call<KakaoPayResponse> postRedirect(@HeaderMap Map<String,Object> headers, @FieldMap HashMap<String, Object> param);

}
