package com.example.jiptalk.tenant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.AppData;
import com.example.jiptalk.R;
import com.example.jiptalk.tenant.retrofit.RetrofitService;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.Credit;
import com.example.jiptalk.vo.KakaoPayResponse;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class THomeFragment extends Fragment {

    View root;
    Credit credit;
    Building building;
    Unit unit;
    User tenantUser,landlordUser;
    TextView paymentStatusTv;
    RecyclerView creditHistoryRv;
    Button payNowBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home_tenant, container, false);

        initialize();
        setToken();

        return root;
    }

    public void initialize(){

        paymentStatusTv = root.findViewById(R.id.tv_tenant_home_paymentStatus);
        payNowBtn = root.findViewById(R.id.btn_tenant_home_payNow);

        //지금 납부
        payNowBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                TenantPayDialog tenantPayDialog = new TenantPayDialog(getContext(), landlordUser, tenantUser,building,unit,credit);
//                tenantPayDialog.show(fm, "tenantPayFeeDialog show");

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://kapi.kakao.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                HashMap<String, Object> body = new HashMap<>();
                body.put("cid", "TC0ONETIME");
                body.put("partner_order_id", unit.getUnitNum());
                body.put("partner_user_id", unit.getUnitNum()+"호"+unit.getTenantName());
                body.put("item_name", "월세납부");
                body.put("quantity", 1);
                body.put("total_amount", Integer.parseInt(credit.getCredit()));
                body.put("tax_free_amount", 0);
                body.put("approval_url", "https://developers.kakao.com/success");
                body.put("fail_url", "https://developers.kakao.com/fail");
                body.put("cancel_url", "https://developers.kakao.com/cancel");

                HashMap<String, Object> header = new HashMap<>();
                String kakaoApiKey = getString(R.string.kakaopay_api_key);
                header.put("Authorization", "KakaoAK "+kakaoApiKey);
                header.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");


                RetrofitService retrofitService = retrofit.create(RetrofitService.class);

                Log.d("===", "retrofitService 객체생성");

                retrofitService.postRedirect(header,body).enqueue(new Callback<KakaoPayResponse>(){

                    @Override
                    public void onResponse(Call<KakaoPayResponse> call, Response<KakaoPayResponse> response) {
                        if(response.isSuccessful()) {
                            KakaoPayResponse data = response.body();
                            Log.d("===", "kakao페이 연동 성공");
                            Log.d("===", "app url : " + data.getRedirect_app_url());
                            Log.d("===", "app scheme : " + data.getAndroid_app_scheme());
                            Log.d("===", "mobile url : " + data.getRedirect_mobile_url());
                            Intent intent = new Intent(getContext(), WebViewActivity.class);
                            intent.putExtra("redirect_url", data.getRedirect_mobile_url());
                            intent.putExtra("scheme", data.getAndroid_app_scheme());
                            startActivity(intent);

                        }else{
                            Log.d("===", "response : "+ response.message() + " " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<KakaoPayResponse> call, Throwable t) {
                        t.printStackTrace();
                        Log.d("===", "kakao페이 연동 실패");
                    }
                });

            }
        });

        creditHistoryRv=root.findViewById(R.id.CreditHistoryRv);
        final TableLayout contactInfo = root.findViewById(R.id.tl_tenant_home);

        //납부 내역
        ((root.findViewById(R.id.CreditHistoryLayout))).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(creditHistoryRv.getVisibility()==View.GONE){
                    creditHistoryRv.setVisibility(View.VISIBLE);
                    ((root.findViewById(R.id.tenant_home_rvHeader))).setVisibility(View.VISIBLE);
                }else{
                    creditHistoryRv.setVisibility(View.GONE);
                    ((root.findViewById(R.id.tenant_home_rvHeader))).setVisibility(View.GONE);
                }
            }
        });

        //계약 정보
        ((root.findViewById(R.id.ContactInfoLayout))).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(contactInfo.getVisibility()==View.GONE){
                    contactInfo.setVisibility(View.VISIBLE);
                }else{
                    contactInfo.setVisibility(View.GONE);
                }
            }
        });

        getUserUid();

    }

    private void getUserUid(){

        AppData.userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getTenantUser();
    }

    public void getTenantUser(){

        FirebaseDatabase.getInstance().getReference().child("user").child(AppData.userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tenantUser = dataSnapshot.getValue(User.class);

                getLandlordUser();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getLandlordUser(){
        FirebaseDatabase.getInstance().getReference().child("user").child(tenantUser.getLandlordID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                landlordUser = dataSnapshot.getValue(User.class);
                getBuilding();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getBuilding(){
        FirebaseDatabase.getInstance().getReference().child("buildings").child(tenantUser.getLandlordID()).child(tenantUser.getBuildingID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                building = dataSnapshot.getValue(Building.class);
                getUnit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUnit(){

        FirebaseDatabase.getInstance().getReference().child("units").child(tenantUser.getBuildingID()).child(tenantUser.getUnitID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                unit = dataSnapshot.getValue(Unit.class);
                unit.setUnitID(dataSnapshot.getKey());

                setUnitInfo();
                setContractData();
                getCredit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //홈 상단부
    public void setUnitInfo(){

        ((TextView)root.findViewById(R.id.tv_tenant_home_buildingInfo)).setText(building.getName()+" "+unit.getUnitNum()+"호");
    }

    //계약 정보
    public void setContractData(){

        NumberFormat myFormatter = NumberFormat.getInstance(Locale.getDefault());

        //세입자명
        ((TextView)root.findViewById(R.id.tv_tenant_home_name)).setText(unit.getTenantName());
        //입금자명
        ((TextView)root.findViewById(R.id.tv_tenant_home_depositor)).setText(unit.getPayerName());
        //연락처
        ((TextView)root.findViewById(R.id.tv_tenant_home_phone)).setText(unit.getTenantPhone());
        //계약 형태
        ((TextView)root.findViewById(R.id.tv_tenant_home_leaseType)).setText(unit.getLeaseType());
        //계약기간
        ((TextView)root.findViewById(R.id.tv_tenant_home_contractPeriod)).setText(unit.getStartDate() + " ~ " + unit.getEndDate());
        //보증금
        ((TextView)root.findViewById(R.id.tv_tenant_home_deposit)).setText(myFormatter.format(Integer.parseInt(unit.getDeposit())) + "원");
        //입금일
        ((TextView)root.findViewById(R.id.tv_tenant_home_payDay)).setText("매월 " + unit.getPayDay() + "일");
        //월세
        ((TextView)root.findViewById(R.id.tv_tenant_home_monthlyFee)).setText(myFormatter.format(Integer.parseInt(unit.getMonthlyFee())) + "원");
        //관리비
        ((TextView)root.findViewById(R.id.tv_tenant_home_manageFee)).setText(myFormatter.format(Integer.parseInt(unit.getMngFee())) + "원");
        //월 납부 총액
        int totalFee = Integer.parseInt(unit.getMonthlyFee()) + Integer.parseInt(unit.getMngFee());
        ((TextView)root.findViewById(R.id.tv_tenant_home_monthlyTotalFee)).setText(myFormatter.format(totalFee) + "원");

    }

    public void getCredit(){

        FirebaseDatabase.getInstance().getReference("credit").child(unit.getUnitID()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Credit> unpaidCreditList = new ArrayList<>();

                Log.w("===", "getCreditHistoryData executed");

                HashMap<String, Credit> creditMap = new HashMap<String, Credit>();
                for(DataSnapshot credits : dataSnapshot.getChildren()) {
                    Credit credit = credits.getValue(Credit.class);
                    if(credit.getStatus().equals("미납")) {
                       unpaidCreditList.add(credit);
                    }
                    creditMap.put(credits.getKey(), credit);
                }
                //map to arrayList
                Collection<Credit> values = creditMap.values();
                ArrayList<Credit> creditList = new ArrayList<>(values);

                //모든 Credit List 날짜 최신순으로 정렬
                Collections.sort(creditList, new Comparator<Credit>() {
                    @Override
                    public int compare(Credit o1, Credit o2) {
                        return o2.getBillingDate().compareTo(o1.getBillingDate());
                    }
                });

                boolean isPaidFlag = true;

                //미납 내역이 있다면
                if(unpaidCreditList.size()!=0){

                    isPaidFlag=false;

                    //총 미납 요금 세기
                    getTotalCharge(unpaidCreditList);

                    // unPaidCreditList 날짜 오래된 순으로 정렬
                    unpaidCreditList.sort(new Comparator<Credit>(){

                        @Override
                        public int compare(Credit o1, Credit o2) {
                            return o1.getBillingDate().compareTo(o2.getBillingDate());
                        }
                    });

                    //제일 오래된 미납 내역
                    credit = unpaidCreditList.get(0);


                }

                updateIsPaid(building.getId(),unit.getUnitID(),isPaidFlag);

                TextView emptyView = root.findViewById(R.id.rv_tenant_home_emptyView);

                //청구 내역이 없다면
                if(creditList.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    creditHistoryRv.setVisibility(View.GONE);
                } else { //청구 내역이 있다면 RecyclerView 로 보여주기

                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    creditHistoryRv.setLayoutManager(mLayoutManager);
                    CreditViewAdapter creditAdapter = new CreditViewAdapter(creditList);
                    creditHistoryRv.setAdapter(creditAdapter);
                    creditHistoryRv.setItemAnimator(new DefaultItemAnimator());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getTotalCharge(ArrayList<Credit> unpaidCreditList){
        int totalCharge=0;

        for (Credit credit:unpaidCreditList) {
            totalCharge+=Integer.parseInt(credit.getCredit());
        }
        Log.v("===","totalCharge : "+totalCharge);
        setTotalCharge(totalCharge);
    }

    public void setTotalCharge(int totalCharge){
        Log.v("===","set totalCharge : "+totalCharge);
        int thisMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
        ((TextView)root.findViewById(R.id.tv_tenant_home_Month)).setText(thisMonth+"월 납부금");
        ((TextView)root.findViewById(R.id.tv_tenant_home_monthlyFee1)).setText(NumberFormat.getInstance().format(totalCharge) + "원");
    }

    public void updateIsPaid(String buildingID, String unitID, final boolean isPaidFlag){

        if(!isPaidFlag) {
            setPaymentStatus(isPaidFlag);
            Log.w("===", "is not Paid : "+isPaidFlag);
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("units").child(buildingID).child(unitID).child("isPaid").setValue("1")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setPaymentStatus(isPaidFlag);
                        Log.w("===", "is Paid : "+isPaidFlag);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("===", "update isPaid : setValue failed ");
                    }
                });
    }

    public void setPaymentStatus(boolean isPaidFlag){

        if(!isPaidFlag){
            paymentStatusTv.setTextColor(Color.RED);
            paymentStatusTv.setText("미납");
            payNowBtn.setVisibility(View.VISIBLE);

            return;
        }
        paymentStatusTv.setTextColor(Color.BLUE);
        paymentStatusTv.setText("완납");
        setTotalCharge(0);
        payNowBtn.setVisibility(View.GONE);

    }

    public void setToken(){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if (AppData.newToken != null) { // 새로운 토큰 생성할 시 DB에 token 업데이트 및 token static 변수에 값 저장.
            AppData.token = AppData.newToken;
            Map map = new HashMap();
            map.put("token", AppData.token);
            reference.child("user").child(AppData.userUID).updateChildren(map);
        }

        // DB로부터 값을 가져와서 token & category static 변수에 값 저장.
        reference.child("user").child(AppData.userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap hashMap = (HashMap) dataSnapshot.getValue();
                Object tokenFromDB = hashMap.get("token");
                if (tokenFromDB == null) { // DB에 token 값이 없는 경우, token 값을 다시 가져와 DB에 저장한다.
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String newToken = instanceIdResult.getToken();
                            Map map = new HashMap();
                            map.put("token", newToken);
                            reference.child("user").child(AppData.userUID).updateChildren(map);
                            AppData.token = newToken;
                            AppData.category = hashMap.get("category").toString();
                        }
                    });

                } else {
                    Log.d("===", "token0 : " + tokenFromDB);
                    AppData.token = tokenFromDB.toString();
                    AppData.category = hashMap.get("category").toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

class CreditViewAdapter extends RecyclerView.Adapter<CreditViewAdapter.ViewHolder> {

    private ArrayList<Credit> creditList;

    CreditViewAdapter (ArrayList<Credit> creditList) {
        this.creditList = creditList;
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
