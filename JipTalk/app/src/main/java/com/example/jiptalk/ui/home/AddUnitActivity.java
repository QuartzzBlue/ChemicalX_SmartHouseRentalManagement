package com.example.jiptalk.ui.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.Constant;
import com.example.jiptalk.MainActivity;
import com.example.jiptalk.R;
import com.example.jiptalk.Util;
import com.example.jiptalk.Valid;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.Unit;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddUnitActivity extends AppCompatActivity {

    EditText unitNumEt, tenantNameEt, payerNameEt, tenantPhoneEt, startDateEt, endDateEt, depositEt, monthlyFeeEt, manageFeeEt, totalFeeEt, payDayEt;
    ImageView startDateBtn, endDateBtn;
    Button makeSameBtn;
    RadioGroup leaseTypeRg, contractRg;
    RadioButton leaseTypeRb, contractRb, leaseTypeRbMonthly, leaseTypeRbFullDeposit, leaseTypeRbFullFee, contractRb3m, contractRb6m, contractRb1y, contractRb2y;
    TextView errMsgtv;
    Calendar startDateCalendar, endDateCalendar;

    String dateFlag = "";
    String buildingKey;

    private DatePickerDialog.OnDateSetListener callbackMethodDatePicker;
    FirebaseDatabase mDatabase;
    DatabaseReference unitRef;
    Valid valid;
    Context nowContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);
        initialization();

        //입금자동일 버튼 클릭 시
        makeSameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payerNameEt.setText(tenantNameEt.getText().toString());
            }
        });

        manageFeeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(monthlyFeeEt.getText().toString().length() !=0 && manageFeeEt.getText().toString().length()!=0){
                        totalFeeEt.setText(Integer.parseInt(monthlyFeeEt.getText().toString())+Integer.parseInt(manageFeeEt.getText().toString())+"");
                    }
                }
            }
        });

    }

    /* AppBar 에 세이브 버튼 추가 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_save, menu);
        return true;
    }

    //세이브 버튼 클릭 시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:

                String unitNum = unitNumEt.getText().toString();
                String tenantName = tenantNameEt.getText().toString();
                String tenantPhone = tenantPhoneEt.getText().toString();
                String payerName = payerNameEt.getText().toString();
                String startDate = startDateEt.getText().toString();
                String endDate = endDateEt.getText().toString();
                String deposit = depositEt.getText().toString();
                String monthlyFee = monthlyFeeEt.getText().toString();
                String manageFee = manageFeeEt.getText().toString();
                String payDay = manageFeeEt.getText().toString();

                leaseTypeRb = findViewById(leaseTypeRg.getCheckedRadioButtonId());
                String leaseType = leaseTypeRb.getText().toString();

                contractRb = findViewById(contractRg.getCheckedRadioButtonId());
                String contract = contractRb.getText().toString();

//                Unit newUnit = new Unit(unitNum, leaseType, tenantName, tenantPhone, payerName, deposit, manageFee, monthlyFee, payDay, startDate, endDate);
                Unit newUnit = new Unit(unitNum, leaseType, tenantName, tenantPhone, payerName, deposit, manageFee, monthlyFee, payDay, startDate, endDate,"-1","1");

                /* 유효성 검사 */
                if (!isValid(newUnit)) return false;

                /* 유효성 검사 끝나면 인증 & DB 삽입*/
                createUnit(newUnit);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void initialization() {

        unitNumEt = findViewById(R.id.et_add_unit_unitNum);
        tenantNameEt = findViewById(R.id.et_add_unit_tenantName);
        payerNameEt = findViewById(R.id.et_add_unit_payerName);
        tenantPhoneEt = findViewById(R.id.et_add_unit_tenantPhone);
        startDateEt = findViewById(R.id.et_add_unit_startDate);
        endDateEt = findViewById(R.id.et_add_unit_endDate);
        depositEt = findViewById(R.id.et_add_unit_deposit);
        monthlyFeeEt = findViewById(R.id.et_add_unit_monthlyFee);
        manageFeeEt = findViewById(R.id.et_add_unit_manageFee);
        totalFeeEt = findViewById(R.id.et_add_unit_totalFee);
        payDayEt = findViewById(R.id.et_add_unit_payDay);

        startDateBtn = findViewById(R.id.btn_add_unit_startDate);
        endDateBtn = findViewById(R.id.btn_add_unit_endDate);
        makeSameBtn = findViewById(R.id.btn_add_unit_makeSame);

        leaseTypeRg = findViewById(R.id.rg_add_unit_leaseType);
        contractRg = findViewById(R.id.rg_add_unit_contract);
        leaseTypeRbMonthly = findViewById(R.id.rb_add_unit_leaseType_monthly);
        leaseTypeRbFullDeposit = findViewById(R.id.rb_add_unit_leaseType_fullDeposit);
        leaseTypeRbFullFee = findViewById(R.id.rb_add_unit_leaseType_fullFee);
        contractRb3m = findViewById(R.id.rb_add_unit_contract_3m);
        contractRb6m = findViewById(R.id.rb_add_unit_contract_6m);
        contractRb1y = findViewById(R.id.rb_add_unit_contract_1y);
        contractRb2y = findViewById(R.id.rb_add_unit_contract_2y);

        errMsgtv = findViewById(R.id.tv_add_unit_errMsg);

        nowContext = this;

        buildingKey = getIntent().getStringExtra("buildingKey");

        mDatabase = FirebaseDatabase.getInstance();
        unitRef = mDatabase.getReference("buildings").child(Constant.userUID).child(buildingKey).child("units");

        initDatePickerListener();


    }

    public void initDatePickerListener() {

        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();

        callbackMethodDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (dateFlag.equals("start")) {
                    startDateEt.setText(year + "." + (month + 1) + "." + dayOfMonth);
                    startDateCalendar.set(year,month,dayOfMonth);
                    endDateCalendar.setTime(startDateCalendar.getTime());

                } else if (dateFlag.equals("end")) {
                    endDateEt.setText(year + "." + (month + 1) + "." + dayOfMonth);
                }
            }
        };

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFlag = "start";
                DatePickerDialog dialog = new DatePickerDialog(nowContext, callbackMethodDatePicker, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFlag = "end";
                DatePickerDialog dialog = new DatePickerDialog(nowContext, callbackMethodDatePicker, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        contractRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (startDateEt.getText().toString().equals("")) {
                    errMsgtv.setText("시작 날짜를 선택해 주세요!");
                    return;
                } else {
                    //왜 안먹지..?
                    errMsgtv.setVisibility(View.INVISIBLE);
                }

                if (checkedId == R.id.rb_add_unit_contract_3m) {
                    endDateCalendar.add(Calendar.MONTH, 3);
                    Log.d("===","startDate : " +startDateCalendar.getTime());

                } else if (contractRg.getCheckedRadioButtonId() == R.id.rb_add_unit_contract_6m) {
                    endDateCalendar.add(Calendar.MONTH, 6);
                } else if (contractRg.getCheckedRadioButtonId() == R.id.rb_add_unit_contract_1y) {
                    endDateCalendar.add(Calendar.YEAR, 1);
                } else if (contractRg.getCheckedRadioButtonId() == R.id.rb_add_unit_contract_2y) {
                    endDateCalendar.add(Calendar.YEAR, 2);
                }

                String myFormat = "yyyy.MM.dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.KOREA);
                endDateEt.setText(simpleDateFormat.format(endDateCalendar.getTime()));

                //왜 안먹지..?
                endDateCalendar.setTime(startDateCalendar.getTime());

            }
        });

    }

    private boolean isValid(Unit newUnit) {

//        if(!valid.isNotBlank(newUnit.getUnitNum()||!valid.isNotBlank(newUnit.getTenantName())||!valid.isNotBlank(newUnit.getPayerName()||!valid.isNotBlank(newUnit.getTenantPhone()))){
//            Log.d("===", "createBuilding : not valid value" );
//            Toast.makeText(nowContext, "빈칸을 모두 채워주세요",
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    private void createUnit(final Unit newUnit) {

        Log.d("===", "createUnit");

        final Util util = new Util();

        util.showProgressDialog(nowContext);

        Log.d("===",Constant.userUID);
        String key = unitRef.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, newUnit.toMap());
        unitRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("===", "insertUnitToDatabase: succeed");
                Toast.makeText(nowContext, "성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                util.hideProgressDialog();
                // 액티비티 이동
                Intent intent = new Intent(getApplicationContext(),BuildingDetailActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(nowContext, "세대 등록이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.w("===", "insertBuildingToDatabase : Failed");
                util.hideProgressDialog();
                return;
            }
        });


    }
}

