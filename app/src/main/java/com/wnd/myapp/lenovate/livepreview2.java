package com.wnd.myapp.lenovate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class livepreview2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livepreview2);
    }

    public void show(View v){
        Intent i = new Intent(livepreview2.this, livepreview_main.class);
        i.putExtra("url", "https://www.google.com/maps/@28.4687817,77.0517601,3a,17.6y,103.29h,73.33t/data=!3m4!1e1!3m2!1sWigSGh6cR7AAAAGu2hX5yg!2e0?hl=en");
        startActivity(i);
    }
}
