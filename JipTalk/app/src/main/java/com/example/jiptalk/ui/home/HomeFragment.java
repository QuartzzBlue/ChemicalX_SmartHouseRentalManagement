package com.example.jiptalk.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.jiptalk.R;

import java.sql.Timestamp;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FragmentPagerAdapter adapterViewPager;
    ArrayList<Building> buildings;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Hightlight Charts
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

        return root;
    }

    public void InitializePaymentStatus(){
        buildings = new ArrayList<Building>();

        //데이터베이스에서 내가 가진 모든 건물 불러오기.
        buildings.add(new Building("진성원룸"));
        buildings.add(new Building("해피하우스"));
        buildings.add(new Building("로체팰리스"));

        //db 에서 내가 가진 모든 건물 당 납부 내역 최신순 3개 add
       for(int i=0;i<buildings.size();i++){
           buildings.get(i).paymentStatuses.add(new PaymentStatus("조현민","전세",14,0,8,"미납",3, Timestamp.valueOf("2020-4-24 11:31:05")));
           buildings.get(i).paymentStatuses.add(new PaymentStatus("최여진","월세",16,48,8,"완납",0, Timestamp.valueOf("2020-4-28 16:21:05")));
           buildings.get(i).paymentStatuses.add(new PaymentStatus("이슬","전세",21,48,8,"연체",53, Timestamp.valueOf("2020-5-15 19:51:05")));
       }
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

class MyPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;

    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return Chart1Fragment.newInstance(0, "Page # 1");
            case 1:
                return Chart2Fragment.newInstance(1, "Page # 2");
            case 2:
                return Chart3Fragment.newInstance(2, "Page # 3");
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
}

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

        TextView buildingName = view.findViewById(R.id.buildingName);
        TextView name1 = view.findViewById(R.id.name1);
        TextView detail1 = view.findViewById(R.id.detail1);
        TextView type1 = view.findViewById(R.id.type1);
        TextView status1 = view.findViewById(R.id.status1);

        TextView name2 = view.findViewById(R.id.name2);
        TextView detail2 = view.findViewById(R.id.detail2);
        TextView type2 = view.findViewById(R.id.type2);
        TextView status2 = view.findViewById(R.id.status2);

        TextView name3 = view.findViewById(R.id.name3);
        TextView detail3 = view.findViewById(R.id.detail3);
        TextView type3 = view.findViewById(R.id.type3);
        TextView status3 = view.findViewById(R.id.status3);

        buildingName.setText(buildings.get(position).getName());

        //여기 어떻게 간단하게 할 수 있을까?
        ArrayList<PaymentStatus> paymentList;
        paymentList=buildings.get(position).getPaymentStatuses();

        name1.setText(paymentList.get(0).getTenantName());
        type1.setText(paymentList.get(0).getRentType());
        if(paymentList.get(0).getRentType().equals("전세")){
            detail1.setText("매달 "+Integer.toString(paymentList.get(0).getPayDay())+"일 " + "관리비 "+Integer.toString(paymentList.get(0).getMngFee())+"만원");
        }else{
            detail1.setText("매달 "+Integer.toString(paymentList.get(0).getPayDay())+"일 "
                    + "월세 "+Integer.toString(paymentList.get(0).getRentFee())+"만원 / " +"관리비 "+Integer.toString(paymentList.get(0).getMngFee())+"만원");
        }
        if(paymentList.get(0).getStatus().equals("완납")){
            status1.setTextColor(Color.parseColor("#009C64"));
            status1.setText(paymentList.get(0).getStatus());
        }else if (paymentList.get(0).getStatus().equals("미납")) {
            status1.setTextColor(Color.parseColor("#EE9E02"));
            status1.setText("+"+Integer.toString(paymentList.get(0).getDelay())+"일 "+paymentList.get(0).getStatus());
        }else if (paymentList.get(0).getStatus().equals("연체")){
            status1.setTextColor(Color.parseColor("#EE0202"));
            status1.setText("+"+Integer.toString(paymentList.get(0).getDelay())+"일 "+paymentList.get(0).getStatus());
        }

        //
        name2.setText(paymentList.get(1).getTenantName());
        type2.setText(paymentList.get(1).getRentType());
        if(paymentList.get(1).getRentType().equals("전세")){
            detail2.setText("매달 "+Integer.toString(paymentList.get(1).getPayDay())+"일 " + "관리비 "+Integer.toString(paymentList.get(1).getMngFee())+"만원");
        }else{
            detail2.setText("매달 "+Integer.toString(paymentList.get(1).getPayDay())+"일 "
                    + "월세 "+Integer.toString(paymentList.get(1).getRentFee())+"만원 / " +"관리비 "+Integer.toString(paymentList.get(1).getMngFee())+"만원");
        }
        if(paymentList.get(1).getStatus().equals("완납")){
            status2.setTextColor(Color.parseColor("#009C64"));
            status2.setText(paymentList.get(1).getStatus());
        }else if (paymentList.get(1).getStatus().equals("미납")) {
            status2.setTextColor(Color.parseColor("#EE9E02"));
            status2.setText("+"+Integer.toString(paymentList.get(1).getDelay())+"일 "+paymentList.get(1).getStatus());
        }else if (paymentList.get(1).getStatus().equals("연체")){
            status2.setTextColor(Color.parseColor("#EE0202"));
            status2.setText("+"+Integer.toString(paymentList.get(1).getDelay())+"일 "+paymentList.get(1).getStatus());
        }
        //
        name3.setText(paymentList.get(2).getTenantName());
        type3.setText(paymentList.get(2).getRentType());
        if(paymentList.get(2).getRentType().equals("전세")){
            detail3.setText("매달 "+Integer.toString(paymentList.get(2).getPayDay())+"일 " + "관리비 "+Integer.toString(paymentList.get(2).getMngFee())+"만원");
        }else{
            detail3.setText("매달 "+Integer.toString(paymentList.get(2).getPayDay())+"일 "
                    + "월세 "+Integer.toString(paymentList.get(2).getRentFee())+"만원 / " +"관리비 "+Integer.toString(paymentList.get(2).getMngFee())+"만원");
        }
        if(paymentList.get(2).getStatus().equals("완납")){
            status3.setTextColor(Color.parseColor("#009C64"));
            status3.setText(paymentList.get(2).getStatus());
        }else if (paymentList.get(2).getStatus().equals("미납")) {
            status3.setTextColor(Color.parseColor("#EE9E02"));
            status3.setText("+"+Integer.toString(paymentList.get(2).getDelay())+"일 "+paymentList.get(2).getStatus());
        }else if (paymentList.get(2).getStatus().equals("연체")){
            status3.setTextColor(Color.parseColor("#EE0202"));
            status3.setText("+"+Integer.toString(paymentList.get(2).getDelay())+"일 "+paymentList.get(2).getStatus());
        }

        return view;
    }
}

