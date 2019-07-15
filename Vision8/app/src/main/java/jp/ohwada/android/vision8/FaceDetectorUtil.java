 /**
 * Vision Sample
 * FaceDetectorUtill
 * 2019-02-01 K.OHWADA 
 */
package jp.ohwada.android.vision8;

 import android.content.Context;
 import android.content.Intent;
 import android.content.IntentFilter;
 import android.content.res.Resources;
 import android.graphics.Bitmap;
 import android.graphics.BitmapFactory;
 import android.util.SparseArray;
import android.util.Log;

 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.InputStream;


import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


/** 
 * class FaceDetectorUtill
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/photo-demo/app/src/main/java/com/google/android/gms/samples/vision/face
 */
 public class FaceDetectorUtil {



	// dubug
	private final static boolean D = true; 
	private final static String TAG = "vision";
	private final static String TAG_SUB = "FaceDetectorUtill";

/** 
 * Context
 */
    private Context mContext;

/** 
 * Resources
 */
    private Resources mResources;


/** 
 * Detector
 */
    private Detector<Face> mFaceDetector;



/** 
 * constractor
 */
public  FaceDetectorUtil(Context context) {
    mContext = context;
    mResources  = context.getResources();
} // FaceDetectorUtil


/** 
 * prepareDetector
 */
public boolean prepareDetector() {

    mFaceDetector = getSafeFaceDetector();
    boolean ret = false;
    if (mFaceDetector != null) {
            ret = true;
    }
    return ret;

} // prepare


/** 
 * releaseDetector
 */
public  void releaseDetector() {
    if (mFaceDetector != null ) {
        mFaceDetector.release();
    }
} // release


/** 
 * getVisionFaces
 */
public  SparseArray<Face> getVisionFaces(Bitmap bitmap) {
        Frame frame = getFrame( bitmap);
        SparseArray<Face> faces = mFaceDetector.detect(frame);
        return faces;

} // getVisionFaces


/** 
 * getErrorMsg
 */
public String getErrorMsg() {
            int id = R.string.msg_not_available;
            if ( hasLowStorage() ) {
                   id = R.string.msg_low_storage_error;
            }
    return mResources.getString(id);
} // getErrorMsg


/** 
 * hasLowStorage
 */
private boolean hasLowStorage() {
            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = mContext.registerReceiver(null, lowstorageFilter) != null;
    return hasLowStorage;
} // hasLowStorage


/** 
 * getFrame
 */
private Frame getFrame(Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        return frame;
} // getFrame


/** 
 * getSafeFaceDetector
 */
private Detector<Face> getSafeFaceDetector() {
    FaceDetector detector = getFaceDetector();
    mFaceDetector = detector;
    Detector<Face> safeDetector = new SafeFaceDetector(detector);
    if (safeDetector.isOperational()) {
            return safeDetector;
    }
    String msg = getErrorMsg();
    log_d(msg);
    return null;
}


/**
 * getFaceDetector
 */	
private FaceDetector getFaceDetector() {

        FaceDetector detector = new FaceDetector.Builder(mContext)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        return detector;

} // getFaceDetector


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class FaceDetectorUtill





