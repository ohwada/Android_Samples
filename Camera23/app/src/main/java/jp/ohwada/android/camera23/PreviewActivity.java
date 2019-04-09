/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */


package jp.ohwada.android.camera23;

import android.hardware.camera2.CaptureFailure;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

 /**
 *  class PreviewActivity
 * original : https://github.com/googlesamples/android-Camera2Basic/tree/master/Application/src/main/java/com/example/android/camera2basic
  */
public class PreviewActivity extends Activity {

        // debug
	protected final static boolean D = true;
    	protected final static String TAG = "Camera2";
    	protected final static String TAG_PREV = "Preview";

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    protected static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    /**
     * Camera state: Device is closed.
     */
    protected static final int STATE_CLOSED = 0;

    /**
     * Camera state: Device is opened, but is not capturing.
     */
    protected static final int STATE_OPENED = 1;
    /**
     * Camera state: Showing camera preview.
     */
    protected static final int STATE_PREVIEW = 2;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    protected static final int STATE_WAITING_LOCK = 3;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    protected static final int STATE_WAITING_PRECAPTURE = 4;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    protected static final int STATE_WAITING_NON_PRECAPTURE = 5;

    /**
     * Camera state: Picture was taken.
     */
    protected static final int STATE_PICTURE_TAKEN = 6;


    /**
     * Camera state: Waiting for 3A convergence before capturing a photo.
     */
    protected static final int STATE_WAITING_FOR_3A_CONVERGENCE = 7;


    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    protected static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    protected static final int MAX_PREVIEW_HEIGHT = 1080;

    protected static final String BACKGROUND_HANDLER_NAME = "CameraBackground";

    protected static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss_SSS";

    protected static final long LOCK_TIMEOUT = 2500;

    /**
     * ID of the current {@link CameraDevice}.
     */
    protected String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    protected AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    protected CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    protected CameraDevice mCameraDevice;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    protected Size mPreviewSize;

    /**
     * The {@link CameraCharacteristics} for the currently configured camera device.
     */
    protected CameraCharacteristics mCharacteristics;

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    protected HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    protected Handler mBackgroundHandler;

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    protected CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    protected CaptureRequest mPreviewRequest;


    /**
     * A lock protecting camera state.
     */
    protected final Object mCameraStateLock = new Object();


    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    protected int mState = STATE_PREVIEW;


    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    protected Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    protected boolean mFlashSupported;

    /**
     * Orientation of the camera sensor
     */
    protected int mSensorOrientation;

/**
  * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
 */
protected CameraCaptureSession.CaptureCallback mCaptureCallback = null;


    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    protected  Size chooseOptimalSize(Size[] choices, int textureViewWidth,
            int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                    option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
        }
} 

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            prev_log("Couldn't find any suitable preview size");
            return choices[0];
        }
    } // chooseOptimalSize


    protected Activity  mActivity;

    protected CameraPerm  mCameraPerm;


/**
 * onCreate
 */
    @Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    prev_log("onCreate");
        setContentView(R.layout.activity_main);
        mTextureView = (AutoFitTextureView) findViewById(R.id.texture);

        mActivity = this;
        mCameraPerm  = new CameraPerm(this);
    createExtend();

} // onCreate


/**
 * createExtend
 * override for Extend
 */
protected void createExtend() {
} // createExtend


/**
 * onResume
 */
@Override
public void onResume() {
        super.onResume();
    prev_log("onResume");
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    resumeExtend();
} // onResume

/**
 * resumeExtend
 * override for Extend
 */
protected void resumeExtend() {
} // resumeExtend

/**
 * onPause
 */
@Override
public void onPause() {
        super.onPause();
    prev_log("onPause");
        closeCamera();
        stopBackgroundThread();
    pauseExtend();
} // onPause

/**
 * pauseExtend
 * override for Extend
 */
protected void pauseExtend() {
} // pauseExtend

/**
  * onRequestPermissionsResult 
 */
@Override
public void onRequestPermissionsResult( int request, String[] permissions, int[] results ) {

    mCameraPerm.onRequestPermissionsResult( request,  permissions,  results);
    openCamera(mTextureView.getWidth(), mTextureView.getHeight());

} // onRequestPermissionsResult



/**
  * Retrieves the JPEG orientation from the specified screen rotation.
  *
  * @param rotation The screen rotation.
  * @return The JPEG orientation (one of 0, 90, 270, and 360)
 */
protected int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
} // getOrientation


/**
  * Opens the camera specified by {@link Camera2BasicFragment#mCameraId}.
  */
