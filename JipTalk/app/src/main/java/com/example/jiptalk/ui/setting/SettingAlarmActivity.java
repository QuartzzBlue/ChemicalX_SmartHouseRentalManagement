package com.example.jiptalk.ui.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;


import com.example.jiptalk.R;

public class SettingAlarmActivity extends AppCompatActivity {
    Switch pushAlarm;

    Button test; //삭제해야될것

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_alarm);
        initialization();


        pushAlarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                } else {

                }
            }
        });

        // 자동로그인 정보 삭제
        test.setOnClickListener(new View.OnClickListener() { //삭제해야될것
            @Override
            public void onClick(View v) {
                SharedPreferences appData = getSharedPreferences("appData",MODE_PRIVATE);
                SharedPreferences.Editor editor = appData.edit();
                editor.clear();
                editor.commit();
            }
        });

    }

    private void initialization() {

        pushAlarm = findViewById(R.id.switch_setting_alarm_pushAlarm);
        test = findViewById(R.id.btn_setting_alarm_test); //삭제해야될것
    }
}
