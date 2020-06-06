package com.example.jiptalk.ui.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.jiptalk.Constant;
import com.example.jiptalk.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//DialogFragment는 API level 11부터 지원!
public class ModUserNameDialog extends DialogFragment {

    private EditText newNameTv;
    private Context rActivityContext;

    public ModUserNameDialog(Context context) {
        super();
        rActivityContext = context; //SettingUserInfoActivity에 토스트 띄우기 위해 Context를 가져온다.
    }

    /* OncreateDialog onCreateView onViewCreated onStart 순으로 실행되는 것 확인 */
    /* lifecycle of DialogFragment 참고*/

    @Override
    public void onStart() {
        Log.w("===", "ModUserNameDialog : onStart()");
        super.onStart();
        newNameTv = getDialog().findViewById(R.id.tv_mod_user_name_newUsername);
        newNameTv.requestFocus();
    }

    // 제일 먼저 실행됨
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.w("===", "ModUserNameDialog : onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_mod_user_name, null);

        // dialog
        builder.setView(view)
                .setTitle("이름 수정")
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        Log.w("===", "ModUserNameDialog : PositiveButton Clicked");
                        // 유저 이름 업데이트
                        String newName = newNameTv.getText().toString();
                        Log.w("===", "ModUserNameDialog : getText " + newName);
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("user/"+ Constant.userUID +"/name");

                        dbRef.setValue(newName)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.w("===", "ModUserNameDialog : setValue succeed ");
                                        Toast.makeText(rActivityContext, "성공적으로 적용되었습니다.", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("===", "ModUserNameDialog : setValue failed ");
                                        Toast.makeText(getContext(), "에러가 발생했습니다.", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.w("===", "ModUserNameDialog : NegativeButton Clicked");
                        dialog.dismiss();
                    }
                });
        Log.w("===", "ModUserNameDialog : onCreateDialog 2");
        return builder.create();
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        Log.w("===", "ModUserNameDialog : onViewCreated");
//        super.onViewCreated(view, savedInstanceState);
//        newNameTv = view.findViewById(R.id.tv_mod_user_name_newUsername);
//        newNameTv.requestFocus();
//        Log.w("===", "ModUserNameDialog : onViewCreated2");
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Log.w("===", "ModUserNameDialog : onCreateView");
//        View view = inflater.inflate(R.layout.dialog_mod_user_name, container, false);
//        newNameTv = view.findViewById(R.id.tv_mod_user_name_newUsername);
//        newNameTv.requestFocus();
//        Log.w("===", "ModUserNameDialog : onCreateView 2");
//        return view;
//    }
}
