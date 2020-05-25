package com.example.jiptalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private String name, phone;
    private Context nowContext;
    private DatabaseReference findUserRef;
    Valid valid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);
        initialization();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_save, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*** 확인 버튼 눌렸을 때 ***/
            case R.id.action_save :
                name = nameTv.getText().toString().trim();
                phone = phoneTv.getText().toString().trim();

                if(!valid.isNotBlank(phone) || !valid.isValidPhone(phone)){
                    Log.w("===", "FindIdActivity: phone is not valid");
                    Toast.makeText(nowContext, "휴대폰 번호를 형식에 맞게 입력해 주세요",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }

                // DB에서 user 정보 찾기
                findUserId(name, phone);

                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }
    private void findUserId(String name, String phone) {
        findUserRef = FirebaseDatabase.getInstance().getReference("user");
        Query query = findUserRef.orderByChild("phone").equalTo(phone);
        findUserRef.addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User userInfo = dataSnapshot.getValue(User.class);
                Log.w("===", "FindIdOnFirebaseDB : onDataChange");
                Log.w("===", "Value [" + userInfo.toString() + "]");

                // ...
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
    }
}
