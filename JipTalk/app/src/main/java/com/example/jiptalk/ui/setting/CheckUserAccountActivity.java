package com.example.jiptalk.ui.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.User;

public class CheckUserAccountActivity extends AppCompatActivity {
    private User userInfo;
    private TextView bankTv, accountNumTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user_account);
        initialize();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_modify, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /*** 변경 버튼 눌렸을 때 ***/
            case R.id.action_modify :
                Intent intent = new Intent(getApplicationContext(), ModUserAccountActivity.class);
                startActivity(intent);
                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

    private void initialize() {
        userInfo = (User) getIntent().getSerializableExtra("userInfo");
        bankTv = findViewById(R.id.tv_check_user_account_bank);
        accountNumTv = findViewById(R.id.tv_check_user_account_accountNum);

        if(userInfo.getAccountNum() != null && userInfo.getBank() != null && userInfo.getDepositor() != null){
            bankTv.setText(userInfo.getBank() + "은행 " + userInfo.getDepositor());
            accountNumTv.setText(userInfo.getAccountNum());
        }else {
            bankTv.setText("계좌 정보가 없습니다.");
        }

    }

}
