/**
  * NDK Sample
  * 2019-08-01 K.OHWADA
  */
package jp.ohwada.android.ndk2;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;


/**
  * class MainActivity
 * original : https://github.com/android/ndk-samples/tree/master/hello-jni
  */
public class MainActivity extends Activity {


/**
  * onCreate
  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Retrieve our TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */
        setContentView(R.layout.activity_main);
        TextView tv = (TextView)findViewById(R.id.hello_textview);
        tv.setText( stringFromJNI() );
        tv.setTextColor(Color.BLUE);
        tv.setTextSize(20);
    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();

    /* This is another native method declaration that is *not*
     * implemented by 'hello-jni'. This is simply to show that
     * you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the
     * currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a
     * java.lang.UnsatisfiedLinkError exception !
     */
    public native String  unimplementedStringFromJNI();

    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("hello-jni");
    }

}

