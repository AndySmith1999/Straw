package com.example.straw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class aboutActivity extends Activity {
    private WebView webView;

    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutview);
        webView = (WebView) findViewById(R.id.wv_about);

        webView.getSettings().setJavaScriptEnabled(true); // 开启javascript支持
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setAllowFileAccess(true);
//        webView.addJavascriptInterface(this, "changeVersionJs");

        // 根据语言加载不同的页面，实现多语言适配
        if (getResources().getConfiguration().locale.getLanguage().equals("zh")) {
            webView.loadUrl("file:///android_asset/about_cn.html");
        } else {
            webView.loadUrl("file:///android_asset/about.html");
        }

    }
}