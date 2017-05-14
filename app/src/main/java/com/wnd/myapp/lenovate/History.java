package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

public class History extends AppCompatActivity {

    public ArrayList<CartItems> arr_hist = new ArrayList<>();
    SharedPreferences sp;
    RecyclerView rv;
    TextView noitems;
    AVLoadingIndicatorView avl;
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        noitems = (TextView) findViewById(R.id.noitem);
        avl = (AVLoadingIndicatorView) findViewById(R.id.avl);
        sp = History.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        rv = (RecyclerView) findViewById(R.id.rv_hist);
        adapter = new CartAdapter(arr_hist, getApplicationContext());
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        gethistory();
    }


    JSONObject jsonObject;
    public void gethistory() {
        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/history.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    avl.hide();
                    Log.d("check", "response: " + response.toString());
                    jsonObject = new JSONObject(response);

                    if(!jsonObject.isNull("bid")) {
                        JSONArray arrJson = jsonObject.getJSONArray("bid");
                        String[] arr = new String[arrJson.length()];
                        JSONArray arrJsonPrice = jsonObject.getJSONArray("tprice");
                        String[] totalPrice = new String[arrJsonPrice.length()];
                        JSONArray arrJsonDate = jsonObject.getJSONArray("bdate");
                        String[] bdate = new String[arrJsonDate.length()];

                        for (int i = 0; i < arrJson.length(); i++) {
                            bdate[i] = arrJsonDate.getString(i);
                            totalPrice[i] = arrJsonPrice.getString(i);
                            arr[i] = arrJson.getString(i);

                            CartItems list = new CartItems();
                            Log.d("chat.java", "setting hist item" + arr[i] + totalPrice[i]);
                            list.setprice(totalPrice[i]);
                            list.setBookid(arr[i]);
                            list.setDate(bdate[i]);
                            list.setcard("hist");

                            arr_hist.add(list);
                            Log.d("arr_hist", String.valueOf(arr_hist.size()));
                        }
                        rv.setAdapter(adapter);
                    }else{
                        noitems.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", String.valueOf(sp.getString("userEmail", "abc@def.com")));
                return params;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

}
