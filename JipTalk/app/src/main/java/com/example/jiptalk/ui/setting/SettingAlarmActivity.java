package com.example.jiptalk.ui.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;


import com.example.jiptalk.R;
import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SettingAlarmActivity extends AppCompatActivity {
    private DatabaseReference dbRef;
    private Switch pushAlarm;
    private User user;
    private String userUid;
    private Context nowContext;
    Button test; //삭제해야될것

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_alarm);
        initialization();


        pushAlarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(nowContext, "푸시 알림 활성화",Toast.LENGTH_SHORT).show();
                    Log.w("===", "PushAlarm : ON");
                    user.setAlarmOn(true);
                } else {
                    Toast.makeText(nowContext, "푸시 알림 비활성화",Toast.LENGTH_SHORT).show();
                    Log.w("===", "PushAlarm : OFF");
                    user.setAlarmOn(false);
                }
                setPersonalUserSetting();
            }
        });

        // 자동로그인 정보 삭제
        test.setOnClickListener(new View.OnClickListener() { //삭제해야될것
            @Override
            public void onClick(View v) {
                SharedPreferences appData = getSharedPreferences("appData",MODE_PRIVATE);
                SharedPreferences.Editor editor = appData.edit();
                editor.clear();
                editor.commit();
            }
        });

    }

    private void initialization() {
        nowContext = this;
        dbRef = FirebaseDatabase.getInstance().getReference("user");
        pushAlarm = findViewById(R.id.switch_setting_alarm_pushAlarm);
        test = findViewById(R.id.btn_setting_alarm_test); //삭제해야될것
        getPersonalUserSetting();
    }

    private void getPersonalUserSetting() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userUid = currentUser.getUid();
        dbRef.child(userUid).addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.w("===", "getPersonalUserSetting() : onDataChange");

                ////// boolean 타입은 왜 매칭이 안되는거지??
                user.setAlarmOn((Boolean)dataSnapshot.child("isAlarmOn").getValue());
                Log.w("===", "Current User Info : " + user.toString());

                // 푸시알림 설정
                if(user.getAlarmOn()) pushAlarm.setChecked(true);
                else pushAlarm.setChecked(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("===", "getPersonalUserSetting() : onCancelled", databaseError.toException());
            }
        });
    }

    private void setPersonalUserSetting() {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+userUid, user.toMap());
        // 업데이트
        dbRef.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("===", "setPersonalUserSetting: succeed");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("===", "setPersonalUserSetting : Failed");

                        return;
                }
        });
    }
}
