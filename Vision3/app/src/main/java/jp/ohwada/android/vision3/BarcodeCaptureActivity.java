/**
 * Vision Sample
 * Barcode Detection
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision3;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.Tracker;

import java.io.IOException;


import jp.ohwada.android.vision3.ui.CameraSourcePreview;
import jp.ohwada.android.vision3.ui.GraphicOverlay;


import jp.ohwada.android.vision3.util.CameraPerm;
import jp.ohwada.android.vision3.util.Camera2Source;
import jp.ohwada.android.vision3.util.ToastMaster;


/**
 * Activity for Barcode Detection
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/barcode-reader/app/src/main/java/com/google/android/gms/samples/vision/barcodereader
 */
public final class BarcodeCaptureActivity extends Activity implements BarcodeGraphicTracker.BarcodeUpdateListener {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "BarcodeCaptureActivity";


    // constants used to pass extra data in the intent
    public static final String EXTRA_KEY_AUTO_FOCUS = "AutoFocus";
    public static final String EXTRA_KEY_USE_FLASH = "UseFlash";
    public static final String EXTRA_KEY_BARCODE = "Barcode";


    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;


    private Camera2Source mCamera2Source;

    private CameraSourcePreview mPreview;

    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;


    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    private CameraPerm mCameraPerm;

    // barcode detect 
    private boolean isDetectRunning = false;


/**
   * extra data in the intent
   */
    private boolean isAutoFocus;
    private boolean isUseFlash;


/**
   * Initializes the UI and creates the detector pipeline.
   */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.barcode_capture);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);

        Button btnDetect = (Button) findViewById(R.id.Button_detect);
            btnDetect .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detectBarcode();
                }
            }); // btnDetect 

        // read parameters from the intent used to launch the activity.
        isAutoFocus = getIntent().getBooleanExtra(EXTRA_KEY_AUTO_FOCUS, false);
         isUseFlash = getIntent().getBooleanExtra(EXTRA_KEY_USE_FLASH, false);

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        mCameraPerm = new CameraPerm(this);

        Snackbar.make(mGraphicOverlay, "Tap to capture. Pinch/Stretch to zoom",
                Snackbar.LENGTH_LONG)
                .show();

 } // onCreate


    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Check for the camera permission before accessing the camera.
        if( mCameraPerm.requestCameraPermissions() ) {
                return;
        }
        startCameraSource();
    }


    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }


    /**
     * Releases the resources associated with the camera source, 
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }


/**
   * Callback for the result from requesting permissions. 
  */
@Override
public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        mCameraPerm.onRequestPermissionsResult(requestCode, permissions, grantResults);

        startCameraSource();

} // onRequestPermissionsResult


 /**
  * onTouchEvent
  */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        log_d("onTouchEvent");
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        if (b || c) return true;

        return super.onTouchEvent(e);

} // onTouchEvent


/**
 * BarcodeGraphicTracker.BarcodeUpdateListener
 * onBarcodeDetected
 */
    @Override
    public void onBarcodeDetected(Barcode barcode) {
        log_d("onBarcodeDetected");
        //do something with barcode data returned
    }


/**
 * detectBarcode
 */ 
private void detectBarcode() {
    if(isDetectRunning) {
        isDetectRunning = false;
        pauseDetect();
        showToast("pause detect");
    } else {
        isDetectRunning = true;
        resumeDetect() ;
        showToast("resume detect");
    }
} // detectBarcode


 /**
 * resumeDetect
 */
private void resumeDetect() {
    setDetectRuning();
} // resumeDetect


 /**
 * pauseDetect
 */
private void pauseDetect() {
    setDetectRuning();
    if(mGraphicOverlay != null) {
        mGraphicOverlay.clear();
    }
} // pauseDetect


 /**
 * setDetectRuning
 */
private void setDetectRuning() {
    if(mCamera2Source != null) {
        mCamera2Source.setDetectRunning(isDetectRunning);
    }
    if(mGraphicOverlay != null) {
        mGraphicOverlay.setDrawRuning(isDetectRunning);
    }
} // setDetectRuning


/**
  * starts the camera source
  */
    private void startCameraSource() {
            log_d("startCameraSource");
        checkGooglePlayServicesAvailable();
        Camera2Source cameraSource = createCameraSourceBack();
        if (cameraSource != null) {
            mCamera2Source = cameraSource;
                mPreview.start(mCamera2Source, mGraphicOverlay);
        }

} // startCameraSource


/**
 * stopCameraSource
 */ 
    private void stopCameraSource() {
        if (mPreview != null) {
            mPreview.stop();
        }
    } // stopCameraSource


