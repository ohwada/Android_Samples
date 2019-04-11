/**
 * Camera2 Sample
 * take picture with RAW format
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera24;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;

import android.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
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
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

 /**
 *   class MainActivity
 * original : https://github.com/googlesamples/android-Camera2Raw/tree/master/Application/src/main/java/com/example/android/camera2raw
  */
public class MainActivity extends PreviewActivity {

        // debug
   	private final static String TAG_SUB = "MainActivity";


    /**
     * Permissions required to take a picture.
     * reqire WRITE_EXTERNAL_STORAGE
     * because save file on external dcim directory
     */
private static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };


    // output file
    private static final String JPEG_PREFIX = "JPEG_";
     private static final String RAW_PREFIX = "RAW_";
    private static final String JPEG_EXT = ".jpg";
    private static final String RAW_EXT = ".dng";


    /**
     * Timeout for the pre-capture sequence.
     */
    private static final long PRECAPTURE_TIMEOUT_MS = 1000;

    /**
     * Tolerance when comparing aspect ratios.
     */
    private static final double ASPECT_RATIO_TOLERANCE = 0.005;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;


    /**
     * A counter for tracking corresponding {@link CaptureRequest}s and {@link CaptureResult}s
     * across the {@link CameraCaptureSession} capture callbacks.
     */
    private final AtomicInteger mRequestCounter = new AtomicInteger();


    /**
     * A reference counted holder wrapping the {@link ImageReader} that handles JPEG image
     * captures. This is used to allow us to clean up the {@link ImageReader} when all background
     * tasks using its {@link Image}s have completed.
     */
    private RefCountedAutoCloseable<ImageReader> mJpegImageReader;

    /**
     * A reference counted holder wrapping the {@link ImageReader} that handles RAW image captures.
     * This is used to allow us to clean up the {@link ImageReader} when all background tasks using
     * its {@link Image}s have completed.
     */
    private RefCountedAutoCloseable<ImageReader> mRawImageReader;

    /**
     * Whether or not the currently configured camera device is fixed-focus.
     */
    private boolean mNoAFRun = false;

    /**
     * Number of pending user requests to capture a photo.
     */
    private int mPendingUserCaptures = 0;

    /**
     * Request ID to {@link ImageSaver.ImageSaverBuilder} mapping for in-progress JPEG captures.
     */
    private final TreeMap<Integer, ImageSaver.ImageSaverBuilder> mJpegResultQueue = new TreeMap<>();

    /**
     * Request ID to {@link ImageSaver.ImageSaverBuilder} mapping for in-progress RAW captures.
     */
    private final TreeMap<Integer, ImageSaver.ImageSaverBuilder> mRawResultQueue = new TreeMap<>();

    /**
     * Timer to use with pre-capture sequence to ensure a timely capture if 3A convergence is
     * taking too long.
     */
    private long mCaptureTimer;


    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * JPEG image is ready to be saved.
     */
private final ImageReader.OnImageAvailableListener mOnJpegImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            log_d("Jpeg onImageAvailable");
            dequeueAndSaveImage(mJpegResultQueue, mJpegImageReader);
        }

}; // OnJpegImageAvailableListener



    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * RAW image is ready to be saved.
     */
private final ImageReader.OnImageAvailableListener mOnRawImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            log_d("Raw onImageAvailable");
            dequeueAndSaveImage(mRawResultQueue, mRawImageReader);
        }

    }; // OnRawImageAvailableListener

    /**
     * An {@link OrientationEventListener} used to determine when device rotation has occurred.
     * This is mainly necessary for when the device is rotated by 180 degrees, in which case
     * onCreate or onConfigurationChanged is not called as the view dimensions remain the same,
     * but the orientation of the has changed, and thus the preview rotation must be updated.
     */
    private OrientationEventListener mOrientationListener;


/**
 * createExtend
 */
@Override
protected void createExtend() {
    log_d("createExtend");
    Button btnPicture = (Button)findViewById(R.id.Button_picture);
    btnPicture.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                takePicture();
        }
    }); // btnPicture

    mOrientationListener = createOrientationListener();

    mCameraPerm.setPermissions( CAMERA_PERMISSIONS );

} // createExtend


