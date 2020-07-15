package com.example.jiptalk.ui.message;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class AddNotiActivity extends AppCompatActivity {

    String TAG = "===";
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    String uid = FirebaseAuth.getInstance().getUid().toString();
    String buildingName, buildingKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_noti);

        buildingName = getIntent().getStringExtra("buildingName");
        Log.d(TAG, "buildingName : " + buildingName);

        buildingKey = getIntent().getStringExtra("buildingKey");
        Log.d(TAG, "buildingKey : " + buildingKey);


        getSupportActionBar().setTitle("공지사항 등록하기 (" + buildingName + ")");


    }


    public void registerNoti(View v) {
        Log.d(TAG, "Entered registerNoti()");

        String notiref = "noti/" + uid;


        DatabaseReference notiPush = ref.child("noti").child(uid).push();

        String pushID = notiPush.getKey();

        Map sendTime = ServerValue.TIMESTAMP;

        String title = ((EditText) findViewById(R.id.editTextNotiTitle)).getText().toString();
        String content = ((EditText) findViewById(R.id.editTextNotiContent)).getText().toString();

        if (title.equals("") || title == null || content.equals("") || content == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림").setMessage("제목과 내용 모두 입력해주세요.").setIcon(R.drawable.ic_notifications_black_24dp);
            builder.setNegativeButton("닫기", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }

        Map notiMap = new HashMap();
        notiMap.put("key", pushID);
        notiMap.put("title", title);
        notiMap.put("content", content);
        notiMap.put("time", sendTime);
        notiMap.put("buildingName", buildingName);
        notiMap.put("buildingKey", buildingKey);
        notiMap.put("from", uid);

        Map notiRegisterMap = new HashMap();
        notiRegisterMap.put(notiref + "/" + pushID, notiMap);

        ref.updateChildren(notiRegisterMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, databaseError.getMessage().toString());
                }
            }
        });
        finish();
    }
}
