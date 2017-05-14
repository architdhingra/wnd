package com.wnd.myapp.lenovate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pusher.client.Pusher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link chatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link chatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText msg;
    int id=0;
    String email;
    Pusher pusher;

    int stopthread;
    int firsttime = 0;
    public ChatAdapter adapter;
    JSONObject jsonObject;
    public static String lastmsg;
    private ListView messagesContainer;
    Button sendbutton;
    String MESSAGES_ENDPOINT = "http://www.woodndecor.in/chatsenduser.php";

    private OnFragmentInteractionListener mListener;

    public chatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static chatFragment newInstance(String param1, String param2) {
        chatFragment fragment = new chatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        Log.d("new instance","chat fragment new instance");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           // mParam1 = getArguments().getString(ARG_PARAM1);
           // mParam2 = getArguments().getString(ARG_PARAM2);

        }

        Log.d("oncreate","oncreate");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        /*Log.d("chat Fragment","oncreate View");
        email="abcd";
        sendbutton = (Button)root.findViewById(R.id.sndbtn);
        messagesContainer = (ListView)root.findViewById(R.id.messagesContainer);
        msg = (EditText)root.findViewById(R.id.msgtxt);
        adapter = new ChatAdapter(getActivity(), new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg();
            }
        });

        stopthread = 0;

        Thread getchat = new Thread(new Runnable() {

           public void run() {
               firsttime=0;
                while(stopthread==0) {
                    try {
                        Thread.sleep(2000);
                        Log.d("sleep","thread" + stopthread);

                        GetChatFromServer();
                    } catch (Exception e) {
                    }
                }
            }
        });
        //getchat.start();
*/

        return root;
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
        Log.d("OnAttach", String.valueOf(stopthread));
        //GetChatFromServer();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d("OnDetach", String.valueOf(stopthread));
    }

    @Override
    public void onResume() {
        super.onResume();
        stopthread=0;

        Log.d("OnResume", String.valueOf(stopthread));

    }

    @Override
    public void onStop() {
        super.onStop();
        stopthread=1;
        Log.d("Onstop", String.valueOf(stopthread));
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


    public void sendmsg(){


        final String tempmsg = msg.getText().toString();
        msg.setText(null);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(id);//dummy
        id++;
        chatMessage.setMessage(tempmsg);
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(false);
        chatMessage.setFlag("y");
        displayMessage(chatMessage);

/*
        AsyncHttpClient sendingmessage = new AsyncHttpClient();

        sendingmessage.post(MESSAGES_ENDPOINT, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setId(id);//dummy
                        id++;
                        chatMessage.setMessage(tempmsg);
                        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                        chatMessage.setMe(false);
                        Log.d("send message post ", chatMessage.toString());
                        displayMessage(chatMessage);


                        Log.d("response",response.toString());
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(
                        getApplicationContext(),
                        "Something went wrong :(",
                        Toast.LENGTH_LONG
                ).show();
            }
        });*/


    }

    public void displayMessage(ChatMessage message) {
        Log.d("displaymessage ",message.getMessage());
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }


    public void GetChatFromServer() {

        final RequestQueue rq = VolleySingelton.getInstance(getActivity()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/chatrecvuser.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{jsonObject = new JSONObject(response);
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    JSONArray time = jsonObject.getJSONArray("time");
                    JSONArray admin = jsonObject.getJSONArray("admin");
                    String[] timearr = new String[msg.length()];
                    String[] adminarr = new String[msg.length()];

                    Log.d("response",response.toString());

                    String[] msgarr = new String[msg.length()];
                    for (int i = 0; i < msg.length(); i++) {
                        msgarr[i] = msg.getString(i);
                        timearr[i] = time.getString(i);
                        adminarr[i] = admin.getString(i);
                    }

                    setmessage(msgarr,timearr,adminarr);

                    //Toast.makeText(getApplicationContext(), arr.toString(),Toast.LENGTH_LONG).show();

                } catch (JSONException e) {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    Log.d("Error","Something went wrong while retrieving chat from server due to: "+error.toString());
                    }
                });
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", email);
                return params;
            }
        };

        rq.add(sr);
    }


    public void setmessage(String[] msg,String[] time, String[] admin){

        if(firsttime==0) {
            for (int i = msg.length - 1; i >= 0; i--) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(id);//dummy
                id++;
                chatMessage.setMessage(msg[i]);
                chatMessage.setDate(time[i]);

                if (admin[i].equals("y")) {
                    chatMessage.setMe(true);
                } else {
                    chatMessage.setMe(false);
                }

                displayMessage(chatMessage);
            }

            firsttime++;
        }else{

            int pos=0;

            for (int i = 0 ; i < msg.length; i++) {
                if(lastmsg.equals(msg[i])){
                   pos = i;
                    Log.d("Position", String.valueOf(pos));
                }
            }
            if(pos>0) {
                for (int i = pos; i>=0; i--) {
                    Log.d("new message", msg[i]);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(id);//dummy
                    id++;
                    chatMessage.setMessage(msg[i]);
                    chatMessage.setDate(time[i]);

                    if (admin[i].equals("y")) {
                        chatMessage.setMe(true);
                    } else {
                        chatMessage.setMe(false);
                    }

                    displayMessage(chatMessage);
                }
            }

        }

        lastmsg = msg[0];
        Log.d("Last Message", lastmsg);

    }
}