/**
 * resumeExtend
 */
@Override
protected void resumeExtend() {
    log_d("resumeExtend");
        if (mOrientationListener != null && mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        }
} // resumeExtend



/**
 * pauseExtend
 */
@Override
protected void pauseExtend() {
    log_d("pauseExtend");
        if (mOrientationListener != null) {
            mOrientationListener.disable();
        }
} // pauseExtend


/**
 * openCameraExtend
 */
    @Override
protected void openCameraExtend() {
        createCaptureCallback();
} // openCameraExtend

  
/**
  * procCaptureConfigured
  */
    @Override
protected void procCaptureConfigured(CameraCaptureSession cameraCaptureSession) {
    log_d("procCaptureConfigured"); 
                              // The camera is already closed
                                if (null == mCameraDevice) {
                                    log_d("camera is already closed"); 
                                    return;
                                }
                            // When the session is ready, we start displaying the preview.
                                mCaptureSession = cameraCaptureSession;

                                synchronized (mCameraStateLock) {
                                    mState = STATE_PREVIEW;
                                    log_d("set STATE_PREVIEW"); 
                                }

                               try {
                                    setup3AControlsLocked(mPreviewRequestBuilder);
                                    // Finally, we start displaying the camera preview.
                                    cameraCaptureSession.setRepeatingRequest(
                                            mPreviewRequestBuilder.build(),
                                            mPreCaptureCallback, mBackgroundHandler);
                                } catch (CameraAccessException | IllegalStateException e) {
                                    e.printStackTrace();
                                }
} // procCaptureConfigured


    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles the still JPEG and RAW capture
     * request.
     */
private void createCaptureCallback() {

 mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

@Override
public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request,
                                     long timestamp, long frameNumber) {
log_d("onCaptureStarted");
            File rawFile = getOutputFile( RAW_PREFIX,  RAW_EXT );
            File jpegFile = getOutputFile( JPEG_PREFIX,  JPEG_EXT );

            // Look up the ImageSaverBuilder for this request and update it with the file name
            // based on the capture start time.
            ImageSaver.ImageSaverBuilder jpegBuilder;
            ImageSaver.ImageSaverBuilder rawBuilder;
            int requestId = (int) request.getTag();
            synchronized (mCameraStateLock) {
                jpegBuilder = mJpegResultQueue.get(requestId);
                rawBuilder = mRawResultQueue.get(requestId);
            }

            if (jpegBuilder != null) jpegBuilder.setFile(jpegFile);
            if (rawBuilder != null) rawBuilder.setFile(rawFile);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
log_d("onCaptureCompleted");
            int requestId = (int) request.getTag();
            ImageSaver.ImageSaverBuilder jpegBuilder;
            ImageSaver.ImageSaverBuilder rawBuilder;
            StringBuilder sb = new StringBuilder();

            // Look up the ImageSaverBuilder for this request and update it with the CaptureResult
            synchronized (mCameraStateLock) {
                jpegBuilder = mJpegResultQueue.get(requestId);
                rawBuilder = mRawResultQueue.get(requestId);

                if (jpegBuilder != null) {
                    jpegBuilder.setResult(result);
                    sb.append("Saving JPEG as: ");
                    sb.append(jpegBuilder.getSaveLocation());
                }
                if (rawBuilder != null) {
                    rawBuilder.setResult(result);
                    if (jpegBuilder != null) sb.append(", ");
                    sb.append("Saving RAW as: ");
                    sb.append(rawBuilder.getSaveLocation());
                }

                // If we have all the results necessary, save the image to a file in the background.
                handleCompletionLocked(requestId, jpegBuilder, mJpegResultQueue);
                handleCompletionLocked(requestId, rawBuilder, mRawResultQueue);

                finishedCaptureLocked();
            }

             showToast_onUI(sb.toString());
            
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request,
                                    CaptureFailure failure) {
log_d("onCaptureFailed");
            int requestId = (int) request.getTag();
            synchronized (mCameraStateLock) {
                mJpegResultQueue.remove(requestId);
                mRawResultQueue.remove(requestId);
                finishedCaptureLocked();
            }
            showToast_onUI("Capture failed!");
        }

}; // CameraCaptureSession.CaptureCallback

}  // createCaptureCallback



    /**
     * Called after a RAW/JPEG capture has completed; resets the AF trigger state for the
     * pre-capture sequence.
     * <p/>
     * Call this only with {@link #mCameraStateLock} held.
     */
