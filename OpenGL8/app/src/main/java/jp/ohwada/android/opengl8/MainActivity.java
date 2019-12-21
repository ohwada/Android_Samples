/** 
 *  OpenGL ES2.0 Sample
 *  draw Square and Triangle
 *  2019-10-01 K.OHWADA
 * original : https://github.com/JimSeker/opengl/tree/master/HelloOpenGLES20
 */
package jp.ohwada.android.opengl8;



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