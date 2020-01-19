/**
 * OpenCV Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.opencv54;




import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.CameraGLSurfaceView;



/** 
 *  class MainActivity
 */
public class MainActivity extends CameraActivity {


    // debug
    private final static String TAG = "MainActivity";


    private MyGLSurfaceView mGLSurfaceView;


/** 
 *  BaseLoaderCallback
 */
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
/** 
 *  onManagerConnected
 */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    log_d("OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }

    }; // BaseLoaderCallback


/** 
 *  onCreate
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.surfaceView);
        mGLSurfaceView.setCameraTextureListener(mCameraTextureListener);

    }


/** 
 *  onPause
 */
    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }


/** 
 *  onResume
 */
    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            log_d( "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            log_d("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        mGLSurfaceView.onResume();
    }


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	   Log.d( TAG, msg );
} 


/**
 * CameraTextureListener
 */
CameraGLSurfaceView.CameraTextureListener
mCameraTextureListener =
new CameraGLSurfaceView.CameraTextureListener() {

    @Override
        public void onCameraViewStarted(int width, int height) {
            // nop
    }

    @Override
    public void onCameraViewStopped() {
            // nop
    }

    @Override
    public boolean onCameraTexture(int texIn, int texOut, int width, int height) {
            // preview
            return false;
    }

}; // CameraTextureListener


} // class MainActivity