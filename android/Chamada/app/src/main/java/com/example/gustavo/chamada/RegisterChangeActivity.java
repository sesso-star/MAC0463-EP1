package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterChangeActivity extends Activity {

    private static final String myActivityTag = "SIGN_UP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_change);
    }


    public void changeNameClick(View view) {
        final Context context = this;
        final EditText newNameInput =  new EditText(context);
        final EditText passwordInput = new EditText(context);
        passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        User u = AppUser.getCurrentUser();
        final String userType = u.getUserType();
        final Map<String, String> params = new HashMap<String,String>();
        params.put("nusp", u.getNusp());

        /* HADOUKEN!
        *  It may look weird, but it makes a lot of sense, since we define a closure each time
        *  we go on in the following process:
        *       typed password -> confirmed user credentials -> typed new name -> server responded
        * */
        /* Defines the Listener of password input event */
        class OnPasswordInput implements AppUser.PasswordConfirmationListener {
            @Override
            public void onConfirmation() {

                /* Defines the Listener of password confirmation event */
                class OnPasswordConfirmation implements DialogInterface.OnClickListener {
                    @Override

                    /* Defines the Listener of new name input */
                    public void onClick(DialogInterface dialog, int which) {

                        /* Defines the Listener of profile change server request */
                        class ProfileUpdateResponseListener implements Response.Listener<String> {
                            @Override
                            public void onResponse(String response) {
                                String message = context.getString(R.string.
                                        successful_profile_update);
                                /* TODO: different message when change is not successfull */
                                ScreenUtils.showMessaDialog(context, message, null);
                            }
                        }

                        class ProfileUpdateErrorListener implements Response.ErrorListener {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String message = context.getString(R.string.no_server_connection);
                                ScreenUtils.showMessaDialog(context, message, null);
                            }
                        }

                        String newName = newNameInput.getText().toString();
                        params.put("name", newName);
                        ServerConnection sc = ServerConnection.getInstance(context);
                        sc.updateUserProfile(new ProfileUpdateResponseListener(),
                                new ProfileUpdateErrorListener(), userType, params);
                    }
                }

                /* gets typed password starts listener for password confirmation */
                String message = context.getString(R.string.type_new_name_string);
                String password = passwordInput.getText().toString();
                params.put("pass", password);
                ScreenUtils.showInputDialog(context, message, newNameInput,
                        new OnPasswordConfirmation(), null);
            }
        }

        /* starts listener for password input */
        AppUser.confirmPassword(this, passwordInput, new OnPasswordInput());
    }
}
