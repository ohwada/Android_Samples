/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera23;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

 /**
 *  class MainActivity
 * original : https://github.com/googlesamples/android-Camera2Video
  */
public class MainActivity extends PreviewActivity {

        // debug
   	private final static String TAG_SUB = "MainActivity";


    // output file
    private static final String FILE_PREFIX = "camera_";
    private static final String FILE_EXT = ".mp4";


    // MediaRecorder
    private static final int VIDEO_BIT_RATE = 10000000;
    private static final int VIDEO_FRAME_RATE = 30;

// 3x4 aspect ratio
    private static final double VIDEO_RATIO  = 4 / 3;

// larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
    private static final int MAX_VIDEO_WIDTH  = 1080;

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;

    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

 /**
  * Button to record video
  */
    private Button mButtonVideo;


    /**
     * A reference to the current {@link android.hardware.camera2.CameraCaptureSession} for
     * preview.
     */
    private CameraCaptureSession mVideoPreviewSession;

    /**
     * The {@link android.util.Size} of video recording.
     */
    private Size mVideoSize;

    /**
     * MediaRecorder
     */
    private MediaRecorder mMediaRecorder;

    /**
     * Whether the app is recording video now
     */
    private boolean mIsRecordingVideo = false;


    // private String mNextVideoAbsolutePath;

       private File mNextVideoOutputFile;

     private CaptureRequest.Builder mVideoPreviewBuilder;



/**
 * createExtend
 */
@Override
protected void createExtend() {
    log_d("createExtend");
        mButtonVideo = (Button) findViewById(R.id.video);
        mButtonVideo.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View view) {
                    if (mIsRecordingVideo) {
                        stopRecordingVideo();
                    } else {
                        startRecordingVideo();
                    }
        }
    }); // mButtonVideo

    mCameraPerm.setPermissions( VIDEO_PERMISSIONS );

} // createExtend


/**
  * setUpExtendOutputs
  */
@Override
protected void setUpExtendOutputs() {
    log_d("setUpExtendOutputs");
           // Choose the sizes for camera preview and video recording

                StreamConfigurationMap map = null;
    try {
            map = mCharacteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
            if (map == null) {
                log_d("Cannot get available preview/video sizes");
            }
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));

} // setUpExtendOutputs



    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
private  Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if ((size.getWidth() == size.getHeight() *VIDEO_RATIO )&&( size.getWidth() <= MAX_VIDEO_WIDTH)) {
                return size;
            }
        }
        log_d("Couldn't find any suitable video size");
        return choices[choices.length - 1];
} // chooseVideoSize



/**
 * setUpMediaRecorder
 */
private void setUpMediaRecorder() {
    log_d("setUpMediaRecorder");
    mMediaRecorder = new MediaRecorder();
    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

    if (mNextVideoOutputFile == null ) {
            mNextVideoOutputFile = getVideoOutputFile();
    }

    mMediaRecorder.setOutputFile(mNextVideoOutputFile.toString());

    mMediaRecorder.setVideoEncodingBitRate(VIDEO_BIT_RATE);
    mMediaRecorder.setVideoFrameRate(VIDEO_FRAME_RATE);
    mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

    int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

} // setUpMediaRecorder


/**
 * getVideoOutputFile
 */
    private File getVideoOutputFile() {
return getAppOutputFile(FILE_PREFIX, FILE_EXT);
    } // getVideoOutputFile



/**
 * startRecordingVideo
 */
private void startRecordingVideo() {

    log_d("startRecordingVideo");
    setUpMediaRecorder();
    stopPreview();  
    stopVideoPreview();
    startVideoPreview();

} // startRecordingVideo


/**
 * stopRecordingVideo
 */
    private void stopRecordingVideo() {

    log_d("stopRecordingVideo");

    try {
// RuntimeException is intentionally thrown to the application, if no valid audio/video data has been received when stop() is called. This happens if stop() is called immediately after start()
        mMediaRecorder.stop();
        mMediaRecorder.reset();
    } catch (Exception e) {
            e.printStackTrace();
    } // try

        setButtonText_onUI(R.string.button_record);
        mIsRecordingVideo = false;
        log_d("stop recording");

    if( mNextVideoOutputFile != null ) {
        String msg = "Video saved: " + mNextVideoOutputFile.toString();
        showToast_onUI(msg);
        log_d(msg);
    }

    mNextVideoOutputFile = null;
    startVideoPreview();

} // stopRecordingVideo



/**
 * startVideoPreview
  */
private void startVideoPreview() {
    log_d("startVideoPreview");

try {
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
            if (texture == null) {
                log_d("cannot get texture");
                return;
            }
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mVideoPreviewBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
        mVideoPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        Surface surface = new Surface(texture);
        mVideoPreviewBuilder.addTarget(surface);
        Surface recorderSurface = mMediaRecorder.getSurface();
        mVideoPreviewBuilder.addTarget(recorderSurface);
        List outputs = Arrays.asList(surface, recorderSurface);
    CameraCaptureSession.StateCallback callback = createVideoCaptureSessionStateCallback();
        mCameraDevice.createCaptureSession(outputs,callback, mBackgroundHandler);
    } catch (Exception e) {
            e.printStackTrace();
    }

} // startVideoPreview


/**
 * createVideoCaptureSessionStateCallback
  */
private CameraCaptureSession.StateCallback createVideoCaptureSessionStateCallback() {

    CameraCaptureSession.StateCallback  callback =
 new CameraCaptureSession.StateCallback() {

@Override
public void onConfigured(CameraCaptureSession cameraCaptureSession) {
    log_d("onConfigured");
    mVideoPreviewSession = cameraCaptureSession;
    updateVideoPreview();
    mIsRecordingVideo = true;
    mMediaRecorder.start();
    setButtonText_onUI(R.string.button_stop);
}

@Override
public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
log_d("onConfigureFailed");
    showToast_onUI("onConfigureFailed");
}

}; // CameraCaptureSession.StateCallbac

    return callback;

} //  createVideoCaptureSessionStateCallback



/**
 * setButtonText_onUI
 */
private void setButtonText_onUI(final int res_id) {

    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // UI
                            mButtonVideo.setText(res_id);
                        }
                    }); //  runOnUiThread
} // setButtonText_onUI



/**
 * stopVideoPreview
  */
private void stopVideoPreview() {
    log_d("stopVideoPreview");
        if (mVideoPreviewSession != null) {
            mVideoPreviewSession.close();
            mVideoPreviewSession = null;
        }
} // stopVideoPreview


/**
 * stopPreview
  */
private void stopPreview() {
    log_d("stopPreview");
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
} // stopPreview


    /**
     * Update the camera preview. {@link #startVideoPreview()} needs to be called in advance.
     */
private void updateVideoPreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            mVideoPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // HandlerThread thread = new 
            // HandlerThread("VideoPreview");
            // thread.start();
            CaptureRequest request = mVideoPreviewBuilder.build();
             mVideoPreviewSession.setRepeatingRequest(request, null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
} // updateVideoPreview


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
