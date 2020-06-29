package com.example.jiptalk.ui.unit;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.Credit;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class Tab1Fragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CreditAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String thisUnitKey;
    private ArrayList<Credit> creditList;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        final View view = inflater.inflate(R.layout.item_unit_detail_tab1,null);
        thisUnitKey = getArguments().getString("thisUnitKey");

        DatabaseReference creditRef = FirebaseDatabase.getInstance().getReference("credit").child(thisUnitKey);

//        String tempkey = creditRef.push().getKey();
//        Credit credit = new Credit(thisUnitKey, tempkey, "2020.04.29", "이대휘", "300000", "미납");
//        creditRef.child(tempkey).setValue(credit);

        creditRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.w("===", "Tab1Fragment : onDataChange()");
                HashMap<String, Credit> creditMap = new HashMap<String, Credit>();
                for(DataSnapshot credits : dataSnapshot.getChildren()) {
                    Credit temp = credits.getValue(Credit.class);
                    creditMap.put(credits.getKey(), temp);
                }
                //map to arrayList
                Collection<Credit> values = creditMap.values();
                creditList = new ArrayList<Credit>(values);
                // 날짜 최신순으로 정렬
                Collections.sort(creditList, new Comparator<Credit>() {
                    @Override
                    public int compare(Credit o1, Credit o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
                Log.w("===", "Credit List : " + creditList.toString());

                //https://androidyongyong.tistory.com/5
                mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_tab1_creditList);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new CreditAdapter(creditList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return view;
    }

}

class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.ViewHolder> {

    private ArrayList<Credit> creditList;
    private DatabaseReference creditRef;

    CreditAdapter (ArrayList<Credit> creditList) {
        this.creditList = creditList;
        creditRef = FirebaseDatabase.getInstance().getReference("credit");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.recyclerview_credit, parent, false) ;
        CreditAdapter.ViewHolder myViewHolder = new CreditAdapter.ViewHolder(view) ;
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CreditAdapter.ViewHolder holder, final int position) {
        NumberFormat myFormatter = NumberFormat.getInstance(Locale.getDefault());

        holder.dateTv.setText(creditList.get(position).getDate());
        holder.payerTv.setText(creditList.get(position).getPayerName());
        int credit = Integer.parseInt(creditList.get(position).getCredit());
        holder.creditTv.setText(myFormatter.format(credit));
        String status = creditList.get(position).getStatus();
        holder.statusTv.setText(status);
        if(status.equals("완납")) {
            holder.statusTv.setTextColor(Color.parseColor("#007B38"));
            holder.statusSw.setChecked(true);
        }else {
            holder.statusTv.setTextColor(Color.parseColor("#AA0000"));
            holder.statusSw.setChecked(false);
        }

        holder.statusSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Credit temp = creditList.get(position);
                if(isChecked) {
                    Log.w("===", position +"th credit is confirmed");
                    temp.setStatus("완납");
                } else {
                    Log.w("===", position +"th credit is not confirmed");
                    temp.setStatus("미납");
                }
//                creditList.set(position, temp);
                updateCredit(temp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return creditList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTv, payerTv, creditTv, statusTv;
        Switch statusSw;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.tv_rv_credit_payDay);
            payerTv = itemView.findViewById(R.id.tv_rv_credit_payerName);
            creditTv = itemView.findViewById(R.id.tv_rv_credit_credit);
            statusTv = itemView.findViewById(R.id.tv_rv_credit_status);
            statusSw = itemView.findViewById(R.id.sw_rv_credit_status);
        }
    }

//    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            int position = (int) buttonView.getTag();
//
//            if (onItemCheckedListener != null) {
//                onItemCheckedListener.onCheckedChanged(position, isChecked);
//            }
//        }
//    };

    private void updateCredit (Credit credit) {
        creditRef.child(credit.getUnitID()).child(credit.getCreditID()).child("status")
                .setValue(credit.getStatus())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.w("===", "updateCredit : onSuccess");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("===", "updateCredit : onFailure / " + e.getStackTrace());
                    }
                });
    }

}