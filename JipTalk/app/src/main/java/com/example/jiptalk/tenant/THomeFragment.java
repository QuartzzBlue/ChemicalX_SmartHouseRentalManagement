package com.example.jiptalk.tenant;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.R;
import com.example.jiptalk.TenantPayDialog;
import com.example.jiptalk.vo.Account;
import com.example.jiptalk.vo.Credit;
import com.example.jiptalk.vo.Unit;
import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class THomeFragment extends Fragment {

    View root;
    Button payNowBtn;
    TextView buildingInfoTv,monthTv,monthlyFee1Tv,paymentStatusTv,emptyView;
    ImageView historyBtn,contactInfoBtn;
    TableLayout contactInfo;
    RecyclerView creditHistoryRv;
    Calendar calendar,target;
    String buildingID,unitID;
    String unitNum;
    Credit recentCredit;
    private TextView nameTv, depositorTv, phoneTv, leaseTypeTv, contractPeriodTv, depositTv, payDayTv, monthlyFeeTv, manageFeeTv, monthlyTotalFeeTv;
    private User currentUser;
    private Account landlordAct;
    private int totalFee;
    boolean isPaidFlag = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home_tenant, container, false);

        initialize();

        return root;
    }

    public void initialize(){
        landlordAct = new Account();
        historyBtn = root.findViewById(R.id.btn_tenant_home_checkHistory);
        contactInfoBtn = root.findViewById(R.id.btn_tenant_home_contactInfo);
        payNowBtn= root.findViewById(R.id.btn_tenant_home_payNow);
        contactInfo = root.findViewById(R.id.tl_tenant_home);
        calendar = Calendar.getInstance();
        target = Calendar.getInstance();
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
        creditHistoryRv=root.findViewById(R.id.CreditHistoryRv);
        emptyView = root.findViewById(R.id.rv_tenant_home_emptyView);


        //납부 내역
        ((LinearLayout)(root.findViewById(R.id.CreditHistoryLayout))).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(creditHistoryRv.getVisibility()==View.GONE){
                    creditHistoryRv.setVisibility(View.VISIBLE);
                    ((LinearLayout)(root.findViewById(R.id.tenant_home_rvHeader))).setVisibility(View.VISIBLE);
                }else{
                    creditHistoryRv.setVisibility(View.GONE);
                    ((LinearLayout)(root.findViewById(R.id.tenant_home_rvHeader))).setVisibility(View.GONE);
                }
            }
        });

        //계약 정보
        ((LinearLayout)(root.findViewById(R.id.ContactInfoLayout))).setOnClickListener(new View.OnClickListener(){

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
                Account tenantAct = new Account(currentUser.getBank(), currentUser.getAccountNum(), currentUser.getDepositor());
//                Account landlordAct = new Account(currentUser.getBank(), currentUser.getAccountNum(), currentUser.getDepositor());

                FragmentManager fm = getActivity().getSupportFragmentManager();
                TenantPayDialog tenantPayDialog = new TenantPayDialog(getContext(), landlordAct, tenantAct, buildingID,unitNum,recentCredit);
                tenantPayDialog.show(fm, "tenantPayFeeDialog show");

            }
        });

        getContractData();
    }

    public void getContractData(){
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                unitID = currentUser.getUnitID();
                buildingID = currentUser.getBuildingID();
                FirebaseDatabase.getInstance().getReference().child("units").child(buildingID).child(unitID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Unit unit = dataSnapshot.getValue(Unit.class);
                        setData(unit);
                        getCreditHistoryData();
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

    public void getCreditHistoryData(){
        DatabaseReference creditRef = FirebaseDatabase.getInstance().getReference("credit").child(unitID);

        creditRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.w("===", "getCreditHistoryData executed");

                HashMap<String, Credit> creditMap = new HashMap<String, Credit>();
                for(DataSnapshot credits : dataSnapshot.getChildren()) {
                    Credit temp = credits.getValue(Credit.class);
                    if(temp.getStatus().equals("미납")) {
                        isPaidFlag = false;
                    }
                    creditMap.put(credits.getKey(), temp);
                }
                //map to arrayList
                Collection<Credit> values = creditMap.values();
                ArrayList<Credit> creditList = new ArrayList<>(values);

                // 날짜 최신순으로 정렬
                Collections.sort(creditList, new Comparator<Credit>() {
                    @Override
                    public int compare(Credit o1, Credit o2) {
                        return o2.getBillingDate().compareTo(o1.getBillingDate());
                    }
                });

                if(creditList.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    creditHistoryRv.setVisibility(View.GONE);
                } else {

                    recentCredit = creditList.get(0);
                    //https://androidyongyong.tistory.com/5

                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    creditHistoryRv.setLayoutManager(mLayoutManager);
                    CreditViewAdapter creditAdapter = new CreditViewAdapter(creditList);
                    creditHistoryRv.setAdapter(creditAdapter);
                    creditHistoryRv.setItemAnimator(new DefaultItemAnimator());

                    DatabaseReference unitRef = FirebaseDatabase.getInstance().getReference().child("units").child(buildingID).child(unitID).child("isPaid");
                    Log.w("===", "isPaid : "+isPaidFlag);
                    String flag = null;
                    if(isPaidFlag) flag = "1";
                    else flag = "0";
                    unitRef.setValue(flag)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w("===", "update isPaid : setValue succeed");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("===", "update isPaid : setValue failed ");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setData(Unit thisUnit){
        NumberFormat myFormatter = NumberFormat.getInstance(Locale.getDefault());

        unitNum = thisUnit.getUnitNum()+"호";
        buildingInfoTv.setText(unitNum);

        if(thisUnit.getIsPaid().equals("0")){
            paymentStatusTv.setTextColor(Color.RED);
            paymentStatusTv.setText("미납");
            payNowBtn.setVisibility(View.VISIBLE);
        }else{
            paymentStatusTv.setTextColor(Color.BLUE);
            paymentStatusTv.setText("완납");
            payNowBtn.setVisibility(View.GONE);
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
        totalFee = Integer.parseInt(thisUnit.getMonthlyFee().replace(",", "")) + Integer.parseInt(thisUnit.getMngFee().replace(",", ""));

        monthlyTotalFeeTv.setText(myFormatter.format(totalFee) + "원");
        monthlyFee1Tv.setText(myFormatter.format(totalFee) + "원");

        if(thisUnit.getLlBank() != null && thisUnit.getLlAccountNum() != null && thisUnit.getLlDepositor() != null){
            landlordAct.setBank(thisUnit.getLlBank());
            landlordAct.setAccountNum(thisUnit.getLlAccountNum());
            landlordAct.setDepositor(thisUnit.getLlDepositor());
        }
    }
}

class CreditViewAdapter extends RecyclerView.Adapter<CreditViewAdapter.ViewHolder> {

    private ArrayList<Credit> creditList;
    private DatabaseReference creditRef;

    CreditViewAdapter (ArrayList<Credit> creditList) {
        this.creditList = creditList;
        creditRef = FirebaseDatabase.getInstance().getReference("credit");
    }

    @NonNull
    @Override
    public CreditViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.recyclerview_credit_tenant, parent, false) ;
        CreditViewAdapter.ViewHolder myViewHolder = new CreditViewAdapter.ViewHolder(view) ;
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CreditViewAdapter.ViewHolder holder, final int position) {
        NumberFormat myFormatter = NumberFormat.getInstance(Locale.getDefault());
        holder.billingDateTv.setText(creditList.get(position).getBillingDate());

        if(creditList.get(position).getDepositDate() != null) {
            holder.depositDateTv.setText(creditList.get(position).getDepositDate());
            holder.depositDateTv.setVisibility(View.VISIBLE);
        }

        holder.payerTv.setText(creditList.get(position).getPayerName());
        int credit = Integer.parseInt(creditList.get(position).getCredit());
        holder.creditTv.setText(myFormatter.format(credit));
        String status = creditList.get(position).getStatus();
        holder.statusTv.setText(status);
        if(status.equals("완납")) {
            holder.statusTv.setTextColor(Color.parseColor("#007B38"));
        }else {
            holder.statusTv.setTextColor(Color.parseColor("#AA0000"));
        }
    }

    @Override
    public int getItemCount() {
        return creditList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView billingDateTv, depositDateTv, payerTv, creditTv, statusTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            billingDateTv = itemView.findViewById(R.id.tv_rv_credit_payDay_tenant);
            depositDateTv = itemView.findViewById(R.id.tv_rv_credit_depositDate_tenant);
            payerTv = itemView.findViewById(R.id.tv_rv_credit_payerName_tenant);
            creditTv = itemView.findViewById(R.id.tv_rv_credit_credit_tenant);
            statusTv = itemView.findViewById(R.id.tv_rv_credit_status_tenant);
        }
    }
}
