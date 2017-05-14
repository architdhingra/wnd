package com.wnd.myapp.lenovate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class esLogin extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {



    private SignInButton siB;
    private GoogleSignInOptions gSO;

    private GoogleApiClient mGoogleAPiCLIENT;

    private int RC_SIGN_IN =100;

    private TextView tVNAME;
    private TextView tv_EMAIL;
    private NetworkImageView profPhoto;

    private ImageLoader imgLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_es_login);

        tVNAME = (TextView) findViewById(R.id.textViewName);
        tv_EMAIL = (TextView) findViewById(R.id.textViewEmail);
        profPhoto = (NetworkImageView) findViewById(R.id.profileImage);


        gSO =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();


        siB= (SignInButton ) findViewById(R.id.sign_in_button);
        siB.setSize(SignInButton.SIZE_WIDE);
        siB.setScopes(gSO.getScopeArray());


        mGoogleAPiCLIENT = new GoogleApiClient.Builder(this).enableAutoManage(this,this)
                                .addApi(Auth.GOOGLE_SIGN_IN_API,gSO)
                                .build();

        siB.setOnClickListener(this);

    }

    private void SignIn()
    {
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
            handleSignInResult(result);
        }
    }

    private  void  handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess())
        {  String photuUri=null;
            GoogleSignInAccount acc = result.getSignInAccount();
            tVNAME.setText(acc.getDisplayName());
            tv_EMAIL.setText(acc.getEmail());
            imgLoader = VolleySingelton.getInstance(this.getApplicationContext()).getImageLoader();
           if(acc.getPhotoUrl()!=null)
           {imgLoader.get(acc.getPhotoUrl().toString(), ImageLoader.getImageListener(profPhoto, R.mipmap.ic_launcher, R.mipmap.ic_launcher));

            profPhoto.setImageUrl(acc.getPhotoUrl().toString(),imgLoader);
             photuUri =  acc.getPhotoUrl().toString();
           }

            SharedPrefAp sharedPref;
            sharedPref = SharedPrefAp.getInstance();

            sharedPref.saveISLogged_IN(this, true,1);
           sharedPref.SetNameAndPhotu(this,true,acc.getDisplayName(),acc.getEmail(), photuUri!=null? photuUri :"null"  );

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

}
