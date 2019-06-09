/**
 * Vision Sample
 * Barcode / OCR Detection using Vision API
 * 2019-02-01 K.OHWADA
 */


package jp.ohwada.android.vision4.util;


import android.graphics.ImageFormat;
import android.media.Image;
import android.os.SystemClock;
import android.util.Log;


import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;


import java.nio.ByteBuffer;


/**
  * This runnable controls access to the underlying receiver,   
 * calling it to process frames when available from the camera.  
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */
public class Camera2FrameProcessor implements Runnable {

        // debug
	    private final static boolean D = true;
        private final static String TAG = "Vision";
        private final static String TAG_SUB = "Camera2FrameProcessor";

/**
 *  Constant whether to reduce to quarter size the NV21 frame
  */
        public final static int SCALE_QUARTER = 4;


/**
 *  Flag whether to reduce to quarter size the NV21 frame
 *  Face detection works well in QuarterSize
 *  Barcode detection can only works wel in Original size
  */
        private boolean isQuarterSize = false;


/**
 * Detector of Fce, Barcode, or OCR
  */
        private Detector<?> mDetector;


/**
 * This lock guards all of the member variables below.
  */ 
        private final Object mLock = new Object();


/**
 * Flag of whether to exit an endless loop in run method
  */ 
        private boolean mActive = true;


/**
 * Identifier  of pending frame
  */
        private int mPendingFrameId = 0;


/**
 * Timestamp  of pending frame
  */
        private long mPendingTimeMillis;


/**
 * Data byte of pending frame
  */
        private byte[] mPendingFrameData;


/**
 * reference Time to calculate Timestamp
  */  
        private long mStartTimeMillis = SystemClock.elapsedRealtime();


 /**
  *  The size of  YUV_420_888 format image
  */
    private int mFrameDataWidth;
    private int mFrameDataHeight;


    /**
     * image rotation, indicating the rotation from the upright orientation
     */
    private int  mFrameDataOrientation;


 /**
  * constractor
  */
    public Camera2FrameProcessor(Detector<?> detector, boolean is_quarter) {
            mDetector = detector;
            isQuarterSize = is_quarter;
    }


/**
 * setImageSize
  */
    public void setImageSize(int width,  int height ) {
        mFrameDataWidth = width;
        mFrameDataHeight = height;
    }


/**
  * setSensorOrientation
  */
    public void setSensorOrientation(int orientation) {
         mFrameDataOrientation = getDetectorOrientation(orientation);
    }


