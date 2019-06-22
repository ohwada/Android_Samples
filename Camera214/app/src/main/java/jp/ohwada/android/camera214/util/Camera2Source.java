/**
 * Camera2 Sample
 * take Picture with RAW mode 
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera214.util;

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
 * Flag whether to support AutoWhiteBalnce
 * CameraCharacteristics#CONTROL_AWB_MODE_AUTO
  */
    private boolean isAutoWhiteBalnceSupported = false;


/**
 * Flag whether to support AutoFocusContinuousPicture
 * CameraCharacteristics#
 * CONTROL_AF_MODE_CONTINUOUS_PICTURE
  */
    private boolean isAutoFocusContinuousPictureSupported = false;



/**
 * Flag whether to support AutoFlash
 * CameraCharacteristics#
 * CONTROL_AE_MODE_ON_AUTO_FLASH
  */
    private boolean  isAutoFlashSupported = false;


/**
 * Minimum Focus Distance
 * CameraCharacteristics#
 * LENS_INFO_MINIMUM_FOCUS_DISTANCE
  */
    private Float mMinFocusDist;


/**
  * Whether  to support Auto Focus or Manual  Focus
  * fixed focus if false
 */
    private boolean isFocusSupported = false;



/**
 * Date Time when capture started
 * in order to use the same time stamp with JPEG and RAW
  */
    private Date mCurrentDate;




/**
 * CameraCharacteristics for DngCreator
  */
    private CameraCharacteristics mCharacteristics;


/**
 * CaptureResult for DngCreator
  */
    private CaptureResult mCaptureResult;


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
 * setUpExtend
 */
@Override
protected void setUpExtend(CameraCharacteristics characteristics) {
    log_d("setUpExtend");
    isRawSupported = checkRawAvailableCapabilies(characteristics);
    isAutoFocusContinuousPictureSupported = checkAutoFocusContinuousPictureAvailable(characteristics);
    isAutoFlashSupported = checkAeAutoFlashAvailable(characteristics);
    isAutoWhiteBalnceSupported = checkAwbAutoAvailable(characteristics);

    mMinFocusDist =
                characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        //  lens is fixed-focus if MinimumFocusDistance is 0
        isFocusSupported = ((mMinFocusDist == null || mMinFocusDist == 0))? false: true;

    setupImageReaderStill(characteristics);
    setupImageReaderRaw(characteristics);
} // setUpExtend


/**
  * contains
  * Return true if the given array contains the given integer.
  */
private boolean contains(int[] modes, int key_mode) {
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
   * checkAwbAutoAvailable
  */
private boolean checkAwbAutoAvailable(CameraCharacteristics characteristics) {
    int[] modes = characteristics.get(
                        CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
    return contains(modes, CameraCharacteristics.CONTROL_AWB_MODE_AUTO);
} // checkAwbAutoAvailable


/**
   * checkRawAvailableCapabilies
  */
private boolean checkRawAvailableCapabilies(CameraCharacteristics characteristics) {
     int[] modes = characteristics.get(
                                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
    return contains(modes, CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_RAW);

} //checkRawAvailableCapabilies


/**
   * checkAutoFocusContinuousPictureAvailable
  */
private boolean checkAutoFocusContinuousPictureAvailable(CameraCharacteristics characteristics) {

    int[] modes = characteristics.get(
                            CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
    return contains(modes, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

} // checkAutoFocusContinuousPictureAvailable


/**
   * checkAeAutoFlashAvailable
  */
private boolean checkAeAutoFlashAvailable(CameraCharacteristics characteristics) {

    int[] modes = characteristics.get(
                        CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
    return contains(modes, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

} // checkAeAutoFlashAvailable


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

          Size largestRaw = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.RAW_SENSOR)),
                        new CompareSizesByArea());

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
            captureBuilder.addTarget(mImageReaderStill.getSurface());
            if( mImageReaderRaw != null) {
                    captureBuilder.addTarget(mImageReaderRaw.getSurface());
            }

            // Use the same AE and AF modes as the preview.
            setup3AControlsLocked(captureBuilder);

            // Orientation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation());

            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(),  mCaptureCallbackStillPicture, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
} // captureStillPicture


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
        // Enable auto-magical 3A run by camera device
        builder.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO);


        if (isFocusSupported) {
            // If there is a "continuous picture" mode available, use it, otherwise default to AUTO.
                if(isAutoFocusContinuousPictureSupported) {
                    builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                } else {
                        builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_AUTO);
                }
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
                        //finishedCaptureLocked();
                       unlockFocus();
        }
        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            // no action
    }

}; // CameraCaptureSession.CaptureCallback



/**
 * Called after a RAW/JPEG capture has completed; resets the AF trigger state for the
 * pre-capture sequence.
  * <p/>
  * Call this only with {@link #mCameraStateLock} held.
  */
private void finishedCaptureLocked() {
        try {
                // Reset the auto-focus trigger in case AF didn't run quickly enough.
                if ( !isFocusSupported ) {
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
            File file = RawUtil.getOutputFile(mCurrentDate);
            boolean ret = RawUtil.saveImage(mCharacteristics, mCaptureResult, image, file); 
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
