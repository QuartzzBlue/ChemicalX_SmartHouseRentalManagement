package com.example.jiptalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
    Context nowContext;

    EditText userId, userPwd;
    TextView findIdPwd;
    Button loginBt, signUpBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialization();

        /*** Firebase Authentication ***/
        //FirebaseAuth 인스턴스 가져오기
        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) { //콜백함수(로그인되거나 로그아웃될때)
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d("===", "onAuthStateChanged:signed_in:" + user.getUid());
//
//
////                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    Intent intent = new Intent(getApplicationContext(), SampleLoginActivity.class);
//                    intent.putExtra("userID", user.getEmail());
//                    intent.putExtra("userUID", user.getUid());
//                    startActivity(intent);
////                  finish();
//                    /****/
//
//                } else {
//                    // User is signed out
//                    Log.d("===", "onAuthStateChanged:signed_out");
//                }
//            }
//        }; //[End AuthStateListener]

        //클릭 리스너 설정
        loginBt.setClickable(true);
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = userId.getText().toString();
                String password = userPwd.getText().toString();
                Log.d("***",email+", "+password);

                //아이디 패스워드 모두 빈칸이면 홈으로 이동. by Yeojin
                if(email.equals("a") && password.equals("a")){
                    Log.d("***","Go to Home...");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else{
                    if(!isValid(email,password)){
                        Log.d("===", "Login: fail ");
                        Toast.makeText(nowContext, "아이디나 비밀번호가 올바르지 않습니다.",
                                Toast.LENGTH_LONG).show();
                        userId.setText("");
                        userPwd.setText("");
                        return;
                    }
                    userLogin(email,password);
                }
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

    private void userLogin (String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d("===", "LoginAuthentication: onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("===", "LoginAuthentication: fail", task.getException());
                            Toast.makeText(LoginActivity.this, "회원이 아닙니다.",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            Log.w("===", "LoginAuthentication: succeed", task.getException());
                            Toast.makeText(LoginActivity.this, "로그인 성공",
                                    Toast.LENGTH_SHORT).show();

                            /* Version 업데이트 되면서 굳이 콜백함수 안 쓰고 바로 user 정보 불러와서 해결하는듯 */
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(getApplicationContext(), SampleLoginActivity.class);
                            intent.putExtra("userID", user.getEmail());
                            intent.putExtra("userUID", user.getUid());
                            startActivity(intent);

                        }

                    }


                });

    }

    private void initialization(){
        nowContext = this;

        userId = findViewById(R.id.userIdTv);
        userPwd = findViewById(R.id.userPwdTv);
        findIdPwd = findViewById(R.id.findIdPwdTv);
        loginBt = findViewById(R.id.loginBt);
        signUpBt = findViewById(R.id.signUpBt);



    }

    private boolean isValid(String email, String password){
        ValidClass valid = new ValidClass();
        if(!valid.isValidEmail(email) || !valid.isValidPwd(password)){
            return false;
        }else{
            return true;
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*** user 정보 있냐 없냐에 따라 자동 로그인 ? ***/
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut();   //로그아웃
        }
    }

}
