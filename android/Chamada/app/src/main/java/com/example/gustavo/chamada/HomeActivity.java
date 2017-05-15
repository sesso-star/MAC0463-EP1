package com.example.gustavo.chamada;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends Activity {

    private static final String myActivityTag = "HOME_ACTIVITY";
    /* Contains all seminars available in the server */
    private static SeminarList homeSeminarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout layout = (LinearLayout) findViewById(R.id.homeLayout);
        ScreenUtils.enableDisableView(layout, false);
        Button addUserButton = (Button) findViewById(R.id.addUserButton);
        Button addSeminarButton = (Button) findViewById(R.id.addSeminarButton);
        if (!AppUser.getCurrentUser().isProfessor ()) {
            addUserButton.setVisibility(View.GONE);
            addSeminarButton.setVisibility(View.GONE);
        }
        fetchUserName();
        fetchSeminarList();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserName();
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
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


    /* Callback for presence confirmation using qr code */
    public void qrCodeClick(View view) {
        Intent intent = new Intent(this, QRScannerActivity.class);
        startActivity(intent);
    }


    /* Callback for adding a Seminar */
    public void addSeminarClick(View view) {
        final Context context = this;
        final EditText seminarNameInput = new EditText(context);
        final Map<String, String> params = new HashMap<>();

        /* Defines the Listener of seminar name input event */
        class OnSeminarInput implements AlertDialog.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String seminarName = seminarNameInput.getText().toString();
                params.put("name", seminarName);
                ServerConnection sc = ServerConnection.getInstance(context);

                class OnSeminarAddition implements Response.Listener<String> {
                    @Override
                    public void onResponse(String response) {
                        JSONObject obj;
                        String message;
                        try {
                            obj = new JSONObject(response);
                            if (obj.getString("success").equals ("true")) {
                                message = context.getString(R.string.successful_seminar_addition);
                            }
                            else {
                                message = context.getString(R.string.unsuccessful_seminar_addition);
                            }
                        } catch (JSONException e) {
                            message = context.getString(R.string.blame_server);
                        }
                        ScreenUtils.showMessaDialog(context, message, null);
                        fetchSeminarList();
                    }
                }

                class OnError implements Response.ErrorListener {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = getString(R.string.no_server_connection);
                        ScreenUtils.showMessaDialog(context, message, null);
                    }
                }

                sc.addSerminar(new OnSeminarAddition(), new OnError(), params);
            }
        }

        /* shows input dialog message */
        String message = getString(R.string.type_seminar_name);
        ScreenUtils.showInputDialog(this, message,seminarNameInput, new OnSeminarInput(), null);
    }


    /* Sends a server request to get user name and sets it on a textview */
    private void fetchUserName () {
        final Context context = this;
        final TextView userNameT = (TextView) findViewById(R.id.userNameTextView);

        /* Listener to server request for getting an user info */
        class FetchUserResponseListener implements Response.Listener<String> {
            @Override
            public void onResponse (String response) {
                JSONObject obj;
                String name = "";
                try {
                    obj = new JSONObject(response);
                    JSONObject data = obj.getJSONObject("data");
                    name = data.getString("name");
                }
                catch (Exception e) {
                    String message = getString(R.string.blame_server);
                    ScreenUtils.showMessaDialog(context, message, null);
                }
                userNameT.setText(name);
                User u = AppUser.getCurrentUser();
                u.setName(name);
            }
        }

        /* Listener for error in fetch user request */
        class FetchUserErrorListener implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = getString(R.string.no_server_connection);
                ScreenUtils.showMessaDialog(context, message, null);
            }
        }

        User u = AppUser.getCurrentUser();
        if (u.getName() == null || u.getName().equals("")) {
            ServerConnection sc = ServerConnection.getInstance(this);
            sc.fetchUser(new FetchUserResponseListener(), new FetchUserErrorListener(), u.getNusp(),
                    u.getUserType());
        }
        else if  (!u.getName().equals(userNameT.getText())) {
            userNameT.setText(u.getName());
        }
        LinearLayout layout = (LinearLayout) findViewById(R.id.homeLayout);
        ScreenUtils.enableDisableView(layout, true);
    }


    /* Fetches seminars from server and populates view with them */
    private void fetchSeminarList() {
        final Context context = this;

        /* Listener to server request for getting seminar list */
        class FetchSeminarResponseListener implements Response.Listener<String> {
            @Override
            public void onResponse(String response) {
                JSONObject respJO;
                try {
                    respJO = new JSONObject(response);
                    homeSeminarList = new SeminarList(respJO);
                    updateSeminarListView();
                }
                catch (Exception e) {
                    String message = getString(R.string.blame_server);
                    ScreenUtils.showMessaDialog(context, message, null);
                }
            }
        }

        /* Error listener to server request for getting seminar list */
        class FetchSeminarErrorListener implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = getString(R.string.no_server_connection);
                ScreenUtils.showMessaDialog(context, message, null);
            }
        }

        ServerConnection sc = ServerConnection.getInstance(this);
        sc.fetchSeminars(new FetchSeminarResponseListener(), new FetchSeminarErrorListener());
    }


    /* Callback for updating view whenever you click on a seminar */
    private class SeminarClickListener implements View.OnClickListener {
        Seminar seminar;
        Context context;

        SeminarClickListener(Context c, Seminar s) {
            this.context = c;
            this.seminar = s;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, SeminarActivity.class);
            intent.putExtra("seminar_id", seminar.getId());
            startActivity(intent);
        }
    }

    private class SeminarListButton extends ListButton {

        private SeminarListButton(Context c, SeminarClickListener l) {
            super (c);
            this.setOnClickListener(l);
        }

        private SeminarListButton(Context c, Seminar s) {
            this(c, new SeminarClickListener(c, s));
            setText(s.getName());
        }
    }


    /* Updates view with seminars fetched from server */
    private void updateSeminarListView() {
        Seminar[] seminars = homeSeminarList.getSeminarArray();
        LinearLayout linearView = (LinearLayout) findViewById(R.id.seminarList);
        linearView.removeAllViews();
        for (Seminar s : seminars) {
            Button seminarButton = new SeminarListButton(this, s);
            linearView.addView(seminarButton);
        }
    }
}