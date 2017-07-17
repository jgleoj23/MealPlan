package joseph.com.mealplan;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScannerActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();
    private static final int PHOTO_REQUEST = 10;
    private TextView scanResults;
    private BarcodeDetector detector;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;


    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN
    };
    private static int currentColorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        final TextView txtView = (TextView) findViewById(R.id.txtContent);

        requestCameraPermission();

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int format = Barcode.DATA_MATRIX | Barcode.QR_CODE;
                BarcodeDetector detector = new BarcodeDetector.Builder(ScannerActivity.this)
                                                                .build();
                final GraphicOverlay graphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

                detector.setProcessor(new MultiProcessor.Builder<>(new MultiProcessor.Factory<Barcode>() {

                    @Override
                    public Tracker<Barcode> create(Barcode barcode) {

                        return new GraphicTracker<>(graphicOverlay, new TrackedGraphic<Barcode>(graphicOverlay) {
                            private Barcode barcode;

                            private Paint rectPaint;
                            private Paint textPaint;

                            {
                                rectPaint = new Paint();

                                currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
                                int selectedColor = COLOR_CHOICES[currentColorIndex];

                                rectPaint.setColor(selectedColor);
                                rectPaint.setStyle(Paint.Style.STROKE);
                                rectPaint.setStrokeWidth(4f);

                                textPaint = new Paint();
                                textPaint.setColor(selectedColor);
                                textPaint.setTextSize(36f);
                            }

                            @Override
                            void updateItem(Barcode item) {
                                barcode = item;
                                postInvalidate();
                            }

                            @Override
                            public void draw(Canvas canvas) {
                                if (barcode == null) {
                                    return;
                                }

                                Log.i(TAG, "drawing barcode");

                                // Draws the bounding box around the barcode.
                                RectF rect = new RectF(barcode.getBoundingBox());
                                rect.left = translateX(rect.left);
                                rect.top = translateY(rect.top);
                                rect.right = translateX(rect.right);
                                rect.bottom = translateY(rect.bottom);
                                canvas.drawRect(rect, rectPaint);

                                // Draws a label at the bottom of the barcode indicate the barcode value that was detected.
                                canvas.drawText(barcode.rawValue, rect.left, rect.bottom, textPaint);
                            }
                        });
                    }
                }).build());

                // It is a parallelogram! This is why we pay attention in math class
                RectF rect;

                Canvas canvas;



                if (!detector.isOperational()) {
                    Log.i(TAG, "not operational");
                    txtView.setText("Could not set up the detector!");
                } else {
                    Log.i(TAG, "I'm afraid it is fully operational");
                    CameraSource cameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                            .setFacing(CameraSource.CAMERA_FACING_BACK)
                            .setRequestedPreviewSize(1600, 1024)
                            .setRequestedFps(15.0f)
                            .build();

                    // check that the device has play services available.
                    int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                            getApplicationContext());
                    if (code != ConnectionResult.SUCCESS) {
                        Dialog dlg =
                                GoogleApiAvailability.getInstance().getErrorDialog(ScannerActivity.this, code,
                                                                                   RC_HANDLE_GMS);
                        dlg.show();
                    }

                    CameraSourcePreview preview = (CameraSourcePreview) findViewById(R.id.preview);
                    // recognize 1D barcodes
                    // Draw UI on them

                    if (cameraSource != null) {
                        try {
                            preview.start(cameraSource, graphicOverlay);
                        } catch (IOException e) {
                            Log.e(TAG, "Unable to start camera source.", e);
                            cameraSource.release();
                            cameraSource = null;
                        }
                    }
                }
            }
        });
    }


    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                 Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        ActivityCompat.requestPermissions(this, permissions,
                                          RC_HANDLE_CAMERA_PERM);
    }


    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
//            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
               .setMessage(R.string.no_camera_permission)
               .setPositiveButton(R.string.ok, listener)
               .show();
    }
}
