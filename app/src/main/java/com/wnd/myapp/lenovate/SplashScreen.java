package com.wnd.myapp.lenovate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by SAchin Kharb on 8/5/2016.
 */
public class SplashScreen extends Activity {

    Thread splashTread;
    ImageView imageView;
    Context context;
    Boolean isloggedin = null, firstime=null;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        context = getApplicationContext();
        sharedPreferences = SplashScreen.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        isloggedin = sharedPreferences.getBoolean("userLogin", false);

        firstime = sharedPreferences.getBoolean("firstTime",false);

        imageView = (ImageView) findViewById(R.id.splash_image_viw);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_screen);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if(firstime==false){
                    startActivity(new Intent(SplashScreen.this, DefaultIntro.class));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("firstTime",true);
                    editor.commit();
                    finish();

                }else {
                    if (isloggedin == true) {
                        startActivity(new Intent(SplashScreen.this, Dashboard.class));
                        finish();
                    } else if (isloggedin == false) {
                        startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                        finish();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
