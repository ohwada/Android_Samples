/**
 * Camera2 Sample
 * camera preview using Camera2Base
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera27;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import jp.ohwada.android.camera27.util.CameraPerm;
import jp.ohwada.android.camera27.util.Camera2Base;
import jp.ohwada.android.camera27.util.ToastMaster;
import jp.ohwada.android.camera27.ui.AutoFitTextureView;

/**
 * class PreviewActivity 
 */
public class PreviewActivity extends Activity {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "PreviewActivity";

    // Camera2Source
    private Camera2Base mCamera2Base;

    // view
    private AutoFitTextureView mTextureView;

    // utility
    private CameraPerm mCameraPerm;

 

/**
 * onCreate
 */ 
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview);


    // view
    mTextureView = (AutoFitTextureView) findViewById(R.id.texture);
    mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

     // utility
    mCameraPerm = new CameraPerm(this);


} // onCreate



/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
} // onResume


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        super.onPause();
        stopCamera();
    }


/**
 * onRequestPermissionsResult
 */ 
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        mCameraPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
        startCamera();
} // onRequestPermissionsResult



/**
 * startCamera
 */ 
private void startCamera() {
        log_d("startCamera");
        if(mCameraPerm.requestCameraPermissions()) {
            log_d("NOT permit");
            return;
        }
        if(!mTextureView.isAvailable()) {
            log_d("TextureView NOT Available");
            return;
        }
        mCamera2Base = new Camera2Base(this);
        mCamera2Base.start(mTextureView);
} // startCamera


/**
 * stopCamera
 */ 
private void stopCamera() {
    log_d("stopCamera");
    if(mCamera2Base != null) {
        mCamera2Base.stop();
    }
} // stopCamera



/**
 * toast_long
 */
private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // toast_long

/**
 * write into logcat
 */ 
private void log_d(String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d



/**
 * TextureView.SurfaceTextureListener
 */ 
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            log_d(" onSurfaceTextureAvailable");
            startCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            log_d("onSurfaceTextureSizeChanged");
            // nop
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            log_d("onSurfaceTextureDestroyed");
            return true;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
            //log_d("onSurfaceTextureUpdated");
            // nop
        }

}; // TextureView.SurfaceTextureListener


} // class PreviewActivity
