package com.wnd.myapp.lenovate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class loginAct extends AppCompatActivity {
    private ProgressDialog pDialog;

    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final RequestQueue rq =VolleySingelton.getInstance(getApplicationContext()).getRequestQ();
        final EditText user= (EditText ) findViewById(R.id.uname_text);
        final EditText pwd= (EditText ) findViewById(R.id.pswd_text);


        Button fab = (Button) findViewById(R.id.loginButt);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(loginAct.this);
                pDialog.setMessage("Logging In..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
                StringRequest sRq = new StringRequest(Request.Method.POST,
                        "http://www.woodndecor.in/login.php", new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jObj = new JSONObject(response);
                             int suc = jObj.getInt("success");
                           if(suc==1) {
                               pDialog.hide();
                               Toast.makeText(getApplicationContext(),"Logged in !", Toast.LENGTH_LONG).show();
                               //Creating a shared preference
                               SharedPreferences sharedPreferences = loginAct.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                               //Creating editor to store values to shared preferences
                               SharedPreferences.Editor editor = sharedPreferences.edit();

                               //Adding values to editor
                               editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                               editor.putString(Config.EMAIL_SHARED_PREF, user.getText().toString());

                               //Saving values to editor
                               editor.commit();

                               Intent i= new Intent(loginAct.this,Dashboard.class);
                             i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               startActivity(i);
                               finish();

                           }
                        } catch (JSONException e) {
                            pDialog.hide();
                            e.printStackTrace();
                            //For checking error
                            Toast.makeText(getApplicationContext(),"Error : "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "Unable to Login : "+ error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Post params to login url
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("uname", user.getText().toString());
                        params.put("pname", pwd.getText().toString());

                        return params;
                    }

                };
                rq.add(sRq);
            }

        });

    Button b2= (Button) findViewById(R.id.registButt);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(loginAct.this,Register.class);

                startActivity(i);
            }
        });



    }

}
