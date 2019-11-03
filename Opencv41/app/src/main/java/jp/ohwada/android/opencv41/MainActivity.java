/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.opencv41;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;


/** 
 *  class MainActivity
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MainActivity";

    private final static String LF = "\n";

    private TextView  mTextView1;


/** 
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView1 = (TextView) findViewById(R.id.TextView_1);

}


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();

        if(OpenCVLoader.initDebug()){
            showSuccessfully();
        } else {
            showFailed();
        }

} // onResume


/**
 * showSuccessfully()
 */ 
private void showSuccessfully() {
            String msg = "load OpenCV Successfully" + LF;
            msg += "version " + OpenCVLoader.OPENCV_VERSION + LF;
            mTextView1.setText(msg);
            mTextView1.setTextColor(Color.BLUE);
            mTextView1.setTextSize(20);
            log_d(msg);
}


/**
 * showFailed()
 */ 
private void showFailed() {
            String msg = "load OpenCV Failed";
            mTextView1.setText(msg);
            mTextView1.setTextColor(Color.RED);
            log_d(msg);
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class MainActivity
