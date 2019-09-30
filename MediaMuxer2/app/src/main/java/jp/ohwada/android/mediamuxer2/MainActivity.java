/**
 * MediaMuxer Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.mediamuxer2;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * class MainActivity
 * test EncodeVideo To Mp4
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "MediaMuxer";
    	private final static String TAG_SUB = "MainActivity";


    // arbitrary but popular values
    // VGA at 6Mbps
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static final int BIT_RATE = 6000000;


/**
 * Camera permssion
 */ 
    private CameraPerm mCameraPerm;


/**
  * onCreate
  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            Button btnStart = (Button) findViewById(R.id.Button_start);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    testMediaMuxer();
                }
            }); // btnStart

// util
    mCameraPerm = new CameraPerm(this);

    }

/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        mCameraPerm.requestCameraPermissions();
}


/**
 * onRequestPermissionsResult
 */ 
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {

        log_d("onRequestPermissionsResult: " + requestCode);
                mCameraPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
                // nop
} // onRequestPermissionsResult


/**
 * testMediaMuxer
 */
private void testMediaMuxer() {
    CameraToMpegTest muxTest = new CameraToMpegTest(this,
    new CameraToMpegTest.Callback() {
                @Override
                public void onFinish(String reason) {
                    log_d("onFinish: " + reason);
                    showToast( "Finished" );
                }
            }); // Callback

    try {
        muxTest.testEncodeCameraToMp4(WIDTH, HEIGHT, BIT_RATE);
    } catch (Throwable th) {
               // nop
    }
}


/**
 * showToast
 */
private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} //class MainActivity
