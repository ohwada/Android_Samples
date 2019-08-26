/**
 * Screen Capture Sample
 * show ScreenShot with Bitmap
 * save ScreenShot
 * 2019-02-01 K.OHWADA
 */package jp.ohwada.android.screencapture2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.nio.ByteBuffer;


/**
 * class MainActivity
 * original : https://github.com/TechBooster/AndroidSamples/tree/master/ScreenCapture
 */
public class MainActivity extends Activity {

    // debug
	  private final static boolean D = true;
      private final static String TAG = "ScreenCapture";
     private final static String TAG_SUB = "MainActivity";

/**
 * Request code
 */
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1;

    // UI
    private   ImageView  mImageViewSCreenshot;

    // utility
    private  ScreenCapture  mScreenCapture;
    private  ImageUtil mImageUtil;
    private  FileUtil mFileUtil;


/**
 * ImageReader for screenshot
 */
    private ImageReader mImageReader; 


/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log_d("onCreate");
        setContentView(R.layout.activity_main);

        // UI
        mImageViewSCreenshot = (ImageView) findViewById(R.id.ImageView_screenshot);

        Button btnTake = (Button) findViewById(R.id.Button_take);
        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeScreenshot();
            }
        }); // btnTake

        Button btnClear = (Button) findViewById(R.id.Button_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearScreenshot();
            }
        }); // btnClear

        // utility
        mScreenCapture = new ScreenCapture(this);
        mImageUtil = new ImageUtil(this);
        mFileUtil = new FileUtil(this);


    }


/**
 * onResume
 */
    @Override
    protected void onResume() {
        super.onResume();
        mScreenCapture.requestPermissionIfNotGranted( 
            REQUEST_CODE_SCREEN_CAPTURE);
    }


/**
 * onPause
 */
    @Override
    protected void onPause() {
        super.onPause();
        mScreenCapture.stopScreenCapture();
    }


/**
 * onDestroy
 */
    @Override
    public void onDestroy() {
        super.onDestroy();
        log_d("onDestroy");
        mScreenCapture.tearDownMediaProjection();
    }


/**
 * onActivityResult
 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        log_d("onActivityResult");
        if (REQUEST_CODE_SCREEN_CAPTURE != requestCode) {
            return;
        }

        boolean ret = mScreenCapture.setResultData(resultCode, data);

        if (!ret) {
                log_d("User cancelled");
                showToast( R.string.msg_user_cancelled );
                return;
        }

        startScreenCapture();
}

/**
 * startScreenCapture
 */
private void startScreenCapture() {

    log_d("startScreenCapture");
    mImageReader = mImageUtil.createImageReader();
    mScreenCapture.setUpMediaProjection();
    mScreenCapture.setUpVirtualDisplay(mImageReader);
    showToast("startScreenCapture");
}


/**
 * takeScreenshot
 */
private void takeScreenshot() {
                log_d("takeScreenshot");
        if( mImageReader == null) {
                log_d("NOT ready ImageReader");
                showToast("NOT ready ImageReader");
                return;
        }
        Bitmap bitmap = mImageUtil.getScreenshot(mImageReader);
        if( bitmap == null) {
                showToast("NOT get Bitmap");
            return;
        }
        mImageViewSCreenshot.setImageBitmap(bitmap);
        mFileUtil.saveScreenshot(bitmap);
}


/**
 * clearScreenshot
 */
private void clearScreenshot() {
        mImageViewSCreenshot.setImageBitmap(null);
}


/**
 * showToast
 */
private void showToast( int res_id ) {
		ToastMaster.makeText( this, res_id, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * showToast
 */
private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * write into logcat
 */ 
private void  log_d(String msg) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} //  log_d


} // class MainActivity
