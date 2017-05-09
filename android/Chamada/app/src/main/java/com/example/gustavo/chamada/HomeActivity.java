package com.example.gustavo.chamada;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class HomeActivity extends Activity {

    private static final String myActivityTag = "HOME_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.homeLayout);
        ScreenUtils.enableDisableView(layout, false);
        Button registerProfessorButton = (Button) findViewById(R.id.registerProfessorButton);
        fetchUserName();
        if (!AppUser.getCurrentUser().isProfessor ()) {
            registerProfessorButton.setVisibility(View.GONE);
        }

    }


    /*
   *  Listener to server request for getting an user info
   * */
    private class FetchUserResponseListener implements Response.Listener<String> {

        private String nusp;
        private String userType;

        public FetchUserResponseListener (String nusp, String userType) {
            this.nusp = nusp;
            this.userType = userType;
        }

        @Override
        public void onResponse (String response) {
            JSONObject obj = null;
            String name = "";
            try {
                obj = new JSONObject(response.toString());
                JSONObject data = obj.getJSONObject("data");
                name = data.getString("name");
                Log.d (myActivityTag, "Name fetched: " + name);
            }
            catch (Exception e) {}
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.homeLayout);
            ScreenUtils.enableDisableView(layout, true);
            TextView userNameT = (TextView) findViewById(R.id.userNameTextView);
            userNameT.setText(name);
        }
    }


    /*
    *  This class is used as an error listener for the login request
    * */
    private class FetchUserErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            /* Something wrong... go to login screen ?
            * TODO: put alert message here and go to login screen */
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.homeLayout);
            ScreenUtils.enableDisableView(layout, true);
        }
    }

    private void fetchUserName () {
        User u = AppUser.getCurrentUser();
        ServerConnection sc = ServerConnection.getInstance(this);
        sc.fetchUser(new FetchUserResponseListener (u.getNusp(), u.getUserType()),
                new FetchUserErrorListener(), u.getNusp(), u.getUserType());
    }
}
