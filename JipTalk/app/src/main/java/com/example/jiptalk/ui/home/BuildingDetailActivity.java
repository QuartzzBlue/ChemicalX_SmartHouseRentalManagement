package com.example.jiptalk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.Constant;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.Unit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BuildingDetailActivity extends AppCompatActivity {

    int expireCnt,monthIncome,paidCnt,unpaidCnt,occupiedCnt,emptyCnt,unitCnt;
    TextView buildingNameTv,expireCntTv,monthIncomeTv,unpaidCntTv,paidCntTv,occupiedCntTv,emptyCntTv,unitCntTv, emptyView;
    Button addUnitBtn,editBtn;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    UnitViewAdapter unitViewAdapter;
    Context nowContext;

    private ArrayList<Unit> unitList;
    private String thisBuildingKey, buildingName;
    private Building thisBuilding;
    private DatabaseReference dbRef;
    public NumberFormat myFormatter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);

        initialization();


        addUnitBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nowContext, AddUnitActivity.class);
                intent.putExtra("thisBuildingKey", thisBuildingKey);
                startActivity(intent);

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

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
        Intent intent = getIntent();
        thisBuilding = (Building) intent.getSerializableExtra("buildingInfo");
        thisBuildingKey = thisBuilding.getId();
        buildingName = thisBuilding.getName();
        unitCnt = thisBuilding.getUnitCnt();
        unitList = new ArrayList<>();

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
        recyclerView = findViewById(R.id.rv_building_detail);
        emptyView = findViewById(R.id.tv_building_detail_emptyView);
        dbRef = FirebaseDatabase.getInstance().getReference("units/"+ thisBuildingKey);
        myFormatter = NumberFormat.getInstance(Locale.getDefault());
        nowContext = this;

        getData();


    }

    public void setAdapter(){

        recyclerView=findViewById(R.id.rv_building_detail);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        unitViewAdapter = new UnitViewAdapter(unitList);
        recyclerView.setAdapter(unitViewAdapter);

        // 리사이클러뷰의 아이템 클릭 시 호수의 UnitDetailActivity 로 이동
        unitViewAdapter.setOnItemClickListener(new UnitViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 액티비티 이동
                Intent intent = new Intent(getApplicationContext(), UnitDetailActivity.class);
                intent.putExtra("thisBuildingKey", thisBuildingKey);
                intent.putExtra("thisBuildingName", thisBuilding.getName());
                intent.putExtra("thisUnit", unitList.get(position));
                startActivity(intent);
            }
        });

    }

    public void setData(){

        buildingNameTv.setText(buildingName+"");
        monthIncomeTv.setText(monthIncome+"");
        unpaidCntTv.setText(unpaidCnt+"");
        paidCntTv.setText(paidCnt+"");
        occupiedCntTv.setText(occupiedCnt+"");
        expireCntTv.setText(expireCnt+"");
        emptyCntTv.setText(emptyCnt+"");
        unitCntTv.setText(unitCnt+"");

    }

    public void getData(){

        dbRef.addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("===", "BuildingDetail/getData : onDataChange");
                HashMap<String,Unit> unitMap = (HashMap<String,Unit>) dataSnapshot.getValue();

                // 유닛이 없는 경우에는 return
                if(unitMap == null) {
                    setData();
                    Log.w("===", "BuildingDetailActivity : no Units");
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }

                for(DataSnapshot unitSnapshot : dataSnapshot.getChildren()) {
                    Unit unitItem = unitSnapshot.getValue(Unit.class);
                    unitItem.setUnitID(unitSnapshot.getKey());

                    Log.w("===", "unitItem : " + unitItem.toString());

                    //입금 확인 function 만들어 getIsPaid 를 0 또는 1 로 바꿔주기
                    if(unitItem.getIsPaid().equals("1")){

                        monthIncome += Integer.parseInt(unitItem.getMonthlyFee())+Integer.parseInt(unitItem.getMngFee());
                        paidCnt++;

                    }else if (unitItem.getIsPaid().equals("0")){
                        unpaidCnt++;
                    }

                    //임대중
                    if(unitItem.getIsOccupied().equals("1")){
                        occupiedCnt++;
                    }

                    //계약 만료 예정 cnt. 2달 이내
                    Calendar endCalendar = Calendar.getInstance();
                    Calendar todayCalendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
                    try {
                        Date endDate = simpleDateFormat.parse(unitItem.getEndDate());
                        endCalendar.setTime(endDate);

                        //계약기간 일수
                        long expireDay = (endCalendar.getTimeInMillis() - todayCalendar.getTimeInMillis())/1000;
                        expireDay /= 86400; //하루는 86400초

                        if(expireDay<60){
                            expireCnt++;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    unitList.add(unitItem);
                }

                Log.w("===", "unitList : " + unitList.toString());

                emptyCnt = unitCnt-occupiedCnt;

                setData();
                setAdapter();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("===", "getPersonalInfoThread() : onCancelled", databaseError.toException());
            }
        });

    }
}