private void finishedCaptureLocked() {
        try {
            // Reset the auto-focus trigger in case AF didn't run quickly enough.
            if (!mNoAFRun) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                        CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);

                mCaptureSession.capture(mPreviewRequestBuilder.build(), mPreCaptureCallback,
                        mBackgroundHandler);

                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                        CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
} // finishedCaptureLocked


/**
 * setUpExtendOutputs
 */
@Override
protected void setUpExtendOutputs() {
    log_d("setUpExtendOutputs");

    if(mCharacteristics == null ) {
            log_d("characteristics = null ");
            return;
    }


        // We only use a camera that supports RAW in this sample.

        if (!contains(mCharacteristics.get(
                                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES),
                        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)) {
        showErrorDialog(R.string.msg_not_support_raw);
                    return;
    }

        StreamConfigurationMap map = mCharacteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // For still image captures, we use the largest available size.
                Size largestJpeg = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());

                Size largestRaw = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.RAW_SENSOR)),
                        new CompareSizesByArea());

                synchronized (mCameraStateLock) {
                    // Set up ImageReaders for JPEG and RAW outputs.  Place these in a reference
                    // counted wrapper to ensure they are only closed when all background tasks
                    // using them are finished.
                    if (mJpegImageReader == null || mJpegImageReader.getAndRetain() == null) {
                        mJpegImageReader = new RefCountedAutoCloseable<>(
                                ImageReader.newInstance(largestJpeg.getWidth(),
                                        largestJpeg.getHeight(), ImageFormat.JPEG, /*maxImages*/5));
                    }
                    mJpegImageReader.get().setOnImageAvailableListener(
                            mOnJpegImageAvailableListener, mBackgroundHandler);

                    if (mRawImageReader == null || mRawImageReader.getAndRetain() == null) {
                        mRawImageReader = new RefCountedAutoCloseable<>(
                                ImageReader.newInstance(largestRaw.getWidth(),
                                        largestRaw.getHeight(), ImageFormat.RAW_SENSOR, /*maxImages*/ 5));
                    }
                    mRawImageReader.get().setOnImageAvailableListener(
                            mOnRawImageAvailableListener, mBackgroundHandler);

                } // synchronized


} // setUpExtendOutputs



    /**
     * Return true if the given array contains the given integer.
     *
     * @param modes array to check.
     * @param mode  integer to get for.
     * @return true if the array contains the given integer, otherwise false.
     */
private boolean contains(int[] modes, int mode) {
        if (modes == null) {
            return false;
        }
        for (int i : modes) {
            if (i == mode) {
                return true;
            }
        } // for
        return false;
} // contains



/**
 * closeCameraExtend
 * override for Extend
 */
protected void closeCameraExtend() {

                // Reset state and clean up resources used by the camera.
                // Note: After calling this, the ImageReaders will be closed after any background
                // tasks saving Images from these readers have been completed.
                mPendingUserCaptures = 0;
                if (null != mJpegImageReader) {
                    mJpegImageReader.close();
                    mJpegImageReader = null;
                }
                if (null != mRawImageReader) {
                    mRawImageReader.close();
                    mRawImageReader = null;
                }
} // closeCameraExtend



/**
  * createCameraPreviewSession
  */
    @Override
protected void createCameraPreviewSession() {
log_d("createCameraPreviewSession");

        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
        List outputs = Arrays.asList(surface,
            mJpegImageReader.get().getSurface(),
            mRawImageReader.get().getSurface() );
        CameraCaptureSession.StateCallback callback = createCameraCaptureSessionStateCallback();
        mCameraDevice.createCaptureSession(outputs,callback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO : SecurityException: Lacking privileges to access camera service
            e.printStackTrace();
        }

} // createCameraPreviewSession


    /**
     * Configure the given {@link CaptureRequest.Builder} to use auto-focus, auto-exposure, and
     * auto-white-balance controls if available.
     * <p/>
     * Call this only with {@link #mCameraStateLock} held.
     *
     * @param builder the builder to configure.
     */
