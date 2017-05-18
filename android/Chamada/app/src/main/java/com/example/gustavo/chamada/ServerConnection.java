package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

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


    /* Sends a server request for list of students who attended a seminar */
    public void fetchSeminarAttendance(Response.Listener<String> responseListener,
                                       Response.ErrorListener errorListener,
                                       final Map<String, String> params) {
        String url = serverUrl + "/attendence/listStudents";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        addToRequestQueue(stringRequest);
    }


    /* Sends a server request for list of seminars a student attended */
    public void fetchUserAttendance(Response.Listener<String> responseListener,
                                    Response.ErrorListener errorListener,
                                    final Map<String, String> params) {
        String url = serverUrl + "/attendence/listSeminars";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        addToRequestQueue(stringRequest);
    }


    /* Sends a student attendance to a seminar to the server */
    /* Here we implement a postponed of attendance whenever there's no internet connection. */
    public void sendAttendance(Response.Listener<String> responseListener,
                               Response.ErrorListener errorListener,
                               final Map<String, String> params, Context context) {
        String url = serverUrl + "/attendence/submit";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        if (theresInternetConnection(context))
            addToRequestQueue(stringRequest);
        else {
            /* Delivers "maybe" response */
            JSONObject responseObj = new JSONObject();
            try {
                responseObj.put("success", R.string.maybe);
            } catch (JSONException e) {
                errorListener.onErrorResponse(new VolleyError());
            }
            String response = responseObj.toString().replace("\\\"", "\"");
            responseListener.onResponse(response);

            /* Creates a listener to connection status change and if that means we have connection,
             * try to send it again. If it fails again, then nothing is done. */

            StringRequest newStringRequest = new StringRequest(Request.Method.POST, url,
                    new OnSuccessfulPostponedAttendance(),
                    new OnUnsuccessfulPostponedAttendance()) {
                @Override
                protected Map<String,String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            registerConnectionReceiver(new ConnectionReceiver(newStringRequest));
        }
    }


    void registerConnectionReceiver(ConnectionReceiver cr) {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        myContext.registerReceiver(cr, intentFilter);
    }


    private class OnSuccessfulPostponedAttendance implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            JSONObject obj;
            String message;
            try {
                obj = new JSONObject(response);
                if (obj.getString("success").equals("true"))
                    message = myContext.getString(R.string.successful_postponed_attendance);
                else
                    message = myContext.getString(R.string.unsuccessful_postponed_attendance);
            }
            catch (Exception e) {
                message = myContext.getString(R.string.unsuccessful_postponed_attendance);
            }

            Toast.makeText(myContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private class OnUnsuccessfulPostponedAttendance implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            String message = myContext.getString(R.string.unsuccessful_postponed_attendance);
            Toast.makeText(myContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }


    public class ConnectionReceiver extends BroadcastReceiver {

        private StringRequest stringRequest;

        ConnectionReceiver(StringRequest stringRequest) {
            this.stringRequest = stringRequest;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            NetworkInfo info = (NetworkInfo) extras
                    .getParcelable("networkInfo");
            NetworkInfo.State state = info.getState();
            if (state == NetworkInfo.State.CONNECTED) {
                addToRequestQueue(stringRequest);
                myContext.unregisterReceiver(this);
            }
        }
    }


    interface PasswordConfirmationListener {
        void onConfirmation();
    }

    static void confirmPassword(Context c, final EditText inputText,
                                final PasswordConfirmationListener listener) {
        final Context context = c;

        /* Listener for successful login (password confirmation) connection */
        class LoginResponseListener implements Response.Listener<String> {
            @Override
            public void onResponse (String response) {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    if (obj.getString("success").equals ("true")) {
                        /* Successful login*/
                        listener.onConfirmation();
                    }
                    else {
                        String s = context.getString(R.string.invalid_password);
                        ScreenUtils.showMessaDialog(context, s, null);
                    }
                }
                catch (Exception e) {
                    String message = context.getString(R.string.no_server_connection);
                    ScreenUtils.showMessaDialog(context, message, null);
                }
            }
        }

        /* Listener for login (password confirmation) error */
        class LoginErrorListener implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = context.getString(R.string.no_server_connection);
                ScreenUtils.showMessaDialog(context, message, null);
            }
        }

        String message;
        message = context.getString(R.string.password_textview);
        ScreenUtils.showInputDialog(context, message, inputText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = inputText.getText().toString();
                        ServerConnection sc = ServerConnection.getInstance(context);
                        String userType = AppUser.getCurrentUser().getUserType();
                        Map<String, String> params = new HashMap<>();
                        params.put("nusp", AppUser.getCurrentUser().getNusp());
                        params.put("pass", password);
                        sc.login(new LoginResponseListener(), new LoginErrorListener(),
                                userType, params);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*do nothing*/
                    }});
    }

    static boolean theresInternetConnection (Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}