package com.example.gustavo.chamada;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.SparseArray;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRScannerActivity extends Activity {

    /* Code here is inspired on the following android tutorial:
    * https://developer.android.com/training/camera/photobasics.html */
    final private int REQUEST_IMAGE_CAPTURE = 1;
    private BarcodeDetector detector;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        /* Starts QR detector */
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        String message = getString(R.string.no_qr_reader);
        if (!detector.isOperational())
            ScreenUtils.showMessaDialog(this, message, null);
        takePhoto();
    }


    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /* make sure there's an camera app to handle the intent */
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                String message = getString(R.string.image_file_error);
                ScreenUtils.showMessaDialog(this, message, null);
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.gustavo.chamada.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    /* Called whenever we finish everything we need to do about qr codes */
    private class OnFinishingReadingQR implements AlertDialog.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onBackPressed();
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Context context = this;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = decodePicture();

            try {
                /* QR code detection */
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    for (int i = 0; i < barcodes.size(); i++) {
                        Map<String, String> params = new HashMap<>();
                        Barcode code = barcodes.valueAt(i);
                        final String seminar_id = code.rawValue;
                        User u = AppUser.getCurrentUser();
                        params.put("seminar_id", seminar_id);
                        params.put("nusp", u.getNusp());

                        class OnAttendanceResponse implements Response.Listener<String> {
                            @Override
                            public void onResponse(String response) {
                                JSONObject obj;
                                String message;
                                try {
                                    obj = new JSONObject(response);
                                    if (obj.getString("success").equals ("true")) {
                                        showConfimedAttendanceMessage(seminar_id);
                                        return;
                                    }
                                    else
                                        message = getString(R.string.unsuccessful_attendance);
                                } catch (JSONException e) {
                                    message = getString(R.string.blame_server);
                                }
                                ScreenUtils.showMessaDialog(context, message,
                                        new OnFinishingReadingQR());
                            }
                        }

                        class OnAttendanceError implements Response.ErrorListener {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String message = getString(R.string.unsuccessful_attendance);
                                ScreenUtils.showMessaDialog(context, message,
                                        new OnFinishingReadingQR());
                            }
                        }

                        ServerConnection sc = ServerConnection.getInstance(this);
                        sc.sendAttendance(new OnAttendanceResponse(), new OnAttendanceError(),
                                params);
                    }

                    if (barcodes.size() < 1) {
                        String message = getString(R.string.no_qr_found);
                        ScreenUtils.showMessaDialog(this, message, new OnFinishingReadingQR());
                    }
                }
            }
            catch (Exception e) {
                String message = getString(R.string.no_qr_reader);
                ScreenUtils.showMessaDialog(this, message, new OnFinishingReadingQR());
            }
        }
        else {
            onBackPressed();
        }
    }


    /* Shows a message dialog confirming semiar attendance */
    public void showConfimedAttendanceMessage(String seminar_id) {
        final Context context = this;

        class OnSeminarNameRequest implements Response.Listener<String> {
            @Override
            public void onResponse(String response) {
                String message;
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    Seminar s = new Seminar(obj.getJSONObject("data"));
                    message = getString(R.string.successful_attendance) + " " +
                            context.getString(R.string.at_seminar) + "\n" + s.getName();
                }
                catch (Exception e) {
                    message = getString(R.string.unsuccessful_attendance);
                }
                ScreenUtils.showMessaDialog(context, message, new OnFinishingReadingQR());
            }
        }

        class OnSeminarNameError implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = getString(R.string.unsuccessful_attendance);
                ScreenUtils.showMessaDialog(context, message, new OnFinishingReadingQR());
            }
        }

        ServerConnection sc = ServerConnection.getInstance(context);
        sc.fetchSeminarInfo(new OnSeminarNameRequest(), new OnSeminarNameError(),seminar_id);
    }


    /* Saves image on app directory. This image is private to the app */
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFilePrefix = "SEMINAR_QR_CODE.jpg";

        File imageFolder = new File(this.getFilesDir(), "images");
        if (!imageFolder.exists())
            if (!imageFolder.mkdir())
                ScreenUtils.showMessaDialog(this, getString(R.string.image_file_error), null);

        File image = File.createTempFile(imageFilePrefix, ".jpg", imageFolder);
        photoPath = image.getAbsolutePath();
        return image;
    }


    /* Opens and scales the picture taken */
    private Bitmap decodePicture() {
        // We need to specify the size of the picture
        int targetW = 500;
        int targetH = 500;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        return bitmap;
    }
}
