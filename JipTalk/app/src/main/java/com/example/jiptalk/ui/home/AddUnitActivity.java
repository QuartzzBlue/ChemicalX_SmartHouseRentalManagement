package com.example.jiptalk.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.R;

public class AddUnitActivity extends AppCompatActivity {

    EditText unitNameEt,tenantName,tenantPhoneEt,startDateEt,endDateEt,depositEt,monthlyFeeEt,manageFeeEt,totalFeeEt,payDayEt;
    ImageButton startDateBtn,endDateBtn;
    Button isSameBtn;
    RadioGroup leaseTypeRg,contractRg;
    RadioButton leaseMonthlyRb,leaseFullDepositRb,leaseFullFeeRb,contract3mRb,contract6mRb,contract1yRb,contract2yRb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);
        initialization();

    }
    /* AppBar 에 세이브 버튼 추가 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_save, menu) ;
        return true ;
    }


    public void initialization(){

    }
}
