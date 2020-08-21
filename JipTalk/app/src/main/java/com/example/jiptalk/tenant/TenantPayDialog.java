package com.example.jiptalk.tenant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.jiptalk.R;
import com.example.jiptalk.tenant.retrofit.RetrofitService;
import com.example.jiptalk.ui.message.PushFCMMessageThread;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.Credit;
import com.example.jiptalk.vo.KakaoPayResponse;
import com.example.jiptalk.vo.Unit;
import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TenantPayDialog extends DialogFragment {
    private int REQUEST_TEST = 0;
    private int RESULT_OK = 1;
    private Context context;
    User landlord,tenant;
    Building building;
    Unit unit;
    Credit credit;

    public TenantPayDialog(Context context, User landlord, User tenant, Building building, Unit unit, Credit credit) {
        this.context = context;
        this.landlord = landlord;
        this.tenant = tenant;
        this.building = building;
        this.unit = unit;
        this.credit = credit;
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    private void initialize() {

        ((TextView)getDialog().findViewById(R.id.tv_dialog_tenant_billingDate)).setText(credit.getBillingDate());

        if(landlord != null){
            ((TextView)getDialog().findViewById(R.id.tv_dialog_tenant_landlordAccount)).setText(landlord.getBank() + " " + landlord.getAccountNum() + " " + landlord.getDepositor());
        }
        if(tenant!=null){
            ((TextView)getDialog().findViewById(R.id.tv_dialog_tenant_tenantAccount)).setText(tenant.getBank() + " " + tenant.getAccountNum() + " " + tenant.getDepositor());
        }

        ((TextView)getDialog().findViewById(R.id.tv_dialog_tenant_amount)).setText(NumberFormat.getInstance(Locale.getDefault()).format(Integer.parseInt(credit.getCredit())) + " 원");
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tenant_monthly_fee, null);

        builder.setView(view)
                .setTitle("알림")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        Log.d("===", "click positive button");

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://kapi.kakao.com")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        Log.d("===", "Retrofit 객체생성");

                        HashMap<String, Object> body = new HashMap<>();
                        body.put("cid", "TC0ONETIME");
                        body.put("partner_order_id", unit.getUnitNum());
                        body.put("partner_user_id", unit.getUnitNum()+"호"+unit.getTenantName());
                        body.put("item_name", "월세납부");
                        body.put("quantity", 1);
                        body.put("total_amount", Integer.parseInt(credit.getCredit()));
                        body.put("tax_free_amount", 0);
                        body.put("approval_url", "https://github.com/QuartzzBlue");
                        body.put("fail_url", "https://developers.kakao.com");
                        body.put("cancel_url", "https://developers.kakao.com/cancel");

                        HashMap<String, Object> header = new HashMap<>();
                        String kakaoApiKey = getString(R.string.kakaopay_api_key);
                        header.put("Authorization", "KakaoAK "+kakaoApiKey);
                        header.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                        Log.d("===", "header, body 객체생성");

                        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

                        Log.d("===", "retrofitService 객체생성");

                        retrofitService.postRedirect(header,body).enqueue(new Callback<KakaoPayResponse>(){

                            @Override
                            public void onResponse(Call<KakaoPayResponse> call, Response<KakaoPayResponse> response) {
                                if(response.isSuccessful()) {
                                    KakaoPayResponse data = response.body();
                                    Log.d("===", "kakao페이 연동 성공");
                                    Log.d("===", data.getRedirect_mobile_url());

                                    Intent intent = new Intent(context, WebViewActivity.class);
                                    intent.putExtra("redirect_url", data.getRedirect_mobile_url());
                                    startActivityForResult(intent, REQUEST_TEST);

                                }
                            }

                            @Override
                            public void onFailure(Call<KakaoPayResponse> call, Throwable t) {
                                t.printStackTrace();
                                Log.d("===", "kakao페이 연동 실패");
                            }
                        });


                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });




        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEST) {
            if (resultCode == RESULT_OK) {
                updateCredit();
                Toast.makeText(context, "Result: " + data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
            } else {   // RESULT_CANCEL
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
//        } else if (requestCode == REQUEST_ANOTHER) {
//            ...
        }
    }

    private void updateCredit(){

        if(credit!=null){
            Calendar calendar = Calendar.getInstance();
            String thisDate = calendar.get(Calendar.YEAR)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.DAY_OF_MONTH);
            credit.setStatus("완납");
            credit.setDepositDate(thisDate);
            credit.setPayerName(unit.getPayerName());
            FirebaseDatabase.getInstance().getReference().child("credit").child(credit.getUnitID()).child(credit.getCreditID()).setValue(credit)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sendFCM();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("===", "updateCredit : onFailure / " + e.getStackTrace()); }
                    });
        }

    }

    private void sendFCM(){

        //집주인 토큰 가져오기
        FirebaseDatabase.getInstance().getReference("user").child(tenant.getLandlordID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User landlord = dataSnapshot.getValue(User.class);
                String landlordToken = landlord.getToken();

                Log.v("===","landlord token : "+landlordToken);
                if(landlordToken!=null){

                    //credit.getBillingDate().split(".")[0]+"년"+credit.getBillingDate().split(".")[1]+"월 월세 "

                    PushFCMMessageThread pushFCMMessageThread = new PushFCMMessageThread(landlordToken,building.getName()+" "+unit.getUnitNum()+"호", NumberFormat.getInstance(Locale.getDefault()).format(Integer.parseInt(credit.getCredit()))+" 원 입금 되었습니다!");
                    new Thread(pushFCMMessageThread).start();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

