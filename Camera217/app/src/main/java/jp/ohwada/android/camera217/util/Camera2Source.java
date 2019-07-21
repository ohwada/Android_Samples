/**
 * Camera2 Sample
 * Manual Mode
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera217.util;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;


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
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;


import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


 /**
  *  class Camera2Source
 * similar to CameraSource of Vision API
 * original : https://github.com/EzequielAdrianM/Camera2Vision
  */
public class Camera2Source extends Camera2Base {

    // debug
    private final static String TAG_SUB = "Camera2Source";


    /**
     * The maximum number of images the user will want to access simultaneously for Image Reader Jpeg
     */
    private final static int IMAGE_READER_STILL_MAX_IMAGES = 2;


    /**
     * The maximum number of images the user will want to access simultaneously for Image Reader Raw
     */
    private final static int IMAGE_READER_RAW_MAX_IMAGES = 4;


    /**
     * Callback interface used to supply image data from a photo capture.
     */
    public interface PictureCallback {
        /**
         * Called when image data is available after a picture is taken.  The format of the data
         * is a JPEG Image.
         */
        void onJpegTaken(Image image, Date date);
        void onRawTaken(File file);
    }


   /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReaderStill;


   /**
     * An ImageReader that handles Raw image capture.
     */
    private ImageReader mImageReaderRaw;


