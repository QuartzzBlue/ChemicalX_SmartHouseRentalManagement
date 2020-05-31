package com.example.jiptalk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.R;
import com.example.jiptalk.Util;
import com.example.jiptalk.Valid;
import com.example.jiptalk.vo.Building;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddBuildingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference buildingRef;
    Valid valid;
    Context nowContext;
    static final int REQUEST_CODE = 400;

    EditText et_buildingAddress,et_buildingName,et_totalUnitCnt;
    Button searchBtn;

    SharedPreferences appData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);
        initialization();

        //도로명 주소
        searchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JusoWebViewActivity.class);
                startActivityForResult(intent,REQUEST_CODE);

            }
        });
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*** 저장 버튼 눌렸을 때 ***/
            case R.id.action_save :

                String buildingAddress = et_buildingAddress.getText().toString();
                String buildingName = et_buildingName.getText().toString();
                int totalUnitCnt = Integer.parseInt(et_totalUnitCnt.getText().toString());
                Building newBuilding = new Building(buildingName,buildingAddress,totalUnitCnt);
                //buildingID : userId_buildingname_등록날짜
                String uid = appData.getString("email","").split("@")[0];
                newBuilding.setId(uid);
                Log.d("===",newBuilding.getId());

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

        final Util util = new Util();

        util.showProgressDialog(nowContext);

        /*** Firebase Authentication ***/
        //FirebaseAuth 인스턴스 가져오기
        mAuth = FirebaseAuth.getInstance();
        //데이터베이스 레퍼런스 설정
        buildingRef = FirebaseDatabase.getInstance().getReference();

        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("building/"+ newBuilding.getId(), newBuilding.toMap());
        buildingRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("===", "insertBuildingToDatabase: succeed");
                                            Toast.makeText(nowContext, "성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                            util.hideProgressDialog();
                                            // 액티비티 이동
//                                            Intent intent = new Intent(getApplicationContext(), SignUpCompleteActivity.class);
//                                            startActivity(intent);
//                                            finish();
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
        searchBtn = findViewById(R.id.btn_add_building_search);

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
