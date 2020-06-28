package com.example.jiptalk.ui.unit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jiptalk.Constant;
import com.example.jiptalk.FindIdActivity;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.Unit;

import java.text.NumberFormat;
import java.util.Locale;

public class Tab2Fragment extends Fragment {
    private ViewGroup viewGroup;
    private TextView nameTv, depositorTv, phoneTv, leaseTypeTv, contractPeriodTv, depositTv, payDayTv, monthlyFeeTv, manageFeeTv, monthlyTotalFeeTv;
    private Unit thisUnit;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        viewGroup = (ViewGroup) inflater.inflate(R.layout.item_unit_detail_tab2,null);
        thisUnit = (Unit) getArguments().getSerializable("thisUnit");
        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();


    }

    private void initialize() {
        NumberFormat myFormatter = NumberFormat.getInstance(Locale.getDefault());
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

        nameTv.setText(thisUnit.getTenantName());
        depositorTv.setText(thisUnit.getPayerName());
        phoneTv.setText(thisUnit.getTenantPhone());
        leaseTypeTv.setText(thisUnit.getLeaseType());
        contractPeriodTv.setText(thisUnit.getStartDate() + " ~ " + thisUnit.getEndDate());
        depositTv.setText(thisUnit.getDeposit() + "원");
        payDayTv.setText("매월 " + thisUnit.getPayDay() + "일");
        monthlyFeeTv.setText(thisUnit.getMonthlyFee() + "원");
        manageFeeTv.setText(thisUnit.getMngFee() + "원");
        int totalFee = Integer.parseInt(thisUnit.getMonthlyFee().replace(",", "")) + Integer.parseInt(thisUnit.getMngFee().replace(",", ""));
        monthlyTotalFeeTv.setText(myFormatter.format(totalFee) + "원");

    }

}