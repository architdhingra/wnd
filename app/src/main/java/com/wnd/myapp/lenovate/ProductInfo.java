package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.wang.avi.AVLoadingIndicatorView;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.wnd.myapp.lenovate.SubCategoryGallery.s;

public class ProductInfo extends AppCompatActivity {
   int prodId;
    int success=0;
    ViewPager mPager;
    TestFragmentAdapter adapter;
    TextView proddesc,prodname,prodprice;
    LinearLayout addcart;
    AVLoadingIndicatorView avi;
    RelativeLayout overlay;
    RelativeLayout relLayout;
    private BottomSheetBehavior mBottomSheetBehavior;
    SharedPreferences sharedPreferences, sharedPref;
    String[] url;
    String type, item_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addcart = (LinearLayout) findViewById(R.id.addcart);
        prodId = getIntent().getIntExtra("itemId",-1);
        Log.d("productinfo","Item id: " + prodId);
        overlay = (RelativeLayout) findViewById(R.id.overlay);
        avi = (AVLoadingIndicatorView) findViewById(R.id.aviHome);
        sharedPreferences = getApplicationContext().getSharedPreferences("cart", Context.MODE_PRIVATE);
        sharedPref = ProductInfo.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        relLayout = (RelativeLayout) findViewById(R.id.parentscrollview);
        prodname = (TextView) findViewById(R.id.prodname);
        proddesc = (TextView) findViewById(R.id.desc);
        prodprice = (TextView) findViewById(R.id.prodprice);
        View bottomSheet = findViewById( R.id.AddcartBottomView);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        type = getIntent().getStringExtra("gallery");

        Log.d("from: ",type);

