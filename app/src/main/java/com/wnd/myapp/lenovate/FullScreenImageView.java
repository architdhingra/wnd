package com.wnd.myapp.lenovate;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wnd.myapp.lenovate.externalDecor.FullScreenImageAdapter;
import com.wnd.myapp.lenovate.externalDecor.Utils;

import java.util.ArrayList;

public class FullScreenImageView extends AppCompatActivity {
    private Utils utils;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_view);

        /*viewPager = (ViewPager) findViewById(R.id.pager_fullScreen);

        utils = new Utils(getApplicationContext());

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);

        adapter = new FullScreenImageAdapter(FullScreenImageView.this,
                imagePathsAll());

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);*/
    }


   /* public ArrayList<Integer> imagePathsAll() {
        ArrayList<Integer> arr= new ArrayList<>();
        arr.add(R.drawable.borderbutt);
        arr.add(R.drawable.img1);
        arr.add(R.drawable.img4);
        arr.add(R.drawable.img3);
        arr.add(R.drawable.tempimg);


        return arr;
    }*/
}