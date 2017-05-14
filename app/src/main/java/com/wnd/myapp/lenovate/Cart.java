package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Cart extends AppCompatActivity {

    public ArrayList<CartItems> arr_cart = new ArrayList<>() ;
    TextView Buy;
    SharedPreferences sharedPreferences, sp;
    String cart_items[] = null, itemid[] = null,itemqty[] = null;
    CartAdapter adapter;
    AVLoadingIndicatorView av;
    RecyclerView rv;
    boolean samplePresent = false;
    int total_price=0, spriceFlag = 0;
    TextView tv_totalprice,noitem_in_cart, tv_number, tv_addr, tv_email,warning;
    ScrollView cart_layout;
    LinearLayout buynow_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d("cart.java","oncreate");

        av = (AVLoadingIndicatorView) findViewById(R.id.aviHome);
        sharedPreferences = getApplicationContext().getSharedPreferences("cart", Context.MODE_PRIVATE);
        sp = Cart.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        cart_layout = (ScrollView) findViewById(R.id.cart_layout);
        buynow_layout = (LinearLayout) findViewById(R.id.buynow_layout);
        noitem_in_cart = (TextView) findViewById(R.id.noitem_cart);

        warning = (TextView) findViewById(R.id.warning);
        Buy = (TextView) findViewById(R.id.buy);
        tv_totalprice = (TextView) findViewById(R.id.total_price);
        tv_addr = (TextView) findViewById(R.id.address_value);
        tv_number = (TextView) findViewById(R.id.phone_value);
        tv_email = (TextView) findViewById(R.id.email_value);
        String add = sp.getString("userAddr", "**Please Enter Address in Profile**");
        String no = sp.getString("userNumber", "**Please Enter Number in Profile**");
        if (no.length() == 0) {
            tv_number.setText("**Please Enter Number in Profile**");
        }
        else {
            tv_number.setText(no);
        }
        if (add.length() == 0) {
            tv_addr.setText("**Please Enter Address in Profile**");
        }
        else {
            tv_addr.setText(add);
        }
        tv_email.setText(sp.getString("userEmail", null));

        Buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("debug", spriceFlag + String.valueOf(!sp.getString("userPin", null).matches("1100(.*)")));
                if (tv_number.getText().equals("**Please Enter Number in Profile**") || tv_addr.getText().equals("**Please Enter Address in Profile**")){
                    Snackbar snackbar = Snackbar
                            .make(v, "Please Complete Your Details in Profile..", Snackbar.LENGTH_LONG)
                            .setAction("Go To Profile", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Cart.this, profile.class));
                                    finish();
                                }
                            });

                    snackbar.show();
                    //Toast.makeText(Cart.this, "Enter Your Details in Profile First.", Toast.LENGTH_LONG).show();
                }
                else if (spriceFlag==1 && !sp.getString("userPin", null).matches("1100(.*)")) {
                    Toast.makeText(Cart.this, "We can't deliver samples at this pin code.", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(Cart.this, WebViewActivity.class);
                    Log.d("price", String.valueOf(total_price));
                    i.putExtra("price", String.valueOf(total_price));

                    startActivity(i);
                }
            }
        });


        rv = (RecyclerView) findViewById(R.id.rv_cart);
        adapter = new CartAdapter(arr_cart, getApplication());
        //rv_products.setAdapter(adapter_products);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        getcart();
    }

    public void getcart(){

        String items = sharedPreferences.getString("cart_item","0");
        String tempid = null,tempqty =null, temptype=null;
        Log.d("getcart", "your cart has following items: " + items);

        if(!items.equals("0")) {

            Log.d("getcart","items found");
            cart_items = items.split(",");
            Log.d("getcart","extracting itemid's from array: " + items);

            for(int i=0;i<cart_items.length;i++) {
                String[] temp = cart_items[i].split(":");
                String[] typearr = new String[cart_items.length];

                /*if (temp[1].equals("999")) {
                    typearr[i] = "1";
                }else{
                    typearr[i] = "0";
                }*/
                if (tempid == null) {
                    tempid = temp[0];
                    tempqty = temp[1];
                    //temptype = typearr[i];

                } else {
                    tempid = tempid + "," + temp[0];
                    tempqty = tempqty + "," + temp[1];
                    //temptype = temptype + "," + typearr[i];
                }
            }
            itemid = tempid.split(",");
            itemqty = tempqty.split(",");

            Log.d("getcart","itemid's : " + tempid);
            Log.d("getcart","item qty's: " + tempqty);
            //Log.d("getcart","item types: " + temptype);


            getcartitems(tempid /*,temptype*/);

        }else{
            av.hide();
            noitem_in_cart.setVisibility(View.VISIBLE);
        }
    }


    JSONObject jsonObject;
    public void getcartitems(final String prodId /*, final String prodtype*/)
    {
        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/cartitem_details.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    Log.d("getcart","getting details: "+response);

                    jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("success")==1) {

                        JSONArray arrJson = jsonObject.getJSONArray("itemname");
                        String[] arr = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++)
                            arr[i] = arrJson.getString(i);

                        arrJson = jsonObject.getJSONArray("type");
                        String[] arrtype = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++)
                            arrtype[i] = arrJson.getString(i);

                        arrJson = jsonObject.getJSONArray("itemprice");
                        String[] ItemPrices = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++)
                            ItemPrices[i] = arrJson.getString(i);

                        String[] ItemPhotos = new String[arrJson.length()];
                        arrJson = jsonObject.getJSONArray("itemimg");
                        for (int i = 0; i < arrJson.length(); i++)
                            ItemPhotos[i] = arrJson.getString(i);


                        for (int i = 0; i < arr.length; i++) {
                            CartItems list = new CartItems();
                            Log.d("cart.java", "setting cart item : " + arr[i] + ", id : " + itemid[i] + " With Quantity: " + itemqty[i]);
                            list.setItemname(arr[i]);
                            if (!ItemPrices[i].equals("-")) {
                                list.setqty(itemqty[i]);
                                total_price += Integer.parseInt(ItemPrices[i]) * Integer.parseInt(itemqty[i]);
                            } else {
                                list.setqty("1");
                                if (spriceFlag == 0) {
                                    spriceFlag = 1;
                                    warning.setVisibility(View.VISIBLE);
                                    total_price += 599;
                                }
                            }
                            list.setitemid(itemid[i]);
                            list.seturl(ItemPhotos[i]);
                            list.settype(arrtype[i]);
                            list.setprice(ItemPrices[i]);
                            list.setcard("cart");
                            arr_cart.add(list);

                        }
                        av.hide();
                        cart_layout.setVisibility(View.VISIBLE);
                        buynow_layout.setVisibility(View.VISIBLE);
                        tv_totalprice.setText("Total Price: " + String.valueOf(total_price));
                        rv.setAdapter(adapter);

                        Log.d("cart.java", "after for with array size: " + arr_cart.size());

                    }else{
                        av.hide();
                        Toast.makeText(Cart.this,"Oops! An Error Occured",Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e) {
                    av.hide();
                    Toast.makeText(Cart.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                av.hide();
                Log.d("getcart error","Error: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("itemid", prodId);
                //params.put("itemtype", prodtype);
                return params;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(sr);
    }

    public void removefromcart(View v){

        RelativeLayout rl = (RelativeLayout) v.getParent();
        TextView tv = (TextView) rl.findViewById(R.id.itemsid);
        String id = tv.getText().toString();
        String temp = null;

        Log.d("cart,java","item you're trying to remove id: " + id );

        for(int i=0;i<arr_cart.size();i++) {

            Log.d("removefromcart", "Searchin... current item on position:" + i + " is: " + arr_cart.get(i).getItemname());

            if (arr_cart.get(i).getitemid().equals(id)) {
                Log.d("removefromcart", "item found on position:" + i + " is: " + arr_cart.get(i).getItemname());

                if(!arr_cart.get(i).getprice().equals("-")) { ///if product
                    total_price = total_price - (Integer.parseInt(arr_cart.get(i).getprice())*Integer.parseInt(arr_cart.get(i).getqty()));
                    arr_cart.remove(i);
                }
                else { //////if matches with sample
                    arr_cart.remove(i);
                    samplePresent = false;
                    for (int j=0; j<arr_cart.size(); j++){
                        if (arr_cart.get(j).getprice().equals("-")){
                            samplePresent = true;
                            warning.setVisibility(View.VISIBLE);
                        }
                    }

                    if (samplePresent==false){
                        total_price = total_price - 599;
                        spriceFlag = 0;
                        warning.setVisibility(View.GONE);
                    }
                }
                tv_totalprice.setText("Total price: " + String.valueOf(total_price));

                adapter.notifyDataSetChanged();
            }
        }

        for(int i=0;i<arr_cart.size();i++){
            if(arr_cart.size()!=0) {
                if (temp == null) {
                    temp = arr_cart.get(i).getitemid() +":"+ arr_cart.get(i).getqty() +",";
                } else {
                    temp += arr_cart.get(i).getitemid() +":"+ arr_cart.get(i).getqty() +",";
                }
            }else{
                Log.d("remove from cart","cart is empty clearing the sharedprefs");
                temp=null;
            }
        }

        if(temp==null) {
            cart_layout.setVisibility(View.GONE);
            buynow_layout.setVisibility(View.GONE);
            noitem_in_cart.setVisibility(View.VISIBLE);
        }
        Log.d("remove","items after removing: " + temp);

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("cart_item",temp);
        edit.commit();

    }

}
