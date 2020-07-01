package com.example.jiptalk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.jiptalk.vo.Account;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "SameParameterValue", "unused"})
public class TenantPayDialog extends DialogFragment {
    private Context context;
    private Account landlordAct, tenantAct;
    private int amount;
    private String buildingID, unitID;
    TextView landlordAccountTv, tenantAccountTv, amountTv;

    public TenantPayDialog(@NonNull Context context, Account landlord, Account tenant, int amount, String buildingID, String unitID) {
        super();
        this.context = context;
        this.landlordAct = landlord;
        this.tenantAct = tenant;
        this.amount = amount;
        this.buildingID = buildingID;
        this.unitID = unitID;
    }

    @Override
    public void onStart() {
        Log.w("===", "tenantPayFeeDialog : onStart()");
        super.onStart();
        initialize();

    }

    // 제일 먼저 실행됨
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.w("===", "tenantPayFeeDialog : onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tenant_monthly_fee, null);

        builder.setView(view)
                .setTitle("알림")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        Log.w("===", "tenantPayFeeDialog : PositiveButton Clicked");

                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("units").child(buildingID).child(unitID).child("isPaid");
                        dbRef.setValue("1")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.w("===", "tenantPayFeeDialog : setValue succeed ");
                                        Toast.makeText(context, "성공적으로 납부 처리 되었습니다.", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("===", "tenantPayFeeDialog : setValue failed ");
                                        Toast.makeText(getContext(), "에러가 발생했습니다.", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.w("===", "tenantPayFeeDialog : NegativeButton Clicked");
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private void initialize() {
        landlordAccountTv = getDialog().findViewById(R.id.tv_dialog_tenant_landlordAccount);
        tenantAccountTv = getDialog().findViewById(R.id.tv_dialog_tenant_tenantAccount);
        amountTv = getDialog().findViewById(R.id.tv_dialog_tenant_amount);
        if(landlordAct != null){
            landlordAccountTv.setText(landlordAct.getBank() + " " + landlordAct.getAccountNum() + " " + landlordAct.getDepositor());
        }
        if(tenantAct.getBank() != null && tenantAct.getAccountNum() != null && tenantAct.getDepositor() != null){
            tenantAccountTv.setText(tenantAct.getBank() + " " + tenantAct.getAccountNum() + " " + tenantAct.getDepositor());
        }
        amountTv.setText(amount + " 원");
    }

    //    private String title;
//    private CharSequence account;
//    private CharSequence amount;
//    private String positiveBtnStr;
//    private String negativeBtnStr;
//    private ListAdapter adapter;
//    private AdapterView.OnItemClickListener listener;
//    private String[] multiChoiceItems;
//    private boolean[] multiChoiceCheckedItems;
//    private AdapterView.OnItemClickListener multiChoiceListener;
//    private String[] singleChoiceItems;
//    private int singleChoiceCheckedItem;
//    private AdapterView.OnItemClickListener singleChoiceListener;


//    @Override
//    public AlertDialog.Builder setTitle(@Nullable CharSequence title) {
//        this.title = String.valueOf(title);
//        return super.setTitle("");
//    }
//    @Override
//    public AlertDialog.Builder setMessage(@Nullable CharSequence message) {
//        this.account = message;
//        return super.setMessage("");
//    }
//    public AlertDialog.Builder setDetailMessage(@Nullable CharSequence detailMessage) {
//        this.amount = detailMessage;
//        return super.setMessage("");
//    }
//    @Override
//    public AlertDialog.Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
//        this.positiveBtnStr = String.valueOf(text);
//        return super.setPositiveButton("", listener);
//    }
//    @Override
//    public AlertDialog.Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
//        this.negativeBtnStr = String.valueOf(text);
//        return super.setNegativeButton("", listener);
//    }
//
//    public AlertDialog.Builder setAdapter(ListAdapter adapter, AdapterView.OnItemClickListener listener) {
//        this.adapter = adapter;
//        this.listener = listener;
//        return this;
//    }
//
//    public AlertDialog.Builder setMultiChoiceItems(String[] items, boolean[] checkedItems, AdapterView.OnItemClickListener multiChoiceListener) {
//        this.multiChoiceItems = items;
//        this.multiChoiceCheckedItems = checkedItems;
//        this.multiChoiceListener = multiChoiceListener;
//        return this;
//    }
//
//    public AlertDialog.Builder setSingleChoiceItems(String[] items, int checkedItem, AdapterView.OnItemClickListener listener) {
//        this.singleChoiceItems = items;
//        this.singleChoiceCheckedItem = checkedItem;
//        this.singleChoiceListener = listener;
//        return this;
//    }
//
//    @Override
//    public AlertDialog show() {
//        LayoutInflater inflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View view = inflater.inflate(R.layout.dialog_tenant_monthly_fee, null);
//
//        TextView tvTitle = view.findViewById(R.id.tvTitle);
//        TextView tvAccount = view.findViewById(R.id.tv_account);
//        TextView tvAmount = view.findViewById(R.id.tv_amount);
//        LinearLayout twoBtn = view.findViewById(R.id.twoBtn);
//        Button btnPositive = view.findViewById(R.id.btnPositive);
//        Button btnNegative = view.findViewById(R.id.btnNegative);
//
//        if (TextUtils.isEmpty(title)) {
//            tvTitle.setVisibility(View.GONE);
//        } else {
//            tvTitle.setText(title);
//        }
//
//        if (!TextUtils.isEmpty(positiveBtnStr)) {
//            btnPositive.setText(positiveBtnStr);
//        }
//
//        if (!TextUtils.isEmpty(negativeBtnStr)) {
//            btnNegative.setText(negativeBtnStr);
//        }
//
//
//
//        super.setView(view);
//        final AlertDialog dialog = super.create();
//
//        btnPositive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // performClick()을 사용하면 클릭소리가 두번 들리게되므로, callOnClick()을 사용한다.
//                dialog.getButton(DialogInterface.BUTTON_POSITIVE).callOnClick();
//            }
//        });
//
//        btnPositive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // performClick()을 사용하면 클릭소리가 두번 들리게되므로, callOnClick()을 사용한다.
//                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).callOnClick();
//            }
//        });


//        ScrollView llContents = view.findViewById(R.id.svContents);
//        ListView listView = view.findViewById(R.id.listView);

//        // 어댑터가 있으면 리스트뷰를 보여준다.
//        if (adapter != null && listener != null) {
//            llContents.setVisibility(View.GONE);
//            listView.setVisibility(View.VISIBLE);
//
//            listView.setAdapter(adapter);
//            listView.setOnItemClickListener((parent, view1, position, id) -> {
//                dialog.dismiss();
//                listener.onItemClick(parent, view1, position, id);
//            });
//        }

//        // 멀티선택이 있으면 보여준다.
//        else if (multiChoiceItems != null && multiChoiceListener != null) {
//            llContents.setVisibility(View.GONE);
//            listView.setVisibility(View.VISIBLE);
//
//            adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.select_dialog_multichoice, android.R.id.text1, multiChoiceItems) {
//                @NonNull
//                @Override
//                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                    View view = super.getView(position, convertView, parent);
//                    if (multiChoiceCheckedItems != null) {
//                        boolean isItemChecked = multiChoiceCheckedItems[position];
//                        if (isItemChecked) {
//                            listView.setItemChecked(position, true);
//                        }
//                    }
//                    return view;
//                }
//            };
//            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
//            listView.setAdapter(adapter);
//            listView.setOnItemClickListener((parent, view2, position, id) -> {
//                multiChoiceListener.onItemClick(parent, view2, position, id);
//            });
//        }
//
//        // 싱글선택이 있으면 보여준다.
//        else if (singleChoiceItems != null && singleChoiceListener != null) {
//            llContents.setVisibility(View.GONE);
//            listView.setVisibility(View.VISIBLE);
//
//            adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.select_dialog_singlechoice, android.R.id.text1, singleChoiceItems) {
//                @NonNull
//                @Override
//                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                    View view = super.getView(position, convertView, parent);
//                    if (position == singleChoiceCheckedItem) {
//                        listView.setItemChecked(position, true);
//                    }
//                    return view;
//                }
//            };
//            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
//            listView.setAdapter(adapter);
//            listView.setOnItemClickListener((parent, view2, position, id) -> {
//                singleChoiceListener.onItemClick(parent, view2, position, id);
//            });
//        }
//
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//
//        dialog.show();
//
//        // 크기 조정
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int widthPixel = (int)(displayMetrics.widthPixels*0.85);
//        int heightPixel = ViewGroup.LayoutParams.WRAP_CONTENT;
//
//        dialog.getWindow().setLayout(widthPixel, heightPixel);
//
//        return null;
//    }
}

