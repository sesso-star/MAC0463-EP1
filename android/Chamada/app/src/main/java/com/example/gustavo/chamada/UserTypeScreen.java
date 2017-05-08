package com.example.gustavo.chamada;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class UserTypeScreen extends Activity {

    private static final String myActivityTag = "USER_TYPE_SCREEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_screen);
    }


    public void tryLogin(View view) {
        EditText nuspText = (EditText)findViewById(R.id.nuspText);
        EditText passwdText = (EditText) findViewById(R.id.passwordText);

        String nusp = nuspText.getText().toString();
        String passwd = passwdText.getText().toString();
        Log.d(myActivityTag, "Loggin attempt! " + nusp + ": " + passwd);
        try {
            postLogin(nusp, passwd);
        }
        catch(Exception ex) {}
    }


    private void postLogin(String nusp, String password) {
        final TextView noLoginTextView = (TextView) findViewById(R.id.noLoginTextView);
        RequestQueue queue = Volley.newRequestQueue(this);
        final String my_nusp = nusp;
        final String my_password = password;
        String url = "http://207.38.82.139:8001/login/student";

        // Request a string response from Login URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(myActivityTag, "Response is: "+ response.toString());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response.toString());
                            if (obj.getString("success").equals ("true")) {
                                nextScreen();
                            }
                            else {
                                noLoginTextView.setText("Usuário ou senha inválidos!");
                                noLoginTextView.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (Exception e) {
                            noLoginTextView.setText("Err... erro no servidor!");
                            noLoginTextView.setVisibility(View.VISIBLE);
                            Log.d (myActivityTag, "Couldn't parse server response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(myActivityTag, "Error! " + error.getMessage());
                        noLoginTextView.setText("Não conseguimos nos conectar :(");
                        noLoginTextView.setVisibility(View.VISIBLE);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                params.put("nusp", my_nusp);
                params.put("pass", my_password);
                return params;
            }

        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    void nextScreen () {
        final TextView noLoginTextView = (TextView) findViewById(R.id.noLoginTextView);
        noLoginTextView.setVisibility(View.GONE);
        Log.d (myActivityTag, "Ready to go to next screen!");
    }
}
