package com.example.jiptalk;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.jiptalk.ui.message.FirebaseNotificationService;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String TAG = "===";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitConstants();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_message, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if (Constant.newToken != null) { // 새로운 토큰 생성할 시 DB에 token 업데이트 및 token static 변수에 값 저장.
            Constant.token = Constant.newToken;
            Map map = new HashMap();
            map.put("token", Constant.token);
            reference.child("user").child(Constant.userUID).updateChildren(map);
        }

        // DB에서 token 값 가져와서 token static 변수에 값 저장.
        reference.child("user").child(Constant.userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap hashMap = (HashMap) dataSnapshot.getValue();
                Object tokenFromDB = hashMap.get("token");
                if (tokenFromDB == null) {
                    tokenFromDB = FirebaseInstanceId.getInstance().getToken();
                    Map map = new HashMap();
                    map.put("token", tokenFromDB.toString());
                    reference.child("user").child(Constant.userUID).updateChildren(map);
                }
                Log.d(TAG, "token0 : " + tokenFromDB);
                Constant.token = tokenFromDB.toString();
                Constant.category = hashMap.get("category").toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /*** Constant 변수 초기화 ***/
    public void InitConstants() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("buildings");

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


        Constant.userUID = fUser.getUid();
        Constant.userID = fUser.getEmail();

        databaseReference.child(Constant.userUID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.w("===", "InitConstants() : onDataChange");
                Constant.buildings = (HashMap<String, Building>) dataSnapshot.getValue();
//
//                for(DataSnapshot bdSnapshot : dataSnapshot.getChildren()){
//                    String bId = bdSnapshot.getKey();
//                    bdSnapshot.child("units").getValue();
//                }
                Log.w("===", "Set Constant.building : " + Constant.buildings.toString());
                Log.w("===", "InitConstants() : succeed");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("===", "InitConstants() : onCancelled", databaseError.toException());
            }
        });

    }

}
