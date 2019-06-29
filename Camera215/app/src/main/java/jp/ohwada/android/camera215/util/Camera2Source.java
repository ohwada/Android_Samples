/**
 * Camera2 Sample
 *  Zoom
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera215.util;


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

import java.util.Arrays;
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
 * Minimum valid value of Zoom
 */ 
   public static final  float MIN_VALID_ZOOM = 1.0f;


/**
 * threshold value whether to zoom in or out
 * zoom, when larger than this value
 * use original full size, when smaller than this value
 */ 
    private static final  float ZOOM_THRESHOLD = 1.0f;


    // image reader
    private final static int IMAGE_READER_STILL_MAX_IMAGES = 2;


    /**
     * Capture Request Tag for Zoom
     */
    private final static String ZOOM_TAG = "ZOOM_TAG";

    private final static String NONE_TAG = "";


    /**
     * Callback interface used to supply image data from a photo capture.
     */
    public interface PictureCallback {
        /**
         * Called when image data is available after a picture is taken.  The format of the data
         * is a JPEG Image.
         */
        void onPictureTaken(Image image);
    }


    /**
     * Callback interface used to notify on completion of camera zoom.
     */
    public interface ZoomCallback {
        void onZoom(boolean success);
    }



   /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReaderStill;

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
     * callback used to notify on completion of camera zoom.
     */
    private ZoomCallback mZoomCallback;


    /**
     * The maximum ratio between both active area width and crop region width, and active area height and crop region height, for CaptureRequest#SCALER_CROP_REGION.
     */
    private float mMaxDigitalZoom = 0;


    /**
     * The desired region of the sensor to read out for this capture.
     */
    private Rect mZoomArea;


    /**
     * flag for stil picture zoom
     */
      private boolean  isPictureZoom = false;


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
        } catch (Exception e) {
                e.printStackTrace();
        }
} // stopExtend


/**
 * getMaxDigitalZoom
 */
    public float getMaxDigitalZoom() {
            return mMaxDigitalZoom;
    } // getMaxDigitalZoom


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
 * takePicture with zoom
 */
    public void takePicture(boolean isZoom, PictureCallback picCallback) {
        log_d("takePicture");
        isPictureZoom = isZoom;
        mPictureCallback = picCallback;
        lockFocus();
} // takePicture


/**
 * doZoom
 */
    public void doZoom(float zoomScale, ZoomCallback cb) {
        if(cb != null) {
            mZoomCallback = cb;
        }

        if(mSensorArraySize == null) return;
        if(mCaptureSession == null) return;

        mZoomArea = createZoomArea(zoomScale);

            try {
                mCaptureSession.stopRepeating();
                // new request
                mPreviewRequestBuilder.setTag(ZOOM_TAG); 
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, mFocusMode);
                if(isFlashSupported) {
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, mFlashMode);
                }

                mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, mZoomArea);

                //we'll capture this later for resuming the preview!
                //Then we ask for a single request (not repeating!)
                mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            } catch(CameraAccessException ex) {
                ex.printStackTrace();
            }

} // doZoom




/**
 * createZoomArea
 */
private Rect createZoomArea(float zoomScale) {
        if(mSensorArraySize == null) return null;

        float zoom = 0;
       if ( zoomScale > mMaxDigitalZoom) {
            // limit by　maximum value, if  exceeded
            zoom = mMaxDigitalZoom;
        } else if ( zoomScale > ZOOM_THRESHOLD){
            // set zoom, when larger than threshold
            zoom = zoomScale;
        } else {
            // return original sensor size, when smaller than threshold
            return mSensorArraySize;
        }

        // sensor size
        int sensor_width = mSensorArraySize.width();
        int sensor_height = mSensorArraySize.height();
        int center_x = mSensorArraySize.centerX();
        int center_y =  mSensorArraySize.centerY();

        // zoom size
        int zoom_width = (int)((float)sensor_width / zoom);
        int zoom_height = (int)((float)sensor_height / zoom);

        // rectangle
        int right = center_x - zoom_width/2;
        int left = center_x + zoom_width/2;
        int top = center_y - zoom_height/2;
        int bottom = center_y + zoom_height/2;

        Rect rect = new Rect(right, top, left, bottom);
        log_d("createZoomArea: " + zoomScale + " , " + rect.toString());
        return rect;
} // createZoomArea



/**
 * setUpExtend
 */