private void setup3AControlsLocked(CaptureRequest.Builder builder) {
        // Enable auto-magical 3A run by camera device
        builder.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO);

        Float minFocusDist =
                mCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);

        // If MINIMUM_FOCUS_DISTANCE is 0, lens is fixed-focus and we need to skip the AF run.
        mNoAFRun = (minFocusDist == null || minFocusDist == 0);

        if (!mNoAFRun) {
            // If there is a "continuous picture" mode available, use it, otherwise default to AUTO.
            if (contains(mCharacteristics.get(
                            CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES),
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)) {
                builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            } else {
                builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_AUTO);
            }
        }

        // If there is an auto-magical flash control mode available, use it, otherwise default to
        // the "on" mode, which is guaranteed to always be available.
        if (contains(mCharacteristics.get(
                        CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES),
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)) {
            builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        } else {
            builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON);
        }

        // If there is an auto-magical white balance control mode available, use it.
        if (contains(mCharacteristics.get(
                        CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES),
                CaptureRequest.CONTROL_AWB_MODE_AUTO)) {
            // Allow AWB to run auto-magically if this device supports this
            builder.set(CaptureRequest.CONTROL_AWB_MODE,
                    CaptureRequest.CONTROL_AWB_MODE_AUTO);
        }

} // setup3AControlsLocked




    /**
     * Initiate a still image capture.
     * <p/>
     * This function sends a capture request that initiates a pre-capture sequence in our state
     * machine that waits for auto-focus to finish, ending in a "locked" state where the lens is no
     * longer moving, waits for auto-exposure to choose a good exposure value, and waits for
     * auto-white-balance to converge.
     */
