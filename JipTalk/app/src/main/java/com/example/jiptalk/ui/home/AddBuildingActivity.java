package com.example.jiptalk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.MainActivity;
import com.example.jiptalk.R;
import com.example.jiptalk.Util;
import com.example.jiptalk.Valid;
import com.example.jiptalk.ui.building.JusoWebViewActivity;
import com.example.jiptalk.vo.Building;
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

public class AddBuildingActivity extends AppCompatActivity {

    Valid valid;
    Context nowContext;
    static final int REQUEST_CODE = 400;

    EditText et_buildingAddress,et_buildingName,et_totalUnitCnt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);
        initialization();

    }

    /* AppBar 에 세이브 버튼 추가 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_save, menu) ;
        return true ;
    }

    //세이브 버튼 클릭 시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save :

                String buildingAddress = et_buildingAddress.getText().toString();
                String buildingName = et_buildingName.getText().toString();
                int totalUnitCnt = Integer.parseInt(et_totalUnitCnt.getText().toString());
                Building newBuilding = new Building(buildingName,buildingAddress,totalUnitCnt);

                /* 유효성 검사 */
                if(!isValid(newBuilding)) return false;

                /* 유효성 검사 끝나면 인증 & DB 삽입*/
                createBuilding(newBuilding);

                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

    private void createBuilding(final Building newBuilding){

        Log.d("===","createBuilding");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userReference = firebaseDatabase.getReference("user");
        final DatabaseReference buildingReference = firebaseDatabase.getReference("buildings");
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Util util = new Util();

        util.showProgressDialog(nowContext);

        userReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("===","createBuilding : onDataChange");
                User currentUser = dataSnapshot.getValue(User.class);

                newBuilding.setLlBank(currentUser.getBank());
                newBuilding.setLlAccountNum(currentUser.getAccountNum());
                newBuilding.setLlDepositor(currentUser.getDepositor());

                String key = buildingReference.push().getKey();
                Map<String,Object> childUpdates = new HashMap<>();
                childUpdates.put("/"+uid+"/"+key, newBuilding.toMap());
                buildingReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("===", "insertBuildingToDatabase: succeed");
                        Toast.makeText(nowContext, "성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        util.hideProgressDialog();
                        // 액티비티 이동
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(nowContext, "건물 등록이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.w("===", "insertBuildingToDatabase : Failed");
                        util.hideProgressDialog();
                        return;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("===","createBuilding : onCancelled");
            }
        });




    }

    private void initialization(){

        et_buildingAddress = findViewById(R.id.tv_add_building_address);
        et_buildingName = findViewById(R.id.tv_add_building_buildingName);
        et_totalUnitCnt = findViewById(R.id.tv_add_building_totalUnitCnt);

        Button searchBtn = findViewById(R.id.btn_add_building_search);

        //도로명 주소 웹뷰 액티비티로 이동
        searchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JusoWebViewActivity.class);
                startActivityForResult(intent,REQUEST_CODE);

            }
        });

        nowContext = this; // this = View.getContext();  현재 실행되고 있는 View의 context를 return 하는데 보통은 현재 활성화된 activity의 context가 된다.
        valid = new Valid();

    }

    //도로명 주소 웹뷰 액티비티에서 주소 받아옴
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            if(resultCode == RESULT_OK){
                et_buildingAddress.setText(data.getStringExtra("result"));
            }
        }
    }

    private boolean isValid(Building newBuilding){

        if(!valid.isNotBlank(newBuilding.getName())||!valid.isNotBlank(newBuilding.getBuildingAddress())||!valid.isNotBlank(Integer.toString(newBuilding.getUnitCnt()))){
            Log.d("===", "createBuilding : not valid value" );
            Toast.makeText(nowContext, "빈칸을 모두 채워주세요",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
