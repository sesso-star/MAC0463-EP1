package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }


    /* onClick call from change password button */
    public void changePasswordClick(View view) {
        final Context context = this;
        final EditText newPasswordInput =  new EditText(context);
        final EditText passwordInput = new EditText(context);
        passwordInput.
                setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordInput.
                setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        User u = AppUser.getCurrentUser();
        final String userType = u.getUserType();
        final Map<String, String> params = new HashMap<>();
        params.put("nusp", u.getNusp());
        params.put("name", u.getName());

        /* Defines the Listener of (old) password input event */
        class OnPasswordInput implements AppUser.PasswordConfirmationListener {
            @Override
            public void onConfirmation() {
                /* Defines the Listener of (old) password confirmation event */
                class OnPasswordConfirmation implements DialogInterface.OnClickListener {
                    @Override
                    /* Defines the Listener of new password input */
                    public void onClick(DialogInterface dialog, int which) {
                        String newPass = newPasswordInput.getText().toString();
                        params.put("pass", newPass);
                        sendProfileChange(context, userType, params);
                    }
                }
                /* gets typed password starts listener for password confirmation */
                String message = context.getString(R.string.type_new_password_string);
                String password = passwordInput.getText().toString();
                params.put("pass", password);
                ScreenUtils.showInputDialog(context, message, newPasswordInput,
                        new OnPasswordConfirmation(), null);
            }
        }

        /* shows input dialog message */
        AppUser.confirmPassword(this, passwordInput, new OnPasswordInput());
    }


    /* onClick call from change name button */
    public void changeNameClick(View view) {
        final Context context = this;
        final EditText newNameInput =  new EditText(context);
        final EditText passwordInput = new EditText(context);
        passwordInput.
                setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final User u = AppUser.getCurrentUser();
        final String userType = u.getUserType();
        final Map<String, String> params = new HashMap<>();
        params.put("nusp", u.getNusp());
        params.put("name", u.getNusp());

        /* Defines the Listener of password input event */
        class OnPasswordInput implements AppUser.PasswordConfirmationListener {
            @Override
            public void onConfirmation() {
                /* Defines the Listener of password confirmation event */
                class OnPasswordConfirmation implements DialogInterface.OnClickListener {
                    @Override
                    /* Defines the Listener of new name input */
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = newNameInput.getText().toString();
                        params.put("name", newName);
                        sendProfileChange(context, userType, params);
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

        /* shows input dialog message */
        AppUser.confirmPassword(this, passwordInput, new OnPasswordInput());
    }


    /* Asks ServerConnection for a request for profile change */
    private void sendProfileChange (final Context context, String userType,
                                    final Map<String, String> params) {
        final User u = AppUser.getCurrentUser();
        /* Defines the Listeners of profile change server request */
        class ProfileUpdateResponseListener implements Response.Listener<String> {
            @Override
            public void onResponse(String response) {
                String message;
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    if (obj.getString("success").equals ("true")) {
                        u.setName(params.get("name"));
                        u.setNusp(params.get("nusp"));
                        AppUser.logIn(u);
                        message = context.getString(R.string.successful_profile_update);
                    }
                    else {
                        message = context.getString(R.string.unsuccessful_profile_update);
                    }
                } catch (JSONException e) {
                    message = context.getString(R.string.blame_server);
                }
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

        ServerConnection sc = ServerConnection.getInstance(context);
        sc.updateUserProfile(new ProfileUpdateResponseListener(),
                new ProfileUpdateErrorListener(), userType, params);
        u.setName(params.get("name"));
    }


    /* Listener for LogOut click */
    public void logoutClick(View view) {
        AppUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
