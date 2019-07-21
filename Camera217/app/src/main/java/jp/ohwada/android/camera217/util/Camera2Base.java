/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera217.util;


import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;


import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.CameraCharacteristics.Key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;


import jp.ohwada.android.camera217.ui.AutoFitTextureView;


 /**
  *  class Camera2Base
 * similar to CameraSource of Vision API
 * original : https://github.com/EzequielAdrianM/Camera2Vision
  */
public class Camera2Base {


        // debug
	protected final static boolean D = true;
    protected final static String TAG = "Camera2";
    protected final static String TAG_BASE = "Camera2Base";

        // image size、value for Nexus5
        public final static  int DEFAULT_WIDTH = 1280;
        public final static  int DEFAULT_HEIGHT = 768;

    protected final static String LF = "\n";


    /**
     * Camera state: Device is closed.
     */
    public static final int STATE_CLOSED = 0;

 /**
     * Camera state: Device is opened, but is not capturing.
     */
    public static final int STATE_OPENED = 1;

    /**
     * Camera state: Showing camera preview.
     */
    public static final int STATE_PREVIEW = 2;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    public static final int STATE_WAITING_LOCK = 3;


    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    public static final int STATE_WAITING_PRECAPTURE = 4;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    public static final int STATE_WAITING_NON_PRECAPTURE = 5;

    /**
     * Camera state: Picture was taken.
     */
    public static final int STATE_PICTURE_TAKEN = 6;


    // camera face
    public static final int CAMERA_FACING_BACK = CameraCharacteristics.LENS_FACING_BACK;
    public static final int CAMERA_FACING_FRONT = CameraCharacteristics.LENS_FACING_FRONT;
    protected int mFacing = CAMERA_FACING_BACK;

    // CaptureRequest
    // AE : auto exposure mode 
    public static final int CAMERA_FLASH_OFF = CaptureRequest.CONTROL_AE_MODE_OFF;
    public static final int CAMERA_FLASH_ON = CaptureRequest.CONTROL_AE_MODE_ON;
    public static final int CAMERA_FLASH_AUTO = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;
    public static final int CAMERA_FLASH_ALWAYS = CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH;
    public static final int CAMERA_FLASH_REDEYE = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE;
    protected int mFlashMode = CAMERA_FLASH_AUTO;

    // AF : auto focas mode
    public static final int CAMERA_AF_AUTO = CaptureRequest.CONTROL_AF_MODE_AUTO;
    public static final int CAMERA_AF_EDOF = CaptureRequest.CONTROL_AF_MODE_EDOF;
    public static final int CAMERA_AF_MACRO = CaptureRequest.CONTROL_AF_MODE_MACRO;
    public static final int CAMERA_AF_OFF = CaptureRequest.CONTROL_AF_MODE_OFF;
    public static final int CAMERA_AF_CONTINUOUS_PICTURE = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
    public static final int CAMERA_AF_CONTINUOUS_VIDEO = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO;
    protected int mFocusMode = CAMERA_AF_AUTO;


// hardware level
// 0
    public static final int LEVEL_LIMITED =            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL;
// 1
    public static final int LEVEL_FULL =           CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED;
// 2
    public static final int LEVEL_LEGACY =    CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
// 3
    public static final int LEVEL_3 =          CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3;
     // Added in API level 28
     // 4
    //public static final int LEVEL_EXTERNAL =          CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL;

    public static final int LEVEL_NOT_SUPPORT =  -1; 
    public static final int LEVEL_NOT_FOUND = -2;  
    public static final int LEVEL_NONE = -3;  


    // CameraDevice StateCallback Error
    // value 1
   private static final int ERROR_CAMERA_IN_USE = CameraDevice.StateCallback.ERROR_CAMERA_IN_USE;
    // value 2
    private static final int ERROR_MAX_CAMERAS_IN_USE = CameraDevice.StateCallback.ERROR_MAX_CAMERAS_IN_USE;
    // value 3
    private static final int ERROR_CAMERA_DISABLED = CameraDevice.StateCallback.ERROR_CAMERA_DISABLED;
    // value 4
   private static final int ERROR_CAMERA_DEVICE = CameraDevice.StateCallback.ERROR_CAMERA_DEVICE;
    // value 5
    private static final int ERROR_CAMERA_SERVICE = CameraDevice.StateCallback.ERROR_CAMERA_SERVICE;



