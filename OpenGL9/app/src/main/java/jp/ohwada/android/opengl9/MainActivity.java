/** 
 *  OpenGL ES2.0 Sample
 *  draw Images continuously like slot machine
 *  2019-10-01 K.OHWADA
 * original : http://junkcode.aakaka.com/archives/500
 */
package jp.ohwada.android.opengl9;


import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;


/** 
 *  class MainActivity
 */
public class MainActivity extends Activity {
	
	
	private BoxGLSurfaceView mSurfaceView;
	

/** 
 *  onCreate
 *  Called when the activity is first created
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
	    mSurfaceView = (BoxGLSurfaceView)
			findViewById(R.id.surfaceView);
    }
    

/** 
 *  onResume
 */
    @Override
    public void onResume() {
    	super.onResume();
    	mSurfaceView.onResume();
    }
 

/** 
 *  onPause
 */   
    @Override
    public void onPause() {
    	super.onPause();
    	mSurfaceView.onPause();
    }


} // class MainActivity