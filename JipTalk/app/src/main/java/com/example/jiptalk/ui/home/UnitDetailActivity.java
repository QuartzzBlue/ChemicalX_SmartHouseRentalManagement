package com.example.jiptalk.ui.home;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.R;

public class UnitDetailActivity extends AppCompatActivity {

    TextView nameTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_detail);

        initialization();

    }
    private void initialization(){
        nameTv = findViewById(R.id.tv_unit_detail_name);
    }
}
