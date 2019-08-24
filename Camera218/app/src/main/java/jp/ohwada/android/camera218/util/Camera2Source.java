/**
 * Camera2 Sample
 * take Picture with burst mode 
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera218.util;


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


import android.graphics.YuvImage;
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
  * Flag for trial
  * TODO : the preview is disturbed, 
  * when use JPEG and YUV together
  */
    private final static boolean USE_JPEG = false;
    private final static boolean USE_YUV = true;
    private final static boolean USE_RAW = true;


/**
  * for ImageReader
  */
     private final static int IMAGE_READER_JPEG_MAX_IMAGES = 4;

    private final static int IMAGE_READER_YUV420_MAX_IMAGES = 4;

    private final static int 
    IMAGE_READER_RAW_MAX_IMAGES = 4;

/**
  *  Request Tag
  */
     private final static String TAG_FORMAT = "BURST %d";

/**
   * ImageReader Format
  */
    private int mImageReaderFormat = 0;


/**
   * Max Number of Burst Shots
  */
    private int mMaxBurst = 0;


 /**
  * Flag whether Manual Exposure
 */
    private boolean isManualExposure = false;


/**
  * Flag whether to use external storage
 */
    private boolean isUseStorage = false;


/**
  * Flag whether to save Images together when captureBurst is completed
 */
    private boolean isSaveTogether = false;


    /**
     * Callback interface used to supply image data from a photo capture.
     */
    public interface PictureCallback {
        void onPictureTaken(File file);
        void onYuvBurstTaken(List<YuvImage> list, int jpegOrientation);
    }


    /**
     * callback used to supply image data from a photo capture.
     */
    private PictureCallback mPictureCallback;


/**
   * LENS_INFO_AVAILABLE_APERTURES
  */
    private float[] mApertures;


 /**
  * ImageReader for Yuv
 */
    private ImageReader mImageReaderJpeg;

    private ImageReader mImageReaderRaw;

    private ImageReader mImageReaderYuv;


 /**
  * List of YuvImage
 */
    private List<YuvImage> mYuvImageList;


/**
 * List of CaptureResult for DngCreator
  */
    private List<CaptureResult> mResultList;


/**
 * 
 * counter for the number of burst shots
 */
    private int mJpegCount;
    private int mYuvCount;
    private int mRawCount;


/**
 * CameraCharacteristics for DngCreator
  */
    private CameraCharacteristics mCharacteristics;


/**
 * JpegOrientation
 * JPEG_ORIENTATION
  */
    private int mJpegOrientation;


/**
 * CurrentDate
  */
    private Date mCurrentDate;


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
 * stopExtend
 */
