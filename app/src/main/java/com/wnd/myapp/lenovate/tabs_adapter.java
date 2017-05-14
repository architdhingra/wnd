package com.wnd.myapp.lenovate;

import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by Sachin Kharb on 8/29/2016.
 */
public class tabs_adapter extends PagerAdapter {
    Fragment[] a ;
    @Override
    public int getCount() {
        return a.length;
    }

    public tabs_adapter(Fragment[] a) {

        super();
        this.a = a;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
