package com.example.jiptalk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.MainActivity;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.Building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FragmentPagerAdapter adapterViewPager;
    Button buildingAddBtn;

    ArrayList<Building> buildings;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager; //어댑터에서 getView 역할을 하는것
    // (뷰홀더 지정. 뷰홀더 : 화면에 표시될 아이템 뷰를 저장하는 객체)

    Map<String,Object> building = new HashMap<>();
    Context nowContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        buildingAddBtn = root.findViewById(R.id.btn_home_buildingAdd);

        InitializePaymentStatus();

        MyRecycleViewAdapter myRecycleViewAdapter = new MyRecycleViewAdapter(buildings);
        recyclerView.setAdapter(myRecycleViewAdapter);

        buildingAddBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 액티비티 이동
                Intent intent = new Intent(getActivity(), AddBuildingActivity.class);
                startActivity(intent);
            }
        });

        /*//Hightlight Charts
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.viewPager);
        adapterViewPager = new MyPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        CircleIndicator indicator = (CircleIndicator) root.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        //Pament Status Per Building

        InitializePaymentStatus();
        ListView listView = root.findViewById(R.id.listView);
        final MyListViewAdapter myListViewAdapter = new MyListViewAdapter(getContext().getApplicationContext(),buildings);
        listView.setAdapter(myListViewAdapter);
        listViewHeightSet(myListViewAdapter,listView);
*/
        return root;
    }

    public void InitializePaymentStatus(){
        buildings = new ArrayList<Building>();

        //데이터베이스에서 내가 가진 모든 건물 불러오기.


        //db 에서 내가 가진 모든 건물 당 납부 내역 최신순 3개 add

    }

    private static void listViewHeightSet(BaseAdapter listAdapter, ListView listView){
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++){
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}

class MyRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView buildingName;
        int position;

        MyViewHolder(@NonNull View view) {
            super(view);
            buildingName = view.findViewById(R.id.buildingName);
        }

        void onBind(){
            buildingName.setText("진성원룸");
           // changeVisibility(selectedItems.get(position));
            buildingName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.buildingName){

            }
        }
    }

    private ArrayList<Building> buildingArrayList;
    private SparseBooleanArray selectedItems = new SparseBooleanArray(); //item 의 클릭 상태를 저장할 array 객체
    private int prePosition = -1; //직전에 클릭됐던 item 의 position

    MyRecycleViewAdapter(ArrayList<Building> buildingArrayList){
        this.buildingArrayList=buildingArrayList;
    }

    //RecyclerView 의 행을 표시하는데 사용되는 레이아웃 xml 을 가져옴
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);

        return new MyViewHolder(v);
    }

    //RecyclerView 의 행에 보여질 view 설정
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;

       // myViewHolder.buildingName.setText(buildingArrayList.get(position).name);
    }

    //RecyclerVew 의 행 갯수 리턴
    @Override
    public int getItemCount() {
        return buildingArrayList.size();
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

class MyListViewAdapter extends BaseAdapter{

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Building> buildings;

    public MyListViewAdapter() {
    }

    public MyListViewAdapter(Context mContext, ArrayList<Building> buildings) {
        this.mContext = mContext;
        this.buildings = buildings;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return buildings.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = mLayoutInflater.inflate(R.layout.payment_status,null);

        return view;
    }
}

