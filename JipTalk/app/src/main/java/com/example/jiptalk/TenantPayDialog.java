package com.example.jiptalk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.jiptalk.ui.message.PushFCMMessageThread;
import com.example.jiptalk.vo.Account;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.Credit;
import com.example.jiptalk.vo.Unit;
import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TenantPayDialog extends DialogFragment {
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
                        updateCredit();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private void updateCredit(){

        if(credit!=null){
            Calendar calendar = Calendar.getInstance();
            String thisDate = calendar.get(Calendar.YEAR)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.DAY_OF_MONTH);
            credit.setStatus("완납");
            credit.setDepositDate(thisDate);
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

                    PushFCMMessageThread pushFCMMessageThread = new PushFCMMessageThread(landlordToken,building.getName()+" "+unit.getUnitNum()+"호", credit.getBillingDate().split(".")[0]+"년"+credit.getBillingDate().split(".")[1]+"월 월세 "+NumberFormat.getInstance(Locale.getDefault()).format(Integer.parseInt(credit.getCredit()))+" 원 입금 되었습니다!");
                    pushFCMMessageThread.run();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

