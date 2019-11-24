/**
 * OpenCV Sample
 * Cat Face Detection
 * 2019-10-01 K.OHWADA
 * reference : https://github.com/opencv/opencv/tree/master/samples/android/face-detection
 */
package jp.ohwada.android.opencv51;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;


/** 
 *  class MainActivity
 */
public class MainActivity extends CameraActivity implements CvCameraViewListener2 {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MainActivity";


/** 
 *  Cascade File
 *  https://github.com/opencv/opencv/tree/master/data/haarcascades
 */
    private final static int RES_ID_CASCADE_FILE = R.raw.haarcascade_frontalcatface;

    private final static String CASCADE_DIR_NAME = "cascade";

    private final static String CASCADE_FILE_NAME = "haarcascade_frontalcatface.xm";


    // green
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);

    private final static int FACE_RECT_THICKNESS = 3;


	private final static int COPY_BUF_SIZE = 4096;

 	private final static int COPY_EOF = -1;


    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;


    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    private CameraBridgeViewBase   mOpenCvCameraView;


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
                    log_d("OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    // System.loadLibrary("detection_based_tracker");
                    setupDetector();
                    mOpenCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            } // switch
        } // onManagerConnected

    }; // BaseLoaderCallback


/** 
 *  setupDetector
 */
private void setupDetector() {

        // load cascade file from application resources
        File cascadeDir = createCascadeDir(CASCADE_DIR_NAME); 
        File cascadeFile = createCascadeFile(cascadeDir, CASCADE_FILE_NAME); 

        InputStream is = openRawResource(RES_ID_CASCADE_FILE);

        FileOutputStream os = getFileOutputStream(cascadeFile);
        copyStream(is, os);

        setupJavaDetector(cascadeFile);

        cascadeDir.delete();
}


/** 
 *  createCascadeDir
 */
private File createCascadeDir(String dirName) {
                File cascadeDir = getDir(dirName, Context.MODE_PRIVATE);
        return cascadeDir;
}


/** 
 *  createCascadeFile
 */
private File createCascadeFile(File cascadeDir, String fileName) {
                File cascadeFile = new File(cascadeDir, fileName);
            return  cascadeFile;
}


/** 
 *  openRawResource
 */
private InputStream openRawResource(int res_d) {
            InputStream is = getResources().openRawResource(res_d);
            return is;
}


/** 
 *  getFileOutputStream
 */
private FileOutputStream getFileOutputStream(File cascadeFile) {
        FileOutputStream fos =  null;
        try {
                fos = 
                new FileOutputStream(cascadeFile);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        return fos;
}


/** 
 *  copyStream
 */
private void copyStream(InputStream is, OutputStream os) {

        try {
                byte[] buffer = new byte[COPY_BUF_SIZE];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != COPY_EOF) {
                            os.write(buffer, 0, bytesRead);
                } // while

        } catch (IOException e) {
                e.printStackTrace();
        }

        try {
                is.close();
                os.close();
        } catch (IOException e) {
                        // nop
        }

}


/** 
 *  setupJavaDetector
 */
private void setupJavaDetector(File cascadeFile) {
        String cascadeFilePath = cascadeFile.getAbsolutePath();
        mJavaDetector = new CascadeClassifier(cascadeFilePath);
        if (mJavaDetector.empty()) {
                log_d( "Failed to load cascade classifier");
                mJavaDetector = null;
        } else {
                log_d( "Loaded cascade classifier from " + cascadeFilePath);
        }
}


/** 
 *  constractor
 */
    //public FdActivity() {
    public MainActivity() {
        log_d( "Instantiated new " + this.getClass());
    }


/** 
 *  onCreate
 *  Called when the activity is first created.
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        log_d("called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
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
            log_d( "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            log_d( "OpenCV library found inside package. Using it!");
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
        mOpenCvCameraView.disableView();
    }


/** 
 *  onCameraViewStarted
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
    }


/** 
 *  onCameraFrame
 *  detect cat faces
 *  and draw rectangle on faces
 */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        MatOfRect faces = new MatOfRect();

            if (mJavaDetector != null) {
                // detect cat faces
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
            }

        // draw rectangle on faces
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, FACE_RECT_THICKNESS);

        return mRgba;
    }


/** 
 *  setMinFaceSize
 */
    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} // class MainActivity