/**
 * a barcode detector is created to track barcodes
 */
    private BarcodeDetector createBarcodeDetector() {
        log_d("createBarcodeDetector");

        // An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();

        barcodeDetector.setProcessor( createBarcodeProcessor() );


        if (!barcodeDetector.isOperational()) {
            // check if the required native libraries are currently
            log_d( "Detector dependencies are not yet available.");

            // Check for low storage.  
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                String msg = getString(R.string.low_storage_error);
                showToast(msg);
                log_d(msg);
            }
        } // if (!barcodeDetector.isOperational()) 
        return barcodeDetector;

} // createBarcodeDetector


/**
 * createBarcodeProcessor
 */ 
private MultiProcessor<Barcode> createBarcodeProcessor() {
            log_d("createBarcodeProcessor");

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory( mGraphicOverlay, this);

        MultiProcessor.Builder<Barcode> builder = new MultiProcessor.Builder<Barcode>(barcodeFactory);

        MultiProcessor<Barcode> processor = builder.build();
        return processor;
} // createBarcodeProcessor


/**
 * creates the camera source with back camera
 */
private Camera2Source createCameraSourceBack() {

        BarcodeDetector barcodeDetector = createBarcodeDetector();

        // Note that this uses a higher resolution in comparison
        // to other detection 
        Camera2Source.Builder builder = new Camera2Source.Builder(this, barcodeDetector)
                .setFacing(Camera2Source.CAMERA_FACING_BACK)
                 .setErrorCallback(cameraErrorCallback);

    if(isAutoFocus) {
            builder = builder.setFocusMode(Camera2Source.CAMERA_AF_CONTINUOUS_PICTURE);
    }

    if(isUseFlash) {
            builder = builder.setFlashMode(Camera2Source.CAMERA_FLASH_ON);
    }

        Camera2Source cameraSource = builder.build();
        return cameraSource;

    } // createCameraSourceBack



 /**
  * check that the device has play services available
  */
    private void checkGooglePlayServicesAvailable() {
           log_d("checkGooglePlayServicesAvailable");
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

    } // checkGooglePlayServicesAvailable


/**
  * Shows an error message dialog.
 */
private void showErrorDialog(String msg) {
             new AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setPositiveButton(R.string.button_ok, null)
                    .show();
} // showErrorDialog


/**
  * showErrorDialog on the UI thread.
 */
private void showErrorDialog_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorDialog(msg);
                }
    });
} // showErrorDialog_onUI

/**
 * showToast
 */
protected void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast

/**
  * ShowToast on the UI thread.
 */
private void showToast_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(msg);
                }
    });
} // showToast_onUI


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * CameraErrorCallback
 */ 
 Camera2Source.ErrorCallback cameraErrorCallback = new Camera2Source.ErrorCallback() {

        @Override
        public void onError(String msg) {
                stopCameraSource();
                showErrorDialog_onUI(msg);
        }

    }; // CameraErrorCallback 


/**
 * class CaptureGestureListener
  * find the Barcode closest to the tapped position
 */ 
    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            log_d("onSingleTapConfirmed");
            boolean ret = procSingleTapConfirmed(event.getRawX(), event.getRawY());
            if (ret) return true;
            return super.onSingleTapConfirmed(event);
        }

} // class CaptureGestureListener


/**
  * find the Barcode closest to the tapped position
  * and return Barcode value to the calling Activity
 */
private boolean procSingleTapConfirmed(float rawX, float rawY) {
        log_d("procSingleTapConfirmed");
        Barcode barcode = findBarcode(rawX, rawY);
        if (barcode == null) return false;

            // return Barcode value to the calling Activity
            Intent data = new Intent();
            data.putExtra(EXTRA_KEY_BARCODE, barcode);
            setResult(CommonStatusCodes.SUCCESS, data);
            showToast( "Barcode: " + barcode.displayValue );
            finish();
            return true;

} // procSingleTapConfirmed


/**
  * find the Barcode closest to the tapped position
 */
    private Barcode findBarcode(float rawX, float rawY) {
            log_d("findBarcode");
        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        // find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);  // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestDistance = distance;
            }
        } // for
    return best;
} // findBarcode


/**
 * class ScaleListener
 * TODO : zoom Camera when expand the screen
 */ 
    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

/**
  * Responds to scaling events for a gesture in progress.
  */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            log_d("onScale");
            return false;
        }

/**
  * Responds to the beginning of a scaling gesture. 
  */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            log_d("onScaleBegin");
            return true;
        }

/**
  * Responds to the end of a scale gesture. 
  */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            log_d("onScaleEnd");
            // TODO
            //mCameraSource.doZoom(detector.getScaleFactor());
        }

} // class ScaleListener


} // BarcodeCaptureActivity
