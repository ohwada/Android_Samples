/**
 * Camera2 Sample
 * record Video 
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera213.util;


import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.icu.text.SimpleDateFormat;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
  * Aspect Ratio of Video Screen Size
  */
    private final static float VIDEO_ASPECT_RATIO = 16 / 9;


/**
  * Callback interface used to indicate 
  * when video Recording started or stopped.
  */
    public interface VideoCallback {
        void onVideoStart();
        void onVideoStop(String videoFile);
        void onVideoError(String error);
    }


/**
   *  VideoCallback
  */
    private VideoCallback mVideoCallback;


/**
   *  MediaRecorder
  */
    private MediaRecorder mMediaRecorder;


 /**
  * The {@link Size} of Media Recorder.
  */
    private Size mVideoSize;

/**
   *  Output File for MediaRecorder
  */
    private File mVideoFile;


/**
   *  constractor
  */
        public Camera2Source(Context context) {
                super(context);
        }


/**
  * recordVideo
  * generate VideoFile by default、if outputFile is not specified
  */
    public void recordVideo(VideoParam videoParam, VideoCallback videoCallback) {
            mVideoCallback = videoCallback;

            if(mCameraDevice == null || !mTextureView.isAvailable() || mPreviewSize == null){
                if(mVideoCallback != null) {
                    mVideoCallback.onVideoError("Camera not ready.");
                }
                return;
            }

            mMediaRecorder = createMediaRecorder(videoParam);

            try {
                mMediaRecorder.prepare();
                closePreviewSession();
                createCameraRecordSession();
            } catch(IOException ex) {
                ex.printStackTrace();
            }

} // recordVideo


/**
  * createMediaRecorder
  */
private MediaRecorder createMediaRecorder(VideoParam videoParam) {
            if(videoParam == null) {
                // create default setting
                videoParam = new VideoParam();
            } 
        log_d("VideoParam:" + videoParam.toString());

        int audioSource = videoParam.getAudioSource();
        int audioEncoder = videoParam.getAudioEncoder();
        // VideoEncoder: defult H264
        int videoEncoder = videoParam.getVideoEncoder();
        // OutputFormat: default MPEG_4
        int outputFormat = videoParam.getOutputFormat();
        // VideoEncodingBitRate: default 10000000
        int videoEncodingBitRate = videoParam.getVideoEncodingBitRate();
        // VideoFrameRate: default 30
        int videoFrameRate = videoParam.getVideoFrameRate();

        // AudioChannels: default 0, not specify
        int audioChannels = videoParam.getAudioChannels();
        // AudioEncodingBitRate: default 0, not specify
        int audioEncodingBitRate = videoParam.getAudioEncodingBitRate();
        // AudioSamplingRate: default 0, not specify
        int audioSamplingRate = videoParam.getAudioSamplingRate();

        // OutputFile: default inExternalFilesDir
        mVideoFile = videoParam.getOutputFile(mContext);
        int orientation = VideoParam.getOrientationHint(mSensorOrientation, mDisplayRotation);

// Note there is an order for specifying parameters
// setSource: call this only before setOutputFormat()
// setEncoder: call this after setOutputFormat() but before prepare().
        MediaRecorder mediaRecorder = new MediaRecorder();
        try {
            if( audioSource != VideoParam.AUDIO_SOURCE_DEFAULT) {
                // do not use this feature, if default
                // set the specified value, if not default
                // require RECORD_AUDIO permission, when use MIC
                mediaRecorder.setAudioSource(audioSource);
            }

            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(outputFormat);

            if( audioEncoder != VideoParam.AUDIO_ENCODER_DEFAULT) {
                // do not use this feature, if default
                // set the specified value, if not default
                mediaRecorder.setAudioEncoder(audioEncoder);
            }

            if( audioChannels > 0 ) {
                // set the specified value
                mediaRecorder.setAudioChannels(audioChannels);
            }

            if( audioEncodingBitRate > 0 ) {
                // set the specified value
                mediaRecorder.setAudioEncodingBitRate(audioEncodingBitRate);
            }

            if( audioSamplingRate > 0 ) {
                // set the specified value
                mediaRecorder.setAudioSamplingRate(audioSamplingRate);
            }

            if( videoEncodingBitRate > 0 ) {
                // set the specified value
                mediaRecorder.setVideoEncodingBitRate(videoEncodingBitRate);
            }

            if( videoFrameRate > 0 ) {
                // set the specified value
                mediaRecorder.setVideoFrameRate(videoFrameRate);
            }

            mediaRecorder.setVideoEncoder(videoEncoder);

            mediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
            mediaRecorder.setOutputFile(mVideoFile.toString());
            mediaRecorder.setOrientationHint(orientation);
        } catch(IllegalStateException ex) {
                ex.printStackTrace();
        }
        return mediaRecorder;

    } // createMediaRecorder


