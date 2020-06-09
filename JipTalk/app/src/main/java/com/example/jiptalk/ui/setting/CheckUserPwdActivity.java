package com.example.jiptalk.ui.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiptalk.Constant;
import com.example.jiptalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CheckUserPwdActivity extends AppCompatActivity {

    private boolean isVerified = false;
    private EditText pwdTv, pwdCheckTv;
    private TextView pwdStatusTv;
    private Context nowContext;
    private String userPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user_pwd);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_next, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /*** 다음 버튼 눌렸을 때 ***/
            case R.id.action_next :
                Log.w("===", "CheckUserPwdActivitiy : next button clicked");

                // 비밀번호를 입력하지 않았을 경우
                if(userPwd == null || userPwd.equals("")){
                    Toast.makeText(nowContext, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT);
                    pwdTv.requestFocus();
                    return true;
                }
                // 검증되지 않은 비밀번호 입력 시 (비밀번호 != 비밀번호 확인)
                if(isVerified == false){
                    Toast.makeText(nowContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
                    return true;
                }

                checkPassword();

                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

    private void initialize() {
        nowContext = this;
        pwdTv = findViewById(R.id.tv_check_user_pwd_currentPwd);
        pwdCheckTv = findViewById(R.id.tv_check_user_pwd_currentPwdCheck);
        pwdStatusTv = findViewById(R.id.tv_check_user_pwd_currentPwdCheckStatus);

        pwdCheckTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userPwd = pwdTv.getText().toString().trim();
                if(s.toString().equals(userPwd)){
                    pwdStatusTv.setText("비밀번호가 일치합니다.");
                    pwdStatusTv.setTextColor(ContextCompat.getColor(nowContext, R.color.success));
                    isVerified = true;
                }else{
                    pwdStatusTv.setText("비밀번호가 일치하지 않습니다.");
                    pwdStatusTv.setTextColor(ContextCompat.getColor(nowContext, R.color.danger));
                    isVerified = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkPassword() {
        Log.w("===", "CheckUserPwdActivitiy : checkPassword()");

        FirebaseAuth.getInstance().signInWithEmailAndPassword(Constant.userID, userPwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Log.w("===", "checkPassword() : fail", task.getException());
                            Toast.makeText(nowContext, "비밀번호가 정확하지 않습니다. 다시 입력해 주세요.",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            Log.w("===", "checkPassword() : succeed");
                            Intent intent = new Intent(nowContext, ModUserPwdActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    }

                });
    }
}