    // choiceBestAspectPictureSize
    protected static final double RATIO_TOLERANCE = 0.1;
    protected static final double MAX_RATIO_TOLERANCE = 0.18;

    protected static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    protected static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();


    /**
     * Clockwise angle through which the output image needs to be rotated to be upright on the device screen in its native orientation
     */
    protected int mSensorOrientation;

    /**
     * the rotation of the screen from its "natural" orientation.
     */
    protected int mDisplayRotation;


    /**
     * isSwappedDimensions that can be used by the record video  routine.
     */
    protected boolean isSwappedDimensions = false;


    protected Context mContext;

    // view
    protected AutoFitTextureView mTextureView;
    protected SurfaceView mViewSurfaceView;
    protected Surface mViewSurface;


    /**
     * A lock protecting camera state.
     */
    protected final Object mCameraStateLock = new Object();


    /**
     * A reference to the opened {@link CameraDevice}.
     */
    protected CameraDevice mCameraDevice;

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
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    protected int mState = STATE_CLOSED;


    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    protected CameraCaptureSession mCaptureSession;


    /**
     * The {@link Size} of camera preview.
     */
    protected Size mPreviewSize;

    /**
     *  The size of the largest JPEG format image of the camera　supports
     */
    protected Size mLargestSize;



    /**
     * ID of the current {@link CameraDevice}.
     */
    protected String mCameraId;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    protected static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    protected static final int MAX_PREVIEW_HEIGHT = 1080;


    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }



    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    protected Semaphore mCameraOpenCloseLock = new Semaphore(1);


    /**
     * The area of the image sensor which corresponds to active pixels after any geometric distortion correction has been applied
     * The area that can be used by the auto-focus (AF) routine.
     */
    protected Rect mSensorArraySize;


    /**
     * Whether the current camera device supports Flash or not.
     */
    protected boolean isFlashSupported;


    /**
     * metering regions that can be used by the auto-focus (AF)  routine.
     */
    protected boolean isMeteringAreaAFSupported = false;


/**
 * Flag whether to support AutoFocusContinuousPicture
 * CONTROL_AF_MODE_AUTO
  */
    protected boolean isAutoFocusSupported = false;


/**
 * Flag whether to support AutoFocusContinuousPicture
 * CONTROL_AF_MODE_CONTINUOUS_PICTURE
  */
    protected boolean isAutoFocusContinuousPictureSupported = false;

/**
 * Flag whether to support AutoExposureOn
 * CONTROL_AE_MODE_ON
  */
     protected boolean   isAutoExposureOnSupported = false;


/**
 * Flag whether to support AutoFlash
 * CONTROL_AE_MODE_ON_AUTO_FLASH
  */
    protected boolean  isAutoFlashSupported = false;


/**
 * Flag whether to support AutoWhiteBalnce
 * CONTROL_AWB_MODE_AUTO
  */
   protected boolean isAutoWhiteBalnceSupported = false;



    /**
     * Callback interface used to indicate when an error occurs
     */
    public interface ErrorCallback{
        public void onError(String msg);
    }

    protected ErrorCallback mErrorCallback;


/**
   *  constractor
  */
        public Camera2Base() {
            // nop
        }

/**
   *  constractor
  */
        public Camera2Base(Context context) {
            mContext = context;
        }

/**
   *  setFocusMode
  */
        public void setFocusMode(int mode) {
            mFocusMode = mode;
        }

/**
   *  setFlashMode
  */
        public void setFlashMode(int mode) {
            mFlashMode = mode;
        }

/**
   * setFacing
  */
        public void setFacing(int facing) {
                mFacing = facing;
    }


/**
   *  setErrorCallback
  */
        public void setErrorCallback(ErrorCallback cb) {
            mErrorCallback = cb;
        }

 /**
  * getCameraFacing
  */
    public int getCameraFacing() {
        return mFacing;
}


 /**
  *  isCameraFacingFront
  */
    public boolean isCameraFacingFront() {
            boolean isFront = false;
            if (mFacing == CAMERA_FACING_FRONT) {
                    isFront = true;
            }
            return isFront;
}



