package com.example.jiptalk.ui.setting;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jiptalk.R;

public class SettingFragment extends Fragment {

    private ViewGroup viewGroup;
    private SettingViewModel mViewModel;
    private Button alarmBt, infoBt, developersBt;
    private Intent intent;
    private Context nowContext;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    //프래그먼트와 관련되는 뷰 계층을 만들어서 리턴함
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
        initialization();

        alarmBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                intent = new Intent(nowContext, SettingAlarmActivity.class);
                startActivity(intent);
            }
        });

        infoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(nowContext, SettingUserInfoActivity.class);
                startActivity(intent);
            }
        });

        developersBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent = new Intent(nowContext, SettingAlarmActivity.class);
//                startActivity(intent);


            }
        });

        return viewGroup;
    }

    //프래그먼트와 연결된 액티비티가 onCreate() 작업을ㄹ 완료했을 때 호출됨
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        // TODO: Use the ViewModel

    }

    private void initialization() {
        alarmBt = viewGroup.findViewById(R.id.bt_setting_alarm);
        infoBt = viewGroup.findViewById(R.id.bt_setting_info);
        developersBt = viewGroup.findViewById(R.id.bt_setting_developers);
        nowContext = getContext();
    }

}
