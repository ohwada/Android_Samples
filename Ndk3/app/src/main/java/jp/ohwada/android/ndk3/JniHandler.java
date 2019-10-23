/**
  * NDK Sample
  * 2019-08-01 K.OHWADA
  */
package jp.ohwada.android.ndk3;


import android.os.Build;
import android.util.Log;

/*
 * class JniHandler
 * 
 * A helper class to demo that JNI could call into:
 *     private non-static function
 *     public non-static function
 *     static public function
 * The calling code is inside hello-jnicallback.c
 * original : https://github.com/android/ndk-samples/tree/master/hello-jniCallback
 */
public class JniHandler {

    /*
     * Print out status to logcat
     */
    //@Keep
    private void updateStatus(String msg) {
        if (msg.toLowerCase().contains("error")) {
            Log.e("JniHandler", "Native Err: " + msg);
        } else {
            Log.i("JniHandler", "Native Msg: " + msg);
        }
    }

    /*
     * Return OS build version: a static function
     */
    //@Keep
    static public String getBuildVersion() {
        return Build.VERSION.RELEASE;
    }

    /*
     * Return Java memory info
     */
    //@Keep
    public long getRuntimeMemorySize() {
        return Runtime.getRuntime().freeMemory();
    }
}
