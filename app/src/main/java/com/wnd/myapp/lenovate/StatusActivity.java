package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class StatusActivity extends ActionBarActivity {

    SharedPreferences sharedPreferences, sp;
    String itemid, itemqty, tprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        sharedPreferences = getApplicationContext().getSharedPreferences("cart", Context.MODE_PRIVATE);
        sp = StatusActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Intent mainIntent = getIntent();
        TextView tv4 = (TextView) findViewById(R.id.textView1);
        String id = mainIntent.getStringExtra("id");
        tprice = mainIntent.getStringExtra("tprice");
        String status = mainIntent.getStringExtra("transStatus");
        tv4.setText(status);

        if (status.equals("Transaction Successful!")) {
            addBooking(id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    JSONObject jsonObject;
    int success;
    public void addBooking(final String id){

        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/booking.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    if (success == 1) {
                        String bid = jsonObject.getString("bid");
                        Log.d("bookin confirmed", "bid: " + bid);
                    }
                    else{
                        Toast.makeText(getApplication(), "Error Loading", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", itemid);
                params.put("qty", itemqty);
                params.put("ccaid", id);
                params.put("tprice", tprice);
                params.put("email", sp.getString("userEmail", "abc@def.com"));
                params.put("addr", sp.getString("userAddr", "error"));
                return params;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

    public void getcart(){

        String items = sharedPreferences.getString("cart_item","0");
        String tempid = null, tempqty =null, cart_items[] = null;
        Log.d("getcart", "your cart has following items: " + items);

        if(!items.equals("0")) {

            Log.d("getcart","items found");
            cart_items = items.split(",");
            Log.d("getcart","extracting itemid's from array: " + items);

            for(int i=0;i<cart_items.length;i++){
                String[] temp = cart_items[i].split(":");

                if(tempid == null){
                    tempid = temp[0];
                    tempqty = temp[1];

                }else{
                    tempid = tempid + "," + temp[0];
                    tempqty = tempqty + "," + temp[1];
                }
            }
            itemid = tempid;
            itemqty = tempqty;

            Log.d("getcart","itemid's from array: " + tempid);
        }
    }

}
