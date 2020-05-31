package com.example.jiptalk.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.R;

public class BuildingDetailActivity extends AppCompatActivity {

    TextView buildingName;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);

        initialization();

        load();

    }

    public void initialization(){
        buildingName = findViewById(R.id.tv_building_detail_buildingName);
        intent = getIntent();
    }

    public void load(){
        buildingName.setText(intent.getStringExtra("buildingName"));
    }

}