private void takePicture() {
    log_d("takePicture");
        synchronized (mCameraStateLock) {
            mPendingUserCaptures++;

            // If we already triggered a pre-capture sequence, or are in a state where we cannot
            // do this, return immediately.
            if (mState != STATE_PREVIEW) {
                log_d("skip because not STATE_PREVIEW");
                return;
            }

            // set mPreviewRequestBuilder in createCameraPreviewSession
            try {
                // Trigger an auto-focus run if camera is capable. If the camera is already focused,
                // this should do nothing.
                if (!mNoAFRun) {
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                            CameraMetadata.CONTROL_AF_TRIGGER_START);
                }

                // If this is not a legacy device, we can also trigger an auto-exposure metering
                // run.
                if (!isLegacyLocked()) {
                    // Tell the camera to lock focus.
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                            CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
                }

                // Update state machine to wait for auto-focus, auto-exposure, and
                // auto-white-balance (aka. "3A") to converge.
                mState = STATE_WAITING_FOR_3A_CONVERGENCE;

                // Start a timer for the pre-capture sequence.
                startTimerLocked();
                // Replace the existing repeating request with one with updated 3A triggers.
                mCaptureSession.capture(mPreviewRequestBuilder.build(), mPreCaptureCallback,
                        mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        } //  synchronized

} // takePicture




    /**
     * Send a capture request to the camera device that initiates a capture targeting the JPEG and
     * RAW outputs.
     * <p/>
     * Call this only with {@link #mCameraStateLock} held.
     */
private void captureStillPictureLocked() {
        try {

            if ( null == mCameraDevice) {
                return;
            }

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            captureBuilder.addTarget(mJpegImageReader.get().getSurface());
            captureBuilder.addTarget(mRawImageReader.get().getSurface());

            // Use the same AE and AF modes as the preview.
            setup3AControlsLocked(captureBuilder);

            // Set orientation.
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    sensorToDeviceRotation(mCharacteristics, rotation));

            // Set request tag to easily track results in callbacks.
            captureBuilder.setTag(mRequestCounter.getAndIncrement());

            CaptureRequest request = captureBuilder.build();

            // Create an ImageSaverBuilder in which to collect results, and add it to the queue
            // of active requests.
            ImageSaver.ImageSaverBuilder jpegBuilder = new ImageSaver.ImageSaverBuilder(this)
                    .setCharacteristics(mCharacteristics);
            ImageSaver.ImageSaverBuilder rawBuilder = new ImageSaver.ImageSaverBuilder(this)
                    .setCharacteristics(mCharacteristics);

            mJpegResultQueue.put((int) request.getTag(), jpegBuilder);
            mRawResultQueue.put((int) request.getTag(), rawBuilder);

            mCaptureSession.capture(request, mCaptureCallback, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

} // captureStillPictureLocked


    /**
     * Rotation need to transform from the camera sensor orientation to the device's current
     * orientation.
     *
     * @param c                 the {@link CameraCharacteristics} to query for the camera sensor
     *                          orientation.
     * @param deviceOrientation the current device orientation relative to the native device
     *                          orientation.
     * @return the total rotation from the sensor orientation to the current device orientation.
     */
private static int sensorToDeviceRotation(CameraCharacteristics c, int deviceOrientation) {
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Get device orientation in degrees
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);

        // Reverse device orientation for front-facing cameras
        if (c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
            deviceOrientation = -deviceOrientation;
        }

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        return (sensorOrientation - deviceOrientation + 360) % 360;
 } // sensorToDeviceRotation


    /**
     * Retrieve the next {@link Image} from a reference counted {@link ImageReader}, retaining
     * that {@link ImageReader} until that {@link Image} is no longer in use, and set this
     * {@link Image} as the result for the next request in the queue of pending requests.  If
     * all necessary information is available, begin saving the image to a file in a background
     * thread.
     *
     * @param pendingQueue the currently active requests.
     * @param reader       a reference counted wrapper containing an {@link ImageReader} from which
     *                     to acquire an image.
     */
private void dequeueAndSaveImage(TreeMap<Integer, ImageSaver.ImageSaverBuilder> pendingQueue,
                                     RefCountedAutoCloseable<ImageReader> reader) {
        synchronized (mCameraStateLock) {
            Map.Entry<Integer, ImageSaver.ImageSaverBuilder> entry =
                    pendingQueue.firstEntry();
            ImageSaver.ImageSaverBuilder builder = entry.getValue();

            // Increment reference count to prevent ImageReader from being closed while we
            // are saving its Images in a background thread (otherwise their resources may
            // be freed while we are writing to a file).
            if (reader == null || reader.getAndRetain() == null) {
                Log.e(TAG, "Paused the activity before we could save the image," +
                        " ImageReader already closed.");
                pendingQueue.remove(entry.getKey());
                return;
            }

            Image image;
            try {
                image = reader.get().acquireNextImage();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Too many images queued for saving, dropping image for request: " +
                        entry.getKey());
                pendingQueue.remove(entry.getKey());
                return;
            }
            builder.setRefCountedReader(reader).setImage(image);

            handleCompletionLocked(entry.getKey(), builder, pendingQueue);
        }
} // dequeueAndSaveImage


    /**
     * If the given request has been completed, remove it from the queue of active requests and
     * send an {@link ImageSaver} with the results from this request to a background thread to
     * save a file.
     * <p/>
     * Call this only with {@link #mCameraStateLock} held.
     *
     * @param requestId the ID of the {@link CaptureRequest} to handle.
     * @param builder   the {@link ImageSaver.ImageSaverBuilder} for this request.
     * @param queue     the queue to remove this request from, if completed.
     */
private void handleCompletionLocked(int requestId, ImageSaver.ImageSaverBuilder builder,
                                        TreeMap<Integer, ImageSaver.ImageSaverBuilder> queue) {
        if (builder == null) return;
        ImageSaver saver = builder.buildIfComplete();
        if (saver != null) {
            queue.remove(requestId);
            AsyncTask.THREAD_POOL_EXECUTOR.execute(saver);
        }
} // handleCompletionLocked



/**
 * getOutputFile
 */
