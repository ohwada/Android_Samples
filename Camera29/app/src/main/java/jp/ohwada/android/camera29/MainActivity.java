/**
 * Camera2 Sample
 * take Picture with Shutter Sound
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera29;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;


import jp.ohwada.android.camera29.util.CameraPerm;
import jp.ohwada.android.camera29.util.Camera2Source;
import jp.ohwada.android.camera29.util.ToastMaster;
import jp.ohwada.android.camera29.util.SoundUtil;
import jp.ohwada.android.camera29.util.ImageUtil;

import jp.ohwada.android.camera29.ui.CameraSourcePreview;


/**
 * class MainActivity 
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";


    // output file
    private static final String FILE_PREFIX = "camera_";
    private static final String FILE_EXT =  ImageUtil.FILE_EXT_JPG;


    // Camera2Source
    private Camera2Source mCamera2Source = null;

    // view
    private CameraSourcePreview mPreview;


    // default camera face
    private boolean usingFrontCamera = false;

    // utility
    private CameraPerm mCameraPerm;

     private SoundUtil mSoundUtil;

     private ImageUtil mImageUtil;


/**
 * onCreate
 */ 
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button btnFLip = (Button) findViewById(R.id.Button_flip);
    btnFLip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flipCameraFace();
                }
            }); // btnFLip

        Button btnPicture = (Button) findViewById(R.id.Button_picture);
            btnPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }
            }); // btnPicture

    // view
    mPreview = (CameraSourcePreview) findViewById(R.id.preview);

     // utility
    mCameraPerm = new CameraPerm(this);

    mSoundUtil = new SoundUtil(this);
    mImageUtil = new ImageUtil(this);

} // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        startCameraSource();
        if( mSoundUtil != null ) {
            // load shutter sound
            mSoundUtil.load(R.raw.camera_shutter);
        }
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
 * flipCameraFace
 */ 
private void flipCameraFace() {
        if(usingFrontCamera) {
                stopCameraSource();
                usingFrontCamera = false;
                startCameraSource();
                showToast("flip to Back");
        } else {
                stopCameraSource();
                usingFrontCamera = true;
                startCameraSource();
                showToast("flip to Front");
           }
} // witchFace


 /**
 * takePicture
 */
private void takePicture() {
              if(mCamera2Source != null) {
                mCamera2Source.takePicture(camera2SourcePictureCallback, cameraShutterCallback);
    }
} // takePicture


/**
 * createCameraSourceFront
 */ 
    private Camera2Source createCameraSourceFront() {
        log_d("createCameraSourceFront");
            Camera2Source camera2Source = new 
Camera2Source.Builder(this) 
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
        Camera2Source camera2Source = null;
        		if(usingFrontCamera) {
        			        camera2Source = createCameraSourceFront();
        		} else {
        			       camera2Source = createCameraSourceBack();
        		}
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
 * cameraShutterCallback
 */ 
    Camera2Source.ShutterCallback cameraShutterCallback = new Camera2Source.ShutterCallback() {
        @Override 
        public void onShutter() {
                log_d("onShutter");
                procShutter();
        }

}; // cameraShutterCallback


/**
 * procShutter
 * play shutter sound
 */ 
private void procShutter() {
             log_d("procShutter");
            if( mSoundUtil != null ) {
                // play shutter sound
                mSoundUtil.play();
            }
            showToast_onUI("Shutter Callback!");
}


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
        File file = mImageUtil.getOutputFileInExternalFilesDir(FILE_PREFIX, FILE_EXT);
        mImageUtil.saveImageAsJpeg(image, file);
       String msg = "saved: " + file.toString();
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


} // class MainActivity 
