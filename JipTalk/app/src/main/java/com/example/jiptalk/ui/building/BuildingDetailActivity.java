package com.example.jiptalk.ui.building;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.MainActivity;
import com.example.jiptalk.R;
import com.example.jiptalk.ui.unit.UnitDetailActivity;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.Unit;
import com.google.firebase.auth.FirebaseAuth;
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

public class BuildingDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    int paidCnt,unpaidCnt,occupiedCnt,unitCnt;
    boolean flag = false;
    TextView buildingNameTv,unpaidCntTv,paidCntTv,occupiedCntTv,unitCntTv, emptyView;
    Button addUnitBtn;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    UnitViewAdapter unitViewAdapter;
    Spinner spinner;
    Context nowContext;
    private ArrayList<Unit> unitList,paidUnitList,unpaidUnitList;
    private String thisBuildingKey, buildingName;
    private Building thisBuilding;
    private DatabaseReference dbRef;
    public NumberFormat myFormatter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);

        initialization();
        setSpinner();
    }

    /* AppBar 에 Overflow 버튼 추가 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_buildingdetail, menu) ;
        return true ;
    }

    //overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            //초기화
            case R.id.building_clear:
                FirebaseDatabase.getInstance().getReference().child("units").child(thisBuildingKey).removeValue();
                getData();
                return true;

            case R.id.building_delete:
                FirebaseDatabase.getInstance().getReference().child("buildings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(thisBuildingKey).removeValue();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                FirebaseDatabase.getInstance().getReference().child("units").child(thisBuildingKey).removeValue();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initialization(){
        Intent intent = getIntent();
        thisBuilding = (Building) intent.getSerializableExtra("buildingInfo");
        thisBuildingKey = thisBuilding.getId();
        buildingName = thisBuilding.getName();
        unitCnt = thisBuilding.getUnitCnt();
        unitList = new ArrayList<>();
        unpaidUnitList = new ArrayList<>();
        paidUnitList = new ArrayList<>();

        buildingNameTv = findViewById(R.id.tv_building_detail_buildingName);
        unpaidCntTv = findViewById(R.id.tv_building_detail_unpaidCnt);
        paidCntTv = findViewById(R.id.tv_building_detail_paidCnt);
        occupiedCntTv = findViewById(R.id.tv_building_detail_occupiedCnt);
        unitCntTv = findViewById(R.id.tv_building_detail_unitCnt);

        //buttons
        addUnitBtn = findViewById(R.id.btn_building_detail_addUnit);
        addUnitBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nowContext, AddUnitActivity.class);
                intent.putExtra("thisBuildingKey", thisBuildingKey);
                startActivityForResult(intent,100);
            }
        });

//        editBtn=findViewById(R.id.btn_building_detail_edit);
//        editBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                delBtn.setVisibility(View.VISIBLE);
//                completeBtn.setVisibility(View.VISIBLE);
//                editBtn.setVisibility(View.GONE);
//
//                for (int position=0; position<recyclerView.getChildCount(); position++){
//                    recyclerView.getChildAt(position).findViewById(R.id.cb_rv_unit_selectedItem).setVisibility(View.VISIBLE);
//                    recyclerView.getChildAt(position).findViewById(R.id.btn_recyclerview_buildingDetail).setVisibility(View.GONE);
////                    recyclerView.getChildAt(position).findViewById(R.id.cb_rv_unit_selectedItem).setTag(position);
//                }
//                unitViewAdapter.notifyDataSetChanged();
//            }
//        });

//        delBtn = findViewById(R.id.btn_building_detail_delete);
//        delBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        completeBtn = findViewById(R.id.btn_building_detail_complete);
//        completeBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                delBtn.setVisibility(View.GONE);
//                completeBtn.setVisibility(View.GONE);
//                editBtn.setVisibility(View.VISIBLE);
//                for (int position=0; position<recyclerView.getChildCount(); position++){
//                    recyclerView.getChildAt(position).findViewById(R.id.cb_rv_unit_selectedItem).setVisibility(View.GONE);
//                    recyclerView.getChildAt(position).findViewById(R.id.btn_recyclerview_buildingDetail).setVisibility(View.VISIBLE);
//                }
//                unitViewAdapter.notifyDataSetChanged();
//            }
//        });

        recyclerView = findViewById(R.id.rv_building_detail);
        emptyView = findViewById(R.id.tv_building_detail_emptyView);
        spinner = findViewById(R.id.sp_building_detail);
        spinner.setOnItemSelectedListener(this);
        //dbRef = FirebaseDatabase.getInstance().getReference("units/"+ thisBuildingKey);
        dbRef = FirebaseDatabase.getInstance().getReference().child("units");
        myFormatter = NumberFormat.getInstance(Locale.getDefault());
        nowContext = this;
    }

    public void getData(){

        unitList.clear();
        unpaidUnitList.clear();
        paidUnitList.clear();

        unpaidCnt = 0;
        paidCnt = 0;
        occupiedCnt = 0;

        dbRef.child(thisBuildingKey).addValueEventListener(new ValueEventListener() {  //addValueEventListener : 한 번만 콜백되고 즉시 삭제
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
                        paidUnitList.add(unitItem);
                        //monthIncome += Integer.parseInt(unitItem.getMonthlyFee())+Integer.parseInt(unitItem.getMngFee());
                        paidCnt++;
                    }else if (unitItem.getIsPaid().equals("0")){
                        unpaidCnt++;
                        unpaidUnitList.add(unitItem);
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

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    unitList.add(unitItem);
                }

                Log.w("===", "unitList : " + unitList.toString());

                setAdapter(unitList);
                unitViewAdapter.notifyDataSetChanged();
                setData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("===", "getPersonalInfoThread() : onCancelled", databaseError.toException());
            }
        });
    }

    public void setData(){
        buildingNameTv.setText(buildingName+"");
        unpaidCntTv.setText(unpaidCnt+"");
        paidCntTv.setText(paidCnt+"");
        occupiedCntTv.setText(occupiedCnt+"");
        unitCntTv.setText(unitCnt+"");
    }

    public void setAdapter(ArrayList<Unit> list){
        recyclerView=findViewById(R.id.rv_building_detail);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        unitViewAdapter = new UnitViewAdapter(list);
        recyclerView.setAdapter(unitViewAdapter);

        // 리사이클러뷰의 아이템 클릭 시 호수의 UnitDetailActivity 로 이동
        unitViewAdapter.setOnItemClickListener(new UnitViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 액티비티 이동
                Intent intent = new Intent(getApplicationContext(), UnitDetailActivity.class);
                intent.putExtra("thisBuildingKey", thisBuildingKey);
                intent.putExtra("thisBuildingName", thisBuilding.getName());
                intent.putExtra("thisUnitKey", unitList.get(position).getUnitID());
                intent.putExtra("thisUnit", unitList.get(position));
                startActivity(intent);
            }
        });
        unitViewAdapter.notifyDataSetChanged();
    }

    public void setSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.filter_list,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if(item.equals("전체")){
           getData();
        }else if (item.equals("완납")){
            setAdapter(paidUnitList);
        }else if (item.equals("미납")){
            setAdapter(unpaidUnitList);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

class UnitViewAdapter extends RecyclerView.Adapter<UnitViewAdapter.MyViewHolder>{

    private ArrayList<Unit> units;
    private OnItemClickListener onItemClickListener = null;

    //아이템 뷰를 저장하는 뷰 홀더 클래스
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView unitNumTv,userNameTv,monthlyFeeTv,isPaidTv,startDateTv,endDateTv;
        CheckBox checkBox;
        ProgressBar progressBar;
        ImageButton btn_unitDetail;

        //뷰 객체에 대한 참조
        MyViewHolder(final View itemView){
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_rv_unit_selectedItem);
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

        holder.unitNumTv.setText(unitNum + "호");
        holder.userNameTv.setText(units.get(position).getTenantName());
        int totalFee = Integer.parseInt(units.get(position).getMonthlyFee().replace(",", ""))+Integer.parseInt(units.get(position).getMngFee().replace(",", ""));
//        holder.monthlyFeeTv.setText("월 "+ myFormatter.format(totalFee) +" 원");
        holder.monthlyFeeTv.setText("월 "+ totalFee +" 원");
        holder.startDateTv.setText(units.get(position).getStartDate());
        holder.endDateTv.setText(units.get(position).getEndDate());
        if(units.get(position).getIsPaid().equals("0")){
            holder.isPaidTv.setText("미납");
            holder.isPaidTv.setTextColor(Color.RED);
        }else if(units.get(position).getIsPaid().equals("1")){
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

