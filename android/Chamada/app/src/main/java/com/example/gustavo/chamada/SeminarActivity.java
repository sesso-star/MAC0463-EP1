package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SeminarActivity extends Activity {

    private Seminar mySeminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seminar);

        String seminarId = getIntent().getExtras().getString("seminar_id");
        fetchSeminar(seminarId);
        LinearLayout seminarActivityLayout = (LinearLayout)
                findViewById(R.id.seminarActivityLayout);
        ScreenUtils.enableDisableView(seminarActivityLayout, false);
    }


    public void fetchSeminar(String id) {
        final Context context = this;
        final TextView seminarNameView = (TextView) findViewById(R.id.seminarNameView);

        class OnFetchedSeminar implements Response.Listener<String> {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    mySeminar = new Seminar(obj);
                    seminarNameView.setText(mySeminar.getName());
                }
                catch (Exception e) {
                    String message = context.getString(R.string.blame_server);
                    ScreenUtils.showMessaDialog(context, message, null);

                }
                fetchSeminarAttendance(mySeminar.getId());
            }
        }

        class OnErrorFetchingSeminar implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = context.getString(R.string.no_server_connection);
                ScreenUtils.showMessaDialog(context, message, null);
                LinearLayout seminarActivityLayout = (LinearLayout)
                        findViewById(R.id.seminarActivityLayout);
                ScreenUtils.enableDisableView(seminarActivityLayout, true);
            }
        }

        ServerConnection sc = ServerConnection.getInstance(this);
        sc.fetchSeminarInfo(new OnFetchedSeminar(), new OnErrorFetchingSeminar(), id);
    }


    private void fetchSeminarAttendance(String seminarId) {
        final Context context = this;

        /* Listener for fetching list of students who attended this seminar */
        class OnResponse implements Response.Listener<String> {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    AttendanceList studentList = new AttendanceList(obj);
                    updateStudentListView(studentList);
                }
                catch (Exception e) {
                    String message = context.getString(R.string.blame_server);
                    ScreenUtils.showMessaDialog(context, message, null);
                }
            }
        }

        class OnError implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = context.getString(R.string.no_server_connection);
                ScreenUtils.showMessaDialog(context, message, null);
            }
        }

        Map<String, String> params = new HashMap<>();
        params.put("seminar_id", mySeminar.getId());
        ServerConnection sc = ServerConnection.getInstance(context);
        sc.fetchSeminarAttendance(new OnResponse(), new OnError(), params);
    }


    private class StudentListButton extends ListButton {

        class OnStudentNameFetch implements Response.Listener<String> {

            /* Saves the button in which we will write the student name */
            private Button studentButton;
            private String nusp;

            OnStudentNameFetch(String nusp, Button bt) {
                this.nusp = nusp;
                studentButton = bt;
            }

            @Override
            public void onResponse(String response) {
                JSONObject obj;
                String name = "";
                try {
                    obj = new JSONObject(response);
                    JSONObject data = obj.getJSONObject("data");
                    name = data.getString("name");
                }
                catch (Exception e) {
                    name = getString(R.string.blame_server);
                }
                studentButton.setText(nusp + " - " + name);
            }
        }

        public StudentListButton(Context c, User u) {
            super(c);
            setText(u.getName());
            ServerConnection sc = ServerConnection.getInstance(c);
            sc.fetchUser(new OnStudentNameFetch(u.getNusp(), this), null, u.getNusp(),
                    u.getUserType());
        }
    }

    void updateStudentListView(AttendanceList students) {
        final Context context = this;
        final User[] studentArray = students.getStudentArray();
        final LinearLayout linearView = (LinearLayout) findViewById(R.id.seminarListLayout);
        linearView.removeAllViews();

        for (User u : studentArray) {
            Button seminarButton = new StudentListButton(context, u);
            linearView.addView(seminarButton);
        }

        LinearLayout seminarActivityLayout = (LinearLayout)
                findViewById(R.id.seminarActivityLayout);
        ScreenUtils.enableDisableView(seminarActivityLayout, true);
    }
}
