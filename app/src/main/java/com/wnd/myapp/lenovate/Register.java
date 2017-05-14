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

public class Register extends AppCompatActivity {
   private String phone;
    private String email;
    private String passwd;
    private String address;
    SharedPreferences sharedPreferences;
    private String name;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = Register.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final RequestQueue rq =VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        Button b= (Button) findViewById(R.id.register);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(Register.this);
                pDialog.setMessage("Registering...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.hide();
                boolean regist=true;
                EditText e1= (EditText ) findViewById(R.id.nameT);
                if(e1.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Name can't be empty",Toast.LENGTH_LONG).show(); regist=false;}

                name = e1.getText().toString();
                e1=(EditText) findViewById(R.id.emailT);
                if(e1.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "enter Email",Toast.LENGTH_LONG).show(); regist=false;}
                email = e1.getText().toString();

                e1=(EditText) findViewById(R.id.addressT);

                if(e1.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "enter Address",Toast.LENGTH_LONG).show(); regist=false;}

                address = e1.getText().toString();

                e1=(EditText) findViewById(R.id.phoneT);
                if(e1.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Phone number not Entered",Toast.LENGTH_SHORT).show(); regist=false;}

                phone = e1.getText().toString();

                e1=(EditText) findViewById(R.id.passT);
                if(e1.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Password can't be empty",Toast.LENGTH_SHORT).show();regist=false;}

                passwd = e1.getText().toString();
                if(regist){
                pDialog.show();
            //Request POST


                StringRequest srQ = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/signup.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{JSONObject jObj = new JSONObject(response);
                                int suc = 0;

                                    suc = jObj.getInt("success");

                                if(suc==1) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("userLogin", true);
                                    editor.putString("userEmail", email);
                                    editor.putString("userName", name);
                                    editor.putString("userAddr", address);
                                    editor.putString("userNumber", phone);
                                    editor.commit();
                                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(Register.this, Dashboard.class));
                                    finish();
                                }else{
                                    pDialog.hide();
                                    Toast.makeText(getApplicationContext(), "Error: " + jObj.getString("message"), Toast.LENGTH_LONG).show();
                                    }

                                }catch (JSONException e) {
                                    pDialog.hide();
                                    Toast.makeText(getApplicationContext(), "Error while Registering", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "Error While Registering", Toast.LENGTH_LONG).show();
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() {
                        // Post params to login url
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("name",name);
                        params.put("password",passwd);
                        params.put("email",email);
                        params.put("number",phone);
                        params.put("address",address);

                        return params;
                    }

                };
            rq.add(srQ);
            }
            else
            Toast.makeText(getApplicationContext(), "One or more required fields are empty",Toast.LENGTH_LONG).show();
            }
        });
    }

}
