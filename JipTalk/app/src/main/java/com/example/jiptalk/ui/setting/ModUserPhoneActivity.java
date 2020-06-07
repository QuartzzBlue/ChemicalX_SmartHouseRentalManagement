package com.example.jiptalk.ui.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jiptalk.R;
import com.example.jiptalk.Valid;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ModUserPhoneActivity extends AppCompatActivity {

    private Context nowContext;
    private EditText newPhoneTv, checkTv;
    private Button sendCodeBt, checkBt;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_user_phone);

        initialize();

    }

    private void initialize() {
        nowContext = this;
        newPhoneTv = findViewById(R.id.tv_mod_user_phone_newPhone);
        checkTv = findViewById(R.id.tv_mod_user_phone_check);
        sendCodeBt = findViewById(R.id.bt_mod_user_phone_send);
        checkBt = findViewById(R.id.bt_mod_user_phone_check);

        /*** 인증 코드 전송 ***/
        sendCodeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("===", "ModUserPhoneActivity : sendCodeBt clicked");
                String newPhone = newPhoneTv.getText().toString().trim();
                Valid valid = new Valid();

                if (!valid.isNotBlank(newPhone) || !valid.isValidPhone(newPhone)){
                    Toast.makeText(nowContext, "전화번호를 정확히 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    newPhoneTv.setText("");
                    newPhoneTv.requestFocus();
                    return;
                }

                // 코드 전송
                sendVerificationCode(newPhone);

            }
        });

        /*** 인증 코드 확인 ***/
        checkBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("===", "ModUserPhoneActivity : checkBt clicked");
                String rCode = checkTv.getText().toString().trim();

                if(rCode.equals(verificationCode)){
                    Toast.makeText(nowContext, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.w("===", "ModUserPhoneActivity : code verified");
                }else{
                    Toast.makeText(nowContext, "인증에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Log.w("===", "ModUserPhoneActivity : code verification failed");
                }
            }
        });
    }

    private void sendVerificationCode(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+82" + phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        PhoneAuthProvider.ForceResendingToken mResendToken;

        // 인증이 정상적으로 완료되었을 때
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d("===", "onVerificationCompleted:" + credential);

            String code = credential.getSmsCode();

            if (code != null) {
                verificationCode = code;
                Log.d("===", "onVerificationCompleted: code = " + code);
            }

        }

        // 인증 실패 시
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w("===", "onVerificationFailed", e);
            //에러 2가지 : FirebaseAuthInvalidCredentialsException, FirebaseTooManyRequestsException

            Toast.makeText(nowContext, "인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }

        // 인증번호가 정상적으로 보내졌을 때
        @Override
        public void onCodeSent(@NonNull String verificationId, //전송된 인증번호
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d("===", "onCodeSent:" + verificationId);

            checkTv.requestFocus();
        }
    };

}
