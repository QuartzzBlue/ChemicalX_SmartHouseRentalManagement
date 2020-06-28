package com.example.jiptalk.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jiptalk.Constant;
import com.example.jiptalk.FindIdActivity;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.Unit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Tab2Fragment extends Fragment {
    private ViewGroup viewGroup;
    private TextView nameTv, depositorTv, phoneTv, leaseTypeTv, contractPeriodTv, depositTv, payDayTv, monthlyFeeTv, manageFeeTv, monthlyTotalFeeTv;
    private Unit thisUnit;
    private String thisUnitKey, thisBuildingKey;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        viewGroup = (ViewGroup) inflater.inflate(R.layout.item_unit_detail_tab2,null);
//        thisUnit = (Unit) getArguments().getSerializable("thisUnit");
        thisUnitKey = getArguments().getString("thisUnitKey");
        thisBuildingKey = getArguments().getString("thisBuildingKey");
        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();


    }

    private void initialize() {
        final NumberFormat myFormatter = NumberFormat.getInstance(Locale.getDefault());
        nameTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_name);
        depositorTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_depositor);
        phoneTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_phone);
        leaseTypeTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_leaseType);
        contractPeriodTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_contractPeriod);
        depositTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_deposit);
        payDayTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_payDay);
        monthlyFeeTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_monthlyFee);
        manageFeeTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_manageFee);
        monthlyTotalFeeTv = viewGroup.findViewById(R.id.tv_unit_detail_tab2_monthlyTotalFee);

        DatabaseReference unitRef = FirebaseDatabase.getInstance().getReference("units");
        unitRef.child(thisBuildingKey).child(thisUnitKey).addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("===", "Tab2/initialize() : onDataChange");
                thisUnit = dataSnapshot.getValue(Unit.class);
                thisUnit.setUnitID(dataSnapshot.getKey());

                nameTv.setText(thisUnit.getTenantName());
                depositorTv.setText(thisUnit.getPayerName());
                phoneTv.setText(thisUnit.getTenantPhone());
                leaseTypeTv.setText(thisUnit.getLeaseType());
                contractPeriodTv.setText(thisUnit.getStartDate() + " ~ " + thisUnit.getEndDate());
                int deposit = Integer.parseInt(thisUnit.getDeposit());
                depositTv.setText(myFormatter.format(deposit) + "원");
                payDayTv.setText("매월 " + thisUnit.getPayDay() + "일");
                int monthlyFee = Integer.parseInt(thisUnit.getMonthlyFee());
                int manageFee = Integer.parseInt(thisUnit.getMngFee());
                monthlyFeeTv.setText(myFormatter.format(monthlyFee) + "원");
                manageFeeTv.setText(myFormatter.format(manageFee) + "원");
                int totalFee = Integer.parseInt(thisUnit.getMonthlyFee().replace(",", "")) + Integer.parseInt(thisUnit.getMngFee().replace(",", ""));
                monthlyTotalFeeTv.setText(myFormatter.format(totalFee) + "원");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("===", "getPersonalInfoThread() : onCancelled", databaseError.toException());
            }
        });

    }

}