/**
  * stopVideo
  */
    public void stopVideo() {
        try {
                //Stop recording
                mMediaRecorder.stop();
                // reset to idle state
                mMediaRecorder.reset();
        } catch(IllegalStateException e) {
                e.printStackTrace();
        }
        if((mVideoCallback != null )&&( mVideoFile != null )) {
                // notify stop recording
                String videoFile = mVideoFile.toString();
                mVideoCallback.onVideoStop(videoFile);
        }
        closePreviewSession();
        createCameraPreviewSession();
    }


/**
 * setUpExtend
 */
@Override
protected void setUpExtend(CameraCharacteristics characteristics) {
    log_d("setUpExtend");
    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    Size[] outputSizesMediaRecorder = sizeToSize(map.getOutputSizes(MediaRecorder.class));
    mVideoSize = chooseVideoSize(outputSizesMediaRecorder);
    log_d("VideoSize: " + mVideoSize.toString());
} // setUpExtend


/**
 * sizeToSize
 */
private Size[] sizeToSize(android.util.Size[] sizes) {
        Size[] size = new Size[sizes.length];
        for(int i=0; i<sizes.length; i++) {
            size[i] = new Size(sizes[i].getWidth(), sizes[i].getHeight());
        } // for
        return size;
} // sizeToSize


/**
  * We choose a video size with 3x4 aspect ratio. Also, we don't use sizes
=  * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
  */
private Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * VIDEO_ASPECT_RATIO)
            {
                return size;
            }
        } // for
        log_d("Couldn't find any suitable video size");
        return choices[0];
} // chooseVideoSize


/**
 * stopExtend
 */
@Override
protected void stopExtend() {
        if (null != mMediaRecorder) {
                // releases resources 
                mMediaRecorder.release();
                mMediaRecorder = null;
        }
} // stopExtend


 /**
  * createCameraRecordSession
  */
    private void createCameraRecordSession() {
        try {
            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();

            // set up a CaptureRequest.Builder
             mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
             mPreviewRequestBuilder.addTarget(mViewSurface);
             mPreviewRequestBuilder.addTarget(recorderSurface);

            // set up the output Surface.
            List<Surface> outputs = new ArrayList<>();
            outputs.add(mViewSurface);
            outputs.add(recorderSurface);

            // Start a capture session
            mCameraDevice.createCaptureSession(outputs, mVideoSessionStateCallback , mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
} // createCameraRecordSession


 /**
  * mVideoSessionStateCallback 
  */
private CameraCaptureSession.StateCallback mVideoSessionStateCallback 
= new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured( CameraCaptureSession cameraCaptureSession) {
                    procVideoCaptureSessionConfigured( cameraCaptureSession);
                }
                @Override
                public void onConfigureFailed( CameraCaptureSession session) {
                    log_d("video Configuration failed!");
                }
}; // mVideoSessionStateCallback


 /**
  * procVideoCaptureSessionConfigured
  */
private void procVideoCaptureSessionConfigured( CameraCaptureSession cameraCaptureSession) {
        log_d("procVideoCaptureSessionConfigured");
        // The camera is already closed
        if (null == mCameraDevice) {
                        return;
        }
    mCaptureSession = cameraCaptureSession;
        updatePreview();

        //Start recording
        mMediaRecorder.start();
        if( mVideoCallback != null) {
                // notify start recording
                mVideoCallback.onVideoStart();
        }
} //  procVideoCaptureSessionConfigured


    /**
     * Update the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            // settings for each individual 3A routine.
            // TODO : the user specify camera parameters
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            mCaptureSession.setRepeatingRequest( mPreviewRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
}



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


