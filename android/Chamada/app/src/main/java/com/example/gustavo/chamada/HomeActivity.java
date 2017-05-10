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

import org.json.JSONArray;
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
        Button addUserButton = (Button) findViewById(R.id.addUserButton);
        if (!AppUser.getCurrentUser().isProfessor ()) {
            addUserButton.setVisibility(View.GONE);
        }
        fetchUserName();
        fetchSeminarLsit();
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
            JSONObject respJO = null;
            try {
                respJO = new JSONObject(response.toString());
                JSONArray seminarJArray = new JSONArray(respJO.getString("data"));
                for (int i = 0; i < seminarJArray.length(); i++) {
                    JSONObject seminarJObj = seminarJArray.getJSONObject(i);
                    String name = seminarJObj.getString("name");
                    String id = seminarJObj.getString("id");
                    Seminar s = new Seminar(id, name);
                }
            }
            catch (Exception e) {
                /* TODO: blame server */
                Log.d (myActivityTag, "Couldn't parse server response");
            }
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.homeLayout);
            ScreenUtils.enableDisableView(layout, true);
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



    /*
   *  Listener to server request for getting an user info
   * */
    private class FetchSeminarResponseListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {
            processSeminarFetch(response);
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.homeLayout);
            ScreenUtils.enableDisableView(layout, true);
            Log.d(myActivityTag, response);
        }
    }

    /*
    *  This class is used as an error listener for the login request
    * */
    private class FetchSeminarErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            /* Something wrong... go to login screen ?
            * TODO: put alert message here and go to login screen */
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.homeLayout);
            ScreenUtils.enableDisableView(layout, true);
            Log.d(myActivityTag, error.toString());
        }
    }

    private void fetchSeminarLsit() {
        ServerConnection sc = ServerConnection.getInstance(this);
        sc.fetchSeminars(new FetchSeminarResponseListener(), new FetchSeminarErrorListener());
    }

    private void processSeminarFetch(String s) {
        JSONObject resJO = null;
        try {

        }
        catch(Exception e) {}
    }
}
