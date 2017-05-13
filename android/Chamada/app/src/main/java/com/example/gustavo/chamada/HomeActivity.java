package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONObject;

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
        SeminarList.cleanSeminarList();
        fetchSeminarList();

    }

    @Override
    public void onBackPressed() {
        AppUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /* Callback for add user button. This button only shows if you are a professor user */
    public void addUser(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /* Callback for change register */
    public void changeProfile(View view) {
        Intent intent = new Intent(this, RegisterChangeActivity.class);
        startActivity(intent);
    }

    /* Listener to server request for getting an user info */
    private class FetchUserResponseListener implements Response.Listener<String> {

        @Override
        public void onResponse (String response) {
            JSONObject obj = null;
            String name = "";
            try {
                obj = new JSONObject(response);
                JSONObject data = obj.getJSONObject("data");
                name = data.getString("name");
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
                respJO = new JSONObject(response);
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
            * TODO: blame server */
            LinearLayout layout = (LinearLayout) findViewById(R.id.homeLayout);
            ScreenUtils.enableDisableView(layout, true);
            Log.d(myActivityTag, error.toString());
        }
    }

    /* Fetches seminars from server and populates view with them */
    private void fetchSeminarList() {
        ServerConnection sc = ServerConnection.getInstance(this);
        sc.fetchSeminars(new FetchSeminarResponseListener(), new FetchSeminarErrorListener());
    }

    /* Callback for updating view whenever you click on a seminar */
    private class ClickSeminarListener implements View.OnClickListener {
        private Seminar seminar;

        public ClickSeminarListener(Seminar s) {
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
        LinearLayout linearView = (LinearLayout) findViewById(R.id.seminarList);
        linearView.removeAllViews();
        for (int i = 0; i < seminars.length; i++) {
            Seminar s = seminars[i];
            Button seminarButton = new SeminarButton(this, s);
            linearView.addView(seminarButton);
        }
    }

    /* This class defines a button used on the list view of seminars */
    private class SeminarButton extends android.support.v7.widget.AppCompatButton {
        public SeminarButton(Context context, Seminar s) {
            super(context, null, R.style.Widget_SeminarButton);
            setText(s.getName());
            setPadding(0, 20, 0, 20);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            setGravity(Gravity.CENTER);
            setBackgroundColor(getColor(R.color.seminarButtonColor));
            setTextColor(getColor(R.color.White));
            this.setOnClickListener(new ClickSeminarListener(s));
        }
    }
}
