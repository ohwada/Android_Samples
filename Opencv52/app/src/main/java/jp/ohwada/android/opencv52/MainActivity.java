/**
 * OpenCV Sample
 * mobilenet-objdetect
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/mobilenet-objdetect
 */
package jp.ohwada.android.opencv52;


// reference
//How to run deep networks on Android 
// https://docs.opencv.org/3.4/d0/d6c/tutorial_dnn_android.html

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.dnn.Dnn;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;


/** 
 *  class MainActivity
 */
//public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {
public class MainActivity extends CameraActivity implements CvCameraViewListener2 {

    // debug
    private final static String TAG = "MainActivity";

    // prototxt file with text description of the network architecture.
     private final static String FILE_NAME_PROTO = "MobileNetSSD_deploy.prototxt";

    // caffemodel file with learned network
     private final static String FILE_NAME_MODEL = "MobileNetSSD_deploy.caffemodel";

    private static final String[] CLASS_NAMES = {"background",
            "aeroplane", "bicycle", "bird", "boat",
            "bottle", "bus", "car", "cat", "chair",
            "cow", "diningtable", "dog", "horse",
            "motorbike", "person", "pottedplant",
            "sheep", "sofa", "train", "tvmonitor"};

    // Neural Network
    private Net mNet;

    private CameraBridgeViewBase mOpenCvCameraView;


/** 
 *  BaseLoaderCallback
 *  initialize OpenCV manager
 */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
/** 
 *  onManagerConnected
 */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    log_d("OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }

    }; // BaseLoaderCallback


/**
 * onResume
 */ 
    @Override
    public void onResume() {
        super.onResume();
        // OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);

        if (!OpenCVLoader.initDebug()) {
            log_d("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            log_d("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


    }


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set up camera listener.
        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.cameraView);

        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
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
 *  load a network
 */
    public void onCameraViewStarted(int width, int height) {

        String proto = getPath( FILE_NAME_PROTO, this);
        String weights = getPath(FILE_NAME_MODEL, this);
        log_d("proto: " + proto);
        log_d("weights: " + weights);

        // when YOU forget to put a file in an Assets folder
        String msg = "";
        if( (proto == null) || proto.isEmpty() ) {
            msg = "can not get path: " + FILE_NAME_PROTO;
            log_d(msg);
            showToast( msg);
            return;
        }
        if( (weights == null) || weights.isEmpty() ) {
            msg = "can not get path: " + FILE_NAME_MODEL;
            log_d(msg);
            showToast( msg);
            return;
        }

        // read Network Model
        mNet = Dnn.readNetFromCaffe(proto, weights);
        log_d("Network loaded successfully");
    }


/** 
 *  onCameraFrame
 */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        final int IN_WIDTH = 300;
        final int IN_HEIGHT = 300;
        final double IN_SCALE_FACTOR = 0.007843;
        final double MEAN_R_VAL = 127.5;
         final double MEAN_G_VAL = 127.5;
        final double MEAN_B_VAL = 127.5;
        final double THRESHOLD = 0.2;

        // get a new frame
        Mat frame = inputFrame.rgba();
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

        // skip if net is not initialized Network
        if(mNet == null) {
                log_d("skip: not initialized Network");
                return frame;
        }

        // create Forward Image Blob through network.
        Size blobSize = new Size(IN_WIDTH, IN_HEIGHT);
        Scalar blobScalar = new Scalar(MEAN_R_VAL, MEAN_G_VAL, MEAN_B_VAL);
        Mat blob = Dnn.blobFromImage(frame,
                IN_SCALE_FACTOR,
                blobSize,
                blobScalar, 
                /*swapRB*/false, 
                /*crop*/false);
           mNet.setInput(blob);

        // run forward pass to compute output
        Mat detections = mNet.forward();

        int cols = frame.cols();
        int rows = frame.rows();

        detections = detections.reshape(1, (int)detections.total() / 7);

        for (int i = 0; i < detections.rows(); ++i) {
            double confidence = detections.get(i, 2)[0];
            if (confidence > THRESHOLD) {
                int classId = (int)detections.get(i, 1)[0];
                String className = CLASS_NAMES[classId];

                int left   = (int)(detections.get(i, 3)[0] * cols);
                int top    = (int)(detections.get(i, 4)[0] * rows);
                int right  = (int)(detections.get(i, 5)[0] * cols);
                int bottom = (int)(detections.get(i, 6)[0] * rows);

                 drawDetection(frame, className,  confidence, left,  top, right,  bottom);

                if (i==0) {
                        showToast_onUI(className);
                }

            }
        }
        return frame;
    }


/** 
 *  drawDetection
 */
private void drawDetection(Mat frame, String className,  double confidence, int left,  int top, int right,  int bottom) {

                // Background(White)
                Scalar BG_COLOR = new Scalar(255, 255, 255);

                // Text(Black)
                Scalar TEXT_COLOR = new Scalar(0, 0, 0);

                // Rectangle(Green)
                Scalar RECT_COLOR = new Scalar(0, 255, 0);
                int RECT_THICKNESS = 2;

                double FONT_SCALE = 0.5;
                int FONT_THICKNESS = 1;

                // draw Rectangle around detected object.
                Imgproc.rectangle(frame, new Point(left, top), new Point(right, bottom),
                                  RECT_COLOR, RECT_THICKNESS);

                String label = className + ": " + confidence;

                int[] baseLine = new int[1];
                Size labelSize = Imgproc.getTextSize(label, Imgproc.FONT_HERSHEY_SIMPLEX, FONT_SCALE, FONT_THICKNESS, baseLine);

                // draw Background for label. 
                Imgproc.rectangle(frame, new Point(left, top - labelSize.height),
                                  new Point(left + labelSize.width, top + baseLine[0]),
                                  BG_COLOR, Imgproc.FILLED);
                // write label (class name and confidence)
                Imgproc.putText(frame, label, new Point(left, top),
                        Imgproc.FONT_HERSHEY_SIMPLEX, FONT_SCALE, TEXT_COLOR);

                log_d("label: " + label);
}


/** 
 *  BaseLoaderCallback
 */
    public void onCameraViewStopped() {
        // nop
    }


/** 
 *  getPath
 *  copy File to storage and return file path
 */
    private  String getPath(String fileName, Context context) {

        File outFile = getOutputFileInFilesDir(fileName, context);
        if(outFile == null) return "";
        boolean is_error = false;

        try {
            InputStream is = getAssetsInputStream(fileName, context);
            FileOutputStream os = new FileOutputStream(outFile);
            boolean ret = copyStream(is, os);
            if(!ret) is_error = true;
            if(is != null) is.close();
            if(os != null) os.close();
        } catch (IOException ex) {
            is_error = true;
            ex.printStackTrace();
        }

           // Return a path to file which may be read in common way.
            String path = is_error ? "": outFile.getAbsolutePath();
            return path;
    }


/** 
 *  getAssetsInputStream
 */
private InputStream getAssetsInputStream(String fileName, Context context) {

    AssetManager assetManager = context.getAssets();
    InputStream inputStream = null;
    try {
            inputStream = assetManager.open(fileName);
    } catch (IOException ex) {
            ex.printStackTrace();
    }
    return inputStream;
}


/** 
 *  getOutputFileInFilesDir
 */
private File getOutputFileInFilesDir(String fileName, Context context) {
    File dir = context.getFilesDir();
    File outFile = new File(dir, fileName);
    return outFile;
}


/** 
 *  copyStream
 */
private boolean copyStream( InputStream is, OutputStream os) {
        if(is == null) return false;
        if(os == null) return false;
        boolean is_error = false;
        try {
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            os.write(data);
        } catch (IOException ex) {
            is_error = true;
            ex.printStackTrace();
        }
        return ! is_error;
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
      ToastMaster.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	   Log.d( TAG,  msg );
} 


} // class MainActivity