@Override
protected void stopExtend() {
    log_d("stopExtend");
       try {
            if (null !=  mImageReaderJpeg) {
                        mImageReaderJpeg.close();
                        mImageReaderJpeg = null;
            }
            if (null !=  mImageReaderYuv) {
                        mImageReaderYuv.close();
                        mImageReaderYuv = null;
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
     * take Picture
     */
    public void takePicture(BurstParam burstParam, PictureCallback picCallback) {

        log_d("takePicture: " + burstParam.toString() );
       mPictureCallback = picCallback;

        isManualExposure = burstParam.getManualMode();
        isUseStorage = burstParam.getUseStorage();
        isSaveTogether = burstParam.getSaveTogether();
        mImageReaderFormat = burstParam.getImageFormat();
        mMaxBurst = burstParam.getNumberOfShots();

    if(!USE_JPEG && (mImageReaderFormat == BurstParam.FORMAT_JPEG)) {
            notifyError("Camera2Source: disable JPEG");
    }

        lockFocus();

} // takePicture


/**
 * setUpExtend
 */
@Override
protected void setUpExtend(CameraCharacteristics characteristics) {

    log_d("setUpExtend");

   boolean isBurstAvailable = checkAvailableCapabilities(characteristics,
    CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE);

    if(!isBurstAvailable) {
        notifyError("This device doesn't support Burst Capture");
        return;
    }

    setUp3ACotrol(characteristics);
    setUpManualParam(characteristics);

    setUpImageReaderJpeg(characteristics);
    setUpImageReaderYuv(characteristics);
    setUpImageReaderRaw(characteristics);

} // setUpExtend

/**
 * setUpManualParam
 */
private void setUpManualParam(CameraCharacteristics characteristics) {

     mApertures = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);

    if (mApertures == null) {
        notifyError("NOT support APERTURES");
    }

} // setUpManualParam


/**
  * setUpImageReaderJpeg
  */
private void setUpImageReaderJpeg(CameraCharacteristics characteristics) {

        log_d("setUpImageReaderJpeg");

        StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);


        long minFrameDuration = map.getOutputMinFrameDuration(ImageFormat.JPEG, mLargestSize);
        long stallDuration = map.getOutputStallDuration(ImageFormat.JPEG, mLargestSize);
        log_d("JPEG MinFrameDuration: " + minFrameDuration);
        log_d("JPEG StallDuration: " + stallDuration);

        mImageReaderJpeg = ImageReader.newInstance( 
            mLargestSize.getWidth(), 
            mLargestSize.getHeight(), 
            ImageFormat.JPEG, IMAGE_READER_JPEG_MAX_IMAGES);
        mImageReaderJpeg.setOnImageAvailableListener(mImageJpegListener, mBackgroundHandler);

} // setUpImageReaderJpeg


/**
  * setUpImageReaderYuv
  */
private void setUpImageReaderYuv(CameraCharacteristics characteristics) {

        log_d("setUpImageReaderYuv");

        StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        Size yuv420Size = chooseLargestSize(map, ImageFormat.YUV_420_888);
        log_d("YUV_420_888 largest : " +  yuv420Size.toString());

        long minFrameDuration = map.getOutputMinFrameDuration(ImageFormat.YUV_420_888, yuv420Size);
        long stallDuration = map.getOutputStallDuration(ImageFormat.YUV_420_888, yuv420Size);
        log_d("YUV MinFrameDuration: " + minFrameDuration);
        log_d("YUV StallDuration: " + stallDuration);

        mImageReaderYuv = ImageReader.newInstance( 
            yuv420Size.getWidth(), 
            yuv420Size.getHeight(), 
            ImageFormat.YUV_420_888, IMAGE_READER_YUV420_MAX_IMAGES);
        mImageReaderYuv.setOnImageAvailableListener(mImageYuvListener, mBackgroundHandler);

} // setUpImageReaderYuv


/**
   * setUpImageReaderRaw
  */
private void setUpImageReaderRaw(CameraCharacteristics  characteristics) {

    boolean isRawAvailable =
    checkAvailableCapabilities( characteristics, 
    CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_RAW);

    if(!isRawAvailable ) {
        notifyError("This device doesn't support capturing RAW photos");
        return;
    }   

    mCharacteristics = characteristics;

        StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        Size largestRaw = chooseLargestSize(map, ImageFormat.RAW_SENSOR);

        log_d("RAW_SENSOR largest : " +  largestRaw.toString());

        long minFrameDuration = map.getOutputMinFrameDuration(ImageFormat.RAW_SENSOR, largestRaw);
        long stallDuration = map.getOutputStallDuration(ImageFormat.RAW_SENSOR, largestRaw);
        log_d("RAW MinFrameDuration: " + minFrameDuration);
        log_d("RAW StallDuration: " + stallDuration);

        mImageReaderRaw =
            ImageReader.newInstance( 
            largestRaw.getWidth(),
            largestRaw.getHeight(), 
            ImageFormat.RAW_SENSOR, 
            IMAGE_READER_RAW_MAX_IMAGES);

    mImageReaderRaw.setOnImageAvailableListener(
        mImageRawListener, mBackgroundHandler);

} // setUpImageReaderRaw


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
                List<Surface> outputs =  getSurfaceOutputs();
                mCameraDevice.createCaptureSession(outputs, mPreviewSession, mBackgroundHandler);
        } catch (Exception e) {
                        e.printStackTrace();
        }
} //  createCameraPreviewSession


/**
 * getSurfaceOutputs
 */ 
