/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/tutorial-3-cameracontrol
 */
package jp.ohwada.android.opencv43;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


import org.opencv.android.JavaCameraView;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;


/** 
 *  class MyCameraView
*  TODO : change to use Camera2 API
 *  because Camera API are deprecated
 */
// public class Tutorial3View extends JavaCameraView implements PictureCallback {
public class MyCameraView extends JavaCameraView implements PictureCallback {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MyCameraView";


    //private String mPictureFileName;
    private File mOutputFile;


/** 
 *  constractor
 */
    //public Tutorial3View(Context context, AttributeSet attrs) {
    public MyCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


/** 
 *  getEffectList
 */
    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }


/** 
 *  isEffectSupported
 */
    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }


/** 
 *  getEffect
 */
    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

/** 
 *  setEffect
 */
    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
// Camera.Parameters#setColorEffect
// TODO : Deprecated in API level 21
        params.setColorEffect(effect);

        mCamera.setParameters(params);
    }


/** 
 *  getResolutionList
 */
    public List<Size> getResolutionList() {
// Camera.Parameters#getSupportedPreviewSizes
// TODO : Deprecated in API level 21
        return mCamera.getParameters().getSupportedPreviewSizes();
    }


/** 
 *  setResolution
 */
    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }


/** 
 *  getResolution
 */
    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }


/** 
 *  takePicture
 */
    //public void takePicture(final String fileName) {
    public void takePicture(File file) {
        log_d( "Taking picture");
        // this.mPictureFileName = fileName;
        this.mOutputFile = file;

        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

// Camera#takePicture
// TODO : Deprecated in API level 21
        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }


/** 
 *  onPictureTaken
 */
    @Override
// Camera.PictureCallback#onPictureTaken
// TODO : Deprecated in API level 21
    public void onPictureTaken(byte[] data, Camera camera) {
        log_d("Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        savePicture(data, mOutputFile);

    }


/**
 * savePicture
 */ 
private void savePicture(byte[] data, File file) {

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} // class MyCameraView