    /**
     * This is a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private PictureDoneCallback mOnImageAvailableListener = new PictureDoneCallback();


    /**
     * callback used to supply image data from a photo capture.
     */
    private PictureCallback mPictureCallback;



/**
 * Flag whether to support RAW mode
  */
    private boolean isRawSupported = false;


/**
   * SENSOR_INFO_SENSITIVITY_RANGE
  */
    private Range<Integer> mSensitivityRange;


/**
   * SENSOR_INFO_EXPOSURE_TIME_RANGE
  */
    private    Range<Long> mExposureTimeRange;



/**
   * LENS_INFO_AVAILABLE_APERTURES
  */
    private float[] mApertures;


/**
 * Minimum Focus Distance
 * LENS_INFO_MINIMUM_FOCUS_DISTANCE
  */
    private Float mMinimumFocusDistance;


/**
  * the minimum SENSOR_FRAME_DURATION 
 */
    private long mMinFrameDuration;


/**
 * CameraCharacteristics for DngCreator
  */
    private CameraCharacteristics mCharacteristics;


/**
 * CaptureResult for DngCreator
  */
    private CaptureResult mCaptureResult;


/**
 * Date Time when capture started
 * in order to use the same time stamp with JPEG and RAW
  */
    private Date mCurrentDate;


/**
   * parameter of manual mode
  */
    private CameraParam mCameraParam;


/**
  * Flag whether to use external storage
 */
    private boolean isUseStorage = false;


/**
  * Flag whether to useRaw format
 */
    private boolean isUseRaw = false;

/**
   *  constractor
  */
        public Camera2Source() {
                super();
        }

/**
   *  constractor
  */
        public Camera2Source(Context context) {
                super(context);
        }



/**
   *  getSensitivityRange
  */
    public Range getSensitivityRange() {
        return mSensitivityRange;
    }


/**
   *  getExposureTimeRange
  */
    public Range getExposureTimeRange() {
        return mExposureTimeRange;
    }


/**
   *  getApertures
  */
    public float[] getApertures() {
        return mApertures;
    }


/**
   *  getMinimumFocusDistance
  */
    public Float getMinimumFocusDistance() {
        return mMinimumFocusDistance;
    }


/**
   *  getMinFrameDuration
  */
    public long getMinFrameDuration() {
        return mMinFrameDuration;
    }


/**
   *  createCameraParam
  */
    public CameraParam createCameraParam() {

        CameraParam param = new CameraParam();

        // set 3A mode
        param.setControlMode(CameraParam.CONTROL_MODE_AUTO);
        param.setAfMode( getAutoFocusPictureMode() );
        param.setAeMode( getAutoExposureFlashMode() );
        param.setAwbMode( getAutoWhiteBalanceMode() );

        // set exposure param
        param.setSensitivityRange(mSensitivityRange);
        param.setExposureTimeRange(mExposureTimeRange);
        return param;

    } // createCameraParam


/**
 * stopExtend
 */
@Override
protected void stopExtend() {
    log_d("stopExtend");
       try {
            if (null != mImageReaderStill) {
                mImageReaderStill.close();
                mImageReaderStill = null;
            }
            if (null != mImageReaderRaw) {
                mImageReaderRaw.close();
                mImageReaderRaw = null;
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
} // stopExtend



    /**
     * Initiate a still image capture. The camera preview is suspended
     * while the picture is being taken, but will resume once picture taking is done.
     */
    public void takePicture(PictureCallback picCallback) {
        log_d("takePicture");
       mPictureCallback = picCallback;
        lockFocus();
} // takePicture




/**
 * takePictureWithParam
  */
public void takePictureWithParam(CameraParam param, PictureCallback picCallback) {

    if(param != null) {
            String msg = "takePictureWithParam: " + param.toString();
            log_d(msg );
    }

       mPictureCallback = picCallback;
        mCameraParam = param;

        isUseStorage = param.getUseStorage();
        isUseRaw = param.getUseRaw();

        lockFocus();
} // takePictureWithParam


/**
 * setUpExtend
 */
@Override
protected void setUpExtend(CameraCharacteristics characteristics) {

    log_d("setUpExtend");

    boolean isManualSensorAvailable 
    = checkAvailableCapabilities(characteristics,
    CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR);
    isRawSupported = checkAvailableCapabilities(characteristics,
    CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_RAW);

    boolean isManualExposureSupported 
    = checkManualExposureAvailable(characteristics);
    log_d("ManualExposureSupported: " + isManualExposureSupported);

    if( !isManualSensorAvailable ) {
        notifyError("This device doesn't support ManualSensor");
        return;
    }
    if( !isManualExposureSupported ) {
        notifyError("This device doesn't support ManualExposure");
        return;
    }

    setUp3ACotrol(characteristics);
    setUpManualParam(characteristics);
    setupImageReaderStill(characteristics);
    setupImageReaderRaw(characteristics);

} // setUpExtend




/**
 * setUpManualParam
 */
private void setUpManualParam(CameraCharacteristics characteristics) {

    mSensitivityRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
    mExposureTimeRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
    mApertures = getAvailableApertures(characteristics);

    if (mSensitivityRange == null) {
        notifyError("NOT support SENSITIVITY_RANGE");
        return;
    }
    if (mExposureTimeRange == null) {
        notifyError("NOT support EXPOSURE_TIME_RANGE");
        return;
    }
    if (mApertures == null) {
        notifyError("NOT support APERTURES");
        return;
    }

    log_d("SensitivityRang= " + mSensitivityRange.toString());
    log_d("ExposureTimeRange= " + mExposureTimeRange.toString());

    mMinimumFocusDistance =
                characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
    log_d("MinimumFocusDistance: " + mMinimumFocusDistance);

} // setUpManualParam


/**
   * getAvailableApertures
  */
private float[] getAvailableApertures(CameraCharacteristics characteristics) { 
    float[] apertures = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
    if(apertures == null) return null;

    for(int i=0; i<apertures.length; i++) {
        float aperture = apertures[i];
        log_d("aperture = " + i +" , " + aperture);
    } // for

    return apertures;
} // getAvailableApertures


/**
   * checkAwbAutoAvailable
  */
private boolean checkAwbAutoAvailable(CameraCharacteristics characteristics) {
    return containsCharacteristics(characteristics, CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES, 
    CameraCharacteristics.CONTROL_AWB_MODE_AUTO );
} // checkAwbAutoAvailable


/**
   * checkManualExposureAvailable
  */
private boolean checkManualExposureAvailable(CameraCharacteristics characteristics) {
    return containsCharacteristics(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, 
    CameraMetadata.CONTROL_AE_MODE_OFF );
} 









/**
   * setupImageReaderStill
  */
private void setupImageReaderStill(CameraCharacteristics characteristics) {
    if(mLargestSize == null) {
        log_d("LargestSize  null");
    }
    int w = mLargestSize.getWidth();
    int h = mLargestSize.getHeight();
    mImageReaderStill = ImageReader.newInstance(w, h, ImageFormat.JPEG, IMAGE_READER_STILL_MAX_IMAGES);
        mImageReaderStill.setOnImageAvailableListener( mOnImageAvailableListener, mBackgroundHandler);
} // setupImageReaderStill


/**
   * setupImageReaderRaw
  */
private void setupImageReaderRaw(CameraCharacteristics  characteristics) {

    if(!isRawSupported ) {
        notifyError("This device doesn't support capturing RAW photos");
        return;
    }   

    mCharacteristics = characteristics;
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

          Size largestRaw = chooseLargestSize(map, ImageFormat.RAW_SENSOR);

            mMinFrameDuration = map.getOutputMinFrameDuration(ImageFormat.RAW_SENSOR, largestRaw);
            log_d("MinFrameDuration: " + mMinFrameDuration);

                mImageReaderRaw =
                                ImageReader.newInstance( 
                                        largestRaw.getWidth(),
                                        largestRaw.getHeight(), ImageFormat.RAW_SENSOR, 
                                        IMAGE_READER_RAW_MAX_IMAGES);

                mImageReaderRaw.setOnImageAvailableListener(
                            mOnRawImageAvailableListener, mBackgroundHandler);

} // setupImageReaderRaw


/**
   * createCameraPreviewSession
  */
@Override
protected void createCameraPreviewSession() {
    log_d("createCameraPreviewSession");
        try {
            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mViewSurface);
            // Here, we create a CameraCaptureSession for camera preview.
            List<Surface> outputs =  new ArrayList<Surface>();
            outputs.add(mViewSurface);
            outputs.add(mImageReaderStill.getSurface());
            if(mImageReaderRaw != null) {
                outputs.add(mImageReaderRaw.getSurface());
            }
                mCameraDevice.createCaptureSession(outputs, mPreviewSession, mBackgroundHandler);
        } catch (Exception e) {
                        e.printStackTrace();
        }
} //  createCameraPreviewSession


/**
 * procCaptureResult
 */ 
@Override
protected void procCaptureResult(CaptureResult result) {
        //log_d("procCaptureResult");
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState
                            || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState
                            || CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED == afState
                            || CaptureRequest.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState
                            || CaptureRequest.CONTROL_AF_STATE_INACTIVE == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }

} // procCaptureResult






    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (Exception e) {
                e.printStackTrace();
        }
} // runPrecaptureSequence



  /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
         log_d(" lockFocus");
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mPreviewRequest = mPreviewRequestBuilder.build();
            mCaptureSession.capture(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
} //  lockFocus


    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
         log_d("unlockFocus");
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            if(isFlashSupported) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, mFlashMode);
            }
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
} // unlockFocus


    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {

        log_d("captureStillPicture");

        if (null == mCameraDevice) {
                log_d("CameraDevice null");
                return;
        }

        mCurrentDate = new Date();

        try {
            // This is the CaptureRequest.Builder that we use to take a picture.
             CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget( mImageReaderStill.getSurface() );
            if( isUseRaw) {
                    captureBuilder.addTarget( mImageReaderRaw.getSurface() );
            }

            // Orientation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation());

            setupManualRequest(captureBuilder);

            CaptureRequest request = captureBuilder.build();
            debugLogRequest(request);
            mCaptureSession.stopRepeating();
            mCaptureSession.capture(request,  mCaptureCallbackStillPicture, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
} // captureStillPicture


/**
 * setupManualRequest
 */
private void setupManualRequest(CaptureRequest.Builder builder) { 
    log_d("setupManualRequest");
    CameraParam param = mCameraParam;
    if(param == null) {
        param = createCameraParam();
    }

    boolean isManualMode = param.getManualMode();
    int controlMode = param.getControlMode();
    int afMode = param.getAfMode();
    int aeMode = param.getAeMode();
    int awbMode = param.getAwbMode();
    int controlCaptureIntent = param.getControlCaptureIntent();
    int sensitivity = param.getSensitivity();
    long time = param.getExposureTime();
    float aperture = param.getAperture();
    int opticalStabilizationMode = param.getOpticalStabilizationMode();

    // 3A mode
    builder.set(CaptureRequest.CONTROL_MODE, controlMode);
    builder.set(CaptureRequest.CONTROL_AF_MODE, afMode);
    builder.set(CaptureRequest.CONTROL_AE_MODE, aeMode);
    builder.set(CaptureRequest.CONTROL_AWB_MODE, awbMode);

    if (isManualMode) {
        builder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, controlCaptureIntent);
        builder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
        builder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, time);
        builder.set(CaptureRequest.LENS_APERTURE, aperture);
        builder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, opticalStabilizationMode);
    }

} // setupManualRequest


