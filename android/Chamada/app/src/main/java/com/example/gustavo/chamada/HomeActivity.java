package com.example.gustavo.chamada;

import android.app.Activity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HomeActivity extends Activity {

    private static final String myActivityTag = "HOME_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout layout = (LinearLayout) findViewById(R.id.homeLayout);
        ScreenUtils.enableDisableView(layout, false);
        Button addUserButton = (Button) findViewById(R.id.addUserButton);
        if (!AppUser.getCurrentUser().isProfessor ()) {
            addUserButton.setVisibility(View.GONE);
        }
        fetchUserName();
        fetchSeminarLsit();
    }


    /* Listener to server request for getting an user info */
    private class FetchUserResponseListener implements Response.Listener<String> {

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
            LinearLayout layout = (LinearLayout) findViewById(R.id.homeLayout);
            ScreenUtils.enableDisableView(layout, true);
            TextView userNameT = (TextView) findViewById(R.id.userNameTextView);
            userNameT.setText(name);
        }
    }

    /* This class is used as an error listener for the login request */
    private class FetchUserErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            /* Something wrong... go to login screen ?
            * TODO: put alert message here and go to login screen */
            LinearLayout layout = (LinearLayout) findViewById(R.id.homeLayout);
            ScreenUtils.enableDisableView(layout, true);
        }
    }

    /* Sends a server request to get user name and sets it on a textview */
    private void fetchUserName () {
        User u = AppUser.getCurrentUser();
        ServerConnection sc = ServerConnection.getInstance(this);
        sc.fetchUser(new FetchUserResponseListener (), new FetchUserErrorListener(), u.getNusp(),
                u.getUserType());
    }


    /* Listener to server request for getting an user info */
    private class FetchSeminarResponseListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {
            JSONObject respJO = null;
            try {
                respJO = new JSONObject(response.toString());
                JSONArray seminarJArray = new JSONArray(respJO.getString("data"));
                for (int i = 0; i < seminarJArray.length(); i++) {
                    JSONObject seminarJObj = seminarJArray.getJSONObject(i);
                    String name = seminarJObj.getString("name");
                    String id = seminarJObj.getString("id");
                    Seminar s = new Seminar(id, name);
                    SeminarList.addSeminar(s);
                }
            }
            catch (Exception e) {
                /* TODO: blame server */
                Log.d (myActivityTag, "Couldn't parse server response");
            }
            updateSeminarListView();
        }
    }

    /* This class is used as an error listener for the login request */
    private class FetchSeminarErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            /* Something wrong... go to login screen ?
            * TODO: put alert message here and go to login screen */
            LinearLayout layout = (LinearLayout) findViewById(R.id.homeLayout);
            ScreenUtils.enableDisableView(layout, true);
            Log.d(myActivityTag, error.toString());
        }
    }

    private void fetchSeminarLsit() {
        ServerConnection sc = ServerConnection.getInstance(this);
        sc.fetchSeminars(new FetchSeminarResponseListener(), new FetchSeminarErrorListener());
    }


    /* Callback for updating view whenever you click on a seminar */
    private class clickSeminarListener implements View.OnClickListener {
        private Seminar seminar;

        public clickSeminarListener(Seminar s) {
            this.seminar = s;
        }

        @Override
        public void onClick(View v) {
            // the default action for all lines
            Log.d(myActivityTag, "Clicou em " + seminar.getName());
        }
    }

    /* Updates view with seminars feteched from server */
    private void updateSeminarListView() {
        Seminar[] seminars = SeminarList.getSeminarArray();
        Button semButtonMold = (Button) findViewById(R.id.exampleSemButton);
        ViewGroup.LayoutParams seminarLayout = semButtonMold.getLayoutParams();
        LinearLayout linearView = (LinearLayout) findViewById(R.id.seminarList);
        int textColor = semButtonMold.getCurrentTextColor();
        int buttonColor = ResourcesCompat.getColor(getResources(), R.color.buttonColor, null);
        for (int i = 0; i < seminars.length; i++) {
            Seminar s = seminars[i];
            Button seminarButton = new Button(this);
            seminarButton.setLayoutParams(seminarLayout);
            seminarButton.setText(s.getName());
            seminarButton.setBackgroundColor(buttonColor);
            seminarButton.setTextColor(textColor);
            View.OnClickListener listener = new clickSeminarListener(s);
            seminarButton.setOnClickListener(listener);
            linearView.addView(seminarButton);
        }
    }
}
