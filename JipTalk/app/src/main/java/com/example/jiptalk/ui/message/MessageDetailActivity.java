package com.example.jiptalk.ui.message;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jiptalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;



    private DatabaseReference databaseReference;
    private String currentUserID;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        recyclerView = findViewById(R.id.recyclerViewMsgDetail);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter();
//        recyclerView.scrollToPosition();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMsgDetail);

        // Set ActionBar Title to name of the Tenant
        getSupportActionBar().setTitle(getIntent().getStringExtra("name").toString());



        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();








    }

    public void loadMessages() {
        swipeRefreshLayout.setRefreshing(false);
    }
}
