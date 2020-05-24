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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Ref;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    protected ProgressDialog mProgressDialog = null;

    Context nowContext;
    EditText idTv, pwdTv, phoneTv, nameTv;
    TextView saveBt;

    RadioButton checkedSexRgbt, checkedCategoryRgbt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialization();

        saveBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                /*******먼저 모든 문항 유효성 체크해야함!********/

                String email = idTv.getText().toString();
                String password = pwdTv.getText().toString();
                String phone = phoneTv.getText().toString();
                String name = nameTv.getText().toString();
                String sex = checkedSexRgbt.getText().toString();
                String category = checkedCategoryRgbt.getText().toString();
                User newUser = new User(email, password, phone, name, sex, category);

                /* 유효성 검사 */
                if(!isValid(newUser)) return;

                /* 유효성 검사 끝나면 인증 & DB 삽입*/
                createUser(newUser);

            }
        });

    }

    private void createUser(final User newUser){

        final Util util = new Util();

        util.showProgressDialog(nowContext);

        /*** Firebase Authentication ***/
        //FirebaseAuth 인스턴스 가져오기
        mAuth = FirebaseAuth.getInstance();
        //데이터베이스 레퍼런스 설정
        userRef = FirebaseDatabase.getInstance().getReference();

        //[Authentification] Email로 유저 생성
        mAuth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPwd())
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
//                    userRef.child(uid).setValue(user)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d("===", "insertUserToDatabase: succeed");
//                                    Toast.makeText(nowContext, "회원가입이 성공적으로 이루어졌습니다.", Toast.LENGTH_SHORT).show();
//                                    util.hideProgressDialog();
//
//                                    // 액티비티 이동
//                                    Intent intent = new Intent(getApplicationContext(), SignUpCompleteActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(nowContext, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
//                                    Log.w("===", "insertUserToDatabase : Failed");
//                                    util.hideProgressDialog();
//                                    return;
//                                }
//                            });

                }



            }
        });

    }

    private void initialization(){
        idTv = findViewById(R.id.signUpIdTv);
        pwdTv = findViewById(R.id.signUpPwdTv);
        phoneTv = findViewById(R.id.signUpPhoneTv);
        nameTv = findViewById(R.id.signUpNameTv);

        RadioGroup sexRg = findViewById(R.id.signUpSexRg);
        RadioGroup categoryRg = findViewById(R.id.signUpCategoryRg);
        checkedSexRgbt = findViewById(sexRg.getCheckedRadioButtonId());
        checkedCategoryRgbt = findViewById(categoryRg.getCheckedRadioButtonId());

        saveBt = findViewById(R.id.signUpSaveBt);
        nowContext = this; // this = View.getContext();  현재 실행되고 있는 View의 context를 return 하는데 보통은 현재 활성화된 activity의 context가 된다.
    }

    private boolean isValid(User newUser){
        Valid valid = new Valid();

        if(!valid.isNotBlank(newUser.getEmail()) || !valid.isValidEmail(newUser.getEmail())){
            Log.d("===", "createAccount: email is not valid ");
            Toast.makeText(nowContext, "이메일이 올바르지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!valid.isNotBlank(newUser.getPwd()) || !valid.isValidPwd(newUser.getPwd())){
            Log.d("===", "createAccount: password is not valid ");
            Toast.makeText(nowContext, "비밀번호가 올바르지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!valid.isNotBlank(newUser.getName())){
            Log.d("===", "createAccount: name is not valid ");
            Toast.makeText(nowContext, "이름을 입력해 주세요.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!valid.isNotBlank(newUser.getPhone()) || !valid.isValidPhone(newUser.getPhone())){
            Log.d("===", "createAccount: phone is not valid ");
            Toast.makeText(nowContext, "핸드폰 번호가 올바르지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
