package com.example.jiptalk.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.R;
import com.example.jiptalk.Valid;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class AddBuildingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference buildingRef;
    Valid valid;

    EditText et_buildingAddress,et_buildingName,et_totalUnitCnt;
    ImageButton searchBtn;


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
                startActivity(intent);
            }
        });
    }

    /* AppBar 에 세이브 버튼 추가 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_save, menu) ;
        return true ;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            /*** 저장 버튼 눌렸을 때 ***/
//            case R.id.action_save :
//
//                String buildingAddress = et_buildingAddress.getText().toString();
//                String buildingName = et_buildingName.getText().toString();
//                int totalUnitCnt = Integer.parseInt(et_totalUnitCnt.getText().toString());
//                Building newBuilding = new Building(buildingName,buildingAddress,totalUnitCnt);
//
//                /* 유효성 검사 */
//                if(!isValid(newBuilding)) return false;
//
//                /* 유효성 검사 끝나면 인증 & DB 삽입*/
//                //createUser(newUser);
//
//                return true ;
//            default :
//                return super.onOptionsItemSelected(item) ;
//        }
//    }

//    private void createBuilding(final Building newBuilding){
//
//        final Util util = new Util();
//
//        util.showProgressDialog(nowContext);
//
//        /*** Firebase Authentication ***/
//        //FirebaseAuth 인스턴스 가져오기
//        mAuth = FirebaseAuth.getInstance();
//        //데이터베이스 레퍼런스 설정
//        buildingRef = FirebaseDatabase.getInstance().getReference();
//
////        //[Authentification] Email로 유저 생성
////        mAuth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPwd())
////                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
////                    @Override
////                    public void onComplete(@NonNull Task<AuthResult> task) {
////                        Log.d("===", "createUserAuthentication:onComplete:" + task.isSuccessful());
////
////                        if (!task.isSuccessful()) { // 인증 실패
////                            Log.d("===", "createUserAuthentication:failure", task.getException());
////                            Toast.makeText(nowContext, "인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
////                            util.hideProgressDialog();
////                        } else { // 인증 성공
////                            Log.d("===", "createUserAuthentication: succeed");
////
////                            /*** DB에 USER 정보 등록 ***/
////                            FirebaseUser user = task.getResult().getUser();
////
////                            String uid = user.getUid();
////
////
////                            Map<String, Object> childUpdates = new HashMap<>();
////                            childUpdates.put("user/"+ uid, newUser.toMap());
////                            // 업데이트
////                            userRef.updateChildren(childUpdates)
////                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
////                                        @Override
////                                        public void onSuccess(Void aVoid) {
////                                            Log.d("===", "insertUserToDatabase: succeed");
////                                            Toast.makeText(nowContext, "회원가입이 성공적으로 이루어졌습니다.", Toast.LENGTH_SHORT).show();
////                                            util.hideProgressDialog();
////                                            // 액티비티 이동
////                                            Intent intent = new Intent(getApplicationContext(), SignUpCompleteActivity.class);
////                                            startActivity(intent);
////                                            finish();
////                                        }
////                                    }).addOnFailureListener(new OnFailureListener() {
////                                @Override
////                                public void onFailure(@NonNull Exception e) {
////                                    Toast.makeText(nowContext, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
////                                    Log.w("===", "insertUserToDatabase : Failed");
////                                    util.hideProgressDialog();
////                                    return;
////                                }
////                            });
////
////                        }
////
////
////
////                    }
////                });
//
//    }

    private void initialization(){
        et_buildingAddress = findViewById(R.id.tv_add_building_address);
        et_buildingName = findViewById(R.id.tv_add_building_buildingName);
        et_totalUnitCnt = findViewById(R.id.tv_add_building_totalUnitCnt);
        searchBtn = findViewById(R.id.btn_add_building_search);

        //nowContext = this; // this = View.getContext();  현재 실행되고 있는 View의 context를 return 하는데 보통은 현재 활성화된 activity의 context가 된다.
        //valid = new Valid();
    }

//    private boolean isValid(Building newBuilding){
//
//        if(!valid.isNotBlank(newBuilding.getName())||!valid.isNotBlank(newBuilding.getBuildingAddress())||!valid.isNotBlank(Integer.toString(newBuilding.getUnitCnt()))){
//            Log.d("===", "createBuilding : not valid value" );
//            Toast.makeText(nowContext, "빈칸을 모두 채워주세요",
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
}