        if(!type.equals("shop")){
            Log.d("gallery type", "from gallery");
            addcart.setVisibility(View.GONE);
            getSupportActionBar().setTitle("");
        }else{
            //getSupportActionBar().setTitle(getIntent().getStringExtra("prodName"));
            getSupportActionBar().setTitle("");
        }

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
                    shareImage(url[0]);
                }
                else{
                    Toast.makeText(this, "Let the details load first", Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
    }



    JSONObject jsonObject;
    public void getProduct(final int prodId)
    {
        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/item_details.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    avi.hide();
                    relLayout.setVisibility(View.VISIBLE);
                    jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");

                    if (success == 1) {
                        String arr = jsonObject.getString("itemname");
                        String price = jsonObject.getString("itemprice");
                        String desc = jsonObject.getString("itemdesc");
                        String photo = jsonObject.getString("itemimg");

                        Log.d("productinfo", "product url: " + photo);

                        setdetails(arr, price, desc, photo);

                    }
                    else{
                        avi.hide();
                        Toast.makeText(getApplication(), "Error Loading", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e) {
                    avi.hide();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                avi.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("itemid", String.valueOf(prodId));
                return params;
        }

    };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

    public void selectQuantity(View v){
        TextView tv_prodprice = (TextView) findViewById(R.id.prodprice);
        TextView textprice = (TextView) findViewById(R.id.quantityPrice);
        textprice.setText("Rs. " + tv_prodprice.getText() + "/-");
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        overlay.setVisibility(View.VISIBLE);
    }

    public void bottom_cart_cancel(View v){
        close_filter();
    }

    public void decreaseQuantity(View v){
        TextView quantitytext = (TextView) findViewById(R.id.quantityText);
        TextView textprice = (TextView) findViewById(R.id.quantityPrice);
        TextView tv_prodprice = (TextView) findViewById(R.id.prodprice);

        int i = Integer.parseInt(String.valueOf(quantitytext.getText()));
        if(i>1){
            i--;
            quantitytext.setText(String.valueOf(i));
            //int price = Integer.parseInt(String.valueOf(text.getText()));
            if (type.equals("home")){
                textprice.setText("Rs. 0/-  ");
            }
            else {
                int prodprice = Integer.parseInt(String.valueOf(tv_prodprice.getText()));
                int temp = i * prodprice;
                textprice.setText("Rs. " + String.valueOf(temp) + "/-  ");
            }
        }
    }

    public void increaseQuantity(View v){
        TextView quantitytext = (TextView) findViewById(R.id.quantityText);
        TextView textprice = (TextView) findViewById(R.id.quantityPrice);
        TextView tv_prodprice = (TextView) findViewById(R.id.prodprice);
        int i = Integer.parseInt(String.valueOf(quantitytext.getText()));
        i = i + 1;
        quantitytext.setText(String.valueOf(i));

        if (type.equals("home")){
            textprice.setText("Rs. 0/-  ");
        }
        else {

            int prodprice = Integer.parseInt(String.valueOf(tv_prodprice.getText()));
            int temp = i * prodprice;
            textprice.setText("Rs. " + String.valueOf(temp) + "/-  ");
        }
    }

    public void enquire(View v){
        Intent i = new Intent(ProductInfo.this, enquiry.class);
        i.putExtra("url",url[0]);
        i.putExtra("from","prodinfo");
        i.putExtra("item_name",item_name);
        startActivity(i);
    }

    public void addtocart(View v){

        TextView quantitytext = (TextView) findViewById(R.id.quantityText);
        int qty = Integer.parseInt(String.valueOf(quantitytext.getText()));

        close_filter();
        /* add items to cart in database of the user   */

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Log.d("cart_items","item id which you are adding: " +  prodId);

        if(prodId>=0) {

            String items = sharedPreferences.getString("cart_item","0");

            Log.d("cart_items", "your cart has following items: " + items);

            if(!items.equals("0")) {

                String[] temp = items.split(",");
                String[] cart_array = new String[temp.length];

                for(int i=0;i<temp.length;i++){
                    String[] temp2 = temp[i].split(":");
                    cart_array[i] = temp2[0];
                }

                for (int i = 0; i < cart_array.length; i++) {
                    Log.d("cart_array", "item " + i + " :" + cart_array[i]);
                }

                if (Arrays.asList(cart_array).contains(String.valueOf(prodId))) {
                    Log.d("cart_items", "this item is already in your cart!!");
                    Toast.makeText(this, "This item is already in your cart!!", Toast.LENGTH_SHORT).show();
                } else {

                    items += String.valueOf(prodId) + ":" + qty + "," ;
                    Toast.makeText(this, "Item Added in your cart!!", Toast.LENGTH_SHORT).show();
                    Log.d("cart_items", "saving array: " + items);
                    editor.putString("cart_item", items);
                }
            }else{
                Log.d("cart_items","there were no items in your cart");
                Toast.makeText(this, "Item Added in your cart!!", Toast.LENGTH_SHORT).show();
                editor.putString( "cart_item", String.valueOf(prodId) + ":" + qty + "," ) ;

            }

            editor.commit();

        }else{
            Toast.makeText(this, "oops! an error occured", Toast.LENGTH_SHORT).show();
        }

    }


    public void setdetails(String name, String price, String desc, String photo) {

        prodname.setText(name);
        prodprice.setText(price);
        proddesc.setText(desc);
        item_name = name;

        url = photo.split(";");

        /*----------------------- view Pager -------------------*/
        adapter = new TestFragmentAdapter(getSupportFragmentManager(), url);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(adapter);
        InkPageIndicator inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        inkPageIndicator.setViewPager(mPager);
        /*-----------------------------------------------------*/

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
        public static Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                return null;
            }
        }

    public void openChat(View v){
        VisitorInfo visitorData = new VisitorInfo.Builder()
                .name(sharedPref.getString("userName", "abc"))
                .note("Enquiry about: " + item_name)
                .email(sharedPref.getString("userEmail", "abc@def.com"))
                .phoneNumber(sharedPref.getString("userNumber", null))
                .build();

        ZopimChat.setVisitorInfo(visitorData);

        startActivity(new Intent(ProductInfo.this, ZopimChatActivity.class));
    }

    public void close_filter(){
        overlay.setVisibility(View.GONE);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

}
