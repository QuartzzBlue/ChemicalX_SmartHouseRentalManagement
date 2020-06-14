package com.example.jiptalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class    SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    protected ProgressDialog mProgressDialog = null;
    private boolean isValidPhone, isValidPwd, isCheckedPwd = false;   // 전화번호 인증, 비밀번호 유효성 및 확인
    private String mVerificationCode; // 전화번호 인증번호

    Valid valid;

    Context nowContext;
    EditText idTv, pwdTv, phoneTv, nameTv, pwdCheckTv, phoneCheckTv;
    TextView pwdValidTv, pwdCheckValidTv;

    RadioButton checkedSexRgbt, checkedCategoryRgbt;
    Button phoneAuthBt, phoneAuthCheckBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialization();

        /*** 비밀번호 형식 유효성 체크 ***/
        pwdTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!valid.isNotBlank(s.toString()) || !valid.isValidPwd(s.toString())){
                    pwdValidTv.setText("형식에 맞지 않는 비밀번호입니다.");
                    pwdValidTv.setTextColor(ContextCompat.getColor(nowContext, R.color.danger));
                    isValidPwd = false;
                }else{
                    pwdValidTv.setText("가능한 비밀번호입니다.");
                    pwdValidTv.setTextColor(ContextCompat.getColor(nowContext, R.color.success));
                    isValidPwd = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        /*** 비밀번호 일치 확인 ***/
        pwdCheckTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tempPwd = pwdTv.getText().toString();
                if(!s.toString().equals(tempPwd)){
                    pwdCheckValidTv.setText("비밀번호가 일치하지 않습니다.");
                    pwdCheckValidTv.setTextColor(ContextCompat.getColor(nowContext, R.color.danger));
                    isCheckedPwd = false;
                }else{
                    pwdCheckValidTv.setText("비밀번호가 일치합니다.");
                    pwdCheckValidTv.setTextColor(ContextCompat.getColor(nowContext, R.color.success));
                    isCheckedPwd = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        /*** 전화번호 인증 ***/
        phoneAuthBt.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {

                String phoneNumber = phoneTv.getText().toString();

                if(!valid.isNotBlank(phoneNumber) || !valid.isValidPhone(phoneNumber)){
                    Log.d("===", "createAccount: phone is not valid ");
                    Toast.makeText(nowContext, "올바른 핸드폰 번호를 입력해 주세요.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 인증코드 전송
                sendVerificationCode(phoneNumber);
            }

        });

        /*** 전화번호 인증번호 확인 ***/
        phoneAuthCheckBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("===", "phoneAuthCheckBt : clicked");
                String tmpCode = phoneCheckTv.getText().toString().trim();
                if(tmpCode.equals(mVerificationCode)){
                    Log.d("===", "phoneAuthentication:succeed");
                    Toast.makeText(nowContext, "핸드폰 번호가 성공적으로 인증되었습니다.",
                            Toast.LENGTH_SHORT).show();
                    isValidPhone = true;
                    phoneTv.setEnabled(false);
                    return;
                }else{
                    Log.d("===", "phoneAuthentication:failed");
                    Toast.makeText(nowContext, "인증번호가 일치하지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    /* AppBar 에 세이브 버튼 추가 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_save, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*** 저장 버튼 눌렸을 때 ***/
            case R.id.action_save :

                String email = idTv.getText().toString();
                String password = pwdTv.getText().toString();
                String phone = phoneTv.getText().toString();
                String name = nameTv.getText().toString();
                String sex = checkedSexRgbt.getText().toString();
                String category = checkedCategoryRgbt.getText().toString();
                User newUser = new User(email, phone, name, null, null, null, sex, category, true, null);

                /* 유효성 검사 */
                if(!isValid(newUser)) return false;

                /* 유효성 검사 끝나면 인증 & DB 삽입*/
                createUser(newUser, password);

                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

    private void createUser(final User newUser, String pwd){

        final Util util = new Util();

        util.showProgressDialog(nowContext);

        /*** Firebase Authentication ***/
        //FirebaseAuth 인스턴스 가져오기
        mAuth = FirebaseAuth.getInstance();
        //데이터베이스 레퍼런스 설정
        userRef = FirebaseDatabase.getInstance().getReference();

        //[Authentification] Email로 유저 생성
        mAuth.createUserWithEmailAndPassword(newUser.getEmail(), pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("===", "createUserAuthentication:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) { // 인증 실패
                    Log.d("===", "createUserAuthentication:failure", task.getException());
                    Toast.makeText(nowContext, "인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    util.hideProgressDialog();
                } else { // 인증 성공
                    Log.d("===", "createUserAuthentication: succeed");

                    /*** DB에 USER 정보 등록 ***/
                    FirebaseUser user = task.getResult().getUser();

                    String uid = user.getUid();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("user/"+ uid, newUser.toMap());
                    // 업데이트
                    userRef.updateChildren(childUpdates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("===", "insertUserToDatabase: succeed");
                                    Toast.makeText(nowContext, "회원가입이 성공적으로 이루어졌습니다.", Toast.LENGTH_SHORT).show();
                                    util.hideProgressDialog();
                                    // 액티비티 이동
                                    Intent intent = new Intent(getApplicationContext(), SignUpCompleteActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(nowContext, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            Log.w("===", "insertUserToDatabase : Failed");
                            util.hideProgressDialog();
                            return;
                        }
                    });

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
                mVerificationCode = code;
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
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("===", "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
//            mVerificationCode = verificationId;
//            mResendToken = token;

            phoneCheckTv.requestFocus();
            // ...
        }
    };
    private void initialization(){
        idTv = findViewById(R.id.signUpIdTv);
        pwdTv = findViewById(R.id.signUpPwdTv);
        phoneTv = findViewById(R.id.signUpPhoneTv);
        nameTv = findViewById(R.id.signUpNameTv);
        pwdCheckTv = findViewById(R.id.signUpPwdCheckTv);
        phoneCheckTv = findViewById(R.id.signUpPhoneCheckTv);

        RadioGroup sexRg = findViewById(R.id.signUpSexRg);
        RadioGroup categoryRg = findViewById(R.id.signUpCategoryRg);
        checkedSexRgbt = findViewById(sexRg.getCheckedRadioButtonId());
        checkedCategoryRgbt = findViewById(categoryRg.getCheckedRadioButtonId());

        phoneAuthBt = findViewById(R.id.signUpPhoneAuthBt);
        phoneAuthCheckBt = findViewById(R.id.signUpPhoneAuthVerificationBt);
        pwdValidTv = findViewById(R.id.signUpPwdValidTv);
        pwdCheckValidTv = findViewById(R.id.signUpPwdCheckValidTv);

        nowContext = this; // this = View.getContext();  현재 실행되고 있는 View의 context를 return 하는데 보통은 현재 활성화된 activity의 context가 된다.
        valid = new Valid();
    }

    private boolean isValid(User newUser){

        if(!valid.isNotBlank(newUser.getEmail()) || !valid.isValidEmail(newUser.getEmail())){
            Log.d("===", "createAccount: email is not valid ");
            Toast.makeText(nowContext, "이메일이 올바르지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            idTv.requestFocus();
            return false;
        }
        if (!isValidPwd){
            Log.d("===", "createAccount: password is not valid ");
            Toast.makeText(nowContext, "비밀번호가 올바르지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            pwdTv.requestFocus();
            return false;
        }

        if(!isCheckedPwd){
            Log.d("===", "createAccount: password not equal");
            Toast.makeText(nowContext, "비밀번호가 일치하지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            pwdTv.requestFocus();
            return false;
        }

        if(!isValidPhone){
            Log.d("===", "createAccount: phone Auth required");
            Toast.makeText(nowContext, "휴대폰 번호 인증을 진행해주세요.",
                    Toast.LENGTH_SHORT).show();
            phoneTv.requestFocus();
            return false;
        }

        if(!valid.isNotBlank(newUser.getName())){
            Log.d("===", "createAccount: name is not valid ");
            Toast.makeText(nowContext, "이름을 입력해 주세요.",
                    Toast.LENGTH_SHORT).show();
            nameTv.requestFocus();
            return false;
        }
//        if(!valid.isNotBlank(newUser.getPhone()) || !valid.isValidPhone(newUser.getPhone())){
//            Log.d("===", "createAccount: phone is not valid ");
//            Toast.makeText(nowContext, "핸드폰 번호가 올바르지 않습니다.",
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }


}
