package com.example.jiptalk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.AppData;
import com.example.jiptalk.R;
import com.example.jiptalk.ui.building.BuildingDetailActivity;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.Unit;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    View root;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager; //어댑터에서 getView 역할을 하는것
    // (뷰홀더 지정. 뷰홀더 : 화면에 표시될 아이템 뷰를 저장하는 객체)
    MyRecyclerViewAdapter myRecycleViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        getData();
        initialization();

        return root;
    }



    private void getData() {

        getCurrentUser();
        getBuildingInfo(AppData.userUID);
        setToken();

    }

    public void getCurrentUser(){
        AppData.userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void getBuildingInfo(String userUID){


        FirebaseDatabase.getInstance().getReference("buildings").child(userUID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.v("===","getBuildingInfo executed");

                //buildingMap = (HashMap<String, Building>) dataSnapshot.getValue();
                AppData.buildings.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Building buildingItem = postSnapshot.getValue(Building.class);
                    buildingItem.setId(postSnapshot.getKey());

                    AppData.buildings.put(buildingItem.getId(),buildingItem);
                    AppData.unitsInBuildings.put(buildingItem.getId(),new HashMap<String, Unit>());

                    getUnitInfo(buildingItem.getId());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("===", "InitConstants() : onCancelled", databaseError.toException());
            }

        });
    }

    public void getUnitInfo(final String buildingUID){

        FirebaseDatabase.getInstance().getReference("units").child(buildingUID).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.v("===","getUnitInfo executed");

                // 유닛이 없는 경우에는 return
                if(dataSnapshot.getValue() == null) {
                    return;
                }

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Unit unitItem = postSnapshot.getValue(Unit.class);
                    unitItem.setUnitID(postSnapshot.getKey());

                    AppData.unitsInBuildings.get(buildingUID).put(unitItem.getUnitID(),unitItem);
                }

                paymentCnt();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void paymentCnt(){

        Log.v("===","paymentCnt executed");

        AppData.totalMonthlyIncome =0;
        AppData.totalUnitCnt =0;
        AppData.totalPaidCnt =0;

        //buildings
        for(String buildingKey : AppData.unitsInBuildings.keySet()){

            int occupiedCnt = 0;
            int paidCnt = 0;
            int monthIncome = 0;

            //units
            for(String unitKey : AppData.unitsInBuildings.get(buildingKey).keySet()){
                if(AppData.unitsInBuildings.get(buildingKey).get(unitKey).getIsPaid().equals("1")){
                    paidCnt++;
                }

                if(AppData.unitsInBuildings.get(buildingKey).get(unitKey).getIsOccupied().equals("1")){
                    occupiedCnt++;
                }
                monthIncome += Integer.parseInt(AppData.unitsInBuildings.get(buildingKey).get(unitKey).getMngFee())
                        +Integer.parseInt(AppData.unitsInBuildings.get(buildingKey).get(unitKey).getMonthlyFee());

            }
            if(AppData.buildings.get(buildingKey)!=null){
                AppData.buildings.get(buildingKey).setPaidCnt(paidCnt);
                AppData.buildings.get(buildingKey).setOccupiedCnt(occupiedCnt);
                AppData.buildings.get(buildingKey).setUnpaidCnt(occupiedCnt-paidCnt);
                AppData.buildings.get(buildingKey).setMonthlyIncome(monthIncome);

            }


            AppData.totalMonthlyIncome += monthIncome;
            AppData.totalUnitCnt += occupiedCnt;
            AppData.totalPaidCnt += paidCnt;
        }

        setData();
        setAdapter();

    }

    private void setData(){

        NumberFormat myFormatter = NumberFormat.getInstance(Locale.getDefault());
        ((TextView)root.findViewById(R.id.tv_home_totalMonthlyIncome)).setText(myFormatter.format(AppData.totalMonthlyIncome)+"원");
        ((TextView)root.findViewById(R.id.tv_home_paymentStatus)).setText(AppData.totalPaidCnt+"/"+AppData.totalUnitCnt);

    }

    public void initialization(){

        // Add New Building Btn
        root.findViewById(R.id.btn_home_addBuilding).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Move to AddBuildingActivity.class
                Intent intent = new Intent(getActivity(), AddBuildingActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = root.findViewById(R.id.rv_home);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

    }

    public void setAdapter(){
        final ArrayList<Building> buildingList = new ArrayList<>();
        buildingList.addAll(AppData.buildings.values());

        myRecycleViewAdapter = new MyRecyclerViewAdapter(buildingList);
        recyclerView.setAdapter(myRecycleViewAdapter);

        // 리사이클러뷰의 아이템 클릭 시 해당 아이템(빌딩) 의 BuildingDetailActivity 로 이동
        myRecycleViewAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 액티비티 이동
                Intent intent = new Intent(getActivity(), BuildingDetailActivity.class);
                //intent.putExtra("buildingInfo", buildingList.get(position));
                AppData.nowBuildingKey = buildingList.get(position).getId();
                startActivity(intent);
            }
        });
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

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>{

    private ArrayList<Building> buildings;
    private OnItemClickListener onItemClickListener=null;

    //아이템 뷰를 저장하는 뷰 홀더 클래스
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView buildingName;
        TextView isDelayed;
        ImageButton btn_buildingDetail;

        //뷰 객체에 대한 참조
        MyViewHolder(final View itemView){
            super(itemView);
            buildingName = itemView.findViewById(R.id.tv_rectclerview_buildingName);
            isDelayed = itemView.findViewById(R.id.tv_isDelayed);
            btn_buildingDetail = itemView.findViewById(R.id.btn_recyclerview_buildingDetail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(onItemClickListener!=null){
                            onItemClickListener.onItemClick(v,pos);
                        }
                    }
                }
            });

            btn_buildingDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(onItemClickListener!=null){
                            onItemClickListener.onItemClick(v,pos);
                        }
                    }
                }
            });
        }
    }

    //생성자에서 데이터 리스트 객체를 전달받음
    MyRecyclerViewAdapter(ArrayList<Building> buildinglist){
        buildings = buildinglist;
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public MyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recyclerview_item_building, parent, false) ;
        MyRecyclerViewAdapter.MyViewHolder myViewHolder = new MyRecyclerViewAdapter.MyViewHolder(view) ;
        return myViewHolder;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.MyViewHolder holder, int position) {

        String buildingName = buildings.get(position).getName();
        holder.buildingName.setText(buildingName);
        holder.isDelayed.setText("미납 " + buildings.get(position).getUnpaidCnt()+"");

        // 미납 개수가 0인경우 배경을 초록색으로 변환
        if(buildings.get(position).getUnpaidCnt() == 0) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.isDelayed.setBackground(ContextCompat.getDrawable(holder.isDelayed.getContext(), R.drawable.round_button_4));
            } else {
                holder.isDelayed.setBackgroundDrawable(ContextCompat.getDrawable(holder.isDelayed.getContext(), R.drawable.round_button_4));
            }
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }


}
