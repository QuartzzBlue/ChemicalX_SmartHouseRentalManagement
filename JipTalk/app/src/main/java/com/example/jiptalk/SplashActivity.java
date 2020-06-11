package com.example.jiptalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.Building;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends Activity {
    private boolean autoLoginFlag;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** 자동 로그인 체크 **/
        isAutoLogin();

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                if(autoLoginFlag){
//                    intent = new Intent(getApplicationContext(), MainActivity.class);
//
//                }else{
//                    intent = new Intent(getApplicationContext(), LoginActivity.class);
//                }
//
//                startActivity(intent);
//                finish();
//            }
//        }, 2000);

        if(autoLoginFlag){
            MyRunnable myRunnable = new MyRunnable();
            Thread getDataThread = new Thread(myRunnable);
            getDataThread.start();
            try {
                getDataThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }else {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }

        startActivity(intent);
        finish();



    }

   class MyRunnable implements Runnable{

       @Override
       public void run() {
           FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
           DatabaseReference buildingReference = firebaseDatabase.getReference("buildings");
//        DatabaseReference userReference = firebaseDatabase.getReference("user");

           FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


           Constant.userUID = fUser.getUid();
           Constant.userID = fUser.getEmail();

           buildingReference.child(Constant.userUID).addValueEventListener(new ValueEventListener() {

               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   Log.w("===", "InitConstants() : onDataChange");

                   for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                       Building buildingItem = postSnapshot.getValue(Building.class);
                       buildingItem.setId(postSnapshot.getKey());
                       Constant.buildings.put(postSnapshot.getKey(),buildingItem);
                   }

                   if (Constant.buildings != null) {
                       Log.w("===", "Set Constant.building : " + Constant.buildings.toString());
                       Log.w("===", "InitConstants() : succeed");
                   }

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                   Log.w("===", "InitConstants() : onCancelled", databaseError.toException());
               }

           });

       }

    };

    private void isAutoLogin(){
        SharedPreferences appData = getSharedPreferences("appData",MODE_PRIVATE);
        autoLoginFlag = appData.getBoolean("SAVE_LOGIN_DATA",false);
        Log.d("===","AutoLogin : " + autoLoginFlag);
        String email = appData.getString("email","");
        String password = appData.getString("password","");
        return;
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        /* 스플래시 화면에서 뒤로가기 기능 제거. */
    }

}
