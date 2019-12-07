/** 
*  OpenGL Sample
 *  draw rotating Cube
 *  2019-10-01 K.OHWADA
 * original : http://junkcode.aakaka.com/archives/197
 */
package jp.ohwada.android.opengl4;



import android.app.Activity;
import android.os.Bundle;


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {
	
	
	private SampleGLSurfaceView mGLSurfaceView;
	

/** 
 *  onCreate
 *  Called when the activity is first created
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
	this.mGLSurfaceView = (SampleGLSurfaceView)findViewById(R.id.surfaceView);
    }
    

/** 
 *  onResume
 */
    @Override
    public void onResume() {
    	super.onResume();
    	this.mGLSurfaceView.onResume();
    }
 

/** 
 *  onPause
 */   
    @Override
    public void onPause() {
    	super.onPause();
    	this.mGLSurfaceView.onPause();
    }


} // class MainActivity