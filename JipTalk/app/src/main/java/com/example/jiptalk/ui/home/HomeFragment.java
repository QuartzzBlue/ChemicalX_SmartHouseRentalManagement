package com.example.jiptalk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.Constant;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.Building;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    String TAG = "===";

    private HomeViewModel homeViewModel;
    Button buildingAddBtn;
    View root;
    int totalPaidCnt,totalUnitCnt, totalExpireCnt,totalMonthlyIncome;
    TextView payStatusTv, expireCntTv,monthlyIncomeTv;

    ArrayList<Building> buildings;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager; //어댑터에서 getView 역할을 하는것
    // (뷰홀더 지정. 뷰홀더 : 화면에 표시될 아이템 뷰를 저장하는 객체)
    MyRecyclerViewAdapter myRecycleViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        initialization();
        setToken();
        getData();
        setData();


        // Add New Building Btn
        buildingAddBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Move to AddBuildingActivity.class
                Intent intent = new Intent(getActivity(), AddBuildingActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    public void initialization(){

        payStatusTv = root.findViewById(R.id.tv_home_paymentStatus);
        expireCntTv = root.findViewById(R.id.tv_home_expireCnt);
        monthlyIncomeTv = root.findViewById(R.id.tv_home_totalMonthlyIncome);

        buildings = new ArrayList<>();

        buildingAddBtn = root.findViewById(R.id.btn_home_addBuilding);

        recyclerView = root.findViewById(R.id.rv_home);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

    }

    public void setAdapter(){
        myRecycleViewAdapter = new MyRecyclerViewAdapter(buildings);
        recyclerView.setAdapter(myRecycleViewAdapter);

        // 리사이클러뷰의 아이템 클릭 시 해당 아이템(빌딩) 의 BuildingDetailActivity 로 이동
        myRecycleViewAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 액티비티 이동
                Intent intent = new Intent(getActivity(), BuildingDetailActivity.class);
                intent.putExtra("buildingKey",buildings.get(position).getId());
                //Constant.nowBuildingKey = buildings.get(position).getId();
                startActivity(intent);
            }
        });
    }


    public void setToken(){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if (Constant.newToken != null) { // 새로운 토큰 생성할 시 DB에 token 업데이트 및 token static 변수에 값 저장.
            Constant.token = Constant.newToken;
            Map map = new HashMap();
            map.put("token", Constant.token);
            reference.child("user").child(Constant.userUID).updateChildren(map);
        }

        // DB로부터 값을 가져와서 token & category static 변수에 값 저장.
        reference.child("user").child(Constant.userUID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            reference.child("user").child(Constant.userUID).updateChildren(map);
                            Constant.token = newToken;
                            Constant.category = hashMap.get("category").toString();
                        }
                    });
                } else {
                    Log.d(TAG, "token0 : " + tokenFromDB);
                    Constant.token = tokenFromDB.toString();
                    Constant.category = hashMap.get("category").toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setData(){
        expireCntTv.setText(totalExpireCnt +"");
        monthlyIncomeTv.setText(totalMonthlyIncome+"");
        payStatusTv.setText(totalPaidCnt+"/"+totalUnitCnt);
    }

    private void getData() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference buildingReference = firebaseDatabase.getReference("buildings");
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        buildingReference.child(fUser.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    //            Building building = dataSnapshot.getValue(Building.class);

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Building buildingItem = postSnapshot.getValue(Building.class);
                    buildingItem.setId(postSnapshot.getKey());

                    totalUnitCnt += buildingItem.getOccupiedCnt();
                    totalPaidCnt += buildingItem.getPaidCnt();
                    totalMonthlyIncome += buildingItem.getMonthlyIncome();
                    totalExpireCnt += buildingItem.getExpireCnt();

                    buildings.add(buildingItem);
                }

                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v(TAG, "InitConstants() : onCancelled", databaseError.toException());
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
        holder.isDelayed.setText(buildings.get(position).getUnpaidCnt()+"");
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
