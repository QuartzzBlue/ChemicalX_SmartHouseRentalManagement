package com.example.jiptalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    protected ProgressDialog mProgressDialog = null;

    Context nowContext;
    EditText idTv, pwdTv;
    TextView saveBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        idTv = findViewById(R.id.signUpIdTv);
        pwdTv = findViewById(R.id.signUpPwdTv);

        saveBt = findViewById(R.id.signUpSaveBt);
        nowContext = this; // this = View.getContext();  현재 실행되고 있는 View의 context를 return 하는데 보통은 현재 활성화된 activity의 context가 된다.

        saveBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*******먼저 모든 문항 유효성 체크해야함!********/


                String email = idTv.getText().toString();
                String password = pwdTv.getText().toString();

                /* 유효성 검사 */
                if(!isValidEmail(email)){
                    Log.d("===", "createAccount: email is not valid ");
                    Toast.makeText(nowContext, "이메일이 올바르지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isValidPwd(password)){
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

        showProgressDialog();

        /*** Firebase Authentication ***/
        //FirebaseAuth 인스턴스 가져오기
        mAuth = FirebaseAuth.getInstance();

        //Email로 유저 생성
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("===", "createUserWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w("===", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(nowContext, "인증 실패", Toast.LENGTH_SHORT).show();
                }

                hideProgressDialog();
            }
        });

    }

    private boolean isValidEmail(String email){
        if (email == null || TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isValidPwd(String pwd){
        //비밀번호 6자리 이상 20자리 이하, 한글미포함
        Pattern p = Pattern.compile("(^.*(?=.{6,20})(?=.*[0-9])(?=.*[a-zA-Z]).*$)");

        Matcher m = p.matcher(pwd);
        if (m.find() && !pwd.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")){
            return true;
        }else{
            return false;
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            if (Build.VERSION_CODES.KITKAT < Build.VERSION.SDK_INT) {

//              R.style.ProgressDialogStyle은 커스텀으로 정의한 스타일임
                mProgressDialog = new ProgressDialog(nowContext, R.style.ProgressDialogStyle);

            } else {
                mProgressDialog = new ProgressDialog(nowContext);
            }
            mProgressDialog.setMessage("잠시만 기다려주세요...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }


    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
