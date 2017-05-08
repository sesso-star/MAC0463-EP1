package com.example.gustavo.chamada;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SingUp extends Activity {

    private static final String myActivityTag = "SIGN_UP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
    }


    public void registerUser(View view) {
        TextView nameT = (TextView) findViewById(R.id.nameText);
        String name = nameT.getText().toString();
        TextView nuspT = (TextView) findViewById(R.id.nuspText);
        String nusp = nuspT.getText().toString();
        TextView passT = (TextView) findViewById(R.id.passwordText);
        String pass = passT.getText().toString();
        RadioGroup userTypeGroup = (RadioGroup) findViewById(R.id.userTypeRadioGroup);
        RadioButton studentRadio = (RadioButton) findViewById(R.id.studentCheck);
        int radioButtonID = userTypeGroup.getCheckedRadioButtonId();
        String userType = "";
        if (radioButtonID == studentRadio.getId())
            userType = "student";
        else
            userType = "teacher";
        Log.d (myActivityTag, "Values: " + name + ", " + nusp + ", " + pass + ", " + userType);
        postSignUp (name, nusp, pass, userType);
    }


    public void postSignUp (final String name, final String nusp, final String pass, String
            userType) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://207.38.82.139:8001/" + userType + "/add";

        // Request a string response from Login URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(myActivityTag, "Response is: "+ response.toString());
                        proccessSignUpResponse (response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(myActivityTag, "Error! " + error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                params.put("nusp", nusp);
                params.put("name", name);
                params.put("pass", pass);
                return params;
            }

        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    private void proccessSignUpResponse (String response) {
        TextView responseTextView = (TextView) findViewById(R.id.signupResponseMessage);
        Button signupButton = (Button) findViewById(R.id.signUpButton);
        JSONObject obj = null;
        Boolean success;
        try {
            obj = new JSONObject(response.toString());
            if (obj.getString("success").equals ("true")) {
                success = true;
                responseTextView.setText("Usuário registrado!");
            }
            else {
                success = false;
                responseTextView.setText("Não conseguimos te registrar! Você não esqueceu" +
                        " a senha?");
            }

        }
        catch (Exception e) {
            responseTextView.setText("Err... erro no servidor!");
            success = false;
        }
        responseTextView.setVisibility(View.VISIBLE);
        if (success)
            signupButton.setVisibility(View.GONE);
    }
}
