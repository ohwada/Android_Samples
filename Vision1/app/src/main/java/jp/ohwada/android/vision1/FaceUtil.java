/**
 * Vision Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.vision1;

 import android.content.Context;
 import android.content.Intent;
 import android.content.IntentFilter;
 import android.graphics.Bitmap;
 import android.graphics.BitmapFactory;
 import android.util.SparseArray;
import android.util.Log;
 import android.widget.Toast;

 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.InputStream;


import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


/** 
 * class FaceUtil
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/photo-demo/app/src/main/java/com/google/android/gms/samples/vision/face
 */
 public class FaceUtil {

	// dubug
	private final static boolean D = true; 
	private final static  String TAG = "Vision";
	private final static String TAG_SUB = "FaceUtil";

private Context mContext;

/** 
 * constractor
 */
public  FaceUtil(Context context) {
    mContext = context;
} // ImageDialog

/** 
 * getLandmarkImage
 */
public Bitmap getLandmarkImage(Bitmap bitmap_orig) {

    Detector<Face> safeDetector = getSafeFaceDetector();
    SparseArray<Face> faces = getFaces( safeDetector, bitmap_orig);

    FaceLandmark faceLandmark = new FaceLandmark(mContext);
    Bitmap bitmap_landmark = faceLandmark.transformLandmark(bitmap_orig, faces);
    return bitmap_landmark;

} // getLandmarkImage


/** 
 * getFaces
 */
private SparseArray<Face> getFaces( Detector<Face> safeDetector, Bitmap bitmap) {

        Frame frame = getFrame( bitmap);
        SparseArray<Face> faces = safeDetector.detect(frame);

        if (!safeDetector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            if ( hasLowStorage() ) {
                    toast_long(R.string.msg_low_storage_error);
                    log_d(R.string.msg_low_storage_error);
            } else {
                    toast_long(R.string.msg_not_available);
                    log_d(R.string.msg_not_available);
            }
            return null;
        }
    return faces;

 } // getFaces

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
    Detector<Face> safeDetector = new SafeFaceDetector(detector);
    return safeDetector;
} // getSafeDetector


/**
 * getFaceDetector
 */	
private FaceDetector getFaceDetector() {

        FaceDetector detector = new FaceDetector.Builder(mContext)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        return detector;

} // getFaceDetector

/**
	 * toast_long
	 */
	private void toast_long( int res_id ) {
		ToastMaster.makeText( mContext, res_id, Toast.LENGTH_LONG ).show();
	} // toast_long

/**
	 * toast_long
	 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

/**
 * write into logcat
 */ 
private void log_d( int res_id ) {
    log_d( mContext.getString(res_id) );
} // log_d

/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class FaceUtil





