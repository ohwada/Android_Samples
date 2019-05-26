/**
 * Vision Sample
 * Face Detection using Camera2 API and Vision API
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision2;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


import jp.ohwada.android.vision2.util.Camera2Source;
import jp.ohwada.android.vision2.util.CameraPerm;
import jp.ohwada.android.vision2.util.ToastMaster;


import jp.ohwada.android.vision2.ui.CameraSourcePreview;
import jp.ohwada.android.vision2.ui.GraphicOverlay;


 /**
  *  class MainActivity
 * original : https://github.com/EzequielAdrianM/Camera2Vision
  */
public class MainActivity extends Activity {

     // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "MainActivity";


/**
 *  request code for Google  Play Services API
  */
    private final static int GOOGLE_API_REQUEST_CODE = 2404;


/**
 *  Flag whether to reduce to quarter size the NV21 frame
  */
    private final static boolean IS_QUARTER_SIZE = true;


/**
 *  Basement Context
  */
    private Context mContext;


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
    private boolean isUsingFrontCamera = false;

/**
 *  Flag whether to detect the Face
  */
    private boolean isDetectRunning = false;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
         mContext = getApplicationContext();

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
 * onResume
 */ 
@Override
protected void onResume() {
        super.onResume();
            log_d("onResume");
        if( !checkGooglePlayAvailability() ) {
            log_d("GooglePlay NOT Availability");
            return;
        }
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
 * onRequestPermissionsResult
 */ 
@Override
public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        log_d("onRequestPermissionsResult");
        mCameraPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
        startCameraSource();

} // onRequestPermissionsResult


/**
 * flipCameraFace
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
        pauseFaceDetect();
        showToast("pause detect");
    } else {
        isDetectRunning = true;
        resumeFaceDetect();
        showToast("resume detect");
    }
} // detectFace


 /**
 * resumeFaceDetect
 */
private void resumeFaceDetect() {
    setDetectRuning();
} // resumeFaceDetect


 /**
 * pauseFaceDetect
 */
private void pauseFaceDetect() {
    setDetectRuning();
    if(mGraphicOverlay != null) {
        mGraphicOverlay.clear();
    }
} // pauseFaceDetect


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
 * checkGooglePlayAvailability
 */
    private boolean checkGooglePlayAvailability() {
        log_d("checkGooglePlayAvailability");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode == ConnectionResult.SUCCESS) {
            return true;
        } else {
            log_d("NOT GooglePlayAvailability:  resultCode=" + resultCode);
            if(googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, GOOGLE_API_REQUEST_CODE).show();
            }
        }
        return false;
} // checkGooglePlayAvailability


/**
 * createCameraSourceFront
 */ 
    private Camera2Source createCameraSourceFront() {
        log_d("createCameraSourceFront");
            FaceDetector faceDetector = createFaceDetector();

            Camera2Source camera2Source = new Camera2Source.Builder(this, faceDetector, IS_QUARTER_SIZE)
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                    .setFacing(Camera2Source.CAMERA_FACING_FRONT)
                    .setErrorCallback(cameraErrorCallback)
                    .build();

                return camera2Source;

} // createCameraSourceFront

/**
 * createCameraSourceBack
 */ 
    private Camera2Source createCameraSourceBack() {
        log_d("createCameraSourceBack");
            FaceDetector  faceDetector = createFaceDetector();

            Camera2Source camera2Source = new Camera2Source.Builder(this, faceDetector, IS_QUARTER_SIZE)
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                    .setFacing(Camera2Source.CAMERA_FACING_BACK)
                    .setErrorCallback(cameraErrorCallback)
                    .build();

                return camera2Source;

} // createCameraSourceBack


/**
 * createFaceDetector
 */ 
    private FaceDetector createFaceDetector() {
        log_d("createFaceDetector");
        FaceDetector faceDetector = new FaceDetector.Builder(this)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(true)
                .setTrackingEnabled(true)
                .build();

        if(faceDetector.isOperational()) {
            faceDetector.setProcessor( createFaceProcessor() );
        } else {
            showToast("FACE DETECTION NOT AVAILABLE");
            log_d("FACE DETECTION NOT AVAILABLE");
        }
        return faceDetector;
} // createFaceDetector


/**
 * createFaceProcessor
 */ 
private MultiProcessor<Face> createFaceProcessor() {
            log_d("createFaceProcessor");

            FaceGraphic faceGraphic = new FaceGraphic(mGraphicOverlay, this);

            FaceTrackerFactory factory= new FaceTrackerFactory(mGraphicOverlay, faceGraphic);
            MultiProcessor.Builder<Face>builder = new MultiProcessor.Builder<Face>(factory);
            MultiProcessor<Face> processor = builder.build();
            return processor;
} // createFaceProcessor


/**
 * startCameraSource
 */ 
    private void startCameraSource() {
        log_d("startCameraSource");
        		Camera2Source camera2Source = null;
        		if(isUsingFrontCamera) {
        			camera2Source = createCameraSourceFront();
        		} else {
        			camera2Source = createCameraSourceBack();
        		}
                if(camera2Source != null) {
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
}

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