/**
 * debugLogRequest
 */
private void debugLogRequest(CaptureRequest request) {

    int controlMode = request.get(CaptureRequest.CONTROL_MODE);
    int captureIntent = request.get(CaptureRequest.CONTROL_CAPTURE_INTENT);
    int aeMode = request.get(CaptureRequest.CONTROL_AE_MODE);
    int sensitivity = request.get(CaptureRequest.SENSOR_SENSITIVITY);
    long time = request.get(CaptureRequest.SENSOR_EXPOSURE_TIME);

        StringBuilder sb = new StringBuilder();
        sb.append("CaptureRequest: ");
        sb.append(" , Control mode= ");
        sb.append(controlMode);
        sb.append(" , CaptureIntent= ");
        sb.append(captureIntent);
        sb.append(" , AE mode= ");
        sb.append(aeMode);
        sb.append(" , sensitivity= ");
        sb.append(sensitivity);
        sb.append(" , ExposureTime== ");
        sb.append(time);
        log_d( sb.toString() );

}


    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getJpegOrientation() {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(mDisplayRotation) + mSensorOrientation + 270) % 360;
} // getOrientation




 /**
  * procCaptureSessionConfigured
  */
protected void procCaptureSessionConfigured(CameraCaptureSession cameraCaptureSession) {
        if (null == mCameraDevice) {
                    // The camera is already closed
                    return;
        }

        synchronized (mCameraStateLock) {
                    mState = STATE_PREVIEW;
                    // When the session is ready, we start displaying the preview.
                    mCaptureSession = cameraCaptureSession;
        }

        try {
                    setup3AControlsLocked(mPreviewRequestBuilder);
                    // Finally, we start displaying the camera preview.
                    //cameraCaptureSession.setRepeatingRequest(
                    //mPreviewRequestBuilder.build(),
                    //mPreCaptureCallback, mBackgroundHandler);
                    cameraCaptureSession.setRepeatingRequest(
                    mPreviewRequestBuilder.build(),
                    mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException | IllegalStateException e) {
                                    e.printStackTrace();
        }


} // procCaptureSessionConfigured



    /**
     * Configure the given {@link CaptureRequest.Builder} to use auto-focus, auto-exposure, and
     * auto-white-balance controls if available.
     * <p/>
     * Call this only with {@link #mCameraStateLock} held.
     *
     * @param builder the builder to configure.
     */
private void  setup3AControlsLocked(CaptureRequest.Builder builder) {
        log_d("setup3AControlsLocked");
        // Enable auto-magical 3A run by camera device
        builder.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO);


            // If there is a "continuous picture" mode available, use it, otherwise default to AUTO.
                if(isAutoFocusContinuousPictureSupported) {
                    builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                } else if (isAutoFocusSupported){
                        builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_AUTO);
                }


        // If there is an auto-magical flash control mode available, use it, otherwise default to
        // the "on" mode, which is guaranteed to always be available.
        if (isAutoFlashSupported) {
                builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        } else {
                builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON);
        }

        // If there is an auto-magical white balance control mode available, use it.
        if(isAutoWhiteBalnceSupported) {
            // Allow AWB to run auto-magically if this device supports this
                builder.set(CaptureRequest.CONTROL_AWB_MODE,
                    CaptureRequest.CONTROL_AWB_MODE_AUTO);
        }
}





