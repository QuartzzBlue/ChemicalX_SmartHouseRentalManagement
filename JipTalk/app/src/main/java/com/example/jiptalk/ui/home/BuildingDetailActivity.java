package com.example.jiptalk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.Unit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BuildingDetailActivity extends AppCompatActivity {

    String buildingKey;
    ArrayList<Unit> units;

    TextView buildingNameTv,expireCntTv,monthIncomeTv,unpaidCntTv,paidCntTv,occupiedCntTv,emptyCntTv,unitCntTv;
    Button addUnitBtn,editBtn;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    UnitViewAdapter unitViewAdapter;
    Context nowContext;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);

        initialization();

        addUnitBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(nowContext, AddUnitActivity.class);
                startActivity(intent);

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        // 리사이클러뷰의 아이템 클릭 시 호수의 UnitDetailActivity 로 이동
        unitViewAdapter.setOnItemClickListener(new UnitViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 액티비티 이동
                Intent intent = new Intent(getApplicationContext(), BuildingDetailActivity.class);
                intent.putExtra("UserID",units.get(position).getUserID());
                startActivity(intent);
            }
        });

    }

    /* AppBar 에 정보 버튼 추가 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_info, menu) ;
        return true ;
    }

    public void initialization(){
        buildingNameTv = findViewById(R.id.tv_building_detail_buildingName);
        monthIncomeTv = findViewById(R.id.tv_building_detail_monthIncome);
        unpaidCntTv = findViewById(R.id.tv_building_detail_unpaidCnt);
        paidCntTv = findViewById(R.id.tv_building_detail_paidCnt);
        occupiedCntTv = findViewById(R.id.tv_building_detail_occupiedCnt);
        emptyCntTv = findViewById(R.id.tv_building_detail_emptyCnt);
        unitCntTv = findViewById(R.id.tv_building_detail_unitCnt);
        expireCntTv = findViewById(R.id.tv_building_detail_expireCnt);
        addUnitBtn = findViewById(R.id.btn_building_detail_addUnit);
        editBtn=findViewById(R.id.btn_building_detail_edit);
        nowContext = this;

        initDatabase();

        recyclerView=findViewById(R.id.rv_building_detail);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        unitViewAdapter = new UnitViewAdapter(units);
        recyclerView.setAdapter(unitViewAdapter);

    }

    public void initDatabase(){

        units = new ArrayList<>();

        buildingKey = getIntent().getStringExtra("buildingKey");
        Log.d("===","buildingKey : "+buildingKey);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("buildings");

        //db 에서 호수 정보 불러오기




    }
}

class UnitViewAdapter extends RecyclerView.Adapter<UnitViewAdapter.MyViewHolder>{

    private ArrayList<Unit> units = null;
    private OnItemClickListener onItemClickListener=null;

    //아이템 뷰를 저장하는 뷰 홀더 클래스
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView unitNumTv,userIdTv,monthlyFeeTv,isPaid;
        ImageButton btn_unitDetail;

        //뷰 객체에 대한 참조
        MyViewHolder(final View itemView){
            super(itemView);
            //folow
            btn_unitDetail = itemView.findViewById(R.id.btn_recyclerview_buildingDetail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(onItemClickListener!=null){
                            onItemClickListener.onItemClick(v,pos);
                        }
                    }
                }
            });

            btn_unitDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(onItemClickListener!=null){
                            onItemClickListener.onItemClick(v,pos);
                        }
                    }
                }
            });
        }
    }

    //생성자에서 데이터 리스트 객체를 전달받음
    UnitViewAdapter(ArrayList<Unit> unitList){
        units = unitList;
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public UnitViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recyclerview_item_unit, parent, false) ;
        UnitViewAdapter.MyViewHolder myViewHolder = new UnitViewAdapter.MyViewHolder(view) ;
        return myViewHolder;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull UnitViewAdapter.MyViewHolder holder, int position) {
        String unitNum = units.get(position).getUnitNum();
        holder.unitNumTv.setText(unitNum);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return units.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }
}