@Override
protected void setUpExtend(CameraCharacteristics characteristics) {
    log_d("setUpExtend");
    if(mLargestSize == null) {
        log_d("LargestSize  null");
    }
    int w = mLargestSize.getWidth();
    int h = mLargestSize.getHeight();
    mImageReaderStill = ImageReader.newInstance(w, h, ImageFormat.JPEG, IMAGE_READER_STILL_MAX_IMAGES);
        mImageReaderStill.setOnImageAvailableListener( mOnImageAvailableListener, mBackgroundHandler);

    checkScalerCroppingType(characteristics);
} // setUpExtend


/**
   * checkScalerCroppingType
  */
private void checkScalerCroppingType(CameraCharacteristics characteristics) {

   mMaxDigitalZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
    log_d("MaxDigitalZoom= " +  mMaxDigitalZoom);

    if(mMaxDigitalZoom < MIN_VALID_ZOOM) {
        String msg = "MaxDigitalZoom= " + mMaxDigitalZoom + LF + "NOT support Zoom";
        notifyError(msg);
    }

   int type = characteristics.get(CameraCharacteristics.SCALER_CROPPING_TYPE);
    log_d("Scaler Cropping Type = " +  type);

    if ((type != CameraMetadata.SCALER_CROPPING_TYPE_CENTER_ONLY)&&
(type != CameraMetadata.SCALER_CROPPING_TYPE_FREEFORM)) {
        notifyError("undefined Zoom Type");
    }

} // checkScalerCroppingType


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
                List outputs = Arrays.asList(mViewSurface, mImageReaderStill.getSurface() );
                mCameraDevice.createCaptureSession(outputs, mPreviewSession, mBackgroundHandler);
        } catch (Exception e) {
                        e.printStackTrace();
        }
} //  createCameraPreviewSession


/**
  * procCaptureCompleted
 */
@Override
protected void procCaptureCompleted( CameraCaptureSession session,
                                        CaptureRequest request,
                                       TotalCaptureResult result) {
            //log_d("procCaptureCompleted");
            String tag= (String)request.getTag();
            if(ZOOM_TAG.equals(tag)) {
                procZoomCaptureCompleted( session, request, result);
            } else {
                procCaptureResult(result);
            }
} // procCaptureCompleted


/**
 * procZoomCaptureCompleted
 */
private void procZoomCaptureCompleted( CameraCaptureSession session,
                                        CaptureRequest request,
                                       TotalCaptureResult result) {

                log_d("Zoom is complete!");
                // callback
                if(mZoomCallback != null) {
                        mZoomCallback.onZoom(true);
                }
                resumeRepeatingRequest();
} // procZoomCaptureCompleted


/**
 * resume repeating request
 */
private void resumeRepeatingRequest() {

                try {
                    mPreviewRequestBuilder.setTag(NONE_TAG);
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, mFocusMode);
                    if(isFlashSupported) {
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, mFlashMode);
                    }
                    mPreviewRequest = mPreviewRequestBuilder.build();
                    mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                } catch(CameraAccessException ex) {
                    ex.printStackTrace();
                }
} // resumeRepeatingRequest



/**
 * procCaptureFailed
 */ 
@Override
protected void procCaptureFailed( CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
        log_d("procCaptureFailed: "+ failure.getReason());
    String tag = (String)request.getTag();
            if(ZOOM_TAG.equals(tag)) {
                procZoomCaptureFailed(  session,  request,  failure);
            }
} // procCaptureFailed



/**
 * procZoomCaptureFailed
 */ 
private void procZoomCaptureFailed( CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
        if(mZoomCallback != null) {
                        mZoomCallback.onZoom(false);
        }
} // procZoomCaptureFailed


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
     *  option : take picture with zoom
     */
    private void captureStillPicture() {
        log_d("captureStillPicture");
            if (null == mCameraDevice) {
        log_d("CameraDevice null");
                return;
            }

        try {
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReaderStill.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, mFocusMode);
            if(isFlashSupported) {
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, mFlashMode);
            }


            //　specify the zoom area, when take picture with zoom
            if(isPictureZoom && (mZoomArea != null)) {
                    captureBuilder.set(CaptureRequest.SCALER_CROP_REGION, mZoomArea);
            }

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
                        unlockFocus();
                }
}; // CameraCaptureSession.CaptureCallback



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
                mPictureCallback.onPictureTaken(image);
            }
            image.close();
} // procImageAvailable

 
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
