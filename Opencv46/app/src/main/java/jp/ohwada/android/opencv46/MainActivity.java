/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/color-blob-detection
 */
package jp.ohwada.android.opencv46;


import java.util.Collections;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;


/** 
 *  class MainActivity
 */
//public class ColorBlobDetectionActivity extends CameraActivity implements OnTouchListener, CvCameraViewListener2 {
public class MainActivity extends CameraActivity implements OnTouchListener, CvCameraViewListener2 {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MainActivity";


    // touched rect
    private final static int RECT_HALF_WIDTH  = 4;
    private final static int RECT_HALF_HEIGHT  = 4;

    // spectrum
    private final static int SPECTRUM_WIDTH = 200;
    private final static int SPECTRUM_HEIGHT = 64;

    private final static int SPECTRUM_LABEL_ROW_START = 4;
    private final static int SPECTRUM_LABEL_COL_START = 70;


    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;

    private int CONTOUR_THICKNESS = 10;

    private CameraBridgeViewBase mOpenCvCameraView;


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
    //public ColorBlobDetectionActivity() {
    public MainActivity() {
        log_d("Instantiated new " + this.getClass());
    }


/** 
 *  onCreate
 *  Called when the activity is first created.
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        log_d( "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
 *  onCameraViewStarted
 */
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(SPECTRUM_WIDTH, SPECTRUM_HEIGHT);

        // red
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }


/** 
 *  onCameraViewStopped
 */
    public void onCameraViewStopped() {
        mRgba.release();
    }


/** 
 *  onTouch
 *  detect the touched color, when touch the screen
 *  and set color to Blob Detector
 */
    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        // touched image coordinates
        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        log_d("Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        // coordinates of 4 corners of Rectangle surrounding the touched location
        int left = x - RECT_HALF_WIDTH;
        int right = x + RECT_HALF_WIDTH;
        int top = y - RECT_HALF_HEIGHT;
        int bottom = y + RECT_HALF_HEIGHT;

        touchedRect.x = ( x >RECT_HALF_WIDTH) ? left : 0;
        touchedRect.y = (y > RECT_HALF_HEIGHT) ? top : 0;

        touchedRect.width = (right < cols) ? right - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (bottom < rows) ? bottom - touchedRect.y : rows - touchedRect.y;

        // extract the submatrix where you touch from the camera image
        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        // color of touched region
        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        // touched rgba color
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        log_d("Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);

        mIsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }


/** 
 *  onCameraFrame
 *  detect blob
 *  and draw contour on blob
 *  and draw spectrum in the upper left
 */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (mIsColorSelected) {

            // detect blob
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            log_d("Contours count: " + contours.size());

            // draw contour on blob
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR, CONTOUR_THICKNESS);

            // submatrix position in the upper left
            int rowEnd = SPECTRUM_LABEL_ROW_START + mSpectrum.rows();
            int colEnd = SPECTRUM_LABEL_COL_START + mSpectrum.cols();

            // draw spectrum in the upper left
            Mat spectrumLabel = mRgba.submat(SPECTRUM_LABEL_ROW_START, rowEnd, SPECTRUM_LABEL_COL_START, colEnd);
            mSpectrum.copyTo(spectrumLabel);
        }

        return mRgba;
    }


/** 
 *  converScalarHsv2Rgba
 */
    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} // class MainActivity