protected void openCamera(int width, int height) {
    prev_log("openCamera");
        if ( mCameraPerm.requestCameraPermissions() ) {
            return;
        }

        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    if(manager == null) {
            prev_log(R.string.msg_not_support);
           showErrorDialog(R.string.msg_not_support);
        return;
    }

    if(mCameraId == null) {
            prev_log(R.string.msg_not_found);
           showErrorDialog(R.string.msg_not_found);
        return;
    }

        try {
            if (!mCameraOpenCloseLock.tryAcquire(LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                prev_log("Time out waiting to lock camera opening.");
                // TODO
                // throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            prev_log("Interrupted while trying to lock camera opening.");
            e.printStackTrace();
        }
    openCameraExtend();
} // openCamera


/**
 * openCameraExtend
 * override for Extend
 */
protected void openCameraExtend() {
} // openCameraExtend


 /**
 * Closes the current {@link CameraDevice}.
  */
protected void closeCamera() {

    prev_log("closeCamera");
    try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            prev_log("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
        closeCameraExtend();

} // closeCamera

/**
 * closeCameraExtend
 * override for Extend
 */
protected void closeCameraExtend() {
} // closeCameraExtend


/**
  * Starts a background thread and its {@link Handler}.
 */
protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread(BACKGROUND_HANDLER_NAME);
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
} //  startBackgroundThread

 /**
  * Stops the background thread and its {@link Handler}.
 */
protected void stopBackgroundThread() {
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
 * Creates a new {@link CameraCaptureSession} for camera preview.
 * caller : CameraDevice.StateCallback#onOpened
 */
protected void createCameraPreviewSession() {
    prev_log("createCameraPreviewSession");

        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
    if(texture == null) {
        prev_log("cannot get texture");
        return;
    }

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            // TODO : SecurityException: Lacking privileges to access camera service
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
    List outputs = Arrays.asList(surface);       
    CameraCaptureSession.StateCallback callback = createCameraCaptureSessionStateCallback();
        mCameraDevice.createCaptureSession(outputs,callback, mBackgroundHandler);
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
protected CameraCaptureSession.StateCallback createCameraCaptureSessionStateCallback() {
CameraCaptureSession.StateCallback callback = new CameraCaptureSession.StateCallback() {
    @Override
    public void onConfigured(CameraCaptureSession cameraCaptureSession) {
        prev_log("onConfigured");
        procCaptureConfigured( cameraCaptureSession);
}

@Override
public void onConfigureFailed(
            CameraCaptureSession cameraCaptureSession) {
        prev_log("onConfigureFailed");
        procCaptureConfigureFailed(cameraCaptureSession);
    } // onConfigureFailed

}; // CameraCaptureSession.StateCallback

    return callback;

} // createCameraCaptureSessionStateCallback



/**
  * procCaptureConfigured
  */
protected void procCaptureConfigured(CameraCaptureSession cameraCaptureSession) {
    prev_log("procCaptureConfigured");
    // The camera is already closed
     if (null == mCameraDevice) {
         return;
    }
        mCaptureSession = cameraCaptureSession;
        synchronized (mCameraStateLock) {
            mState = STATE_PREVIEW;
        }
        // When the session is ready, we start displaying the preview.
        try {
            // Auto focus should be continuous for camera preview.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            procRepeatingRequest();
        } catch (Exception e) {
            e.printStackTrace();
        } // try
} // procConfigured


/**
  * procRepeatingRequest
  */
protected void procCaptureConfigureFailed(
            CameraCaptureSession cameraCaptureSession) {
        prev_log("procCaptureConfigureFailed");
        showToast_onUI("onConfigureFailed");
    } // procCaptureConfigureFailed


/**
  * procRepeatingRequest
  */
protected void procRepeatingRequest() {
            // Finally, we start displaying the camera preview.
        try {
            mPreviewRequest = mPreviewRequestBuilder.build();
            mCaptureSession.setRepeatingRequest(mPreviewRequest,
                    mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } // try
    } //  procRepeatingRequest

/**
  * Sets up member variables related to camera.
 *
  * @param width  The width of available size for camera preview
  * @param height The height of available size for camera preview
  */
@SuppressWarnings("SuspiciousNameCombination")
    protected void setUpCameraOutputs(int width, int height) {
prev_log("setUpCameraOutputs");
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    if(manager == null) {
        showErrorDialog(R.string.msg_not_support);
        return;
    }

        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        prev_log("Display rotation is invalid: " + displayRotation);
                }
                Point displaySize = new Point();
                getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;
                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }
                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }
                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);
                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;
                mCameraId = cameraId;
                mCharacteristics = characteristics;
                setUpExtendOutputs();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            showErrorDialog(R.string.msg_not_support);
        }

} // setUpCameraOutputs


