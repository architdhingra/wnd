package com.wnd.myapp.lenovate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class livepreview1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livepreview1);
    }

    public void show(View v){
        Intent i = new Intent(livepreview1.this, livepreview_main.class);
        i.putExtra("url", "http://testlite.decorastudio.com/kajaria/DSLite/src/custom.html?brand=kajaria");
        startActivity(i);
    }
}
