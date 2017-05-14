package com.wnd.myapp.lenovate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.wnd.myapp.lenovate.ProductInfo.getBitmapFromURL;
import static com.wnd.myapp.lenovate.SubCategoryGallery.s;


public class zoomview extends AppCompatActivity {

    int prodId;
    TestFragmentAdapter adapter;
    String from;
    int success=0;
    private ProgressDialog pDialog;
    TextView proddesc, prodname;
    TouchImageView zoomableimage;
    AVLoadingIndicatorView av;
    RelativeLayout zoomimgcontainer;
    JSONObject jsonObject = null;
    TextView removebtn,savecollectionsbtn,usernote_text;
    LinearLayout more;
    private BottomSheetBehavior mBottomSheetBehavior, bottomSheet_save;
    LinearLayout userNote;
    String item_photo;
    RelativeLayout overlay;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sharedPreferences = zoomview.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        overlay = (RelativeLayout) findViewById(R.id.overlay);
        removebtn = (TextView) findViewById(R.id.removebtn);
        savecollectionsbtn = (TextView) findViewById(R.id.savebtn);
        userNote = (LinearLayout) findViewById(R.id.note_byuser);

        av = (AVLoadingIndicatorView) findViewById(R.id.aviHome);

        from = getIntent().getStringExtra("gallery");

        Log.d("zoomview","from: " + from);

        if(from.equals("mycollections")){
            removebtn.setVisibility(View.VISIBLE);
            userNote.setVisibility(View.VISIBLE);
            Log.d("from","mycollections");
            usernote_text = (TextView) findViewById(R.id.note_text);
        }else{
            Log.d("from","gallery");
            savecollectionsbtn.setVisibility(View.VISIBLE);
        }


        zoomimgcontainer = (RelativeLayout) findViewById(R.id.zoomimglayout);
        prodId = getIntent().getIntExtra("itemId", -1);
        Log.d("zomeview", "Item id: " + prodId);

        proddesc = (TextView) findViewById(R.id.desc);
        zoomableimage = (TouchImageView) findViewById(R.id.prodimg);


        View bottomSheet = findViewById( R.id.bottom_sheet );
        View bottomsheet2 = findViewById(R.id.bottom_sheet_save);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet_save = BottomSheetBehavior.from(bottomsheet2);

        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheet_save.setPeekHeight(0);
        bottomSheet_save.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Log.d("zoomview","bootmsheet state: "+ mBottomSheetBehavior.getState());
        more = (LinearLayout) findViewById(R.id.more);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                    overlay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

        bottomSheet_save.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                    overlay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

        getProduct(prodId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.productinfo, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    onBackPressed();
                    return true;
            case R.id.product_share:
                if(success==1) {
                    Log.d("menu", "success1");
                    shareImage(item_photo);
                }
                else{
                    Toast.makeText(this, "Let the details load first", Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
    }


    public void getProduct(final int prodId) {
        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/item_details.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    String arr = jsonObject.getString("itemname");
                    String price = jsonObject.getString("itemprice");
                    String desc = jsonObject.getString("itemdesc");
                    String photo = jsonObject.getString("itemimg");
                    String note = jsonObject.getString("itemnote");            // user note about the image

                    setdetails(desc, photo, note);

                    jsonObject=null;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("itemid", String.valueOf(prodId));
                params.put("email", sharedPreferences.getString("userEmail", "abc@def.com") );
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

    public void setdetails(String desc, String photo, String notex) {

        av.hide();
        Log.d("setdetails","settings desc and note: "+ desc +"  ::  "+ notex);
        proddesc.setText(desc);
        Log.d("prodesc","prodescset");
        item_photo = photo;

        if(from.equals("mycollections")) {
            if (notex.equals("null") || notex.isEmpty() || notex.length() < 0) {
                userNote.setVisibility(View.GONE);
            } else {
                Log.d("els", "ekse");
                usernote_text.setText(notex);
            }
        }
        CoordinatorLayout parent = (CoordinatorLayout) zoomimgcontainer.getParent();
        parent.setBackgroundColor(getResources().getColor(android.R.color.black));
        zoomimgcontainer.setVisibility(View.VISIBLE);

        Picasso.with(getApplication())      //setting zoomable image
                .load(photo)
                .into(zoomableimage);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    public void moredesc(View v){
        Log.d("zoomview","showing moredesc botton sheet");

            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                overlay.setVisibility(View.VISIBLE);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
    }

    public void SaveCollections_Done(View v){
        Log.d("zoomview","save botton sheet");

        if (bottomSheet_save.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheet_save.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            overlay.setVisibility(View.GONE);
            bottomSheet_save.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        pDialog = new ProgressDialog(zoomview.this);
        pDialog.setMessage("Saving Image in MyCollections..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        /*--------------- your code for saving image in my collections of the user ---------------*/

        EditText MyCollection_note = (EditText) findViewById(R.id.MyCollection_note);
        String temp = MyCollection_note.getText().toString();


        // variable to be used here: temp | prodid | userEmail


        Save_MyCollections(prodId, sharedPreferences.getString("userEmail", "abc@def.com"), temp);

    }

    public void SaveCollections_save(View v){
        Log.d("zoomview","show save bottom sheet");

        if (bottomSheet_save.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            overlay.setVisibility(View.VISIBLE);
            bottomSheet_save.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheet_save.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void SaveCollections_remove(View v){
        Log.d("zoomview","remove image");

        // remove image from my collections

        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/deleteCollection.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    jsonObject = new JSONObject(response);

                    int status = jsonObject.getInt("success");

                    if(status == 1){

                        Toast.makeText(zoomview.this, "Image removed from Your Collection", Toast.LENGTH_SHORT).show();

                    }

                    jsonObject=null;
                    finish();

                } catch (JSONException e) {
                    pDialog.hide();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("itemid", String.valueOf(prodId));
                params.put("email", sharedPreferences.getString("userEmail", "abc@def.com"));
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

    public void Save_MyCollections(final int prodId, final String usermail, final String note) {
        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/saveCollection.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    jsonObject = new JSONObject(response);

                    int status = jsonObject.getInt("success");

                    if(status == 1){

                        Toast.makeText(zoomview.this, "Saved Image in Your My Collections", Toast.LENGTH_SHORT).show();

                    }

                    pDialog.hide();

                    jsonObject=null;
                } catch (JSONException e) {
                    pDialog.hide();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("itemid", String.valueOf(prodId));
                params.put("email", usermail);
                params.put("note", note);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

    public void shareImage(String url) {
        Bitmap icon = getBitmapFromURL(url);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(android.content.Intent.EXTRA_TEXT,"From Lenovate");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
        startActivity(Intent.createChooser(share, "Share Image"));

    }

    public void enquire(View v){
        Intent i = new Intent(zoomview.this, enquiry.class);
        i.putExtra("url",item_photo);
        i.putExtra("from","zoomview");
        i.putExtra("item_name",s);
        startActivity(i);
    }

    public void close_filter(View v){
        overlay.setVisibility(View.GONE);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


}
