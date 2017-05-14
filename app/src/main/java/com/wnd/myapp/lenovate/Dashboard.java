package com.wnd.myapp.lenovate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.wnd.myapp.lenovate.externalDecor.CircleImageView;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class Dashboard extends AppCompatActivity
        implements chatFragment.OnFragmentInteractionListener,
        gallery.OnFragmentInteractionListener,
        home_fragment.OnFragmentInteractionListener, shopFragment.OnFragmentInteractionListener, blogfrag.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    public String SearchedString = "";
    ViewPager viewPager;
    private TabLayout tabLayout;
    SharedPreferences sharedPref;
    public boolean Searched = false;
    private ViewPager viewPager_tab;
    ViewPagerAdapter adapter;
    Swip_Image swip_image;


    //Gpt FaceBook

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }


        sharedPref = Dashboard.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View header = navigationView.getHeaderView(0);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API).build();


        String s = sharedPref.getString("userName", "Name");
        String em = sharedPref.getString("userEmail", "user@email.com");
        TextView tv = (TextView) header.findViewById(R.id.Tnameofuser);
        tv.setText(s);
        tv = (TextView) header.findViewById(R.id.Tmobno);
        tv.setText(em);
        //String imageUri = sharedPref.getPhotUri(this);
        CircleImageView img = (CircleImageView) header.findViewById(R.id.IProfileImage);
        loadBackdrop(img);


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        swip_image = new Swip_Image(this);

        viewPager_tab = (ViewPager) findViewById(R.id.view_pager_tabs);

        setupViewPager(viewPager_tab);
        tabLayout = (TabLayout) findViewById(R.id.tab_main);
        tabLayout.setupWithViewPager(viewPager_tab);

    }

    public void edit(View v) {
        startActivity(new Intent(Dashboard.this, profile.class));
    }

    private void loadBackdrop(ImageView imgView) {
        Bitmap bitmap = null;
        Log.d("Dashboard","LoadBackDrop");
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/Lenovate");
        File file = new File(directory, "profile.jpg"); //or any other format supported
        Log.d("file path: ", file.toString());

        if(file.exists() && file.canRead()) {
            Log.d("Dashboard","Profile image exists");

            try {
                bitmap = BitmapFactory.decodeFile(file.toString());
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));
        }
    }

    private void setupViewPager(ViewPager viewPager) {


        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new home_fragment(), "Home");

        Bundle bundle = new Bundle();
        bundle.putString("type", "inspi");
        gallery glry = new gallery();
        glry.setArguments(bundle);
        adapter.addFragment(glry, "Inspiration");


        Bundle bundle1 = new Bundle();
        bundle1.putString("type", "furni");
        gallery glry1 = new gallery();
        glry1.setArguments(bundle1);
        adapter.addFragment(glry1, "Furniture");

        Bundle bundle2 = new Bundle();
        bundle2.putString("type", "shop");
        gallery glry2 = new gallery();
        glry2.setArguments(bundle2);
        adapter.addFragment(glry2, "Shop");

        adapter.addFragment(new blogfrag(), "blog");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

    }

    //Handling backpresses to inflate or deflate custon vuiws in Faragment, especially Gallery
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            showDialogTofinish();

        }
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint("Search in Inspiration");
        changeSearchViewTextColor(searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Searched = true;
                SearchedString = query;

                Log.d("Current frag:  ", String.valueOf(viewPager_tab.getCurrentItem()));
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }

                myActionMenuItem.collapseActionView();

                Intent i = new Intent(Dashboard.this, SearchResult.class);
                i.putExtra("query", query);
                i.putExtra("frag", String.valueOf(viewPager_tab.getCurrentItem()));
                startActivity(i);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        return true;
    }

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimary));
                ((TextView) view).setHintTextColor(getResources().getColor(R.color.colorPrimary));
                //((TextView) view).setPadding(0,2,0,0);
                return;

            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.cart_menu) {
            Intent i = new Intent(Dashboard.this, Cart.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            VisitorInfo visitorData = new VisitorInfo.Builder()
                    .name(sharedPref.getString("userName", "abc"))
                    .note("About: information regarding the query of the user")
                    .email(sharedPref.getString("userEmail", "abc@def.com"))
                    .phoneNumber(sharedPref.getString("userNumber", null))
                    .build();

            ZopimChat.setVisitorInfo(visitorData);

            startActivity(new Intent(getApplicationContext(), ZopimChatActivity.class));
        }
        if (id == R.id.nav_dashboard) {
            Intent i = new Intent(Dashboard.this, Dashboard.class);
            startActivity(i);

        } else if (id == R.id.nav_profile) {
            Intent i = new Intent(Dashboard.this, profile.class);
            startActivity(i);

        }
        else if (id == R.id.nav_getintouch) {
            Intent i = new Intent(Dashboard.this, GetinTouch.class);
            startActivity(i);

        }else if (id == R.id.nav_mycollections) {
            Intent i = new Intent(Dashboard.this, mycollections.class);
            startActivity(i);

        }
        else if (id == R.id.nav_enquire) {
            Intent i = new Intent(Dashboard.this, enquiry.class);
            i.putExtra("from","enquire_all");
            startActivity(i);

        }
        /*else if (id == R.id.live1) {
            Intent i = new Intent(Dashboard.this, livepreview1.class);
            i.putExtra("from","enquire_all");
            startActivity(i);

        }else if (id == R.id.live2) {
            Intent i = new Intent(Dashboard.this, livepreview2.class);
            i.putExtra("from","enquire_all");
            startActivity(i);

        }*/
        else if (id == R.id.nav_history) {

            ///////////////////////PAYMENT TEST//////////////////////////////////////////////////////////////
           /*Intent intent = new Intent(this,WebViewActivity.class);
           intent.putExtra(AvenuesParams.ACCESS_CODE, "AVKJ67DK99BY18JKYB");
           intent.putExtra(AvenuesParams.MERCHANT_ID, "70551");
           intent.putExtra(AvenuesParams.ORDER_ID, String.valueOf(ServiceUtility.randInt(0, 9999999)));
           intent.putExtra(AvenuesParams.CURRENCY, "INR");
           intent.putExtra(AvenuesParams.AMOUNT, "50");
           intent.putExtra(AvenuesParams.REDIRECT_URL, "http://www.woodndecor.in/ccavResponseHandler.php");
           intent.putExtra(AvenuesParams.CANCEL_URL,"http://www.woodndecor.in/ccavResponseHandler.php");
           intent.putExtra(AvenuesParams.RSA_KEY_URL,"http://www.woodndecor.in/GetRSA.php");

           startActivity(intent);*/

            startActivity(new Intent(Dashboard.this, History.class));


        } else if (id == R.id.nav_tnc) {
            startActivity(new Intent(Dashboard.this, tnc.class));

        } else if (id == R.id.nav_privacypolicy) {
            startActivity(new Intent(Dashboard.this, privacy_policy.class));

        } else if (id == R.id.nav_share) {
            String shareBody = "WoodnDecor: Visit us on www.woodndecor.co.in";
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "www.woodndecor.co.in");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, ("Share Using ")));

        } else if (id == R.id.nav_logout) {
            logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void showDialogTofinish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    //Logout function
    private void logout() {


        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (mGoogleApiClient.isConnected()) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            if (status.isSuccess())
                                                clearIdAndRestart();
                                        }
                                    });

                        } else {
                            LoginManager.getInstance().logOut();
                            clearIdAndRestart();
                        }
                    finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void clearIdAndRestart() {
        SharedPrefAp sharedPref;
        sharedPref = SharedPrefAp.getInstance();

        SharedPreferences sharedPreferences = Dashboard.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("userLogin", false);
        editor.putString("userName", null);
        editor.putString("userEmail", null);
        editor.putString("userNumber", null);

        editor.commit();
        sharedPref.RemovePhotuAndALL(getApplicationContext());
        sharedPref.saveISLogged_IN(getApplicationContext(), false, 1);
        //Puting the value false for loggedin

        //Starting login activity
        Intent intent = new Intent(Dashboard.this, LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void openEnquire(View v) {

        Intent i = new Intent(Dashboard.this, enquiry.class);
        i.putExtra("from","enquire_all");
        startActivity(i);

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }
}
