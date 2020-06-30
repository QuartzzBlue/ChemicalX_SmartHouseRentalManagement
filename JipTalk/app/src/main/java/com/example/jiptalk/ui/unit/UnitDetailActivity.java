package com.example.jiptalk.ui.unit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.Unit;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;

public class UnitDetailActivity extends AppCompatActivity {

    private TextView unitNumTv;
    private TabLayout tabLayout;
    private TabItem tab1,tab2,tab3;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    private Unit thisUnit;
    private String thisBuildingKey, thisBuildingName, thisUnitKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_detail);

        initialize();

    }

    /* AppBar 에 Overflow 버튼 추가 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_unitdetail, menu) ;
        return true ;
    }

    //overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.unit_delete:
                FirebaseDatabase.getInstance().getReference().child("units").child(thisBuildingKey).child(thisUnitKey).removeValue();
                finish();

                return true;

            case R.id.unit_modify:
                Intent intent = new Intent(getApplicationContext(), ModUnitDetailActivity.class);
                intent.putExtra("thisUnit", thisUnit);
                intent.putExtra("thisBuildingKey", thisBuildingKey);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initialize() {
        thisUnit = (Unit) getIntent().getSerializableExtra("thisUnit");
        thisUnitKey = getIntent().getStringExtra("thisUnitKey");
        thisBuildingKey = getIntent().getStringExtra("thisBuildingKey");
        thisBuildingName = getIntent().getStringExtra("thisBuildingName");

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

        setData();


    }

    public void setData(){
        unitNumTv.setText(thisBuildingName+" "+thisUnit.getUnitNum() + "호");
    }

//    public void getData(){
//
//
//        unitNum = thisUnit.getUnitNum();
//
//        setData();
//
//    }

    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{

        public MyFragmentPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            switch(position){
                case 0 :
                    bundle.putString("thisUnitKey", thisUnitKey);
                    Tab1Fragment tab1 = new Tab1Fragment();
                    tab1.setArguments(bundle);
                    return tab1;
                case 1 :
                    bundle.putString("thisUnitKey", thisUnitKey);
                    bundle.putString("thisBuildingKey", thisBuildingKey);
//                    bundle.putSerializable("thisUnit", thisUnit);
                    Tab2Fragment tab2 = new Tab2Fragment();
                    tab2.setArguments(bundle);
                    return tab2;
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