/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


 /**
  * mCaptureCallbackStillPicture
  */
private CameraCaptureSession.CaptureCallback
        mCaptureCallbackStillPicture = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted( CameraCaptureSession session,  CaptureRequest request, TotalCaptureResult result) {
                        log_d("onCaptureCompleted");
                        // for DngCreator
                        mCaptureResult = result;
                        debugLogResult(result);
                        //finishedCaptureLocked();
                       unlockFocus();
        }
        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                        log_d("onCaptureFailed");
    }

}; // CameraCaptureSession.CaptureCallback


 /**
 * debugLogResult
 */
private void debugLogResult(CaptureResult result) {
   int mode = result.get(CaptureResult.CONTROL_AE_MODE);
    int sensitivity = result.get(CaptureResult.SENSOR_SENSITIVITY);
    long time = result.get(CaptureResult.SENSOR_EXPOSURE_TIME);
    String msg = "CaptureResult: ";
    msg += " AE mode= " + mode;
    msg += " ,  sensitivity= " + sensitivity;
    msg += " , exposureTime= " + time;
    log_d(msg);
}


/**
 * Called after a RAW/JPEG capture has completed; resets the AF trigger state for the
 * pre-capture sequence.
  * <p/>
  * Call this only with {@link #mCameraStateLock} held.
  */
