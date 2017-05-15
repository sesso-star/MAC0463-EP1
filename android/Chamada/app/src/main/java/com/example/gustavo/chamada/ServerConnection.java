package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * This class is responsible for making any kind of request to the server.
 *
 */
public class ServerConnection extends Activity {

    static private final String serverUrl = "http://207.38.82.139:8001";
    static private final int MY_TIMEOUT_MS = 7000;
    /* TODO: memory leak*/
    /* Android studio says there's a memory leak here!*/
    private static Context myContext;
    private RequestQueue requestQueue;


    /*
     * Class singleton.
     * We need a singleton in this class because a server queue need a context, which should be
     * the same for all requests once it is set. */
    private static ServerConnection instance = null;


    private ServerConnection(Context context) {
        myContext = context;
        requestQueue = getRequestQueue ();
    }


    public static ServerConnection getInstance(Context context) {
        if (instance == null)
            instance = new ServerConnection(context);
        return instance;
    }


    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(myContext.getApplicationContext());
        return requestQueue;
    }


    /* Modifies every request and adds it to request queue */
    private void addToRequestQueue(Request<String> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(MY_TIMEOUT_MS, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }


    /* Sends a login request to the server */
    public void login(Response.Listener<String> responseListener,
                       Response.ErrorListener errorListener,
                       String userType, final Map<String, String> params) {
        String url = serverUrl + "/login/" + userType;
        // Request a string response from Login URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        addToRequestQueue(stringRequest);
    }


    /* Sends a register student/teacher request to the server */
    public void singUp(Response.Listener<String> responseListener,
                       Response.ErrorListener errorListener, String userType,
                       final Map<String, String> params) {
        String url = serverUrl + "/" + userType + "/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,responseListener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        addToRequestQueue(stringRequest);
    }


    /* Sends a server request for student/teacher info */
    public void fetchUser(Response.Listener<String> responseListener,
                          Response.ErrorListener errorListener, String nusp, String userType) {
        String url = serverUrl + "/" + userType + "/get/" + nusp;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,responseListener,
                errorListener);
        addToRequestQueue(stringRequest);
    }


    /* Sends a server request for the list of seminars */
    public void fetchSeminars(Response.Listener<String> responseListener,
                              Response.ErrorListener errorListener) {
        String url = serverUrl + "/seminar";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener,
                errorListener);
        addToRequestQueue(stringRequest);
    }


    /* Sends a server request for changing profile */
    public void updateUserProfile(Response.Listener<String> responseListener,
                                  Response.ErrorListener errorListener, String userType,
                                  final Map<String, String> params) {
        String url = serverUrl + "/" + userType + "/edit";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        addToRequestQueue(stringRequest);
    }


    /* Sends a server request for adding a seminar */
    public void addSerminar(Response.Listener<String> responseListener,
                            Response.ErrorListener errorListener,
                            final Map<String, String> params) {
        String url = serverUrl + "/seminar/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        addToRequestQueue(stringRequest);
    }


    /* Sends a server request for seminar details */
    public void fetchSeminarInfo(Response.Listener<String> responseListener,
                                 Response.ErrorListener errorListener, String id) {
        String url = serverUrl + "/seminar/get/" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener,
                errorListener);
        addToRequestQueue(stringRequest);
    }
}
