package com.example.buma_p5m.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.buma_p5m.R;

public class PrivacyPolicy extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/bumaprivacypolicy.htm");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("P5M Privacy Policy");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}