/**
  * getPreviewSize
 */
    public Size getPreviewSize() {
        return mPreviewSize;
}


/**
  * getSensorArraySize
  */
    public Rect getSensorArraySize() {
        return mSensorArraySize;
    }


    /**
     * isSwappedDimensions
     */
    public boolean isSwappedDimensions() {
        return isSwappedDimensions;
    }


/**
  * startBackgroundThread
  */
    protected void startBackgroundThread() {
log_base("startBackgroundThread");
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * stopBackgroundThread
     */
    protected void stopBackgroundThread() {
log_base("stopBackgroundThread");
        try {
            if(mBackgroundThread != null) {
                mBackgroundThread.quitSafely();
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
}


/**
   * release
   */
    public void release() {
        log_base("release");
        stop();
}


/**
  * stop
  */
    public void stop() {
        log_base("stop");
        synchronized (mCameraStateLock) {
            mState = STATE_CLOSED;
        }
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
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
                mCameraOpenCloseLock.release();
                stopBackgroundThread();
        }
        stopExtend();
} // stop


/**
 * stopExtend
 */
protected void stopExtend() {
}


/**
  * getState
  */
    public int getState() {
        return mState;
} // getState


/**
 * getDeviceLevel
 */
    public int getDeviceLevel() {
            log_base("getDeviceLevel");
            CameraManager manager = null;
            String cameraId = null;
            int deviceLevel = LEVEL_NONE;
        try {
            manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            if (manager != null) {
                    cameraId = getCameraId(manager, mFacing);
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            }
        } catch (Exception ex) {
                ex.printStackTrace();
        }
        if (manager == null) return LEVEL_NOT_SUPPORT;
        if( cameraId == null ) return LEVEL_NOT_FOUND;
        return deviceLevel;
} // getDeviceLevel


/**
 * isCamera2Native
 */
    public boolean isCamera2Native() {
            log_base("isCamera2Native");
            int deviceLevel = getDeviceLevel();
            // This camera device is running in backward compatibility mode.
          boolean ret =  (deviceLevel == LEVEL_LEGACY)? true: false;
        return ret;
} // isCamera2Native



/**
 * start with AutoFitTextureView
 */
    public void start(AutoFitTextureView textureView) {
        log_base(" start AutoFitTextureView");
        mTextureView = textureView;
        setUpCameraOutputs(textureView.getWidth(), textureView.getHeight() );
        SurfaceTexture texture = textureView.getSurfaceTexture();
        if(mPreviewSize != null) {
                texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        }
        mViewSurface  = new Surface(texture);
        startBackgroundThread();
        prepareCamera();
        openCamera();
} // start


/**
 * start with SurfaceView
 */
    public void start(SurfaceView surfaceView) {
        log_base(" start SurfaceView");
        mViewSurfaceView = surfaceView;
        SurfaceHolder holder = mViewSurfaceView.getHolder();
        mViewSurface = holder.getSurface();
        setUpCameraOutputs(surfaceView.getWidth(), surfaceView.getHeight());
        startBackgroundThread();
        prepareCamera();
        openCamera();
} // start


 /**
  * setUpCameraOutputs
  */
    protected void setUpCameraOutputs(int width, int height) {
        log_base("setUpCameraOutputs");

        // check permission
        if (!CameraPerm.isCameraGranted(mContext)) {
                notifyError("NOT Permission to access Camera");
                return;
        }

        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
         if(manager == null) {
                notifyError("This device doesn't support Camera2 API");
                return;
        }
        mCameraId = getCameraId(manager, mFacing);
                log_base("cameraId= " + mCameraId);
         if(mCameraId == null) {
                notifyError("NOT found " + getCameraFaceString());
                return;
        }

        CameraCharacteristics characteristics = null;
        StreamConfigurationMap map = null;
        try {
                characteristics
                        = manager.getCameraCharacteristics(mCameraId);
                map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if (characteristics == null) {
                    log_base("characteristics null");
                    return;
        }
        if (map == null) {
                    log_base("map null");
                    return;
        }
        
        // For still image captures, we use the largest available size.
        mLargestSize =    getJpegLargestSize(map);

        log_base(  "LargestSize: " +mLargestSize.toString() );

        // no inspection ConstantConditions
        mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // preview size
        mDisplayRotation = DisplayUtil.getDisplayRotation(mContext);


        // Find out if we need to swap dimension to get the preview size relative to sensor coordinate.
        isSwappedDimensions = getSwappedDimensions();
        mPreviewSize = calcPreviewSize(map, width, height, mLargestSize, isSwappedDimensions);

        // The area of the image sensor which corresponds to active pixels after any geometric distortion correction has been applied.
            mSensorArraySize = getSensorArraySize(characteristics);

        //The maximum number of metering regions that can be used by the auto-focus (AF) routine.
            isMeteringAreaAFSupported = hasMeteringAreaAFSupported(characteristics);

       // Check if the flash is supported.
        isFlashSupported = hasFlashSupport(characteristics);

        configureTransform(width, height, mPreviewSize, mDisplayRotation);

        setUpExtend( characteristics);

} //  setUpCameraOutputs


/**
 * setUpExtend
 * for override
 */
protected void setUpExtend(CameraCharacteristics characteristics) {
    // nop
}


/**
   * setUp3ACotrol
  */
protected void setUp3ACotrol(CameraCharacteristics characteristics) {

    isAutoFocusSupported = 
    containsCharacteristics(characteristics, CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES, 
    CameraMetadata.CONTROL_AF_MODE_AUTO );
    log_base("AutoFocusAuto: " + isAutoFocusSupported);

    isAutoFocusContinuousPictureSupported = 
    containsCharacteristics(characteristics, CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES, 
    CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE );
    log_base("AutoFocusContinuousPicture: " + isAutoFocusContinuousPictureSupported);

    isAutoExposureOnSupported 
    = containsCharacteristics(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, 
    CaptureRequest.CONTROL_AE_MODE_ON );
    log_base("AutoExposureOn: " +  isAutoExposureOnSupported);

    isAutoFlashSupported 
    = containsCharacteristics(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, 
    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH );
    log_base("isAutoFlashSupported: " +  isAutoFlashSupported);

    isAutoWhiteBalnceSupported 
    = containsCharacteristics(characteristics, CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES, 
    CameraCharacteristics.CONTROL_AWB_MODE_AUTO );
    log_base("AutoWhiteBalnce: " +  isAutoWhiteBalnceSupported);

}


/**
 * getAutoFocusPictureMode
 */
protected int getAutoFocusPictureMode() {

        int mode = CameraParam.AF_MODE_OFF;
        if(isAutoFocusContinuousPictureSupported) {
            mode = CameraParam.AF_MODE_CONTINUOUS_PICTURE;
        } else if(isAutoFocusSupported) {
            mode = CameraParam.AF_MODE_AUTO;
        }
        return mode;
}


/**
 * getAutoExposureFlashMode
 */
protected int getAutoExposureFlashMode() {

        int mode = CameraParam.AE_MODE_OFF;
        if (isAutoFlashSupported) {
            mode = CameraParam.AE_MODE_ON_AUTO_FLASH;
        } else if (isAutoExposureOnSupported) {
            mode = CameraParam.AE_MODE_ON;
        }
        return mode;
}


/**
 * getAutoWhiteBalanceMode
 */
protected int getAutoWhiteBalanceMode() {

        int mode = CameraParam.AWB_MODE_OFF;
        if (isAutoWhiteBalnceSupported) {
            mode = CameraParam.AWB_MODE_AUTO;
        }
        return mode;
}


/**
   * checkAvailableCapabilities
  */
protected boolean checkAvailableCapabilities(CameraCharacteristics characteristics, int key) {
    return containsCharacteristics(characteristics, CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES, 
    key );
}


/**
   * containsCharacteristics
  */
protected boolean containsCharacteristics(CameraCharacteristics characteristics, Key<int[]> key1, int key2 ) {
    int[] modes = characteristics.get(key1);
    return contains(modes, key2);
} // containsCharacteristics


/**
  * contains
  * Return true if the given array contains the given integer.
  */
protected boolean contains(int[] modes, int key_mode) {

        if (modes == null) {
            return false;
        }
        for (int i=0; i<modes.length; i++ ) {
            int mode = modes[i];
            if (mode == key_mode) {
                return true;
            }
        } // for
        return false;
} // contains


/**
 * getCameraId
 */
protected String getCameraId(CameraManager manager, int cameraFacing) {
        String cameraId = null;
        try {
            String[] ids = manager.getCameraIdList();
            for (int i=0; i<ids.length; i++ ) {
                String id = ids[i];
                CameraCharacteristics c
                        = manager.getCameraCharacteristics(id);
                int facing = c.get(CameraCharacteristics.LENS_FACING);
                if (facing == cameraFacing) {
                    cameraId = id;
                    break;
                }
            } // for
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return cameraId;
} // getCameraId


/**
 * getCameraFace
 */
protected String getCameraFaceString() {
    String msg = "";
    switch(mFacing) {
        case CAMERA_FACING_FRONT:
            msg = "Front Camera";
            break;
        case CAMERA_FACING_BACK:
            msg = "Back Camera";
            break;
    }
    return msg;
} // getCameraFace


/**
 * getJpegLargestSize
 */
protected Size getJpegLargestSize(StreamConfigurationMap map) {

    Size size = chooseLargestSize(map, ImageFormat.JPEG);
    log_base("JPEG LargestSize: " + size.toString());
    return size;
} // getJpegLargestSize


/**
 * chooseLargestSize
 */
protected Size chooseLargestSize(StreamConfigurationMap map, int format) {
    Size[] supportedSizes = map.getOutputSizes(format);
    Size largest = Collections.max(Arrays.asList(supportedSizes), new CompareSizesByArea());
    return largest;
} // chooseLargestSize


/**
  * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
 * This method should be called after the camera preview size is  * determined in setUpCameraOutputs 
 */
protected void configureTransform(int viewWidth, int viewHeight, Size previewSize, int displayRotation) {
        if (null == mTextureView || null == previewSize ) {
            return;
        }
        int previewWidth = previewSize.getWidth();
        int previewHeight = previewSize.getHeight();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewHeight, previewWidth);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == displayRotation || Surface.ROTATION_270 == displayRotation) {
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                float scale = Math.max(
                    (float) viewHeight / previewHeight,
                    (float) viewWidth / previewWidth);
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (displayRotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == displayRotation) {
                matrix.postRotate(180, centerX, centerY);
        }
        if( mTextureView != null ) {
            mTextureView.setTransform(matrix);
        }
} // configureTransform


/**
  * calcPreviewSize
  */
protected Size calcPreviewSize(StreamConfigurationMap map, int width, int height, Size largest, boolean isSwapped) {
                    Point displaySize = DisplayUtil.getDisplaySize(mContext);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;
                if (isSwapped) {
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

            // Danger
            // Attempting to use too large a preview size could  exceed the camera bus' bandwidth limitation
            Size[] choices = map.getOutputSizes(SurfaceTexture.class);
            Size previewSize = chooseOptimalSize(choices,
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);
    log_base("PreviewSize: " + previewSize.toString());
    return previewSize;
} // calcPreviewSize


/**
  * choose the smallest one that
  * is at least as large as the respective texture view size, and that is at most as large as the
  * respective max size, and whose aspect ratio matches with the specified value. If such size
  * doesn't exist, choose the largest one that is at most as large as the respective max size,
 * and whose aspect ratio matches with the specified value.
  */
protected Size chooseOptimalSize(Size[] choices, int textureViewWidth,
            int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
            log_base("chooseOptimalSize");
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
            log_base("Couldn't find any suitable preview size");
            return choices[0];
        }
} // chooseOptimalSize


/**
 * getSensorArraySize
 */
protected Rect getSensorArraySize(CameraCharacteristics characteristics) {
        Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        log_base(  "SensorArraySize=" + rect.toString() );
        return rect;
}


/**
 * getDisplayOrientation
 */
protected int getDisplayOrientation() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getRotation();
} // getDisplayOrientation


/**
 * getDisplaySize
 */
protected Point getDisplaySize() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
} // getDisplaySize


/**
 * hasMeteringAreaAFSupported
 * The maximum number of metering regions that can be used by the auto-focus (AF) routine.
 */
protected boolean hasMeteringAreaAFSupported(CameraCharacteristics characteristics) {
            Integer maxAFRegions = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
            boolean isSupported = (maxAFRegions >= 1)? true: false;
            log_base( "maxAFRegions= "  + maxAFRegions + " isMeteringAreaAFSupported= " + isSupported);
        return  isSupported;
}


/**
 * hasFlashSupport
 */
protected boolean hasFlashSupport(CameraCharacteristics characteristics) {
        log_base("hasFlashSupport");
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                boolean hasSupport = available == null ? false : available;
        return hasSupport;
} // hasFlashSupport


/**
 * Find out if we need to swap dimension to get the preview size relative to sensor coordinate.
 */
protected boolean getSwappedDimensions() {
         	            boolean swappedDimensions = false;
            	switch (mDisplayRotation) {
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
            	        log_base( "Display rotation is invalid: " + mDisplayRotation);
            	}
                log_base("SwappedDimensions= " + swappedDimensions);
                return swappedDimensions;
} // getSwappedDimensions



/**
  * choiceBestAspectPictureSize
 * For still image captures, we use the largest available size.
 */
    protected Size choiceBestAspectPictureSize(StreamConfigurationMap  map) {
    Size[] supportedPictureSizes = map.getOutputSizes(ImageFormat.JPEG);
        //float targetRatio = Utils.getScreenRatio(mContext);
        float targetRatio = getScreenRatio();
        Size bestSize = null;
        TreeMap<Double, List<Size>> diffs = new TreeMap<>();
        //Select supported sizes which ratio is less than RATIO_TOLERANCE
        for (Size size : supportedPictureSizes) {
            float ratio = (float) size.getWidth() / size.getHeight();
            double diff = Math.abs(ratio - targetRatio);
            if (diff < RATIO_TOLERANCE){
                if (diffs.keySet().contains(diff)){
                    //add the value to the list
                    diffs.get(diff).add(size);
                } else {
                    List<android.util.Size> newList = new ArrayList<>();
                    newList.add(size);
                    diffs.put(diff, newList);
                }
            }
        }
        //If no sizes were supported, (strange situation) establish a higher RATIO_TOLERANCE
        if(diffs.isEmpty()) {
            for (Size size : supportedPictureSizes) {
                float ratio = (float)size.getWidth() / size.getHeight();
                double diff = Math.abs(ratio - targetRatio);
                if (diff < MAX_RATIO_TOLERANCE){
                    if (diffs.keySet().contains(diff)){
                        //add the value to the list
                        diffs.get(diff).add(size);
                    } else {
                        List<android.util.Size> newList = new ArrayList<>();
                        newList.add(size);
                        diffs.put(diff, newList);
                    }
                }
            }
        }
        //Select the highest resolution from the ratio filtered ones.
        for (Map.Entry entry: diffs.entrySet()){
            List<?> entries = (List) entry.getValue();
            for (int i=0; i<entries.size(); i++) {
                android.util.Size s = (android.util.Size) entries.get(i);
                if(bestSize == null) {
                    bestSize = new Size(s.getWidth(), s.getHeight());
                } else if(bestSize.getWidth() < s.getWidth() || bestSize.getHeight() < s.getHeight()) {
                    bestSize = new Size(s.getWidth(), s.getHeight());
                }
            }
        }
        return bestSize;
} // choiceBestAspectPictureSize




/**
  * getScreenRatio
 */
protected float getScreenRatio() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float ratio = ((float)metrics.heightPixels / (float)metrics.widthPixels);
        return ratio;
} // getScreenRatio


