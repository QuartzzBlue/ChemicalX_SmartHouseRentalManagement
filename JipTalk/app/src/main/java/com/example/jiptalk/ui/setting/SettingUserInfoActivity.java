package com.example.jiptalk.ui.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingUserInfoActivity extends AppCompatActivity {

    private TextView emailTv, nameTv;
    private Button modPwdBt, modPhoneBt, modAccountBt;
    private ImageView modNameBt;
    private User currentUser;
    private DatabaseReference dbRef;
    private Context nowContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user_info);
        getPersonalInfo();
        initialization();


    }

    private void initialization() {

        Log.w("===", "initialization start");
        nowContext = this;
        emailTv = findViewById(R.id.tv_setting_user_info_userEmail);
        nameTv = findViewById(R.id.tv_setting_user_info_userName);
        modPwdBt = findViewById(R.id.bt_setting_user_info_modPassword);
        modPhoneBt = findViewById(R.id.bt_setting_user_info_modPhoneNum);
        modNameBt = findViewById(R.id.image_setting_user_info_modUserName);
        modAccountBt = findViewById(R.id.bt_setting_user_info_modAccount);

        //edit icon
        modNameBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("===", "modNameBt : onClick");
                //modMameDialog 띄움
                FragmentManager fm = getSupportFragmentManager();
                final ModUserNameDialog modNameDialog = new ModUserNameDialog(nowContext);
                modNameDialog.show(fm, "modNameDialog show");
            }
        });

        //핸드폰번호 변경
        modPhoneBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("===", "modPhoneBt : onClick");
                Intent intent = new Intent(nowContext, ModUserPhoneActivity.class);
                startActivity(intent);
            }
        });

        //비밀번호 변경
        modPwdBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("===", "modPwdBt : onClick");
                Intent intent = new Intent(nowContext, CheckUserPwdActivity.class);
                startActivity(intent);
            }
        });

        //계좌정보 변경
        modAccountBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("===", "modAccountBt : onClick");
                Intent intent = new Intent(nowContext, CheckUserAccountActivity.class);
                intent.putExtra("userInfo", currentUser);
                startActivity(intent);
            }
        });

    }


    private void getPersonalInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference("user/"+ user.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                Log.w("===", "getPersonalInfoThread() : onDataChange");
                Log.w("===", "getPersonalInfoThread() : " + currentUser.toString());
                Log.w("===", "getPersonalInfoThread() : " + dataSnapshot.getValue());

                emailTv.setText(currentUser.getEmail());
                nameTv.setText(currentUser.getName());

                if(currentUser.getCategory().trim().equals("임대인")){
                    Log.w("===", "if");
                    modAccountBt.setVisibility(View.VISIBLE);

                } else {
                    Log.w("===", "else");
                    modAccountBt.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("===", "getPersonalInfoThread() : onCancelled", databaseError.toException());
            }
        });
    }

//    class getPersonalInfoThread implements Runnable {
//
//        @Override
//        public void run() {
//            dbRef.addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    currentUser = dataSnapshot.getValue(User.class);
//                    Log.w("===", "getPersonalInfoThread() : onDataChange");
//                    Log.w("===", "getPersonalInfoThread() : " + currentUser.toString());
//                    Log.w("===", "getPersonalInfoThread() : " + dataSnapshot.getValue());
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.w("===", "getPersonalInfoThread() : onCancelled", databaseError.toException());
//                }
//            });
//        }
//
//    }
}

