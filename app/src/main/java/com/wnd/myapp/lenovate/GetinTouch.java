package com.wnd.myapp.lenovate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetinTouch extends AppCompatActivity {

    SharedPreferences sharedPref;
    EditText fullname, email, message;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_getin_touch);

        fullname = (EditText) findViewById(R.id.fullname);
        email = (EditText) findViewById(R.id.email);
        message = (EditText) findViewById(R.id.message);

        progressDialog = new ProgressDialog(GetinTouch.this);
        progressDialog.setMessage("Loading");


        sharedPref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    public void callus(View v) {
        Log.d("getintouch", "callus button clicked");

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+911234567891"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(GetinTouch.this, "Permission not granted",Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);

    }

    public void submit(View v){

        Log.d("getintouch", "submit button clicked");

        String name = fullname.getText().toString();
        String emailx = email.getText().toString();
        String msg = message.getText().toString();

        if(name.length()>0 && emailx.length()>0 && msg.length()>0 && emailx.contains("@")){
            SubmitQuery(name, emailx, msg);
        }else{
            Toast.makeText(GetinTouch.this, "Some Fields Missing, Please enter all the Missing fields and try again!",Toast.LENGTH_SHORT).show();

        }

    }

    JSONObject jsonObject;

    public void SubmitQuery(final String username, final String usermail, final String messagex) {
        progressDialog.show();
        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/getInTouch.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int success =0;
                    jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    Log.d("response", response.toString());
                    if (success == 1) {
                        progressDialog.hide();
                        showDialogTofinish();
                        Log.d("Reponse", "Query submitted Successfully");

                    } else {
                        progressDialog.hide();
                        Toast.makeText(getApplication(), "Error submitting", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    progressDialog.hide();
                    e.printStackTrace();
                    Toast.makeText(getApplication(), "Something went wrong, Please Try Again!", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getApplication(), "Oops! An Error Occured", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", username);
                params.put("email", usermail);
                params.put("msg", messagex);
                return params;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

    public void showDialogTofinish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Query has been submitted successfully, We will get back to your shortly..")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
