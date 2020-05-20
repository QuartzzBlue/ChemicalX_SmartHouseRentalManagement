package com.example.jiptalk.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.jiptalk.R;
import com.google.android.material.tabs.TabLayout;

import me.relex.circleindicator.CircleIndicator;

import static androidx.core.content.ContextCompat.getSystemService;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FragmentPagerAdapter adapterViewPager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ViewPager viewPager = (ViewPager) root.findViewById(R.id.viewPager);
        adapterViewPager = new MyPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        CircleIndicator indicator = (CircleIndicator) root.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);


        return root;
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

