/**
 * OpenCV Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.opencv55;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.CameraGLSurfaceView;


/** 
 *  class MainActivity
 */
public class MainActivity extends CameraActivity {


    // debug
    private final static String TAG = "MainActivity";


    private static final int MODE_PREVIEW = 0;
    private static final int MODE_RGBA = 1;
    private static final int MODE_GRAY = 2;
    private static final int MODE_CANNY = 3;
    private static final int MODE_SEPIA = 4;
    private static final int MODE_ZOOM = 5;


    private int mMode = MODE_PREVIEW;


/** 
 *  Natve call
 */
    public native int processFrame(int tex1, int tex2, int w, int h, int mode);


    private MyGLSurfaceView mGLSurfaceView;

    private TextView mTextViewMode;


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
                    System.loadLibrary("CvProcessor");
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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.surfaceView);

        mGLSurfaceView.setCameraTextureListener(mCameraTextureListener);

        mTextViewMode = (TextView)findViewById(R.id.TextView_mode);

        mMode =MODE_PREVIEW;
    }


/** 
 *  onPause
 */
    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }


/** 
 *  onResume
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

        mGLSurfaceView.onResume();
    }


/** 
 *  onCreateOptionsMenu
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


/** 
 *  onOptionsItemSelected
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_preview:
            updateMode_onUI(R.string.action_preview);
            mMode = MODE_PREVIEW;
            return true;

        case R.id.action_rgba:
            updateMode_onUI(R.string.action_rgba);
            mMode = MODE_RGBA;
            return true;

        case R.id.action_gray:
            updateMode_onUI(R.string.action_gray);
            mMode = MODE_GRAY;
            return true;

        case R.id.action_canny:
            updateMode_onUI(R.string.action_canny);
            mMode = MODE_CANNY;
            return true;

        case R.id.action_sepia:
            updateMode_onUI(R.string.action_sepia);
            mMode = MODE_SEPIA;
            return true;

        case R.id.action_zoom:
            updateMode_onUI(R.string.action_zoom);
            mMode = MODE_ZOOM;
            return true;

        default:
            return false;

        } // switch
    } 



/**
 * updateMode_onUI
 */ 
private void updateMode_onUI(final int res_id) {
        runOnUiThread(new Runnable() {
            public void run() {
                mTextViewMode.setText(res_id);
            }
        }); // Runnable
}


/**
 * showToast_onUI
 */ 
private void showToast_onUI(final String msg) {

runOnUiThread(new Runnable() {
            public void run() {
                    showToast(msg);
            }
        }); // Runnable
}


/**
 * showToast
 */ 
private void showToast(String msg) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	   Log.d( TAG, msg );
} 


/**
 * CameraTextureListener
 */
CameraGLSurfaceView.CameraTextureListener
mCameraTextureListener =
new CameraGLSurfaceView.CameraTextureListener() {

    @Override
        public void onCameraViewStarted(int width, int height) {
                log_d("onCameraViewStarted");
    }

    @Override
    public void onCameraViewStopped() {
               log_d("onCameraViewStopped");
    }

    @Override
    public boolean onCameraTexture(int texIn, int texOut, int width, int height) {
        
        if(mMode == MODE_PREVIEW) {
            // display preview Texture
            return false;
        }

        processFrame(texIn, texOut, width, height, mMode);
        // display output Texture
        return true;

    }

}; // CameraTextureListener


} // class MainActivity