/**
 * OpenCL Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.opencl6;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


import java.io.File;
import java.util.Collections;
import java.util.List;


/**
  * class MainActivity
 */
public class MainActivity extends CameraActivity implements CvCameraViewListener2 {


    // debug
    private final static String TAG = "MainActivity";


    private final static String CL_FILE_NAME 
        = "detectLine.cl";


   private CameraBridgeViewBase  mOpenCvCameraView;


    private File mClProgramFile;


/** 
 *  for JNI
 */
    private Mat mMatInput;
    private Mat mMatOutput;


/** 
 *  Native call
 */
    public native int initCL(String programFilePath, int width, int height);

    public native void releaseCL();

    public native int processFrame(long matAddrInput, long matAddrOutput );


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

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detectLine");

                    mClProgramFile = AssetFile.getFileInExternalFilesDir(MainActivity.this, CL_FILE_NAME);

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }

    }; // BaseLoaderCallback



/**
  * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }


/**
 * onResume
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
}


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        super.onPause();
        if (mMatInput != null) mMatInput.release();
        if (mMatOutput != null)mMatOutput.release();

        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }


/** 
 *  etCameraViewList
 */
    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }


/** 
 *  onCameraViewStarted
 */
    public void onCameraViewStarted(int width, int height) {

            mMatInput = new Mat(height, width, CvType.CV_8UC4);
            mMatOutput = new Mat(height, width, CvType.CV_8UC4);

            int ret = initCL( mClProgramFile.toString(), width, height );
            if(ret != 0) {
                String msg = "OpenCL Failed: ";
                log_d(msg);
               showToast_onUI(msg);
            }

    }


/** 
 *  onCameraViewStopped
 */
    public void onCameraViewStopped() {
            releaseCL();
    }


/** 
 *  onCameraFrame
 */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mMatInput = inputFrame.rgba();

        int ret = processFrame(mMatInput.getNativeObjAddr(), mMatOutput.getNativeObjAddr());
            if(ret != 0) {
                String msg = "processFrame Failed: ";
                log_d(msg);
            }

        return mMatOutput;
    }


/**
  * ShowToast on the UI thread.
 */
private void showToast_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(msg);
                }
    });
} 


/**
 * showToast
 */
private void showToast( String msg ) {
		Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
} 


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    Log.d( TAG,  msg );
} 


} // class MainActivity
