package com.example.jiptalk.ui.building;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.jiptalk.R;

public class BuildingFragment extends Fragment {

    private BuildingViewModel buildingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        buildingViewModel =
                ViewModelProviders.of(this).get(BuildingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_building, container, false);
        final TextView textView = root.findViewById(R.id.text_building);
        buildingViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}