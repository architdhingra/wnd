package com.wnd.myapp.lenovate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.wnd.myapp.lenovate.externalDecor.GridRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link shopFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link shopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class shopFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Dashboard parentActivy;
    private OnFragmentInteractionListener mListener;

    public shopFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment shopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static shopFragment newInstance(String param1, String param2) {
        shopFragment fragment = new shopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    GridRecyclerView rView;
    TextView vMt;
    View roortView;
    ViewPager viewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        roortView = inflater.inflate(R.layout.fragment_shop, container, false);
        rView = (GridRecyclerView) roortView.findViewById(R.id.fragment_recycler_shop);
        //rView.setLayoutManager(lLayout);
        parentActivy =(Dashboard) getActivity();
        rView.setAnimater(false);
        rView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL ));

       // ProductAdapter pa= new ProductAdapter(example(),getActivity().getApplicationContext());
       // rView.setAdapter(pa);
    if(!parentActivy.Searched) {
        example();
    parentActivy.Searched = false;
    }else
       SearchedBack(parentActivy.SearchedString);

        return  roortView;
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

    public List<ShopItemObject> sampleList(ShopItemObject[] arr)
    { List<ShopItemObject> allItemsProds = new ArrayList<ShopItemObject>();
        for(int i=0;i<arr.length;i++)
            allItemsProds.add(arr[i]);
        return  allItemsProds;

    }
    private List<ShopItemObject> productsLoad;

  public  List<ShopItemObject> example()
  {
    final  List<ShopItemObject> allItemsProds = new ArrayList<ShopItemObject>();
//        for(int i=0;i<10;i++)
//            allItemsProds.add(new ShopItemObject("Item "+String.valueOf(i),R.drawable.ic_fullscreen_exit_black_24dp,i,i, 100*i));

      final RequestQueue rq = VolleySingelton.getInstance(getContext()).getRequestQ();
final ImageLoader imgLoader = VolleySingelton.getInstance(getContext()).getImageLoader();

      StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/itemsall.php", new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
              try{jsonObject = new JSONObject(response);
                  JSONArray arrJson = jsonObject.getJSONArray("itemname");
                  String[] arr = new String[arrJson.length()];

                  String[] ItemPhotos = new String[arrJson.length()];
                  for (int i = 0; i < arrJson.length(); i++)
                      arr[i] = arrJson.getString(i);
                  arrJson= jsonObject.getJSONArray("itemprice");
                  String[] ItemPrices = new String[arrJson.length()];
                  for (int i = 0; i < arrJson.length(); i++)
                      ItemPrices[i] = arrJson.getString(i);

                  arrJson= jsonObject.getJSONArray("itemimg");

                  for (int i = 0; i < arrJson.length(); i++)
                      ItemPhotos[i] = arrJson.getString(i);

                  for(int i=0;i<arrJson.length();i++)
            allItemsProds.add(new ShopItemObject(arr[i],R.drawable.ic_fullscreen_exit_black_24dp,i,i,Integer.parseInt(ItemPrices[i]),ItemPhotos[i]));

                  ProductAdapter pa= new ProductAdapter(allItemsProds,getActivity().getApplicationContext(),imgLoader);
                  rView.setAdapter(pa);
                  Toast.makeText(getContext(), arr.toString(),Toast.LENGTH_LONG).show();

              }
              catch (JSONException e)
              {}

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

              params.put("lowrange",String.valueOf(0) );
              params.put("highrange",String.valueOf(10) );
              return params;
          }
      };
      rq.add(sr);
        return  allItemsProds;
    }
    JSONObject jsonObject;
    TextView tvvv ;
    String[] ItemNAmes;
    Integer[] ItemPrices;
    String[] ItemPhotos;
    String[] ItemDescs;

  public void SearchedBack(final String SearchQ)
  {
      final  List<ShopItemObject> allItemsProds = new ArrayList<ShopItemObject>();
//        for(int i=0;i<10;i++)
//            allItemsProds.add(new ShopItemObject("Item "+String.valueOf(i),R.drawable.ic_fullscreen_exit_black_24dp,i,i, 100*i));

      final RequestQueue rq = VolleySingelton.getInstance(getContext()).getRequestQ();
      final ImageLoader imgLoader = VolleySingelton.getInstance(getContext()).getImageLoader();

      StringRequest sr = new StringRequest(Request.Method.POST, "http://www.wademy.in/wnd/search.php", new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
              try{jsonObject = new JSONObject(response);
                  JSONArray arrJson = jsonObject.getJSONArray("itemname");
                  String[] arr = new String[arrJson.length()];

                  String[] ItemPhotos = new String[arrJson.length()];
                  for (int i = 0; i < arrJson.length(); i++)
                      arr[i] = arrJson.getString(i);
                  arrJson= jsonObject.getJSONArray("itemprice");
                  String[] ItemPrices = new String[arrJson.length()];
                  for (int i = 0; i < arrJson.length(); i++)
                      ItemPrices[i] = arrJson.getString(i);

                  arrJson= jsonObject.getJSONArray("itemimg");

                  for (int i = 0; i < arrJson.length(); i++)
                      ItemPhotos[i] = arrJson.getString(i);

                  arrJson= jsonObject.getJSONArray("itemid");
                  String[] ItemIds = new String[arrJson.length()];
                  for (int i = 0; i < arrJson.length(); i++)
                      ItemIds[i] = arrJson.getString(i);

                  for(int i=0;i<arrJson.length();i++)
                      allItemsProds.add(new ShopItemObject(arr[i],R.drawable.ic_fullscreen_exit_black_24dp,Integer.parseInt(ItemIds[i]),0,Integer.parseInt(ItemPrices[i]),ItemPhotos[i]));

                  ProductAdapter pa= new ProductAdapter(allItemsProds,getActivity().getApplicationContext(),imgLoader);
                  rView.setAdapter(pa);
                  Toast.makeText(getContext(), arr.toString(),Toast.LENGTH_LONG).show();

              }
              catch (JSONException e)
              {}

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

              params.put("lowrange",String.valueOf(0) );
              params.put("highrange",String.valueOf(3) );
              params.put("name",SearchQ);
              return params;
          }
      };
      sr.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


      rq.add(sr);


  }


}
