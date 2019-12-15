/**
 * OpenCV Sample
 * QRCodeDetector
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.opencv53;



import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.QRCodeDetector;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.CvException;
import org.opencv.imgproc.Imgproc;


import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;


/** 
 *  class MainActivity
 */
public class MainActivity extends CameraActivity implements CvCameraViewListener2 {

    // debug
    private final static String TAG = "MainActivity";


    // initial value for detected QRcode quadrangle
    private final static Point[] QUAD = { 
        new Point​(0.0, 0.0) , 
        new Point​(0.0, 1.0) , 
        new Point​(1.0, 0.0), 
         new Point​(1.0, 1.0) };


    // Green
    private final static Scalar    LINE_COLOR = new Scalar(0, 255, 0, 255);
    private final static  int LINE_THICKNESS = 3;

    private CameraBridgeViewBase mOpenCvCameraView;

    private Mat mRgba;
    private Mat mGray;



    private QRCodeDetector mQRCodeDetector;

    // QRcode quadrangle points
    private MatOfPoint2f mPoints;


    private boolean isDecode = false;


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
                    setupQRCodeDetector();
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
 *  setupQRCodeDetector
 */
private void setupQRCodeDetector() {
        mQRCodeDetector = new QRCodeDetector();

        // setup buffer area
        try {
           mPoints = new MatOfPoint2f(QUAD);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
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

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

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
 *  onDestroy
 */
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


/** 
 *  onTouchEvent
 */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
    switch (motionEvent.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // enable to decode when touch the screen
            isDecode = true;
            break;
        case MotionEvent.ACTION_UP:
            // nop
            break;
        case MotionEvent.ACTION_MOVE:
            // nop
            break;
        case MotionEvent.ACTION_CANCEL:
            // nop
            break;
    } // switch
 
    return false;
}


/** 
 *  getCameraViewList
 */
    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }


/** 
 * onCameraViewStarted
 */
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

/** 
 *  onCameraViewStopped
 */
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        if ( mPoints != null){
            mPoints.release();
        }
    }


/** 
 *  onCameraFrame
 */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        mQRCodeDetector.detect(mGray, mPoints);

        drawQuadrangle();

    // skip to decode when decoded once
    // because ocure out of memory
    // when decode repeatedly
    // enable to decode
    // when touch the screen
        if (!isDecode)  {
            return mRgba;
        }

        String result = null;
        try {
            result = mQRCodeDetector.decode(mGray, mPoints);
        } catch (CvException e) {
            e.printStackTrace();
        }

        if (result != null && result.length() > 0) {

            // disable to decode
            isDecode = false;

            log_d(result);
            showToast_onUI(result);
        }

        return mRgba;

    }


/**
 * drawQuadrangle
 */ 
private void drawQuadrangle() {

        Point[] pointsArray = mPoints.toArray();
        int length = pointsArray.length;

        // return if not quad
        if(length != 4) return;

        // draw 4 lines as quadrangle
        for (int i = 0; i < length; i++) {

                // next point
                int i2 = i + 1;
                if (i == 3 ) {
                        // draw last to first, if last point
                        i2 = 0;
                }

                Point pt1 = pointsArray[i];
                Point pt2 = pointsArray[i2];

	            Imgproc.line(mRgba, pt1, pt2, LINE_COLOR, LINE_THICKNESS);
        }
}


/**
 * showToast_onUI
 */ 
private void showToast_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(msg);
                }
    }); // Runnable
}


/**
 * showToast
 */ 
private void showToast(String msg) {
      Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    Log.d( TAG,  msg );
} 


} // class MainActivity
