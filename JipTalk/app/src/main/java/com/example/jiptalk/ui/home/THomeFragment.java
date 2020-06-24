package com.example.jiptalk.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.Unit;
import com.example.jiptalk.vo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class THomeFragment extends Fragment {

    View root;
    Button payNowBtn;
    TextView buildingInfoTv,monthTv,monthlyFee1Tv,paymentStatusTv;
    ImageView historyBtn,contactInfoBtn;
    TableLayout contactInfo;
    private TextView nameTv, depositorTv, phoneTv, leaseTypeTv, contractPeriodTv, depositTv, payDayTv, monthlyFeeTv, manageFeeTv, monthlyTotalFeeTv;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home_tenant, container, false);

        initialize();
        getData();
        return root;
    }

    public void initialize(){
        historyBtn = root.findViewById(R.id.btn_tenant_home_checkHistory);
        contactInfoBtn = root.findViewById(R.id.btn_tenant_home_contactInfo);
        payNowBtn= root.findViewById(R.id.btn_tenant_home_payNow);
        contactInfo = root.findViewById(R.id.tl_tenant_home);

        //납부 내역
        historyBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });

        //계약 정보
        contactInfoBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(contactInfo.getVisibility()==View.GONE){
                    contactInfo.setVisibility(View.VISIBLE);
                }else{
                    contactInfo.setVisibility(View.GONE);
                }
            }
        });

        //지금 납부 - 카카오 api
        payNowBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });

        monthlyFee1Tv = root.findViewById(R.id.tv_tenant_home_monthlyFee1);
        buildingInfoTv = root.findViewById(R.id.tv_tenant_home_buildingInfo);
        monthTv = root.findViewById(R.id.tv_tenant_home_Month);
        paymentStatusTv = root.findViewById(R.id.tv_tenant_home_paymentStatus);
        nameTv=root.findViewById(R.id.tv_tenant_home_name);
        depositorTv=root.findViewById(R.id.tv_tenant_home_depositor);
        phoneTv=root.findViewById(R.id.tv_tenant_home_phone);
        leaseTypeTv=root.findViewById(R.id.tv_tenant_home_leaseType);
        contractPeriodTv=root.findViewById(R.id.tv_tenant_home_contractPeriod);
        depositTv=root.findViewById(R.id.tv_tenant_home_deposit);
        payDayTv=root.findViewById(R.id.tv_tenant_home_payDay);
        monthlyFeeTv = root.findViewById(R.id.tv_tenant_home_monthlyFee);
        manageFeeTv=root.findViewById(R.id.tv_tenant_home_manageFee);
        monthlyTotalFeeTv = root.findViewById(R.id.tv_tenant_home_monthlyTotalFee);


    }

    public void getData(){
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String unitID = user.getUnitID();
                String buildingID = user.getBuildingID();
                FirebaseDatabase.getInstance().getReference().child("units").child(buildingID).child(unitID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Unit unit = dataSnapshot.getValue(Unit.class);
                        setData(unit);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setData(Unit thisUnit){
        NumberFormat myFormatter = NumberFormat.getInstance(Locale.getDefault());
        buildingInfoTv.setText(thisUnit.getUnitNum()+"호");
        if(thisUnit.getIsPaid().equals("0")){
            paymentStatusTv.setText("미납");
        }else{
            paymentStatusTv.setText("완납");
        }

        nameTv.setText(thisUnit.getTenantName());
        depositorTv.setText(thisUnit.getPayerName());
        phoneTv.setText(thisUnit.getTenantPhone());
        leaseTypeTv.setText(thisUnit.getLeaseType());
        contractPeriodTv.setText(thisUnit.getStartDate() + " ~ " + thisUnit.getEndDate());
        depositTv.setText(myFormatter.format(Integer.parseInt(thisUnit.getDeposit())) + "원");
        payDayTv.setText("매월 " + thisUnit.getPayDay() + "일");
        monthlyFeeTv.setText(thisUnit.getMonthlyFee() + "원");
        manageFeeTv.setText(thisUnit.getMngFee() + "원");
        int totalFee = Integer.parseInt(thisUnit.getMonthlyFee().replace(",", "")) + Integer.parseInt(thisUnit.getMngFee().replace(",", ""));
        monthlyTotalFeeTv.setText(myFormatter.format(totalFee) + "원");
        monthlyFee1Tv.setText(myFormatter.format(totalFee) + "원");
    }
}
