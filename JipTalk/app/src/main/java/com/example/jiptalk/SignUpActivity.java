package com.example.jiptalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    protected ProgressDialog mProgressDialog = null;

    Context nowContext;
    EditText idTv, pwdTv;
    TextView saveBt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialization();

        saveBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                ValidClass valid = new ValidClass();

                /*******먼저 모든 문항 유효성 체크해야함!********/


                String email = idTv.getText().toString();
                String password = pwdTv.getText().toString();

                /* 유효성 검사 */
                // 나중에 유저 정보 class 만들어서 더 깔끔하게 메서드 처리하자
                if(!valid.isValidEmail(email)){
                    Log.d("===", "createAccount: email is not valid ");
                    Toast.makeText(nowContext, "이메일이 올바르지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!valid.isValidPwd(password)){
                    Log.d("===", "createAccount: password is not valid ");
                    Toast.makeText(nowContext, "비밀번호가 올바르지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                /* 유효성 검사 끝나면 아이디/비밀번호 생성 */
                createUser(email, password);

            }
        });

    }

    private void createUser(String email, String password){

        final UtilClass utilClass = new UtilClass();

        utilClass.showProgressDialog(nowContext);

        /*** Firebase Authentication ***/
        //FirebaseAuth 인스턴스 가져오기
        mAuth = FirebaseAuth.getInstance();

        //Email로 유저 생성
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("===", "createUserAuthentication:onComplete:" + task.isSuccessful());


                if (!task.isSuccessful()) {
                    Log.d("===", "createUserAuthentication:failure", task.getException());
                    Toast.makeText(nowContext, "인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    utilClass.hideProgressDialog();
                } else {
                    Log.d("===", "createUserAuthentication: succeed");
                    Toast.makeText(nowContext, "회원가입이 성공적으로 이루어졌습니다.", Toast.LENGTH_SHORT).show();
                    utilClass.hideProgressDialog();
                    Intent intent = new Intent(getApplicationContext(), SignUpCompleteActivity.class);
                    startActivity(intent);
                    finish();

                }



            }
        });

    }

    private void initialization(){
        idTv = findViewById(R.id.signUpIdTv);
        pwdTv = findViewById(R.id.signUpPwdTv);

        saveBt = findViewById(R.id.signUpSaveBt);
        nowContext = this; // this = View.getContext();  현재 실행되고 있는 View의 context를 return 하는데 보통은 현재 활성화된 activity의 context가 된다.
    }

}
