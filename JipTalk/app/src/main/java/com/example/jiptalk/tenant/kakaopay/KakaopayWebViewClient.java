package com.example.jiptalk.tenant.kakaopay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jiptalk.ui.building.JusoWebViewActivity;

import java.net.URISyntaxException;

public class KakaopayWebViewClient extends WebViewClient {
    private Activity activity;
    public static final String INTENT_PROTOCOL_START = "intent:";
    public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
    public static final String INTENT_PROTOCOL_END = ";end;";
    public static final String INTENT_PROTOCOL_URI = "?url=";
    public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

    public KakaopayWebViewClient(Activity activity) {
        this.activity = activity;

    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("===", "shouldOverrideUrlLoading");
//        if (url.startsWith("intent:")) {
//            Log.d("===", "kakaotalk: shouldOverrideUrlLoading");
//            try {
//                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
//                Intent existPackage = activity.getPackageManager().getLaunchIntentForPackage(intent.getPackage());
//                if (existPackage != null) {
//                    activity.startActivity(intent);
//                } else {
//                    Intent marketIntent = new Intent(Intent.ACTION_VIEW);
//                    marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
//                    activity.startActivity(marketIntent);
//                }
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        else {
//            view.loadUrl(cfuntion.urlValidation(url));
//        }


        if (url.startsWith(INTENT_PROTOCOL_START)) {
            final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
            final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
            if (customUrlEndIndex < 0) {
                return false;
            } else {
                final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                Log.d("===", "customUrl : " + customUrl);
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                } catch (ActivityNotFoundException e) {
                    final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                    final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                    final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                }
                return true;
            }
        } else {
            return false;
        }
//        return false;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        //... 구현
        return true;

    }

}
