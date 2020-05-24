package com.example.jiptalk;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;


public class Util {
    public ProgressDialog mProgressDialog;

    public void showProgressDialog(Context nowContext) {

        if (mProgressDialog == null) {
            if (Build.VERSION_CODES.KITKAT < Build.VERSION.SDK_INT) {

//              R.style.ProgressDialogStyle은 커스텀으로 정의한 스타일임
                mProgressDialog = new ProgressDialog(nowContext, R.style.ProgressDialogStyle);

            } else {
                mProgressDialog = new ProgressDialog(nowContext);
            }
            mProgressDialog.setMessage("잠시만 기다려주세요...");
            mProgressDialog.setIndeterminate(true); //불확정적 상태 표시 모드 (작업 완료가 언제 될지 불확실)
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }


    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
