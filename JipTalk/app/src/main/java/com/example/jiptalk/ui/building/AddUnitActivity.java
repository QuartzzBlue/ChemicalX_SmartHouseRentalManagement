package com.example.jiptalk.ui.building;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.AppData;
import com.example.jiptalk.R;
import com.example.jiptalk.Util;
import com.example.jiptalk.Valid;
import com.example.jiptalk.vo.Tenant;
import com.example.jiptalk.vo.Unit;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddUnitActivity extends AppCompatActivity {

    LinearLayout monthlyFeeLo;
    EditText unitNumEt, tenantNameEt, payerNameEt, tenantPhoneEt, startDateEt, endDateEt, depositEt, monthlyFeeEt, manageFeeEt, totalFeeEt, payDayEt;
    ImageView startDateBtn, endDateBtn;
    Button makeSameBtn,cameraBtn;
    RadioGroup leaseTypeRg, contractRg;
    RadioButton leaseTypeRb, contractRb, leaseTypeRbMonthly, leaseTypeRbFullDeposit, leaseTypeRbFullFee, contractRb3m, contractRb6m, contractRb1y, contractRb2y;
    TextView errMsgTv;
    Calendar startDateCalendar, endDateCalendar;
    View mainView;

    String dateFlag = "";
    private String thisBuildingKey;
    private DatePickerDialog.OnDateSetListener callbackMethodDatePicker;

    FirebaseDatabase mDatabase;
    DatabaseReference unitRef, totalRef;
    Valid valid;
    Context nowContext;
    NumberFormat myFormatter;
    Util util;

    private int totalUnpaidCnt, totalOccupiedCnt, totalMonthlyIncome;
    private String userUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);
        initialization();

        tenantPhoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //입금자동일 버튼 클릭 시
        makeSameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payerNameEt.setText(tenantNameEt.getText().toString());
            }
        });

        //보증금 EditText
        depositEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(depositEt.getText().toString().length() == 0) depositEt.setText("0");
                }
                else{
                    if(depositEt.getText().toString().equals("0")) depositEt.setText("");
                }
            }
        });

        //관리비 EditText
        manageFeeEt.addTextChangedListener(textWatcher);
        manageFeeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(manageFeeEt.getText().toString().length() == 0) manageFeeEt.setText("0");
                }
                else{
                    if(manageFeeEt.getText().toString().equals("0")) manageFeeEt.setText("");
                }
            }
        });

        //월세 EditText
        monthlyFeeEt.addTextChangedListener(textWatcher);
        monthlyFeeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(monthlyFeeEt.getText().toString().length() == 0) monthlyFeeEt.setText("0");
                }
                else{
                    if(monthlyFeeEt.getText().toString().equals("0")) monthlyFeeEt.setText("");
                }
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });

        leaseTypeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_add_unit_leaseType_monthly) {   //월세
                    monthlyFeeLo.setVisibility(View.VISIBLE);
                }else if(checkedId == R.id.rb_add_unit_leaseType_fullDeposit) {   //전세
                    monthlyFeeLo.setVisibility(View.GONE);
                }else {  //선납
                    monthlyFeeLo.setVisibility(View.VISIBLE);
                }
                initializeLeaseOptions();
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
                String deposit = depositEt.getText().toString().replace(",", "");
                String monthlyFee = monthlyFeeEt.getText().toString().replace(",", "");
                String manageFee = manageFeeEt.getText().toString().replace(",", "");
                String payDay = payDayEt.getText().toString();

                leaseTypeRb = findViewById(leaseTypeRg.getCheckedRadioButtonId());
                String leaseType = leaseTypeRb.getText().toString();

