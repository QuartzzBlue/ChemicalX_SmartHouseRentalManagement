package com.example.jiptalk.tenant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.jiptalk.R;

import retrofit2.Call;
import retrofit2.http.GET;

public class TPayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_pay);
    }

}
