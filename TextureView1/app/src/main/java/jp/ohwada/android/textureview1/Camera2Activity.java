/**
 * TextureView Sample
 * Camera Preview using camera 2 API
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.textureview1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;

/**
 * Camera2Activity
 * reference : https://github.com/googlesamples/android-Camera2Raw/blob/master/Application/src/main/java/com/example/android/camera2raw/Camera2RawFragment.java
 */
public class Camera2Activity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "TextureView";
    	private final static String TAG_SUB = "Camera2Activity";


    // SurfaceTexture#setDefaultBufferSize
    private static final int BUFFER__WIDTH = 640;

    private static final int BUFFER__HEIGHT = 480;

    // BackgroundThread
    private static final String BACKGROUND_THREAD_NAME = "CameraBackground";

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private TextureView mTextureView;


    /**
     * A reference to the open {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;


    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An additional thread for running tasks that shouldn't block the UI.  This is used for all
     * callbacks from the {@link CameraDevice} and {@link CameraCaptureSession}s.
     */
    private HandlerThread mBackgroundThread;


    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;


    private CameraPerm  mCameraPerm;


    /**
     * {@link CameraDevice.StateCallback} is called when the currently active {@link CameraDevice}
     * changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            log_d("onOpened");
            // This method is called when the camera is opened.  We start camera preview here if
            // the TextureView displaying this has been set up.
                mCameraDevice = cameraDevice;
                createCameraPreviewSession();

        } // onOpened


        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            log_d(" onDisconnected");
                cameraDevice.close();
                mCameraDevice = null;
        } // onDisconnected

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            log_d( "Received camera device error: " + error);
                cameraDevice.close();
                mCameraDevice = null;
                finish();
        } // onError

}; // CameraDevice.StateCallback


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
public void onResume() {
        super.onResume();
    log_d("onResume");
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
} // onResume



/**
 * onPause
 */
@Override
public void onPause() {
        super.onPause();

        closeCamera();
        stopBackgroundThread();

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
  * Starts a background thread and its {@link Handler}.
  */
private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread( BACKGROUND_THREAD_NAME );
        mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
} // startBackgroundThread

 /**
  * Stops the background thread and its {@link Handler}.
 */
private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
} // stopBackgroundThread


/**
 * openCamera
 */ 
private void openCamera() {

        if ( mCameraPerm.requestCameraPermissions() ) {
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(this.CAMERA_SERVICE);
        if (manager == null) {
            log_d(R.string.msg_not_support_carera_api2);
           toast_long(R.string.msg_not_support_carera_api2);
        }

String backCameraId = getBackCameraId(manager);

        if (backCameraId == null ) {
            log_d(R.string.msg_not_found_back_camera);
           toast_long(R.string.msg_not_found_back_camera);
            return;
        }

    try {
            manager.openCamera(backCameraId, mStateCallback, mBackgroundHandler);
    } catch (Exception ex) {
            if (D )ex.printStackTrace();
    }

} // openCamera


/**
 * getBackCameraId
 *search camera id has LENS_FACING_BACK
 */
private String getBackCameraId(CameraManager manager) {

    String[] ids = {};
    try {
    ids = manager.getCameraIdList();
    } catch (CameraAccessException e) {
        e.printStackTrace();
    }
    if( ids.length == 0 ) {
        return null;
    }

    String backCameraId = null;

        for (String id : ids) {

    try {
            CameraCharacteristics c = manager.getCameraCharacteristics(id);
            int face = c.get(CameraCharacteristics.LENS_FACING);
            if ( face == CameraCharacteristics.LENS_FACING_BACK) {
                backCameraId = id;
                log_d("back: " + id);
            } // if
    } catch (CameraAccessException e) {
        e.printStackTrace();
}

        } // for

return backCameraId;

} // getBackCamera


/**
   * Closes the current {@link CameraDevice}.
  */
private void closeCamera() {
        try {
                // Reset state and clean up resources used by the camera.
                // Note: After calling this, the ImageReaders will be closed after any background
                // tasks saving Images from these readers have been completed.
                if (null != mCaptureSession) {
                    mCaptureSession.close();
                    mCaptureSession = null;
                }
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
} // closeCamera



/**
 * Creates a new {@link CameraCaptureSession} for camera preview.
  * <p/>
  * Call this only with {@link #mCameraStateLock} held.
  */
private void createCameraPreviewSession() {
    log_d("createCameraPreviewSession");

    if (mCameraDevice == null) {
        return;
    }

        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
             texture.setDefaultBufferSize(BUFFER__WIDTH, BUFFER__HEIGHT);


            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
	// createCaptureRequest(int templateType, Set<String> physicalCameraIdSet)
//createCaptureRequest(int templateType)
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW); 
       mPreviewRequestBuilder.addTarget(surface);
            // Here, we create a CameraCaptureSession for camera preview.
// CameraDevice.createCaptureSession(List outputs, CameraCaptureSession.StateCallback callback, Handler handler)
List outputs = Arrays.asList(surface);
CameraCaptureSession.StateCallback callback = createCameraCaptureSessionStateCallback();
    mCameraDevice.createCaptureSession(outputs, callback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO : SecurityException: Lacking privileges to access camera service
            e.printStackTrace();
        }

} // createCameraPreviewSession


/**
 * createCameraCaptureSessionStateCallback
  */
private CameraCaptureSession.StateCallback createCameraCaptureSessionStateCallback() {

CameraCaptureSession.StateCallback callback = new CameraCaptureSession.StateCallback() {
    @Override
    public void onConfigured(CameraCaptureSession cameraCaptureSession) {
        log_d("onConfigured");
            // The camera is already closed
            if (null == mCameraDevice) {
                return;
            }
            try {
                cameraCaptureSession.setRepeatingRequest(
                        mPreviewRequestBuilder.build(),
                        mPreCaptureCallback,
                        mBackgroundHandler);
            } catch (CameraAccessException | IllegalStateException e) {
                e.printStackTrace();
                return;
            } // try

            // When the session is ready, we start displaying the preview.
            mCaptureSession = cameraCaptureSession;
    } // onConfigured

    @Override
    public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
        toast_long("Failed to configure camera.");
    } // onConfigureFailed

}; // CameraCaptureSession.StateCallback

return callback;
} // createCameraCaptureSessionStateCallback



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
     * A {@link CameraCaptureSession.CaptureCallback} that handles events for the preview and
     * pre-capture sequence.
     */
    private CameraCaptureSession.CaptureCallback mPreCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                        CaptureResult partialResult) {
    log_d("onCaptureProgressed");
            // process(partialResult);
        }
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
    log_d("onCaptureCompleted");
            // process(result);
        }

}; // CameraCaptureSession.CaptureCallback


    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events of a
     * {@link TextureView}.
     */
private SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener() {

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    log_d("onSurfaceTextureUpdated");
    } 

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
            int width, int height) {
    log_d("onSurfaceTextureSizeChanged");
    } 

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        log_d("onSurfaceTextureDestroyed");
        return true;
    } 

    @Override
    public void onSurfaceTextureAvailable(
            SurfaceTexture surface,
            int width, int height) {

    log_d("onSurfaceTextureAvailable");
    openCamera();

} // onSurfaceTextureAvailable


}; // SurfaceTextureListener


} // class Camera2Activity
