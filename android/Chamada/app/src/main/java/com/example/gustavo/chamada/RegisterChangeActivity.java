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
        /** Closure  Variables **/
        final Context context = this;
        final EditText inputText = new EditText(this);


        /* Listener for succesful login (password confirmation) connection */
        class LoginResponseListener implements Response.Listener<String> {
            @Override
            public void onResponse (String response) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    if (obj.getString("success").equals ("true")) {
                        /* Successful login*/
                        inputText.setText("");
                        ScreenUtils.showMessaDialog(context, "Password confirmed", null);
                        inputText.setInputType(InputType.TYPE_CLASS_TEXT);

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

        String message = getString(R.string.password_textview);
        inputText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ScreenUtils.showInputDialog(RegisterChangeActivity.this, message, inputText,
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
