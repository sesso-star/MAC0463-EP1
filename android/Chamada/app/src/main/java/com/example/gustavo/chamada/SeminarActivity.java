package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;


public class SeminarActivity extends Activity {

    private Seminar mySeminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seminar);

        String seminarId = getIntent().getExtras().getString("seminar_id");
        fetchSeminar(seminarId);
        fetchSeminarAttendance(seminarId);
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
                LinearLayout seminarActivityLayout = (LinearLayout)
                        findViewById(R.id.seminarActivityLayout);
                ScreenUtils.enableDisableView(seminarActivityLayout, true);
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
    }
}
