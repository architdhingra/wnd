package com.wnd.myapp.lenovate;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.zopim.android.sdk.api.ZopimChat;

/**
 * Created by Dhruv Sharma on 8/23/2016.
 */
public class MyApplication extends MultiDexApplication {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        super.onCreate();
        MultiDex.install(this);
        ZopimChat.init("4ZLimEso6K0AI01mN9KICEbmHI0pO1QK");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public static MyApplication getmInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

}
