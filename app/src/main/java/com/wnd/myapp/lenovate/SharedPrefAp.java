package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Sachin Kharb on 9/13/2016.
 */public class SharedPrefAp {

    SharedPreferences sharepreferences;

    public static SharedPrefAp instance = null;

    public static SharedPrefAp getInstance()
    {

        if (instance == null) {
            synchronized (SharedPrefAp.class) {
                instance = new SharedPrefAp();
            }
        }
        return instance;
    }
    //type , 1: google, 2 :facebook
    public void saveISLogged_IN(Context context, Boolean isLoggedin, int TypeLogin) {
        sharepreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharepreferences.edit();
        editor.putBoolean("IS_LOGIN", isLoggedin);
        editor.putInt("LOGIN_TYPE",TypeLogin);
        editor.commit();
    }
    public void SetNameAndPhotu(Context context, Boolean isLoggedin, String name , String enail, String photoUri)
    {sharepreferences = PreferenceManager
            .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharepreferences.edit();
        editor.putString("name", name);
        editor.putString("email_signed", enail);
        editor.putString("photuUri", photoUri);
        editor.commit();
    }

    public void RemovePhotuAndALL(Context context)
    {sharepreferences = PreferenceManager
            .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharepreferences.edit();
        editor.remove("name");

        editor.remove("email_signed");
        editor.remove("photuUri");
        editor.commit();}

    public String getName(Context c)
    {
        sharepreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        return sharepreferences.getString("name", "User");
    }
 public String getEMail(Context c)
    {
        sharepreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        return sharepreferences.getString("email_signed", "user@woodndecor.com");
    }
 public String getPhotUri(Context c)
    {
        sharepreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        return sharepreferences.getString("photuUri",null);
    }
    public boolean getISLogged_IN(Context context) {
        sharepreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharepreferences.getBoolean("IS_LOGIN", false);
    }

    public int getType(Context c)
    {sharepreferences = PreferenceManager
            .getDefaultSharedPreferences(c);
        return sharepreferences.getInt("TYPE_LOGIN",1);}

}