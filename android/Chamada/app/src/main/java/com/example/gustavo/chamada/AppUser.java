package com.example.gustavo.chamada;


import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/* This class is used to define a singleton containing the user of the application */
class AppUser {

    private static User currentUser;

    static void logIn(User usr) {
        currentUser = usr;
    }

    static void logOut() {
        currentUser = null;
    }

    static User getCurrentUser() {
        return currentUser;
    }

    static boolean hasLoggedInUser() {
        return currentUser != null;
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
}
