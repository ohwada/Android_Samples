/**
 * Camera2 Sample
 * zoom Camera
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera215;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.ohwada.android.camera215.util.Camera2Source;
import jp.ohwada.android.camera215.util.CameraPerm;
import jp.ohwada.android.camera215.util.ImageUtil;
import jp.ohwada.android.camera215.util.ToastMaster;
import jp.ohwada.android.camera215.ui.CameraSourcePreview;


/**
 * class MainActivity 
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";


/**
 * minimum value of Zoom
 * little smaller than the threshold
 * in consideration of calculation error
 */ 
    private static final  float MIN_ZOOM = Camera2Source.MIN_VALID_ZOOM;


 /**
 * Maximum value of Zoom
 * Nexus5: 4.0
 */ 
    private static final  float MAX_ZOOM = 4.0f;


/**
 * step value of Zoom for UpDown
 * for Volume Botton
 */ 
    private static final  float ZOOM_STEP = 0.5f;


/**
 * threshold value whether to zoom in or out
 * for ScaleGesture
 * zoom, when larger than this value
 * zoom before scaling, when smaller than this value
 */ 
    private static final  float SCALE_THRESHOLD = 1.0f;


/**
 * Camera2Source
 */
    private Camera2Source mCamera2Source = null;


/**
 * CameraSourcePreview
 */
    private CameraSourcePreview mPreview;


/**
 * CameraPerm
 */
    private CameraPerm mCameraPerm;


/**
 * utility for Image
 */
     private ImageUtil mImageUtil;


/**
 * ScaleGestureDetector for Zoom
 */
    private ScaleGestureDetector mScaleGestureDetector;


/**
 * Flag whether ToggleZoom is Max or not
 * for Zoom Botton
 * true : Max
 * false : Min
 */
    private boolean isToggleZoomMax = false;


/**
 * current value of Zoom
 * for Volume Botton
 */ 
    private float mCurrentZoom = 1.0f;


/**
 * zoom value before scaling
 * for ScaleGesture
 */ 
    private float mPrevZoom = 1.0f;


/**
 * Flag whether to take picture with zoom or not
 */
    private boolean isPictureZoom = false;


/**
 * onCreate
 */ 
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button btnPicture = (Button) findViewById(R.id.Button_picture);
            btnPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }
            }); // btnPicture

        Button btnZoom = (Button) findViewById(R.id.Button_zoom);
            btnZoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleZoom();
                }
            }); // btnZoom


    // view
    mPreview = (CameraSourcePreview) findViewById(R.id.preview);

     // utility
    mCameraPerm = new CameraPerm(this);

    mImageUtil = new ImageUtil(this);

    mScaleGestureDetector = new ScaleGestureDetector(this, mScaleGestureListener);

} // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        startCameraSource();
} // onResume


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        log_d("onPause");
        super.onPause();
        stopCameraSource();
    }


/**
 * onDestroy
 */ 
    @Override
    protected void onDestroy() {
        log_d("onDestroy");
        super.onDestroy();
        stopCameraSource();
    }


/**
 * onRequestPermissionsResult
 */ 
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        mCameraPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
        startCameraSource();
} // onRequestPermissionsResult


/**
 * onTouchEvent
 */ 
@Override
public boolean onTouchEvent(MotionEvent event) {
  mScaleGestureDetector.onTouchEvent(event);
  return true;
} // onTouchEvent


/** 
 *  dispatchKeyEvent
 *  interupt VolumeUp Key, VolumeDown Key
 */
@Override
public boolean dispatchKeyEvent(KeyEvent event) {
 
    boolean ret = procKeyEvent(event);
    if(ret) return true;
 
   return super.dispatchKeyEvent(event);
} // dispatchKeyEvent


/** 
 *  procKeyEvent
 *  interupt VolumeUp Key, VolumeDown Key
 */
private boolean procKeyEvent(KeyEvent event) {
 
    boolean ret = false;
    int key_action = event.getAction();
    int key_code = event.getKeyCode();

   if (key_action != KeyEvent.ACTION_DOWN) {
        return false;
    }

    switch(key_code) {
        case KeyEvent.KEYCODE_VOLUME_UP:
            procZoomUpDown(+ZOOM_STEP);
            ret = true;
            break;
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            procZoomUpDown(-ZOOM_STEP);
            ret = true;
            break;
        case KeyEvent.KEYCODE_BACK:
            // nop
            break;
        default:
            showToast("KeyCode: " + key_code);
            break;
    }
    return ret;

} // procKeyEvent


/**
 * procZoomUpDown
 * Zoom by the Volume Button
 */ 
private void procZoomUpDown(float step) {
    float zoom = mCurrentZoom + step;
log_d("procZoomUpDown: " + step + " : " + mCurrentZoom + " -> " + zoom);
    doZoom(zoom);
}






 /**
 * takePicture
 */
