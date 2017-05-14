package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;
import com.wnd.myapp.lenovate.externalDecor.GridRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mycollections extends AppCompatActivity {

    gallery g;
    boolean isProd = false;
    boolean isCatView = false;
    GridRecyclerView rView;
    String subs;
    GalleryAdapter adapter;
    SharedPreferences sp;
    String url;
    TextView noCollection;
    List<GalleryItemObject> allItems = new ArrayList<>();
    public static String s;
    JSONObject jsonObject = null;
    String fromclass;
    AVLoadingIndicatorView avcHiomne;
    int subid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_mycollections);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noCollection = (TextView) findViewById(R.id.no_collection_text);
        sp = mycollections.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        avcHiomne = (AVLoadingIndicatorView) findViewById(R.id.aviHome);
        GridLayoutManager lLayout = new GridLayoutManager(this, 2);
        rView = (GridRecyclerView) findViewById(R.id.fragment_recycler);
        rView.setLayoutManager(lLayout);
        //rView.addItemDecoration(new recyclerDecorations(3, 10, false));

        List<GalleryItemObject> allItems = new ArrayList<GalleryItemObject>();
        url = "http://www.woodndecor.in/mycollections.php";
        GalleryAdapter rcAdapter = new GalleryAdapter(this, allItems, true, isProd, "gallery", 0);
        rView.setAdapter(rcAdapter);

        getSubCats();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSubCats() {

        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            String[] arrimg, arrnote, arr;

            @Override
            public void onResponse(String response) {


                try {

                    jsonObject = new JSONObject(response);
                    Log.d("response", response.toString());

                    if (!jsonObject.isNull("itemname")) {

                        JSONArray arrJson = jsonObject.getJSONArray("itemname");
                        arr = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arr[i] = arrJson.getString(i);
                            Log.d("itemname", "names: " + arr[i]);
                        }

                        arrJson = jsonObject.getJSONArray("itemid");
                        String[] Idarr = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            Idarr[i] = arrJson.getString(i);
                            Log.d("itemid", "ids: " + Idarr[i]);
                        }

                        arrJson = jsonObject.getJSONArray("itemimg");
                        arrimg = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arrimg[i] = arrJson.getString(i);
                            Log.d("item images: ", arrimg[i]);
                        }

                        arrJson = jsonObject.getJSONArray("itemnote");
                        arrnote = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arrnote[i] = arrJson.getString(i);
                            Log.d("item note: ", arrnote[i]);
                        }

                        adapter = new GalleryAdapter(getApplicationContext(), fromJson(arr, Idarr, arrimg, "no"), false, true, "mycollections", 0);
                        rView.setAdapter(adapter);

                        avcHiomne.hide();

                    }else{
                        avcHiomne.hide();
                        noCollection.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    avcHiomne.hide();
                    Toast.makeText(getApplicationContext(), "An error occured", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                avcHiomne.hide();
                Toast.makeText(getApplicationContext(), "error" + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", sp.getString("userEmail", "abc@def.com"));
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }


    private List<GalleryItemObject> fromJson(String[] arr, String[] iudA, String[] img, String text) {

        for (int i = 0; i < arr.length; i++)
            allItems.add(new GalleryItemObject(arr[i], img[i], Integer.parseInt(iudA[i]), text));

        return allItems;

    }

    @Override
    public void onResume(){
        super.onResume();

        if(!allItems.isEmpty()){
            Log.d("MyCollections","Emptying AllItems");
            allItems.clear();
            adapter.notifyDataSetChanged();
            avcHiomne.setVisibility(View.VISIBLE);
            noCollection.setVisibility(View.GONE);
            getSubCats();
        }

    }

}
