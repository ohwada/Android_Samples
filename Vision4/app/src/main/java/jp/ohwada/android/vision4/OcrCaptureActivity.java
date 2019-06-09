/**
 * Vision Sample
 * OCR Reader
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision4;

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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;


import jp.ohwada.android.vision4.util.Camera2Source;
import jp.ohwada.android.vision4.util.CameraPerm;
import jp.ohwada.android.vision4.util.ToastMaster;

import jp.ohwada.android.vision4.ui.CameraSourcePreview;
import jp.ohwada.android.vision4.ui.GraphicOverlay;


/**
 * Activity for the multi-tracker app.  
 * This app detects text and displays the value with the rear facing camera. 
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/ocr-reader
 */
public final class OcrCaptureActivity extends AppCompatActivity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "OcrCaptureActivity";

  // Constants used to pass extra data in the intent
    public static final String EXTRA_KEY_FOCUS = "AutoFocus";
    public static final String EXTRA_KEY_FLASH = "UseFlash";
    public static final String EXTRA_KEY_TEXT = "String";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

  



    private Camera2Source mCamera2Source;
    private CameraSourcePreview mPreview;

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    private CameraPerm mCameraPerm;

/**
   * extra data in the intent
   */
    private boolean isAutoFocus;
    private boolean isUseFlash;

    // OCR detect 
    private boolean isDetectRunning = false;


/**
 * Initializes the UI and creates the detector pipeline.
  */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.ocr_capture);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);

        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);

        Button btnDetect = (Button) findViewById(R.id.Button_detect);
            btnDetect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detectOcr();
                }
            }); // btnDetect

        // read parameters from the intent used to launch the activity.
        isAutoFocus = getIntent().getBooleanExtra(EXTRA_KEY_FOCUS, false);
       isUseFlash = getIntent().getBooleanExtra(EXTRA_KEY_FLASH, false);

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        mCameraPerm = new CameraPerm(this);

        Snackbar.make(mPreview, "Tap to capture. Pinch/Stretch to zoom",
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
    } // onResume


/**
  * Stops the camera.
  */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    } // onPause


/**
   * Releases the resources associated with the camera source
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
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        if (b || c) return true;

        return super.onTouchEvent(e);

    } // onTouchEvent


/**
 * detectOcr
 */ 
private void detectOcr() {
    if(isDetectRunning) {
        isDetectRunning = false;
        pauseDetect();
        showToast("pause detect");
    } else {
        isDetectRunning = true;
        resumeDetect() ;
        showToast("resume detect");
    }
} // detectOcr


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
     * starts the camera source, if it exists.  
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
 * creates the camera source with back camera
 */
private Camera2Source createCameraSourceBack() {

        TextRecognizer textRecognizer = createTextRecognizer();

        Camera2Source.Builder builder  =
                new Camera2Source.Builder(this, textRecognizer)
                .setFacing(Camera2Source.CAMERA_FACING_BACK)
                .setDetectRunning(true)
                 .setErrorCallback(cameraErrorCallback);

        if(isAutoFocus) {
                builder = builder.setFocusMode(Camera2Source.CAMERA_AF_CONTINUOUS_PICTURE);
        }

        if(isUseFlash) {
                builder = builder.setFlashMode(Camera2Source.CAMERA_FLASH_AUTO);
        }

        Camera2Source cameraSource = builder.build();
        return cameraSource;
} // createCameraSourceBack


/**
  * create a text recognizer to find text.
  */
    private TextRecognizer createTextRecognizer() {

        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        OcrDetectorProcessor processor = new OcrDetectorProcessor(mGraphicOverlay);
        textRecognizer.setProcessor(processor);

        if (textRecognizer.isOperational()) {
            // The first time that an app using a Vision API is installed on a device
            return textRecognizer;
        }

        log_d("Detector dependencies are not yet available.");

        // Check for low storage.  
        IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

        if (hasLowStorage) {
                String msg = getString(R.string.low_storage_error);
                showToast(msg);
                log_d(msg);
        } // if 
        return textRecognizer;

} // createTextRecognizer


 /**
  * check that the device has play services available
  */
    private void checkGooglePlayServicesAvailable() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        } // if
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
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * CameraErrorCallback
 */ 
 Camera2Source.ErrorCallback cameraErrorCallback = new Camera2Source.ErrorCallback() {

/**
 * onError
 */ 
        @Override
        public void onError(String msg) {
                stopCameraSource();
                showErrorDialog_onUI(msg);
        }

}; // CameraErrorCallback 


/**
 * CaptureGestureListener
 */ 
    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

/**
 * onSingleTapConfirmed
 */ 
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            log_d("onSingleTapConfirmed");
            boolean ret = procSingleTapConfirmed(event.getRawX(), event.getRawY());
            if(ret) return true; 
            return super.onSingleTapConfirmed(event);
        }

} // CaptureGestureListener


/**
 *  capture the first TextBlock under the tap location 
     * and return it to the Initializing Activity.
 */
private boolean procSingleTapConfirmed(float rawX, float rawY) {
        log_d("procSingleTapConfirmed");
        // find the first TextBlock under the tap location
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        if (graphic == null) {
            log_d("no text detected");
            return false;
        }
        TextBlock text = graphic.getTextBlock();
        if (text == null ) {
                log_d("text data is null");
                return false;
        }
        String value = text.getValue();
        if (value == null) {
            log_d("text data is null");
                return false;
        }
        // return TextBlock to the Initializing Activity.
        Intent data = new Intent();
        data.putExtra(EXTRA_KEY_TEXT, value);
        setResult(CommonStatusCodes.SUCCESS, data);
        finish();
        return true;

} // procSingleTapConfirmed


/**
 * ScaleListener
 */ 
    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

/**
 * Responds to scaling events for a gesture in progress.
  */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

/**
  * Responds to the beginning of a scaling gesture. 
  */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

/**
  * Responds to the end of a scale gesture. 
  */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            // TODO
            //mCameraSource.doZoom(detector.getScaleFactor());
        }

} // ScaleListener


} // class OcrCaptureActivity
