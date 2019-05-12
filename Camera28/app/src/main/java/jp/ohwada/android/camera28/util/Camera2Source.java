/**
 * Camera2 Sample
 * Face Detection
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera28.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


 /**
  *  class Camera2Source
 * Face Detection
 * similar to CameraSource of Vision API
 * original : https://github.com/EzequielAdrianM/Camera2Vision
  */
public class Camera2Source extends Camera2Base {

    // debug
    private final static String TAG_SUB = "Camera2Source";

    // face detect mode
// Value: 0
public final static int FACE_DETECT_MODE_OFF =CameraMetadata.STATISTICS_FACE_DETECT_MODE_OFF;
// Value: 1
public final static int FACE_DETECT_MODE_SIMPLE =CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE;
// Value: 2
public final static int FACE_DETECT_MODE_FULL =CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL;

 
    /**
     * FaceDetectCallback
     */
    public interface FaceDetectCallback {
        /**
         * call when detect Face
         */
        public void onDetect(Face[] fsces);
} // FaceDetectCallback


    // face detect 
    private int mFaceDetectMode = 0;
    private boolean isFaceDetectRunning = false;

    private FaceDetectCallback  mFaceDetectCallback;







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
  * startFaceDetect
  */
public void startFaceDetect(FaceDetectCallback callback) {
    log_d("startFaceDetect");
    log_d(" FaceDetectMode= " +  mFaceDetectMode);
    mFaceDetectCallback = callback;
    isFaceDetectRunning = true;
    startFaceDetectCapture();
} // startFaceDetect


/**
  * stopFaceDetect
  */
public void stopFaceDetect() {
    log_d("stopFaceDetect");
    isFaceDetectRunning = false;
    stopFaceDetectCapture();
} // stopFaceDetect


/**
  * startFaceDetectCapture
  */
private void startFaceDetectCapture() {
    log_d("startFaceDetectCapture");
    if(mPreviewRequestBuilder == null) {
        log_d("mPreviewRequestBuilder == null");
        return;
    }

    try {
        // Auto focus should be continuous for camera preview.
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

        // set face detect mode
        mPreviewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,  mFaceDetectMode );

        // Finally, we start displaying the camera preview.
        mPreviewRequest = mPreviewRequestBuilder.build();
        mCaptureSession.setRepeatingRequest(mPreviewRequest,
           mCaptureCallback, mBackgroundHandler);
    } catch (CameraAccessException e) {
        e.printStackTrace();
    }

} // startFaceDetectCapture



/**
 * stopFaceDetectCapture
 */ 
private void stopFaceDetectCapture() {
    log_d("stopFaceDetectCapture");
        try {
            // set auto focus off
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_OFF);
        // set face detect off
        mPreviewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,  FACE_DETECT_MODE_OFF );
        mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } // try
} // stopFaceDetectCapture


/**
 * setUpExtend
 */
@Override
protected void setUpExtend(CameraCharacteristics characteristics) {
        mFaceDetectMode = getAvailableFaceDetectMode(characteristics);
} // setUpExtend


/**
 * getAvailableFaceDetectMode
 */
private int getAvailableFaceDetectMode(CameraCharacteristics characteristics) {
 
    int ret = FACE_DETECT_MODE_OFF;
    int[] modes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
        int len = modes.length;
        log_d("mode length= " + len);
    for(int i=0; i<len; i++ ) {
        int mode = modes[i];
        //log_d("i= " + i +" mode= " + mode);
        if( mode == CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE ) {
                ret =  FACE_DETECT_MODE_SIMPLE;
                log_d("FACE_DETECT_MODE_SIMPLE");
        } else if (mode == CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_FULL ) {
                ret =  CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL;
                log_d("FACE_DETECT_MODE_FULL");
        }

    } // for

    if(  ret ==FACE_DETECT_MODE_OFF ) {
           notifyError("NOT support FACE DETECT");
    }
    return ret;
} // checkAvailableFaceDetectMode


/**
 * procCaptureResult
 */ 
@Override
protected void procCaptureResult(CaptureResult result) {
        //log_d("procCaptureResult");
        procCaptureResultFaceDetect(result);
} // procCaptureResult


/**
 * procCaptureResult
 */ 
private void procCaptureResultFaceDetect(CaptureResult result) {
    if( !isFaceDetectRunning ) return;
            if(result == null) return;
        int mode = 0;
        Face[] faces = null;
    try {
            if(result != null) {
                    mode = result.get(CaptureResult.STATISTICS_FACE_DETECT_MODE);
                    faces = result.get(CaptureResult.STATISTICS_FACES);
            }
    } catch (Exception e) {
            e.printStackTrace();
    }
    if (faces == null ) return;
    if(mFaceDetectCallback != null) {
            mFaceDetectCallback.onDetect(faces);
    }
} // procCaptureResult


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d




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
         * Sets the camera to use
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
