/**
 * TextureView Sample
 * Camera Preview using camera API
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.textureview1;

import java.io.IOException;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * CameraActivity
 * original : https://github.com/dalinaum/TextureViewDemo/tree/master/src/kr/gdg/android/textureview
 */
public class CameraActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "TextureView";
    	private final static String TAG_SUB = "CameraActivity";


    private OrientationEventListener mOrientationEventListener;

    private TextureView mTextureView;

       private  CameraPerm mCameraPerm;

    private Camera mCamera;

/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_texture);

        mTextureView = (TextureView) findViewById(R.id.texture_view);

    mCameraPerm  = new CameraPerm(this);

    } // onCreate


/**
 * onResume
 */
@Override
protected void onResume() {
        super.onResume();

        if (mOrientationEventListener == null) {
            mOrientationEventListener = new OrientationEventListener(this,
                    SensorManager.SENSOR_DELAY_NORMAL) {
                private int mOrientation;

                @Override
                public void onOrientationChanged(int orientation) {
                    int lastOrientation = mOrientation;

                    if (orientation >= 315 || orientation < 45) {
                        if (mOrientation != Surface.ROTATION_0) {
                            mOrientation = Surface.ROTATION_0;
                        }
                    } else if (orientation >= 45 && orientation < 135) {
                        if (mOrientation != Surface.ROTATION_90) {
                            mOrientation = Surface.ROTATION_90;
                        }
                    } else if (orientation >= 135 && orientation < 225) {
                        if (mOrientation != Surface.ROTATION_180) {
                            mOrientation = Surface.ROTATION_180;
                        }
                    } else if (mOrientation != Surface.ROTATION_270) {
                        mOrientation = Surface.ROTATION_270;
                    }

                    if (lastOrientation != mOrientation) {
                        log_d("rotation!!! lastOrientation:"
                                + lastOrientation + " mOrientation:"
                                + mOrientation + " orientaion:"
                                + orientation);
                    }
                }
            }; // OrientationEventListener
        }

        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }

        openCamera();
        SurfaceTextureListener surfaceTextureListener = new CameraSurfaceTextureListener(
                this);
            mTextureView
                .setSurfaceTextureListener(surfaceTextureListener);

} // onResume

/**
 * onPause
 */
@Override
protected void onPause() {
        super.onPause();
        if( mCamera != null) {
                mCamera.release();
                mCamera = null;
        }
        if( mOrientationEventListener != null) {
                mOrientationEventListener.disable();
                mOrientationEventListener = null;
        }
} // onPause



/**
  * onRequestPermissionsResult 
 */
@Override
public void onRequestPermissionsResult( int request, String[] permissions, int[] results ) {

    mCameraPerm.onRequestPermissionsResult( request,  permissions,  results);
    openCamera();

} // onRequestPermissionsResult


/**
 * getBackCamera
 */
    private Pair<CameraInfo, Integer> getBackCamera() {
        CameraInfo cameraInfo = new CameraInfo();
        final int numberOfCameras = Camera.getNumberOfCameras();

        for (int i = 0; i < numberOfCameras; ++i) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                return new Pair<Camera.CameraInfo, Integer>(cameraInfo,
                        Integer.valueOf(i));
            }
        } // for
        return null;
    } // getBackCamera


/**
 * openCamera
 */ 
private void openCamera() {

        if ( mCameraPerm.requestCameraPermissions() ) {
            return;
        }

        Pair<CameraInfo, Integer> backCamera = getBackCamera();
        if (backCamera == null ) {
            log_d(R.string.msg_not_support_carera_api);
           toast_long(R.string.msg_not_support_carera_api);
            return;
        }

    try {
        final int backCameraId = backCamera.second;
        // mBackCameraInfo = backCamera.first;
// Fail to connect to camera service
        mCamera = Camera.open(backCameraId);
    } catch (Exception ex) {
            if (D )ex.printStackTrace();
    }

        if (mCamera == null) {
                toast_long("cannot open camera");
        } 

} // openCamera


/**
 * toast_long
 */
private void toast_long( int res_id ) {
		ToastMaster.makeText( this, res_id, Toast.LENGTH_LONG ).show();
} // toast_long

/**
 * toast_long
 */
private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // toast_long


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
 * interface CameraHelper
 */
interface CameraHelper {
    Camera getCamera();
} // interface CameraHelper

/**
 * class CameraSurfaceTextureListener
 */
class CameraSurfaceTextureListener implements
        SurfaceTextureListener {
    // private Camera mCamera;
    private Activity mActivity;
    private CameraInfo mBackCameraInfo;

/**
 * constractor
 */
public CameraSurfaceTextureListener(Activity activity) {
        mActivity = activity;
} // CameraSurfaceTextureListener


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    log_d("onSurfaceTextureUpdated");
    } // onSurfaceTextureUpdated

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
            int width, int height) {
    log_d("onSurfaceTextureSizeChanged");
    } // onSurfaceTextureSizeChanged

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

    log_d("onSurfaceTextureDestroyed");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    } // onSurfaceTextureDestroyed

    @Override
    public void onSurfaceTextureAvailable(
            SurfaceTexture surface,
            int width, int height) {

    log_d("onSurfaceTextureAvailable");
     if (mCamera == null) {
        // TODO
        log_d("Camera = null");
        return;
    }
    try {
            cameraDisplayRotation();
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
    } catch (IOException ex) {
            if (D )ex.printStackTrace();
    }

} // onSurfaceTextureAvailable


/**
 * cameraDisplayRotation
 */
public void cameraDisplayRotation() {

        if (mCamera == null ) return;

        if (mBackCameraInfo == null ) return;

        final int rotation = mActivity.getWindowManager()
                .getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        final int displayOrientation = (mBackCameraInfo.orientation
                - degrees + 360) % 360;
        mCamera.setDisplayOrientation(displayOrientation);
} // cameraDisplayRotation


/**
 * isCameraOpen
 */
    public boolean isCameraOpen() {
        return mCamera != null;
    } // isCameraOpen


} // class CameraSurfaceTextureListener


} // class CameraActivity
