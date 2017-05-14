package com.wnd.myapp.lenovate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDetails extends AppCompatActivity {

    public ArrayList<CartItems> arr_histproducts = new ArrayList<>() ;
    public ArrayList<CartItems> arr_histsamples = new ArrayList<>() ;
    RecyclerView rv_samples, rv_products;
    CartAdapter adapter_products, adapter_samples;
    AVLoadingIndicatorView avl;
    ScrollView detailslayout;
    TextView tv_addr, tv_email, tv_bid, tv_date, tv_tprice, tv_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String bid = intent.getStringExtra("bid");
        tv_bid = (TextView)findViewById(R.id.orderid_value);
        tv_date = (TextView)findViewById(R.id.date_value);
        tv_tprice = (TextView)findViewById(R.id.price_value);
        tv_number = (TextView)findViewById(R.id.phone_value);
        tv_addr = (TextView)findViewById(R.id.address_value);
        tv_email = (TextView)findViewById(R.id.email_value);
        tv_bid.setText(bid);
        tv_date.setText(intent.getStringExtra("date"));

        detailslayout = (ScrollView) findViewById(R.id.detailslayout);
        avl = (AVLoadingIndicatorView) findViewById(R.id.avl);

        //-----------------------------Products---------------------------------

        rv_products = (RecyclerView) findViewById(R.id.rv_histproducts);
        adapter_products = new CartAdapter(arr_histproducts, getApplication());
        rv_products.setAdapter(adapter_products);
        rv_products.setHasFixedSize(true);
        LinearLayoutManager llm_products = new LinearLayoutManager(getApplicationContext());
        llm_products.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_products.setLayoutManager(llm_products);

        //-----------------------------samples---------------------------------

        rv_samples = (RecyclerView) findViewById(R.id.rv_histsamples);
        adapter_samples = new CartAdapter(arr_histsamples, getApplication());
        rv_samples.setAdapter(adapter_samples);
        rv_samples.setHasFixedSize(true);
        LinearLayoutManager llm_samples = new LinearLayoutManager(getApplicationContext());
        llm_samples.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_samples.setLayoutManager(llm_samples);

        getDetails(bid);

    }


    JSONObject jsonObject;
    public void getDetails(final String bid){
        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/bidDetails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    avl.hide();
                    detailslayout.setVisibility(View.VISIBLE);
                    Log.d("check", "get bid details");
                    jsonObject = new JSONObject(response);

                    JSONArray arrNumber = jsonObject.getJSONArray("number");
                    String[] number = new String[arrNumber.length()];
                    JSONArray arrEmail = jsonObject.getJSONArray("email");
                    String[] email = new String[arrEmail.length()];
                    JSONArray arrItems = jsonObject.getJSONArray("items");
                    String[] items = new String[arrItems.length()];
                    JSONArray arrQty = jsonObject.getJSONArray("qty");
                    String[] qty = new String[arrQty.length()];
                    JSONArray arrType = jsonObject.getJSONArray("type");
                    String[] type = new String[arrType.length()];
                    JSONArray arrPrice = jsonObject.getJSONArray("price");
                    String[] price = new String[arrPrice.length()];
                    JSONArray arrTPrice = jsonObject.getJSONArray("tprice");
                    String[] tprice = new String[arrTPrice.length()];
                    JSONArray arrAddr = jsonObject.getJSONArray("addr");
                    String[] addr = new String[arrAddr.length()];


                    for (int i = 0; i < arrItems.length(); i++) {
                        number[i] = arrNumber.getString(i);
                        email[i] = arrEmail.getString(i);
                        items[i] = arrItems.getString(i);
                        qty[i] = arrQty.getString(i);
                        type[i] = arrType.getString(i);
                        price[i] = arrPrice.getString(i);
                        tprice[i] = arrTPrice.getString(i);
                        addr[i] = arrAddr.getString(i);

                        CartItems list = new CartItems();
                        Log.d("chat.java","setting bid details item"+ number[i] + items[i] + qty[i] + type[i]);
                        list.setItemname(items[i]);
                        list.setqty(qty[i]);
                        list.setcard("hist_details");
                        if (type[i].equals("product")){
                            list.setprice(price[i]);
                            arr_histproducts.add(list);
                        }
                        else{
                            list.setprice("-");
                            arr_histsamples.add(list);
                        }

                    }
                    rv_products.setAdapter(adapter_products);
                    rv_samples.setAdapter(adapter_samples);
                    tv_addr.setText(addr[0]);
                    tv_email.setText(email[0]);
                    tv_number.setText(number[0]);
                    tv_tprice.setText(tprice[0]);
                }
                catch (JSONException e)
                {
                    avl.hide();
                    Log.d("errorexception", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                avl.hide();
                Log.d("error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("bid", bid);
                return params;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

}