private List<Surface> getSurfaceOutputs() {

    List<Surface> outputs =  new ArrayList<Surface>();
    outputs.add(mViewSurface);

    if(USE_JPEG) {
            outputs.add( mImageReaderJpeg.getSurface() );
    }
    if(USE_YUV) {
            outputs.add( mImageReaderYuv.getSurface() );
    }
    if(USE_RAW) {
            outputs.add( mImageReaderRaw.getSurface() );
    }
    return outputs;
}


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
            mCaptureSession.capture( 
            mPreviewRequestBuilder.build(),
            mCaptureCallback, 
            mBackgroundHandler);
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
            mCaptureSession.setRepeatingRequest( 
            mPreviewRequestBuilder.build(), 
            mCaptureCallback, 
            mBackgroundHandler);
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

        // clear counter
        mJpegCount = 0;
        mYuvCount = 0;
        mRawCount = 0;
        mCurrentDate = new Date();

        // setup List
        mYuvImageList = new ArrayList<YuvImage>();
        mResultList = new ArrayList<CaptureResult>();

        try {
            mCaptureSession.stopRepeating();
            if (mMaxBurst == 1) {
                    // single
                    CaptureRequest.Builder captureBuilder = createStillCaptureRequestBuilder();
                    mCaptureSession.capture(captureBuilder.build(),  mCaptureCallbackStillPicture, mBackgroundHandler);
            } else {
                    // burst
                    List<CaptureRequest> list = createBurstRequestList();
                    mCaptureSession.captureBurst(list,  mCaptureCallbackStillPicture, mBackgroundHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

} // captureStillPicture


 /**
  *  createBurstRequestList
  */
private List<CaptureRequest> createBurstRequestList() {

    List<CaptureRequest> list = new ArrayList<CaptureRequest>();

    CaptureRequest.Builder captureBuilder = createStillCaptureRequestBuilder();
    CaptureRequest captureRequest = captureBuilder.build();

    for (int i=0; i < mMaxBurst; i++) {
        CaptureRequest.Builder builder = captureBuilder;
        String tag = String.format(TAG_FORMAT, i);
        builder.setTag(tag);
        if(isManualExposure ) {
            int sensitivity = BurstParam.getSensorSensitivity(i);
            int compensation = BurstParam.getExposureCompensation(i);
            builder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
            builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, compensation);
        }
        list.add( builder.build() );
    } // for

    return list;

} //  createBurstRequestList


 /**
  *  createStillCaptureRequestBuilder
  */
private CaptureRequest.Builder createStillCaptureRequestBuilder() {

    CaptureRequest.Builder captureBuilder = null;
    try {
            captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
    } catch (CameraAccessException e) {
            e.printStackTrace();
    }

    // ImageReader
        captureBuilder.addTarget(  getImageReaderSurface() );

        // setup 3A
        captureBuilder.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO);
        captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
            getAutoFocusPictureMode() );
        captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,
            getAutoExposureFlashMode() );
        captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE,
                getAutoWhiteBalanceMode() );

        // Orientation
        mJpegOrientation = getJpegOrientation();
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, mJpegOrientation);

    if(isManualExposure ) {
            captureBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_MANUAL);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,
            CaptureRequest.CONTROL_AE_MODE_OFF );
            captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, BurstParam.getExposureTime() );
            captureBuilder.set(CaptureRequest.LENS_APERTURE,  getAperture() );
            captureBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON);
            captureBuilder.set(CaptureRequest.STATISTICS_LENS_SHADING_MAP_MODE, CaptureRequest.STATISTICS_LENS_SHADING_MAP_MODE_ON);

    }

    return captureBuilder;

} //createStillCaptureRequestBuilder



/**
 * getImageReaderSurface
 */
private Surface getImageReaderSurface() {

            Surface surface = null;
            if ( USE_JPEG && ( mImageReaderFormat == BurstParam.FORMAT_JPEG )) {
                surface = mImageReaderJpeg.getSurface();
            } else if(USE_YUV &&( mImageReaderFormat == BurstParam.FORMAT_YUV )) {
                surface = mImageReaderYuv.getSurface();
            } else if(USE_RAW &&(mImageReaderFormat == BurstParam.FORMAT_RAW )) {
                surface = mImageReaderRaw.getSurface();
            }
        return surface;
}


/**
 * getAperture
 */ 
private float getAperture() {

    float aperture = 0;
    if( mApertures.length > 0) {
        aperture = mApertures[0];
    }
    return aperture;
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
        int orientation = (ORIENTATIONS.get(mDisplayRotation) + mSensorOrientation + 270) % 360;
        log_d("getJpegOrientation: " + orientation);
        return orientation;
} // getJpegOrientation




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
                    cameraCaptureSession.setRepeatingRequest(
                    mPreviewRequestBuilder.build(),
                    mCaptureCallback, 
                    mBackgroundHandler);
        } catch (CameraAccessException | IllegalStateException e) {
                                    e.printStackTrace();
        }


} // procCaptureSessionConfigured



    /**
     * Configure the given {@link CaptureRequest.Builder} to use auto-focus, auto-exposure, and
     * auto-white-balance controls if available.
     */
private void  setup3AControlsLocked(CaptureRequest.Builder builder) {
        // Enable auto-magical 3A run by camera device
        builder.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO);


            // If there is a "continuous picture" mode available, use it, otherwise default to AUTO.
                if(isAutoFocusContinuousPictureSupported) {
                    builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                } else if (isAutoFocusSupported) {
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

                    String tag = (String)request.getTag();
                    log_d("onCaptureCompleted: " + tag );
                    mResultList.add(result);
                    unlockFocus();
        }
        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                        log_d("onCaptureFailed");
    }

}; // CameraCaptureSession.CaptureCallback


/**
  * ImageJpegListener
 */
private ImageReader.OnImageAvailableListener mImageJpegListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            log_d("Jpeg onImageAvailable");
            procImageJpegAvailable(reader);
        }

    }; // ImageReader.OnImageAvailableListener


/**
  * procImageJpegAvailable
 */
