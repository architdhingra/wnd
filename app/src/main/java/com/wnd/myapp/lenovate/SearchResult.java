package com.wnd.myapp.lenovate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class SearchResult extends AppCompatActivity {
    RequestQueue rq = null;
    private GridLayoutManager lLayout;
    GridRecyclerView rView;
    JSONObject jsonObject;
    String[] arr;
    AVLoadingIndicatorView avi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rq = VolleySingelton.getInstance(getApplication()).getRequestQ();
        lLayout = new GridLayoutManager(getApplication(),3);
        rView = (GridRecyclerView) findViewById(R.id.fragment_recycler);
        rView.setLayoutManager(lLayout);
        avi = (AVLoadingIndicatorView) findViewById(R.id.aviHome);

        Intent i = getIntent();
        String query = i.getStringExtra("query");
        String frag = i.getStringExtra("frag");
        processResult(query, frag);
    }

    public void processResult(String query, String frag){
        switch (frag){
            case "0":
                break;
            case  "1":
                getGallerySearch(query, "http://www.woodndecor.in/getGallerySearch.php");
                break;
            case "2":
                getGallerySearch(query, "http://www.woodndecor.in/getGallerySearch.php");
                break;
            case "3":
                getGallerySearch(query, "http://www.woodndecor.in/getShopSearch.php");
                break;
            case "4":
                break;
        }
    }

    public void getGallerySearch(final String query, String url){

        StringRequest srQ = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{JSONObject jObj = new JSONObject(response);
                            int suc = 0;

                            suc = jObj.getInt("success");
                            avi.hide();
                            if(suc==1) {

                                String[] arrimg;

                                try {
                                    jsonObject = new JSONObject(response);
                                    JSONArray arrJson = jsonObject.getJSONArray("subcategories");
                                    arr = new String[arrJson.length()];
                                    for (int i = 0; i < arrJson.length(); i++) {
                                        arr[i] = arrJson.getString(i);
                                        Log.d("subCat", "names: " + arr[i]);
                                    }

                                    jsonObject = new JSONObject(response);
                                    arrJson = jsonObject.getJSONArray("image");
                                    arrimg = new String[arrJson.length()];
                                    for (int i = 0; i < arrJson.length(); i++) {
                                        arrimg[i] = arrJson.getString(i);
                                        Log.d("category images: ", arrimg[i]);
                                    }

                                    arrJson = jsonObject.getJSONArray("subid");
                                    String[] Idarr = new String[arrJson.length()];
                                    for (int i = 0; i < arrJson.length(); i++) {
                                        Idarr[i] = arrJson.getString(i);
                                        Log.d("subCat", "ids: " + Idarr[i]);
                                    }

                                    rView.setAdapter(new GalleryAdapter(getApplicationContext(), fromJson(arr, Idarr,arrimg,"no"), false, true, "gallery", 0));


                                }
                                 catch(Exception e){
                                     Log.d("Exception:" , e.toString());
                                 }

                            }else{
                                //pDialog.hide();
                                avi.hide();
                                Toast.makeText(getApplicationContext(), "Error: " + jObj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException e) {
                            //pDialog.hide();
                            avi.hide();
                            Toast.makeText(getApplicationContext(), "Error while Registering", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //pDialog.hide();
                avi.hide();
                Toast.makeText(getApplicationContext(), "Error While Registering", Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("query",query);

                return params;
            }

        };

        srQ.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(srQ);
    }

    private List<GalleryItemObject> fromJson(String[] arr, String[] iudA, String[] img, String text ) {
        List<GalleryItemObject> allItems = new ArrayList<>();

        for (int i = 0; i < arr.length; i++) {
            allItems.add(new GalleryItemObject(arr[i], img[i], Integer.parseInt(iudA[i]), text));
        }

        return allItems;
    }
}
