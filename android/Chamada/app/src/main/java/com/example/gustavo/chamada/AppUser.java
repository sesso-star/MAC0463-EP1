package com.example.gustavo.chamada;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.DialogInterface;
import android.drm.DrmManagerClient;
import android.support.constraint.ConstraintLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/* This class is used to define a singleton containing the user of the application */
class AppUser {

    private static User currentUser;

    public static void logIn(User usr) {
        currentUser = usr;
    }

    public static void logOut() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean hasLoggedInUser() {
        return currentUser != null;
    }

    public interface PasswordConfirmationListener {
        void onConfirmation();
    }

    public static void confirmPassword(Context c, final EditText inputText,
                                       final PasswordConfirmationListener listener) {
        /** Closure  Variables **/
        final Context context = c;

        /* Listener for successful login (password confirmation) connection */
        class LoginResponseListener implements Response.Listener<String> {
            @Override
            public void onResponse (String response) {
                JSONObject obj = null;
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
                    return;
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

        String message = context.getString(R.string.password_textview);
        ScreenUtils.showInputDialog(context, message, inputText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = inputText.getText().toString();
                        ServerConnection sc = ServerConnection.getInstance(context);
                        String userType = AppUser.getCurrentUser().getUserType();
                        Map<String, String> params = new HashMap<String,String>();
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
                        return;
                    }});
    }
}