private void finishedCaptureLocked() {
        try {
                // Reset the auto-focus trigger in case AF didn't run quickly enough.
                if ( !isAutoFocusSupported ) {
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                        CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                        //mCaptureSession.capture(mPreviewRequestBuilder.build(), mPreCaptureCallback,
                        //mBackgroundHandler);
                        mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                        mBackgroundHandler);
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                        CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
                }
    } catch (CameraAccessException e) {
            e.printStackTrace();
    }
} // finishedCaptureLocked


    /**
     * PictureDoneCallback
     */
    private class PictureDoneCallback implements ImageReader.OnImageAvailableListener {

        @Override
        public void onImageAvailable(ImageReader reader) {
            log_d("onImageAvailable");
            procImageAvailable(reader);
        }

} // class PictureDoneCallback


/**
  * procImageAvailable
 */
private void procImageAvailable(ImageReader reader) {
            log_d("procImageAvailable");
            Image image = null;
            try {
                image = reader.acquireNextImage();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (image == null) return;
            if(mPictureCallback != null) {
                mPictureCallback.onJpegTaken(image, mCurrentDate);
            }
            image.close();
} // procImageAvailable


    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * RAW image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnRawImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            //dequeueAndSaveImage(mRawResultQueue, // mRawImageReader);
            procImageAvailableRaw(reader);
        }

    }; // mOnRawImageAvailableListener


/**
  * procImageAvailableRaw
 */
private void procImageAvailableRaw(ImageReader reader) {
            log_d("procImageAvailableRaw");
            Image image = null;
            try {
                image = reader.acquireNextImage();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (image == null) return;
            File file = RawUtil.getOutputFile(mContext, mCurrentDate, isUseStorage);

            boolean ret = RawUtil.saveImage(
                mContext, 
                mCharacteristics, 
                mCaptureResult, 
                image, 
                file, 
                isUseStorage); 

            if(ret&&(mPictureCallback != null)) {
                mPictureCallback.onRawTaken(file);
            }
            image.close();
} // procImageAvailableRaw

 

    /**
     * Builder for configuring and creating an associated camera source.
     */
    public static class Builder {

        private Camera2Source mCameraSource;

        /**
         * Creates a camera source builder with the supplied context and detector.  Camera preview
         * images will be streamed to the associated detector upon starting the camera source.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            }
            mCameraSource
            = new Camera2Source(context);

        }

/**
  * setFocusMode
  */
        public Builder setFocusMode(int mode) {
            mCameraSource.setFocusMode(mode);
            return this;
        }

/**
  * setFocusMode
  */
        public Builder setFlashMode(int mode) {
            mCameraSource. setFlashMode(mode);
            return this;
        }

        /**
         * Sets the camera to use (either {@link #CAMERA_FACING_BACK} or
         * {@link #CAMERA_FACING_FRONT}). Default: back facing.
         */
        public Builder setFacing(int facing) {
            mCameraSource.setFacing(facing);
            return this;
        }


/**
   *  setErrorCallback
  */
        public Builder setErrorCallback(ErrorCallback cb) {
            mCameraSource.setErrorCallback(cb);
            return this;
        }


        /**
         * Creates an instance of the camera source.
         */
        public Camera2Source build() {
            return mCameraSource;
        } // build
} // class Builder 


} // class Camera2Source
