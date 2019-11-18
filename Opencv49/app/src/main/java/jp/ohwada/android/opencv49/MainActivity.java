/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/face-detection
 */
package jp.ohwada.android.opencv49;


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
import android.widget.Toast;


/** 
 *  class MainActivity
 */
//public class FdActivity extends CameraActivity implements CvCameraViewListener2 {
public class MainActivity extends CameraActivity implements CvCameraViewListener2 {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MainActivity";


    // green
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);

    private final static int FACE_RECT_THICKNESS = 3;


    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

/** 
 *  Cascade File
 *  original : opencv/sdk/etc/lbpcascades
 */
    private final static int RES_ID_CASCADE_FILE = R.raw.lbpcascade_frontalface;


    private final static String CASCADE_DIR_NAME = "cascade";

    private final static String CASCADE_FILE_NAME = "lbpcascade_frontalface.xml";

	private final static int COPY_BUF_SIZE = 4096;

 	private final static int COPY_EOF = -1;


    private MenuItem               mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemType;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;
    private DetectionBasedTracker  mNativeDetector;

    private int                    mDetectorType       = JAVA_DETECTOR;

    private String[]               mDetectorName;

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
                    System.loadLibrary("detection_based_tracker");
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
        setupNativeDetector(cascadeFile);

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
 *  setupNativeDetector
 */
private void setupNativeDetector(File cascadeFile) {
        String cascadeFilePath = cascadeFile.getAbsolutePath();               
        mNativeDetector = new DetectionBasedTracker(cascadeFilePath, 0);
}


/** 
 *  constractor
 */
    //public FdActivity() {
    public MainActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

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
 *  detect faces
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
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null) {
                // detect faces
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
            }
        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null) {
                // detect faces
                mNativeDetector.detect(mGray, faces);
            }
        } else {
            log_d("Detection method is not selected!");
        }


        // draw rectangle on faces
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, FACE_RECT_THICKNESS);

        return mRgba;
    }


/** 
 *  onCreateOptionsMenu
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        log_d( "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Face size 50%");
        mItemFace40 = menu.add("Face size 40%");
        mItemFace30 = menu.add("Face size 30%");
        mItemFace20 = menu.add("Face size 20%");
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        return true;
    }


/** 
 *  onOptionsItemSelected
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        log_d("called onOptionsItemSelected; selected item: " + item);
        if (item == mItemFace50)
            setMinFaceSize(0.5f); // 50%
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);  // 40%
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);  // 30%
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);  // 20%
        else if (item == mItemType) {
            toggleDetectorType(item);
        }
        return true;
    }


/** 
 *  toggleDetectorType
 */
private void toggleDetectorType(MenuItem item) {
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            String name = mDetectorName[tmpDetectorType];
            item.setTitle(name);
            setDetectorType(tmpDetectorType);
            showToast( name );
            log_d("type= " + tmpDetectorType + ", name= " + name );
}


/** 
 *  setMinFaceSize
 */
    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }


/** 
 *  setDetectorType
 */
    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                log_d( "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                log_d( "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
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
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} // class MainActivity
