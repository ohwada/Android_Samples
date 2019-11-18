/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/face-detection
 */
package jp.ohwada.android.opencv49;


import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;


/** 
 *  class DetectionBasedTracker
 */
public class DetectionBasedTracker
{

/** 
 *  constractor
 */
    public DetectionBasedTracker(String cascadeName, int minFaceSize) {
        mNativeObj = nativeCreateObject(cascadeName, minFaceSize);
    }


/** 
 *  start
 */
    public void start() {
        nativeStart(mNativeObj);
    }


/** 
 *  stop
 */
    public void stop() {
        nativeStop(mNativeObj);
    }


/** 
 *  setMinFaceSize
 */
    public void setMinFaceSize(int size) {
        nativeSetFaceSize(mNativeObj, size);
    }


/** 
 *  detect
 */
    public void detect(Mat imageGray, MatOfRect faces) {
        nativeDetect(mNativeObj, imageGray.getNativeObjAddr(), faces.getNativeObjAddr());
    }


/** 
 *  release
 */
    public void release() {
        nativeDestroyObject(mNativeObj);
        mNativeObj = 0;
    }

    private long mNativeObj = 0;


/**
 *  external link to the native code
 */
    private static native long nativeCreateObject(String cascadeName, int minFaceSize);
    private static native void nativeDestroyObject(long thiz);
    private static native void nativeStart(long thiz);
    private static native void nativeStop(long thiz);
    private static native void nativeSetFaceSize(long thiz, int size);
    private static native void nativeDetect(long thiz, long inputImage, long faces);

} // class DetectionBasedTracker