//                Unit newUnit = new Unit(unitNum, leaseType, tenantName, tenantPhone, payerName, deposit, manageFee, monthlyFee, payDay, startDate, endDate);
                Unit newUnit = new Unit(unitNum, leaseType, tenantName, tenantPhone, payerName, deposit, manageFee, monthlyFee, payDay, startDate, endDate,"0","1");
                Log.v("===","unit created : "+newUnit.toString());
                /* 유효성 검사 */
                //if (!isValid(newUnit)) return false;

                /* 유효성 검사 끝나면 인증 & DB 삽입*/
                createUnit(newUnit);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initialization() {
        util = new Util();
        thisBuildingKey = getIntent().getStringExtra("thisBuildingKey");

        mainView = findViewById(R.id.sv_add_unit);

        monthlyFeeLo = findViewById(R.id.layout_add_unit_monthlyFee);
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
        cameraBtn = findViewById(R.id.btn_moveToCamera);

        //기능 구현 후 보이게
        cameraBtn.setVisibility(View.INVISIBLE);

        leaseTypeRg = findViewById(R.id.rg_add_unit_leaseType);
        contractRg = findViewById(R.id.rg_add_unit_contract);
        leaseTypeRbMonthly = findViewById(R.id.rb_add_unit_leaseType_monthly);
        leaseTypeRbFullDeposit = findViewById(R.id.rb_add_unit_leaseType_fullDeposit);
        leaseTypeRbFullFee = findViewById(R.id.rb_add_unit_leaseType_fullFee);
        contractRb3m = findViewById(R.id.rb_add_unit_contract_3m);
        contractRb6m = findViewById(R.id.rb_add_unit_contract_6m);
        contractRb1y = findViewById(R.id.rb_add_unit_contract_1y);
        contractRb2y = findViewById(R.id.rb_add_unit_contract_2y);

        myFormatter = NumberFormat.getInstance(Locale.getDefault());
        depositEt.addTextChangedListener(new NumberTextWatcher(depositEt));
        monthlyFeeEt.addTextChangedListener(new NumberTextWatcher(monthlyFeeEt));
        manageFeeEt.addTextChangedListener(new NumberTextWatcher(manageFeeEt));

        errMsgTv = findViewById(R.id.tv_add_unit_errMsg);
        nowContext = this;


        mDatabase = FirebaseDatabase.getInstance();
//        unitRef = mDatabase.getReference("units").child(thisBuildingKey);
        unitRef = mDatabase.getReference();
        userUid = AppData.userUID;
        totalRef = mDatabase.getReference("buildings").child(userUid);

        initDatePickerListener();

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int monthlyFee, manageFee;
            if(monthlyFeeEt.getText().toString().trim().equals("")){
                monthlyFee = 0;
            }else{
                monthlyFee = Integer.parseInt(monthlyFeeEt.getText().toString().replaceAll(",",""));
            }
            if(manageFeeEt.getText().toString().trim().equals("")){
                manageFee = 0;
            }else{
                manageFee = Integer.parseInt(manageFeeEt.getText().toString().replaceAll(",",""));
            }
            int totalFee = monthlyFee + manageFee;
            totalFeeEt.setText(myFormatter.format(totalFee));
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

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
                    errMsgTv.setText("시작 날짜를 선택해 주세요.");
                    return;
                } else {
                    //왜 안먹지..?
                    errMsgTv.setVisibility(View.INVISIBLE);
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
                endDateCalendar.add(Calendar.DATE, -1);

                String myFormat = "yyyy.MM.dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.KOREA);
                endDateEt.setText(simpleDateFormat.format(endDateCalendar.getTime()));

                //왜 안먹지..?
                endDateCalendar.setTime(startDateCalendar.getTime());

            }
        });

    }

    private void initializeLeaseOptions(){
        depositEt.setText("0");
        monthlyFeeEt.setText("0");
        manageFeeEt.setText("0");
        totalFeeEt.setText("0");
    }

    private boolean isValid(Unit newUnit) {

        Log.d("===", String.valueOf(newUnit.getUnitNum().equals("")));

        if(!valid.isNotBlank(newUnit.getUnitNum()))
//                ||!valid.isNotBlank(newUnit.getTenantName())||!valid.isNotBlank(newUnit.getPayerName())
//                ||!valid.isNotBlank(newUnit.getTenantPhone())||!valid.isNotBlank(newUnit.getStartDate())||!valid.isNotBlank(newUnit.getEndDate())
//                ||!valid.isNotBlank(newUnit.getPayDay()))
        {
            Log.d("===", "createBuilding : not valid value" );
            //Snackbar.make(mainView,"빈칸을 모두 채워주세요",Snackbar.LENGTH_SHORT).show();
            //Toast.makeText(nowContext, "빈칸을 모두 채워주세요",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createUnit(final Unit newUnit) {

        Log.d("===", "createUnit()");

        final Util util = new Util();

        util.showProgressDialog(nowContext);

        Map<String, Object> childUpdates = new HashMap<>();

        String unitKey = unitRef.child("units").child(thisBuildingKey).push().getKey();
        String tenantKey = unitRef.child("tenants").push().getKey();

        Tenant tenant = new Tenant();
        tenant.setName(newUnit.getTenantName());
        tenant.setPhone(newUnit.getTenantPhone());
        tenant.setUnitID(unitKey);
        tenant.setBuildingID(thisBuildingKey);

        childUpdates.put("units/" + thisBuildingKey + "/" + unitKey + "/", newUnit.toMap());
        childUpdates.put("registeredTenants/" + tenantKey + "/", tenant.toMap());

        unitRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("===", "insertUnitToDatabase : succeed");
                Toast.makeText(nowContext, "성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                util.hideProgressDialog();
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

class NumberTextWatcher implements TextWatcher{

    private NumberFormat nf;
    private EditText et;

    public NumberTextWatcher(EditText et){
       nf = NumberFormat.getInstance();
       this.et = et;
    }

    private static final String TAG = "NumberTextWatcher";

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        et.removeTextChangedListener(this);

        try{
            String tmp = s.toString();
            tmp = tmp.replace(",","");
            long itmp = Long.parseLong(tmp);
            tmp = nf.format(itmp);
            et.setText(tmp);
            et.setSelection(tmp.length());
        } catch(Exception e){

        }
        et.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
