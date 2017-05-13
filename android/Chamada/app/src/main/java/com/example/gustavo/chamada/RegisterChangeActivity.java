package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RegisterChangeActivity extends Activity {

    private static final String myActivityTag = "SIGN_UP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_change);
    }


    public void changeNameClick(View view) {
        final EditText inputText = new EditText(this);
        final String message = getString(R.string.password_textview);
        ScreenUtils.showInputDialog(RegisterChangeActivity.this, message, inputText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = inputText.getText().toString();
                        Log.d(myActivityTag, "name = " + name);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }});
    }
}
