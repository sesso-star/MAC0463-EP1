package com.example.gustavo.chamada;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
        RadioGroup userTypeGroup = (RadioGroup) findViewById(R.id.userTypeRadioGroup);
        if (AppUser.getCurrentUser().isProfessor())
            userTypeGroup.setVisibility(View.VISIBLE);
        else
            userTypeGroup.setVisibility(View.GONE);
    }


    /*
    *
    *  This class is used as a listener to the server response when the request is successful
    *
    * */
    private class SignUpResponseListener implements Response.Listener<String> {

        private String name;
        private String nusp;
        private String password;
        private String userType;

        public SignUpResponseListener (String name, String nusp, String pass, String userType) {
            this.name = name;
            this.nusp = nusp;
            this.password = pass;
            this.userType = userType;
        }

        @Override
        public void onResponse (String response) {
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.singUpLayout);
            ScreenUtils.enableDisableView(layout, true);
            proccessSignUpResponse(response, this.nusp, this.password, this.userType);
        }
    }


    /*
    *
    *  This class is used as an error listener for the login request
    *
    * */
    private class SignUpErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.singUpLayout);
            ScreenUtils.enableDisableView(layout, true);
            TextView responseTextView = (TextView) findViewById(R.id.signupResponseMessage);
//            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.loginLayout);
//            ScreenUtils.enableDisableView(layout, true);
            Log.d(myActivityTag, "Error! " + error.getMessage());
            responseTextView.setText("Não conseguimos nos conectar :(");
            responseTextView.setVisibility(View.VISIBLE);
        }
    }

    /*
    * Deals with clicks on register button
    * */
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


    /*
    * Calls server connection for signing up
    * */
    public void postSignUp (final String name, final String nusp, final String pass, final String
            userType) {
        Map<String, String> params = new HashMap<String,String>();
        params.put("nusp", nusp);
        params.put("name", name);
        params.put("pass", pass);
        ServerConnection sc = ServerConnection.getInstance(this);
        sc.singUp(new SignUpResponseListener(name, nusp, pass, userType), new SignUpErrorListener(),
                userType, params);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.singUpLayout);
        ScreenUtils.enableDisableView(layout, false);
    }


    /*
    * Updates app after succesful user sign up
    * */
    private void proccessSignUpResponse (String response, String nusp, String password,
                                         String userType) {
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
                responseTextView.setText("Não conseguimos te registrar!");
            }
        }
        catch (Exception e) {
            responseTextView.setText("Err... erro no servidor!");
            success = false;
        }
        responseTextView.setVisibility(View.VISIBLE);
        if (success) {
            signupButton.setVisibility(View.GONE);
        }
    }
}
