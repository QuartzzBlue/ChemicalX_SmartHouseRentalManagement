package com.example.jiptalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiptalk.tenant.TMainActivity;
import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
    Context nowContext;

    EditText userId, userPwd;
    TextView findIdPwd;
    Button loginBt, signUpBt;
    CheckBox checkBox;

    SharedPreferences appData;
    private String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appData = getSharedPreferences("appData",MODE_PRIVATE);

        initialization();

        mAuth = FirebaseAuth.getInstance();

        /*** 로그인 ***/
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = userId.getText().toString().trim();
                password = userPwd.getText().toString().trim();
                Log.d("***",email+", "+password);

                if(!isValid(email,password)){
                    Log.d("===", "Login: fail ");
                    Toast.makeText(nowContext, "아이디나 비밀번호가 올바르지 않습니다.",
                            Toast.LENGTH_LONG).show();
                    userId.setText("");
                    userPwd.setText("");
                    return;
                }

                /*** 자동로그인 설정 ***/
                SharedPreferences.Editor editor = appData.edit();
                editor.putBoolean("SAVE_LOGIN_DATA",checkBox.isChecked());
                editor.putString("email",email);
                editor.putString("password",password);
                editor.apply();

                userLogin(email,password);

            }
        });

        /*** 회원가입 ***/
        signUpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("===", "Go to Signup Activity");
                Intent intent = new Intent(nowContext, SignUpActivity.class);
                startActivity(intent);

            }
        });

        /*** 아이디 & 비밀번호 찾기 ***/
        findIdPwd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("===", "Go to SettingIdPwd Activity");
                Intent intent = new Intent(nowContext, SettingIdPwdActivity.class);
                startActivity(intent);
            }
        });
    }

    public void userLogin (String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Log.w("===", "LoginAuthentication: fail", task.getException());
                            Toast.makeText(LoginActivity.this, "회원이 아닙니다.",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            Log.w("===", "LoginAuthentication: succeed", task.getException());
//                            Toast.makeText(LoginActivity.this, "로그인 성공",
//                                    Toast.LENGTH_SHORT).show();

                            /* Version 업데이트 되면서 굳이 콜백함수 안 쓰고 바로 user 정보 불러와서 해결하는듯 */
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(nowContext,MainActivity.class);
                            startActivity(intent);
                        }

                    }


                });

    }

    private void initialization(){
        nowContext = this;

        userId = findViewById(R.id.userIdTv);
        userPwd = findViewById(R.id.userPwdTv);
        findIdPwd = findViewById(R.id.loginFindIdPwdTv);
        loginBt = findViewById(R.id.loginBt);
        signUpBt = findViewById(R.id.signUpBt);
        checkBox = findViewById(R.id.checkBox);

    }

    private boolean isValid(String email, String password){
        Valid valid = new Valid();
        if(!valid.isValidEmail(email) || !valid.isValidPwd(password)){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
