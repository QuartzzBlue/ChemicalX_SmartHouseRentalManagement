package com.example.jiptalk.ui.building;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jiptalk.R;

public class JusoWebViewActivity extends AppCompatActivity {

    Handler handler;
    private WebView webView;
    Context nowContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juso_web_view);
        nowContext = this;
        handler = new Handler();

        //MyWebChromeClient myWebChromeClient = new MyWebChromeClient();
        //myWebChromeClient.onCreateWindow();

        //MyAsyncTask myAsyncTask = new MyAsyncTask();
        //myAsyncTask.execute();

        init_webView();
    }

    class MyWebChromeClient extends WebChromeClient{
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(JusoWebViewActivity.this);
            // JavaScript 허용
            newWebView.getSettings().setJavaScriptEnabled(true);

            final Dialog dialog = new Dialog(JusoWebViewActivity.this);
            dialog.setContentView(newWebView);

            ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams)params);
            dialog.show();

            newWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                }
            });

            newWebView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });

            ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
            resultMsg.sendToTarget();

            return true;
        }
    }

    public void init_webView() {

        webView = findViewById(R.id.webView);

        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setDomStorageEnabled(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        webView.addJavascriptInterface(new AndroidBridge(), "Android");

        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.setNetworkAvailable(true);

        /* 2019.03.21 sjwiq200 Cross App Scripting 대비 */
        webView.getSettings().setDatabaseEnabled(false);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setDomStorageEnabled(false);
        webView.getSettings().setAppCacheEnabled(false);

        webView.loadUrl("https://smart-house-rental-management.web.app/");

    }

    public class AndroidBridge extends WebViewClient {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (handler != null) {
                        handler.removeMessages(0);
                        init_webView();
                        Intent intent = new Intent();
                        intent.putExtra("result", arg2 + " " + arg3);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                }
            });
        }
    }

    class MyAsyncTask extends AsyncTask<Void,Void,Void> {

        WebView webView;

        @Override
        protected Void doInBackground(Void... voids) {
            webView = findViewById(R.id.webView);
            webView.post(new Runnable(){

                @Override
                public void run() {
                    init_webView();
                }
            });

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

//        public void init_webView() {
//
//            // JavaScript 허용
//            webView.getSettings().setJavaScriptEnabled(true);
//
//            // JavaScript의 window.open 허용
//            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//            webView.getSettings().setSupportMultipleWindows(true);
//            webView.getSettings().setDomStorageEnabled(true);
//
//            // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
//            webView.addJavascriptInterface(new AndroidBridge(), "Android");
//
//            webView.getSettings().setDomStorageEnabled(true);
//            webView.setWebChromeClient(new WebChromeClient());
//            webView.setWebViewClient(new WebViewClient());
//            webView.setNetworkAvailable(true);
//
//            webView.loadUrl("http://192.168.56.1:8080/WebServer/AddressWeb.html");
//
//        }

//        public class AndroidBridge {
//            @JavascriptInterface
//            public void setAddress(final String arg1, final String arg2, final String arg3) {
//                final Handler handler = new Handler();
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (handler != null) {
//                            handler.removeMessages(0);
//                            init_webView();
//                            Intent intent = new Intent();
//                            intent.putExtra("result", arg2 + " " + arg3);
//                            setResult(RESULT_OK, intent);
//                            finish();
//                        }
//                        // WebView를 초기화 하지않으면 재사용할 수 없음
//                    }
//                });
//            }
//        }
    }
}
