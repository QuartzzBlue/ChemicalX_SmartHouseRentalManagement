package com.example.jiptalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FindIdActivity extends AppCompatActivity {
    private EditText nameTv, phoneTv;
    private TextView userIdTv;
    private Button toFindPwdBt;
    private String name, phone;
    private Context nowContext;
    private DatabaseReference findUserRef;
    private String fUid, fEmail, fName;

    Valid valid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);
        initialization();

        toFindPwdBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nowContext, FindPwdActivity.class);
                intent.putExtra("userUid", fUid);
                startActivity(intent);
                finish();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_complete, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*** 확인 버튼 눌렸을 때 ***/
            case R.id.action_complete :
                Log.w("===", "FindIdActivity: clicked button");
                name = nameTv.getText().toString();
                phone = phoneTv.getText().toString();
                Log.w("===", "FindIdActivity: phone, name = " + phone + ", " + name);

                if(!valid.isNotBlank(name)){
                    Log.w("===", "FindIdActivity: name is not valid");
                    Toast.makeText(nowContext, "이름을 입력해 주세요",
                            Toast.LENGTH_SHORT).show();
                    nameTv.requestFocus();
                    return true;
                }else if(!valid.isNotBlank(phone) || !valid.isValidPhone(phone)){
                    Log.w("===", "FindIdActivity: phone is not valid");
                    Toast.makeText(nowContext, "휴대폰 번호를 형식에 맞게 입력해 주세요",
                            Toast.LENGTH_SHORT).show();
                    phoneTv.requestFocus();
                    return true;
                }

                // DB에서 user 정보 찾기
                findUserId(name, phone);

                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }
    private void findUserId(final String name, final String phone) {

        Query query = findUserRef.orderByChild("phone").equalTo(phone);
        query.addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("===", "FindIdOnFirebaseDB : onDataChange");
                Log.w("===", "Value [" + dataSnapshot.getKey() + ", " + dataSnapshot.getValue() + "]");
                if(dataSnapshot.getValue() == null){
                    Log.w("===", "FindIdOnFirebaseDB : fail to find user");
                    userIdTv.setText("해당 회원을 찾을 수 없습니다.");
                }else{
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
//                    Log.w("===", "Value [" + userSnapshot.getKey() + ", " + userSnapshot.getValue() + "]");
                        fUid = (String) userSnapshot.getKey();
                        fEmail = (String) userSnapshot.child("email").getValue();
                        fName = (String) userSnapshot.child("name").getValue();
                    }
                    if (!fName.equals(name)){
                        Log.w("===", "FindIdOnFirebaseDB : fail to find user");
                        userIdTv.setText("해당 회원을 찾을 수 없습니다.");
                    }else {
                        Log.w("===", "FindIdOnFirebaseDB : succeed to find user [" + fUid + ", " + fEmail + "]");
                        userIdTv.setText("해당 회원의 아이디는 다음과 같습니다.\n"+fEmail);
                        toFindPwdBt.setVisibility(View.VISIBLE);
                    }

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
    private void initialization() {
        nowContext = this;
        nameTv = findViewById(R.id.findIdNameTv);
        phoneTv = findViewById(R.id.findIdPhoneTv);
        userIdTv = findViewById(R.id.findIdShowUserIdTv);
        toFindPwdBt = findViewById(R.id.findIdTofindPwdBt);
        findUserRef = FirebaseDatabase.getInstance().getReference("user");
        valid = new Valid();
    }
}
