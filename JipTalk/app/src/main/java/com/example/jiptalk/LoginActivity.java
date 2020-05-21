package com.example.jiptalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth signInAuth;

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

        //클릭 리스너 설정

//        Button.OnClickListener onClickListener = new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent;
//                switch (view.getId()) {
//                    case R.id.loginBt :
//                        String id = userId.getText().toString();
//                        String password = userPwd.getText().toString();
//                        Log.d("***",id+", "+password);
//
//                        intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                    case R.id.signUpBt :
//                        Log.d("+++", "회원가입버튼 클릭리스너");
//                        intent = new Intent(getApplicationContext(), SignUpActivity.class);
//                        startActivity(intent);
//                }
//            }
//        } ;

        loginBt.setClickable(true);
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userId.getText().toString();
                String password = userPwd.getText().toString();
                Log.d("***",id+", "+password);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
//                finish();
            }
        });


        signUpBt.setClickable(true);
        signUpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("+++", "회원가입버튼 클릭리스너");
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
//                finish();
            }
        });
    }
}
