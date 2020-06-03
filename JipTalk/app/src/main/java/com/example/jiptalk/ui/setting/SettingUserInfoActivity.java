package com.example.jiptalk.ui.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Button modPwdBt, modPhoneBt;
    private ImageView modNameBt;
    private User currentUser;
    private DatabaseReference dbRef;
    private Runnable getPersonalInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user_info);
        initialization();

//        Thread t = new Thread(getPersonalInfo);
//        Log.w("===", "Thread");
//        t.start();
//        Log.w("===", "Thread start");
//        try {
//            Log.w("===", "join : before");
//            t.join();
//            Log.w("===", "join : after");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }

    private void initialization() {

        Log.w("===", "initialization start");
        emailTv = findViewById(R.id.tv_setting_user_info_userEmail);
        nameTv = findViewById(R.id.tv_setting_user_info_userName);
        modPwdBt = findViewById(R.id.bt_setting_user_info_modPassword);
        modPhoneBt = findViewById(R.id.bt_setting_user_info_modPhoneNum);
        modNameBt = findViewById(R.id.image_setting_user_info_modUserName);
//        emailTv.setText(currentUser.getEmail());

        modNameBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("===", "onClick");
                FragmentManager fm = getSupportFragmentManager();
                final ModUserNameDialog modNameDialog = new ModUserNameDialog();
                modNameDialog.show(fm, "Fragment Tag");
            }
        });
        Log.w("===", "initialization start");

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPersonalInfo();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("===", "getPersonalInfoThread() : onCancelled", databaseError.toException());
            }
        });
    }

    class getPersonalInfoThread implements Runnable {

        @Override
        public void run() {
            dbRef.addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(User.class);
                    Log.w("===", "getPersonalInfoThread() : onDataChange");
                    Log.w("===", "getPersonalInfoThread() : " + currentUser.toString());
                    Log.w("===", "getPersonalInfoThread() : " + dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("===", "getPersonalInfoThread() : onCancelled", databaseError.toException());
                }
            });
        }

    }
}

