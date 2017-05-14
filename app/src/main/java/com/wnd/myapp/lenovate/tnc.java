package com.wnd.myapp.lenovate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class tnc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnc);
        WebView webView = (WebView) findViewById(R.id.textarea);
        webView.loadUrl("file:///android_asset/tnc.html");
    }
}
