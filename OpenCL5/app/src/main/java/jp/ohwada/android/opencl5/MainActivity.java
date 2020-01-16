/**
 * OpenCL Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.opencl5;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.android.Utils;

import java.io.File;


/**
  * class MainActivity
 */
public class MainActivity extends Activity {

    // debug
    private final static String TAG = "MainActivity";


    private final static String CL_FILE_NAME 
        = "gaussian_filter.cl";

    private final static String IMAGE_FILE_NAME 
        = "flower.png";

    private TextView mTextViewMessage;

    private ImageView mIageViewOutput;

    private File mClProgramFile;

/** 
 *  for JNI
 */
    private Mat mMatInput;
    private Mat mMatOutput;


/** 
 *  Native call
 */
    public native int NativeImageFilter(String programFilePath, long matAddrInput, long matAddrOutput );


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

                    setupImageFilter();
                    //mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }

    }; // BaseLoaderCallback


/**
 * setupImageFilter
 */ 
private void setupImageFilter() {

        // Load native library after(!) OpenCV initialization
        System.loadLibrary("ImageFilter2D");

         mClProgramFile = AssetFile.getFileInExternalFilesDir(this, CL_FILE_NAME);

        Bitmap bitmap = 
            AssetFile.readImage(this, IMAGE_FILE_NAME);

       int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        log_d("setupImageFilter: width=" + width + " , height=" + height);

        mMatInput = new Mat(height, width, CvType.CV_8UC4);
        mMatOutput = new Mat(height, width, CvType.CV_8UC4);

        try {
                Utils.bitmapToMat(bitmap, mMatInput);
        } catch(Exception ex) {
                ex.printStackTrace();
        }

        mIageViewOutput.setImageBitmap(bitmap);

} 


/**
  * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTextViewMessage = (TextView) findViewById(R.id.textView);
        mTextViewMessage.setTextSize(20);
        mTextViewMessage.setTextColor(Color.RED);

        mIageViewOutput = (ImageView)findViewById(R.id.imageView);

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
    }


/**
  * onTouchEvent
 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int ret = NativeImageFilter(
            mClProgramFile.toString(), 
            mMatInput.getNativeObjAddr(), 
            mMatOutput.getNativeObjAddr() );

        if(ret != 0) {
                String msg = "OpenCL Failed: "+ ret;
                log_d(msg);
                showMessage(msg);
                showToast_onUI(msg);
                return true;
        }

        Bitmap bitmap = matToBitmap(mMatOutput);
        showImage(bitmap);

        showToast_onUI("Successful");

        return true;
    }


/**
  * matToBitmap
 */
private Bitmap matToBitmap(Mat mat) {

        int width = mat.width();
        int height = mat.height();

        log_d("matToBitmap:width=" + width + " , height=" + height);

        Bitmap bitmap =  Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        try {
                    Utils.matToBitmap(mat, bitmap);
        } catch(Exception ex) {
                    ex.printStackTrace();
        }

        return bitmap;
}


/**
  * showImage
 */
private void showImage(final Bitmap bitmap) {

mIageViewOutput.post(new Runnable() {
        @Override
        public void run() {
             log_d("setImageBitmap and invalidate");
            mIageViewOutput.setImageBitmap(bitmap);
            mIageViewOutput.invalidate();
        }
    }); // Runnable
}


/**
  * showMessage
 */
private void showMessage(final String msg) {

mTextViewMessage.post(new Runnable() {
        @Override
        public void run() {
            mTextViewMessage.setText(msg);
        }
    }); // Runnable
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
