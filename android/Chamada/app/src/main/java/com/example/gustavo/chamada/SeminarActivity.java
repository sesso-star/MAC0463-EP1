package com.example.gustavo.chamada;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


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
        ScreenUtils.setLoadingView(seminarActivityLayout, false);
    }

    private class OnSeminarError implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onBackPressed();
        }
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
                    fetchSeminarAttendance();
                }
                catch (Exception e) {
                    String message = context.getString(R.string.blame_server);
                    ScreenUtils.showMessaDialog(context, message, new OnSeminarError());
                }
            }
        }

        class OnErrorFetchingSeminar implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = context.getString(R.string.no_server_connection);
                ScreenUtils.showMessaDialog(context, message, null);
                LinearLayout seminarActivityLayout = (LinearLayout)
                        findViewById(R.id.seminarActivityLayout);
                ScreenUtils.setLoadingView(seminarActivityLayout, true);
            }
        }

        ServerConnection sc = ServerConnection.getInstance(this);
        sc.fetchSeminarInfo(new OnFetchedSeminar(), new OnErrorFetchingSeminar(), id);
    }


    private void fetchSeminarAttendance() {
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
                    ScreenUtils.showMessaDialog(context, message, new OnSeminarError());
                }
            }
        }

        class OnError implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = context.getString(R.string.no_server_connection);
                ScreenUtils.showMessaDialog(context, message, new OnSeminarError());
            }
        }

        Map<String, String> params = new HashMap<>();
        params.put("seminar_id", mySeminar.getId());
        ServerConnection sc = ServerConnection.getInstance(context);
        sc.fetchSeminarAttendance(new OnResponse(), new OnError(), params);
    }


    /* Listener to the qr code click */
    public void qrCodeClick(View view) {
        if (AppUser.getCurrentUser().isProfessor())
            showQRCode();
        else
            startQRCodeActivity();
    }


    /* Calls QR code activity */
    public void startQRCodeActivity() {
        Intent intent = new Intent(this, QRScannerActivity.class);
        startActivity(intent);
    }


    /* Creates QR code that contains the seminar id and displays it on the screen */
    private void showQRCode() {
        final Context context = this;
        final LinearLayout seminarActivityLayout = (LinearLayout)
                findViewById(R.id.seminarActivityLayout);
        final String id = mySeminar.getId();
        ScreenUtils.setLoadingView(seminarActivityLayout, false);
        clearSeminarView();
        ImageView imgView = new ImageView(context);
        seminarActivityLayout.addView(imgView);

        class createNShowQR extends AsyncTask<ImageView, Void, Bitmap> {
            private ImageView qrImageView;

            @Override
            protected Bitmap doInBackground(ImageView... params) {
                this.qrImageView = params[0];
                Bitmap qrCodeBitmap = TextToImageEncode(id);
                return qrCodeBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                qrImageView.setImageBitmap(result);
                ScreenUtils.setLoadingView(seminarActivityLayout, true);
            }
        }
        new createNShowQR().execute(imgView);
    }


    void clearSeminarView() {
        final LinearLayout seminarActivityLayout = (LinearLayout)
                findViewById(R.id.seminarActivityLayout);
        seminarActivityLayout.setGravity(Gravity.CENTER);
        seminarActivityLayout.removeAllViews();
        seminarActivityLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.White));
    }

    /* Encodes a text to a bitmap QR code */
    private Bitmap TextToImageEncode(String text) {
        BitMatrix bitMatrix;
        final int QR_CODE_SIZE = 600;
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


    /* Listener for the passcode button */
    public void seminarPasscodeClick(View view) {
        if (AppUser.getCurrentUser().isProfessor())
            showSeminarPassword();
        else
            attendWithPasscode();
    }


    /* Clears screen and shows passcode */
    public void showSeminarPassword() {
        TextView passcodeTextView = new TextView(this);
        clearSeminarView();
        String message = getString(R.string.seminar_passcode_is) + "\n" + mySeminar.getPasscode();
        passcodeTextView.setText(message);
        passcodeTextView.setGravity(Gravity.CENTER);
        passcodeTextView.setTextColor(ContextCompat.getColor(this, R.color.mainBlueish));
        passcodeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        final LinearLayout seminarActivityLayout = (LinearLayout)
                findViewById(R.id.seminarActivityLayout);
        seminarActivityLayout.addView(passcodeTextView);
    }


    /**/
    public void attendWithPasscode() {

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
        ScreenUtils.setLoadingView(seminarActivityLayout, true);
    }
}
