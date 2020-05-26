package com.example.jiptalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FindPwdActivity extends AppCompatActivity {
    private Button sendEmail;
    private EditText emailTv;
    private TextView emailCheckTv;
    private String email;
    private Context nowContext;
    private FirebaseAuth setPwdAuth;
    private DatabaseReference findUserRef;
    Valid valid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        initialization();

        sendEmail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                email = emailTv.getText().toString();
                if(!valid.isNotBlank(email) || !valid.isValidEmail(email)){
                    Log.w("===", "FindPwdActivity: email is not valid");
                    Toast.makeText(nowContext, "이메일을 정확히 입력해 주세요.",
                            Toast.LENGTH_SHORT).show();
                    emailTv.requestFocus();
                    return;
                }


            }
        });

    }
    private void verifyEmail(final String email) {
        Query query = findUserRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("===", "FindEmailOnFirebaseDB : onDataChange");
                Log.w("===", "Value [" + dataSnapshot.getKey() + ", " + dataSnapshot.getValue() + "]");
                if(dataSnapshot.getValue() == null){
                    Log.w("===", "FindEmailOnFirebaseDB : failed (This is not verified Email)");
                    emailCheckTv.setText("가입되지 않은 아이디입니다.");
                }else{
                    Log.w("===", "FindEmailOnFirebaseDB : succeed (This is verified Email)");
                    sendPwdResetEmail(email);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("===", "FindIdOnFirebaseDB :onCancelled", databaseError.toException());
                // ...
            }

        });
    }

    private boolean sendPwdResetEmail(String email) {

    }

    private void initialization() {
        nowContext = this;
        valid = new Valid();
        sendEmail = findViewById(R.id.findPwdSendEmailBt);
        emailTv = findViewById(R.id.findPwdEmailTv);
        emailCheckTv = findViewById(R.id.findPwdEmailValidTv);
        setPwdAuth = FirebaseAuth.getInstance();
        findUserRef = FirebaseDatabase.getInstance().getReference("user");
    }
}
