package com.example.jiptalk.ui.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiptalk.R;
import com.example.jiptalk.Valid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ModUserPwdActivity extends AppCompatActivity {

    private EditText pwdTv, pwdCheckTv;
    private TextView pwdStatusTv, pwdCheckStatusTv;
    private Valid valid;
    private Context nowContext;
    private boolean isValid, isVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_user_pwd);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_complete, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /*** 완료 버튼 눌렸을 때 ***/
            case R.id.action_complete :
                Log.w("===", "ModUserPwdActivitiy : Complete button clicked");
                if(isValid == false) {
                    Toast.makeText(nowContext, "비밀번호를 형식에 맞게 입력해 주세요.", Toast.LENGTH_SHORT);
                    pwdTv.requestFocus();
                    return true;
                }else if(isVerified == false) {
                    Toast.makeText(nowContext, "입력하신 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
                    pwdCheckTv.requestFocus();
                    return true;
                }

                String newPwd = pwdTv.getText().toString().trim();
                setNewPassword(newPwd);

                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

    private void setNewPassword(String newPassword) {
        Log.d("===", "ModUserPwdActivity : setNewPassword()");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("===", "setNewPassword() : succeed");
                            Toast.makeText(nowContext, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Log.w("===", "setNewPassword() : failed ", task.getException());
                            Toast.makeText(nowContext, "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initialize() {
        nowContext = this;
        pwdTv = findViewById(R.id.tv_mod_user_pwd_modPwd);
        pwdCheckTv = findViewById(R.id.tv_mod_user_pwd_modPwdCheck);
        pwdStatusTv = findViewById(R.id.tv_mod_user_pwd_modPwdStatus);
        pwdCheckStatusTv = findViewById(R.id.tv_mod_user_pwd_modPwdCheckStatus);
        valid = new Valid();

        pwdTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tempPwd = s.toString();
                if(valid.isNotBlank(tempPwd) && valid.isValidPwd(tempPwd)) {
                    pwdStatusTv.setText("가능한 비밀번호입니다.");
                    pwdStatusTv.setTextColor(ContextCompat.getColor(nowContext, R.color.success));
                    isValid = true;
                } else {
                    pwdStatusTv.setText("형식에 맞지 않는 비밀번호입니다.");
                    pwdStatusTv.setTextColor(ContextCompat.getColor(nowContext, R.color.danger));
                    isValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        pwdCheckTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = pwdTv.getText().toString();
                String tempPwdCheck = s.toString();
                if(pwd.equals(tempPwdCheck)){
                    pwdCheckStatusTv.setText("비밀번호가 일치합니다.");
                    pwdCheckStatusTv.setTextColor(ContextCompat.getColor(nowContext, R.color.success));
                    isVerified = true;
                } else {
                    pwdCheckStatusTv.setText("비밀번호가 일치하지 않습니다.");
                    pwdCheckStatusTv.setTextColor(ContextCompat.getColor(nowContext, R.color.danger));
                    isVerified = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
