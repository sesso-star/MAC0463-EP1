package com.example.gustavo.chamada;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {

    private static final String myActivityTag = "HOME_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button registerProfessorButton = (Button) findViewById(R.id.registerProfessorButton);
        fetchUserName();
        User u = AppUser.getCurrentUser();
        Log.d (myActivityTag, "Userrr: " + u.getName() + " - " + u.getNusp() + " " + u.isProfessor());
        if (!AppUser.getCurrentUser().isProfessor ()) {
            registerProfessorButton.setVisibility(View.GONE);
        }
    }

    private static void fetchUserName () {
        return;
    }
}
