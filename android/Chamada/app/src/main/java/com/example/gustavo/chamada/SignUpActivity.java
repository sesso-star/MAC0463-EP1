package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends Activity {

    private static final String myActivityTag = "SIGN_UP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        RadioGroup userTypeGroup = (RadioGroup) findViewById(R.id.userTypeRadioGroup);
        if (AppUser.hasLoggedInUser() && AppUser.getCurrentUser().isProfessor())
            userTypeGroup.setVisibility(View.VISIBLE);
        else
            userTypeGroup.setVisibility(View.GONE);
    }


    /* This class is used as a listener to the server response when the request is successful */
    private class SignUpResponseListener implements Response.Listener<String> {

        private String nusp;
        private String password;
        private String userType;

        private SignUpResponseListener(String nusp, String pass, String userType) {
            this.nusp = nusp;
            this.password = pass;
            this.userType = userType;
        }

        @Override
        public void onResponse (final String response) {
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.singUpLayout);
            ScreenUtils.enableDisableView(layout, true);
            proccessSignUpResponse(response, nusp, password, userType);
        }
    }


    /*  This class is used as an error listener for the signup request */
    private class SignUpErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.singUpLayout);
            ScreenUtils.enableDisableView(layout, true);
            String message = getString(R.string.no_server_connection);
            ScreenUtils.showMessaDialog(SignUpActivity.this, message, null);
        }
    }


    /* Deals with clicks on register button */
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
        postSignUp (name, nusp, pass, userType);
    }


    /* Calls server connection for signing up */
    public void postSignUp (final String name, final String nusp, final String pass, final String
            userType) {
        Map<String, String> params = new HashMap<String,String>();
        params.put("nusp", nusp);
        params.put("name", name);
        params.put("pass", pass);
        ServerConnection sc = ServerConnection.getInstance(this);
        sc.singUp(new SignUpResponseListener(nusp, pass, userType), new SignUpErrorListener(),
                userType, params);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.singUpLayout);
        ScreenUtils.enableDisableView(layout, false);
    }


    /* Updates app after succesful user sign up */
    private void proccessSignUpResponse (String response, final String nusp, String password,
                                         final String userType) {
        Button signupButton = (Button) findViewById(R.id.signUpButton);
        JSONObject obj = null;
        Boolean success;
        try {
            obj = new JSONObject(response.toString());
            if (obj.getString("success").equals ("true")) {
                success = true;
            }
            else {
                success = false;
            }
        }
        catch (Exception e) {
            success = false;
            String message = getString(R.string.blame_server);
            ScreenUtils.showMessaDialog(SignUpActivity.this, message,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }});
        }
        if (success) {
            signupButton.setVisibility(View.GONE);
            String message = getString(R.string.successful_signup);
            ScreenUtils.showMessaDialog(SignUpActivity.this, message,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!AppUser.hasLoggedInUser()) {
                                boolean isProfessor = userType.equals("teacher");
                                User u = new User("", nusp, isProfessor);
                                AppUser.logIn(u);
                                startHomeActivity();
                            }
                            else {
                                onBackPressed();
                            }
                        }});
        }
        else {
            String message = getString(R.string.unsuccessful_signup);
            ScreenUtils.showMessaDialog(SignUpActivity.this, message, null);
        }
    }


    private void startHomeActivity () {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
