package com.wnd.myapp.lenovate;

import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.wnd.myapp.lenovate.externalDecor.GridRecyclerView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCategoryGallery extends AppCompatActivity {
    boolean isProd = false;
    GridRecyclerView rView;
    String subs, url, fromclass, curr_filter;
    public static String s;
    AVLoadingIndicatorView avcHiomne;
    GalleryAdapter adapter;
    List<GalleryItemObject> allItems;
    private BottomSheetBehavior mBottomSheetBehavior;
    RelativeLayout overlay;
    View bottomSheet;
    TextView tv, noitem;
    CardView filterlayout;
    LinearLayout material, room, style, categ, color, SelectedFiltersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_subcategorygallery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        allItems = new ArrayList<>();
        Intent i = getIntent();
        noitem = (TextView) findViewById(R.id.no_item);

        url = "http://www.woodndecor.in/subcategories.php";

        fromclass = i.getStringExtra("fromclass");
        subs = i.getStringExtra("categ");
        s = subs;
        Log.d("subcategory", "fromclass: " + fromclass + ":::" + subs);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        tv = (TextView) findViewById(R.id.subCatName);
        tv.setText(subs);

        /*---------------filters------------------*/

        overlay = (RelativeLayout) findViewById(R.id.overlay);

        SelectedFiltersLayout = (LinearLayout) findViewById(R.id.SelectedFltersLayout);

        categ = (LinearLayout) findViewById(R.id.category_filter);
        room = (LinearLayout) findViewById(R.id.room_filter);
        style = (LinearLayout) findViewById(R.id.style_filter);
        color = (LinearLayout) findViewById(R.id.color_filter);
        material = (LinearLayout) findViewById(R.id.material_filter);

        filterlayout = (CardView) findViewById(R.id.filter);
        filter.clearoption();

        bottomSheet = findViewById(R.id.bottom_sheet_filter);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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

        /*---------------------------------*/

        if (fromclass.equals("home")) {

            GridLayoutManager lLayout = new GridLayoutManager(this, 3);
            rView = (GridRecyclerView) findViewById(R.id.fragment_recycler);
            rView.setLayoutManager(lLayout);
            GalleryAdapter rcAdapter = new GalleryAdapter(this, allItems, true, isProd, "no", 0);
            rView.setAdapter(rcAdapter);

        } else if (fromclass.equals("gallery")) {

            GridLayoutManager lLayout = new GridLayoutManager(this, 2);
            rView = (GridRecyclerView) findViewById(R.id.fragment_recycler);
            rView.setLayoutManager(lLayout);
            filterlayout.setVisibility(View.VISIBLE);
            url = "http://www.woodndecor.in/item_furninspi.php";
            GalleryAdapter rcAdapter = new GalleryAdapter(this, allItems, true, isProd, "gallery", 0);
            rView.setAdapter(rcAdapter);

        } else if (fromclass.equals("shop")) {

            GridLayoutManager lLayout = new GridLayoutManager(this, 3);
            rView = (GridRecyclerView) findViewById(R.id.fragment_recycler);
            rView.setLayoutManager(lLayout);
            url = "http://www.woodndecor.in/shopSubcategories.php";
            GalleryAdapter rcAdapter = new GalleryAdapter(this, allItems, true, isProd, "shop", 0);
            rView.setAdapter(rcAdapter);
            Log.d("from class", "shop");
        }


        Log.d("subcategory url: ", url);

        avcHiomne = (AVLoadingIndicatorView) findViewById(R.id.aviHome);
        avcHiomne.show();
        Log.d("subcatgallery", "loading category for: " + subs);
        getSubCats(subs);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.cart_menu:
                Intent i = new Intent(SubCategoryGallery.this, Cart.class);
                startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }

    }

    JSONObject jsonObject;
    String[] arr;


    private void getSubCats(final String Cat) {

        if (noitem.getVisibility() == View.VISIBLE) {
            noitem.setVisibility(View.GONE);
        }

        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            String[] arrimg;

            @Override
            public void onResponse(String response) {

                try {
                    Log.d("Response subCategory", response.toString());
                    jsonObject = new JSONObject(response);

                    if (!jsonObject.isNull("subcategories")) {


                        JSONArray arrJson = jsonObject.getJSONArray("subcategories");
                        arr = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arr[i] = arrJson.getString(i);
                            Log.d("subCat", "names: " + arr[i]);
                        }


                        arrJson = jsonObject.getJSONArray("subid");
                        String[] Idarr = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            Idarr[i] = arrJson.getString(i);
                            Log.d("subCat", "ids: " + Idarr[i]);
                        }


                        arrJson = jsonObject.getJSONArray("image");
                        arrimg = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arrimg[i] = arrJson.getString(i);
                            Log.d("category images: ", arrimg[i]);
                        }

                        /*----------------------filters------------------------*/

                        if (jsonObject.has("filter")) {
                            Log.d("subcat", "Found filters");

                            if (!jsonObject.isNull("filter")) {

                                arrJson = jsonObject.getJSONArray("filter");
                                String[] filte = new String[arrJson.length()];
                                JSONArray arrx;

                                for (int i = 0; i < arrJson.length(); i++) {
                                    filte[i] = arrJson.getString(i);
                                    Log.d("subCat", "inside for with filter name: " + filte[i]);
                                    switch (filte[i]) {
                                        case "category":
                                            arrx = jsonObject.getJSONArray("category_options");
                                            filter.categ = new String[arrx.length()];
                                            for (int x = 0; x < arrx.length(); x++) {
                                                filter.categ[x] = arrx.getString(x);
                                            }
                                            categ.setVisibility(View.VISIBLE);
                                            break;
                                        case "material":
                                            arrx = jsonObject.getJSONArray("material_options");
                                            filter.material = new String[arrx.length()];
                                            for (int x = 0; x < arrx.length(); x++) {
                                                filter.material[x] = arrx.getString(x);
                                            }
                                            material.setVisibility(View.VISIBLE);
                                            break;
                                        case "style":
                                            Log.d("subCat", "style: ");
                                            arrx = jsonObject.getJSONArray("style_options");
                                            filter.style = new String[arrx.length()];
                                            for (int x = 0; x < arrx.length(); x++) {
                                                Log.d("length", String.valueOf(arrx.length()));
                                                filter.style[x] = arrx.getString(x);
                                            }
                                            style.setVisibility(View.VISIBLE);
                                            break;
                                        case "room":
                                            arrx = jsonObject.getJSONArray("room_options");
                                            filter.room = new String[arrx.length()];
                                            for (int x = 0; x < arrx.length(); x++) {

                                                filter.room[x] = arrx.getString(x);
                                            }
                                            room.setVisibility(View.VISIBLE);
                                            break;
                                        case "color":
                                            Log.d("subCat", "color: ");
                                            arrx = jsonObject.getJSONArray("color_options");
                                            filter.color = new String[arrx.length()];
                                            for (int x = 0; x < arrx.length(); x++) {
                                                filter.color[x] = arrx.getString(x);
                                                Log.d("subCat", "color: " + filter.color[x]);
                                            }
                                            color.setVisibility(View.VISIBLE);
                                            break;
                                    }
                                    Log.d("subCat", "after switch");
                                }
                            } else {
                                Log.d("subcat", "but fliters is null");
                            }
                        }

                        /*---------------------------------------------*/

                        if (fromclass.equals("home")) {
                            adapter = new GalleryAdapter(getApplicationContext(), fromJson(arr, Idarr, arrimg, "yes"), true, false, "home", 0);
                            rView.setAdapter(adapter);
                        } else if (fromclass.equals("gallery")) {
                            adapter = new GalleryAdapter(getApplicationContext(), fromJson(arr, Idarr, arrimg, "no"), false, true, "gallery", 0);
                            rView.setAdapter(adapter);
                        } else if (fromclass.equals("shop")) {
                            adapter = new GalleryAdapter(getApplicationContext(), fromJson(arr, Idarr, arrimg, "yes"), true, false, "shop", 0);
                            rView.setAdapter(adapter);
                        }
                        avcHiomne.hide();

                    } else {
                        avcHiomne.hide();
                        noitem.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error" + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                if (filter.style_opt != null) {
                    params.put("style", filter.style_opt.toString());
                }
                if (filter.clr_opt != null) {
                    Log.d("params color", filter.clr_opt);
                    params.put("color", filter.clr_opt.toString());
                }
                if (filter.room_opt != null) {
                    params.put("room", filter.room_opt.toString());
                }
                if (filter.mat_opt != null) {
                    params.put("style", filter.mat_opt.toString());
                }
                if (filter.price_opt != null) {
                    Log.d("orice_opt", filter.price_opt);
                    params.put("price", filter.price_opt.toString());
                }
                if (filter.categ_opt != null) {
                    params.put("cat", filter.categ_opt);
                } else {
                    params.put("cat", Cat);
                }
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(sr);
    }

    private List<GalleryItemObject> fromJson(String[] arr, String[] iudA, String[] img, String text) {

        for (int i = 0; i < arr.length; i++) {
            allItems.add(new GalleryItemObject(arr[i], img[i], Integer.parseInt(iudA[i]), text));
        }

        return allItems;

    }


 /*-------------------------------------- Bottom Sheet Code -----------------------------------------*/

    public void showfilter(View v) {
        TextView filter_name = (TextView) findViewById(R.id.Btmfilter_name);
        Log.d("applied filters: ", filter.showoption());

        switch (v.getId()) {
            case R.id.color_filter:
                filter_name.setText("Color");
                Log.d("show filter", "you've clicked color filter!!");
                Log.d("show filter", "filter color:" + filter.color);
                inflate_filter_options(filter.color);
                break;
            case R.id.category_filter:
                filter_name.setText("Category");
                Log.d("show filter", "you've clicked categ filter!!");
                inflate_filter_options(filter.categ);
                break;
            case R.id.material_filter:
                filter_name.setText("Material");
                Log.d("show filter", "you've clicked finish filter!!");
                inflate_filter_options(filter.material);
                break;
            case R.id.style_filter:
                filter_name.setText("Style");
                Log.d("show filter", "you've clicked finish filter!!");
                inflate_filter_options(filter.style);
                break;
            case R.id.room_filter:
                filter_name.setText("Room");
                Log.d("show filter", "you've clicked room filter!!");
                inflate_filter_options(filter.room);
                break;
            case R.id.price_filter:
                filter_name.setText("Price");
                Log.d("show filter", "you've clicked price filter!!");
                inflate_filter_options(filter.price);
                break;


        }

    }

    public void inflate_filter_options(String[] arr) {

        LinearLayout Filter_list_layout = (LinearLayout) findViewById(R.id.filterListLayout);
        Filter_list_layout.removeAllViews();


        for (int i = 0; i < arr.length; i++) {
            Log.d("inflating options", "inflating option: " + i + "  :  " + arr[i]);
            View option = getLayoutInflater().inflate(R.layout.filter_option, null);
            TextView tv = (TextView) option.findViewById(R.id.opt);
            tv.setText(arr[i]);
            RelativeLayout rl = (RelativeLayout) tv.getParent();
            rl.setTag(arr[i]);
            Filter_list_layout.addView(option);
        }

        overlay.setVisibility(View.VISIBLE);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    public void close_filter(View v) {
        overlay.setVisibility(View.GONE);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        LinearLayout Filter_list_layout = (LinearLayout) findViewById(R.id.filterListLayout);
        Filter_list_layout.removeAllViews();
    }

    public void save_filter(View v) {

        overlay.setVisibility(View.GONE);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        LinearLayout Filter_list_layout = (LinearLayout) findViewById(R.id.filterListLayout);
        Filter_list_layout.removeAllViews();

    }

    public void filter_selected(View v) {

        if (SelectedFiltersLayout.getVisibility() == View.GONE) {
            SelectedFiltersLayout.setVisibility(View.VISIBLE);
        }

        TextView filter_name = (TextView) findViewById(R.id.Btmfilter_name);
        curr_filter = filter_name.getText().toString();

        Log.d("filter selected", "filter you've clicked: " + curr_filter + " and option: " + v.getTag());

        switch (curr_filter) {
            case "Category":
                filter.categ_opt = v.getTag().toString();
                tv.setText(v.getTag().toString());
                break;
            case "Style":
                filter.style_opt = v.getTag().toString();
                break;
            case "Price":
                filter.price_opt = v.getTag().toString();
                break;
            case "Material":
                filter.mat_opt = v.getTag().toString();
                break;
            case "Room":
                filter.room_opt = v.getTag().toString();
                break;
            case "Color":
                filter.clr_opt = v.getTag().toString();
                break;
        }


        allItems.clear();
        adapter.notifyDataSetChanged();

        set_selectedfilter(curr_filter, v);
        avcHiomne.setVisibility(View.VISIBLE);

        getSubCats(subs);
        //GetFilteredItems(SubId);

    }

    public void removefilter(View v) {

        LinearLayout ll = (LinearLayout) v.getParent().getParent();
        Log.d("remove filter", String.valueOf(ll.getChildCount()) + "tag: " + ll.getTag());
        String filter_name = ll.getTag().toString();
        String[] filterarr = filter_name.split("_");

        switch (filterarr[0]) {
            case "Category":
                filter.categ_opt = null;
                tv.setText(s);
                break;
            case "Room":
                filter.room_opt = null;
                break;
            case "Style":
                filter.style_opt = null;
                break;
            case "Material":
                filter.mat_opt = null;
                break;
            case "Price":
                filter.price_opt = null;
                break;
            case "Color":
                filter.clr_opt = null;
                break;
        }

        int count = SelectedFiltersLayout.getChildCount();
        int index = 0;

        Log.d("remove filters", "no. of filters selected: " + count);

        for (int i = 0; i < count; i++) {
            Log.d("removing child", "inside for");
            if (SelectedFiltersLayout.getChildAt(i).getTag().equals(filter_name)) {
                Log.d("removing child", "removing child at : " + i);
                index = i;
            }
        }
        SelectedFiltersLayout.removeViewAt(index);
        if (SelectedFiltersLayout.getChildCount() == 0) {
            SelectedFiltersLayout.setVisibility(View.GONE);
        }
        Log.d("after removing filter", filter.showoption());
        allItems.clear();
        adapter.notifyDataSetChanged();
        avcHiomne.setVisibility(View.VISIBLE);
        getSubCats(subs);
    }

    public void set_selectedfilter(String curfilter, View v) {

        int count = SelectedFiltersLayout.getChildCount();
        int z = 0;

        for (int i = 0; i < count; i++) {
            Log.d("set_selectedfilter", "Searching pre-applied filters");
            String filter = SelectedFiltersLayout.getChildAt(i).getTag().toString();
            String[] arrfilter = filter.split("_");

            if (arrfilter[0].equals(curfilter)) {
                if (arrfilter[1].equals(v.getTag())) {
                    Log.d("set_selectedfilter", "Matching filter found generating a toast");
                    Toast.makeText(SubCategoryGallery.this, "this filter is already added", Toast.LENGTH_SHORT).show();
                    z = 1;
                } else {
                    Log.d("set_selectedfilter", "changing filter");
                    View vx = SelectedFiltersLayout.getChildAt(i);
                    LinearLayout linear = (LinearLayout) vx;
                    TextView tvx = (TextView) vx.findViewById(R.id.selectedFilter_text);
                    tvx.setText(v.getTag().toString());
                    linear.setTag(curfilter + "_" + v.getTag().toString());
                    z = 1;
                }
            }
        }

        Log.d("set_selectedfilter", "count: " + count + ", z: " + z);

        if (z != 1 || count == 0) {
            Log.d("set_selectedfilters", "adding filter: " + v.getTag().toString());
            View option = getLayoutInflater().inflate(R.layout.selected_filter, null);
            TextView tv = (TextView) option.findViewById(R.id.selectedFilter_text);
            tv.setText(v.getTag().toString());
            LinearLayout ll = (LinearLayout) tv.getParent().getParent();
            //Log.d("set_selectedfilters", String.valueOf(ll.getChildCount()));
            ll.setTag(curfilter + "_" + v.getTag().toString());
            SelectedFiltersLayout.addView(option);
        }

    }

    public void resetfilter(View v) {
        filter.clearoption();
        Log.d("reset filters", "all filters habve been reset");
        tv.setText(s);
        SelectedFiltersLayout.removeAllViews();
        SelectedFiltersLayout.setVisibility(View.GONE);

        allItems.clear();
        adapter.notifyDataSetChanged();
        avcHiomne.setVisibility(View.VISIBLE);
        getSubCats(subs);        //load items with no filters
    }


}

