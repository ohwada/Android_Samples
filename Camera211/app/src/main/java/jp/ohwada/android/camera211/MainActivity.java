/**
 * Camera2 Sample
 *  AutoFocus
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera211;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import jp.ohwada.android.camera211.util.Camera2Source;
import jp.ohwada.android.camera211.util.CameraPerm;
import jp.ohwada.android.camera211.util.DisplayUtil;
import jp.ohwada.android.camera211.util.ToastMaster;
import jp.ohwada.android.camera211.util.ImageUtil;

import jp.ohwada.android.camera211.ui.CameraSourcePreview;


import static android.content.res.Resources.getSystem;


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


    // dimen/image_view_autofocus_width
    private static final int IMAGE_VIEW_AUTOFOCUS_WIDTH = 60;
    // dimen/image_view_autofocus_height
    private static final int IMAGE_VIEW_AUTOFOCUS_HEIGHT = 60;

    // Camera2Source
    private Camera2Source mCamera2Source = null;

    // view
    private CameraSourcePreview mPreview;

    private LinearLayout mLinearLayoutControl;

    private ImageView mImageViewAutoFocus;


    // default camera face
    private boolean usingFrontCamera = false;

    // utility
    private CameraPerm mCameraPerm;

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
        mPreview.setOnTouchListener(CameraPreviewTouchListener);

        mLinearLayoutControl = (LinearLayout) findViewById(R.id.control);
        mLinearLayoutControl.setOnTouchListener(ControlTouchListener);

        mImageViewAutoFocus = (ImageView) findViewById(R.id.autoFocus);

     // utility
    mCameraPerm = new CameraPerm(this);
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
} // onResume


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        super.onPause();
        log_d("onPause");
        stopCameraSource();
    }


/**
 * onDestroy
 */ 
    @Override
    protected void onDestroy() {
        super.onDestroy();
        log_d("onDestroy");
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
                mCamera2Source.takePicture(camera2SourcePictureCallback);
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


/**
 * ControlTouchListener
 */ 
 private final LinearLayout.OnTouchListener ControlTouchListener = new LinearLayout.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            log_d("Control onTouch");

            // return true, so that dont move the touch event for  CameraPreviewTouchListener
            return true;
        }
}; // ConrolTouchListener


/**
 * CameraPreviewTouchListener
 */ 
 private final CameraSourcePreview.OnTouchListener CameraPreviewTouchListener = new CameraSourcePreview.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            log_d("Preview onTouch");
            view.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                procTouchActionDown(view, event);
            }
            return false;
        }
}; // CameraPreviewTouchListener



/**
 * procTouchActionDown
 */
private void procTouchActionDown(View view, MotionEvent event) {
            // touch position
            float touchX  = event.getX();
            float touchY  = event.getY();
   
            // origin(left, top) of ImageView
            int autoFocusX = getAutoFocusLeftX(touchX);
            int autoFocusY = getAutoFocusTopY(touchY);
            // move to touched position
            mImageViewAutoFocus.setTranslationX(autoFocusX);
            mImageViewAutoFocus.setTranslationY(autoFocusY);
            mImageViewAutoFocus.setVisibility(View.VISIBLE);
            mImageViewAutoFocus.bringToFront();
            if(mCamera2Source != null) {
                        mCamera2Source.autoFocus(cameraAutoFocusCallback, touchX, touchY, view.getWidth(), view.getHeight());
            } else {
                        mImageViewAutoFocus.setVisibility(View.GONE);
            }
} // procTouchActionDown


/**
 * getAutoFocusLeftX
 * shift the x-coordinate by the size of the ImageView
 */ 
private int getAutoFocusLeftX(float touchX) {
    int x = (int) (touchX - DisplayUtil.dpToPx(IMAGE_VIEW_AUTOFOCUS_WIDTH)/2);
    return x;
}


/**
 * getAutoFocusTopY
 * shift the y-coordinate by the size of the ImageView
 */ 
private int getAutoFocusTopY(float touchY) {
    int y = (int) (touchY - DisplayUtil.dpToPx(IMAGE_VIEW_AUTOFOCUS_HEIGHT)/2);
    return y;
}


/**
 * cameraAutoFocusCallback
 */ 
private Camera2Source.AutoFocusCallback cameraAutoFocusCallback =
new Camera2Source.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success) {
                log_d("onAutoFocus: " + success);
                procAutoFocus(success);
        }
}; // cameraAutoFocusCallback


/**
 * procAutoFocus
 */ 
private void procAutoFocus(boolean success) {
    hideAutoFocusImage_onUI();
    if(success) {
            showToast_onUI("AutoFocus Successful");
    } else {
            showToast_onUI("AutoFocus Faild");
    }
} // procAutoFocus


/**
 * hideAutoFocusImage_onUI
 */ 
private void hideAutoFocusImage_onUI() {
    runOnUiThread(new Runnable() {
                @Override public void run() {
                        mImageViewAutoFocus.setVisibility(View.GONE);
                }
        });
} // hideAutoFocusImage_onUI


} // class MainActivity 