/**
  * setUpExtendOutputs
  * override for extend
 */
protected void setUpExtendOutputs() {
} // setUpExtendOutputs


/**
 * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
 * This method should be called after the camera preview size is determined in
 * setUpCameraOutputs and also the size of `mTextureView` is fixed.
  *
  * @param viewWidth  The width of `mTextureView`
  * @param viewHeight The height of `mTextureView`
  */
protected void configureTransform(int viewWidth, int viewHeight) {
prev_log("configureTransform");
        if (null == mTextureView ) {
        prev_log("TextureView null");
            return;
        }
        if ( null == mPreviewSize ) {
        prev_log("PreviewSize null");
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
} // configureTransform



/**
 * getAppOutputFile
 */
protected File getAppOutputFile(String prefix, String ext) {
            String filename = getOutputFileName(prefix, ext);
        File file = new File(getExternalFilesDir(null), filename);
    return file;
} // gerAppOutputFile


/**
 *getOutputFileName
 */
protected String getOutputFileName(String prefix, String ext) {
   SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            String currentDateTime =  sdf.format(new Date());
            String filename = prefix + currentDateTime + ext;
    return filename;
} // getOutputFileName


/**
  * Shows a {@link Toast} on the UI thread.
 *
  * @param text The message to show
 */
protected void showToast_onUI(final String text) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast_long(text);
                }
    });

} // showToast_onUI


/**
 * toast_long
 */
protected void toast_long( int res_id ) {
		ToastMaster.makeText( this, res_id, Toast.LENGTH_LONG ).show();
} // toast_long

/**
 * toast_long
 */
protected void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // toast_long


/**
  * Shows an error message dialog.
 */
protected void showErrorDialog(int res_id) {
    showErrorDialog( getString(res_id) );
} // showErrorDialog


/**
  * Shows an error message dialog.
 */
protected void showErrorDialog(String msg) {
             new AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setPositiveButton(R.string.button_ok, null)
                    .show();
} // showErrorDialog


/**
 * write into logcat
 */ 
protected void prev_log( int res_id ) {
   log_base(  TAG_PREV, res_id );
} // prev_log

/**
 * write into logcat
 */ 
protected void prev_log( String msg ) {
   log_base(  TAG_PREV, msg );
} // prev_log

/**
 * write into logcat
 */ 
protected void log_base(  String tag_sub, int res_id ) {
    log_base( tag_sub, getString(res_id) );
} // log_base

/**
 * write into logcat
 */ 
protected void log_base( String tag_sub, String msg ) {
	    if (D) Log.d( TAG, tag_sub + " " + msg );
} // log_base


/**
 * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
  */
    protected final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

    @Override
    public void onOpened( CameraDevice cameraDevice) {
            prev_log("onOpened");
            procStateOpened(cameraDevice);
}


@Override
public void onDisconnected( CameraDevice cameraDevice) {
    prev_log("onDisconnected");
    procStateDisconnected( cameraDevice);
}

@Override
public void onError(  CameraDevice cameraDevice,  int error) {
        prev_log("onError: " + error);
        procStateError( cameraDevice, error );
} 

}; // CameraDevice.StateCallback


 /**
  * procStateOpened
 */
protected void procStateOpened( CameraDevice cameraDevice) {
        prev_log("procStateOpened");

        // This method is called when the camera is opened.  We start camera preview here.
        synchronized (mCameraStateLock) {
                mState = STATE_OPENED;
                mCameraOpenCloseLock.release();
                mCameraDevice = cameraDevice;
                createCameraPreviewSession();
        }
} // procStateOpened

 /**
  * rocStateDisconnected
 */
protected void procStateDisconnected( CameraDevice cameraDevice) {
    prev_log("procStateDisconnected");
    synchronized (mCameraStateLock) {
            mState = STATE_CLOSED;
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
    }
} //  procStateDisconnected


 /**
  * procStateError
 */
protected void procStateError( CameraDevice cameraDevice, int error) {

    synchronized (mCameraStateLock) {
            mState = STATE_CLOSED;
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
    }
    String msg = "onStateError " + error;
    showToast_onUI(msg);
    prev_log(msg);

} // procStateError

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
protected final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
    prev_log("onSurfaceTextureAvailable");
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
    prev_log("onSurfaceTextureSizeChanged");
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
    prev_log("onSurfaceTextureDestroyed");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
    //prev_log("onSurfaceTextureUpdated");
        }

}; // TextureView.SurfaceTextureListener


} // class MainActivity
