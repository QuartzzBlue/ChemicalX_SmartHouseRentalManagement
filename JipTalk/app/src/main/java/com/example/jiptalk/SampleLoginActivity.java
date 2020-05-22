package com.example.jiptalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SampleLoginActivity extends AppCompatActivity {
    TextView userId, userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_login);

        userId = findViewById(R.id.loginUserId);
        userUid = findViewById(R.id.loginUserUid);

        Intent intent = getIntent();
        String userID = intent.getExtras().getString("userID"); /*String형*/
        String userUID = intent.getExtras().getString("userUID"); /*String형*/
        userId.setText(userID);
        userUid.setText(userUID);
    }
}
