package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link home_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link home_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_fragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SharedPreferences sharedPref;
    Swip_Image swip_image;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int position);
    }

    public home_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static home_fragment newInstance(String param1, String param2) {
        home_fragment fragment = new home_fragment();
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    GridRecyclerView rView;
    View roortView;
    //CircleIndicator indicator;
    AVLoadingIndicatorView avcHiomne;
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        roortView = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        viewPager = (ViewPager) roortView.findViewById(R.id.view_pager);


        rView = (GridRecyclerView) roortView.findViewById(R.id.fragment_home_recycler);
        rView.setAnimater(false);
        rView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        //indicator = (CircleIndicator) roortView.findViewById(R.id.indicator);
        //indicator.setViewPager(viewPager);


        avcHiomne = (AVLoadingIndicatorView) roortView.findViewById(R.id.aviHome);
        avcHiomne.show();

        //*************************
        // Image Slider Buttons
        ImageButton backB = (ImageButton) roortView.findViewById(R.id.backButt);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navClick(v);
            }
        });
        ImageButton NotBackB = (ImageButton) roortView.findViewById(R.id.forwrdButt);
        NotBackB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navClick(v);
            }
        });
        ImageView ii = (ImageView) roortView.findViewById(R.id.banner1);
        ii.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplication(), howto.class));
            }
        });
        getCategories();

        return roortView;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private List<GalleryItemObject> SettingIconAndNames(int n) {
        List<GalleryItemObject> allItems = new ArrayList<GalleryItemObject>();
        return allItems;
    }

    private List<GalleryItemObject> fromJson(String[] arr, String[] img){
        List<GalleryItemObject> allItems = new ArrayList<GalleryItemObject>();

        for (int i = 0; i < arr.length; i++)
            allItems.add(new GalleryItemObject(arr[i], img[i], 0, "yes"));
        return allItems;

    }

    JSONObject jsonObject;
    String[] arr;

    void getCategories() {

        Log.d("getcategories", "inside getCategories()");

        final RequestQueue rq = VolleySingelton.getInstance(getContext()).getRequestQ();


        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/categories.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] arrimg, arrbannerimg, arrscat, arrscid;

                if (!response.toString().equals(null)) {
                    Log.d("Home Frag Response",response);
                    //if(response.toString()!=null) {
                    //}
                    try {
                        jsonObject = new JSONObject(response);
                        JSONArray arrJson = jsonObject.getJSONArray("categories");
                        arr = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arr[i] = arrJson.getString(i);
                            Log.d("categories homefrag: ", arr[i]);
                        }

                        arrJson = jsonObject.getJSONArray("image");
                        arrimg = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arrimg[i] = arrJson.getString(i);
                        }

                        arrJson = jsonObject.getJSONArray("bannerimage");
                        arrbannerimg = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arrbannerimg[i] = arrJson.getString(i);
                            Log.d("Home Frag Banner img",arrbannerimg[i]);
                        }

                        arrJson = jsonObject.getJSONArray("scat");
                        arrscat = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arrscat[i] = arrJson.getString(i);
                        }

                        arrJson = jsonObject.getJSONArray("scid");
                        arrscid = new String[arrJson.length()];
                        for (int i = 0; i < arrJson.length(); i++) {
                            arrscid[i] = arrJson.getString(i);
                        }

                        swip_image = new Swip_Image(getActivity().getApplicationContext(), arrbannerimg, arrscat, arrscid);
                        viewPager.setAdapter(swip_image);

                        Log.d("length of categories: ", String.valueOf(arr.length));

                        avcHiomne.hide();
                        rView.setAdapter(new homePopAdapter(getContext(), fromJson(arr, arrimg)));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    avcHiomne.hide();
                    Toast.makeText(getActivity(), "no response from the server", Toast.LENGTH_SHORT).show();
                    Log.d("no response", "no response from server");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals(null)) {
                    Toast.makeText(getActivity(), "no response from the server", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("error in home: ", error.toString());
                }

            }
        });

        sr.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(sr);
    }



    void navClick(View v) {
        int viewPagerItem = viewPager.getCurrentItem();
        if (v.getId() == R.id.forwrdButt) {
            if ((viewPagerItem == viewPager.getAdapter().getCount() - 1))
                viewPager.setCurrentItem(0, true);
            else
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        }
        if (v.getId() == R.id.backButt) {
            if (viewPagerItem == 0)
                viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1, true);
            else
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        }
    }


}
