/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/camera-calibration
 */
package jp.ohwada.android.opencv47;


// This sample is based on "Camera calibration With OpenCV" tutorial:
// https://docs.opencv.org/3.4/d4/d94/tutorial_camera_calibration.html
//
// It uses standard OpenCV asymmetric circles grid pattern 11x4:
// https://github.com/opencv/opencv/blob/3.4/doc/acircles_pattern.png
// The results are the camera matrix and 5 distortion coefficients.
//
// Tap on highlighted pattern to capture pattern corners for calibration.
// Move pattern along the whole screen and capture data.
//
// When you've captured necessary amount of pattern corners (usually ~20 are enough),
// press "Calibrate" button for performing camera calibration.


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;


/** 
 *  class MainActivity
 *  
 */
//public class CameraCalibrationActivity extends CameraActivity implements CvCameraViewListener2, OnTouchListener {
public class MainActivity extends CameraActivity implements CvCameraViewListener2, OnTouchListener {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MainActivity";


    private final static int MIN_NUM_CORNERS = 2;


    private Activity mActivity;

    private CameraBridgeViewBase mOpenCvCameraView;
    private CameraCalibrator mCalibrator;
    private OnCameraFrameRender mOnCameraFrameRender;
    private Menu mMenu;
    private int mWidth;
    private int mHeight;


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
                mOpenCvCameraView.setOnTouchListener(MainActivity.this);
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
    // public CameraCalibrationActivity() {
    public MainActivity() {
       mActivity = this;
        log_d("Instantiated new " + this.getClass());
    }


/** 
 *  onCreate
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
 *  onDestroy
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }


/** 
 *  onPrepareOptionsMenu
 */
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.preview_mode).setEnabled(true);
        if (mCalibrator != null && !mCalibrator.isCalibrated()) {
            menu.findItem(R.id.preview_mode).setEnabled(false);
        }
        return true;
    }


/** 
 *  onOptionsItemSelected
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.calibration:
            procOptionCalibration();
            item.setChecked(true);
            return true;
        case R.id.undistortion:
            procOptionUndistortion();
            item.setChecked(true);
            return true;
        case R.id.comparison:
            procOptionComparison();
            item.setChecked(true);
            return true;
        case R.id.calibrate:
            procOptionCalibrate();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    } // switch

} // onOptionsItemSelected


/** 
 *  procOptionCalibration
 *  find calibration pattern and highlight pattern
 */
private void procOptionCalibration() {
            mOnCameraFrameRender =
                new OnCameraFrameRender(new CalibrationFrameRender(mCalibrator));
}


/** 
 *  procOptionUndistortion
 *  display undistorted image
 */
private void procOptionUndistortion() {
            mOnCameraFrameRender =
                new OnCameraFrameRender(new UndistortionFrameRender(mCalibrator));
}


/** 
 *  procOptionComparison
 *  display original and undistorted images side by side
 */
private void procOptionComparison() {
            mOnCameraFrameRender =
                new OnCameraFrameRender(new ComparisonFrameRender(mCalibrator, mWidth, mHeight, getResources()));
}


/** 
 *  procOptionCalibrate
 */
private void procOptionCalibrate() {

            if (mCalibrator.getCornersBufferSize() < MIN_NUM_CORNERS) {
                    showToast( R.string.more_samples );
                    return ;
            }

            mOnCameraFrameRender = new OnCameraFrameRender(new PreviewFrameRender());
            calibrationTask.execute();
}


/** 
 *  CalibrateTask
 */
private AsyncTask<Void, Void, Void> calibrationTask =
            new AsyncTask<Void, Void, Void>() {

                private Resources res;

                private ProgressDialog calibrationProgress;


/** 
 *  onPreExecute
 */
                @Override
                protected void onPreExecute() {
                    Context context = mActivity;
                    res = context.getResources();
                    calibrationProgress = new ProgressDialog(context);
                    calibrationProgress.setTitle(res.getString(R.string.calibrating));
                    calibrationProgress.setMessage(res.getString(R.string.please_wait));
                    calibrationProgress.setCancelable(false);
                    calibrationProgress.setIndeterminate(true);
                    calibrationProgress.show();
                }

/** 
 *  doInBackground
 */
                @Override
                protected Void doInBackground(Void... arg0) {
                    mCalibrator.calibrate();
                    return null;
                }

/** 
 *  onPostExecute
 */
                @Override
                protected void onPostExecute(Void result) {
                    calibrationProgress.dismiss();
                    mCalibrator.clearCorners();
                    mOnCameraFrameRender = new OnCameraFrameRender(new CalibrationFrameRender(mCalibrator));
                    String resultMessage = (mCalibrator.isCalibrated()) ?
                            res.getString(R.string.calibration_successful)  + " " + mCalibrator.getAvgReprojectionError() :
                            res.getString(R.string.calibration_unsuccessful);
                    showToast( resultMessage );
                    log_d( resultMessage );

                    if (mCalibrator.isCalibrated()) {
                        CalibrationResult.save(mActivity,
                                mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients());
                    }
                }

}; // CalibrateTask


/** 
 *  onCameraViewStarted
 */
    public void onCameraViewStarted(int width, int height) {
        if (mWidth != width || mHeight != height) {
            mWidth = width;
            mHeight = height;
            mCalibrator = new CameraCalibrator(mWidth, mHeight);
            if (CalibrationResult.tryLoad(mActivity, mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients())) {
                mCalibrator.setCalibrated();
            } else {
                if (mMenu != null && !mCalibrator.isCalibrated()) {
                    mMenu.findItem(R.id.preview_mode).setEnabled(false);
                }
            }

            mOnCameraFrameRender = new OnCameraFrameRender(new CalibrationFrameRender(mCalibrator));
        }
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
        return mOnCameraFrameRender.render(inputFrame);
    }


/** 
 *  onTouch
 */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        log_d("onTouch invoked");
        mCalibrator.addCorners();
        return false;
    }


/**
 * showToast
 */ 
private void showToast( int res_id ) {
                Toast.makeText(this, res_id, Toast.LENGTH_LONG).show();
}


/**
 * showToast
 */ 
private void showToast( String msg ) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} // class MainActivity
