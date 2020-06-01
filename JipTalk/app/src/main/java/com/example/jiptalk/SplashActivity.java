package com.example.jiptalk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.jiptalk.R;

public class SplashActivity extends Activity {
    private boolean autoLoginFlag;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** 자동 로그인 체크 **/
        isAutoLogin();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(autoLoginFlag){
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }else{
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private void isAutoLogin(){
        SharedPreferences appData = getSharedPreferences("appData",MODE_PRIVATE);
        autoLoginFlag = appData.getBoolean("SAVE_LOGIN_DATA",false);
        Log.d("===","AutoLogin : " + autoLoginFlag);
        String email = appData.getString("email","");
        String password = appData.getString("password","");
        return;
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        /* 스플래시 화면에서 뒤로가기 기능 제거. */
    }

}
