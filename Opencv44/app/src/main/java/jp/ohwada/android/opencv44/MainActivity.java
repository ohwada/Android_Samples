/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/15-puzzle
 */
package jp.ohwada.android.opencv44;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;

//import org.opencv.android.JavaCameraView;
import org.opencv.android.JavaCamera2View;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Collections;
import java.util.List;

/** 
 *  class MainActivity
 */
//public class Puzzle15Activity extends CameraActivity implements CvCameraViewListener, View.OnTouchListener {
public class MainActivity extends CameraActivity implements CvCameraViewListener, View.OnTouchListener {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MainActivity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private Puzzle15Processor    mPuzzle15;
    private MenuItem             mItemHideNumbers;
    private MenuItem             mItemStartNewGame;


    private int                  mGameWidth;
    private int                  mGameHeight;


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

                    /* Now enable camera view to start receiving frames */
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
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
 *  onCreate
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        log_d("Creating and setting view");
        mOpenCvCameraView = (CameraBridgeViewBase) new JavaCamera2View(this, -1);
        setContentView(mOpenCvCameraView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mPuzzle15 = new Puzzle15Processor();
        mPuzzle15.prepareNewGame();
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
 *  onCreateOptionsMenu
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        log_d("called onCreateOptionsMenu");
        mItemHideNumbers = menu.add("Show/hide tile numbers");
        mItemStartNewGame = menu.add("Start new game");
        return true;
    }

/** 
 *  onDestroy
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        log_d( "Menu Item selected " + item);
        if (item == mItemStartNewGame) {
            /* We need to start new game */
            mPuzzle15.prepareNewGame();
        } else if (item == mItemHideNumbers) {
            /* We need to enable or disable drawing of the tile numbers */
            mPuzzle15.toggleTileNumbers();
        }
        return true;
    }


/** 
 *  onCameraViewStarted
 */
    public void onCameraViewStarted(int width, int height) {
        mGameWidth = width;
        mGameHeight = height;
        mPuzzle15.prepareGameSize(width, height);
    }


/** 
 *  onCameraViewStopped
 */
    public void onCameraViewStopped() {
        // nop
    }


/** 
 *  onTouch
 */
    public boolean onTouch(View view, MotionEvent event) {
        int xpos, ypos;

        xpos = (view.getWidth() - mGameWidth) / 2;
        xpos = (int)event.getX() - xpos;

        ypos = (view.getHeight() - mGameHeight) / 2;
        ypos = (int)event.getY() - ypos;

        if (xpos >=0 && xpos <= mGameWidth && ypos >=0  && ypos <= mGameHeight) {
            /* click is inside the picture. Deliver this event to processor */
            mPuzzle15.deliverTouchEvent(xpos, ypos);
        }

        return false;
    }


/** 
 *  onCameraFrame
 */
    public Mat onCameraFrame(Mat inputFrame) {
        return mPuzzle15.puzzleFrame(inputFrame);
    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
}


} // class MainActivity
