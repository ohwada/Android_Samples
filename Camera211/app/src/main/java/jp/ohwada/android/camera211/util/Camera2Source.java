/**
 * Camera2 Sample
 *  AutoFocus
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera211.util;

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
import android.os.Build;
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

    // image reader
    private final static int IMAGE_READER_STILL_MAX_IMAGES = 2;


    // auto focus
    private final static int AF_TOUCH_WIDTH = 300;
    private final static int AF_TOUCH_HEIGHT = 300;
    private final static String AF_TAG = "FOCUS_TAG";
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
     * Callback interface used to notify on completion of camera auto focus.
     */
    public interface AutoFocusCallback {
        /**
         * Called when the camera auto focus completes.  If the camera
         * does not support auto-focus and autoFocus is called,
         * onAutoFocus will be called immediately with a fake value of
         * <code>success</code> set to <code>true</code>.
         * <p/>
         * The auto-focus routine does not lock auto-exposure and auto-white
         * balance after it completes.
         *
         * @param success true if focus was successful, false if otherwise
         */
        void onAutoFocus(boolean success);
    } // interface AutoFocusCallback



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
     * callback used to notify on completion of camera auto focus.
     */
    private AutoFocusCallback mAutoFocusCallback;


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
     * Initiate a still image capture. The camera preview is suspended
     * while the picture is being taken, but will resume once picture taking is done.
     */
    public void takePicture(PictureCallback picCallback) {
        log_d("takePicture");
        mPictureCallback = picCallback;
        lockFocus();
} // takePicture


/**
 * autoFocus
 */
    public void autoFocus(AutoFocusCallback cb, float screenX, float screenY, int screenW, int screenH) {
        if(cb != null) {
            mAutoFocusCallback = cb;
        }

        MeteringRectangle focusAreaTouch = createAutoFocusAreaRect(screenX, screenY, screenW,  screenH);
        MeteringRectangle[] af_regions = new MeteringRectangle[]{focusAreaTouch};

        stopRepeating();
        cancelAutoFocusTrigger();
        captureAutoFocus(af_regions);   

} // autoFocus


/**
 * stopRepeating
 */
private void stopRepeating() {
            try {
                mCaptureSession.stopRepeating(); 
            } catch(CameraAccessException ex) {
                ex.printStackTrace();
            }
} // stopRepeating


/**
 * cancelAutoFocusTrigger
 * cancel any existing AF trigger (repeated touches, etc.)
 */
private void cancelAutoFocusTrigger() {
    try {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
                mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            } catch(CameraAccessException ex) {
                ex.printStackTrace();
            }
} // cancelAfTrigger


/**
 * captureAutoFocus
 * we'll capture this later for resuming the preview
 * then we ask for a single request (not repeating)
 */
private void captureAutoFocus(MeteringRectangle[] af_regions) {
    if(af_regions == null) return;
    try {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
                mPreviewRequestBuilder.setTag(AF_TAG); 
                //Now add a new AF trigger with focus region
                if(isMeteringAreaAFSupported) {
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, af_regions);
                }
                mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            } catch(CameraAccessException ex) {
                ex.printStackTrace();
            }
} // captureAutoFocus


/**
 * createAutoFocusAreaRect
 */
private MeteringRectangle createAutoFocusAreaRect(float screenX, float screenY, int screenW, int screenH) {
        if(mSensorArraySize == null) return null;

            // xy coordinates of camera sensor 
            Point point = convAutoFocusTouch(screenX, screenY, screenW, screenH);
            int sensor_x =  point.x;
            int sensor_y =  point.x;

            // origin(lef, right) of rectangle
            int touch_x = (int)(sensor_x - AF_TOUCH_WIDTH/2);
            int touch_y = (int)(sensor_y - AF_TOUCH_HEIGHT/2);
            int orig_x = Math.max(touch_x, 0);
            int orig_y = Math.max(touch_y , 0);

            int metering_weight = MeteringRectangle.METERING_WEIGHT_MAX - 1;
            MeteringRectangle focusAreaTouch = new MeteringRectangle(
                    orig_x,
                    orig_y,
                    AF_TOUCH_WIDTH,
                    AF_TOUCH_HEIGHT,
                   metering_weight);
        prev_log("FocusAreaTouch= " + focusAreaTouch.toString());
        return focusAreaTouch;
}


/**
 * convert xy coordinates of viewto xy coordinates of sensor 
 */ 
private Point convAutoFocusTouch(float screenX, float screenY, int screenW, int screenH) {
        if(mSensorArraySize == null) return null;

        float ratio_x = (float)screenX / (float)screenW;
        float ratio_y = (float)screenY / (float)screenH;
        int sensorWidth = mSensorArraySize.width();
        int sensorHeight = mSensorArraySize.height();
        int new_x =    (int)(ratio_x  * sensorWidth);
        int new_y =   (int)(ratio_y  * sensorHeight);

        if((mDisplayRotation == Surface.ROTATION_0)&&(mSensorOrientation == 90)) {
            // Nexus5 back camera
            new_x =  (int)(ratio_y  * sensorWidth);
            new_y =  (int)((1-ratio_x)  * sensorHeight);
        }
        Point point = new Point(new_x, new_y);
        return point;
}


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
} // setUpExtend



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
            if(request.getTag() == AF_TAG) {
                //The focus trigger is complete!
                log_d("The focus trigger is complete!");
                // callback
                if(mAutoFocusCallback != null) {
                        mAutoFocusCallback.onAutoFocus(true);
                }
                //Resume repeating request, clear AF trigger.
                clearAutoFocusTrigger();
            } else {
                procCaptureResult(result);
            }
} // procCaptureCompleted


/**
 * resume repeating request, clear AF trigger
 */
private void clearAutoFocusTrigger() {
                mAutoFocusCallback.onAutoFocus(true);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
                mPreviewRequestBuilder.setTag(NONE_TAG);
                mPreviewRequest = mPreviewRequestBuilder.build();
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                } catch(CameraAccessException ex) {
                    ex.printStackTrace();
                }
} // clearAutoFocusTrigge


/**
 * procCaptureFailed
 */ 
@Override
protected void procCaptureFailed( CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            if(request.getTag() == AF_TAG) {
                log_d("Manual AF failure: "+ failure.getReason());
                if(mAutoFocusCallback != null) {
                        mAutoFocusCallback.onAutoFocus(false);
                }
            }
} // procCaptureFailed


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
        log_d("captureStillPicture: State = " + mState);
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
