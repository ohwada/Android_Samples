/**
 * Camera2 Sample
 * Face Detection
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera26;

import android.graphics.Rect;
import android.media.FaceDetector;
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
import android.hardware.camera2.params.Face;

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

import android.hardware.Camera.CameraInfo;


 /**
 *  class MainActivity
 * original : https://github.com/googlesamples/android-Camera2Basic/tree/master/Application/src/main/java/com/example/android/camera2basic
  */
public class MainActivity extends PreviewActivity {

        // debug
   	private final static String TAG_SUB = "MainActivity";

    // face detect
    private final static int SCORE_THRESHOLD = Face.SCORE_MAX / 2;

    // face detect 
    private int mFaceDetectMode = 0;
    private boolean isFaceDetectRunning = false;


/**
 * createExtend
 */
@Override
protected void createExtend() {

    Button btnDetect =
            (Button)findViewById(R.id.Button_detect);
        btnDetect.setOnClickListener( new View.OnClickListener() {
    @Override
    public void onClick(View view) {
       detectFace();
    }
    }); // btnDetect

} // createExtend



 /**
 * detectFace
 */
private void detectFace() {
    if(isFaceDetectRunning) {
        isFaceDetectRunning = false;
        stopFaceDetect();
        toast_long("stop detect");
    } else {
        isFaceDetectRunning = true;
        boolean ret = startFaceDetect();
        if(ret) {
            toast_long("start detect");
        } else {
            toast_long("cannot start detect");
        }
    }
} // detectFace


/**
  * startFaceDetect
  */
private boolean startFaceDetect() {
    log_d("startFaceDetect");
    if(mPreviewRequestBuilder == null) {
        log_d("mPreviewRequestBuilder == null");
        return false;
    }
    boolean is_error = false;
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
        is_error = true;
        e.printStackTrace();
    }

    return !is_error;
} // startFaceDetect


/**
 * stopFaceDetect
 */ 
private void stopFaceDetect() {
    log_d("stopFaceDetect");
        try {
            // set auto focus off
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_OFF);
        // set face detect off
        mPreviewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,  CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_OFF );
        mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } // try
} // stopFaceDetect






/**
 * setUpCameraOutputs
 */

protected void setUpCameraOutputs(int width, int height) {
        log_d("setUpCameraOutputs");
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    if(manager == null) {
        showErrorDialog(R.string.msg_not_support);
        return;
    }

        CameraCharacteristics characteristics = null;
        StreamConfigurationMap  map = null;
        try {
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics c
                        = manager.getCameraCharacteristics(id);
                Integer facing = c.get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    mCameraId = id;
                    characteristics = c;
                    map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                   break;
                }
            } // for
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    // For still image captures, we use the largest available size.
    Size largest = getLargestSize(map);

            //noinspection ConstantConditions
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        mPreviewSize = getPreviewSize(map, width, height, mSensorOrientation);

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            fitTextureView(mPreviewSize);

            // Check if the flash is supported.
            mFlashSupported = hasFlashSupport(characteristics);
            // front camera has no flush
            mFlashSupported =  false;

        checkAvailableFaceDetectMode(characteristics);

} // setUpCameraOutputs


/**
 * checkAvailableFaceDetectMode
 */
private void checkAvailableFaceDetectMode(CameraCharacteristics characteristics) {
        log_d("checkAvailableFaceDetectMode");

    mFaceDetectMode = 0;
    int[] modes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
    for(int i=0; i<modes.length; i++ ) {
        int mode = modes[i];
        if( mode == CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE ) {
                mFaceDetectMode =  CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE;
        } else if (mode == CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_FULL ) {
                mFaceDetectMode =  CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL;
        }

    } // for

    if(  mFaceDetectMode ==0 ) {
            log_d(R.string.msg_not_support_face_detect);
            showErrorDialog(R.string.msg_not_support_face_detect);
    }

} // checkAvailableFaceDetectMode


/**
 * procCaptureResult
 */ 
@Override
protected void procCaptureResult(CaptureResult result) {
    // log_d("procCaptureResult");
    if( !isFaceDetectRunning ) return;
            Integer mode = result.get(CaptureResult.STATISTICS_FACE_DETECT_MODE);
            Face[] faces = result.get(CaptureResult.STATISTICS_FACES);
        int length = 0;
            if(faces != null && mode != null) {
                    length = faces.length;
                    // log_d( "faces : " +length + " , mode : " + mode);
            }

    // not detect
    if (length == 0) return;

    for(int i=0; i<length;  i++ ) {
        Face face = faces[i];
        if (face != null ) {
            log_d( face.toString() );
        }
    } // for

    showToast_onUI("detect face: " + length);

} // procCaptureResult




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
