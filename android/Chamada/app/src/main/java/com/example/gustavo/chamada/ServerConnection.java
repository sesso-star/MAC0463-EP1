package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
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
    private static Context myContext;
    private RequestQueue requestQueue;
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

    /*
    * Sends a login request to the server
    * */
    public void login(Response.Listener<String> responseListener,
                       Response.ErrorListener errorListener,
                       String userType, final Map<String, String> params) {
        String url = "http://207.38.82.139:8001/login/" + userType;
        // Request a string response from Login URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        getRequestQueue().add(stringRequest);
    }


    /*
    * Sends a register student/teacher request to the server
    * */
    public void singUp(Response.Listener<String> responseListener,
                       Response.ErrorListener errorListener, String userType,
                       final Map<String, String> params) {
        String url = "http://207.38.82.139:8001/" + userType + "/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,responseListener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        getRequestQueue().add(stringRequest);
    }


    /*
    * Sends a server request for student/teacher info
    * */
    public void fetchUser(Response.Listener<String> responseListener,
                          Response.ErrorListener errorListener, String nusp, String userType) {
        String url = "http://207.38.82.139:8001/" + userType + "/get/" + nusp;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,responseListener,
                errorListener);
        getRequestQueue().add(stringRequest);
    }


    /*This class implementation is based on the implementation available at:
    *
    * http://stackoverflow.com/questions/42508091/volley-stringrequest-headers-block-parameters
    *
    * */
//    private class ParamsStringRequest extends Request<String> {
//
//        private Response.Listener<String> listener;
//        private Map<String, String> params;
//
//
//        public ParamsStringRequest(String url, Map<String, String> params,
//                                   Response.Listener<String> reponseListener,
//                                   Response.ErrorListener errorListener, int method) {
//            super(method, url, errorListener);
//            this.listener = reponseListener;
//            this.params = params;
//        }
//
//        @Override
//        protected void deliverResponse(String response) {
//            listener.onResponse(response);
//        }
//
//        @Override
//        protected Map<String, String> getParams() throws AuthFailureError {
//            return params;
//        }
//
//        @Override
//        protected Response<String> parseNetworkResponse(NetworkResponse response) {
//            try {
//                String jsonString = new String(response.data,
//                        HttpHeaderParser.parseCharset(response.headers));
//                return Response.success(new String(jsonString),
//                        HttpHeaderParser.parseCacheHeaders(response));
//            } catch (UnsupportedEncodingException e) {
//                return Response.error(new ParseError(e));
//            }
//        }
//    }

}
