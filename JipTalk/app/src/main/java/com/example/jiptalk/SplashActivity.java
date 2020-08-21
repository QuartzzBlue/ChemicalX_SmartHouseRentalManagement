package com.example.jiptalk;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.example.jiptalk.vo.Building;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;


public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private boolean autoLoginFlag;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppKeyHash();

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

            //1.
//            MyRunnable myRunnable = new MyRunnable();
//                Thread getDataThread = new Thread(myRunnable);
//                getDataThread.start();
//                Log.v(TAG,"getDataThread Starts");
//                try {
//                    getDataThread.join();
//                Log.v(TAG,"getDataThread joined");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//intent = new Intent(getApplicationContext(), MainActivity.class);

            //2.
//            AsyncTask<Void, Void, Void> getDataTask = new AsyncTask<Void, Void, Void>() {
//
//                @Override
//                protected void onPreExecute() {
//                    super.onPreExecute();
//                }
//
//                @Override
//                protected void onPostExecute(Void aVoid) {
//                    super.onPostExecute(aVoid);
//                    Log.v(TAG, "Move To MainActivity");
//                    intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                    DatabaseReference buildingReference = firebaseDatabase.getReference("buildings");
//                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//
//                    Constant.userUID = fUser.getUid();
//                    Constant.userID = fUser.getEmail();
//
//                    buildingReference.child(Constant.userUID).addValueEventListener(new ValueEventListener() {
//
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Log.v(TAG, "InitConstants() : onDataChange");
//
//                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                Building buildingItem = postSnapshot.getValue(Building.class);
//                                buildingItem.setId(postSnapshot.getKey());
//                                Constant.buildings.put(postSnapshot.getKey(), buildingItem);
//                            }
//
//                            if (Constant.buildings != null) {
//                                Log.v(TAG, "Set Constant.building : " + Constant.buildings.toString());
//                                Log.v(TAG, "InitConstants() : succeed");
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Log.v(TAG, "InitConstants() : onCancelled", databaseError.toException());
//                        }
//
//                    });
//                    return null;
//                }
//            }.execute();

//            new DataSync(this, new DataSync.OnTaskFinishedCallback() {
//                @Override
//                public void onTaskFinished() {
//                    Log.v(TAG, "Move To MainActivity");
//
//                        intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                        finish();
//
//
//                }
//            }).execute();

            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }else {

            intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void isAutoLogin(){
        SharedPreferences appData = getSharedPreferences("appData",MODE_PRIVATE);
        autoLoginFlag = appData.getBoolean("SAVE_LOGIN_DATA",false);
        Log.v(TAG,"AutoLogin : " + autoLoginFlag);
        String email = appData.getString("email","");
        String password = appData.getString("password","");

        return;
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
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



class DataSync extends AsyncTask<Void,Integer,Void>{

        private static final String TAG = "SplashActivity";
        private Context context;
        private OnTaskFinishedCallback callback;

        // Custom callbac
        public interface OnTaskFinishedCallback {
            void onTaskFinished();
        }

        public DataSync(final Context context, OnTaskFinishedCallback callback){
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference buildingReference = firebaseDatabase.getReference("buildings");
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

            AppData.userUID = fUser.getUid();
            AppData.userID = fUser.getEmail();

            buildingReference.child(AppData.userUID).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.v(TAG, "InitConstants() : onDataChange");

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Building buildingItem = postSnapshot.getValue(Building.class);
                        buildingItem.setId(postSnapshot.getKey());
                        //AppData.buildings.put(postSnapshot.getKey(), buildingItem);
                    }

                    if (AppData.buildings != null) {
                        Log.v(TAG, "Set Constant.building : " + AppData.buildings.toString());
                        Log.v(TAG, "InitConstants() : succeed");
                        callback.onTaskFinished();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.v(TAG, "InitConstants() : onCancelled", databaseError.toException());
                }

            });
            return null;
        }
}