/**
  * notifyError
  * callback error, or throw RuntimeException
 */
protected void notifyError(String msg) {
            log_base("notifyError: " + msg);
            if (mErrorCallback != null) {
                        mErrorCallback.onError(msg);
            } else {
                    throw new RuntimeException(msg);
            }
} // notifyError


/**
  * prepareCamera
 * for override
 */
protected void prepareCamera() {
    // nop
}


/**
  * openCamera
 */
protected void openCamera() {
            log_base("openCamera");
    // check permission
    if (!CameraPerm.isCameraGranted(mContext)) {
                log_base("not granted camera permission");
                return;
    }
    if (mCameraId == null) {
                log_base("CameraId null");
                return;
    }
        try {
                CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
                e.printStackTrace();
        } catch (NullPointerException e) {
                e.printStackTrace();
        } catch (Exception ex) {
                ex.printStackTrace();
        }
} // openCamera


/**
   * closePreviewSession
  */
    protected void closePreviewSession() {
            log_base("closePreviewSession");
        if(mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
} // closePreviewSession


/**
   * createCameraPreviewSession
  */
    protected void createCameraPreviewSession() {
        log_base("createCameraPreviewSession");
        // skip, if not open camera
        if(mCameraDevice == null) return;

        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mViewSurface);
                List outputs = Arrays.asList(mViewSurface);
                mCameraDevice.createCaptureSession(outputs, mPreviewSession, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
} // createCameraPreviewSession


/**
   * CameraCaptureSession.StateCallback
  */
protected CameraCaptureSession.StateCallback mPreviewSession = new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured( CameraCaptureSession cameraCaptureSession) {
                    log_base("onConfigured");
                    procCaptureSessionConfigured(cameraCaptureSession);
               }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    log_base("CameraCaptureSession Configuration Failed!");
                }
}; // CameraCaptureSession.StateCallback

