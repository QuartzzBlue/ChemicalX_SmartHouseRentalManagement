package com.example.jiptalk.ui.setting;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.jiptalk.LoginActivity;
import com.example.jiptalk.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {

    private ViewGroup viewGroup;
    private SettingViewModel mViewModel;
    private Button alarmBt, infoBt, logoutBt;
    private Intent logoutIntent;
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
                logoutIntent = new Intent(nowContext, SettingAlarmActivity.class);
                startActivity(logoutIntent);
            }
        });

        infoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutIntent = new Intent(nowContext, SettingUserInfoActivity.class);
                startActivity(logoutIntent);
            }
        });


        logoutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 로그 아웃 **/
                FirebaseAuth.getInstance().signOut();

                // 자동로그인 초기화
                SharedPreferences appData = getActivity().getSharedPreferences("appData",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = appData.edit();
                editor.clear();
                editor.commit();

                logoutIntent = new Intent(nowContext, LoginActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //중간 스택에 쌓인 액티비티들 제거
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //액티비티가 스택 맨 위에 실행중이라면 재사용
                startActivity(logoutIntent);
                Toast.makeText(nowContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        return viewGroup;
    }

    //프래그먼트와 연결된 액티비티가 onCreate() 작업을 완료했을 때 호출됨
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initialization() {
        alarmBt = viewGroup.findViewById(R.id.bt_setting_alarm);
        infoBt = viewGroup.findViewById(R.id.bt_setting_info);
        logoutBt = viewGroup.findViewById(R.id.bt_setting_logout);
        nowContext = getContext();
    }


}
