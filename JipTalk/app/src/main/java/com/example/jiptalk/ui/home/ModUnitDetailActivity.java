package com.example.jiptalk.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiptalk.R;
import com.example.jiptalk.vo.Unit;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModUnitDetailActivity extends AppCompatActivity {

    private Context nowContext;
    private Unit thisUnit;
    private String thisBuildingKey;

    private EditText tNameEt, pNameEt, tPhoneEt, startDateEt, endDateEt, depositEt, monthlyFeeEt, manageFeeEt, tMonthlyFeeEt, payDayEt;
    private TextView monthlyFeeTv, errorMsgTv;
    private RadioGroup leaseTypeRg, contractPeriodRg;
    private RadioButton sLeaseTypeRb;
    private Button payerSameBt;
    private ImageView startDateBt, endDateBt;

    NumberFormat myFormatter;
    Calendar sCalendar, eCalendar;
    DatePickerDialog.OnDateSetListener callbackMethodDatePicker;
    DatePickerDialog dialog;
    String dateFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_unit_detail);
        initialize();

    }

    /* AppBar 에 완료 버튼 추가 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action_complete, menu);
        return true;
    }

    //완료 버튼 클릭 시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_complete:

                /* 유효성 검사 */
//              if (!isValid(newUnit)) return false;

                getUnitData();
                Log.v("===","modified unit info : " + thisUnit.toString());

                updateUnit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getUnitData() {
        String tenantName = tNameEt.getText().toString();
        String tenantPhone = tPhoneEt.getText().toString();
        String payerName = pNameEt.getText().toString();
        String startDate = startDateEt.getText().toString();
        String endDate = endDateEt.getText().toString();
        String deposit = depositEt.getText().toString().replace(",", "");
        String monthlyFee = monthlyFeeEt.getText().toString().replace(",", "");;
        String manageFee = manageFeeEt.getText().toString().replace(",", "");;
        sLeaseTypeRb = findViewById(leaseTypeRg.getCheckedRadioButtonId());
        String leaseType = sLeaseTypeRb.getText().toString();

        thisUnit.setTenantName(tenantName);
        thisUnit.setTenantPhone(tenantPhone);
        thisUnit.setPayerName(payerName);
        thisUnit.setStartDate(startDate);
        thisUnit.setEndDate(endDate);
        thisUnit.setDeposit(deposit);
        thisUnit.setMngFee(manageFee);
        thisUnit.setLeaseType(leaseType);
        thisUnit.setMonthlyFee(monthlyFee);
    }

    private void updateUnit() {
        DatabaseReference unitRef = FirebaseDatabase.getInstance().getReference("units");
        unitRef.child(thisBuildingKey).child(thisUnit.getUnitID()).setValue(thisUnit).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("===", "updateUnit : onSuccess");
                Toast.makeText(nowContext, "계약 정보를 성공적으로 수정했습니다.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("===", "updateUnit : onFailure " + e.getStackTrace());
                Toast.makeText(nowContext, "다시 시도해 주세요.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialize() {
        nowContext = this;
        thisUnit = (Unit) getIntent().getSerializableExtra("thisUnit");
        thisBuildingKey = getIntent().getStringExtra("thisBuildingKey");
        tNameEt = findViewById(R.id.et_mod_unit_detail_tenantName);
        pNameEt = findViewById(R.id.et_mod_unit_detail_payerName);
        tPhoneEt = findViewById(R.id.et_mod_unit_detail_tenantPhone);
        startDateEt = findViewById(R.id.et_mod_unit_detail_startDate);
        endDateEt = findViewById(R.id.et_mod_unit_detail_endDate);
        depositEt = findViewById(R.id.et_mod_unit_detail_deposit);
        monthlyFeeEt = findViewById(R.id.et_mod_unit_detail_monthlyFee);
        manageFeeEt = findViewById(R.id.et_mod_unit_detail_manageFee);
        tMonthlyFeeEt = findViewById(R.id.et_mod_unit_detail_monthlyTotalFee);
        payDayEt = findViewById(R.id.et_mod_unit_detail_payDay);

        monthlyFeeTv = findViewById(R.id.tv_mod_unit_detail_monthlyFee);
        errorMsgTv = findViewById(R.id.tv_mod_unit_detail_errorMsg);
        leaseTypeRg = findViewById(R.id.rg_mod_unit_detail_leaseType);
        contractPeriodRg = findViewById(R.id.rg_mod_unit_detail_contract);
        payerSameBt = findViewById(R.id.bt_mod_unit_detail_makeSame);
        startDateBt = findViewById(R.id.bt_mod_unit_detail_startDate);
        endDateBt = findViewById(R.id.bt_mod_unit_detail_endDate);
        sCalendar = Calendar.getInstance();
        eCalendar = Calendar.getInstance();
        String myFormat = "yyyy.MM.dd";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.KOREA);

        payerSameBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pNameEt.setText(tNameEt.getText().toString());
                Log.v("===","depositEt : " + depositEt.getText().toString());
            }
        });

        tPhoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        leaseTypeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_mod_unit_detail_monthly) { //월세
                    monthlyFeeTv.setVisibility(View.VISIBLE);
                    monthlyFeeEt.setVisibility(View.VISIBLE);
                }else if(checkedId == R.id.rb_mod_unit_detail_fullDeposit) { //전세
                    monthlyFeeEt.setText("");
                    monthlyFeeTv.setVisibility(View.GONE);
                    monthlyFeeEt.setVisibility(View.GONE);
                }else { //선납
                    monthlyFeeTv.setVisibility(View.VISIBLE);
                    monthlyFeeEt.setVisibility(View.VISIBLE);
                }
            }
        });

        callbackMethodDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (dateFlag.equals("startDate")) {
                    startDateEt.setText(year + "." + (month + 1) + "." + dayOfMonth);
                    sCalendar.set(year,month,dayOfMonth);

                    eCalendar.setTime(sCalendar.getTime());
                    eCalendar.add(Calendar.DATE, 1);
                } else if (dateFlag.equals("endDate")) {
                    endDateEt.setText(year + "." + (month + 1) + "." + dayOfMonth);
                }
            }
        };

        startDateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFlag = "startDate";
                dialog = new DatePickerDialog(nowContext, callbackMethodDatePicker,
                        sCalendar.get(Calendar.YEAR), sCalendar.get(Calendar.MONTH), sCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        endDateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFlag = "endDate";
                dialog = new DatePickerDialog(nowContext, callbackMethodDatePicker,
                        eCalendar.get(Calendar.YEAR), eCalendar.get(Calendar.MONTH), eCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        contractPeriodRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (startDateEt.getText().toString().trim().equals("")) {
                    errorMsgTv.setVisibility(View.VISIBLE);
                    return;
                }
                errorMsgTv.setVisibility(View.GONE);

                int selectedBt = contractPeriodRg.getCheckedRadioButtonId();
                eCalendar.setTime(sCalendar.getTime());

                if (selectedBt == R.id.rb_mod_unit_detail_contract_3m) {
                    eCalendar.add(Calendar.MONTH, 3);
                } else if (selectedBt == R.id.rb_mod_unit_detail_contract_6m) {
                    eCalendar.add(Calendar.MONTH, 6);
                } else if (selectedBt == R.id.rb_mod_unit_detail_contract_1y) {
                    eCalendar.add(Calendar.YEAR, 1);
                } else if (selectedBt == R.id.rb_mod_unit_detail_contract_2y) {
                    eCalendar.add(Calendar.YEAR, 2);
                }
                eCalendar.add(Calendar.DATE, -1);

                endDateEt.setText(simpleDateFormat.format(eCalendar.getTime()));
            }
        });

        depositEt.addTextChangedListener(new NumberTextWatcher(depositEt, null, null));
        monthlyFeeEt.addTextChangedListener(new NumberTextWatcher(monthlyFeeEt, manageFeeEt, tMonthlyFeeEt));
        manageFeeEt.addTextChangedListener(new NumberTextWatcher(manageFeeEt, monthlyFeeEt, tMonthlyFeeEt));

        setOriginalUnitData();

    }

    private void setOriginalUnitData() {
        tNameEt.setText(thisUnit.getTenantName());
        pNameEt.setText(thisUnit.getPayerName());
        tPhoneEt.setText(thisUnit.getTenantPhone());
        String sTemp = thisUnit.getStartDate();
        String eTemp = thisUnit.getEndDate();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy.MM.dd");
        try {
            Date sDate = transFormat.parse(sTemp);
            Date eDate = transFormat.parse(eTemp);
            sCalendar.setTime(sDate);
            eCalendar.setTime(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        startDateEt.setText(sTemp);
        endDateEt.setText(eTemp);

        if(thisUnit.getLeaseType().equals("월세")){
            sLeaseTypeRb = findViewById(R.id.rb_mod_unit_detail_monthly);
        }else if(thisUnit.getLeaseType().equals("전세")) {
            sLeaseTypeRb = findViewById(R.id.rb_mod_unit_detail_fullDeposit);
        }else if(thisUnit.getLeaseType().equals("선납")) {
            sLeaseTypeRb = findViewById(R.id.rb_mod_unit_detail_fullFee);
        }
        sLeaseTypeRb.setChecked(true);
        depositEt.setText(thisUnit.getDeposit());
        monthlyFeeEt.setText(thisUnit.getMonthlyFee());
        manageFeeEt.setText(thisUnit.getMngFee());
        payDayEt.setText(thisUnit.getPayDay());
    }

    public class NumberTextWatcher implements TextWatcher {

        private DecimalFormat df;
        private DecimalFormat dfnd;
        private boolean hasFractionalPart;

        private EditText thisEt, anoEt, totalEt;

        public NumberTextWatcher(EditText thisEt, EditText anoEt, EditText totalEt)
        {
            df = new DecimalFormat("#,###.##");
            df.setDecimalSeparatorAlwaysShown(true);
            dfnd = new DecimalFormat("#,###");
            this.thisEt = thisEt;
            this.anoEt = anoEt;
            this.totalEt = totalEt;
            hasFractionalPart = false;
        }

        @SuppressWarnings("unused")
        private static final String TAG = "NumberTextWatcher";

        @Override
        public void afterTextChanged(Editable s)
        {
            thisEt.removeTextChangedListener(this);

            try {
                int inilen, endlen;
                inilen = thisEt.getText().length();

                String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                Number n = df.parse(v);
                Number total = 0;

                int cp = thisEt.getSelectionStart();
                if (hasFractionalPart) {
                    thisEt.setText(df.format(n));
                } else {
                    thisEt.setText(dfnd.format(n));
                }

                if(anoEt != null) {
                    String v2 = anoEt.getText().toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n2 = df.parse(v2);
                    total = n.floatValue() + n2.floatValue();
                    totalEt.setText(dfnd.format(total));
                }

                endlen = thisEt.getText().length();
                int sel = (cp + (endlen - inilen));
                if (sel > 0 && sel <= thisEt.getText().length()) {
                    thisEt.setSelection(sel);
                } else {
                    // place cursor at the end?
                    thisEt.setSelection(thisEt.getText().length() - 1);
                }
            } catch (NumberFormatException nfe) {
                // do nothing?
            } catch (ParseException e) {
                // do nothing?
            }

            thisEt.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())))
            {
                hasFractionalPart = true;
            } else {
                hasFractionalPart = false;
            }
        }
    }
}
