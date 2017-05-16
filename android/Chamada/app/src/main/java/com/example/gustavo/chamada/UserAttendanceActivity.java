package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserAttendanceActivity extends Activity {

    private SeminarList userAttendanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attendance);

        LinearLayout seminarActivityLayout = (LinearLayout)
                findViewById(R.id.userAttendanceActivityLayout);
        ScreenUtils.enableDisableView(seminarActivityLayout, false);
        fetchAttendanceList();
    }


    /* Asks server for complete list of seminars attended by the user and shows it on the
    * LinearLayout inside the Scroll View*/
    void fetchAttendanceList() {
        final Context context = this;
        Map<String, String> params = new HashMap<>();
        User u = AppUser.getCurrentUser();
        params.put("nusp", u.getNusp());


        /* Listeners for fetching of seminars user attended */
        class OnResponse implements Response.Listener<String> {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    SeminarList studentList = new SeminarList(obj,
                            SeminarList.USER_ATTENDANCE_SEMINAR_LIST);
                    updateSeminarsListView(studentList);
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

        ServerConnection sc = ServerConnection.getInstance(this);
        sc.fetchUserAttendance(new OnResponse(), new OnError(), params);
    }


    private class SeminarListButton extends ListButton {

        private Context context;
        private Seminar seminar;

        class OnSeminarFetch implements Response.Listener<String> {

            private Button seminarButton;

            OnSeminarFetch(Button bt) {
                this.seminarButton = bt;
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
                seminarButton.setText(name);
            }
        }

        class OnErrorFetchingSeminar implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = context.getString(R.string.no_server_connection);
                ScreenUtils.showMessaDialog(context, message, null);
            }
        }

        public SeminarListButton(Context context, Seminar seminar) {
            super(context);
            this.context = context;
            this.seminar = seminar;
            ServerConnection sc = ServerConnection.getInstance(context);
            sc.fetchSeminarInfo(new OnSeminarFetch(this), new OnErrorFetchingSeminar(),
                    seminar.getId());
        }
    }

    void updateSeminarsListView(SeminarList semList) {
        final Seminar[] seminarsArray = semList.getSeminarArray();
        final LinearLayout linearView = (LinearLayout) findViewById(R.id.userAttendanceListView);
        linearView.removeAllViews();

        for (Seminar s : seminarsArray) {
            Button seminarButton = new SeminarListButton(this, s);
            linearView.addView(seminarButton);
        }

        LinearLayout seminarActivityLayout = (LinearLayout)
                findViewById(R.id.userAttendanceActivityLayout);
        ScreenUtils.enableDisableView(seminarActivityLayout, true);
    }
}