class UnitViewAdapter extends RecyclerView.Adapter<UnitViewAdapter.MyViewHolder>{

    private ArrayList<Unit> units;
    private OnItemClickListener onItemClickListener=null;

    //아이템 뷰를 저장하는 뷰 홀더 클래스
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView unitNumTv,userNameTv,monthlyFeeTv,isPaidTv,startDateTv,endDateTv;
        ProgressBar progressBar;
        ImageButton btn_unitDetail;

        //뷰 객체에 대한 참조
        MyViewHolder(final View itemView){
            super(itemView);

            btn_unitDetail = itemView.findViewById(R.id.btn_recyclerview_buildingDetail);
            unitNumTv = itemView.findViewById(R.id.tv_rv_item_unitNum);
            userNameTv = itemView.findViewById(R.id.tv_rv_unit_userName);
            monthlyFeeTv = itemView.findViewById(R.id.tv_rv_unit_monthlyFee);
            isPaidTv = itemView.findViewById(R.id.tv_rv_unit_isPaid);
            startDateTv = itemView.findViewById(R.id.tv_rv_unit_startDate);
            endDateTv = itemView.findViewById(R.id.tv_rv_unit_endDate);
            progressBar = itemView.findViewById(R.id.pb_rv_unit);

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
        holder.userNameTv.setText(units.get(position).getTenantName());
        int totalFee = Integer.parseInt(units.get(position).getMonthlyFee().replace(",", ""))+Integer.parseInt(units.get(position).getMngFee().replace(",", ""));
//        holder.monthlyFeeTv.setText("월 "+ myFormatter.format(totalFee) +" 원");
        holder.monthlyFeeTv.setText("월 "+ totalFee +" 원");
        holder.startDateTv.setText(units.get(position).getStartDate());
        holder.endDateTv.setText(units.get(position).getEndDate());
        if(units.get(position).getIsPaid().equals("-1")){
            holder.isPaidTv.setText("미납");
            holder.isPaidTv.setTextColor(Color.RED);
        }else{
            holder.isPaidTv.setText("완납");
            holder.isPaidTv.setTextColor(Color.BLUE);
        }
        holder.startDateTv.setText(units.get(position).getStartDate());
        holder.endDateTv.setText(units.get(position).getEndDate());

        /* 날짜 프로그래스바 세팅 */
        Date startDate, endDate;
        //날짜 계산
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        Calendar todayCalendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        try {
            startDate = simpleDateFormat.parse(units.get(position).getStartDate());
            endDate = simpleDateFormat.parse(units.get(position).getEndDate());
            startCalendar.setTime(startDate);
            endCalendar.setTime(endDate);

            //계약기간 일수
            long time = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis())/1000;
            time = time/86400; //하루는 86400초

            //오늘까지 산 날짜
            long stayTime = (todayCalendar.getTimeInMillis() - startCalendar.getTimeInMillis())/1000;
            stayTime = stayTime/86400;

            double rate = (double)stayTime/time*100;
            holder.progressBar.setProgress((int)rate);

        } catch (ParseException e) {
            e.printStackTrace();
        }



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