private void takePicture() {
              if(mCamera2Source != null) {
                mCamera2Source.takePicture(isPictureZoom, camera2SourcePictureCallback);
    }
} // takePicture




 /**
 * toggleZoom
 * toggle the zoom to min / max
 * with the View Button
 */
private void toggleZoom() {
    float zoom = 0;
    if(isToggleZoomMax) {
        isToggleZoomMax = false;
        zoom = MIN_ZOOM;
    } else {
        isToggleZoomMax = true;
         zoom = MAX_ZOOM;
    }
    doZoom(zoom);
} // toggleZoom




 /**
 * doZoom
 */
private void doZoom(float zoom) {

        isPictureZoom = false;

    // adjust zoom value
    float maxZoom = getMaxDigitalZoom();
    if( zoom >  maxZoom) {
        zoom = maxZoom;
    }
    if( zoom > MIN_ZOOM ) {
        isPictureZoom = true;
    } else {
        zoom = MIN_ZOOM;
    }

    mCurrentZoom = zoom;
    if(mCamera2Source != null) {
            mCamera2Source.doZoom(zoom, cameraZoomCallback);
    }
    String msg = "doZoom: " + zoom;
    log_d(msg);
    showToast(msg);
} // doZoom


/**
 * getMaxDigitalZoom
 */ 
private float getMaxDigitalZoom() {
            float maxZoom = MAX_ZOOM;
            if(mCamera2Source != null) {
                    maxZoom = mCamera2Source.getMaxDigitalZoom();
                    log_d("MaxZoom=" + maxZoom);
            }
            return maxZoom;
} // getMaxDigitalZoom


/**
 * createCameraSourceBack
 */ 
    private Camera2Source createCameraSourceBack() {
        log_d(" createCameraSourceBack");
        Camera2Source camera2Source = new 
Camera2Source.Builder(this) 
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                    .setFacing(Camera2Source.CAMERA_FACING_BACK)
                    .setErrorCallback(cameraErrorCallback)
                    .build();

        return camera2Source;
} // createCameraSourceBack

/**
 * startCameraSource
 */ 
    private void startCameraSource() {
        log_d("startCameraSource");
        if(mCameraPerm.requestCameraPermissions()) {
                log_d("not permit");
                return;
        }

        Camera2Source camera2Source = createCameraSourceBack();
        if(camera2Source != null) {
                mCamera2Source = camera2Source;
                mPreview.start(camera2Source);
        }
} // startCameraSource


/**
 * stopCameraSource
 */ 
    private void stopCameraSource() {
        log_d("stopCameraSource");
        mPreview.stop();
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
private void log_d( int res_id ) {
    log_d( getString(res_id) );
} // log_d

/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d



/**
 * PictureCallback
 */ 
 Camera2Source.PictureCallback camera2SourcePictureCallback = new Camera2Source.PictureCallback() {
        @Override
        public void onPictureTaken(Image image) {
            procPictureTaken(image);
        }
    }; // PictureCallback 


/**
 * procPictureTaken
 */ 
private void procPictureTaken(Image image) {
        File file = mImageUtil.createOutputFileInExternalFilesDir();
        mImageUtil.saveImageAsJpeg(image, file);
        final String msg = "saved: " + file.toString();
        log_d(msg);
        showToast_onUI(msg);

} // procPictureTaken




/**
 * CameraErrorCallback
 */ 
 Camera2Source.ErrorCallback cameraErrorCallback = new Camera2Source.ErrorCallback() {
        @Override
        public void onError(String msg) {
            showErrorDialog_onUI(msg);
        }
    }; // CameraErrorCallback 

/**
 * cameraZoomCallback
 */ 
private Camera2Source.ZoomCallback cameraZoomCallback =
new Camera2Source.ZoomCallback() {
        @Override
        public void onZoom(boolean success) {
                log_d("onZoom: " + success);
                procZoomCallback(success);
        }
}; // cameraAutoFocusCallback



/**
 * procZoomCallback
 */ 
private void procZoomCallback(boolean success) {
    String msg = "";
    if(success) {
            msg = "Zoom Successful";
    } else  {
            msg ="Zoom Faild";
    }
    showToast_onUI(msg);
} // procZoomCallback


/**
 * ScaleGestureListener
 */ 
    private  ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //log_d("onScale");
            return false;
        }
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            log_d("onScaleBegin");
            return true;
        }
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            log_d("onScaleEnd");
            float scale = detector.getScaleFactor();
            scaleZoom(scale);
        }

}; // ScaleGestureListener



/**
 * Zoom with ScaleGesture
 */ 
private void scaleZoom(float scale) {
    float zoom = 0;
    if(scale > SCALE_THRESHOLD) {
        // set the scale value to the Zoom value
        // when larger than threshold
        mPrevZoom = mCurrentZoom;
        zoom = scale;
    } else {
        // return to the zoom value before scaling
        // when smaller than threshold
        zoom = mPrevZoom;
    }
    log_d("scaleZoom: " + scale + " , " + zoom);
    doZoom(zoom);
} // scaleZoom


} // class MainActivity 
