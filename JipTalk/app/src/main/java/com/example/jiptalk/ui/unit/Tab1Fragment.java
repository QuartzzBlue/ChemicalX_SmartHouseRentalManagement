package com.example.jiptalk.ui.unit;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.Credit;

import java.util.ArrayList;

public class Tab1Fragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CreditAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.item_unit_detail_tab1,null);
        //https://androidyongyong.tistory.com/5
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_tab1_creditList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        출처: https://androidyongyong.tistory.com/5 [용용의 안드로이드 교실]
        //recycler view 구현..

        return view;
    }

}

class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.ViewHolder> {

    private ArrayList<Credit> creditList;

    CreditAdapter (ArrayList<Credit> creditList) {
        this.creditList = creditList;
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
        holder.dateTv.setText(creditList.get(position).getDate());
        holder.payerTv.setText(creditList.get(position).getPayerName());
        holder.creditTv.setText(creditList.get(position).getCredit());
        holder.statusTv.setText(creditList.get(position).getStatus());

        holder.statusSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Credit temp = creditList.get(position);
                if(isChecked) {
                    Log.w("===", position +"th credit is confirmed");
                    temp.setStatus("완납");
                } else {
                    Log.w("===", position +"th credit is not confirmed");
                    temp.setStatus("완납");
                }
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


}