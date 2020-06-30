package com.example.jiptalk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "SameParameterValue", "unused"})
public class MyDialog extends AlertDialog.Builder {
    private Context context;

    public MyDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    private String title;
    private String amount;
    private String positiveBtnStr;
    private String negativeBtnStr;


    @Override
    public AlertDialog.Builder setTitle(@Nullable CharSequence title) {
        this.title = String.valueOf(title);
        return super.setTitle("");
    }

    public AlertDialog.Builder setAmount(@Nullable CharSequence amount) {
        this.amount = String.valueOf(amount);
        return super.setMessage("");
    }
    @Override
    public AlertDialog.Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.positiveBtnStr = String.valueOf(text);
        return super.setPositiveButton("", listener);
    }
    @Override
    public AlertDialog.Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.negativeBtnStr = String.valueOf(text);
        return super.setNegativeButton("", listener);
    }

    @Override
    public AlertDialog show() {

        LayoutInflater inflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.km_alert_dialog_default, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvAmount = view.findViewById(R.id.tv_amount);
        LinearLayout twoBtn = view.findViewById(R.id.twoBtn);
        Button btnPositive = view.findViewById(R.id.btnPositive);
        Button btnNegative = view.findViewById(R.id.btnNegative);

        tvTitle.setText(title);
        tvAmount.setText(amount);
        btnPositive.setText(positiveBtnStr);
        btnNegative.setText(negativeBtnStr);

        super.setView(view);
        final AlertDialog dialog = super.create();

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

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();

        // 크기 조정
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixel = (int)(displayMetrics.widthPixels*0.85);
        int heightPixel = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(widthPixel, heightPixel);

        return null;
    }
}