/**
  * procCaptureSessionConfigured
  */
protected void procCaptureSessionConfigured( CameraCaptureSession cameraCaptureSession) {
    log_base("procCaptureSessionConfigured");
                    // The camera is already closed
                    if (null == mCameraDevice) {
                        return;
                    }

                    // When the session is ready, we start displaying the preview.
                    mCaptureSession = cameraCaptureSession;

                    try {
                        // Auto focus should be continuous for camera preview.
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, mFocusMode);
                        if(isFlashSupported) {
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, mFlashMode);
                        }

                        // Finally, we start displaying the camera preview.
                        mPreviewRequest = mPreviewRequestBuilder.build();
                        mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
} // procCaptureSessionConfigured


/**
 * write into logcat
 */ 
protected void log_base( String msg ) {
	    if (D) Log.d( TAG, TAG_BASE + " " + msg );
} // log_base


    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    protected CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened( CameraDevice cameraDevice) {
            log_base("onOpened");
            procStateOpened( cameraDevice);
        }
        @Override
        public void onDisconnected( CameraDevice cameraDevice) {
            log_base("onDisconnected");
            procStateDisconnected( cameraDevice);
        }
        @Override
        public void onError( CameraDevice cameraDevice, int error) {
            log_base("onError: ");
            procStateError( cameraDevice, error);
        }

}; // CameraDevice.StateCallback



 /**
  * procStateOpened
 */
