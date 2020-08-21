package com.example.jiptalk.tenant;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.R;
import com.example.jiptalk.tenant.kakaopay.KakaopayWebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private WebView webview;
    private final String APP_SCHEME = "iamportkakao://";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        init();

    }

    private void init(){
        String url = getIntent().getStringExtra("redirect_url");
        String scheme = getIntent().getStringExtra("scheme");

        webview = findViewById(R.id.webView);
        webview.setWebViewClient(new WebViewClient(){
            public static final String INTENT_PROTOCOL_START = "intent:";
            public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
            public static final String INTENT_PROTOCOL_END = ";end;";
            public static final String INTENT_PROTOCOL_URI = "?url=";
            public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                return super.shouldOverrideUrlLoading(view, request);
                String url = String.valueOf(request.getUrl());
                Log.d("===", "url = " + url);

                if (url.startsWith(INTENT_PROTOCOL_START)) {
                    final int customUrlStartIntentIndex = INTENT_PROTOCOL_START.length();
                    final int customUrlStartIndex = url.indexOf(INTENT_PROTOCOL_URI)+5;
                    final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                    if (customUrlEndIndex < 0) {
                        return false;
                    } else {
                        final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                        Log.d("===", "customUrl : " + customUrl);
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                        } catch (ActivityNotFoundException e) {
                            final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                            final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                            final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                        }
                        return true;
                    }
                } else {
                    return false;
                }
            }
        });
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        settings.setSupportMultipleWindows(true); // 새창 띄우기 허용 여부
        settings.setDomStorageEnabled(true); // 로컬저장소 허용 여부
        settings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부


//        settings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
//        settings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
//        settings.setSupportZoom(false); // 화면 줌 허용 여부
//        settings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부

        webview.loadUrl(url);
//        webview.loadUrl(scheme);

        Log.d("===", "webview init");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("===", "webview activity onResume");
        Intent intent = getIntent();
        if ( intent != null ) {
            Uri intentData = intent.getData();

            if ( intentData != null ) {
                //카카오페이 인증 후 복귀했을 때 결제 후속조치
                String url = intentData.toString();

                if ( url.startsWith(APP_SCHEME) ) {
                    String path = url.substring(APP_SCHEME.length());
                    if ( "process".equalsIgnoreCase(path) ) {
                        webview.loadUrl("javascript:IMP.communicate({result:'process'})");
                    } else {
                        webview.loadUrl("javascript:IMP.communicate({result:'cancel'})");
                    }
                }
            }
        }

    }
}
