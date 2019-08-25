/**
 * Screen Capture Sample
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.screencapture1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * class MainActivity
 * oroginal : https://github.com/googlesamples/android-ScreenCapture
 */
public class MainActivity extends Activity {

    // debug
	  private final static boolean D = true;
      private final static String TAG = "ScreenCapture";
     private final static String TAG_SUB = "MainActivity";


/**
 * Request code
 */
    private static final int REQUEST_MEDIA_PROJECTION = 1;

    // UI
    private SurfaceView mSurfaceView;
    private Button mButtonToggle;

    // utility
    private  ScreenCapture  mScreenCapture;


/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log_d("onCreate");
        setContentView(R.layout.activity_main);

        // UI
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);

        mButtonToggle = (Button) findViewById(R.id.Button_toggle);
            mButtonToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    procToggle();
                }
            }); // mButtonToggle

        // utility
        mScreenCapture = new ScreenCapture(this);

}


/**
 * onPause
 */
    @Override
    public void onPause() {
        super.onPause();
        log_d("onPause");
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
        if (requestCode != REQUEST_MEDIA_PROJECTION) {
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
 * procToggle
 */
private void procToggle() {

        log_d("procToggle");
        if ( mScreenCapture.isCaptureRunning() ) {
                    stopScreenCapture();
        } else {
                    startScreenCapture();
        }

}


/**
 * startScreenCapture
 */ 
private void startScreenCapture() {
    log_d("startScreenCapture");
    boolean ret = mScreenCapture.startScreenCapture(
            REQUEST_MEDIA_PROJECTION, mSurfaceView);
    if(ret) {
            mButtonToggle.setText(R.string.button_stop);
    }

}


/**
 * stopScreenCapture
 */ 
private void stopScreenCapture() {
        log_d("stopScreenCapture" );
        mScreenCapture.stopScreenCapture();
        mButtonToggle.setText(R.string.button_start);
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
