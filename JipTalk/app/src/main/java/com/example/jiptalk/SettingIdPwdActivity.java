package com.example.jiptalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SettingIdPwdActivity extends AppCompatActivity {
    Context nowContext;
    Intent intent;
    Button toFindIdActivity, toFindPwdActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_id_pwd);

        initialization();

        toFindIdActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("===", "Go to FindId Activity");
                intent = new Intent(nowContext, FindIdActivity.class);
                startActivity(intent);
            }
        });

        toFindPwdActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("===", "Go to FindPwd Activity");
                intent = new Intent(nowContext, FindPwdActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initialization(){
        nowContext = this;
        toFindIdActivity = findViewById(R.id.settingFindIdBt);
        toFindPwdActivity = findViewById(R.id.settingFindPwdBt);
    }
}
