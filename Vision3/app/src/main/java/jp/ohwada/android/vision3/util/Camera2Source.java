/**
 * Vision Sample
 * Barcode Detection using Vision API
 * 2019-02-01 K.OHWADA
 */


package jp.ohwada.android.vision3.util;


import android.content.Context;
import android.graphics.ImageFormat;
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
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.widget.Toast;
import android.util.Size;


import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;


import java.lang.Thread.State;
import java.util.Arrays;
import java.util.List;


/**
  *  class Camera2FrameSource
 * similar to CameraSource of Vision API
 * original : https://github.com/EzequielAdrianM/Camera2Vision
  */
public class Camera2Source extends Camera2Base {


        // debug
    private final static String TAG_SUB = "Camera2Source";

    // image reader
    private final static int IMAGE_READER_PREVIEW_MAX_IMAGES = 4;

    // image size
    public final static int SCALE_FACTOR = 4;


    /**
     * Dedicated thread and associated runnable for calling into the detector with frames, as the
     * frames become available from the camera.
     */
    private static Thread mProcessingThread;
    private Camera2FrameProcessor mFrameProcessor;


 /**
  * An {@link ImageReader} that handles live preview.
 */
    private ImageReader mImageReaderPreview;


 /**
  *  The size of  YUV_420_888 format image
  */
    private Size mImagePreviewSize;


 /**
  *  flag of resume or pause to setNextImage
  */
    private boolean isDetectRunning = false;


/**
  * constractor
 */
    public Camera2Source(Context context) {
        super(context);
    }


/**
  * setFrameProcessor
 */
    public void setFrameProcessor( Camera2FrameProcessor processor) {
        mFrameProcessor = processor;
    }


/**
  * getImagePreviewSize
 */
    public Size getImagePreviewSize() {
        return mImagePreviewSize;
    }


/**
  * release
 */
    public void release() {
        stopFrameProcessor();
        releaseFrameProcessor();
    }


/**
 * stopExtend
 */
protected void stopExtend() {
        stopFrameProcessor();
        isDetectRunning = false;
}


/**
  * stops sending frames to the underlying frame detector.
  */
private void stopFrameProcessor() {
            if (mFrameProcessor != null) {
                    mFrameProcessor.setActive(false);
            }
            if (mProcessingThread == null) return;
            try {
                    // Wait for the thread to complete to ensure that we can't have multiple threads executing at the same time                    
                    mProcessingThread.join();
            } catch (InterruptedException e) {
                        e.printStackTrace();
            }
}


/**
  *  releases the resources of underlying detector.
 */
private void releaseFrameProcessor() {
        if (mFrameProcessor != null) {
            mFrameProcessor.release(mProcessingThread);
        }
}


/**
  * setDetectRunning
 */
    public void setDetectRunning(boolean is_running) {
        isDetectRunning = is_running;
    }


/**
  * pauseFaceDetect
 */
    public void pauseFaceDetect() {
        isDetectRunning = false;
    }


/**
  * prepareCamera
 */
@Override
protected void prepareCamera() {
    if (null != mFrameProcessor) {
            mProcessingThread = new Thread(mFrameProcessor);
            mProcessingThread.start();
            mFrameProcessor.setActive(true);
    }
} // prepareCamera



/**
 * setUpExtend
 */
@Override
protected void setUpExtend(CameraCharacteristics characteristics) {
        log_d("setUpExtend");
        StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] supportedSizes = map.getOutputSizes(ImageFormat.YUV_420_888);
        mImagePreviewSize = chooseLargestSize(supportedSizes);
        log_d("YUV_420_888 largest : " +  mImagePreviewSize.toString());
        int imageWidth = mImagePreviewSize.getWidth();
        int imageHeight = mImagePreviewSize.getHeight();
        mImageReaderPreview = ImageReader.newInstance( imageWidth, imageHeight, ImageFormat.YUV_420_888, IMAGE_READER_PREVIEW_MAX_IMAGES);
        mImageReaderPreview.setOnImageAvailableListener(mOnPreviewAvailableListener, mBackgroundHandler);

        if (null != mFrameProcessor) {
                mFrameProcessor.setImageSize( imageWidth, imageHeight);
                mFrameProcessor.setSensorOrientation(mSensorOrientation);
        }
} // setUpExtend

 

    /**
     * This is a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * preview frame is ready to be processed.
     */
    private final ImageReader.OnImageAvailableListener mOnPreviewAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //log_d("Preview onImageAvailable");
            procImageAvailable(reader);
        } // onImageAvailable
    }; // ImageReader.OnImageAvailableListener


/**
  * procImageAvailable
 */
private void procImageAvailable(ImageReader reader) {
         //log_d("procImageAvailable");
        Image image = null;
        try {
                image = reader.acquireNextImage();
        } catch (IllegalStateException e) {
                e.printStackTrace();
        }
        if (image == null) return;
        if (isDetectRunning &&(mFrameProcessor != null)) {
                mFrameProcessor.setNextImage(image);
        }
        image.close();
} // procImageAvailable



    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
//@Override
protected void createCameraPreviewSession() {
log_d("createCameraPreviewSession");
    try {
            // We set up a CaptureRequest.Builder with the output Surface.
            Surface imageReaderSurface = mImageReaderPreview.getSurface();
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mViewSurface);
            mPreviewRequestBuilder.addTarget(imageReaderSurface);
            // Here, we create a CameraCaptureSession for camera preview.
            List outputs = Arrays.asList(mViewSurface, imageReaderSurface);
                mCameraDevice.createCaptureSession(outputs, mPreviewSession, mBackgroundHandler);
        } catch (CameraAccessException e) {
                        e.printStackTrace();
        }

} //  createCameraPreviewSession


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


    /**
     * Builder for configuring and creating an associated camera source.
     */
    public static class Builder {


/**
   * Camera source  
  */
        private Camera2Source mCamera2Source;


/**
   * constractor for Face Detection
   * require param is_quarter
  */
    public Builder(Context context, Detector<?> detector, boolean is_quarter) {
            procBuilder(context, detector,  is_quarter) ;
    }


/**
   * constractor for Barcode Detection
   * use original Frame size
  */
    public Builder(Context context, Detector<?> detector) {
            procBuilder(context, detector,  false);
    }


/**
  * Creates an instance of the camera source.
  */
private void procBuilder(Context context, Detector<?> detector, boolean is_quarter) {
            mCamera2Source = new Camera2Source(context);
            if (detector != null) {
                Camera2FrameProcessor processor = new Camera2FrameProcessor(detector, is_quarter);
                mCamera2Source.setFrameProcessor(processor);
            }
}


/**
   *  setFocusMode
  */
    public Builder setFocusMode(int mode) {
            mCamera2Source.setFocusMode(mode);
            return this;
    } //  setFocusMode


/**
   *  setFlashMode
  */
    public Builder setFlashMode(int mode) {
            mCamera2Source.setFlashMode(mode);
            return this;
    } // setFlashMode


 /**
  * Sets the camera to use
  */
    public Builder setFacing(int facing) {
            mCamera2Source.setFacing(facing);
            return this;
    } // setFacing


/**
   *  setDetectRunning
  */
    public Builder setDetectRunning(boolean is_running) {
            mCamera2Source.setDetectRunning(is_running);
            return this;
    }


/**
   *  setErrorCallback
  */
    public Builder setErrorCallback(ErrorCallback cb) {
            mCamera2Source.setErrorCallback(cb);
            return this;
    }


/**
  * Creates an instance of the camera source.
  */
       public Camera2Source build() {
            return mCamera2Source;
    } // build


} // class Builder


} // class Camera2Source
