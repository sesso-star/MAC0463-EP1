package com.example.gustavo.chamada;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.Line;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.QRCode;


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


    /* Listener to the qr code click. It should create a QR code image and show it to the user */
    public void qrCodeClick(View view) {
        String id = mySeminar.getId();
        Bitmap qrCodeBitmap = TextToImageEncode(id);
        ImageView imgView = new ImageView(this);
        imgView.setImageBitmap(qrCodeBitmap);
        LinearLayout thisLayout = (LinearLayout) findViewById(R.id.seminarActivityLayout);
        thisLayout.setGravity(Gravity.CENTER);
        thisLayout.removeAllViews();
        thisLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.White));
        thisLayout.addView(imgView);
    }


    /* Encodes a text to a bitmap QR code */
    private Bitmap TextToImageEncode(String text) {
        BitMatrix bitMatrix;
        final int QR_CODE_SIZE = 700;
        try {
            bitMatrix = new MultiFormatWriter().encode(text,
                    BarcodeFormat.DATA_MATRIX.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, null);

        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        } catch (WriterException e) {
            return null;
        }

        int[] pixels = new int[QR_CODE_SIZE * QR_CODE_SIZE];
        for (int y = 0; y < QR_CODE_SIZE; y++) {
            int offset = y * QR_CODE_SIZE;
            for (int x = 0; x < QR_CODE_SIZE; x++) {
                if (bitMatrix.get(x, y))
                    pixels[offset + x] = ContextCompat.getColor(this, R.color.mainBlueish);
                else
                    pixels[offset + x] = ContextCompat.getColor(this, R.color.White);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(QR_CODE_SIZE, QR_CODE_SIZE, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, QR_CODE_SIZE, 0, 0, QR_CODE_SIZE, QR_CODE_SIZE);
        return bitmap;
    }


    public void changeSeminarNameClick(View view) {

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
