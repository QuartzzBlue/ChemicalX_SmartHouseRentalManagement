package com.example.jiptalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.TimerTask;

public class SignUpCompleteActivity extends AppCompatActivity {
    TextView timeoutTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_complete);
        timeoutTv = findViewById(R.id.signUpCompleteTimeOutTV);

        Log.d("===", "SignUpComplete: onCreate");

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, 3000);



    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("===", "SignUpComplete: onPostResume");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);


//        Handler timer = new Handler();//Handler 생성
//
//
//        timer.postDelayed(new Runnable(){ //1초후 쓰레드를 생성하는 postDelayed 메소드
//
//            public void run(){
//                Log.d("===", "SignUpComplete: Time "+ 3);
//                timeoutTv.setText("3초 뒤에 로그인 화면으로 이동합니다");
//            }
//
//        }, 1000);
//
//
//        timer.postDelayed(new Runnable(){ //1초후 쓰레드를 생성하는 postDelayed 메소드
//
//            public void run(){
//                Log.d("===", "SignUpComplete: Time "+ 2);
//                timeoutTv.setText("2초 뒤에 로그인 화면으로 이동합니다");
//            }
//
//        }, 1000);
//
//
//        timer.postDelayed(new Runnable(){ //1초후 쓰레드를 생성하는 postDelayed 메소드
//
//            public void run(){
//                Log.d("===", "SignUpComplete: Time "+ 1);
//                timeoutTv.setText("1초 뒤에 로그인 화면으로 이동합니다");
//            }
//
//        }, 1000);
//
//        Intent intent = new Intent(getApplicationContext(), SignUpCompleteActivity.class);
//        startActivity(intent);
//        finish();
    }
}
