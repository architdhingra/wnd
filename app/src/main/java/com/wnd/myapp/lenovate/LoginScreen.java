package com.wnd.myapp.lenovate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LoginScreen extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private SignInButton siB;
    private GoogleSignInOptions gSO;

    private static GoogleApiClient mGoogleAPiCLIENT;
    private ProgressDialog pDialog;
    private int RC_SIGN_IN = 100;
    SharedPrefAp sharedPref;

    Boolean isloggedin = null;
    //FaceBook
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = LoginScreen.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        isloggedin = sharedPreferences.getBoolean("userLogin", false);
        if (isloggedin) {
            startActivity(new Intent(LoginScreen.this, Dashboard.class));
            finish();
        }

        editor = sharedPreferences.edit();

        setContentView(R.layout.login_screen);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Login");*/


        final EditText user = (EditText) findViewById(R.id.uname_text);
        final EditText pwd = (EditText) findViewById(R.id.pswd_text);
        Button fab = (Button) findViewById(R.id.loginButt);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLogin(user.getText().toString(), pwd.getText().toString(), "xyzz");
            }
        });
        Button b2 = (Button) findViewById(R.id.registButt);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginScreen.this, Register.class);

                startActivity(i);
                finish();
            }
        });

        gSO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        siB = (SignInButton) findViewById(R.id.sign_in_button);
        siB.setSize(SignInButton.SIZE_WIDE);
        siB.setScopes(gSO.getScopeArray());


        mGoogleAPiCLIENT = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gSO)
                .build();

        siB.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.Fb_login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    //   info.setText("Hi, " + object.getString("name")+"  "+ object.getString("email"));
                                    // sharedPref.saveISLogged_IN(getApplicationContext(), true,2);
                                    // sharedPref.SetNameAndPhotu(getApplicationContext(), true, object.getString("name"), object.getString("email"), "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large");
                                    editor.putBoolean("userLogin", true);
                                    editor.putString("userEmail", object.getString("email"));
                                    editor.putString("userName", object.getString("name"));
                                    //Saving values to editor
                                    editor.commit();
                                    Toast.makeText(getApplicationContext(), " Hi  " + object.getString("name"), Toast.LENGTH_LONG).show();
                                    customLogin(object.getString("email"), "197825zaq", object.getString("name"));

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginScreen.this, "Login cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginScreen.this, "Login Error!!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return
                        super.onOptionsItemSelected(item);
        }
    }


    private void SignIn() {
        Intent signINintent = Auth.GoogleSignInApi.getSignInIntent(mGoogleAPiCLIENT);
        startActivityForResult(signINintent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            Log.d("onActivityResult", "result: " + result.getStatus() + " :: " + requestCode + " :: " + resultCode);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {

        Log.d("handlesignin result", "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            String photuUri = null;
            try {
                GoogleSignInAccount acc = result.getSignInAccount();

                photuUri = acc.getPhotoUrl().toString();

                                 /*sharedPref.saveISLogged_IN(this, true,1);
                                 sharedPref.SetNameAndPhotu(this, true, acc.getDisplayName(), acc.getEmail(), photuUri != null ? photuUri : "null");
                                 */

                customLogin(acc.getEmail(), "197825zaq", acc.getDisplayName());
                //sendId(acc.getDisplayName().toString(), acc.getEmail().toString());

            } catch (Exception e) {

            }
        } else {
            Toast.makeText(this, "Error During Signing in", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == siB) {
            //Calling signin
            SignIn();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static void Signout(View v) {
        if (v.getId() == R.id.sign_out_button) {
            Auth.GoogleSignInApi.signOut(mGoogleAPiCLIENT).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                    }
                }
            });
        }
    }

    public void sendId(final String name, final String email) {
        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();
        StringRequest srQ = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/signup.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int suc = 0;

                            suc = jObj.getInt("success");

                            if (suc == 1) {
                                //pDialog.hide();
                                editor.putBoolean("userLogin", true);
                                editor.putString("userEmail", email);
                                editor.putString("userName", name);
                                //editor.putString("userName", object.getString("name"));
                                //Saving values to editor
                                editor.commit();
                                Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(LoginScreen.this, Dashboard.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            } else {
                                //pDialog.hide();
                                Toast.makeText(getApplicationContext(), "Error while Registering", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //pDialog.hide();
                            Toast.makeText(getApplicationContext(), "Error while Registering", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //pDialog.hide();
                Toast.makeText(getApplicationContext(), "Error While Registering", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Post params to login url

                Log.d("send id", "params: " + name + " :: " + email);
                Map<String, String> params = new HashMap<String, String>();

                params.put("name", name);
                params.put("password", "197825zaq");
                params.put("email", email);
                params.put("number", "0000000000");
                params.put("address", "gfhgbjbjz");

                return params;
            }

        };
        rq.add(srQ);


    }

    public void customLogin(final String email, final String pwd, final String name) {
        Log.d("Custom Login", "CL");
        final RequestQueue rq = VolleySingelton.getInstance(LoginScreen.this).getRequestQ();

        pDialog = new ProgressDialog(LoginScreen.this);
        pDialog.setMessage("Logging In..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        StringRequest sRq = new StringRequest(Request.Method.POST,
                "http://www.woodndecor.in/login.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Custom Login1", response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);
                    int suc = jObj.getInt("success");
                    Log.d("Custom Login", "CL1");
                    if (suc == 1) {
                        pDialog.hide();

                        String userNumber = jObj.getString("usernumber");
                        String userName = jObj.getString("username");

                        Toast.makeText(getApplicationContext(), "Logged in !", Toast.LENGTH_LONG).show();
                        //Creating a shared preference
                        //Adding values to editor
                        editor.putBoolean("userLogin", true);
                        editor.putString("userEmail", email);
                        editor.putString("userName", userName);
                        editor.putString("userNumber", userNumber);
                        Log.d("Custom Login", "CL2");
                        //editor.putString("userName", object.getString("name"));
                        //Saving values to editor
                        editor.commit();
                        //Adding values to editor

                        Intent i = new Intent(LoginScreen.this, Dashboard.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    } else {
                        if (pwd == "197825zaq") {
                            Log.d("Custom Login", "CL5");
                            sendId(name, email);
                            Log.d("Custom Login", "CL3");
                        }else if(suc==0){
                            pDialog.hide();
                            Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    pDialog.hide();
                    e.printStackTrace();
                    Log.d("Custom Login", "CL4");
                    //For checking error
                    Toast.makeText(getApplicationContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                Log.d("Custom Login", "CL6");
                Toast.makeText(getApplicationContext(), "Unable to Login : " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("uname", email);
                params.put("pname", pwd);
                Log.d("Custom Login", params.toString());
                return params;
            }

        };
        sRq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sRq);
    }


}
