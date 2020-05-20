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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.jiptalk.R;

import static androidx.core.content.ContextCompat.getSystemService;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ViewPager2 viewPager;
    private TextViewPagerAdapter pagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = (ViewPager2)root.findViewById(R.id.viewPager);
        pagerAdapter = new TextViewPagerAdapte ;
        viewPager.setAdapter(pagerAdapter) ;

        return root;
    }


}

 class TextViewPagerAdapter extends PagerAdapter{

     // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
     private Context mContext = null ;

     public TextViewPagerAdapter() {

     }

     // Context를 전달받아 mContext에 저장하는 생성자 추가.
     public TextViewPagerAdapter(Context context) {
         mContext = context ;
     }


     @Override
    public Object instantiateItem(ViewGroup container, int position){
        View view = null ;

        if (mContext != null) {
            // LayoutInflater를 통해 "/res/layout/page.xml"을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.highlight_chart1, container, false);

            TextView textView = (TextView) view.findViewById(R.id.title) ;
            textView.setText("TEXT " + position) ;
        }

        // 뷰페이저에 추가.
        container.addView(view) ;

        return view ;
    }
     @Override
     public void destroyItem(ViewGroup container, int position, Object object) {
         // 뷰페이저에서 삭제.
         container.removeView((View) object);
     }

     @Override
     public int getCount() {
         // 전체 페이지 수는 10개로 고정.
         return 10;
     }

     @Override
     public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
         return (view == (View)object);
     }
}