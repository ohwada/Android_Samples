/**
 * Vision Sample
 * MultiTracker using Camera2 API and Vision API
 * 2019-02-01 K.OHWADA
 */


package jp.ohwada.android.vision7;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


//import com.google.android.gms.vision.CameraSource;

import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;


//import com.google.android.gms.samples.vision.face.multitracker.ui.camera.CameraSourcePreview;
//import com.google.android.gms.samples.vision.face.multitracker.ui.camera.GraphicOverlay;

import  jp.ohwada.android.vision7.util.Camera2Source;
import jp.ohwada.android.vision7.util.CameraPerm;
import jp.ohwada.android.vision7.util.ToastMaster;

import jp.ohwada.android.vision7.ui.CameraSourcePreview;
import jp.ohwada.android.vision7.ui.GraphicOverlay;


/**
 * class MainActivity
 * Activity for the multi-tracker app.  
 * This app detects faces and barcodes with the rear facing
 * camera, 
 * and draws overlay graphics to indicate the position, size, and ID of each face and barcode.
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/multi-tracker
 */

//public final class MultiTrackerActivity extends AppCompatActivity {
public final class MainActivity extends Activity {


     // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "MainActivity";


/**
 *  request code for Google  Play Services API
  */
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    //private static final int RC_HANDLE_CAMERA_PERM = 2;

/**
 * Class Instance that operates the Camera Device
  */
    private Camera2Source mCamera2Source = null;


/**
 *  
Class Instance to display preview
  */
    private CameraSourcePreview mPreview;

/**
 *  Class Instance for overlay
  */
    private GraphicOverlay mGraphicOverlay;

/**
 *  Class Instance that t operates the Camera Permission
  */
    private CameraPerm mCameraPerm;

/**
 *  Flag whether to detect the Face
  */
    private boolean isDetectRunning = false;


 /**
 * onCreate
  * Initializes the UI and creates the detector pipeline.
  */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //setContentView(R.layout.main);
        setContentView(R.layout.activity_main);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);


        Button btnDetect = (Button) findViewById(R.id.Button_detect);
            btnDetect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detectFace();
                }
            }); // btnDetect

        // utility
        mCameraPerm = new CameraPerm(this);


        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.

}


/**
  * onResume
  * Restarts the camera.
  */
    @Override
    protected void onResume() {
        super.onResume();
        if( mCameraPerm.requestCameraPermissions() ) {
                log_d("requestCameraPermissions");
                return;
        }
        startCameraSource();
    }


/**
  * onPause
  * Stops the camera.
 */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }


/**
  * onDestroy
  * Releases the resources associated with the camera source, the associated detectors, and the
  * rest of the processing pipeline.
  */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera2Source != null) {
            mCamera2Source.release();
        }
    }



/**
  * onRequestPermissionsResult
  * Callback for the result from requesting permissions. 
  * This method is invoked for every call on {@link #requestPermissions(String[], int)}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        mCameraPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
        startCameraSource();

} // onRequestPermissionsResult


 /**
 * detectFace
 */
private void detectFace() {
    if(isDetectRunning) {
        isDetectRunning = false;
        setDetectRuning();
        clearGraphicOverlay();
        showToast("pause detect");
    } else {
        isDetectRunning = true;
        setDetectRuning();
        showToast("resume detect");
    }
} // detectFace


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
 * clearGraphicOverlay
 */
private void clearGraphicOverlay() {
    setDetectRuning();
    if(mGraphicOverlay != null) {
        mGraphicOverlay.clear();
    }
} // clearGraphicOverlay

 
    /**
     * createCameraSource
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private Camera2Source createCameraSource() {

        Context context = getApplicationContext();

        // A multi-detector groups the two detectors together as one detector.  
        MultiDetector multiDetector = createMultiDetector();

        // Creates and starts the camera.  
        // Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        Camera2Source camera2Source = new Camera2Source.Builder(context, multiDetector)
                .setFacing(Camera2Source.CAMERA_FACING_BACK)
                .setErrorCallback(cameraErrorCallback)
                .build();

        return camera2Source;
    }


/**
 * createMultiDetector
 */ 
    private MultiDetector createMultiDetector() {

        Context context = getApplicationContext();

        // A face detector is created to track faces.  An associated multi-processor instance
        // is set to receive the face detection results, track the faces, and maintain graphics for
        // each face on screen.  The factory is used by the multi-processor to create a separate
        // tracker instance for each face.
        FaceDetector faceDetector = new FaceDetector.Builder(context).build();
        FaceTrackerFactory faceFactory = new FaceTrackerFactory(mGraphicOverlay);
        faceDetector.setProcessor(
                new MultiProcessor.Builder<>(faceFactory).build());

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        // A multi-detector groups the two detectors together as one detector.  All images received
        // by this detector from the camera will be sent to each of the underlying detectors, which
        // will each do face and barcode detection, respectively.  The detection results from each
        // are then sent to associated tracker instances which maintain per-item graphics on the
        // screen.
        MultiDetector multiDetector = new MultiDetector.Builder()
                .add(faceDetector)
                .add(barcodeDetector)
                .build();

        if (!multiDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            log_d("Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                String msg = getString(R.string.low_storage_error);
                showToast(msg);
                log_d(msg);
            }
        }

    return multiDetector;
} // createMultiDetector



/**
  * startCameraSource
  * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
 * (e.g., because onResume was called before the camera source was created), this will be called
  * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        Camera2Source camera2Source = createCameraSource();
        if (camera2Source != null) {
                mCamera2Source = camera2Source;
                mPreview.start(mCamera2Source, mGraphicOverlay);
        }
} // startCameraSource


/**
 * stopCameraSource
 */ 
    private void stopCameraSource() {
        log_d("stopCameraSource");
        mPreview.stop();
    } // stopCameraSource


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
 * showToast
 */
protected void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


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
                stopCameraSource() ;
                showErrorDialog_onUI(msg);
        }
    }; // CameraErrorCallback 


} // class MainActivity

