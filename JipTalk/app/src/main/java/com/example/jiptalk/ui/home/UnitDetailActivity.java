package com.example.jiptalk.ui.home;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.jiptalk.Constant;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.Unit;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class UnitDetailActivity extends AppCompatActivity {

    private TextView unitNumTv;
    private TabLayout tabLayout;
    private TabItem tab1,tab2,tab3;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    private String unitKey,buildingKey;
    private String buildingName,unitNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_detail);

        initialization();

    }
    private void initialization(){
        unitNumTv = findViewById(R.id.tv_unit_detail_unitNum);
        tabLayout = findViewById(R.id.tl_unit_detail);

        viewPager = findViewById(R.id.vp_unit_detail);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myFragmentPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });

        getData();


    }

    public void setData(){
        unitNumTv.setText(buildingName+" "+unitNum + "호");
    }

    public void getData(){

        Unit unit =  Constant.buildings.get(Constant.nowBuildingKey).getUnits().get(Constant.nowUnitKey);
        buildingName = Constant.buildings.get(Constant.nowBuildingKey).getName();
        unitNum = unit.getUnitNum();

        setData();

    }

    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{

        public MyFragmentPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0 : return new Tab1Fragment();
                case 1 : return new Tab2Fragment();
                case 2 : return new Tab3Fragment();
                default : return null;
            }

        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