 /**
  * Releases the underlying receiver.  
  * This is only safe to do after the associated thread
  */
    public void release( Thread processingThread ) {
        if ( processingThread == null ) return;
        if ( mDetector == null ) return;
        if (processingThread.getState() == Thread.State.TERMINATED) {
                mDetector.release();
                mDetector = null;
        }
    }


/**
 * Marks the runnable as active/not active.  
  */
    public void setActive(boolean active) {
            log_d("setActive " + active);
            synchronized (mLock) {
                mActive = active;
                mLock.notifyAll();
            }
    }


/**
 * Sets the frame data received from the camera.
  */
    public void setNextImage(Image image ) {
        //log_d("setNextImage");
        byte[] data = convertYUV420888ToNV21(image);
        setNextFrame(data);
    }


/**
 * Sets the frame data received from the camera.
  */
    private void setNextFrame(byte[] data) {
            synchronized (mLock) {
                if (mPendingFrameData != null) {
                    mPendingFrameData = null;
                }

                // Timestamp and frame ID are maintained here, which will give downstream code some
                // idea of the timing of frames received and when frames were dropped along the way.
                mPendingTimeMillis = SystemClock.elapsedRealtime() - mStartTimeMillis;
                mPendingFrameId++;
                mPendingFrameData = data;
                // Notify the processor thread if it is waiting on the next frame (see below).
                mLock.notifyAll();
            } // synchronized
    } // setNextFrame


/**
  * As long as the processing thread is active, 
  * this executes detection on frames continuously.  
  */
    @Override
    public void run() {
            Frame outputFrame;

            while (true) {
                synchronized (mLock) {
                    while (mActive && (mPendingFrameData == null)) {
                        try {
                            // Wait for the next frame to be received from the camera, since we
                            // don't have it yet.
                            mLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    } // while

                    if (!mActive) {
                        // Exit the loop once this camera source is stopped or released.  We check
                        // this here, immediately after the wait() above, to handle the case where
                        // setActive(false) had been called, triggering the termination of this
                        // loop.
                        return;
                    } 

                    outputFrame = buildFrame(mPendingFrameId, mPendingTimeMillis, mPendingFrameData);

                    // We need to clear mPendingFrameData to ensure that this buffer isn't
                    // recycled back to the camera before we are done using that data.
                    mPendingFrameData = null;

                } // synchronized

                // The code below needs to run outside of synchronization, because this will allow
                // the camera to add pending frame(s) while we are running detection on the current
                // frame.
                try {
                        mDetector.receiveFrame(outputFrame);
                } catch (RuntimeException e) {
                        e.printStackTrace();
                }
            } // while
    } // run


/**
 * build a Frame to send to the Detector
 * reduce to quarter size, when specified
  */
    private  Frame buildFrame(int frameId, long timestampMillis, byte[] frameData) {
        //log_d("buildFrame");
            ByteBuffer byteBuffer = ByteBuffer.wrap(frameData);
            int imageDataWidth = mFrameDataWidth;
            int imageDataHeight = mFrameDataHeight;

            if (isQuarterSize) {
                // reduce to quarter size 
                byte[] imageData = frameData = quarterNV21(frameData, mFrameDataWidth, mFrameDataHeight) ;
                byteBuffer = ByteBuffer.wrap(imageData);
                imageDataWidth = mFrameDataWidth /SCALE_QUARTER ;
                imageDataHeight = mFrameDataHeight /SCALE_QUARTER ;
            }

            Frame frame = new Frame.Builder()
                        .setImageData(byteBuffer, imageDataWidth,  imageDataHeight, ImageFormat.NV21)
                        .setId(frameId)
                        .setTimestampMillis(timestampMillis)
                        .setRotation( mFrameDataOrientation)
                        .build();

            return frame;
    }


/**
 * getDetectorOrientation
 */ 
    private  int getDetectorOrientation(int sensorOrientation) {
            int orientation = Frame.ROTATION_90;
            switch (sensorOrientation) {
                case 0:
                    orientation = Frame.ROTATION_0;
                    break;
                case 90:
                    orientation = Frame.ROTATION_90;
                    break;
                case 180:
                    orientation = Frame.ROTATION_180;
                    break;
                case 270:
                    orientation = Frame.ROTATION_270;
                    break;
                case 360:
                    orientation = Frame.ROTATION_0;
                    break;
                default:
                    break;
            }
            return orientation;
    } 


/**
 * convertYUV420888ToNV21
 */ 
    private byte[] convertYUV420888ToNV21(Image imgYUV420) {
        // Converting YUV_420_888 data to NV21.
        byte[] data;
        // Y
        ByteBuffer buffer0 = imgYUV420.getPlanes()[0].getBuffer();
        // V
        ByteBuffer buffer2 = imgYUV420.getPlanes()[2].getBuffer();
        int buffer0_size = buffer0.remaining();
        int buffer2_size = buffer2.remaining();
        int buffer_size =  buffer0_size + buffer2_size;
        data = new byte[buffer_size];
        buffer0.get(data, 0, buffer0_size);
        buffer2.get(data, buffer0_size, buffer2_size);
        return data;
} // convertYUV420888ToNV21


/**
 * reduce to quarter size the NV21 frame
 * in order to prevent CPU overload
 */ 
    private byte[] quarterNV21(byte[] data, int width, int height) {

        int size = (int)((width/SCALE_QUARTER) * (height/SCALE_QUARTER));
        byte[] yuv = new byte[size];
        int i = 0;
        int index = 0;
        int max_index = data.length;

        // thin down to a quarter
        for (int y = 0; y < height; y += SCALE_QUARTER) {
            for (int x = 0; x < width; x += SCALE_QUARTER) {
                index = y * width + x;
                if (( i < size )&&( index < max_index )) {
                    yuv[i] = data[index];
                    i++;
                }
            } // for
        } // for

        return yuv;
    } // quarterNV21


/**
 * write into logcat
 */ 
    private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
    } // log_d

} // class Camera2FrameProcessor
