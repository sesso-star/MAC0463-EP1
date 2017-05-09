package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    private static final String myActivityTag = "LOGIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }


    public void tryLogin(View view) {
        EditText nuspText = (EditText)findViewById(R.id.nuspText);
        EditText passwdText = (EditText) findViewById(R.id.passwordText);
        String nusp = nuspText.getText().toString();
        String passwd = passwdText.getText().toString();
        postLogin(nusp, passwd, "teacher");
    }


    private class LoginResponseListener implements Response.Listener<String> {

        private String nusp;
        private String password;
        private String userType;

        public LoginResponseListener (String nusp, String pass, String userType) {
            this.nusp = nusp;
            this.password = pass;
            this.userType = userType;
        }

        @Override
        public void onResponse (String response) {
            proccessLoginResponse (response, this.nusp, this.password, this.userType);
        }
    }


    private class LoginErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            TextView noLoginTextView = (TextView) findViewById(R.id.noLoginTextView);
            Log.d(myActivityTag, "Error! " + error.getMessage());
            noLoginTextView.setText("Não conseguimos nos conectar :(");
            noLoginTextView.setVisibility(View.VISIBLE);
        }

    }

    private void postLogin(final String nusp, final String password, final String userType)  {
        final TextView noLoginTextView = (TextView) findViewById(R.id.noLoginTextView);
        String url = "http://207.38.82.139:8001/login/" + userType;
        Map<String, String> params = new HashMap<String,String>();
        params.put("nusp", nusp);
        params.put("pass", password);
        ServerConnection sc = ServerConnection.getInstance(this);
        sc.login(new LoginResponseListener (nusp, password, userType), new LoginErrorListener(),
                userType, params);
    }


    private void proccessLoginResponse (String response, String nusp, String password,
                                       String userType) {
        TextView noLoginTextView = (TextView) findViewById(R.id.noLoginTextView);
        Button signupButton = (Button) findViewById(R.id.singupButton);
        int authorized;
        JSONObject obj = null;
        try {
            obj = new JSONObject(response);
            if (obj.getString("success").equals ("true"))
                authorized = 1;
            else
                authorized = 0;

        }
        catch (Exception e) {
            noLoginTextView.setText("Err... erro no servidor!");
            noLoginTextView.setVisibility(View.VISIBLE);
            Log.d (myActivityTag, "Couldn't parse server response");
            return;
        }

        if (authorized == 0)
            if (userType.equals("teacher"))
                // try again as a student...
                postLogin(nusp, password, "student");
            else {
                noLoginTextView.setText("Usuário ou Senha incorreta!");
                noLoginTextView.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.VISIBLE);
            }
        else {
            boolean isProfessor = userType.equals("teacher");
            noLoginTextView.setVisibility(View.GONE);
            User u = new User ("", nusp, isProfessor);
            AppUser.setUser(u);
            startHomeActivity();
        }
    }


    private void startHomeActivity () {
        final TextView noLoginTextView = (TextView) findViewById(R.id.noLoginTextView);
        noLoginTextView.setVisibility(View.GONE);
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    public void startSignUpActivity (View view) {
        Intent intent = new Intent(this, SingUp.class);
        startActivity(intent);
    }
}