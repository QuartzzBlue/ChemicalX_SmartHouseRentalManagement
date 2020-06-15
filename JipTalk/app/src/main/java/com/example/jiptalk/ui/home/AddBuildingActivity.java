package com.example.jiptalk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.jiptalk.vo.Building;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference buildingReference = firebaseDatabase.getReference("buildings");
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("===","createBuilding");

        final Util util = new Util();

        util.showProgressDialog(nowContext);

        String key = buildingReference.push().getKey();
        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+fUser.getUid()+"/"+key, newBuilding.toMap());
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