private void procImageJpegAvailable(ImageReader reader) {

            log_d("procImageJpegAvailable");
            Image  image = null;
            try {
                image = reader.acquireNextImage();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if ( image == null) return;

            File file = getJpegFile(mJpegCount, mCurrentDate,  isUseStorage );
            saveJpegImage(image, file, isUseStorage );

            if(mPictureCallback != null) {
                    mPictureCallback.onPictureTaken(file);
            }

             image.close();
            mJpegCount ++;
} // procImageJpegAvailable


/**
  * getJpegFile
 */
private File getJpegFile(int count, Date date, boolean use_storage ) {
        String dateTime = FileUtil.getStringDateTime( date );
        File file = ImageUtil.getBurstOutputFile(mContext, dateTime, count, use_storage );
        return file;
}


/**
  * saveJpegImage
 */
private void saveJpegImage(Image image, File file, boolean use_storage ) {
        ImageUtil.saveImageAsJpeg(image, file);
        if(use_storage ) {
                MediaScanner.scanFile(mContext , file);
        }
}


/**
  * ImageYuvListener
 */
private ImageReader.OnImageAvailableListener mImageYuvListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            log_d("Yuv onImageAvailable");
            procImageYuvAvailable(reader);
        }

    }; // ImageReader.OnImageAvailableListener



/**
  * procImageYuvAvailable
 */
private void procImageYuvAvailable(ImageReader reader) {

            log_d("procImageYuvAvailable");
            Image  image = null;
            try {
                image = reader.acquireNextImage();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if ( image == null) return;

            if (isSaveTogether) {
                    callbackYuvlist(image);
            } else {
                    saveAndCallbackYuv(image);
            }

            image.close();
            mYuvCount ++;

} // procImageYuvAvailable


/**
 * callbackYuvlist
  */
private void callbackYuvlist(Image image) {

            // convert to YuvImage
            YuvImage yuvImsge = YuvImageUtil.convYuv420ToYuvImage( image);
            if(yuvImsge != null) {
                    mYuvImageList.add(yuvImsge);
            }

            int size = mYuvImageList.size();
            if ( size >= mMaxBurst ) {
                if(mPictureCallback != null) {
                    // callback, when captureBurst is completed
                    mPictureCallback.onYuvBurstTaken(mYuvImageList, mJpegOrientation);
                }
            }
}

/**
 * saveAndCallbackYuv
  */
private void saveAndCallbackYuv(Image image) {

            File file = getYuvFile(mYuvCount, mCurrentDate, isUseStorage);
            boolean ret = saveYuvImage(image, file, mJpegOrientation, isUseStorage);

            if (ret && (mPictureCallback != null)) {
                    mPictureCallback.onPictureTaken(file);
            }
}


/**
 * getYuvFile
  */
private File getYuvFile(int count, Date date, boolean use_storage) {
        String dateTime = FileUtil.getStringDateTime( date );
        File file = YuvImageUtil.getBurstOutputFile(mContext, dateTime, count, use_storage );
        return file;
}


/**
 * saveYuvImage
  */
private boolean saveYuvImage(Image image, File file, int jpegOrientation, boolean use_storage) {

        boolean ret = YuvImageUtil.saveImageAsJpeg(image, jpegOrientation, file);
        if( ret && use_storage ) {
                MediaScanner.scanFile(mContext , file);
        }
        return ret;
}


/**
 * ImageRawListener
  */
    private final ImageReader.OnImageAvailableListener mImageRawListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            log_d("Raw onImageAvailable");
            procImageAvailableRaw(reader);
        }

    }; // ImageRawListener


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

        File file = getRawFile(mCurrentDate, mRawCount, isUseStorage);

        CaptureResult result = getResultFromList(mRawCount);
        saveRawImage( image, mCharacteristics, result, file,  isUseStorage);

        if(mPictureCallback != null) {
                    mPictureCallback.onPictureTaken(file);
        }

        image.close();
        mRawCount ++;

} // procImageAvailableRaw

/**
 * getRawFile
 */
private File getRawFile(Date date, int count, boolean use_storage) {
        String dateTime = FileUtil.getStringDateTime(date );
        File file = RawUtil.getBurstOutputFile(mContext, dateTime, count, use_storage );
        return file;
}


/**
 * saveRawImage
 */
private void saveRawImage(Image image, CameraCharacteristics characteristics, CaptureResult result, File file, boolean use_storage) {
        RawUtil.saveImageAsDng(image,characteristics, result,   file);

        if (use_storage) {
                MediaScanner.scanFile(mContext, file);
        }
}


/**
 * getResultFromList
 */
private CaptureResult getResultFromList(int index) {

    CaptureResult result = null;
    int size = mResultList.size();
    if (index <= size) {
                // specified
               result = mResultList.get(index);
    } else {
                // last one
               result = mResultList.get(size);
    }
    return result;
}


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
