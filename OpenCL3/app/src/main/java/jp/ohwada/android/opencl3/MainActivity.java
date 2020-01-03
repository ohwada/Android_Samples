/**
 * OpenCL Sample
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.opencl3;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
  * class MainActivity
 */
public class MainActivity extends Activity {

    // debug
    private final static String TAG = "MainActivity";

    private final static String LF = "\n";

    private TextView mTextViewResult;
    private TextView mTextViewError;

/**
  * for JNI
 */
    private char[] result = new char[512];

    private int[] flags = {0, 0};


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("ClDeviceInfo");
    }

    public native int NativeClDeviceInfo(char[] resul, int[] flags);


/**
  * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewResult = (TextView) findViewById(R.id.TextView_result);
        mTextViewResult.setTextSize(18);
        mTextViewResult.setTextColor(Color.BLUE);

        mTextViewError = (TextView) findViewById(R.id.TextView_error);
        mTextViewError.setTextSize(20);
        mTextViewError.setTextColor(Color.RED);
    }


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();

        // a call to a native method
        int ret = NativeClDeviceInfo(result, flags);

        // Failed
       if(ret != 0) {
            mTextViewError.setText("OpenCL Failed");
            return;
       }

        // Succesfully
        String str_result = new String(result);
        log_d(str_result);

        String text = "OpenCL Succesfully" + LF + LF;
        text += str_result;
        mTextViewResult.setText(text);

        // flags
        String error = "";
       if(flags[0] == 1) {
            error += "CL GL interlop: not suport" + LF;
       }
       if(flags[1] == 1) {
            error += "images: not suport" + LF;
        }

       if(error.length() > 0) {
            mTextViewError.setText(error);
        }

} 


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	   Log.d( TAG,  msg );
} 


} // class MainActivity
