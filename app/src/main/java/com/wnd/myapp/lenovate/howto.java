package com.wnd.myapp.lenovate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

public class howto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_howto);
        WebView webView = (WebView) findViewById(R.id.textarea);
        webView.loadUrl("file:///android_asset/howto.html");

    }

}
