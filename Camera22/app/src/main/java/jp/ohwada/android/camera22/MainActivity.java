/**
 * Camera2 Sample
 * take picture
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera22;

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
 *  class MainActivity
 * original : https://github.com/googlesamples/android-Camera2Basic/tree/master/Application/src/main/java/com/example/android/camera2basic
  */
public class MainActivity extends PreviewActivity {

        // debug
   	private final static String TAG_SUB = "MainActivity";

    // output file
    private static final String FILE_PREFIX = "camera_";
    private static final String FILE_EXT = ".jpg";


    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;


    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
     private File mFile;




/**
 * createExtend
 */
    @Override
protected void createExtend() {

    Button btnPicture =
            (Button)findViewById(R.id.Button_picture);
        btnPicture.setOnClickListener( new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        takePicture();
    }
    }); // btnPicture

}  // createExtend


/**
 * openCameraExtend
 */
    @Override
protected void openCameraExtend() {
    // mFile = createPictureFile();  
} // openCameraExtend


/**
 * createPictureFile
 */
private File createPictureFile() {
    return getAppOutputFile(FILE_PREFIX, FILE_EXT);
} // createPictureFile


/**
 * closeCameraExtend
 */
    @Override
protected void closeCameraExtend() {
    log_d("closeCameraExtend");
    if (null != mImageReader) {
            mImageReader.close();
               mImageReader = null;
    }
} // closeCameraExtend

/**
 * Initiate a still image capture.
 */
private void takePicture() {
    log_d("takePicture");
    mFile = createPictureFile();  
    lockFocus();
} // takePicture


 /**
  * Lock the focus as the first step for a still image capture.
  */
private void lockFocus() {

    log_d("lockFocus");
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            synchronized (mCameraStateLock) {
                    mState = STATE_WAITING_LOCK;
            }
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } // try
} // lockFocus

/**
 * Run the precapture sequence for capturing a still image. This method should be called when
  * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
 */
private void runPrecaptureSequence() {
    log_d("runPrecaptureSequence");
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            synchronized (mCameraStateLock) {
                    mState = STATE_WAITING_PRECAPTURE;
            }
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } // try

} // runPrecaptureSequence


 /**
 * Capture a still picture. This method should be called when we get a response in
  * {@link #mCaptureCallback} from both {@link #lockFocus()}.
  */
private void captureStillPicture() {
    log_d("captureStillPicture");
    if ( null == mCameraDevice) {
                return;
    }

       try {
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));
            CameraCaptureSession.CaptureCallback captureCallback = createCaptureCallback();
            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
        CaptureRequest request = captureBuilder.build();
            mCaptureSession.capture(request, captureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } // try
} // captureStillPicture


 /**
 *  createCaptureCallback
  */
private CameraCaptureSession.CaptureCallback             createCaptureCallback() {
    log_d("createCaptureCallback");
    CameraCaptureSession.CaptureCallback callback
                    = new CameraCaptureSession.CaptureCallback() {

@Override
public void onCaptureCompleted( CameraCaptureSession session,
                                                CaptureRequest request,
                                                TotalCaptureResult result) {
            log_d("onCaptureCompleted");
            String msg = "Saved: " + mFile.toString();
            log_d(msg);
            unlockFocus();
            showToast_onUI(msg);
                }
            }; // CameraCaptureSession.CaptureCallback
return callback;
} // createCaptureCallback


/**
  * Unlock the focus. This method should be called when still image capture sequence is
  * finished.
 */
private void unlockFocus() {
    log_d("unlockFocus");
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } // try
} // unlockFocus


/**
  * setAutoFlash
  */
private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
    log_d("setAutoFlash");
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
} // setAutoFlash


/**
 *createCameraPreviewSession
 */
@Override
protected void createCameraPreviewSession() {

    log_d("createCameraPreviewSession");
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
    List outputs = Arrays.asList(surface, mImageReader.getSurface());       
    CameraCaptureSession.StateCallback callback = createCameraCaptureSessionStateCallback();
    mCaptureCallback = createCameraCaptureSessionCaptureCallback();
        mCameraDevice.createCaptureSession(outputs,callback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO : SecurityException
            e.printStackTrace();
        }

} // createCameraPreviewSession


/**
  * procCaptureConfigured
  */
@Override
protected void procCaptureConfigured(CameraCaptureSession cameraCaptureSession) {
    log_d("procCaptureConfigured");
     // The camera is already closed
    if (null == mCameraDevice) {
                                return;
    }

     // When the session is ready, we start displaying the preview.
    mCaptureSession = cameraCaptureSession;
    try {
        // Auto focus should be continuous for camera preview.
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        // Flash is automatically enabled when necessary.
        setAutoFlash(mPreviewRequestBuilder);
        // Finally, we start displaying the camera preview.
        mPreviewRequest = mPreviewRequestBuilder.build();
        mCaptureSession.setRepeatingRequest(mPreviewRequest,
            mCaptureCallback, mBackgroundHandler);
    } catch (CameraAccessException e) {
        e.printStackTrace();
    }

} // procCaptureConfigured


/**
  * setUpExtendOutputs
  * override for extend
 */
@Override
protected void setUpExtendOutputs() {

        StreamConfigurationMap map = null;
        try {
            map = mCharacteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (map == null) {
                log_d("map = null");
                    return;
        }
                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());

        mImageReader = ImageReader.newInstance(
        largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
    mImageReader.setOnImageAvailableListener(
            mOnImageAvailableListener, mBackgroundHandler);

} // setUpExtendOutputs


    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback  callback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
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
        }

        @Override
        public void onCaptureProgressed( CameraCaptureSession session,
                                         CaptureRequest request,
                                         CaptureResult partialResult) {
    //log_d("onCaptureProgressed");
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted( CameraCaptureSession session,
                                        CaptureRequest request,
                                       TotalCaptureResult result) {
    // log_d("onCaptureCompleted");
            process(result);
        }

}; // CameraCaptureSession.CaptureCallback


/**
  * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
  * still image is ready to be saved.
 */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener () {
        @Override
        public void onImageAvailable(ImageReader reader) {
    log_d("onImageAvailable");
             mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
        }
}; // ImageReader.OnImageAvailableListener


/**
  * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
 */
private CameraCaptureSession.CaptureCallback  createCameraCaptureSessionCaptureCallback() {
    log_d("createCameraCaptureSessionCaptureCallback");
CameraCaptureSession.CaptureCallback Callback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
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
                        synchronized (mCameraStateLock) {
                                mState = STATE_WAITING_NON_PRECAPTURE;
                        }
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        synchronized (mCameraStateLock) {
                                mState = STATE_PICTURE_TAKEN;
                                captureStillPicture();
                        }
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed( CameraCaptureSession session,
                                         CaptureRequest request,
                                         CaptureResult partialResult) {
    //log_d("onCaptureProgressed");
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted( CameraCaptureSession session,
                                        CaptureRequest request,
                                       TotalCaptureResult result) {
    // log_d("onCaptureCompleted");
            process(result);
        }

}; // CameraCaptureSession.CaptureCallback

return callback;

} // createCameraCaptureSessionCaptureCallback


/**
 * write into logcat
 */ 
private void log_d( int res_id ) {
   log_base(  TAG_SUB, res_id );
} // log_d

/**
 * write into logcat
 */ 
private void log_d( String msg ) {
   log_base(  TAG_SUB, msg );
} // log_d

} // class MainActivity
