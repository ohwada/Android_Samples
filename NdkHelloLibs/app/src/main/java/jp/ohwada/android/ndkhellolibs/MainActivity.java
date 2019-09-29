/**
 * NDK Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.ndkhellolibs;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;


/*
 * class MainActivity 
 *  
 * Simple Java UI to trigger jni function. It is exactly same as Java code
 * in hello-jni.
 * original : https://github.com/googlesamples/android-ndk/tree/master/hello-libs
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText( stringFromJNI() );
        tv.setTextColor(Color.BLUE);
        tv.setTextSize(20);
        setContentView(tv);
    }

/**
 *  external link to the native code
 *  app/src/main/cpp/hello-libs.cpp
 */
    public native String  stringFromJNI();

/**
  * use Native Library
  */
    static {
        System.loadLibrary("hello-libs");
    }

} // class MainActivity
