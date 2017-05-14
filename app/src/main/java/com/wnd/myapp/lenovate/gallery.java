package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;
import com.wnd.myapp.lenovate.externalDecor.GridRecyclerView;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link gallery.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link gallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class gallery extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String typ,url;
    SharedPreferences sharedPref;
    private OnFragmentInteractionListener mListener;
    private GridLayoutManager lLayout;
    private StaggeredGridLayoutManager SGLM;
    AVLoadingIndicatorView avi;
    ScrollView scrollView;



    public gallery() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment gallery.
     */
    // TODO: Rename and change types and number of parameters
    public static gallery newInstance(String param1, String param2) {
        gallery fragment = new gallery();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    Dashboard parentActivity ;
     View rootview;
    gallery g;
    TextView noitem;
    GridRecyclerView rView;
    // depth 1: categories, 2: subcategories , 3: subCatImages
    int galleryDepth=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.fragment_gallery, container, false);
        g=this;

        parentActivity =  (Dashboard) getActivity();
        typ = getArguments().getString("type");
        avi = (AVLoadingIndicatorView) rootview.findViewById(R.id.aviHome);
        scrollView = (ScrollView) rootview.findViewById(R.id.scrollView);


        if(typ.equals("inspi")){
            url = "http://www.woodndecor.in/inspiration.php";

        }else if(typ.equals("furni")){
            url = "http://www.woodndecor.in/furniture.php";
        }
        else if(typ.equals("shop")){
            url = "http://www.woodndecor.in/shopCategories.php";
        }
        log.d("gallery url",url);


        List<GalleryItemObject> rowListItem = getAllItemList();

        if(typ.equals("shop")) {
            lLayout = new GridLayoutManager(getActivity(), 2);
        }else{
            lLayout = new GridLayoutManager(getActivity(), 1);
        }

        SGLM =  new StaggeredGridLayoutManager(1,1);

        getCategories();


        return rootview;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(true);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        if (typ.equals("inspi")){
            searchView.setQueryHint("Search in Inspiration");
        }
        else if (typ.equals("furni")){
            searchView.setQueryHint("Search in Furniture");
        }
        else {
            searchView.setQueryHint("Search in Shop");
        }
        return;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private List<GalleryItemObject> getAllItemList(){

        List<GalleryItemObject> allItems = new ArrayList<GalleryItemObject>();
        return allItems;
    }


    private List<GalleryItemObject> fromJson(String[] arr, String img[]) {


        List<GalleryItemObject> allItems = new ArrayList<GalleryItemObject>();

        for(int i=0;i<arr.length;i++) {
            if(typ.equals("shop")) {
                allItems.add(new GalleryItemObject(arr[i], img[i], 0, "yes"));
            }else{
                allItems.add(new GalleryItemObject(arr[i], img[i], 0, "no"));
            }
        }


        return  allItems;

    }

    JSONObject jsonObject;
    String[] arr;

    void getCategories() {

        final RequestQueue rq = VolleySingelton.getInstance(this.getContext()).getRequestQ();

        StringRequest sr = new StringRequest( Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] arrimg;
                avi.hide();
                scrollView.setVisibility(View.VISIBLE);
                rView = (GridRecyclerView) rootview.findViewById(R.id.fragment_recycler);
                //rView.addItemDecoration(reD);
                rView.setLayoutManager(lLayout);

                try {
                    jsonObject = new JSONObject(response);
                    JSONArray arrJson= jsonObject.getJSONArray("categories");
                    arr=new String[arrJson.length()];
                    for(int i=0;i<arrJson.length();i++) {
                        arr[i] = arrJson.getString(i);

                        Log.d("categories gallery: ", arr[i]);
                    }

                    arrJson= jsonObject.getJSONArray("image");
                    arrimg=new String[arrJson.length()];
                    for(int i=0;i<arrJson.length();i++) {
                        arrimg[i] = arrJson.getString(i);
                        Log.d("category images: ", arrimg[i]);
                    }

                    if(typ.equals("shop")){
                        rView.setAdapter(new GalleryAdapter(getActivity(), fromJson(arr, arrimg), false, false, "shop", 0));
                    }else {
                        rView.setAdapter(new GalleryAdapter(getActivity(), fromJson(arr, arrimg), false, false, "gallery", 0));
                    }

                } catch (JSONException e) {
                    avi.hide();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                avi.hide();
            }
        });

        sr.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(sr);
    }


}
