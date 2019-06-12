/**
 * Vision Sample
 * GooglyEyes using Camera2 API and Vision API
 * 2019-02-01 K.OHWADA
 */


package jp.ohwada.android.vision6;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.hardware.Camera;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;

import jp.ohwada.android.vision6.util.Camera2Source;
import jp.ohwada.android.vision6.util.CameraPerm;
import jp.ohwada.android.vision6.util.ToastMaster;


import jp.ohwada.android.vision6.ui.CameraSourcePreview;
import jp.ohwada.android.vision6.ui.GraphicOverlay;



/**
 * Activity for Googly Eyes
,  * an app that uses the camera to track faces and superimpose Googly Eyes
 * animated graphics over the eyes. 
 * 
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/googly-eyes
 */
public final class MainActivity extends Activity {


        // debug
	    private final static boolean D = true;
    	private final static String TAG = "Vision";
    	private final static String TAG_SUB = "MainActivity";


/**
 * RequestCode for GoogleApiAvailability
  */
    private static final int RC_HANDLE_GMS = 9001;


/**
 * Key for InstanceState
  */
    private final static String KEY_USE_FRONT_FACING = "UseFrontFacing";


/**
 * Class Instance that operates the Camera Device
  */
    private Camera2Source mCamera2Source;


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
 *  Flag to use front camera
  */
    private boolean isUsingFrontCamera = true;

/**
 *  Flag whether to detect the Face
  */
    private boolean isDetectRunning = false;


/**
  * Initializes the UI and initiates the creation of a face detector.
  */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            isUsingFrontCamera = savedInstanceState.getBoolean(KEY_USE_FRONT_FACING);
        }


        setContentView(R.layout.activity_main);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);


        Button btnFlip = (Button) findViewById(R.id.Button_flip);
           btnFlip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flipCameraFace();
                }
            }); // btnFlipbtnFlip


        Button btnDetect = (Button) findViewById(R.id.Button_detect);
            btnDetect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detectFace();
                }
            }); // btnDetect


        // utility
        mCameraPerm = new CameraPerm(this);

} // onCreate


 /**
  * Saves the camera facing mode, 
  * so that it can be restored after the device is rotated.
 */
@Override
public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_USE_FRONT_FACING, isUsingFrontCamera);
}


/**
 * onResume
 */ 
@Override
protected void onResume() {
        super.onResume();
            log_d("onResume");
        if( mCameraPerm.requestCameraPermissions() ) {
                log_d("requestCameraPermissions");
                return;
        }
        startCameraSource();
} // onResume



/**
 * onPause
 */ 
@Override
protected void onPause() {
        super.onPause();
        stopCameraSource();
} // onPause


/**
 * onDestroy
 */ 
@Override
protected void onDestroy() {
        super.onDestroy();
        stopCameraSource();
        if(mCamera2Source != null) {
            mCamera2Source.release();
        }
} // onDestroy






/**
  * Callback for the result from requesting permissions. 
  * This method is invoked for every call on
  * {@link #requestPermissions(String[], int)}.
 */
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        log_d("onRequestPermissionsResult");
        mCameraPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
        startCameraSource();

} // onRequestPermissionsResult






/**
  * flipCameraFace
  * Toggles between front-facing and rear-facing modes.
  */
private void flipCameraFace() {
                    if(isUsingFrontCamera) {
                        stopCameraSource();
                        isUsingFrontCamera = false;
                        startCameraSource();
                        showToast("flip to back");
                    } else {
                        stopCameraSource();
                        isUsingFrontCamera = true;
                        startCameraSource();
                        showToast("flip to front");
                    }
 } // flipCameraFace


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
    if(mGraphicOverlay != null) {
        mGraphicOverlay.clear();
    }
} // clearGraphicOverlay


/**
  * Creates the face detector and associated processing pipeline to support either front facing
  * mode or rear facing mode.  Checks if the detector is ready to use, and displays a low storage
  * warning if it was not possible to download the face library.
  */
    @NonNull
