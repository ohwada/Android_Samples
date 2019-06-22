/**
 * Camera2 Sample
 * take Picture with RAW mode using Camera2Source
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera214;

import android.Manifest;
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
import java.util.Date;


import jp.ohwada.android.camera214.util.CameraPerm;
import jp.ohwada.android.camera214.util.Camera2Source;
import jp.ohwada.android.camera214.util.ToastMaster;
import jp.ohwada.android.camera214.util.ImageUtil;
import jp.ohwada.android.camera214.util.FileUtil;

import jp.ohwada.android.camera214.ui.CameraSourcePreview;


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
 * Permssions
 */ 
    private static final String[] CAMERA_STORAGE_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

   private static final String DIR_NAME =  FileUtil.DIR_NAME;

    // Camera2Source
    private Camera2Source mCamera2Source = null;

    // view
    private CameraSourcePreview mPreview;


    // default camera face
    //private boolean usingFrontCamera = false;

/**
 * Camera permssion
 */ 
    private CameraPerm mCameraPerm;

/**
 * utility for JPEG
 */ 
      private ImageUtil mImageUtil;

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

    // view
    mPreview = (CameraSourcePreview) findViewById(R.id.preview);

     // utility
    mImageUtil = new ImageUtil(this);

    mCameraPerm = new CameraPerm(this);
    mCameraPerm.setRequestMessage(R.string.request_permission_camera_storage);
    mCameraPerm.setPermissions(CAMERA_STORAGE_PERMISSIONS);

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
 * takePicture
 */
private void takePicture() {
              if(mCamera2Source != null) {
                mCamera2Source.takePicture(camera2SourcePictureCallback);
    }
} // takePicture



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
} // createCameraSourceBackBack

/**
 * startCameraSource
 */ 
    private void startCameraSource() {
        log_d("startCameraSource");
        if(mCameraPerm.requestCameraPermissions()) {
                log_d("not permit");
                return;
        }
        FileUtil.mkDirInExternalStoragePublicDCIM();

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
        mCamera2Source = null;
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
        public void onJpegTaken(Image image, Date date) {
            procJpegTaken(image, date);
        }
        @Override
        public void onRawTaken(File file) {
            procRawTaken(file);
        }
    }; // PictureCallback 


/**
 * procJpegTaken
 * save Image and scanFile
 */ 
private void procJpegTaken(Image image, Date date) {
        File file = mImageUtil.getOutputFile(date);
        boolean ret = mImageUtil.saveImageAsJpeg(image, file);
        if(ret) {
            FileUtil.scanFile(this, file);
            log_d("jpeg size= " +file.length());
        }
       String msg = "saved: " + file.toString();
        log_d(msg);
        showToast_onUI(msg);
} // procJpegTaken


/**
 * procRawTaken
 * scanFile
 */ 
private void procRawTaken(File file) {
        FileUtil.scanFile(this, file);
        log_d("raw size= " +file.length());
       String msg = "saved: " + file.toString();
        log_d(msg);
        showToast_onUI(msg);
} // procRawTaken


/**
 * CameraErrorCallback
 */ 
 Camera2Source.ErrorCallback cameraErrorCallback = new Camera2Source.ErrorCallback() {
        @Override
        public void onError(String msg) {
            showErrorDialog_onUI(msg);
        }
    }; // CameraErrorCallback 


} // class PictureActivity 
