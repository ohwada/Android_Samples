/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/tutorial-1-camerapreview
 */
package jp.ohwada.android.opencv42;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.Collections;
import java.util.List;


/** 
 *  class MainActivity
 *  show camera preview
 */
//public class Tutorial1Activity extends CameraActivity implements CvCameraViewListener2 {
public class MainActivity extends CameraActivity implements CvCameraViewListener2 {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MainActivity";


    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;


/** 
 *  BaseLoaderCallback
 */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
/** 
 *  onManagerConnected
 */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    log_d("OpenCV loaded successfully");
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
 *  constractor
 */
    //public Tutorial1Activity() {
    public MainActivity() {
        log_d("Instantiated new " + this.getClass());
    }


/** 
 *  onCreate
 *   Called when the activity is first created.
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        log_d("called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }


/** 
 *  onPause
 */
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


/** 
 *  onResume
 */
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            log_d("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            log_d("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


/** 
 *  getCameraViewList
 */
    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }


/** 
 *  onDestroy
 */
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

/** 
 * onCameraViewStarted
 */
    public void onCameraViewStarted(int width, int height) {
        // nop
    }

/** 
 *  onCameraViewStopped
 */
    public void onCameraViewStopped() {
        // nop
    }


/** 
 *  onCameraFrame
 */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} // class MainActivity
