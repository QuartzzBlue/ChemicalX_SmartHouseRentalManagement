package com.example.jiptalk;

import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Valid {

    public boolean isValidEmail(String email){
        if (email == null || TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public boolean isValidPwd(String pwd){
        //숫자,문자,특수문자 모두 포함 (8~15자), 한글미포함
        Pattern p = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,15}.$");

        Matcher m = p.matcher(pwd);
        if (m.find() && !pwd.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")){
            return true;
        }else{
            return false;
        }
    }

    public boolean isValidPhone(String phone){

        if (Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", phone)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isNotBlank(String value){
        if(value.replace(" ","").equals("")){
            Log.d("===", "false" );
            return false;
        }else{
            return true;
        }
    }



}
