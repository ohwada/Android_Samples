/**
 * OpenCL Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/myhouselove/OpenCL-android
 */
package jp.ohwada.android.opencl1;


import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
  * class MainActivity
 */
public class MainActivity extends Activity {

    private final static String CL_FILE_NAME = "HelloWorld.cl";


    private TextView mTextViewSample;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI(String path);


/**
  * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewSample = (TextView) findViewById(R.id.sample_text);
    }


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();

        // Example of a call to a native method
        String path = AssetFile.getFilePath(this, CL_FILE_NAME);
        String text = stringFromJNI(path);

        mTextViewSample.setText(text);
        mTextViewSample.setTextColor(Color.BLUE);
        mTextViewSample.setTextSize(20);
} 


} // class MainActivity
