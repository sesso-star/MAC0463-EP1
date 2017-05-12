package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

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
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.loginLayout);
        EditText nuspText = (EditText)findViewById(R.id.nuspText);
        EditText passwdText = (EditText) findViewById(R.id.passwordText);
        String nusp = nuspText.getText().toString();
        String passwd = passwdText.getText().toString();
        // We try to login as professor, if we can't login, we try again as a student
        postLogin(nusp, passwd, "teacher");
        ScreenUtils.enableDisableView(layout, false);
    }

    /*
    *
    *  This class is used as a listener to the server response when the login request is successful
    *
    * */
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


    /* This class is used as an error listener for the login request */
    private class LoginErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            TextView noLoginTextView = (TextView) findViewById(R.id.noLoginTextView);
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.loginLayout);
            ScreenUtils.enableDisableView(layout, true);
            Log.d(myActivityTag, "Error! " + error.getMessage());
            noLoginTextView.setText("Não conseguimos nos conectar :(");
            noLoginTextView.setVisibility(View.VISIBLE);
        }
    }

    private void postLogin(final String nusp, final String password, final String userType)  {
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
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.loginLayout);
        ScreenUtils.enableDisableView(layout, true);
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
            AppUser.logIn(u);
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
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}