protected File getOutputFile(String prefix, String ext) {
            File dir = Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String filename = getOutputFileName(prefix, ext);
        File file = new File(dir, filename);
    return file;
} // getOutputFile


/**
  * showImageDialog_onUI
 */





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


    /**
     * Start the timer for the pre-capture sequence.
     * <p/>
     * Call this only with {@link #mCameraStateLock} held.
     */
private void startTimerLocked() {
        mCaptureTimer = SystemClock.elapsedRealtime();
} // startTimerLocked


    /**
     * Check if the timer for the pre-capture sequence has been hit.
     * <p/>
     * Call this only with {@link #mCameraStateLock} held.
     *
     * @return true if the timeout occurred.
     */
private boolean hitTimeoutLocked() {
        return (SystemClock.elapsedRealtime() - mCaptureTimer) > PRECAPTURE_TIMEOUT_MS;
} // hitTimeoutLocked


    /**
     * Check if we are using a device that only supports the LEGACY hardware level.
     * <p/>
     * Call this only with {@link #mCameraStateLock} held.
     *
     * @return true if this is a legacy device.
     */
private boolean isLegacyLocked() {
        return mCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) ==
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
} // isLegacyLocked


    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events for the preview and
     * pre-capture sequence.
     */
private CameraCaptureSession.CaptureCallback mPreCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {

            synchronized (mCameraStateLock) {
                switch (mState) {
                    case STATE_PREVIEW: {
                        // We have nothing to do when the camera preview is running normally.
                        break;
                    }
                    case STATE_WAITING_FOR_3A_CONVERGENCE: {
                        boolean readyToCapture = true;
                        if (!mNoAFRun) {
                            Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                            if (afState == null) {
                                break;
                            }

                            // If auto-focus has reached locked state, we are ready to capture
                            readyToCapture =
                                    (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                                            afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED);
                        }

                        // If we are running on an non-legacy device, we should also wait until
                        // auto-exposure and auto-white-balance have converged as well before
                        // taking a picture.
                        if (!isLegacyLocked()) {
                            // AE : auto-exposure algorithm
                            Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                            // (AWB : auto-white balance 
                            Integer awbState = result.get(CaptureResult.CONTROL_AWB_STATE);
                            if (aeState == null || awbState == null) {
                                break;
                            }

                            readyToCapture = readyToCapture &&
                                    aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED &&
                                    awbState == CaptureResult.CONTROL_AWB_STATE_CONVERGED;
                        }

                        // If we haven't finished the pre-capture sequence but have hit our maximum
                        // wait timeout, too bad! Begin capture anyway.
                        if (!readyToCapture && hitTimeoutLocked()) {
                            log_d("Timed out waiting for pre-capture sequence to complete.");
                            readyToCapture = true;
                        }
                        if (readyToCapture && mPendingUserCaptures > 0) {
                            // Capture once for each user tap of the "Picture" button.
                            while (mPendingUserCaptures > 0) {
                                captureStillPictureLocked();
                                mPendingUserCaptures--;
                            } // while
                            // After this, the camera will go back to the normal state of preview.
                            mState = STATE_PREVIEW;
                        } // if readyToCapture
                    }
                } // synchronized
            } // process
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                        CaptureResult partialResult) {
// log_d("Pre onCaptureProgressed");
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
// log_d("Pre onCaptureCompleted");
            process(result);
        }

    }; // mPreCaptureCallback


    /**
     * An {@link OrientationEventListener} used to determine when device rotation has occurred.
     * This is mainly necessary for when the device is rotated by 180 degrees, in which case
     * onCreate or onConfigurationChanged is not called as the view dimensions remain the same,
     * but the orientation of the has changed, and thus the preview rotation must be updated.
     */
private OrientationEventListener createOrientationListener() {

OrientationEventListener listener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

@Override
public void onOrientationChanged(int orientation) {
log_d("onOrientationChanged");
        if (mTextureView != null && mTextureView.isAvailable()) {
                    configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
        }
    } // onOrientationChanged

}; // OrientationEventListener

    return listener;
} // createOrientationListener

} //  class MainActivity
