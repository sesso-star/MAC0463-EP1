package com.example.gustavo.chamada;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;

public class QRScannerActivity extends Activity {

    /* Code here is inspired on the following android tutorial:
    * https://developer.android.com/training/camera/photobasics.html */
    final private int REQUEST_IMAGE_CAPTURE = 1;
    private BarcodeDetector detector;
    private Uri photoUri;
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
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.gustavo.chamada.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = decodePicture();
//            ImageView iv = (ImageView) findViewById(R.id.imageView);
//            iv.setImageBitmap(bitmap);

            class OnConfirmListener implements AlertDialog.OnClickListener {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                }

            }

            try {
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    for (int i = 0; i < barcodes.size(); i++) {
                        Barcode code = barcodes.valueAt(i);
                        ScreenUtils.showMessaDialog(this, code.rawValue, new OnConfirmListener());
                    }

                    if (barcodes.size() < 1) {
                        String message = getString(R.string.no_qr_found);
                        ScreenUtils.showMessaDialog(this, message, new OnConfirmListener());
                    }
                }
            }
            catch (Exception e) {
                String message = getString(R.string.no_qr_reader);
                ScreenUtils.showMessaDialog(this, message, new OnConfirmListener());
            }
        }
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
