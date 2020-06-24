package com.example.jiptalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.jiptalk.tenant.TMainActivity;
import com.example.jiptalk.ui.message.FirebaseNotificationService;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    Context nowContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nowContext = this;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                setNavigation(user.getCategory());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setNavigation(String category){

        if(category.equals("임대인")){
            setContentView(R.layout.activity_main);
            BottomNavigationView navView1 = findViewById(R.id.nav_view);
            Toast.makeText(nowContext,"임대인 로그인",Toast.LENGTH_LONG).show();
            AppBarConfiguration appBarConfiguration= new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_message, R.id.navigation_setting)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView1, navController);

        }else if (category.equals("세입자")){
            setContentView(R.layout.activity_tmain);
            BottomNavigationView navView2 = findViewById(R.id.nav_view_tenant);
            Toast.makeText(nowContext,"세입자 로그인",Toast.LENGTH_LONG).show();
            AppBarConfiguration appBarConfiguration= new AppBarConfiguration.Builder(
                    R.id.tnavigation_home, R.id.navigation_message, R.id.navigation_setting)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_tenant);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView2, navController);
        }

    }
}
