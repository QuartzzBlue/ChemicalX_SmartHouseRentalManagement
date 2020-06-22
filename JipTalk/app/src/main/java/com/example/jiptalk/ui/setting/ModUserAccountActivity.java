package com.example.jiptalk.ui.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jiptalk.R;
import com.example.jiptalk.Valid;
import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ModUserAccountActivity extends AppCompatActivity {

    private Context nowContext;
    private User userInfo;
    private EditText depositorEt, accountNumEt;
    private Spinner bankSp;
    Valid valid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_user_account);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_complete, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /*** 완료 버튼 눌렸을 때 ***/
            case R.id.action_complete :
                String bank = bankSp.getSelectedItem().toString();
                String depositor = depositorEt.getText().toString();
                String accountNum = accountNumEt.getText().toString();


                // 유효성 체크
                // 계좌번호 유효성체크는 따로 하진 않았음 ㅜ
                if(bank.equals("선택하세요")){
                    Log.d("===", "onOptionsItemSelected() : bank is not selected");
                    Toast.makeText(nowContext, "은행을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(!valid.isNotBlank(depositor)){
                    Log.d("===", "onOptionsItemSelected() : empty depositor column");
                    Toast.makeText(nowContext, "예금주명을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    depositorEt.requestFocus();
                    return false;
                }

                if(!valid.isNotBlank(accountNum)){
                    Log.d("===", "onOptionsItemSelected() : empty accountNum column");
                    Toast.makeText(nowContext, "계좌번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    accountNumEt.requestFocus();
                    return false;
                }

                userInfo.setBank(bank);
                userInfo.setDepositor(depositor);
                userInfo.setAccountNum(accountNum);

                modifyUserAccount();

                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

    private void modifyUserAccount() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user/"+uid);
        userRef.setValue(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("===", "modifyUserAccount() : onSuccess");
                Toast.makeText(nowContext, "계좌정보가 성공적으로 변경되었습니다..", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(nowContext, SettingUserInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //중간 스택에 쌓인 액티비티들 제거
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //액티비티가 스택 맨 위에 실행중이라면 재사용
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("===", "modifyUserAccount() : onFailure " + e.getStackTrace());
                Toast.makeText(nowContext, "계좌정보 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialize() {
        nowContext = this;
        valid = new Valid();
        userInfo = (User) getIntent().getSerializableExtra("userInfo");
        depositorEt = findViewById(R.id.et_mod_user_account_depositor);
        accountNumEt = findViewById(R.id.et_mod_user_account_accountNum);
        bankSp = findViewById(R.id.spinner_mod_user_account_bank);
        ArrayAdapter bankAdapter = ArrayAdapter.createFromResource(nowContext, R.array.bank_list, android.R.layout.simple_spinner_item);
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankSp.setAdapter(bankAdapter);
    }
}
