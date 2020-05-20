package com.example.jiptalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    EditText userId, userPwd;
    TextView findIdPwd;
    Button loginBt, signUpBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userId = findViewById(R.id.userIdTv);
        userPwd = findViewById(R.id.userPwdTv);
        findIdPwd = findViewById(R.id.findIdPwdTv);
        loginBt = findViewById(R.id.loginBt);
        signUpBt = findViewById(R.id.signUpBt);

        //클릭 가능 여부 설정 및 클릭 리스너 설정
        loginBt.setClickable(true);
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userId.getText().toString();
                String password = userPwd.getText().toString();
                Log.d("***",id+", "+password);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
