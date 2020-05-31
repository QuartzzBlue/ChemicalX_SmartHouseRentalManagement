package com.example.jiptalk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.Building;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Button buildingAddBtn;
    View root;

    ArrayList<Building> buildings;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager; //어댑터에서 getView 역할을 하는것
    // (뷰홀더 지정. 뷰홀더 : 화면에 표시될 아이템 뷰를 저장하는 객체)

    Map<String,Object> building = new HashMap<>();
    Context nowContext;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        Initialize();

        buildingAddBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 액티비티 이동
                Intent intent = new Intent(getActivity(), AddBuildingActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    public void Initialize(){

        InitDatabase();

        buildingAddBtn = root.findViewById(R.id.btn_home_buildingAdd);
        buildings = new ArrayList<>();
        buildings.add(new Building("진성원룸","경기도 용인시",16));
        buildings.add(new Building("해피하우스","경기도 용인시",16));
        buildings.add(new Building("로체팰리스","경기도 용인시",16));

        recyclerView = root.findViewById(R.id.home_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        MyRecyclerViewAdapter myRecycleViewAdapter = new MyRecyclerViewAdapter(buildings);
        myRecycleViewAdapter.setOnItemClickListeer(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 액티비티 이동
                Intent intent = new Intent(getActivity(), BuildingDetailActivity.class);
                intent.putExtra("buildingName",buildings.get(position).getName());
                startActivity(intent); //intent 로 선택한 빌딩 정보 값 넘겨줘야한다
            }
        });
        recyclerView.setAdapter(myRecycleViewAdapter);

    }

    public void InitDatabase(){

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("building");

    }
}

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>{

    private ArrayList<Building> buildings = null;
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

        View view = inflater.inflate(R.layout.recyclerview_item, parent, false) ;
        MyRecyclerViewAdapter.MyViewHolder myViewHolder = new MyRecyclerViewAdapter.MyViewHolder(view) ;
        return myViewHolder;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.MyViewHolder holder, int position) {
        String buildingName = buildings.get(position).getName();
        holder.buildingName.setText(buildingName);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListeer(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }
}

//class MyPagerAdapter extends FragmentPagerAdapter {
//    private static int NUM_ITEMS = 3;
//
//    public MyPagerAdapter(FragmentManager fragmentManager) {
//        super(fragmentManager);
//    }
//
//    // Returns total number of pages
//    @Override
//    public int getCount() {
//        return NUM_ITEMS;
//    }
//
//    // Returns the fragment to display for that page
//    @Override
//    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//
//                return Chart1Fragment.newInstance(0, "Page # 1");
//            case 1:
//                return Chart2Fragment.newInstance(1, "Page # 2");
//            case 2:
//                return Chart3Fragment.newInstance(2, "Page # 3");
//            default:
//                return null;
//        }
//    }
//
//    // Returns the page title for the top indicator
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return "Page " + position;
//    }
//}