protected void procStateOpened( CameraDevice cameraDevice) {
        log_base("procStateOpened");

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
    log_base("procStateDisconnected");
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
        }
            mCameraDevice = null;
            String msg = "State Error: " + LF + getCameraDeviceStateErrorMsg(error);
            log_base(msg);
            notifyError(msg);

} // procStateError


/**
  * getCameraDeviceStateErrorMsg
 */
protected String getCameraDeviceStateErrorMsg(int error) {
    String msg = "";
    switch(error) {
        case ERROR_CAMERA_IN_USE:
            msg = "Camera inuse";
            break;
        case ERROR_MAX_CAMERAS_IN_USE:
            msg = "max Cameras in use";
            break;
        case ERROR_CAMERA_DISABLED:
            msg = "Camera Disabled";
            break;
        case ERROR_CAMERA_DEVICE:
            msg = "Camera Device Error";
            break;
        case ERROR_CAMERA_SERVICE:
            msg = "Camera Service Error";
            break;
    }
    return msg;
} // getCameraDeviceStateErrorMsg


/**
  * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
 */
protected CameraCaptureSession.CaptureCallback mCaptureCallback =  new CameraCaptureSession.CaptureCallback() {
    @Override
    public void onCaptureProgressed( CameraCaptureSession session,
                                         CaptureRequest request,
                                         CaptureResult result) {
            //log_base("onCaptureProgressed");
            procCaptureResult(result);
    }
    @Override
    public void onCaptureCompleted( CameraCaptureSession session,
                                        CaptureRequest request,
                                       TotalCaptureResult result) {
            //log_base("onCaptureCompleted");
            procCaptureResult( result);
    }

}; // CameraCaptureSession.CaptureCallback


/**
  * procCaptureResult
 * for override
 */
protected void procCaptureResult(CaptureResult result) {
}


} // class Camera2Base
