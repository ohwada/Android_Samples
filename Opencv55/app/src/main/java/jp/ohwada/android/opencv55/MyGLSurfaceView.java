/**
 * OpenCV Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.opencv55;



import org.opencv.android.CameraGLSurfaceView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;


/** 
 *  class MyGLSurfaceView
 */
public class MyGLSurfaceView extends CameraGLSurfaceView {


    // debug
    static final String TAG = "MyGLSurfaceView";


/** 
 *  Constractor
 */
    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


/** 
 *  surfaceCreated
 */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        log_d("surfaceCreated");
    }


/** 
 *  surfaceDestroyed
 */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        log_d("surfaceDestroyed");
    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	   Log.d( TAG, msg );
} 


} // class MyGLSurfaceView