private FaceDetector createFaceDetector(Context context) {
log_d("createFaceDetector");

        // For both front facing and rear facing modes, the detector is initialized to do landmark
        // detection (to find the eyes), classification (to determine if the eyes are open), and
        // tracking.
        //

        // Use of "fast mode" enables faster detection for frontward faces, at the expense of not
        // attempting to detect faces at more varied angles (e.g., faces in profile).  Therefore,
        // faces that are turned too far won't be detected under fast mode.
        //

        // For front facing mode only, the detector will use the "prominent face only" setting,
        // which is optimized for tracking a single relatively large face.  This setting allows the
        // detector to take some shortcuts to make tracking faster, at the expense of not being able
        // to track multiple faces.
        //
        // Setting the minimum face size not only controls how large faces must be in order to be
        // detected, it also affects performance.  Since it takes longer to scan for smaller faces,
        // we increase the minimum face size for the rear facing mode a little bit in order to make
        // tracking faster (at the expense of missing smaller faces).  But this optimization is less
        // important for the front facing case, because when "prominent face only" is enabled, the
        // detector stops scanning for faces after it has found the first (large) face.

        float minFaceSize = (isUsingFrontCamera)? 0.35f : 0.15f;
 
       FaceDetector detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(isUsingFrontCamera)
                .setMinFaceSize(minFaceSize)
                .build();

        Detector.Processor<Face> processor = createProcessor(detector);
        detector.setProcessor(processor);

        if (!detector.isOperational()) {
                // Note: The first time that an app using face API is installed on a device, GMS will
                // download a native library to the device in order to do detection.  Usually this
                // completes before the app is run for the first time.  But if that download has not yet
                // completed, then the above call will not detect any faces.
                //
                // isOperational() can be used to check if the required native library is currently
                // available.  The detector will automatically become operational once the library
                // download completes on device.
                log_d("Face detector dependencies are not yet available.");

                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                    log_d(getString(R.string.low_storage_error));
                }
        } //  if  detector.isOperational()

        return detector;
} // createFaceDetector


/**
   * createProcessor
  */
private Detector.Processor<Face> createProcessor( FaceDetector detector) {

       Detector.Processor<Face> processor;

        if (isUsingFrontCamera) {
            // For front facing mode, a single tracker instance is used with an associated focusing
            // processor.  This configuration allows the face detector to take some shortcuts to
            // speed up detection, in that it can quit after finding a single face and can assume
            // that the nextIrisPosition face position is usually relatively close to the last seen
            // face position.
            Tracker<Face> tracker = new GooglyFaceTracker(mGraphicOverlay);
// https://developers.google.com/android/reference/com/google/android/gms/vision/face/LargestFaceFocusingProcessor.Builder
            processor = new LargestFaceFocusingProcessor.Builder(detector, tracker).build();

        } else {
            // For rear facing mode, a factory is used to create per-face tracker instances.  A
            // tracker is created for each face and is maintained as long as the same face is
            // visible, enabling per-face state to be maintained over time.  This is used to store
            // the iris position and velocity for each face independently, simulating the motion of
            // the eyes of any number of faces over time.
            //
            // Both the front facing mode and the rear facing mode use the same tracker
            // implementation, avoiding the need for any additional code.  The only difference
            // between these cases is the choice of Processor: one that is specialized for tracking
            // a single face or one that can handle multiple faces.  Here, we use MultiProcessor,
            // which is a standard component of the mobile vision API for managing multiple items.
            processor = new MultiProcessor.Builder<>(mMultiProcessorFactory).build();
        }

    return processor;
} // createProcessor


/**
   * MultiProcessor.Factory
   * which is a standard component of the mobile vision API 
   * for managing multiple items.
  */
private MultiProcessor.Factory<Face> mMultiProcessorFactory = new MultiProcessor.Factory<Face>() {
                @Override
                public Tracker<Face> create(Face face) {
log_d("create");
                    return new GooglyFaceTracker(mGraphicOverlay);
                }
}; // MultiProcessor.Factory


/**
   * Creates the face detector and the camera.
  */
    private Camera2Source createCameraSource() {
        log_d("createCameraSource");
        Context context = getApplicationContext();
        FaceDetector faceDetector = createFaceDetector(context);

        // choose camera facing
        int facing = 0;
        if (isUsingFrontCamera) {
            // front camera
            facing = Camera2Source.CAMERA_FACING_FRONT;
        } else {
            // back camera
            facing = Camera2Source.CAMERA_FACING_BACK;
        }

            Camera2Source camera2Source = new Camera2Source.Builder(this, faceDetector)
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                    .setFacing(facing)
                    .setErrorCallback(cameraErrorCallback)
                    .build();

                return camera2Source;
    }



/**
  * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
 * (e.g., because onResume was called before the camera source was created), this will be called
  * again when the camera source is created.
 */
    private void startCameraSource() {
        log_d("startCameraSource");
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());

        if (code != ConnectionResult.SUCCESS) {
// https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        } // if (code

        Camera2Source camera2Source = createCameraSource();
        if (camera2Source != null) {
                mCamera2Source = camera2Source;
                mPreview.start(mCamera2Source, mGraphicOverlay);
        } //  if camera2Source

} // startCameraSource


/**
 * stopCameraSource
 */ 
    private void stopCameraSource() {
        log_d("stopCameraSource");
        mPreview.stop();
    } // stopCameraSource


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
}


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
private void showToast( String msg ) {
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
protected void log_d( String msg ) {
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
