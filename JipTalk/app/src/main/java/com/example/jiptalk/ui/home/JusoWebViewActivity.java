package com.example.jiptalk.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.R;

public class JusoWebViewActivity extends AppCompatActivity {

    private WebView daum_webView;
    Handler handler = new Handler();

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juso_web_view);

        // WebView 초기화
        init_webView();
    }


    public void init_webView() {

        // WebView 설정
        daum_webView = (WebView) findViewById(R.id.daum_webview);
        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new AndroidBridge(), "TestApp");

        // web client 를 chrome 으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient());

        // webview url load. php 파일 주소
        daum_webView.loadUrl("http://192.168.56.1/JusoWebServer/JusoWebServer.html");

    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(handler != null){
                        handler.removeMessages(0);
                        init_webView();
                        Intent intent = new Intent();
                        intent.putExtra("result",arg2+" "+arg3);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                }
            });
        }
    }


}
