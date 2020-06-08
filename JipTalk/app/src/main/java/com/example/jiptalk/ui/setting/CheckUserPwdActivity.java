package com.example.jiptalk.ui.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jiptalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CheckUserPwdActivity extends AppCompatActivity {

    private boolean isVerified = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user_pwd);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_next, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /*** 다음 버튼 눌렸을 때 ***/
            case R.id.action_next :
                Log.w("===", "CheckUserPwdActivitiy : next button clicked");


                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

}
